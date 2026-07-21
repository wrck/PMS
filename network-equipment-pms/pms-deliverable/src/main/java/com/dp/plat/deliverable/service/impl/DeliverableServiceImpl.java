package com.dp.plat.deliverable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.dto.DeliverableViolation;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.spi.MandatoryDeliverableValidator;
import com.dp.plat.deliverable.dto.MandatoryDeliverableValidationResult;
import com.dp.plat.deliverable.entity.Deliverable;
import com.dp.plat.deliverable.entity.DeliverableReference;
import com.dp.plat.deliverable.entity.DeliverableSignature;
import com.dp.plat.deliverable.entity.DeliverableVersion;
import com.dp.plat.deliverable.enums.DeliverableStatus;
import com.dp.plat.deliverable.exception.IllegalStateTransitionException;
import com.dp.plat.deliverable.mapper.DeliverableMapper;
import com.dp.plat.deliverable.mapper.DeliverableReferenceMapper;
import com.dp.plat.deliverable.mapper.DeliverableSignatureMapper;
import com.dp.plat.deliverable.mapper.DeliverableVersionMapper;
import com.dp.plat.deliverable.service.DeliverableService;
import com.dp.plat.common.dto.StoredBusinessFile;
import com.dp.plat.common.spi.BusinessFileStorage;
import com.dp.plat.common.spi.ProjectPhaseLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

/**
 * 交付件全生命周期服务实现 — 7 态状态机。
 *
 * <p>关联设计文档：§3.4 交付件状态机（行 393-428）、§4.5 事务边界
 * （交付件修订为单事务：新建版本 + 更新交付件）。</p>
 *
 * <p>TD-P8-012：实现 {@link MandatoryDeliverableValidator} SPI，供 {@code pms-project}
 * 的 {@code validateExitGate} 跨模块复用 {@code validateMandatoryDeliverables} 校验逻辑。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliverableServiceImpl extends ServiceImpl<DeliverableMapper, Deliverable>
        implements DeliverableService, MandatoryDeliverableValidator {

    private final DeliverableVersionMapper deliverableVersionMapper;
    private final DeliverableSignatureMapper deliverableSignatureMapper;
    private final DeliverableReferenceMapper deliverableReferenceMapper;
    private final BusinessFileStorage businessFileStorage;
    private final ProjectPhaseLookup projectPhaseLookup;

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
        validatePhaseOwnership(deliverable.getProjectId(), deliverable.getPhaseId());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Deliverable updateBaseInfo(Long id, Deliverable patch) {
        Deliverable current = loadOrThrow(id);
        Long projectId = patch.getProjectId() == null ? current.getProjectId() : patch.getProjectId();
        Long phaseId = patch.getPhaseId();
        validatePhaseOwnership(projectId, phaseId);
        current.setProjectId(projectId);
        current.setPhaseId(phaseId);
        current.setDeliverableName(patch.getDeliverableName());
        current.setDeliverableType(patch.getDeliverableType());
        current.setMandatory(patch.getMandatory());
        current.setApproverRole(patch.getApproverRole());
        updateById(current);
        return current;
    }

    private void validatePhaseOwnership(Long projectId, Long phaseId) {
        if (phaseId == null) return;
        Long phaseProjectId = projectPhaseLookup.findProjectId(phaseId);
        if (phaseProjectId == null) {
            throw new BusinessException("所属阶段不存在");
        }
        if (!phaseProjectId.equals(projectId)) {
            throw new BusinessException("所属阶段不属于当前项目");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliverableVersion uploadInitialVersion(Long deliverableId, MultipartFile file, String changeLog) {
        Deliverable deliverable = loadOrThrow(deliverableId);
        if (DeliverableStatus.of(deliverable.getStatus()) != DeliverableStatus.DRAFT) {
            throw new BusinessException("仅草稿状态交付件可上传初始版本，当前状态：" + deliverable.getStatus());
        }
        Long versionCount = deliverableVersionMapper.selectCount(
                new LambdaQueryWrapper<DeliverableVersion>()
                        .eq(DeliverableVersion::getDeliverableId, deliverableId));
        if (versionCount != null && versionCount > 0) {
            throw new BusinessException("交付件已存在版本，请使用修订功能上传新版本");
        }

        StoredBusinessFile stored = businessFileStorage.upload(file, "DELIVERABLE", deliverableId);
        try {
            DeliverableVersion version = DeliverableVersion.builder()
                    .deliverableId(deliverableId)
                    .versionNo(1)
                    .filePath(stored.getAccessPath())
                    .uploadedBy(stored.getUploadedBy())
                    .uploadedAt(LocalDateTime.now())
                    .changeLog(changeLog == null || changeLog.isBlank() ? "初始版本" : changeLog)
                    .status(DeliverableStatus.DRAFT.code())
                    .build();
            deliverableVersionMapper.insert(version);

            deliverable.setFilePath(stored.getAccessPath());
            deliverable.setCurrentVersion(1);
            deliverable.setStatus(DeliverableStatus.DRAFT.code());
            updateById(deliverable);
            return version;
        } catch (RuntimeException ex) {
            businessFileStorage.delete(stored.getAttachmentId());
            throw ex;
        }
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

    /**
     * TD-P8-012：MandatoryDeliverableValidator SPI 实现。
     *
     * <p>委托给 {@link DeliverableService#validateMandatoryDeliverables(Long)} 并将结果转换为
     * {@link DeliverableViolation} 列表，供 {@code pms-project} 的 {@code validateExitGate}
     * 跨模块复用本模块已实现的集合判断逻辑（避免两套并行校验）。</p>
     */
    @Override
    public List<DeliverableViolation> findMandatoryDeliverableViolations(Long phaseId) {
        // 复用既有 MandatoryDeliverableValidator 风格的校验：phaseId 为空时直接返回空列表
        // （SPI 调用方 advancePhase 已在前面校验过 phase 非空，这里防御性处理）
        if (phaseId == null) {
            return java.util.Collections.emptyList();
        }
        MandatoryDeliverableValidationResult result = ((DeliverableService) this).validateMandatoryDeliverables(phaseId);
        if (result == null || result.getItems() == null) {
            return java.util.Collections.emptyList();
        }
        return result.getItems().stream()
                .map(item -> DeliverableViolation.builder()
                        .deliverableId(item.getDeliverableId())
                        .deliverableName(item.getDeliverableName())
                        .expectedStatus(item.getExpectedStatus())
                        .actualStatus(item.getActualStatus())
                        .approved(item.getApproved())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== 签名管理 ====================

    @Override
    public List<DeliverableSignature> listSignatures(Long deliverableId) {
        if (deliverableId == null) {
            throw new BusinessException("交付件ID不能为空");
        }
        return deliverableSignatureMapper.selectList(
                new LambdaQueryWrapper<DeliverableSignature>()
                        .eq(DeliverableSignature::getDeliverableId, deliverableId)
                        .orderByDesc(DeliverableSignature::getSignedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliverableSignature addSignature(DeliverableSignature signature) {
        if (signature == null) {
            throw new BusinessException("签名记录不能为空");
        }
        if (signature.getDeliverableId() == null) {
            throw new BusinessException("交付件ID不能为空");
        }
        if (signature.getSignerId() == null) {
            throw new BusinessException("签核人ID不能为空");
        }

        Deliverable deliverable = loadOrThrow(signature.getDeliverableId());

        // versionNo 为空时取交付件当前版本
        if (signature.getVersionNo() == null) {
            signature.setVersionNo(deliverable.getCurrentVersion() == null ? 1 : deliverable.getCurrentVersion());
        }
        // signatureType 为空时默认 ELECTRONIC
        if (signature.getSignatureType() == null || signature.getSignatureType().isBlank()) {
            signature.setSignatureType("ELECTRONIC");
        }
        // signedAt 为空时取当前时间
        if (signature.getSignedAt() == null) {
            signature.setSignedAt(LocalDateTime.now());
        }

        deliverableSignatureMapper.insert(signature);
        log.info("新增交付件签名：deliverableId={} versionNo={} signerId={}",
                signature.getDeliverableId(), signature.getVersionNo(), signature.getSignerId());
        return signature;
    }

    // ==================== 引用管理 ====================

    @Override
    public List<DeliverableReference> listReferences(Long deliverableId) {
        if (deliverableId == null) {
            throw new BusinessException("交付件ID不能为空");
        }
        return deliverableReferenceMapper.selectList(
                new LambdaQueryWrapper<DeliverableReference>()
                        .eq(DeliverableReference::getSourceDeliverableId, deliverableId)
                        .orderByDesc(DeliverableReference::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliverableReference addReference(DeliverableReference reference) {
        if (reference == null) {
            throw new BusinessException("引用关系不能为空");
        }
        if (reference.getSourceDeliverableId() == null) {
            throw new BusinessException("被引用的交付件ID不能为空");
        }
        if (reference.getReferenceType() == null || reference.getReferenceType().isBlank()) {
            throw new BusinessException("引用方业务类型不能为空");
        }
        if (reference.getReferencedById() == null) {
            throw new BusinessException("引用方业务ID不能为空");
        }

        // 校验源交付件存在且状态为 PUBLISHED 或 REFERENCED（仅已发布交付件可被引用）
        Deliverable source = loadOrThrow(reference.getSourceDeliverableId());
        DeliverableStatus sourceStatus = DeliverableStatus.of(source.getStatus());
        if (sourceStatus != DeliverableStatus.PUBLISHED && sourceStatus != DeliverableStatus.REFERENCED) {
            throw new BusinessException("仅 PUBLISHED 或 REFERENCED 状态的交付件可被引用，当前状态："
                    + source.getStatus());
        }

        deliverableReferenceMapper.insert(reference);

        // 若源交付件为 PUBLISHED 则流转为 REFERENCED（PUBLISHED → REFERENCED 合法转换）
        if (sourceStatus == DeliverableStatus.PUBLISHED) {
            source.setStatus(DeliverableStatus.REFERENCED.code());
            updateById(source);
            log.info("交付件被引用后状态流转：id={} PUBLISHED → REFERENCED", source.getId());
        }

        log.info("新增交付件引用：sourceDeliverableId={} referenceType={} referencedById={}",
                reference.getSourceDeliverableId(), reference.getReferenceType(), reference.getReferencedById());
        return reference;
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
