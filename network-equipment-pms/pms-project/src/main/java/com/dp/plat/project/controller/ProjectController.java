package com.dp.plat.project.controller;

import com.dp.plat.common.annotation.Idempotent;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.ProjectTreeNode;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.util.Map;

/**
 * Project management controller.
 */
@Tag(name = "项目管理", description = "Project delivery management APIs")
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    @Operation(summary = "创建项目")
    @PostMapping
    @PreAuthorize("hasAuthority('project:project:add')")
    @OperLog(title = "项目管理", businessType = 1)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    @Idempotent
    public Result<Project> create(@Valid @RequestBody Project project) {
        return projectService.createProject(project);
    }

    @Operation(summary = "根据ID查询项目")
    @GetMapping("/{id}")
    public Result<Project> get(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @Operation(summary = "分页查询项目列表")
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String projectName,
                          @RequestParam(required = false) String status) {
        return projectService.listProjects(page, size, projectName, status);
    }

    @Operation(summary = "更新项目")
    @PutMapping
    @PreAuthorize("hasAuthority('project:project:edit')")
    @OperLog(title = "项目管理", businessType = 2)
    @RateLimit(key = "#userId", capacity = 30, refillTokens = 30, refillPeriodSeconds = 60)
    @Idempotent
    public Result<?> update(@Valid @RequestBody Project project) {
        return projectService.updateProject(project);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:project:remove')")
    @OperLog(title = "项目管理", businessType = 3)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    public Result<?> delete(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }

    @Operation(summary = "审批立项项目")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('project:project:approve')")
    @OperLog(title = "项目管理", businessType = 2)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    public Result<Project> approve(@PathVariable Long id) {
        return projectService.approveProject(id);
    }

    @Operation(summary = "获取项目仪表盘数据")
    @GetMapping("/dashboard")
    public Result<Map<String, List<Project>>> dashboard(@RequestParam(required = false) String status) {
        return projectService.dashboard(status);
    }

    // ============ Phase 3：主子项目与生命周期（Story 2，§5.3） ============

    @Operation(summary = "主子项目树（递归）")
    @GetMapping("/{id}/tree")
    public Result<ProjectTreeNode> tree(@PathVariable Long id) {
        return projectService.getProjectTree(id);
    }

    @Operation(summary = "创建子项目")
    @PostMapping("/{id}/subproject")
    @RequiresPermissions("project:subproject:manage")
    @OperLog(title = "项目管理-创建子项目", businessType = 1)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    @Idempotent
    public Result<Project> createSubproject(@PathVariable Long id,
                                            @Valid @RequestBody Project subproject) {
        return projectService.createSubproject(id, subproject);
    }

    @Operation(summary = "关闭主项目（含子项目校验）")
    @PostMapping("/{id}/close")
    @RequiresPermissions("project:close")
    @OperLog(title = "项目管理-关闭项目", businessType = 2)
    @RateLimit(key = "#userId", capacity = 5, refillTokens = 5, refillPeriodSeconds = 60)
    public Result<Project> close(@PathVariable Long id) {
        return projectService.closeProject(id);
    }

    @Operation(summary = "取消项目")
    @PostMapping("/{id}/cancel")
    @RequiresPermissions("project:close")
    @OperLog(title = "项目管理-取消项目", businessType = 2)
    @RateLimit(key = "#userId", capacity = 5, refillTokens = 5, refillPeriodSeconds = 60)
    public Result<Project> cancel(@PathVariable Long id) {
        return projectService.cancelProject(id);
    }

    @Operation(summary = "项目进度汇总（含子项目）")
    @GetMapping("/{id}/progress")
    public Result<Map<String, Object>> progress(@PathVariable Long id) {
        return projectService.getProjectProgress(id);
    }
}
