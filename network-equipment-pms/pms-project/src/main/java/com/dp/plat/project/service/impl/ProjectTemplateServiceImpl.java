package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.project.dao.ProjectTemplateMapper;
import com.dp.plat.project.dao.ProjectTemplateVersionMapper;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 项目模板服务实现
 */
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements IProjectTemplateService {

    private final ProjectTemplateMapper templateMapper;
    private final ProjectTemplateVersionMapper versionMapper;

    @Override
    public Page<ProjectTemplate> page(int page, int size, String templateName, String category, String status) {
        Page<ProjectTemplate> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProjectTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(templateName)) {
            wrapper.like(ProjectTemplate::getTemplateName, templateName);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(ProjectTemplate::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectTemplate::getStatus, status);
        }
        wrapper.orderByDesc(ProjectTemplate::getCreateTime);
        return templateMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public ProjectTemplate getById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    @Transactional
    public ProjectTemplate create(ProjectTemplate template) {
        if (template.getStatus() == null) {
            template.setStatus("DRAFT");
        }
        templateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional
    public ProjectTemplate update(ProjectTemplate template) {
        ProjectTemplate existing = templateMapper.selectById(template.getId());
        if (existing == null) {
            throw new IllegalArgumentException("模板不存在: " + template.getId());
        }
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new IllegalStateException("仅 DRAFT 状态模板可编辑，当前状态: " + existing.getStatus());
        }
        templateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProjectTemplate existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new IllegalStateException("仅 DRAFT 状态模板可删除，当前状态: " + existing.getStatus());
        }
        templateMapper.deleteById(id);
    }

    @Override
    public Page<ProjectTemplateVersion> listVersions(Long templateId, int page, int size) {
        Page<ProjectTemplateVersion> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ProjectTemplateVersion> wrapper = new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .orderByDesc(ProjectTemplateVersion::getCreateTime);
        return versionMapper.selectPage(pageObj, wrapper);
    }

    @Override
    @Transactional
    public ProjectTemplateVersion publishVersion(Long templateId, String version, TemplateSnapshot snapshot, String changeLog) {
        // 1. 校验模板存在且未 DEPRECATED
        ProjectTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + templateId);
        }
        if ("DEPRECATED".equals(template.getStatus())) {
            throw new IllegalStateException("DEPRECATED 状态模板不可发布新版本");
        }

        // 2. 校验版本号唯一
        Long existingCount = versionMapper.selectCount(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getVersion, version));
        if (existingCount > 0) {
            throw new IllegalStateException("版本号已存在: " + version);
        }

        // 3. 创建版本记录
        ProjectTemplateVersion versionRecord = new ProjectTemplateVersion();
        versionRecord.setTemplateId(templateId);
        versionRecord.setVersion(version);
        versionRecord.setSnapshotJson(snapshot);
        versionRecord.setChangeLog(changeLog);
        versionRecord.setStatus("PUBLISHED");
        versionRecord.setPublishedAt(LocalDateTime.now());
        // publishedBy 由 Controller 层注入（从 SecurityContext）
        versionMapper.insert(versionRecord);

        // 4. 更新模板状态为 PUBLISHED
        template.setStatus("PUBLISHED");
        templateMapper.updateById(template);

        return versionRecord;
    }

    @Override
    public ProjectTemplateVersion getPublishedVersion(Long templateId) {
        return versionMapper.selectOne(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "PUBLISHED")
            .orderByDesc(ProjectTemplateVersion::getPublishedAt)
            .last("LIMIT 1"));
    }

    @Override
    @Transactional
    public Project createProjectFromTemplate(ProjectCreateFromTemplateDTO dto) {
        // Task 13 实现：深拷贝模板内容到项目相关表
        throw new UnsupportedOperationException("createProjectFromTemplate 在 Task 13 实现");
    }
}
