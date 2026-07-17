package com.dp.plat.deliverable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.deliverable.dto.MandatoryDeliverableValidationResult;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.entity.DeliverableVersion;
import com.dp.plat.deliverable.enums.DeliverableStatus;
import com.dp.plat.deliverable.exception.IllegalStateTransitionException;
import com.dp.plat.deliverable.mapper.DeliverableMapper;
import com.dp.plat.deliverable.mapper.DeliverableVersionMapper;
import com.dp.plat.deliverable.service.DeliverableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交付件全生命周期服务实现 — 7 态状态机。
 *
 * <p>关联设计文档：§3.4 交付件状态机（行 393-428）、§4.5 事务边界
 * （交付件修订为单事务：新建版本 + 更新交付件）。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliverableServiceImpl extends ServiceImpl<DeliverableMapper, Deliverable>
        implements DeliverableService {

    private final DeliverableVersionMapper deliverableVersionMapper;

    // ==================== CRUD ====================

    @Override
    public List<Deliverable> list(Long projectId, Long phaseId, String status) {
        LambdaQueryWrapper<Deliverable> wrapper = new LambdaQueryWrapper<Deliverable>()
                .eq(projectId != null, Deliverable::getProjectId, projectId)
                .eq(phaseId != null, Deliverable::getPhaseId, phaseId)
                .eq(status != null && !status.isBlank(), Deliverable::getStatus, status)
                .orderByDesc(Deliverable::getId);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Deliverable create(Deliverable deliverable) {
        if (deliverable.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (deliverable.getDeliverableName() == null || deliverable.getDeliverableName().isBlank()) {
            throw new BusinessException("交付件名称不能为空");
        }
        // 默认值：DRAFT / currentVersion=1 / mandatory=false
        if (deliverable.getStatus() == null || deliverable.getStatus().isBlank()) {
            deliverable.setStatus(DeliverableStatus.DRAFT.code());
        }
        if (deliverable.getCurrentVersion() == null) {
            deliverable.setCurrentVersion(1);
        }
        if (deliverable.getMandatory() == null) {
            deliverable.setMandatory(Boolean.FALSE);
        }
        save(deliverable);

        // 若提供文件路径，创建初始 v1 版本记录（uploadedBy 由 createBy 审计字段记录，故可空）
        if (deliverable.getFilePath() != null && !deliverable.getFilePath().isBlank()) {
            DeliverableVersion v1 = DeliverableVersion.builder()
                    .deliverableId(deliverable.getId())
                    .versionNo(1)
                    .filePath(deliverable.getFilePath())
                    .uploadedAt(LocalDateTime.now())
                    .changeLog("初始版本")
                    .status(DeliverableStatus.DRAFT.code())
                    .build();
            deliverableVersionMapper.insert(v1);
        }
        return deliverable;
    }

    // ==================== 7 态状态机 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Deliverable transition(Long id, String toStatus) {
        Deliverable deliverable = loadOrThrow(id);
        DeliverableStatus current = DeliverableStatus.of(deliverable.getStatus());
        DeliverableStatus target = DeliverableStatus.of(toStatus);

        if (current == null) {
            throw new IllegalStateTransitionException(id, deliverable.getStatus(), toStatus,
                    "当前状态无法识别");
        }
        if (target == null) {
            throw new IllegalStateTransitionException(id, deliverable.getStatus(), toStatus,
                    "目标状态无法识别");
        }

        // PUBLISHED → DRAFT 为「修订新建版本」路径，须由 revise 接口处理（需提供新文件与变更说明）
        if (current == DeliverableStatus.PUBLISHED && target == DeliverableStatus.DRAFT) {
            throw new IllegalStateTransitionException(id, current.code(), target.code(),
                    "PUBLISHED 修订请使用 revise 接口（需提供新文件路径与变更说明，将新建 versionNo+1 版本）");
        }

        if (!current.canTransitionTo(target)) {
            throw new IllegalStateTransitionException(id, current.code(), target.code());
        }

        // 应用状态变更 + 相关时间戳副作用
        deliverable.setStatus(target.code());
        if (target == DeliverableStatus.PUBLISHED) {
            deliverable.setPublishedAt(LocalDateTime.now());
        }
        if (target == DeliverableStatus.ARCHIVED) {
            deliverable.setArchivedAt(LocalDateTime.now());
        }
        updateById(deliverable);
        log.info("交付件状态流转：id={} {} → {}", id, current.code(), target.code());
        return deliverable;
    }

    @Override
    public Deliverable submit(Long id) {
        return transition(id, DeliverableStatus.SUBMITTED.code());
    }

    @Override
    public Deliverable review(Long id, boolean passed) {
        return transition(id, passed
                ? DeliverableStatus.REVIEWED.code()
                : DeliverableStatus.DRAFT.code());
    }

    @Override
    public Deliverable sign(Long id) {
        return transition(id, DeliverableStatus.SIGNED.code());
    }

    @Override
    public Deliverable publish(Long id) {
        return transition(id, DeliverableStatus.PUBLISHED.code());
    }

    @Override
    public Deliverable archive(Long id) {
        return transition(id, DeliverableStatus.ARCHIVED.code());
    }

    // ==================== 版本管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliverableVersion revise(Long deliverableId, String filePath, String changeLog, Long uploadedBy) {
        Deliverable deliverable = loadOrThrow(deliverableId);
        DeliverableStatus current = DeliverableStatus.of(deliverable.getStatus());

        // 1. 校验当前状态允许修订（仅 PUBLISHED 或 REFERENCED 可修订）
        if (current != DeliverableStatus.PUBLISHED && current != DeliverableStatus.REFERENCED) {
            throw new BusinessException(
                    "仅 PUBLISHED 或 REFERENCED 状态的交付件可修订，当前状态：" + deliverable.getStatus());
        }
        if (filePath == null || filePath.isBlank()) {
            throw new BusinessException("修订需提供新文件路径");
        }

        // 2. 版本号 +1
        int newVersionNo = (deliverable.getCurrentVersion() == null ? 0 : deliverable.getCurrentVersion()) + 1;

        // 3. 新建版本记录（versionNo = newVersionNo，旧版本记录保留不变）
        DeliverableVersion newVersion = DeliverableVersion.builder()
                .deliverableId(deliverableId)
                .versionNo(newVersionNo)
                .filePath(filePath)
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .changeLog(changeLog)
                .status(DeliverableStatus.DRAFT.code())
                .build();
        deliverableVersionMapper.insert(newVersion);

        // 4. 更新 Deliverable.currentVersion + status=DRAFT + filePath=新文件（旧版本历史不受影响）
        deliverable.setCurrentVersion(newVersionNo);
        deliverable.setStatus(DeliverableStatus.DRAFT.code());
        deliverable.setFilePath(filePath);
        updateById(deliverable);

        log.info("交付件修订：id={} 新版本 v{}，旧版本保留不变", deliverableId, newVersionNo);
        return newVersion;
    }

    @Override
    public List<DeliverableVersion> listVersions(Long deliverableId) {
        if (deliverableId == null) {
            throw new BusinessException("交付件ID不能为空");
        }
        return deliverableVersionMapper.selectList(
                new LambdaQueryWrapper<DeliverableVersion>()
                        .eq(DeliverableVersion::getDeliverableId, deliverableId)
                        .orderByDesc(DeliverableVersion::getVersionNo));
    }

    @Override
    public DeliverableVersion getVersion(Long deliverableId, Integer versionNo) {
        if (deliverableId == null || versionNo == null) {
            throw new BusinessException("交付件ID与版本号均不能为空");
        }
        return deliverableVersionMapper.selectOne(
                new LambdaQueryWrapper<DeliverableVersion>()
                        .eq(DeliverableVersion::getDeliverableId, deliverableId)
                        .eq(DeliverableVersion::getVersionNo, versionNo));
    }

    // ==================== 阶段退出校验 ====================

    @Override
    public MandatoryDeliverableValidationResult validateMandatoryDeliverables(Long phaseId) {
        if (phaseId == null) {
            throw new BusinessException("阶段ID不能为空");
        }

        // 1. 查询阶段下所有 mandatory=true 的交付件
        List<Deliverable> mandatoryDeliverables = list(new LambdaQueryWrapper<Deliverable>()
                .eq(Deliverable::getPhaseId, phaseId)
                .eq(Deliverable::getMandatory, Boolean.TRUE));

        // 2. 过滤 status 未达到「已批准」（PUBLISHED/REFERENCED/ARCHIVED）的条目
        List<MandatoryDeliverableValidationResult.Item> unmet = mandatoryDeliverables.stream()
                .filter(d -> {
                    DeliverableStatus status = DeliverableStatus.of(d.getStatus());
                    return status == null || !status.isApproved();
                })
                .map(d -> {
                    DeliverableStatus status = DeliverableStatus.of(d.getStatus());
                    boolean approved = status != null && status.isApproved();
                    return MandatoryDeliverableValidationResult.Item.builder()
                            .deliverableId(d.getId())
                            .deliverableName(d.getDeliverableName())
                            .mandatory(Boolean.TRUE)
                            .expectedStatus(DeliverableStatus.PUBLISHED.code())
                            .actualStatus(d.getStatus())
                            .approved(approved)
                            .build();
                })
                .collect(Collectors.toList());

        // 3. allApproved = 未满足项为空
        boolean allApproved = unmet.isEmpty();
        log.info("阶段必需交付件校验：phaseId={} 必需项={} 未满足={} allApproved={}",
                phaseId, mandatoryDeliverables.size(), unmet.size(), allApproved);

        return MandatoryDeliverableValidationResult.builder()
                .allApproved(allApproved)
                .items(unmet)
                .build();
    }

    // ==================== helpers ====================

    private Deliverable loadOrThrow(Long id) {
        if (id == null) {
            throw new BusinessException("交付件ID不能为空");
        }
        Deliverable deliverable = getById(id);
        if (deliverable == null) {
            throw new BusinessException("交付件不存在：id=" + id);
        }
        return deliverable;
    }
}
