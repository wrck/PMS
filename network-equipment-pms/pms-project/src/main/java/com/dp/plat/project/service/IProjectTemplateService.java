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
     * 更新模板（仅 DRAFT 状态可更新）
     */
    ProjectTemplate update(ProjectTemplate template);

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
}
