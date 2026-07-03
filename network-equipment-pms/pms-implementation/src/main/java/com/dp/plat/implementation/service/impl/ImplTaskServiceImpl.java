package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.implementation.service.IImplProgressService;
import com.dp.plat.implementation.service.IImplTaskService;
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

    private final IImplProgressService implProgressService;
    private final NotificationService notificationService;

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
}
