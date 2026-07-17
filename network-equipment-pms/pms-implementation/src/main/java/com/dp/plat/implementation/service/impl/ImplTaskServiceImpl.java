package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.dto.TaskReviewResult;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.entity.TaskChecklist;
import com.dp.plat.implementation.exception.TaskChecklistRequiredException;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.implementation.mapper.TaskChecklistMapper;
import com.dp.plat.implementation.service.IImplProgressService;
import com.dp.plat.implementation.service.IImplTaskService;
import com.dp.plat.implementation.service.ITaskRollupService;
import com.dp.plat.implementation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IImplTaskService}.
 */
@Service
@RequiredArgsConstructor
public class ImplTaskServiceImpl extends ServiceImpl<ImplTaskMapper, ImplTask> implements IImplTaskService {

    /** Task type: OEM implementation. */
    public static final String TYPE_OEM = "OEM";
    /** Task type: agent implementation. */
    public static final String TYPE_AGENT = "AGENT";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_REJECTED = "REJECTED";
    /** 评审中（提交评审后、验收前）。 */
    public static final String STATUS_REVIEW = "REVIEW";

    private final IImplProgressService implProgressService;
    private final NotificationService notificationService;
    private final TaskChecklistMapper taskChecklistMapper;
    private final ITaskRollupService taskRollupService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImplTask assignOemTask(ImplTask task) {
        task.setTaskType(TYPE_OEM);
        task.setStatus(STATUS_PENDING);
        if (task.getProgress() == null) {
            task.setProgress(0);
        }
        this.save(task);
        if (task.getEngineerId() != null) {
            notificationService.notifyUser(task.getEngineerId(), "派工通知",
                    "您有新的原厂实施任务：" + task.getTaskName());
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImplTask assignAgentTask(ImplTask task) {
        task.setTaskType(TYPE_AGENT);
        task.setStatus(STATUS_PENDING);
        if (task.getProgress() == null) {
            task.setProgress(0);
        }
        this.save(task);
        if (task.getAgentId() != null) {
            notificationService.notifyUser(task.getAgentId(), "委派通知",
                    "您有新的代理商实施任务：" + task.getTaskName());
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptTask(Long taskId) {
        ImplTask task = loadOrThrow(taskId);
        if (!STATUS_PENDING.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许接单");
        }
        task.setStatus(STATUS_ACCEPTED);
        task.setActualStartDate(LocalDateTime.now().toLocalDate());
        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTask(Long taskId) {
        ImplTask task = loadOrThrow(taskId);
        if (!STATUS_ACCEPTED.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许开始");
        }
        task.setStatus(STATUS_IN_PROGRESS);
        if (task.getActualStartDate() == null) {
            task.setActualStartDate(LocalDateTime.now().toLocalDate());
        }
        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportProgress(Long taskId, ImplProgress progress) {
        ImplTask task = loadOrThrow(taskId);
        progress.setTaskId(taskId);
        implProgressService.create(progress);

        if (progress.getProgressPercent() != null) {
            task.setProgress(progress.getProgressPercent());
        }
        if (STATUS_ACCEPTED.equals(task.getStatus())) {
            task.setStatus(STATUS_IN_PROGRESS);
        }
        this.updateById(task);
        // 进度变更后异步触发父链路汇总
        taskRollupService.recalculateProgress(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, String description) {
        ImplTask task = loadOrThrow(taskId);
        if (!STATUS_IN_PROGRESS.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许完成");
        }
        task.setStatus(STATUS_COMPLETED);
        task.setActualEndDate(LocalDateTime.now().toLocalDate());
        task.setProgress(100);
        if (description != null) {
            task.setWorkDescription(description);
        }
        this.updateById(task);
        // 完成时进度置 100，异步触发父链路汇总
        taskRollupService.recalculateProgress(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmTask(Long taskId, String opinion) {
        ImplTask task = loadOrThrow(taskId);
        if (!STATUS_COMPLETED.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许确认");
        }
        task.setStatus(STATUS_CONFIRMED);
        task.setAcceptOpinion(opinion);
        task.setAcceptUserId(SecurityUtils.getCurrentUserId());
        task.setAcceptUserName(SecurityUtils.getCurrentUsername());
        task.setAcceptTime(LocalDateTime.now());
        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(Long taskId, String opinion) {
        ImplTask task = loadOrThrow(taskId);
        task.setStatus(STATUS_REJECTED);
        task.setAcceptOpinion(opinion);
        task.setAcceptUserId(SecurityUtils.getCurrentUserId());
        task.setAcceptUserName(SecurityUtils.getCurrentUsername());
        task.setAcceptTime(LocalDateTime.now());
        this.updateById(task);
    }

    @Override
    public List<ImplTask> getByProjectId(Long projectId) {
        return this.list(new LambdaQueryWrapper<ImplTask>()
                .eq(ImplTask::getProjectId, projectId)
                .orderByDesc(ImplTask::getCreateTime));
    }

    @Override
    public Page<ImplTask> list(int page, int size, ImplTask filters) {
        LambdaQueryWrapper<ImplTask> wrapper = new LambdaQueryWrapper<>();
        if (filters != null) {
            wrapper.eq(filters.getProjectId() != null, ImplTask::getProjectId, filters.getProjectId())
                    .eq(filters.getTaskType() != null, ImplTask::getTaskType, filters.getTaskType())
                    .eq(filters.getStatus() != null, ImplTask::getStatus, filters.getStatus())
                    .eq(filters.getAgentId() != null, ImplTask::getAgentId, filters.getAgentId())
                    .eq(filters.getEngineerId() != null, ImplTask::getEngineerId, filters.getEngineerId())
                    .like(filters.getTaskName() != null, ImplTask::getTaskName, filters.getTaskName());
        }
        wrapper.orderByDesc(ImplTask::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    private ImplTask loadOrThrow(Long taskId) {
        ImplTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("实施任务不存在");
        }
        return task;
    }

    /**
     * 提交评审 — 含强制检查项校验（Story 3 验收 1）。
     *
     * <p>流程：
     * <ol>
     *   <li>查询任务所有 mandatory=true 的检查项</li>
     *   <li>过滤 checked=false 的条目</li>
     *   <li>若存在未勾选的强制检查项 → 抛 {@link TaskChecklistRequiredException}（携带未勾选列表）</li>
     *   <li>全部已勾选 → 更新任务状态为 REVIEW</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskReviewResult submitForReview(Long taskId, Long operatorId) {
        ImplTask task = loadOrThrow(taskId);

        // 仅 IN_PROGRESS 状态允许提交评审
        if (!STATUS_IN_PROGRESS.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许提交评审");
        }

        // 1. 查询所有强制检查项（mandatory=true）
        List<TaskChecklist> mandatoryItems = taskChecklistMapper.selectList(
                new LambdaQueryWrapper<TaskChecklist>()
                        .eq(TaskChecklist::getTaskId, taskId)
                        .eq(TaskChecklist::getMandatory, true));

        // 2. 过滤未勾选的强制检查项
        List<TaskChecklist> uncheckedMandatory = mandatoryItems.stream()
                .filter(item -> !Boolean.TRUE.equals(item.getChecked()))
                .toList();

        // 3. 存在未勾选的强制检查项 → 拦截，抛异常（保持原状态）
        if (!uncheckedMandatory.isEmpty()) {
            throw new TaskChecklistRequiredException(uncheckedMandatory, task.getStatus());
        }

        // 4. 全部强制检查项已勾选 → 流转至 REVIEW
        task.setStatus(STATUS_REVIEW);
        this.updateById(task);

        return TaskReviewResult.builder()
                .success(true)
                .taskStatus(STATUS_REVIEW)
                .build();
    }

    /**
     * 验收任务 — 评审通过，流转至 COMPLETED。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskReviewResult approveTask(Long taskId, Long operatorId) {
        ImplTask task = loadOrThrow(taskId);
        if (!STATUS_REVIEW.equals(task.getStatus())) {
            throw new BusinessException("当前任务状态不允许验收");
        }
        task.setStatus(STATUS_COMPLETED);
        task.setProgress(100);
        if (task.getActualEndDate() == null) {
            task.setActualEndDate(LocalDateTime.now().toLocalDate());
        }
        task.setAcceptUserId(operatorId != null ? operatorId : SecurityUtils.getCurrentUserId());
        task.setAcceptUserName(SecurityUtils.getCurrentUsername());
        task.setAcceptTime(LocalDateTime.now());
        this.updateById(task);
        // 验收完成进度置 100，异步触发父链路汇总
        taskRollupService.recalculateProgress(taskId);

        return TaskReviewResult.builder()
                .success(true)
                .taskStatus(STATUS_COMPLETED)
                .build();
    }

    /**
     * 移动任务（变更父任务），同步更新 taskPath 与 depth（含所有后代）。
     *
     * <p>步骤：
     * <ol>
     *   <li>校验 newParentId 不是 taskId 的后代（避免环路）</li>
     *   <li>计算新 taskPath：{@code <父taskPath><taskId>/}</li>
     *   <li>更新所有后代任务的 taskPath（前缀替换）与 depth（按层级差平移）</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveTask(Long taskId, Long newParentId) {
        ImplTask task = loadOrThrow(taskId);
        String oldTaskPath = task.getTaskPath();
        if (oldTaskPath == null || oldTaskPath.isBlank()) {
            oldTaskPath = "/" + taskId + "/";
        }
        int oldDepth = task.getDepth() != null ? task.getDepth() : 0;

        String newTaskPath;
        int newDepth;
        if (newParentId == null) {
            // 提升为顶层
            newTaskPath = "/" + taskId + "/";
            newDepth = 0;
        } else {
            if (newParentId.equals(taskId)) {
                throw new BusinessException("不能将任务移动到自身下");
            }
            ImplTask newParent = this.getById(newParentId);
            if (newParent == null) {
                throw new BusinessException("目标父任务不存在");
            }
            String newParentPath = newParent.getTaskPath();
            if (newParentPath == null || newParentPath.isBlank()) {
                newParentPath = "/" + newParentId + "/";
            }
            // 环路校验：新父任务的路径若以当前任务路径为前缀，说明新父是当前任务的后代
            if (newParentPath.startsWith(oldTaskPath)) {
                throw new BusinessException("不能将任务移动到其自身或子任务下（避免环路）");
            }
            newTaskPath = newParentPath + taskId + "/";
            newDepth = (newParent.getDepth() != null ? newParent.getDepth() : 0) + 1;
        }

        // 1. 更新被移动任务自身
        task.setParentTaskId(newParentId);
        task.setTaskPath(newTaskPath);
        task.setDepth(newDepth);
        this.updateById(task);

        // 2. 批量更新所有后代：前缀替换 + depth 平移
        int depthDelta = newDepth - oldDepth;
        List<ImplTask> descendants = this.list(new LambdaQueryWrapper<ImplTask>()
                .likeRight(ImplTask::getTaskPath, oldTaskPath)
                .ne(ImplTask::getId, taskId));
        for (ImplTask desc : descendants) {
            String descPath = desc.getTaskPath();
            if (descPath != null && descPath.startsWith(oldTaskPath)) {
                // 替换前缀：oldTaskPath -> newTaskPath
                desc.setTaskPath(newTaskPath + descPath.substring(oldTaskPath.length()));
            }
            if (desc.getDepth() != null) {
                desc.setDepth(desc.getDepth() + depthDelta);
            }
            this.updateById(desc);
        }
    }

    /**
     * 查询任务子树（基于 task_path 物化路径前缀匹配，含自身）。
     */
    @Override
    public List<ImplTask> getTaskSubtree(Long taskId) {
        ImplTask task = loadOrThrow(taskId);
        String path = task.getTaskPath();
        if (path == null || path.isBlank()) {
            path = "/" + taskId + "/";
        }
        // likeRight: LIKE 'path%'，匹配自身及所有后代
        return this.list(new LambdaQueryWrapper<ImplTask>()
                .likeRight(ImplTask::getTaskPath, path)
                .orderByAsc(ImplTask::getDepth)
                .orderByAsc(ImplTask::getId));
    }
}
