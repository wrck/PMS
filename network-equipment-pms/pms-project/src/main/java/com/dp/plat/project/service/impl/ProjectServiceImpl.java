package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.metrics.BusinessMetrics;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IProjectService;
import com.dp.plat.workflow.dto.CompleteTaskRequest;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.dto.TaskDTO;
import com.dp.plat.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IProjectService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    /** Default status for a newly created project. */
    private static final String STATUS_PENDING = "PENDING";
    /** Status after the project is approved. */
    private static final String STATUS_APPROVED = "APPROVED";
    /** Default priority. */
    private static final String PRIORITY_NORMAL = "NORMAL";

    /** Workflow process definition key for project approval. */
    private static final String PROCESS_KEY_PROJECT_APPROVAL = "projectApproval";
    /** Workflow variable name for the project manager user id. */
    private static final String VAR_PM_USER_ID = "pmUserId";
    /** Approval comment for project approval. */
    private static final String APPROVE_COMMENT = "项目审批通过";
    /** Page size used when querying todo tasks for a process instance. */
    private static final int TODO_QUERY_SIZE = 200;

    private final WorkflowService workflowService;
    private final BusinessMetrics businessMetrics;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createProject(Project project) {
        if (project == null) {
            throw new BusinessException("项目信息不能为空");
        }
        if (!StringUtils.hasText(project.getProjectName())) {
            throw new BusinessException("项目名称不能为空");
        }
        // New project starts in PENDING status.
        project.setStatus(STATUS_PENDING);
        if (!StringUtils.hasText(project.getPriority())) {
            project.setPriority(PRIORITY_NORMAL);
        }
        if (project.getProgress() == null) {
            project.setProgress(0);
        }
        // Project code is generated on approval, not on creation.
        project.setId(null);
        this.save(project);
        // 业务指标：记录项目创建（按项目类型计数）
        businessMetrics.recordProjectCreated(project.getProjectType());
        startApprovalWorkflow(project);
        return Result.ok(project);
    }

    @Override
    public Result<Project> getProjectById(Long id) {
        Project project = this.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return Result.ok(project);
    }

    @Override
    public Result<Page<Project>> listProjects(int page, int size, String projectName, String status) {
        Page<Project> pageObj = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : size);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .like(StringUtils.hasText(projectName), Project::getProjectName, projectName)
                .eq(StringUtils.hasText(status), Project::getStatus, status)
                .orderByDesc(Project::getCreateTime);
        Page<Project> result = this.page(pageObj, wrapper);
        return Result.ok(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProject(Project project) {
        if (project == null || project.getId() == null) {
            throw new BusinessException("项目信息或ID不能为空");
        }
        Project existing = this.getById(project.getId());
        if (existing == null) {
            throw new BusinessException("项目不存在");
        }
        this.updateById(project);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteProject(Long id) {
        Project existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("项目不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result approveProject(Long projectId) {
        Project project = this.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (!STATUS_PENDING.equals(project.getStatus())) {
            throw new BusinessException("当前项目状态不允许审批");
        }
        project.setStatus(STATUS_APPROVED);
        // Generate the project code on approval.
        if (!StringUtils.hasText(project.getProjectCode())) {
            project.setProjectCode(generateProjectCode());
        }
        this.updateById(project);
        completeApprovalTask(project, APPROVE_COMMENT);
        return Result.ok(project);
    }

    @Override
    public Result<Map<String, List<Project>>> dashboard(String status) {
        // Group all projects by status for dashboard display. When a status filter is
        // supplied the result still uses the same shape but only contains that group.
        List<Project> all = this.list(new LambdaQueryWrapper<Project>()
                .eq(StringUtils.hasText(status), Project::getStatus, status)
                .orderByDesc(Project::getCreateTime));
        Map<String, List<Project>> grouped = all.stream()
                .filter(p -> StringUtils.hasText(p.getStatus()))
                .collect(Collectors.groupingBy(Project::getStatus));
        return Result.ok(grouped);
    }

    @Override
    public String generateProjectCode() {
        int year = LocalDate.now().getYear();
        String prefix = "PMS-" + year + "-";
        // Count existing projects whose code starts with the year prefix, then add 1.
        long count = this.count(new LambdaQueryWrapper<Project>()
                .likeRight(Project::getProjectCode, prefix));
        long sequence = count + 1;
        return prefix + String.format("%04d", sequence);
    }

    /**
     * Start the project approval workflow for the given project and persist the
     * returned process instance id.
     */
    private void startApprovalWorkflow(Project project) {
        try {
            StartProcessRequest req = new StartProcessRequest();
            req.setProcessDefinitionKey(PROCESS_KEY_PROJECT_APPROVAL);
            req.setBusinessKey(project.getId().toString());
            Map<String, Object> variables = new HashMap<>();
            if (project.getProjectManagerId() != null) {
                variables.put(VAR_PM_USER_ID, project.getProjectManagerId());
            }
            req.setVariables(variables);
            Result<ProcessInstanceDTO> resp = workflowService.startProcess(req);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                project.setProcessInstanceId(resp.getData().getId());
                this.updateById(project);
            } else {
                log.warn("项目 {} 启动审批流程未返回实例: {}", project.getId(),
                        resp == null ? "null" : resp.getMessage());
            }
        } catch (Exception e) {
            // Workflow engine unavailable should not block project creation.
            log.error("项目 {} 启动审批流程失败: {}", project.getId(), e.getMessage(), e);
        }
    }

    /**
     * Complete the current approval task for the project's workflow instance.
     */
    private void completeApprovalTask(Project project, String comment) {
        String processInstanceId = project.getProcessInstanceId();
        if (!StringUtils.hasText(processInstanceId)) {
            return;
        }
        try {
            String taskId = findCurrentTaskId(processInstanceId);
            if (!StringUtils.hasText(taskId)) {
                log.warn("项目 {} 未找到当前待办任务，processInstanceId={}", project.getId(), processInstanceId);
                return;
            }
            CompleteTaskRequest req = new CompleteTaskRequest();
            req.setTaskId(taskId);
            req.setComment(comment);
            workflowService.completeTask(req);
        } catch (Exception e) {
            log.error("项目 {} 完成审批任务失败: {}", project.getId(), e.getMessage(), e);
        }
    }

    /**
     * Find the current user's todo task id for the given process instance.
     */
    @SuppressWarnings("unchecked")
    private String findCurrentTaskId(String processInstanceId) {
        Result<Map<String, Object>> todoResult = workflowService.getTodoTasks(1, TODO_QUERY_SIZE);
        if (todoResult == null || !todoResult.isSuccess() || todoResult.getData() == null) {
            return null;
        }
        Object records = todoResult.getData().get("records");
        if (!(records instanceof List<?> list)) {
            return null;
        }
        for (Object item : list) {
            if (item instanceof TaskDTO task
                    && processInstanceId.equals(task.getProcessInstanceId())) {
                return task.getId();
            }
        }
        return null;
    }
}
