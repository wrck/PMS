package com.dp.plat.implementation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.service.IImplTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementation task management controller.
 */
@Tag(name = "实施任务管理", description = "Implementation task management APIs")
@RestController
@RequestMapping("/api/impl/task")
@RequiredArgsConstructor
public class ImplTaskController {

    private final IImplTaskService implTaskService;

    @Operation(summary = "Assign OEM implementation task")
    @PostMapping("/oem/assign")
    @PreAuthorize("hasAuthority('implementation:implTask:add')")
    @OperLog(title = "实施任务管理", businessType = 1)
    public Result<ImplTask> assignOem(@Valid @RequestBody ImplTask task) {
        return Result.ok(implTaskService.assignOemTask(task));
    }

    @Operation(summary = "Assign agent implementation task")
    @PostMapping("/agent/assign")
    @PreAuthorize("hasAuthority('implementation:implTask:add')")
    @OperLog(title = "实施任务管理", businessType = 1)
    public Result<ImplTask> assignAgent(@Valid @RequestBody ImplTask task) {
        return Result.ok(implTaskService.assignAgentTask(task));
    }

    @Operation(summary = "Accept a task")
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('implementation:implTask:edit')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> accept(@PathVariable Long id) {
        implTaskService.acceptTask(id);
        return Result.ok();
    }

    @Operation(summary = "Start a task")
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('implementation:implTask:edit')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> start(@PathVariable Long id) {
        implTaskService.startTask(id);
        return Result.ok();
    }

    @Operation(summary = "Report task progress")
    @PostMapping("/{id}/progress")
    @PreAuthorize("hasAuthority('implementation:implTask:edit')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> reportProgress(@PathVariable Long id, @Valid @RequestBody ImplProgress progress) {
        implTaskService.reportProgress(id, progress);
        return Result.ok();
    }

    @Operation(summary = "Complete a task")
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('implementation:implTask:edit')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> complete(@PathVariable Long id, @RequestParam(required = false) String description) {
        implTaskService.completeTask(id, description);
        return Result.ok();
    }

    @Operation(summary = "Confirm a completed task")
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('implementation:implTask:confirm')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> confirm(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        implTaskService.confirmTask(id, opinion);
        return Result.ok();
    }

    @Operation(summary = "Reject a task")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('implementation:implTask:confirm')")
    @OperLog(title = "实施任务管理", businessType = 2)
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        implTaskService.rejectTask(id, opinion);
        return Result.ok();
    }

    @Operation(summary = "Get task by id")
    @GetMapping("/{id}")
    public Result<ImplTask> get(@PathVariable Long id) {
        return Result.ok(implTaskService.getById(id));
    }

    @Operation(summary = "Paginated task query")
    @GetMapping("/list")
    public Result<Page<ImplTask>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       ImplTask filters) {
        return Result.ok(implTaskService.list(page, size, filters));
    }

    @Operation(summary = "List tasks by project id")
    @GetMapping("/project/{projectId}")
    public Result<List<ImplTask>> listByProject(@PathVariable Long projectId) {
        return Result.ok(implTaskService.getByProjectId(projectId));
    }
}
