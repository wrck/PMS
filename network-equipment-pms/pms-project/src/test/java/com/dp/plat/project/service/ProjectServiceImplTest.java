package com.dp.plat.project.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.metrics.BusinessMetrics;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.impl.ProjectServiceImpl;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProjectServiceImpl}.
 *
 * <p>Mocks {@link ProjectMapper} (the {@code baseMapper} of {@link com.baomidou.mybatisplus.extension.service.impl.ServiceImpl})
 * so the service can be exercised without a database.</p>
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private BusinessMetrics businessMetrics;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        // ServiceImpl exposes baseMapper as a protected field populated via @Autowired field
        // injection. Mockito's @InjectMocks uses constructor injection first and does not
        // populate inherited fields afterwards, so set the baseMapper manually.
        ReflectionTestUtils.setField(projectService, "baseMapper", projectMapper);
    }

    private Project sampleProject(String name) {
        return Project.builder()
                .projectName(name)
                .projectType("NETWORK_DEVICE")
                .customerName("ACME")
                .contractAmount(new java.math.BigDecimal("100000"))
                .planStartDate(LocalDate.of(2024, 2, 1))
                .planEndDate(LocalDate.of(2024, 12, 31))
                .projectManagerId(1L)
                .projectManagerName("Alice")
                .build();
    }

    @Test
    @DisplayName("createProject: 保存项目并初始化 PENDING 状态，项目编号在审批时生成")
    void createProject_shouldSaveWithPendingStatus() {
        Project input = sampleProject("New Project");

        when(projectMapper.insert(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1L);
            return 1;
        });
        when(workflowService.startProcess(any())).thenReturn(Result.ok(new ProcessInstanceDTO()));

        Result result = projectService.createProject(input);

        assertTrue(result.isSuccess());
        Project saved = (Project) result.getData();
        assertNotNull(saved);
        assertEquals(STATUS_PENDING, saved.getStatus());
        assertEquals("NORMAL", saved.getPriority());
        assertEquals(0, saved.getProgress());
        assertNull(saved.getProjectCode(), "项目编号应在审批时生成，创建时为空");
        assertNull(saved.getId(), "id 在保存前应被置空（由数据库生成）");
        verify(projectMapper, times(1)).insert(any(Project.class));
    }

    @Test
    @DisplayName("createProject: 项目名为空时抛出业务异常")
    void createProject_emptyName_throws() {
        Project input = Project.builder().build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> projectService.createProject(input));
        assertTrue(ex.getMessage().contains("项目名称"));
        verify(projectMapper, never()).insert(any(Project.class));
    }

    @Test
    @DisplayName("createProject: 入参为 null 抛出业务异常")
    void createProject_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> projectService.createProject(null));
        assertNotNull(ex.getMessage());
        verify(projectMapper, never()).insert(any(Project.class));
    }

    @Test
    @DisplayName("approveProject: 将 PENDING 项目置为 APPROVED 并补全项目编号")
    void approveProject_shouldChangeStatusToApproved() {
        Project existing = sampleProject("Approve Me");
        existing.setId(10L);
        existing.setStatus(STATUS_PENDING);
        existing.setProjectCode(null);

        when(projectMapper.selectById(10L)).thenReturn(existing);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);
        when(projectMapper.selectCount(any(Wrapper.class))).thenReturn(5L);

        Result result = projectService.approveProject(10L);

        assertTrue(result.isSuccess());
        Project approved = (Project) result.getData();
        assertEquals(STATUS_APPROVED, approved.getStatus());
        assertNotNull(approved.getProjectCode(), "审批时缺号应自动补生成");
        assertTrue(approved.getProjectCode().startsWith("PMS-"));
        verify(projectMapper, times(1)).updateById(any(Project.class));
    }

    @Test
    @DisplayName("approveProject: 非 PENDING 状态不允许审批")
    void approveProject_wrongStatus_throws() {
        Project existing = sampleProject("Already Approved");
        existing.setId(11L);
        existing.setStatus(STATUS_APPROVED);

        when(projectMapper.selectById(11L)).thenReturn(existing);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> projectService.approveProject(11L));
        assertTrue(ex.getMessage().contains("状态"));
        verify(projectMapper, never()).updateById(any(Project.class));
    }

    @Test
    @DisplayName("approveProject: 项目不存在抛出业务异常")
    void approveProject_notFound_throws() {
        when(projectMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> projectService.approveProject(999L));
        verify(projectMapper, never()).updateById(any(Project.class));
    }

    @Test
    @DisplayName("generateProjectCode: 格式为 PMS-YYYY-XXXX")
    void generateProjectCode_shouldMatchFormat() {
        when(projectMapper.selectCount(any(Wrapper.class))).thenReturn(3L);

        String code = projectService.generateProjectCode();

        assertNotNull(code);
        int year = LocalDate.now().getYear();
        String expectedPrefix = "PMS-" + year + "-";
        assertTrue(code.startsWith(expectedPrefix), "编号应以 " + expectedPrefix + " 开头");
        // Last 4 chars should be the zero-padded sequence (count + 1 = 4)
        String sequence = code.substring(expectedPrefix.length());
        assertEquals(4, sequence.length(), "序号段应为 4 位");
        assertEquals("0004", sequence);
        assertEquals("PMS-" + year + "-0004", code);
    }

    @Test
    @DisplayName("generateProjectCode: 无历史项目时返回序号 0001")
    void generateProjectCode_firstProject() {
        when(projectMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        String code = projectService.generateProjectCode();
        int year = LocalDate.now().getYear();
        assertEquals("PMS-" + year + "-0001", code);
    }

    @Test
    @DisplayName("listProjects: 返回分页结果且包装为 Result")
    void listProjects_shouldReturnPage() {
        List<Project> records = new ArrayList<>();
        records.add(sampleProject("P1"));
        records.add(sampleProject("P2"));

        when(projectMapper.selectPage(any(IPage.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<Project> page = invocation.getArgument(0);
                    page.setRecords(records);
                    page.setTotal(records.size());
                    return page;
                });

        Result<Page<Project>> result = projectService.listProjects(1, 10, null, null);

        assertTrue(result.isSuccess());
        Page<Project> page = result.getData();
        assertNotNull(page);
        assertEquals(2, page.getRecords().size());
        assertEquals(2L, page.getTotal());
        verify(projectMapper, times(1)).selectPage(any(IPage.class), any(Wrapper.class));
    }

    @Test
    @DisplayName("listProjects: 传入非法分页参数时回退为默认值")
    void listProjects_invalidPaging_usesDefaults() {
        when(projectMapper.selectPage(any(IPage.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<Project> page = invocation.getArgument(0);
                    page.setRecords(new ArrayList<>());
                    page.setTotal(0);
                    return page;
                });

        Result<Page<Project>> result = projectService.listProjects(-1, -5, "P", STATUS_PENDING);

        assertTrue(result.isSuccess());
        Page<Project> page = result.getData();
        assertEquals(1, page.getCurrent(), "page <=0 时应回退为 1");
        assertEquals(10, page.getSize(), "size <=0 时应回退为 10");
    }

    @Test
    @DisplayName("getProjectById: 查询存在项目返回数据")
    void getProjectById_found() {
        Project existing = sampleProject("Found");
        existing.setId(20L);
        when(projectMapper.selectById(20L)).thenReturn(existing);

        Result<Project> result = projectService.getProjectById(20L);
        assertTrue(result.isSuccess());
        assertEquals("Found", result.getData().getProjectName());
    }

    @Test
    @DisplayName("getProjectById: 项目不存在抛出业务异常")
    void getProjectById_notFound_throws() {
        when(projectMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> projectService.getProjectById(404L));
    }

    @Test
    @DisplayName("updateProject: 项目不存在抛出业务异常")
    void updateProject_notFound_throws() {
        Project toUpdate = sampleProject("Update");
        toUpdate.setId(77L);
        when(projectMapper.selectById(77L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> projectService.updateProject(toUpdate));
        verify(projectMapper, never()).updateById(any(Project.class));
    }

    @Test
    @DisplayName("deleteProject: 删除已存在项目")
    void deleteProject_existing_removes() {
        Project existing = sampleProject("Delete Me");
        existing.setId(50L);
        when(projectMapper.selectById(50L)).thenReturn(existing);
        when(projectMapper.deleteById(50L)).thenReturn(1);

        Result result = projectService.deleteProject(50L);
        assertTrue(result.isSuccess());
        verify(projectMapper, times(1)).deleteById(50L);
    }

    @Test
    @DisplayName("dashboard: 返回按状态分组的项目映射")
    void dashboard_returnsGroupedByStatus() {
        Project approved = sampleProject("Approved");
        approved.setId(1L);
        approved.setStatus(STATUS_APPROVED);
        Project pending = sampleProject("Pending");
        pending.setId(2L);
        pending.setStatus(STATUS_PENDING);

        when(projectMapper.selectList(any(Wrapper.class)))
                .thenReturn(new ArrayList<>(List.of(approved, pending)));

        Result<Map<String, List<Project>>> result = projectService.dashboard(null);

        assertTrue(result.isSuccess());
        Map<String, List<Project>> grouped = result.getData();
        assertNotNull(grouped);
        assertEquals(1, grouped.get(STATUS_APPROVED).size());
        assertEquals(1, grouped.get(STATUS_PENDING).size());
        verify(projectMapper, times(1)).selectList(any(Wrapper.class));
        verify(projectMapper, never()).selectPage(any(), any());
    }

    @Test
    @DisplayName("dashboard: 按状态过滤时仅返回对应分组")
    void dashboard_filteredByStatus() {
        Project approved = sampleProject("Approved");
        approved.setId(1L);
        approved.setStatus(STATUS_APPROVED);

        when(projectMapper.selectList(any(Wrapper.class)))
                .thenReturn(new ArrayList<>(List.of(approved)));

        Result<Map<String, List<Project>>> result = projectService.dashboard(STATUS_APPROVED);

        assertTrue(result.isSuccess());
        Map<String, List<Project>> grouped = result.getData();
        assertNotNull(grouped);
        assertNotNull(grouped.get(STATUS_APPROVED));
        assertEquals(1, grouped.get(STATUS_APPROVED).size());
    }

    @Test
    @DisplayName("updateProject: 缺少 ID 抛出业务异常")
    void updateProject_missingId_throws() {
        Project toUpdate = sampleProject("No Id");
        assertThrows(BusinessException.class, () -> projectService.updateProject(toUpdate));
        verify(projectMapper, never()).updateById(any(Project.class));
    }
}
