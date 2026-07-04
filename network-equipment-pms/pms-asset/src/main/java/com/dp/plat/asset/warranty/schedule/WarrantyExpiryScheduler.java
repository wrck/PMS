package com.dp.plat.asset.warranty.schedule;

import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Daily scheduler that scans warranties approaching their end date and emits
 * warnings grouped by urgency (90 / 60 / 30 days).
 *
 * <p>For each warranty an in-app + WebSocket notification ({@code WARRANTY}
 * category) is sent to the asset's responsible party — the asset's project
 * manager. Notification failures are isolated in try-catch so they never
 * block the scheduler scan.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarrantyExpiryScheduler {

    private static final int URGENT_DAYS = 30;
    private static final int NEAR_DAYS = 60;
    private static final int SOON_DAYS = 90;

    /** Notification metadata. */
    private static final String CATEGORY_WARRANTY = "WARRANTY";
    private static final String BIZ_TYPE_WARRANTY_EXPIRE_URGENT = "WARRANTY_EXPIRE_30";
    private static final String BIZ_TYPE_WARRANTY_EXPIRE_NEAR = "WARRANTY_EXPIRE_60";
    private static final String BIZ_TYPE_WARRANTY_EXPIRE_SOON = "WARRANTY_EXPIRE_90";
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS");

    private final IWarrantyService warrantyService;
    private final AssetMapper assetMapper;
    private final ProjectMapper projectMapper;
    private final INotificationService notificationService;

    /**
     * Run every day at 03:00 to flag warranties expiring within 90 days.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void scanExpiringWarranties() {
        LocalDate today = LocalDate.now();
        List<Warranty> expiring = warrantyService.listExpiringSoon(SOON_DAYS);
        if (expiring == null || expiring.isEmpty()) {
            return;
        }

        List<Warranty> urgent = new ArrayList<>();
        List<Warranty> near = new ArrayList<>();
        List<Warranty> soon = new ArrayList<>();
        for (Warranty warranty : expiring) {
            if (warranty.getEndDate() == null) {
                continue;
            }
            long daysLeft = ChronoUnit.DAYS.between(today, warranty.getEndDate());
            if (daysLeft <= URGENT_DAYS) {
                urgent.add(warranty);
            } else if (daysLeft <= NEAR_DAYS) {
                near.add(warranty);
            } else {
                soon.add(warranty);
            }
        }

        log.warn("质保到期扫描：紧急(≤{}天) {} 条，临近(≤{}天) {} 条，预告(≤{}天) {} 条",
                URGENT_DAYS, urgent.size(), NEAR_DAYS, near.size(), SOON_DAYS, soon.size());
        for (Warranty w : urgent) {
            log.warn("质保紧急到期：资产 id={}, 质保 id={}, 项目 id={}, 到期日={}",
                    w.getAssetId(), w.getId(), w.getProjectId(), w.getEndDate());
            sendExpiryNotification(w, "紧急", URGENT_DAYS, daysBetween(today, w), BIZ_TYPE_WARRANTY_EXPIRE_URGENT);
        }
        for (Warranty w : near) {
            log.warn("质保临近到期：资产 id={}, 质保 id={}, 项目 id={}, 到期日={}",
                    w.getAssetId(), w.getId(), w.getProjectId(), w.getEndDate());
            sendExpiryNotification(w, "临近", NEAR_DAYS, daysBetween(today, w), BIZ_TYPE_WARRANTY_EXPIRE_NEAR);
        }
        for (Warranty w : soon) {
            log.warn("质保预告到期：资产 id={}, 质保 id={}, 项目 id={}, 到期日={}",
                    w.getAssetId(), w.getId(), w.getProjectId(), w.getEndDate());
            sendExpiryNotification(w, "预告", SOON_DAYS, daysBetween(today, w), BIZ_TYPE_WARRANTY_EXPIRE_SOON);
        }
    }

    private long daysBetween(LocalDate today, Warranty w) {
        return ChronoUnit.DAYS.between(today, w.getEndDate());
    }

    /**
     * Resolve the responsible user id for the warranty. Falls back through:
     * warranty.projectId → project.projectManagerId; otherwise asset.projectId
     * → project.projectManagerId. Returns {@code null} when nothing resolves.
     */
    private Long resolveResponsibleUserId(Warranty warranty) {
        Long projectId = warranty.getProjectId();
        if (projectId == null && warranty.getAssetId() != null) {
            Asset asset = assetMapper.selectById(warranty.getAssetId());
            if (asset != null) {
                projectId = asset.getProjectId();
            }
        }
        if (projectId == null) {
            return null;
        }
        Project project = projectMapper.selectById(projectId);
        return project == null ? null : project.getProjectManagerId();
    }

    private String resolveAssetNo(Long assetId) {
        if (assetId == null) {
            return "未知资产";
        }
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            return String.valueOf(assetId);
        }
        return asset.getSerialNo() == null ? String.valueOf(assetId) : asset.getSerialNo();
    }

    /**
     * Send a warranty-expiry notification. Failures are swallowed: notifications
     * are best-effort and must not affect the warranty scan.
     */
    private void sendExpiryNotification(Warranty warranty, String level,
                                        int levelDays, long daysLeft, String bizType) {
        Long userId = resolveResponsibleUserId(warranty);
        if (userId == null) {
            log.warn("质保 id={} 未找到责任人，跳过通知发送", warranty.getId());
            return;
        }
        String assetNo = resolveAssetNo(warranty.getAssetId());
        String title = String.format("质保期到期预警（%s）", level);
        String content = String.format("资产 %s 质保将于 %s 到期，剩余 %d 天",
                assetNo, warranty.getEndDate(), daysLeft);
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .category(CATEGORY_WARRANTY)
                .bizType(bizType)
                .bizId(warranty.getId())
                .build();
        try {
            notificationService.multiChannelSend(notification, CHANNELS);
        } catch (Exception e) {
            log.error("质保到期通知发送失败 warrantyId={} userId={}",
                    warranty.getId(), userId, e);
        }
    }
}
