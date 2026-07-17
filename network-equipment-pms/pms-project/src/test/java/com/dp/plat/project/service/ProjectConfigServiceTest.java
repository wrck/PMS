package com.dp.plat.project.service;

import com.dp.plat.project.dao.ProjectConfigMapper;
import com.dp.plat.project.entity.ProjectConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectConfigServiceTest {

    @Mock
    private ProjectConfigMapper configMapper;

    @InjectMocks
    private ProjectConfigService configService;

    @BeforeEach
    void setUp() {
        // 系统默认配置（project_id IS NULL AND template_id IS NULL）
        ProjectConfig sysDefault = new ProjectConfig();
        sysDefault.setProjectId(null);
        sysDefault.setTemplateId(null);
        sysDefault.setConfigKey("approval.timeout.hours");
        sysDefault.setConfigValue("48");

        // 模板级配置
        ProjectConfig templateLevel = new ProjectConfig();
        templateLevel.setProjectId(null);
        templateLevel.setTemplateId(1L);
        templateLevel.setConfigKey("approval.timeout.hours");
        templateLevel.setConfigValue("72");

        // 项目级配置
        ProjectConfig projectLevel = new ProjectConfig();
        projectLevel.setProjectId(1001L);
        projectLevel.setTemplateId(1L);
        projectLevel.setConfigKey("approval.timeout.hours");
        projectLevel.setConfigValue("96");

        when(configMapper.selectList(any())).thenAnswer(invocation -> {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectConfig> wrapper =
                invocation.getArgument(0);
            // 简化模拟：根据 wrapper 的 SQL 返回不同子集
            // 真实场景由 SQL 过滤，这里直接返回全部让 Service 内部过滤
            return Arrays.asList(sysDefault, templateLevel, projectLevel);
        });
    }

    @Test
    void get_projectLevelOverridesSystemDefault() {
        // 项目级 > 系统默认
        String value = configService.get(1001L, 1L, "approval.timeout.hours");
        assertEquals("96", value, "项目级配置应覆盖系统默认");
    }

    @Test
    void get_templateLevelOverridesSystemDefault() {
        // 模板级 > 系统默认（无项目级）
        String value = configService.get(null, 1L, "approval.timeout.hours");
        assertEquals("72", value, "模板级配置应覆盖系统默认");
    }

    @Test
    void get_systemDefaultWhenNoOverride() {
        // 系统默认（无项目级、无模板级）
        String value = configService.get(null, null, "approval.timeout.hours");
        assertEquals("48", value, "无覆盖时应返回系统默认");
    }

    @Test
    void getInt_returnsParsedInteger() {
        int value = configService.getInt(1001L, 1L, "approval.timeout.hours");
        assertEquals(96, value);
    }

    @Test
    void get_returnsNullWhenKeyNotFound() {
        String value = configService.get(1001L, 1L, "nonexistent.key");
        assertEquals(null, value);
    }
}
