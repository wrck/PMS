package com.dp.plat.asset.rma.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.enums.AssetStatus;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.rma.dto.RmaKpiDto;
import com.dp.plat.asset.rma.entity.Rma;
import com.dp.plat.asset.rma.mapper.RmaMapper;
import com.dp.plat.asset.rma.service.IRmaService;
import com.dp.plat.asset.service.AssetStateTransitionValidator;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link IRmaService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RmaServiceImpl extends ServiceImpl<RmaMapper, Rma> implements IRmaService {

    /** Ticket status constants. */
    private static final String TICKET_REGISTERED = "REGISTERED";
    private static final String TICKET_WARRANTY_CHECKED = "WARRANTY_CHECKED";
    private static final String TICKET_RMA_ISSUED = "RMA_ISSUED";
    private static final String TICKET_RETURNING = "RETURNING";
    private static final String TICKET_INSPECTED = "INSPECTED";
    private static final String TICKET_CLOSED = "CLOSED";

    /** Warranty status constants. */
    private static final String WARRANTY_IN = "IN_WARRANTY";
    private static final String WARRANTY_OUT = "OUT_OF_WARRANTY";

    /** Notification metadata. */
    private static final String CATEGORY_RMA = "RMA";
    private static final String BIZ_TYPE_RMA_STATUS_CHANGE = "RMA_STATUS_CHANGE";
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS");

    private final AssetMapper assetMapper;
    private final AssetStateTransitionValidator stateValidator;
    /** Optional dependency: defaults to IN_WARRANTY when the warranty module is absent. */
    private final ObjectProvider<IWarrantyService> warrantyServiceProvider;
    private final INotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(Rma rma) {
        if (rma.getAssetId() == null) {
            throw new BusinessException("RMA 关联设备不能为空");
        }
        rma.setRmaNo(generateRmaNo());
        rma.setTicketStatus(TICKET_REGISTERED);
        LocalDateTime now = LocalDateTime.now();
        rma.setRegisteredAt(now);
        rma.setRegisterUserId(SecurityUtils.getCurrentUserId());
        rma.setRegisterUserName(SecurityUtils.getCurrentUsername());
        // Snapshot the asset SN / project when not supplied by the caller.
        if (rma.getSn() == null || rma.getProjectId() == null) {
            Asset asset = assetMapper.selectById(rma.getAssetId());
            if (asset != null) {
                if (rma.getSn() == null) {
                    rma.setSn(asset.getSerialNo());
                }
                if (rma.getProjectId() == null) {
                    rma.setProjectId(asset.getProjectId());
                }
            }
        }
        return this.save(rma);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkWarranty(Long id) {
        Rma rma = loadRma(id);
        if (!TICKET_REGISTERED.equals(rma.getTicketStatus())) {
            throw new BusinessException("RMA 单当前状态不可进行质保核验");
        }
        boolean inWarranty = checkInWarranty(rma.getAssetId());
        rma.setWarrantyStatus(inWarranty ? WARRANTY_IN : WARRANTY_OUT);
        rma.setWarrantyCheckedAt(LocalDateTime.now());
        rma.setTicketStatus(TICKET_WARRANTY_CHECKED);
        boolean updated = this.updateById(rma);
        sendStatusChangeNotification(rma, TICKET_WARRANTY_CHECKED);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean issueRma(Long id) {
        Rma rma = loadRma(id);
        String status = rma.getTicketStatus();
        if (!TICKET_REGISTERED.equals(status) && !TICKET_WARRANTY_CHECKED.equals(status)) {
            throw new BusinessException("RMA 单当前状态不可签发");
        }
        rma.setTicketStatus(TICKET_RMA_ISSUED);
        rma.setRmaIssuedAt(LocalDateTime.now());
        boolean updated = this.updateById(rma);
        sendStatusChangeNotification(rma, TICKET_RMA_ISSUED);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markReturning(Long id) {
        Rma rma = loadRma(id);
        if (!TICKET_RMA_ISSUED.equals(rma.getTicketStatus())) {
            throw new BusinessException("RMA 单当前状态不可标记为返修运输中");
        }
        rma.setTicketStatus(TICKET_RETURNING);
        rma.setReturningAt(LocalDateTime.now());
        boolean updated = this.updateById(rma);
        sendStatusChangeNotification(rma, TICKET_RETURNING);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inspect(Long id, String notes) {
        Rma rma = loadRma(id);
        if (!TICKET_RETURNING.equals(rma.getTicketStatus())) {
            throw new BusinessException("RMA 单当前状态不可检验");
        }
        LocalDateTime now = LocalDateTime.now();
        rma.setTicketStatus(TICKET_INSPECTED);
        rma.setInspectedAt(now);
        rma.setInspectorNotes(notes);
        boolean updated = this.updateById(rma);

        // Update the related asset status according to the repair outcome.
        Asset asset = assetMapper.selectById(rma.getAssetId());
        if (asset != null) {
            AssetStatus current = parseStatus(asset.getStatus());
            AssetStatus target = determineInspectTarget(current, rma.getResolution());
            if (current != target) {
                stateValidator.validate(current, target);
                asset.setStatus(target.name());
                assetMapper.updateById(asset);
            }
        }
        sendStatusChangeNotification(rma, TICKET_INSPECTED);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean close(Long id) {
        Rma rma = loadRma(id);
        if (!TICKET_INSPECTED.equals(rma.getTicketStatus())) {
            throw new BusinessException("RMA 单当前状态不可关闭");
        }
        rma.setTicketStatus(TICKET_CLOSED);
        rma.setClosedAt(LocalDateTime.now());
        boolean updated = this.updateById(rma);
        sendStatusChangeNotification(rma, TICKET_CLOSED);
        return updated;
    }

    @Override
    public List<Rma> listByProject(Long projectId) {
        return this.list(new LambdaQueryWrapper<Rma>()
                .eq(Rma::getProjectId, projectId)
                .orderByDesc(Rma::getId));
    }

    @Override
    public List<Rma> listByAsset(Long assetId) {
        return this.list(new LambdaQueryWrapper<Rma>()
                .eq(Rma::getAssetId, assetId)
                .orderByDesc(Rma::getId));
    }

    @Override
    public RmaKpiDto kpi(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        long totalCount = this.count(new LambdaQueryWrapper<Rma>()
                .ge(Rma::getRegisteredAt, start)
                .lt(Rma::getRegisteredAt, end));

        List<Rma> closed = this.list(new LambdaQueryWrapper<Rma>()
                .eq(Rma::getTicketStatus, TICKET_CLOSED)
                .ge(Rma::getClosedAt, start)
                .lt(Rma::getClosedAt, end));
        long closedCount = closed.size();

        BigDecimal mttrHours = BigDecimal.ZERO;
        if (!closed.isEmpty()) {
            double totalHours = closed.stream()
                    .filter(r -> r.getRegisteredAt() != null && r.getClosedAt() != null)
                    .mapToDouble(r -> Duration.between(r.getRegisteredAt(), r.getClosedAt()).toMinutes() / 60.0)
                    .sum();
            mttrHours = BigDecimal.valueOf(totalHours / closed.size()).setScale(2, RoundingMode.HALF_UP);
        }

        // First-pass yield: of tickets that reached inspection, the share closed in one pass.
        long inspectedCount = this.count(new LambdaQueryWrapper<Rma>()
                .in(Rma::getTicketStatus, TICKET_INSPECTED, TICKET_CLOSED)
                .ge(Rma::getRegisteredAt, start)
                .lt(Rma::getRegisteredAt, end));
        BigDecimal firstPassRate = BigDecimal.ZERO;
        if (inspectedCount > 0) {
            firstPassRate = BigDecimal.valueOf(closedCount * 100.0 / inspectedCount)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return RmaKpiDto.builder()
                .totalCount(totalCount)
                .closedCount(closedCount)
                .mttrHours(mttrHours)
                .firstPassRate(firstPassRate)
                .build();
    }

    @Override
    public String generateRmaNo() {
        int year = LocalDate.now().getYear();
        String prefix = "RMA-" + year + "-";
        Long existing = this.count(new LambdaQueryWrapper<Rma>().likeRight(Rma::getRmaNo, prefix));
        long next = (existing == null ? 0 : existing) + 1;
        return prefix + String.format("%04d", next);
    }

    private Rma loadRma(Long id) {
        if (id == null) {
            throw new BusinessException("RMA 单 id 不能为空");
        }
        Rma rma = this.getById(id);
        if (rma == null) {
            throw new BusinessException("RMA 单不存在");
        }
        return rma;
    }

    /**
     * Notify the RMA registrant that the ticket status has changed. The RMA
     * entity's {@code registerUserId} is the creator/owner of the ticket.
     * Failures are swallowed: notifications are best-effort and must not roll
     * back the status transition.
     */
    private void sendStatusChangeNotification(Rma rma, String newStatus) {
        Long recipientId = rma.getRegisterUserId();
        if (recipientId == null) {
            log.warn("RMA id={} 无登记人 userId，跳过状态变更通知", rma.getId());
            return;
        }
        String title = "RMA 状态变更";
        String content = String.format("RMA %s 状态已变更为 %s",
                rma.getRmaNo() == null ? rma.getId() : rma.getRmaNo(),
                newStatus);
        Notification notification = Notification.builder()
                .userId(recipientId)
                .title(title)
                .content(content)
                .category(CATEGORY_RMA)
                .bizType(BIZ_TYPE_RMA_STATUS_CHANGE)
                .bizId(rma.getId())
                .build();
        try {
            notificationService.multiChannelSend(notification, CHANNELS);
        } catch (Exception e) {
            log.error("RMA 状态变更通知发送失败 rmaId={} userId={}",
                    rma.getId(), recipientId, e);
        }
    }

    /**
     * Determine the asset's target status after RMA inspection.
     *
     * <ul>
     *   <li>scrapped (resolution indicates scrap) → DECOMMISSIONED</li>
     *   <li>repaired, asset was INSTALLED → COMMISSIONED</li>
     *   <li>repaired, otherwise → IN_PRODUCTION</li>
     * </ul>
     */
    private AssetStatus determineInspectTarget(AssetStatus current, String resolution) {
        if (isScrapped(resolution)) {
            return AssetStatus.DECOMMISSIONED;
        }
        if (current == AssetStatus.INSTALLED) {
            return AssetStatus.COMMISSIONED;
        }
        return AssetStatus.IN_PRODUCTION;
    }

    private boolean isScrapped(String resolution) {
        if (resolution == null) {
            return false;
        }
        String upper = resolution.toUpperCase();
        return upper.contains("SCRAP") || upper.contains("报废");
    }

    /**
     * Check warranty using the warranty service when available; default to
     * IN_WARRANTY otherwise.
     */
    private boolean checkInWarranty(Long assetId) {
        IWarrantyService warrantyService = warrantyServiceProvider.getIfAvailable();
        if (warrantyService == null) {
            return true;
        }
        return warrantyService.isInWarranty(assetId, LocalDate.now());
    }

    private AssetStatus parseStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return AssetStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("未知的资产状态: " + status);
        }
    }
}
