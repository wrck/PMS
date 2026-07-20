package com.dp.plat.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 项目模板 Controller
 * 关联设计文档：§5.2 Story 1 API
 */
@RestController
@RequestMapping("/api/project/template")
@RequiredArgsConstructor
public class ProjectTemplateController {

    private final IProjectTemplateService templateService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('project:template:list')")
    public Result<Page<ProjectTemplate>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return Result.ok(templateService.page(page, size, templateName, category, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('project:template:list')")
    public Result<ProjectTemplate> getById(@PathVariable Long id) {
        return Result.ok(templateService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('project:template:add')")
    public Result<ProjectTemplate> create(@RequestBody ProjectTemplate template) {
        return Result.ok(templateService.create(template));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('project:template:add')")
    public Result<ProjectTemplate> update(@RequestBody ProjectTemplate template) {
        return Result.ok(templateService.update(template));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:template:add')")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAuthority('project:template:list')")
    public Result<Page<ProjectTemplateVersion>> listVersions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(templateService.listVersions(id, page, size));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('project:template:publish')")
    public Result<ProjectTemplateVersion> publishVersion(
            @PathVariable Long id,
            @RequestBody PublishVersionRequest request) {
        ProjectTemplateVersion version = templateService.publishVersion(
            id, request.getVersion(), request.getSnapshot(), request.getChangeLog());
        return Result.ok(version);
    }

    @GetMapping("/{id}/published-version")
    @PreAuthorize("hasAuthority('project:template:list')")
    public Result<ProjectTemplateVersion> getPublishedVersion(@PathVariable Long id) {
        return Result.ok(templateService.getPublishedVersion(id));
    }

    @PostMapping("/create-project")
    @PreAuthorize("hasAuthority('project:template:use')")
    public Result<Project> createProjectFromTemplate(@RequestBody ProjectCreateFromTemplateDTO dto) {
        return Result.ok(templateService.createProjectFromTemplate(dto));
    }

    /** 发布版本请求体 */
    @lombok.Data
    public static class PublishVersionRequest {
        private String version;
        private TemplateSnapshot snapshot;
        private String changeLog;
    }
}
