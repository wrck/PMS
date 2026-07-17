package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.TaskActivity;
import com.dp.plat.implementation.mapper.TaskActivityMapper;
import com.dp.plat.implementation.service.ITaskActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务活动记录服务实现。
 */
@Service
@RequiredArgsConstructor
public class TaskActivityServiceImpl extends ServiceImpl<TaskActivityMapper, TaskActivity>
        implements ITaskActivityService {

    @Override
    public List<TaskActivity> listByTaskId(Long taskId) {
        return this.list(new LambdaQueryWrapper<TaskActivity>()
                .eq(TaskActivity::getTaskId, taskId)
                .orderByDesc(TaskActivity::getCreateTime)
                .orderByDesc(TaskActivity::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(Long taskId, String activityType, String content, String metadata) {
        TaskActivity activity = TaskActivity.builder()
                .taskId(taskId)
                .activityType(activityType)
                .content(content)
                .metadata(metadata)
                .userId(SecurityUtils.getCurrentUserId())
                .userName(SecurityUtils.getCurrentUsername())
                .build();
        this.save(activity);
    }
}
