package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.service.IProjectPhaseService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目阶段 Controller
 * 关联设计文档：§5.1 API 路由总览
 *
 * <p>注：advancePhase 端点在 Phase 3 实现计划中添加。
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
    @RequiresPermissions("project:advance:phase")
    public Result<ProjectPhase> create(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.create(phase));
    }

    @PutMapping
    @RequiresPermissions("project:advance:phase")
    public Result<ProjectPhase> update(@RequestBody ProjectPhase phase) {
        return Result.ok(phaseService.update(phase));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("project:advance:phase")
    public Result<Void> delete(@PathVariable Long id) {
        phaseService.delete(id);
        return Result.ok();
    }
}
