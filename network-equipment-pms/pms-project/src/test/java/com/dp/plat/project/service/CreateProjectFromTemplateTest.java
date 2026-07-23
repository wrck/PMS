package com.dp.plat.project.service;

import com.dp.plat.project.dao.*;
import com.dp.plat.project.mapper.ProjectMemberMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.dto.ProjectCreateFromTemplateDTO;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.dp.plat.project.entity.*;
import com.dp.plat.project.service.impl.ProjectTemplateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectFromTemplateTest {

    @Mock private ProjectTemplateMapper templateMapper;
    @Mock private ProjectTemplateVersionMapper versionMapper;
    @Mock private ProjectMapper projectMapper;
    @Mock private ProjectPhaseMapper phaseMapper;
    @Mock private ProjectMemberMapper memberMapper;
    @Mock private ProjectConfigMapper configMapper;

    @InjectMocks
    private ProjectTemplateServiceImpl templateService;

    @Test
    void createProjectFromTemplate_deepCopiesPhases() {
        // 准备：模板版本含 2 个阶段
        TemplateSnapshot snapshot = new TemplateSnapshot();
        TemplateSnapshot.PhaseDef phase1 = new TemplateSnapshot.PhaseDef();
        phase1.setPhaseCode("PREPARE");
        phase1.setPhaseName("准备阶段");
        phase1.setSortOrder(1);
        TemplateSnapshot.PhaseDef phase2 = new TemplateSnapshot.PhaseDef();
        phase2.setPhaseCode("PLAN");
        phase2.setPhaseName("规划阶段");
        phase2.setSortOrder(2);
        snapshot.setPhases(Arrays.asList(phase1, phase2));

        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template);
        when(projectMapper.insert(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-001");
        dto.setProjectName("测试项目");
        dto.setPlanStartDate(LocalDate.of(2026, 7, 1));
        dto.setPlanEndDate(LocalDate.of(2026, 12, 31));
        dto.setProjectManagerId(100L);

        // 执行
        Project result = templateService.createProjectFromTemplate(dto);

        // 验证：项目创建
        assertNotNull(result.getId());
        assertEquals("PLANNING", result.getStatus(), "新建项目状态应为 PLANNING");
        assertEquals("/1001/", result.getProjectPath(), "顶层项目路径应为 /<id>/");
        assertEquals(0, result.getDepth());
        assertEquals(1L, result.getTemplateId());
        assertEquals("v1.0.0", result.getTemplateVersion());

        // 验证：2 个阶段被深拷贝
        verify(phaseMapper, times(2)).insert(any(ProjectPhase.class));
    }

    @Test
    void createProjectFromTemplate_initializesMembers() {
        TemplateSnapshot snapshot = new TemplateSnapshot();
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template());
        when(projectMapper.insert(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-002");
        dto.setProjectName("测试项目2");
        dto.setProjectManagerId(100L);

        ProjectCreateFromTemplateDTO.MemberDef m1 = new ProjectCreateFromTemplateDTO.MemberDef();
        m1.setUserId(100L);
        m1.setRole("PROJECT_MANAGER");
        ProjectCreateFromTemplateDTO.MemberDef m2 = new ProjectCreateFromTemplateDTO.MemberDef();
        m2.setUserId(101L);
        m2.setRole("PROJECT_MEMBER");
        dto.setMembers(Arrays.asList(m1, m2));

        templateService.createProjectFromTemplate(dto);

        // 验证：2 个成员被创建
        verify(memberMapper, times(2)).insert(any(ProjectMember.class));
    }

    @Test
    void createProjectFromTemplate_appliesConfigOverrides() {
        TemplateSnapshot snapshot = new TemplateSnapshot();
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setId(10L);
        version.setTemplateId(1L);
        version.setVersion("v1.0.0");
        version.setSnapshotJson(snapshot);
        version.setStatus("PUBLISHED");

        when(versionMapper.selectById(10L)).thenReturn(version);
        when(templateMapper.selectById(1L)).thenReturn(template());
        when(projectMapper.insert(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1001L);
            return 1;
        });

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);
        dto.setProjectCode("IMPL-2026-003");
        dto.setProjectName("测试项目3");
        dto.setProjectManagerId(100L);
        dto.setConfigOverrides(Collections.singletonMap("approval.timeout.hours", "72"));

        templateService.createProjectFromTemplate(dto);

        // 验证：项目级配置被写入
        verify(configMapper, times(1)).insert(any(ProjectConfig.class));
    }

    @Test
    void createProjectFromTemplate_throwsWhenVersionNotPublished() {
        ProjectTemplateVersion version = new ProjectTemplateVersion();
        version.setStatus("DRAFT");
        when(versionMapper.selectById(10L)).thenReturn(version);

        ProjectCreateFromTemplateDTO dto = new ProjectCreateFromTemplateDTO();
        dto.setTemplateId(1L);
        dto.setVersionId(10L);

        assertThrows(IllegalStateException.class, () -> {
            templateService.createProjectFromTemplate(dto);
        });
    }

    private ProjectTemplate template() {
        ProjectTemplate t = new ProjectTemplate();
        t.setId(1L);
        t.setStatus("PUBLISHED");
        return t;
    }
}
