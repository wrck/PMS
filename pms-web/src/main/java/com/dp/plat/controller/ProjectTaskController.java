package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProjectTask;
import com.dp.plat.service.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project/task")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService taskService;

    @GetMapping("/list")
    public R<List<PmsProjectTask>> list(@RequestParam Long projectId) {
        return R.ok(taskService.queryTasksByProject(projectId));
    }

    @PostMapping
    public R<Void> save(@RequestParam Long projectId, @RequestBody List<PmsProjectTask> tasks) {
        taskService.saveTasks(projectId, tasks);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProjectTask task) {
        taskService.updateTask(task);
        return R.ok();
    }
}
