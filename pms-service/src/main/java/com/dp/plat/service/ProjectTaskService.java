package com.dp.plat.service;

import com.dp.plat.model.entity.PmsProjectTask;

import java.util.List;

public interface ProjectTaskService {

    List<PmsProjectTask> queryTasksByProject(Long projectId);

    void saveTasks(Long projectId, List<PmsProjectTask> tasks);

    void updateTask(PmsProjectTask task);
}
