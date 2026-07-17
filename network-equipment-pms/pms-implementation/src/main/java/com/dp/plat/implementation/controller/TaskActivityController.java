package com.dp.plat.implementation.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.TaskActivity;
import com.dp.plat.implementation.service.ITaskActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务活动记录控制器（只读查询，活动由各业务操作内部记录）。
 */
@Tag(name = "任务活动记录", description = "Task activity APIs")
@RestController
@RequestMapping("/api/impl/task/activity")
@RequiredArgsConstructor
public class TaskActivityController {

    private final ITaskActivityService taskActivityService;

    @Operation(summary = "查询任务活动记录（按时间倒序）")
    @GetMapping("/{taskId}")
    public Result<List<TaskActivity>> listByTaskId(@PathVariable Long taskId) {
        return Result.ok(taskActivityService.listByTaskId(taskId));
    }
}
