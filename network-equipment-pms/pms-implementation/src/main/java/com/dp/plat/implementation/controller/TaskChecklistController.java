package com.dp.plat.implementation.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.TaskChecklist;
import com.dp.plat.implementation.service.ITaskChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务检查项管理控制器。
 * 关联设计文档 §5.4 检查项 API。
 */
@Tag(name = "任务检查项管理", description = "Task checklist APIs")
@RestController
@RequestMapping("/api/implementation/task/checklist")
@RequiredArgsConstructor
public class TaskChecklistController {

    private final ITaskChecklistService taskChecklistService;

    @Operation(summary = "查询任务检查项列表")
    @GetMapping("/{taskId}")
    public Result<List<TaskChecklist>> list(@PathVariable Long taskId) {
        return Result.ok(taskChecklistService.listByTaskId(taskId));
    }

    @Operation(summary = "新增检查项")
    @PostMapping
    @PreAuthorize("hasAuthority('project:task:edit')")
    @OperLog(title = "任务检查项", businessType = 1)
    public Result<TaskChecklist> create(@Valid @RequestBody TaskChecklist checklist) {
        return Result.ok(taskChecklistService.create(checklist));
    }

    @Operation(summary = "更新检查项")
    @PutMapping
    @PreAuthorize("hasAuthority('project:task:edit')")
    @OperLog(title = "任务检查项", businessType = 2)
    public Result<TaskChecklist> update(@Valid @RequestBody TaskChecklist checklist) {
        return Result.ok(taskChecklistService.update(checklist));
    }

    @Operation(summary = "勾选/取消勾选检查项")
    @PostMapping("/{id}/check")
    @PreAuthorize("hasAuthority('project:task:edit')")
    @OperLog(title = "任务检查项", businessType = 2)
    public Result<TaskChecklist> toggleCheck(@PathVariable Long id,
                                             @RequestParam(defaultValue = "true") boolean checked) {
        return Result.ok(taskChecklistService.toggleCheck(id, checked));
    }

    @Operation(summary = "删除检查项")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:task:edit')")
    @OperLog(title = "任务检查项", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        taskChecklistService.delete(id);
        return Result.ok();
    }
}
