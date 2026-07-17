package com.dp.plat.project.service;

import com.dp.plat.project.dao.ProjectTemplateMapper;
import com.dp.plat.project.dao.ProjectTemplateVersionMapper;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.dp.plat.project.entity.ProjectTemplate;
import com.dp.plat.project.entity.ProjectTemplateVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectTemplateServiceImplTest {

    @Mock
    private ProjectTemplateMapper templateMapper;

    @Mock
    private ProjectTemplateVersionMapper versionMapper;

    @InjectMocks
    private com.dp.plat.project.service.impl.ProjectTemplateServiceImpl templateService;

    @Test
    void createTemplate_setsDefaultStatus() {
        ProjectTemplate template = new ProjectTemplate();
        template.setTemplateCode("TPL-001");
        template.setTemplateName("测试模板");

        when(templateMapper.insert(any())).thenReturn(1);

        ProjectTemplate result = templateService.create(template);

        assertEquals("DRAFT", result.getStatus(), "新模板默认状态应为 DRAFT");
        verify(templateMapper).insert(any());
    }

    @Test
    void publishVersion_setsStatusToPublished() {
        Long templateId = 1L;
        String version = "v1.0.0";
        TemplateSnapshot snapshot = new TemplateSnapshot();
        String changeLog = "初始版本";

        ProjectTemplate template = new ProjectTemplate();
        template.setId(templateId);
        template.setStatus("DRAFT");
        when(templateMapper.selectById(templateId)).thenReturn(template);
        when(versionMapper.insert(any())).thenAnswer(invocation -> {
            ProjectTemplateVersion v = invocation.getArgument(0);
            v.setId(100L);
            return 1;
        });

        ProjectTemplateVersion result = templateService.publishVersion(templateId, version, snapshot, changeLog);

        assertEquals("PUBLISHED", result.getStatus(), "发布后版本状态应为 PUBLISHED");
        assertNotNull(result.getPublishedAt(), "发布时间不应为空");
        assertEquals(version, result.getVersion());
        assertEquals(changeLog, result.getChangeLog());
    }

    @Test
    void publishVersion_throwsWhenTemplateNotFound() {
        when(templateMapper.selectById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            templateService.publishVersion(999L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }

    @Test
    void publishVersion_throwsWhenTemplateDeprecated() {
        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("DEPRECATED");
        when(templateMapper.selectById(1L)).thenReturn(template);

        assertThrows(IllegalStateException.class, () -> {
            templateService.publishVersion(1L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }

    @Test
    void publishVersion_throwsWhenVersionExists() {
        ProjectTemplate template = new ProjectTemplate();
        template.setId(1L);
        template.setStatus("PUBLISHED");
        when(templateMapper.selectById(1L)).thenReturn(template);

        when(versionMapper.selectCount(any())).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> {
            templateService.publishVersion(1L, "v1.0.0", new TemplateSnapshot(), "log");
        });
    }
}
