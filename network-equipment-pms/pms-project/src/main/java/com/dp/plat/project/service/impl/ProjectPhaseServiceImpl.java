package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.dto.PhaseExitGate;
import com.dp.plat.project.dao.ProjectPhaseMapper;
import com.dp.plat.project.dto.PhaseExitGateViolation;
import com.dp.plat.project.entity.Deliverable;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.exception.PhaseExitGateFailedException;
import com.dp.plat.project.mapper.DeliverableMapper;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IProjectPhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectPhaseServiceImpl implements IProjectPhaseService {

    private final ProjectPhaseMapper phaseMapper;
    private final DeliverableMapper deliverableMapper;
    private final MilestoneMapper milestoneMapper;
    private final ProjectMapper projectMapper;

    /** 阶段状态常量（关联设计文档 §3.2） */
    private static final String PHASE_NOT_STARTED = "NOT_STARTED";
    private static final String PHASE_IN_PROGRESS = "IN_PROGRESS";
    private static final String PHASE_COMPLETED = "COMPLETED";

    /** 项目生命周期状态常量（关联设计文档 §3.1） */
    private static final String PROJECT_CLOSING = "CLOSING";

    @Override
    public List<ProjectPhase> listByProjectId(Long projectId) {
        return phaseMapper.selectList(new LambdaQueryWrapper<ProjectPhase>()
            .eq(ProjectPhase::getProjectId, projectId)
            .orderByAsc(ProjectPhase::getSortOrder));
    }

    @Override
    public ProjectPhase getById(Long id) {
        return phaseMapper.selectById(id);
    }

    @Override
    @Transactional
    public ProjectPhase create(ProjectPhase phase) {
        if (phase.getStatus() == null) {
            phase.setStatus(PHASE_NOT_STARTED);
        }
        phaseMapper.insert(phase);
        return phase;
    }

    @Override
    @Transactional
    public ProjectPhase update(ProjectPhase phase) {
        phaseMapper.updateById(phase);
        return phase;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        phaseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public List<ProjectPhase> batchCreate(List<ProjectPhase> phases) {
        for (ProjectPhase phase : phases) {
            if (phase.getStatus() == null) {
                phase.setStatus(PHASE_NOT_STARTED);
            }
            phaseMapper.insert(phase);
        }
        return phases;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ProjectPhase> advancePhase(Long phaseId) {
        ProjectPhase phase = phaseMapper.selectById(phaseId);
        if (phase == null) {
            throw new BusinessException("阶段不存在");
        }
        if (!PHASE_IN_PROGRESS.equals(phase.getStatus())) {
            throw new BusinessException("当前阶段状态不允许推进，必须为 IN_PROGRESS，实际为 " + phase.getStatus());
        }

        // 1. 校验 4 类退出条件
        List<PhaseExitGateViolation> violations = validateExitGate(phase);
        if (!violations.isEmpty()) {
            // 任一未满足 → 阻止推进（Story 2 验收 1）
            throw new PhaseExitGateFailedException("当前阶段退出条件未满足", violations);
        }

        // 2. 当前阶段 → COMPLETED
        phase.setStatus(PHASE_COMPLETED);
        phase.setActualEndDate(LocalDate.now());
        phaseMapper.updateById(phase);

        // 3. 激活下一阶段；若无下一阶段 → 项目进入 CLOSING
        ProjectPhase nextPhase = findNextPhase(phase);
        if (nextPhase != null) {
            nextPhase.setStatus(PHASE_IN_PROGRESS);
            nextPhase.setActualStartDate(LocalDate.now());
            phaseMapper.updateById(nextPhase);
            updateProjectCurrentPhase(phase.getProjectId(), nextPhase.getId());
            return Result.ok(nextPhase);
        }
        // 最后阶段完成 → 项目状态置 CLOSING（等待关闭审批）
        updateProjectStatusToClosing(phase.getProjectId());
        return Result.ok(phase);
    }

    /**
     * 校验阶段退出条件（PhaseExitGate，4 类）。
     *
     * <p>当前已实现 DELIVERABLE、MILESTONE 两类（依赖 pms-project 内的 DeliverableMapper、
     * MilestoneMapper）。TASK、APPROVAL 两类依赖 pms-implementation 的 ImplTask 与 Story 4 的
     * ApprovalRecord，pms-project 模块当前未依赖这些实体；为避免在无任务/审批数据时锁死阶段推进，
     * 暂不阻断（仅记录 TODO），待 Story 3/4 接入对应服务后补充校验。
     */
    private List<PhaseExitGateViolation> validateExitGate(ProjectPhase phase) {
        List<PhaseExitGateViolation> violations = new ArrayList<>();
        PhaseExitGate gate = phase.getExitCriteria();
        if (gate == null) {
            return violations; // 未配置退出条件，直接通过
        }

        // 1. 必需交付件：状态须等于 requiredStatus
        if (gate.getRequiredDeliverables() != null) {
            for (PhaseExitGate.RequiredDeliverable req : gate.getRequiredDeliverables()) {
                Deliverable d = deliverableMapper.selectById(req.getDeliverableId());
                if (d == null) {
                    violations.add(PhaseExitGateViolation.builder()
                            .gateType("DELIVERABLE")
                            .message("必需交付件不存在")
                            .businessId(req.getDeliverableId())
                            .businessName(req.getDeliverableName())
                            .expectedStatus(req.getRequiredStatus())
                            .actualStatus(null)
                            .build());
                } else if (req.getRequiredStatus() != null
                        && !req.getRequiredStatus().equals(d.getStatus())) {
                    violations.add(PhaseExitGateViolation.builder()
                            .gateType("DELIVERABLE")
                            .message("必需交付件未达到要求状态")
                            .businessId(d.getId())
                            .businessName(d.getDeliverableName())
                            .expectedStatus(req.getRequiredStatus())
                            .actualStatus(d.getStatus())
                            .build());
                }
            }
        }

        // 2. 必需里程碑：mustReached=true 时状态须为 COMPLETED
        if (gate.getRequiredMilestones() != null) {
            for (PhaseExitGate.RequiredMilestone req : gate.getRequiredMilestones()) {
                if (!Boolean.TRUE.equals(req.getMustReached())) {
                    continue;
                }
                Milestone m = milestoneMapper.selectById(req.getMilestoneId());
                if (m == null) {
                    violations.add(PhaseExitGateViolation.builder()
                            .gateType("MILESTONE")
                            .message("必需里程碑不存在")
                            .businessId(req.getMilestoneId())
                            .expectedStatus(PHASE_COMPLETED)
                            .actualStatus(null)
                            .build());
                } else if (!PHASE_COMPLETED.equals(m.getStatus())) {
                    violations.add(PhaseExitGateViolation.builder()
                            .gateType("MILESTONE")
                            .message("必需里程碑未达成")
                            .businessId(m.getId())
                            .businessName(m.getMilestoneName())
                            .expectedStatus(PHASE_COMPLETED)
                            .actualStatus(m.getStatus())
                            .build());
                }
            }
        }

        // 3. 必需任务（requiredTasks）：ImplTask 在 pms-implementation 模块，pms-project 未依赖。
        //    Story 3 接入任务服务后，在此按 phaseId + allCompleted 校验未完成任务并生成 violations。
        // 4. 必需审批（requiredApprovals）：ApprovalRecord 待 Story 4 接入，同上策略。
        return violations;
    }

    /** 查询下一阶段（同项目内 sortOrder 大于当前阶段的最小一个）。 */
    private ProjectPhase findNextPhase(ProjectPhase current) {
        return phaseMapper.selectOne(new LambdaQueryWrapper<ProjectPhase>()
                .eq(ProjectPhase::getProjectId, current.getProjectId())
                .gt(ProjectPhase::getSortOrder, current.getSortOrder() == null ? -1 : current.getSortOrder())
                .orderByAsc(ProjectPhase::getSortOrder)
                .last("LIMIT 1"));
    }

    /** 仅更新项目 currentPhaseId（局部更新，version 不参与乐观锁校验）。 */
    private void updateProjectCurrentPhase(Long projectId, Long phaseId) {
        if (projectId == null) {
            return;
        }
        Project patch = new Project();
        patch.setId(projectId);
        patch.setCurrentPhaseId(phaseId);
        projectMapper.updateById(patch);
    }

    /** 最后阶段完成时，将项目状态置 CLOSING。 */
    private void updateProjectStatusToClosing(Long projectId) {
        if (projectId == null) {
            return;
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            log.warn("updateProjectStatusToClosing: 项目 {} 不存在", projectId);
            return;
        }
        project.setStatus(PROJECT_CLOSING);
        projectMapper.updateById(project);
    }
}
