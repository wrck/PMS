package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;

/**
 * 项目模板服务接口
 * 关联设计文档：§4.3 Story 1
 */
public interface IProjectTemplateService {

    /**
     * 分页查询模板
     */
    Page<ProjectTemplate> page(int page, int size, String templateName, String category, String status);

    /**
     * 查询模板详情
     */
    ProjectTemplate getById(Long id);

    /**
     * 创建模板（默认状态 DRAFT）
     */
    ProjectTemplate create(ProjectTemplate template);

    /**
     * 更新模板基本信息（DRAFT / PUBLISHED 状态可更新，DEPRECATED 不可更新）
     */
    ProjectTemplate update(ProjectTemplate template);

    /**
     * 保存草稿快照（创建或更新 DRAFT 状态的版本记录，不影响模板发布状态）。
     * 用于新建/编辑模板时持久化阶段/任务/交付件等详细配置。
     */
    ProjectTemplateVersion saveDraftSnapshot(Long templateId, TemplateSnapshot snapshot);

    /**
     * 获取模板的草稿版本（最新 DRAFT 状态版本，无则返回 null）
     */
    ProjectTemplateVersion getDraftVersion(Long templateId);

    /**
     * 删除模板（仅 DRAFT 状态可删除）
     */
    void delete(Long id);

    /**
     * 查询模板的所有版本
     */
    Page<ProjectTemplateVersion> listVersions(Long templateId, int page, int size);

    /**
     * 发布新版本（深拷贝模板内容到 snapshot_json）
     */
    ProjectTemplateVersion publishVersion(Long templateId, String version, TemplateSnapshot snapshot, String changeLog);

    /**
     * 获取模板已发布版本（取最新 PUBLISHED 状态版本）
     */
    ProjectTemplateVersion getPublishedVersion(Long templateId);

    /**
     * 从模板创建项目（深拷贝 snapshot 到项目相关表）
     */
    Project createProjectFromTemplate(ProjectCreateFromTemplateDTO dto);

    /**
     * 废弃模板（PUBLISHED → DEPRECATED，不可再用于创建项目）。
     * 仅 PUBLISHED 状态可废弃。
     */
    ProjectTemplate deprecate(Long id);

    /**
     * 重新启用模板（DEPRECATED → PUBLISHED，恢复使用）。
     * 仅 DEPRECATED 状态可启用，恢复后沿用最近一次 PUBLISHED 版本。
     */
    ProjectTemplate enable(Long id);

    /**
     * 复制模板（深拷贝源模板的最新快照到新模板，新模板状态为 DRAFT）。
     *
     * @param sourceId 源模板 ID
     * @param newTemplateCode 新模板编码（须唯一）
     * @param newTemplateName 新模板名称
     * @return 新建的模板（含草稿快照）
     */
    ProjectTemplate copyTemplate(Long sourceId, String newTemplateCode, String newTemplateName);
}
