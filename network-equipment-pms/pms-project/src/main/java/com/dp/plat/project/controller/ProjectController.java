package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Project> create(@RequestBody Project project) {
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
    public Result<?> update(@RequestBody Project project) {
        return projectService.updateProject(project);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }

    @Operation(summary = "审批立项项目")
    @PostMapping("/{id}/approve")
    public Result<Project> approve(@PathVariable Long id) {
        return projectService.approveProject(id);
    }

    @Operation(summary = "获取项目仪表盘数据")
    @GetMapping("/dashboard")
    public Result<?> dashboard(@RequestParam(required = false) String status) {
        return projectService.dashboard(status);
    }
}
