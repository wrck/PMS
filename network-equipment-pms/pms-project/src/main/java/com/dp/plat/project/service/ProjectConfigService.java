package com.dp.plat.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.dao.ProjectConfigMapper;
import com.dp.plat.project.entity.ProjectConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目配置服务 — 多层级读取（项目级 > 模板级 > 系统默认）
 * 关联设计文档：§4.3 ProjectConfigService
 *
 * <p>读取顺序：
 * <ol>
 *   <li>项目级覆盖（project_id = ? AND template_id IS NOT NULL OR project_id = ? AND template_id IS NULL）</li>
 *   <li>模板级默认（project_id IS NULL AND template_id = ?）</li>
 *   <li>系统默认（project_id IS NULL AND template_id IS NULL）</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ProjectConfigService {

    private final ProjectConfigMapper configMapper;

    /**
     * 读取配置值（字符串）
     *
     * @param projectId  项目ID（NULL 表示无项目级）
     * @param templateId 模板ID（NULL 表示无模板级）
     * @param key        配置键
     * @return 配置值，找不到返回 NULL
     */
    public String get(Long projectId, Long templateId, String key) {
        // 1. 查询所有候选配置（一次性查询，内存中按优先级筛选）
        LambdaQueryWrapper<ProjectConfig> wrapper = new LambdaQueryWrapper<ProjectConfig>()
            .eq(ProjectConfig::getConfigKey, key)
            .and(w -> w
                .and(w1 -> w1.isNull(ProjectConfig::getProjectId).isNull(ProjectConfig::getTemplateId)) // 系统默认
                .or(w2 -> w2.isNull(ProjectConfig::getProjectId).eq(ProjectConfig::getTemplateId, templateId)) // 模板级
                .or(w3 -> w3.eq(ProjectConfig::getProjectId, projectId)) // 项目级
            );
        List<ProjectConfig> configs = configMapper.selectList(wrapper);

        // 2. 按优先级筛选：项目级 > 模板级 > 系统默认
        String projectLevel = null;
        String templateLevel = null;
        String systemDefault = null;

        for (ProjectConfig config : configs) {
            if (projectId != null && projectId.equals(config.getProjectId())) {
                projectLevel = config.getConfigValue();
            } else if (templateId != null && templateId.equals(config.getTemplateId())
                       && config.getProjectId() == null) {
                templateLevel = config.getConfigValue();
            } else if (config.getProjectId() == null && config.getTemplateId() == null) {
                systemDefault = config.getConfigValue();
            }
        }

        if (projectLevel != null) return projectLevel;
        if (templateLevel != null) return templateLevel;
        return systemDefault;
    }

    /**
     * 读取配置值并转为 int
     */
    public int getInt(Long projectId, Long templateId, String key) {
        String value = get(projectId, templateId, key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * 读取配置值并转为 boolean
     */
    public boolean getBoolean(Long projectId, Long templateId, String key) {
        String value = get(projectId, templateId, key);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * 批量读取项目所有配置（用于前端配置页展示）
     */
    public Map<String, String> getAllForProject(Long projectId, Long templateId) {
        LambdaQueryWrapper<ProjectConfig> wrapper = new LambdaQueryWrapper<ProjectConfig>()
            .and(w -> w
                .and(w1 -> w1.isNull(ProjectConfig::getProjectId).isNull(ProjectConfig::getTemplateId))
                .or(w2 -> w2.isNull(ProjectConfig::getProjectId).eq(ProjectConfig::getTemplateId, templateId))
                .or(w3 -> w3.eq(ProjectConfig::getProjectId, projectId))
            );
        List<ProjectConfig> configs = configMapper.selectList(wrapper);

        Map<String, String> result = new HashMap<>();
        // 反向填充：先系统默认，再模板级覆盖，最后项目级覆盖
        for (ProjectConfig config : configs) {
            if (config.getProjectId() == null && config.getTemplateId() == null) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        for (ProjectConfig config : configs) {
            if (config.getProjectId() == null && templateId != null
                && templateId.equals(config.getTemplateId())) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        for (ProjectConfig config : configs) {
            if (projectId != null && projectId.equals(config.getProjectId())) {
                result.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return result;
    }
}
