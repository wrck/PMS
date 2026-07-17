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
import com.dp.plat.project.entity.ProjectPhase;
import com.dp.plat.project.entity.ProjectMember;
import com.dp.plat.project.entity.ProjectConfig;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 项目模板服务实现
 */
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements IProjectTemplateService {

    private final ProjectTemplateMapper templateMapper;
    private final ProjectTemplateVersionMapper versionMapper;
    private final com.dp.plat.project.mapper.ProjectMapper projectMapper;
    private final com.dp.plat.project.dao.ProjectPhaseMapper phaseMapper;
    private final com.dp.plat.project.dao.ProjectMemberMapper memberMapper;
    private final com.dp.plat.project.dao.ProjectConfigMapper configMapper;

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
        // 1. 校验模板版本存在且已发布
        ProjectTemplateVersion version = versionMapper.selectById(dto.getVersionId());
        if (version == null) {
            throw new IllegalArgumentException("模板版本不存在: " + dto.getVersionId());
        }
        if (!"PUBLISHED".equals(version.getStatus())) {
            throw new IllegalStateException("仅可从 PUBLISHED 状态版本创建项目，当前状态: " + version.getStatus());
        }
        TemplateSnapshot snapshot = version.getSnapshotJson();
        if (snapshot == null) {
            throw new IllegalStateException("模板版本快照为空");
        }

        // 校验模板存在
        ProjectTemplate template = templateMapper.selectById(dto.getTemplateId());
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + dto.getTemplateId());
        }

        // 2. 创建项目（顶层项目）
        Project project = new Project();
        project.setProjectCode(dto.getProjectCode());
        project.setProjectName(dto.getProjectName());
        project.setCustomerName(dto.getCustomerName());
        project.setCustomerContact(dto.getCustomerContact());
        project.setCustomerPhone(dto.getCustomerPhone());
        project.setContractNo(dto.getContractNo());
        project.setContractAmount(dto.getContractAmount());
        project.setPlanStartDate(dto.getPlanStartDate());
        project.setPlanEndDate(dto.getPlanEndDate());
        project.setProjectManagerId(dto.getProjectManagerId());
        project.setStatus("PLANNING");
        project.setTemplateId(dto.getTemplateId());
        project.setTemplateVersion(version.getVersion());
        project.setProjectObjective(dto.getProjectObjective());
        project.setProjectScope(dto.getProjectScope());
        project.setParentProjectId(null);
        project.setDepth(0);
        project.setWeight(new BigDecimal("1.00"));
        projectMapper.insert(project);

        // 3. 设置物化路径（依赖自增主键）
        project.setProjectPath("/" + project.getId() + "/");
        projectMapper.updateById(project);

        // 4. 深拷贝阶段
        if (snapshot.getPhases() != null) {
            for (TemplateSnapshot.PhaseDef phaseDef : snapshot.getPhases()) {
                ProjectPhase phase = new ProjectPhase();
                phase.setProjectId(project.getId());
                phase.setTemplatePhaseId(null);
                phase.setPhaseName(phaseDef.getPhaseName());
                phase.setPhaseCode(phaseDef.getPhaseCode());
                phase.setSortOrder(phaseDef.getSortOrder());
                phase.setEntryCriteria(phaseDef.getEntryCriteria());
                phase.setExitCriteria(phaseDef.getExitCriteria());
                phase.setStatus("NOT_STARTED");
                phaseMapper.insert(phase);
            }
        }

        // 5. 初始化成员
        if (dto.getMembers() != null) {
            for (ProjectCreateFromTemplateDTO.MemberDef memberDef : dto.getMembers()) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getId());
                member.setUserId(memberDef.getUserId());
                member.setRole(memberDef.getRole());
                memberMapper.insert(member);
            }
        }

        // 6. 应用配置覆盖
        if (dto.getConfigOverrides() != null) {
            for (Map.Entry<String, String> entry : dto.getConfigOverrides().entrySet()) {
                ProjectConfig config = new ProjectConfig();
                config.setProjectId(project.getId());
                config.setTemplateId(dto.getTemplateId());
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue());
                configMapper.insert(config);
            }
        }

        // 7. 设置当前阶段为第一个阶段（若有）
        if (snapshot.getPhases() != null && !snapshot.getPhases().isEmpty()) {
            ProjectPhase firstPhase = phaseMapper.selectOne(
                new LambdaQueryWrapper<ProjectPhase>()
                    .eq(ProjectPhase::getProjectId, project.getId())
                    .orderByAsc(ProjectPhase::getSortOrder)
                    .last("LIMIT 1"));
            if (firstPhase != null) {
                project.setCurrentPhaseId(firstPhase.getId());
                projectMapper.updateById(project);
            }
        }

        // 注：任务/里程碑/交付件/依赖的深拷贝在 Phase 2-6 实现计划中实现
        return project;
    }
}
