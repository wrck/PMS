package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.project.entity.FinalAcceptance;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.FinalAcceptanceMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IFinalAcceptanceService;
import com.dp.plat.project.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IFinalAcceptanceService}.
 */
@Service
@RequiredArgsConstructor
public class FinalAcceptanceServiceImpl extends ServiceImpl<FinalAcceptanceMapper, FinalAcceptance>
        implements IFinalAcceptanceService {

    /** Acceptance status values. */
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    /** Milestone completed status. */
    private static final String MILESTONE_COMPLETED = "COMPLETED";
    /** Project status after final acceptance is approved. */
    private static final String PROJECT_COMPLETED = "COMPLETED";

    private final IMilestoneService milestoneService;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result apply(Long projectId, String report) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        // Check all milestones are completed before applying for final acceptance.
        List<Milestone> milestones = milestoneService.list(new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getProjectId, projectId));
        if (milestones.isEmpty()) {
            throw new BusinessException("项目暂无里程碑，无法申请终验");
        }
        boolean allCompleted = milestones.stream()
                .allMatch(m -> MILESTONE_COMPLETED.equals(m.getStatus()));
        if (!allCompleted) {
            throw new BusinessException("存在未完成的里程碑，无法申请终验");
        }
        // Prevent duplicate pending applications.
        FinalAcceptance existing = this.getOne(new LambdaQueryWrapper<FinalAcceptance>()
                .eq(FinalAcceptance::getProjectId, projectId)
                .eq(FinalAcceptance::getStatus, STATUS_PENDING));
        if (existing != null) {
            throw new BusinessException("该项目已有待审批的终验申请");
        }

        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(projectId)
                .applyTime(LocalDateTime.now())
                .applyUserId(SecurityUtils.getCurrentUserId())
                .applyUserName(SecurityUtils.getCurrentUsername())
                .status(STATUS_PENDING)
                .acceptanceReport(report)
                .build();
        this.save(acceptance);
        // TODO: integrate workflow module to start the final acceptance approval workflow once pms-workflow is ready.
        return Result.ok(acceptance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result approve(Long acceptanceId, String opinion) {
        FinalAcceptance acceptance = this.getById(acceptanceId);
        if (acceptance == null) {
            throw new BusinessException("终验申请不存在");
        }
        if (!STATUS_PENDING.equals(acceptance.getStatus())) {
            throw new BusinessException("当前终验申请状态不允许审批");
        }
        acceptance.setStatus(STATUS_APPROVED);
        acceptance.setAcceptanceOpinion(opinion);
        acceptance.setAcceptUserId(SecurityUtils.getCurrentUserId());
        acceptance.setAcceptUserName(SecurityUtils.getCurrentUsername());
        acceptance.setAcceptTime(LocalDateTime.now());
        this.updateById(acceptance);

        // Close the project by setting its status to COMPLETED.
        Project project = projectMapper.selectById(acceptance.getProjectId());
        if (project != null) {
            project.setStatus(PROJECT_COMPLETED);
            projectMapper.updateById(project);
        }

        // TODO: release bound project equipment once the asset module is ready.
        return Result.ok(acceptance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result reject(Long acceptanceId, String opinion) {
        FinalAcceptance acceptance = this.getById(acceptanceId);
        if (acceptance == null) {
            throw new BusinessException("终验申请不存在");
        }
        if (!STATUS_PENDING.equals(acceptance.getStatus())) {
            throw new BusinessException("当前终验申请状态不允许操作");
        }
        acceptance.setStatus(STATUS_REJECTED);
        acceptance.setAcceptanceOpinion(StringUtils.hasText(opinion) ? opinion : "");
        acceptance.setAcceptUserId(SecurityUtils.getCurrentUserId());
        acceptance.setAcceptUserName(SecurityUtils.getCurrentUsername());
        acceptance.setAcceptTime(LocalDateTime.now());
        this.updateById(acceptance);
        return Result.ok(acceptance);
    }

    @Override
    public Result<FinalAcceptance> getByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        FinalAcceptance acceptance = this.getOne(new LambdaQueryWrapper<FinalAcceptance>()
                .eq(FinalAcceptance::getProjectId, projectId)
                .orderByDesc(FinalAcceptance::getCreateTime)
                .last("LIMIT 1"));
        return Result.ok(acceptance);
    }
}
