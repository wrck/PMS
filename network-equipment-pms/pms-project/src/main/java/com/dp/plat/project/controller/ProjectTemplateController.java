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

    /**
     * 保存草稿快照（新建/编辑模板时持久化阶段/任务/交付件等详细配置）。
     * 创建或更新 DRAFT 状态版本记录，不影响模板发布状态。
     */
    @PutMapping("/{id}/draft-snapshot")
    @PreAuthorize("hasAuthority('project:template:add')")
    public Result<ProjectTemplateVersion> saveDraftSnapshot(
            @PathVariable Long id,
            @RequestBody TemplateSnapshot snapshot) {
        return Result.ok(templateService.saveDraftSnapshot(id, snapshot));
    }

    /**
     * 获取模板草稿版本（最新 DRAFT 状态版本，无则返回 null）。
     */
    @GetMapping("/{id}/draft-version")
    @PreAuthorize("hasAuthority('project:template:list')")
    public Result<ProjectTemplateVersion> getDraftVersion(@PathVariable Long id) {
        return Result.ok(templateService.getDraftVersion(id));
    }

    @PostMapping("/create-project")
    @PreAuthorize("hasAuthority('project:template:use')")
    public Result<Project> createProjectFromTemplate(@RequestBody ProjectCreateFromTemplateDTO dto) {
        return Result.ok(templateService.createProjectFromTemplate(dto));
    }

    /**
     * 废弃模板（PUBLISHED → DEPRECATED）。
     */
    @PutMapping("/{id}/deprecate")
    @PreAuthorize("hasAuthority('project:template:publish')")
    public Result<ProjectTemplate> deprecate(@PathVariable Long id) {
        return Result.ok(templateService.deprecate(id));
    }

    /**
     * 重新启用模板（DEPRECATED → PUBLISHED）。
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('project:template:publish')")
    public Result<ProjectTemplate> enable(@PathVariable Long id) {
        return Result.ok(templateService.enable(id));
    }

    /**
     * 复制模板（深拷贝源模板快照到新模板，新模板状态为 DRAFT）。
     */
    @PostMapping("/{id}/copy")
    @PreAuthorize("hasAuthority('project:template:add')")
    public Result<ProjectTemplate> copyTemplate(
            @PathVariable Long id,
            @RequestBody CopyTemplateRequest request) {
        return Result.ok(templateService.copyTemplate(id, request.getTemplateCode(), request.getTemplateName()));
    }

    /** 发布版本请求体 */
    @lombok.Data
    public static class PublishVersionRequest {
        private String version;
        private TemplateSnapshot snapshot;
        private String changeLog;
    }

    /** 复制模板请求体 */
    @lombok.Data
    public static class CopyTemplateRequest {
        private String templateCode;
        private String templateName;
    }
}
