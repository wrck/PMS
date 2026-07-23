package com.dp.plat.baseline.controller;

import com.dp.plat.baseline.entity.TaskDependency;
import com.dp.plat.baseline.service.TaskDependencyService;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 任务依赖管理控制器 — 保存依赖（含 DFS 循环检测）、删除、查询。
 *
 * <p>关联设计文档：§5.5 依赖与基线 API（Story 4）。保存时若检测到循环依赖，
 * {@link com.dp.plat.baseline.exception.CycleDetectedException} 由
 * {@link com.dp.plat.baseline.advice.BaselineExceptionHandler} 转换为
 * HTTP 200 + {@code data.success=false} 的结构化响应（验收 1）。</p>
 *
 * <p>权限码：{@code project:baseline:save}（保存/删除依赖）。
 * 注：设计文档原文标注 {@code @RequiresPermissions}（Shiro），但本项目未引入 Shiro
 * 依赖，统一采用 Spring Security {@code @PreAuthorize}（与 pms-implementation 模块一致），
 * 权限码保持不变。</p>
 */
@Tag(name = "任务依赖管理", description = "Task dependency APIs with cycle detection")
@RestController
@RequestMapping("/api/implementation/task/dependency")
@RequiredArgsConstructor
public class TaskDependencyController {

    private final TaskDependencyService taskDependencyService;

    @Operation(summary = "查询项目下全部任务依赖")
    @GetMapping
    public Result<List<TaskDependency>> list(@RequestParam Long projectId) {
        return Result.ok(taskDependencyService.listByProject(projectId));
    }

    @Operation(summary = "保存任务依赖（含循环检测）")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('project:baseline:save')")
    @OperLog(title = "任务依赖", businessType = 1)
    public Result<TaskDependency> save(@Valid @RequestBody TaskDependency dependency) {
        return Result.ok(taskDependencyService.saveDependency(dependency));
    }

    @Operation(summary = "删除任务依赖")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('project:baseline:save')")
    @OperLog(title = "任务依赖", businessType = 3)
    public Result<Void> delete(@PathVariable Long id) {
        taskDependencyService.deleteDependency(id);
        return Result.ok();
    }
}
