package com.dp.plat.project.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目阶段 Controller
 * 关联设计文档：§5.1 API 路由总览、§5.3 Story 2
 */
@RestController
@RequestMapping("/api/project/phase")
@RequiredArgsConstructor
public class ProjectPhaseController {

    private final IProjectPhaseService phaseService;

    @GetMapping("/project/{projectId}")
    public Result<List<ProjectPhase>> listByProjectId(@PathVariable Long projectId) {
        return Result.ok(phaseService.listByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public Result<ProjectPhase> getById(@PathVariable Long id) {
        return Result.ok(phaseService.getById(id));
    }

    @PostMapping
    @RequiresPermissions("project:phase:advance")
    public Result<ProjectPhase> create(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.create(phase));
    }

    @PutMapping
    @RequiresPermissions("project:phase:advance")
    public Result<ProjectPhase> update(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.update(phase));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("project:phase:advance")
    public Result<Void> delete(@PathVariable Long id) {
        phaseService.delete(id);
        return Result.ok();
    }

    /**
     * 推进阶段（含 4 类退出条件校验）。
     *
     * <p>关联设计文档：§3.2 Story 2 验收 1、§5.3 POST /api/project/phase/{phaseId}/advance。
     * 校验失败时抛 {@code PhaseExitGateFailedException}，由 {@code ProjectExceptionHandler}
     * 转换为 {@code code=200} 的结构化响应体（{@code data.success=false} + violations）。
     */
    @Operation(summary = "推进阶段（含退出条件校验）")
    @PostMapping("/{phaseId}/advance")
    @RequiresPermissions("project:phase:advance")
    @OperLog(title = "项目阶段-推进", businessType = 2)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    public Result<ProjectPhase> advance(@PathVariable Long phaseId) {
        return phaseService.advancePhase(phaseId);
    }
}
