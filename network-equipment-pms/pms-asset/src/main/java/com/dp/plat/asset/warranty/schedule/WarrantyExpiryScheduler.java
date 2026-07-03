package com.dp.plat.asset.warranty.schedule;

import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Daily scheduler that scans warranties approaching their end date and logs
 * warnings grouped by urgency (90 / 60 / 30 days).
 *
 * <p>No notification service is wired yet; warnings are emitted via {@code log.warn}.
 * Scheduling is enabled globally on the application main class.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarrantyExpiryScheduler {

    private static final int URGENT_DAYS = 30;
    private static final int NEAR_DAYS = 60;
    private static final int SOON_DAYS = 90;

    private final IWarrantyService warrantyService;

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
        }
        for (Warranty w : near) {
            log.warn("质保临近到期：资产 id={}, 质保 id={}, 项目 id={}, 到期日={}",
                    w.getAssetId(), w.getId(), w.getProjectId(), w.getEndDate());
        }
        for (Warranty w : soon) {
            log.warn("质保预告到期：资产 id={}, 质保 id={}, 项目 id={}, 到期日={}",
                    w.getAssetId(), w.getId(), w.getProjectId(), w.getEndDate());
        }
    }
}
