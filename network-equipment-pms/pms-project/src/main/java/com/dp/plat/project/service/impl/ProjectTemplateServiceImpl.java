package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.spi.ApprovalPlanBatchCreator;
import com.dp.plat.common.spi.DeliverableBatchCreator;
import com.dp.plat.common.spi.DependencyBatchCreator;
import com.dp.plat.common.spi.TaskBatchCreator;
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
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.service.IProjectTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements IProjectTemplateService {

    private final ProjectTemplateMapper templateMapper;
    private final ProjectTemplateVersionMapper versionMapper;
    private final com.dp.plat.project.mapper.ProjectMapper projectMapper;
    private final com.dp.plat.project.dao.ProjectPhaseMapper phaseMapper;
    private final com.dp.plat.project.mapper.ProjectMemberMapper memberMapper;
    private final com.dp.plat.project.dao.ProjectConfigMapper configMapper;
    private final MilestoneMapper milestoneMapper;

    // TD-P8-003：跨模块深拷贝 SPI（可选注入，模块未加载时跳过对应深拷贝并 log.warn）
    @Autowired(required = false)
    private TaskBatchCreator taskBatchCreator;

    @Autowired(required = false)
    private DeliverableBatchCreator deliverableBatchCreator;

    @Autowired(required = false)
    private DependencyBatchCreator dependencyBatchCreator;

    @Autowired(required = false)
    private ApprovalPlanBatchCreator approvalPlanBatchCreator;

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
        // 校验模板编码唯一
        if (StringUtils.hasText(template.getTemplateCode())) {
            Long count = templateMapper.selectCount(new LambdaQueryWrapper<ProjectTemplate>()
                .eq(ProjectTemplate::getTemplateCode, template.getTemplateCode()));
            if (count > 0) {
                throw new IllegalStateException("模板编码已存在: " + template.getTemplateCode());
            }
        }
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
        if ("DEPRECATED".equals(existing.getStatus())) {
            throw new IllegalStateException("DEPRECATED 状态模板不可编辑，当前状态: " + existing.getStatus());
        }
        // 仅更新可编辑字段，保留 id/templateCode/status 不被请求体覆盖
        existing.setTemplateName(template.getTemplateName());
        existing.setCategory(template.getCategory());
        existing.setDescription(template.getDescription());
        templateMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public ProjectTemplateVersion saveDraftSnapshot(Long templateId, TemplateSnapshot snapshot) {
        ProjectTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + templateId);
        }
        if ("DEPRECATED".equals(template.getStatus())) {
            throw new IllegalStateException("DEPRECATED 状态模板不可保存草稿，当前状态: " + template.getStatus());
        }
        // 查找现有 DRAFT 版本（每个模板至多一个草稿版本）
        ProjectTemplateVersion draftVersion = versionMapper.selectOne(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "DRAFT")
            .orderByDesc(ProjectTemplateVersion::getCreateTime)
            .last("LIMIT 1"));
        if (draftVersion == null) {
            draftVersion = new ProjectTemplateVersion();
            draftVersion.setTemplateId(templateId);
            draftVersion.setVersion(generateDraftVersionNumber(templateId));
            draftVersion.setSnapshotJson(snapshot);
            draftVersion.setChangeLog("草稿编辑");
            draftVersion.setStatus("DRAFT");
            versionMapper.insert(draftVersion);
        } else {
            draftVersion.setSnapshotJson(snapshot);
            versionMapper.updateById(draftVersion);
        }
        return draftVersion;
    }

    @Override
    public ProjectTemplateVersion getDraftVersion(Long templateId) {
        return versionMapper.selectOne(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "DRAFT")
            .orderByDesc(ProjectTemplateVersion::getCreateTime)
            .last("LIMIT 1"));
    }

    /**
     * 生成草稿版本号：基于已发布版本递增，无已发布版本则使用 v0.1.0-draft。
     */
    private String generateDraftVersionNumber(Long templateId) {
        ProjectTemplateVersion latestPublished = getPublishedVersion(templateId);
        if (latestPublished != null && latestPublished.getVersion() != null) {
            return latestPublished.getVersion() + "-draft";
        }
        return "v0.1.0-draft";
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

        // 3. 归档既有 PUBLISHED 版本为 ARCHIVED（同一模板仅保留一个 PUBLISHED 版本）
        List<ProjectTemplateVersion> previousPublished = versionMapper.selectList(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "PUBLISHED"));
        for (ProjectTemplateVersion prev : previousPublished) {
            prev.setStatus("ARCHIVED");
            versionMapper.updateById(prev);
        }

        // 4. 创建新版本记录
        ProjectTemplateVersion versionRecord = new ProjectTemplateVersion();
        versionRecord.setTemplateId(templateId);
        versionRecord.setVersion(version);
        versionRecord.setSnapshotJson(snapshot);
        versionRecord.setChangeLog(changeLog);
        versionRecord.setStatus("PUBLISHED");
        versionRecord.setPublishedAt(LocalDateTime.now());
        versionRecord.setPublishedBy(com.dp.plat.common.util.SecurityUtils.getCurrentUserId());
        versionMapper.insert(versionRecord);

        // 5. 清理草稿版本（发布后草稿已并入正式版本）
        List<ProjectTemplateVersion> draftVersions = versionMapper.selectList(new LambdaQueryWrapper<ProjectTemplateVersion>()
            .eq(ProjectTemplateVersion::getTemplateId, templateId)
            .eq(ProjectTemplateVersion::getStatus, "DRAFT"));
        for (ProjectTemplateVersion draft : draftVersions) {
            versionMapper.deleteById(draft.getId());
        }

        // 6. 更新模板状态为 PUBLISHED
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
        project.setProjectType(StringUtils.hasText(dto.getProjectType()) ? dto.getProjectType() : "NETWORK_DEVICE");
        project.setCustomerName(dto.getCustomerName());
        project.setCustomerContact(dto.getCustomerContact());
        project.setCustomerPhone(dto.getCustomerPhone());
        project.setContractNo(dto.getContractNo());
        project.setContractAmount(dto.getContractAmount());
        project.setPlanStartDate(dto.getPlanStartDate());
        project.setPlanEndDate(dto.getPlanEndDate());
        project.setProjectManagerId(dto.getProjectManagerId());
        project.setProjectManagerName(dto.getProjectManagerName());
        project.setStatus("PLANNING");
        project.setPriority(StringUtils.hasText(dto.getPriority()) ? dto.getPriority() : "NORMAL");
        project.setProgress(0);
        project.setDescription(dto.getDescription());
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

        // 4. 深拷贝阶段，并构建 phaseCode → phaseId 映射（供后续 tasks/deliverables/approvalPlans 解析）
        Map<String, Long> phaseCodeToIdMap = new LinkedHashMap<>();
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
                if (phaseDef.getPhaseCode() != null) {
                    phaseCodeToIdMap.put(phaseDef.getPhaseCode(), phase.getId());
                }
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

        // 8. 深拷贝里程碑（同模块，直接调用 MilestoneMapper）
        deepCopyMilestones(project.getId(), snapshot.getMilestones(), phaseCodeToIdMap);

        // 9. 深拷贝任务（跨模块 SPI：pms-implementation）
        deepCopyTasks(project.getId(), snapshot.getTasks(), phaseCodeToIdMap);

        // 10. 深拷贝交付件（跨模块 SPI：pms-deliverable）
        deepCopyDeliverables(project.getId(), snapshot.getDeliverables(), phaseCodeToIdMap);

        // 11. 深拷贝任务依赖（跨模块 SPI：pms-baseline）
        deepCopyDependencies(project.getId(), snapshot.getDependencies());

        // 12. 深拷贝审批计划（跨模块 SPI：pms-workflow）
        deepCopyApprovalPlans(project.getId(), snapshot.getApprovalPlans(), phaseCodeToIdMap);

        return project;
    }

    /**
     * 深拷贝里程碑（同模块，直接调用 MilestoneMapper）。
     * 关联设计文档：§2.2 Milestone、§5.2 模板深拷贝。
     *
     * <p>{@code ppdiooPhase} 通过 {@link com.dp.plat.project.enums.MilestoneType} 枚举推导
     * （MilestoneType → PpdiooPhase 一一对应），无需在模板中显式定义。</p>
     */
    private void deepCopyMilestones(Long projectId, List<TemplateSnapshot.MilestoneDef> milestoneDefs,
                                    Map<String, Long> phaseCodeToIdMap) {
        if (milestoneDefs == null || milestoneDefs.isEmpty()) {
            return;
        }
        int created = 0;
        for (TemplateSnapshot.MilestoneDef def : milestoneDefs) {
            com.dp.plat.project.enums.MilestoneType type =
                    com.dp.plat.project.enums.MilestoneType.fromName(def.getMilestoneType());
            String ppdiooPhase = type != null ? type.getPpdiooPhase().name() : null;
            Integer sortOrder = def.getSortOrder() != null
                    ? def.getSortOrder()
                    : (type != null ? type.getSortOrder() : 0);
            Milestone milestone = Milestone.builder()
                    .projectId(projectId)
                    .milestoneName(def.getMilestoneName())
                    .milestoneType(def.getMilestoneType())
                    .ppdiooPhase(ppdiooPhase)
                    .status("PENDING")
                    .sortOrder(sortOrder)
                    .build();
            milestoneMapper.insert(milestone);
            created++;
        }
        log.info("模板深拷贝：里程碑创建成功 projectId={} count={}", projectId, created);
    }

    /**
     * 深拷贝任务（跨模块 SPI：pms-implementation）。
     * 按 phaseCode 分组调用 {@link TaskBatchCreator}。
     */
    private void deepCopyTasks(Long projectId, List<TemplateSnapshot.TaskDef> taskDefs,
                               Map<String, Long> phaseCodeToIdMap) {
        if (taskDefs == null || taskDefs.isEmpty()) {
            return;
        }
        if (taskBatchCreator == null) {
            log.warn("模板深拷贝：TaskBatchCreator SPI 未加载，跳过任务深拷贝（projectId={} count={}）",
                    projectId, taskDefs.size());
            return;
        }
        // 按 phaseCode 分组
        Map<String, List<TemplateSnapshot.TaskDef>> grouped = new LinkedHashMap<>();
        for (TemplateSnapshot.TaskDef def : taskDefs) {
            String phaseCode = def.getPhaseCode() != null ? def.getPhaseCode() : "";
            grouped.computeIfAbsent(phaseCode, k -> new ArrayList<>()).add(def);
        }
        int totalCreated = 0;
        for (Map.Entry<String, List<TemplateSnapshot.TaskDef>> entry : grouped.entrySet()) {
            Long phaseId = phaseCodeToIdMap.get(entry.getKey());
            if (phaseId == null) {
                log.warn("模板深拷贝：跳过 phaseCode={} 的任务组（阶段未找到，projectId={} count={}）",
                        entry.getKey(), projectId, entry.getValue().size());
                continue;
            }
            taskBatchCreator.batchCreateTasks(projectId, phaseId, entry.getValue());
            totalCreated += entry.getValue().size();
        }
        log.info("模板深拷贝：任务深拷贝完成 projectId={} totalCreated={}", projectId, totalCreated);
    }

    /**
     * 深拷贝交付件（跨模块 SPI：pms-deliverable）。
     * 按 phaseCode 分组调用 {@link DeliverableBatchCreator}。
     */
    private void deepCopyDeliverables(Long projectId, List<TemplateSnapshot.DeliverableDef> deliverableDefs,
                                      Map<String, Long> phaseCodeToIdMap) {
        if (deliverableDefs == null || deliverableDefs.isEmpty()) {
            return;
        }
        if (deliverableBatchCreator == null) {
            log.warn("模板深拷贝：DeliverableBatchCreator SPI 未加载，跳过交付件深拷贝（projectId={} count={}）",
                    projectId, deliverableDefs.size());
            return;
        }
        // 按 phaseCode 分组
        Map<String, List<TemplateSnapshot.DeliverableDef>> grouped = new LinkedHashMap<>();
        for (TemplateSnapshot.DeliverableDef def : deliverableDefs) {
            String phaseCode = def.getPhaseCode() != null ? def.getPhaseCode() : "";
            grouped.computeIfAbsent(phaseCode, k -> new ArrayList<>()).add(def);
        }
        int totalCreated = 0;
        for (Map.Entry<String, List<TemplateSnapshot.DeliverableDef>> entry : grouped.entrySet()) {
            Long phaseId = phaseCodeToIdMap.get(entry.getKey());
            if (phaseId == null) {
                log.warn("模板深拷贝：跳过 phaseCode={} 的交付件组（阶段未找到，projectId={} count={}）",
                        entry.getKey(), projectId, entry.getValue().size());
                continue;
            }
            deliverableBatchCreator.batchCreateDeliverables(projectId, phaseId, entry.getValue());
            totalCreated += entry.getValue().size();
        }
        log.info("模板深拷贝：交付件深拷贝完成 projectId={} totalCreated={}", projectId, totalCreated);
    }

    /**
     * 深拷贝任务依赖（跨模块 SPI：pms-baseline）。
     * 调用 {@link DependencyBatchCreator}，由实现自行解析任务名称 → ID。
     */
    private void deepCopyDependencies(Long projectId, List<TemplateSnapshot.DependencyDef> dependencyDefs) {
        if (dependencyDefs == null || dependencyDefs.isEmpty()) {
            return;
        }
        if (dependencyBatchCreator == null) {
            log.warn("模板深拷贝：DependencyBatchCreator SPI 未加载，跳过任务依赖深拷贝（projectId={} count={}）",
                    projectId, dependencyDefs.size());
            return;
        }
        dependencyBatchCreator.batchCreateDependencies(projectId, dependencyDefs);
        log.info("模板深拷贝：任务依赖深拷贝完成 projectId={} count={}", projectId, dependencyDefs.size());
    }

    /**
     * 深拷贝审批计划（跨模块 SPI：pms-workflow）。
     * 调用 {@link ApprovalPlanBatchCreator} 注册审批计划（不立即创建审批记录）。
     */
    private void deepCopyApprovalPlans(Long projectId, List<TemplateSnapshot.ApprovalPlanDef> approvalPlanDefs,
                                       Map<String, Long> phaseCodeToIdMap) {
        if (approvalPlanDefs == null || approvalPlanDefs.isEmpty()) {
            return;
        }
        if (approvalPlanBatchCreator == null) {
            log.warn("模板深拷贝：ApprovalPlanBatchCreator SPI 未加载，跳过审批计划深拷贝（projectId={} count={}）",
                    projectId, approvalPlanDefs.size());
            return;
        }
        approvalPlanBatchCreator.batchCreateApprovalPlans(projectId, phaseCodeToIdMap, approvalPlanDefs);
        log.info("模板深拷贝：审批计划深拷贝完成 projectId={} count={}", projectId, approvalPlanDefs.size());
    }

    @Override
    @Transactional
    public ProjectTemplate deprecate(Long id) {
        ProjectTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }
        if (!"PUBLISHED".equals(template.getStatus())) {
            throw new IllegalStateException("仅 PUBLISHED 状态模板可废弃，当前状态: " + template.getStatus());
        }
        template.setStatus("DEPRECATED");
        templateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public ProjectTemplate enable(Long id) {
        ProjectTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }
        if (!"DEPRECATED".equals(template.getStatus())) {
            throw new IllegalStateException("仅 DEPRECATED 状态模板可重新启用，当前状态: " + template.getStatus());
        }
        template.setStatus("PUBLISHED");
        templateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public ProjectTemplate copyTemplate(Long sourceId, String newTemplateCode, String newTemplateName) {
        ProjectTemplate source = templateMapper.selectById(sourceId);
        if (source == null) {
            throw new IllegalArgumentException("源模板不存在: " + sourceId);
        }
        if (!StringUtils.hasText(newTemplateCode)) {
            throw new IllegalArgumentException("新模板编码不能为空");
        }
        // 校验新编码唯一
        Long count = templateMapper.selectCount(new LambdaQueryWrapper<ProjectTemplate>()
            .eq(ProjectTemplate::getTemplateCode, newTemplateCode));
        if (count > 0) {
            throw new IllegalStateException("模板编码已存在: " + newTemplateCode);
        }
        // 创建新模板
        ProjectTemplate newTemplate = new ProjectTemplate();
        newTemplate.setTemplateCode(newTemplateCode);
        newTemplate.setTemplateName(StringUtils.hasText(newTemplateName) ? newTemplateName : newTemplateCode);
        newTemplate.setCategory(source.getCategory());
        newTemplate.setDescription(source.getDescription());
        newTemplate.setStatus("DRAFT");
        templateMapper.insert(newTemplate);

        // 深拷贝源模板最新快照（优先草稿，其次已发布）到新模板的草稿版本
        TemplateSnapshot snapshot = null;
        ProjectTemplateVersion sourceDraft = getDraftVersion(sourceId);
        if (sourceDraft != null && sourceDraft.getSnapshotJson() != null) {
            snapshot = sourceDraft.getSnapshotJson();
        } else {
            ProjectTemplateVersion sourcePublished = getPublishedVersion(sourceId);
            if (sourcePublished != null) {
                snapshot = sourcePublished.getSnapshotJson();
            }
        }
        if (snapshot != null) {
            saveDraftSnapshot(newTemplate.getId(), snapshot);
        }
        log.info("模板复制成功 sourceId={} newTemplateId={} newCode={}", sourceId, newTemplate.getId(), newTemplateCode);
        return newTemplate;
    }
}
