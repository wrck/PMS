package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.PmsProjectTaskMapper;
import com.dp.plat.model.entity.PmsProjectTask;
import com.dp.plat.service.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectTaskServiceImpl implements ProjectTaskService {

    @Autowired
    private PmsProjectTaskMapper taskMapper;

    @Override
    public List<PmsProjectTask> queryTasksByProject(Long projectId) {
        return taskMapper.selectList(new LambdaQueryWrapper<PmsProjectTask>()
                .eq(PmsProjectTask::getProjectId, projectId)
                .and(w -> w.le(PmsProjectTask::getEffectiveFrom, LocalDateTime.now())
                        .and(x -> x.isNull(PmsProjectTask::getEffectiveTo).or()
                                .gt(PmsProjectTask::getEffectiveTo, LocalDateTime.now())))
                .orderByAsc(PmsProjectTask::getTaskTypeId));
    }

    @Override
    @Transactional
    public void saveTasks(Long projectId, List<PmsProjectTask> tasks) {
        for (PmsProjectTask task : tasks) {
            task.setProjectId(projectId);
            task.setEffectiveFrom(LocalDateTime.now());
            taskMapper.insert(task);
        }
    }

    @Override
    @Transactional
    public void updateTask(PmsProjectTask task) {
        taskMapper.updateById(task);
    }
}
