package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.dto.ApprovalViolation;
import com.dp.plat.common.dto.DeliverableViolation;
import com.dp.plat.common.dto.PhaseExitGate;
import com.dp.plat.common.dto.TaskCompletionViolation;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.spi.ApprovalStatusChecker;
import com.dp.plat.common.spi.MandatoryDeliverableValidator;
import com.dp.plat.common.spi.TaskCompletionChecker;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectPhaseServiceImpl implements IProjectPhaseService {

    private final ProjectPhaseMapper phaseMapper;
    private final DeliverableMapper deliverableMapper;
    private final MilestoneMapper milestoneMapper;
    private final ProjectMapper projectMapper;

    /**
     * 必需交付件校验 SPI（TD-P8-012）。
     *
     * <p>由 {@code pms-deliverable} 模块实现并注册为 Spring Bean。若该模块未加载
     * （bean 不存在），fallback 到本类内联的集合判断逻辑（已按 TD-P8-011 修复）。
     * 通过 SPI 复用 {@code validateMandatoryDeliverables} 的集合判断，避免两套并行逻辑。</p>
     */
    @Autowired(required = false)
    private MandatoryDeliverableValidator mandatoryDeliverableValidator;

    /**
     * 任务完成率校验 SPI（TD-P8-005）。
     *
     * <p>由 {@code pms-implementation} 模块实现并注册为 Spring Bean。若该模块未加载
     * （bean 不存在），TASK 分支跳过校验（仅 log.warn），避免在无任务数据时锁死阶段推进。</p>
     */
    @Autowired(required = false)
    private TaskCompletionChecker taskCompletionChecker;

    /**
     * 审批状态校验 SPI（TD-P8-005）。
     *
     * <p>由 {@code pms-workflow} 模块实现并注册为 Spring Bean。若该模块未加载
     * （bean 不存在），APPROVAL 分支跳过校验（仅 log.warn）。</p>
     */
    @Autowired(required = false)
    private ApprovalStatusChecker approvalStatusChecker;

    /** 阶段状态常量（关联设计文档 §3.2） */
    private static final String PHASE_NOT_STARTED = "NOT_STARTED";
    private static final String PHASE_IN_PROGRESS = "IN_PROGRESS";
    private static final String PHASE_COMPLETED = "COMPLETED";

    /** 项目生命周期状态常量（关联设计文档 §3.1） */
    private static final String PROJECT_CLOSING = "CLOSING";

    /**
     * 交付件「已批准」状态集合（TD-P8-011）。
     *
     * <p>关联设计文档 §3.4 行 427：阶段退出条件判断「必需交付件是否达到
     * PUBLISHED/REFERENCED/ARCHIVED（即已批准）」。当 {@code requiredStatus} 属于此集合时，
     * 校验 {@code d.getStatus()} 是否也在此集合中（集合判断），而非精确匹配单一状态。
     * 与 {@code DeliverableStatus.isApproved()} 保持语义一致。</p>
     */
    private static final Set<String> DELIVERABLE_APPROVED_SET = Set.of("PUBLISHED", "REFERENCED", "ARCHIVED");

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
     * <p>已实现全部 4 类：
     * <ul>
     *   <li>DELIVERABLE：必需交付件状态校验（TD-P8-011/012，优先走 SPI，fallback 内联集合判断）</li>
     *   <li>MILESTONE：必需里程碑 mustReached=true 时须为 COMPLETED</li>
     *   <li>TASK：必需任务 allCompleted=true 时通过 TaskCompletionChecker SPI 校验（TD-P8-005）</li>
     *   <li>APPROVAL：必需审批 mustApproved=true 时通过 ApprovalStatusChecker SPI 校验（TD-P8-005）</li>
     * </ul>
     * TASK/APPROVAL 通过 SPI 解耦 pms-implementation/pms-workflow，bean 不存在时跳过校验（仅 log.warn），
     * 避免在无任务/审批数据时锁死阶段推进。
     */
    private List<PhaseExitGateViolation> validateExitGate(ProjectPhase phase) {
        List<PhaseExitGateViolation> violations = new ArrayList<>();
        PhaseExitGate gate = phase.getExitCriteria();
        if (gate == null) {
            return violations; // 未配置退出条件，直接通过
        }

        // 1. 必需交付件：状态须满足 requiredStatus 语义
        //    TD-P8-012：优先通过 MandatoryDeliverableValidator SPI 复用 pms-deliverable 的
        //    validateMandatoryDeliverables 逻辑（基于 mandatory 标志 + isApproved 集合判断）。
        //    若 SPI bean 不存在（pms-deliverable 未加载），fallback 到本类内联逻辑：
        //    TD-P8-011：当 requiredStatus 属于「已批准集合」时按集合判断，否则精确匹配。
        if (gate.getRequiredDeliverables() != null) {
            if (mandatoryDeliverableValidator != null) {
                // SPI 路径：复用 pms-deliverable 的集合判断逻辑
                List<DeliverableViolation> spiViolations =
                        mandatoryDeliverableValidator.findMandatoryDeliverableViolations(phase.getId());
                if (spiViolations != null) {
                    for (DeliverableViolation dv : spiViolations) {
                        violations.add(PhaseExitGateViolation.builder()
                                .gateType("DELIVERABLE")
                                .message("必需交付件未达到已批准状态")
                                .businessId(dv.getDeliverableId())
                                .businessName(dv.getDeliverableName())
                                .expectedStatus(dv.getExpectedStatus() != null
                                        ? dv.getExpectedStatus()
                                        : "已批准（PUBLISHED/REFERENCED/ARCHIVED）")
                                .actualStatus(dv.getActualStatus())
                                .build());
                    }
                }
            } else {
                // Fallback 路径：本类内联集合判断（TD-P8-011 已修复）
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
                        continue;
                    }
                    if (req.getRequiredStatus() == null) {
                        continue;
                    }
                    boolean satisfied;
                    String expectedDisplay;
                    if (DELIVERABLE_APPROVED_SET.contains(req.getRequiredStatus())) {
                        // 集合判断：达到任一已批准状态即可
                        satisfied = DELIVERABLE_APPROVED_SET.contains(d.getStatus());
                        expectedDisplay = "已批准（PUBLISHED/REFERENCED/ARCHIVED）";
                    } else {
                        // 精确匹配：DRAFT/SUBMITTED/REVIEWED/SIGNED 等
                        satisfied = req.getRequiredStatus().equals(d.getStatus());
                        expectedDisplay = req.getRequiredStatus();
                    }
                    if (!satisfied) {
                        violations.add(PhaseExitGateViolation.builder()
                                .gateType("DELIVERABLE")
                                .message("必需交付件未达到要求状态")
                                .businessId(d.getId())
                                .businessName(d.getDeliverableName())
                                .expectedStatus(expectedDisplay)
                                .actualStatus(d.getStatus())
                                .build());
                    }
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

        // 3. 必需任务（requiredTasks）：TD-P8-005 通过 TaskCompletionChecker SPI 跨模块校验。
        //    设计 §3.4 定义 TASK 类退出条件为「阶段内任务完成率达阈值」，本实现按 allCompleted
        //    标志查询指定 phaseId 下未完成任务。
        if (gate.getRequiredTasks() != null) {
            for (PhaseExitGate.RequiredTask req : gate.getRequiredTasks()) {
                if (!Boolean.TRUE.equals(req.getAllCompleted())) {
                    continue; // 不要求全部完成，跳过
                }
                if (taskCompletionChecker == null) {
                    log.warn("TASK 退出条件校验跳过：TaskCompletionChecker SPI 未注入（pms-implementation 模块未加载），phaseId={}",
                            req.getPhaseId());
                    continue;
                }
                List<TaskCompletionViolation> taskViolations =
                        taskCompletionChecker.findUncompletedTasks(req.getPhaseId());
                if (taskViolations != null) {
                    for (TaskCompletionViolation tv : taskViolations) {
                        violations.add(PhaseExitGateViolation.builder()
                                .gateType("TASK")
                                .message("阶段内存在未完成任务")
                                .businessId(tv.getTaskId())
                                .businessName(tv.getTaskName())
                                .expectedStatus(tv.getExpectedStatus())
                                .actualStatus(tv.getActualStatus())
                                .build());
                    }
                }
            }
        }

        // 4. 必需审批（requiredApprovals）：TD-P8-005 通过 ApprovalStatusChecker SPI 跨模块校验。
        //    设计 §3.4 定义 APPROVAL 类退出条件为「关联审批通过」。
        if (gate.getRequiredApprovals() != null) {
            for (PhaseExitGate.RequiredApproval req : gate.getRequiredApprovals()) {
                if (!Boolean.TRUE.equals(req.getMustApproved())) {
                    continue; // 不要求已通过，跳过
                }
                if (approvalStatusChecker == null) {
                    log.warn("APPROVAL 退出条件校验跳过：ApprovalStatusChecker SPI 未注入（pms-workflow 模块未加载），approvalType={}",
                            req.getApprovalType());
                    continue;
                }
                List<ApprovalViolation> approvalViolations = approvalStatusChecker.findApprovalViolations(
                        phase.getProjectId(), req.getApprovalType(), Boolean.TRUE.equals(req.getMustApproved()));
                if (approvalViolations != null) {
                    for (ApprovalViolation av : approvalViolations) {
                        violations.add(PhaseExitGateViolation.builder()
                                .gateType("APPROVAL")
                                .message("关联审批未通过")
                                .businessId(av.getApprovalRecordId())
                                .businessName(av.getApprovalType())
                                .expectedStatus(av.getExpectedStatus())
                                .actualStatus(av.getActualStatus())
                                .build());
                    }
                }
            }
        }
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
