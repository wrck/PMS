package com.dp.plat.implementation.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.TaskComment;
import com.dp.plat.implementation.service.ITaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务评论管理控制器（支持二级回复）。
 */
@Tag(name = "任务评论管理", description = "Task comment APIs")
@RestController
@RequestMapping("/api/implementation/task/comment")
@RequiredArgsConstructor
public class TaskCommentController {

    private final ITaskCommentService taskCommentService;

    @Operation(summary = "查询任务评论列表")
    @GetMapping("/{taskId}")
    public Result<List<TaskComment>> list(@PathVariable Long taskId) {
        return Result.ok(taskCommentService.listByTaskId(taskId));
    }

    @Operation(summary = "新增评论")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('project:task:edit')")
    @OperLog(title = "任务评论", businessType = 1)
    public Result<TaskComment> create(@Valid @RequestBody TaskComment comment) {
        return Result.ok(taskCommentService.create(comment));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('project:task:edit')")
    @OperLog(title = "任务评论", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        taskCommentService.delete(id);
        return Result.ok();
    }
}
