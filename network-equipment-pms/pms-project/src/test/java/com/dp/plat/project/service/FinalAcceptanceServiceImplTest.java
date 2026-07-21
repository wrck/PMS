package com.dp.plat.project.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Deliverable;
import com.dp.plat.project.entity.FinalAcceptance;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.FinalAcceptanceMapper;
import com.dp.plat.project.mapper.ProjectDeliverableMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.punchlist.service.IPunchListService;
import com.dp.plat.project.service.impl.FinalAcceptanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FinalAcceptanceServiceImpl}.
 *
 * <p>终验交付物校验已从 pms_deliverable_checklist 表改为直接查 pms_deliverable 表，
 * 本测试使用 {@link Deliverable} 实体和 {@link ProjectDeliverableMapper} mock。</p>
 */
@ExtendWith(MockitoExtension.class)
class FinalAcceptanceServiceImplTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String PROJECT_COMPLETED = "COMPLETED";

    /** 8 类标准终验交付件类型。 */
    private static final String[] STANDARD_TYPES = {
            "AS_BUILT", "TEST_REPORT", "ACCEPTANCE_CERT", "TRAINING_RECORD",
            "OPERATION_MANUAL", "ASSET_REGISTER", "WARRANTY_CERT", "SPARE_PARTS_LIST"
    };
    /** 8 类标准终验交付件中文名称。 */
    private static final String[] STANDARD_NAMES = {
            "竣工资料", "测试报告", "验收证书", "培训记录",
            "操作手册", "资产清单", "质保证书", "备件清单"
    };

    @Mock
    private FinalAcceptanceMapper finalAcceptanceMapper;

    @Mock
    private IMilestoneService milestoneService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private IPunchListService punchListService;

    @Mock
    private ProjectDeliverableMapper deliverableMapper;

    @InjectMocks
    private FinalAcceptanceServiceImpl finalAcceptanceService;

    @BeforeEach
    void setUp() {
        // Wire the ServiceImpl.baseMapper field (FinalAcceptanceMapper) manually since
        // @InjectMocks stops at constructor injection.
        ReflectionTestUtils.setField(finalAcceptanceService, "baseMapper", finalAcceptanceMapper);
    }

    private Milestone milestone(String name, String status) {
        Milestone m = Milestone.builder()
                .projectId(10L)
                .milestoneName(name)
                .milestoneType("INSTALL")
                .planDate(LocalDate.of(2024, 3, 1))
                .status(status)
                .build();
        m.setId(1L);
        return m;
    }

    /**
     * 构造一份全部就绪（status=PUBLISHED, mandatory=true）的交付件列表，使终验申请能通过交付件校验。
     */
    private List<Deliverable> readyDeliverables() {
        List<Deliverable> list = new ArrayList<>();
        for (int i = 0; i < STANDARD_TYPES.length; i++) {
            Deliverable item = Deliverable.builder()
                    .projectId(10L)
                    .deliverableName(STANDARD_NAMES[i])
                    .deliverableType(STANDARD_TYPES[i])
                    .status("PUBLISHED")
                    .mandatory(true)
                    .currentVersion(1)
                    .build();
            item.setId((long) (i + 1));
            list.add(item);
        }
        return list;
    }

    /**
     * 构造一份全部未就绪（status=DRAFT, mandatory=true）的交付件列表。
     */
    private List<Deliverable> draftDeliverables() {
        List<Deliverable> list = new ArrayList<>();
        for (int i = 0; i < STANDARD_TYPES.length; i++) {
            Deliverable item = Deliverable.builder()
                    .projectId(10L)
                    .deliverableName(STANDARD_NAMES[i])
                    .deliverableType(STANDARD_TYPES[i])
                    .status("DRAFT")
                    .mandatory(true)
                    .currentVersion(1)
                    .build();
            item.setId((long) (i + 1));
            list.add(item);
        }
        return list;
    }

    @Test
    @DisplayName("apply: 所有里程碑完成且交付件就绪时成功创建终验申请")
    void apply_allMilestonesCompleted_success() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        List<Milestone> milestones = Arrays.asList(
                milestone("M1", STATUS_COMPLETED),
                milestone("M2", STATUS_COMPLETED));
        when(milestoneService.list(any(Wrapper.class))).thenReturn(milestones);
        // 所有 Punch List 项已验证
        when(punchListService.isAllVerified(10L)).thenReturn(true);
        // 交付件全部就绪（mandatory=true, status=PUBLISHED）
        when(deliverableMapper.selectList(any(Wrapper.class))).thenReturn(readyDeliverables());
        // 无重复申请
        when(finalAcceptanceMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);
        when(finalAcceptanceMapper.insert(any(FinalAcceptance.class))).thenReturn(1);

        Result result = finalAcceptanceService.apply(10L, "终验报告内容");

        assertTrue(result.isSuccess());
        FinalAcceptance acceptance = (FinalAcceptance) result.getData();
        assertNotNull(acceptance);
        assertEquals(STATUS_PENDING, acceptance.getStatus());
        assertEquals(10L, acceptance.getProjectId());
        assertEquals("终验报告内容", acceptance.getAcceptanceReport());
        assertNotNull(acceptance.getApplyTime());
        verify(finalAcceptanceMapper, times(1)).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 存在未完成里程碑时拒绝申请")
    void apply_milestonesNotCompleted_throws() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        List<Milestone> milestones = Arrays.asList(
                milestone("M1", STATUS_COMPLETED),
                milestone("M2", STATUS_PENDING));
        when(milestoneService.list(any(Wrapper.class))).thenReturn(milestones);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.apply(10L, "report"));
        assertTrue(ex.getMessage().contains("里程碑"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 无里程碑时拒绝申请")
    void apply_noMilestones_throws() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(milestoneService.list(any(Wrapper.class))).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.apply(10L, "report"));
        assertTrue(ex.getMessage().contains("里程碑"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 项目不存在抛出业务异常")
    void apply_projectNotFound_throws() {
        when(projectMapper.selectById(anyLong())).thenReturn(null);

        assertThrows(BusinessException.class, () -> finalAcceptanceService.apply(99L, "report"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: projectId 为 null 抛出业务异常")
    void apply_nullProjectId_throws() {
        assertThrows(BusinessException.class, () -> finalAcceptanceService.apply(null, "report"));
        verify(projectMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("apply: 已有待审批终验申请时拒绝重复申请")
    void apply_duplicatePending_throws() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(milestoneService.list(any(Wrapper.class)))
                .thenReturn(Collections.singletonList(milestone("M1", STATUS_COMPLETED)));
        when(punchListService.isAllVerified(10L)).thenReturn(true);
        when(deliverableMapper.selectList(any(Wrapper.class))).thenReturn(readyDeliverables());
        FinalAcceptance existing = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_PENDING).build();
        existing.setId(5L);
        when(finalAcceptanceMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(existing);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.apply(10L, "report"));
        assertTrue(ex.getMessage().contains("待审批"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 存在未验证的 Punch List 项时拒绝申请")
    void apply_punchListNotVerified_throws() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(milestoneService.list(any(Wrapper.class)))
                .thenReturn(Collections.singletonList(milestone("M1", STATUS_COMPLETED)));
        when(punchListService.isAllVerified(10L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.apply(10L, "report"));
        assertTrue(ex.getMessage().contains("Punch List"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 交付件未就绪时拒绝申请")
    void apply_deliverablesMissing_throws() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(milestoneService.list(any(Wrapper.class)))
                .thenReturn(Collections.singletonList(milestone("M1", STATUS_COMPLETED)));
        when(punchListService.isAllVerified(10L)).thenReturn(true);
        // 构造一份 AS_BUILT 未就绪（status=DRAFT）的交付件列表
        List<Deliverable> incomplete = new ArrayList<>();
        for (int i = 0; i < STANDARD_TYPES.length; i++) {
            Deliverable item = Deliverable.builder()
                    .projectId(10L)
                    .deliverableName(STANDARD_NAMES[i])
                    .deliverableType(STANDARD_TYPES[i])
                    .status(STANDARD_TYPES[i].equals("AS_BUILT") ? "DRAFT" : "PUBLISHED")
                    .mandatory(true)
                    .currentVersion(1)
                    .build();
            item.setId((long) (i + 1));
            incomplete.add(item);
        }
        when(deliverableMapper.selectList(any(Wrapper.class))).thenReturn(incomplete);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.apply(10L, "report"));
        assertTrue(ex.getMessage().contains("终验交付物未就绪"));
        assertTrue(ex.getMessage().contains("竣工资料"));
        verify(finalAcceptanceMapper, never()).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("apply: 无必需交付件时直接通过（不再自动初始化）")
    void apply_noMandatoryDeliverables_passes() {
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(milestoneService.list(any(Wrapper.class)))
                .thenReturn(Collections.singletonList(milestone("M1", STATUS_COMPLETED)));
        when(punchListService.isAllVerified(10L)).thenReturn(true);
        // 无任何交付件记录（无 mandatory），终验应直接通过
        when(deliverableMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());
        when(finalAcceptanceMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);
        when(finalAcceptanceMapper.insert(any(FinalAcceptance.class))).thenReturn(1);

        Result result = finalAcceptanceService.apply(10L, "report");

        assertTrue(result.isSuccess());
        verify(deliverableMapper, never()).insert(any(Deliverable.class));
        verify(finalAcceptanceMapper, times(1)).insert(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("approve: 审批通过后项目状态变更为 COMPLETED")
    void approve_shouldCompleteProject() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_PENDING).build();
        acceptance.setId(1L);
        when(finalAcceptanceMapper.selectById(1L)).thenReturn(acceptance);
        when(finalAcceptanceMapper.updateById(any(FinalAcceptance.class))).thenReturn(1);
        Project project = Project.builder().status(STATUS_APPROVED).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        Result result = finalAcceptanceService.approve(1L, "同意终验");

        assertTrue(result.isSuccess());
        FinalAcceptance approved = (FinalAcceptance) result.getData();
        assertEquals(STATUS_APPROVED, approved.getStatus());
        assertEquals("同意终验", approved.getAcceptanceOpinion());
        assertNotNull(approved.getAcceptTime());
        assertEquals(PROJECT_COMPLETED, project.getStatus(), "项目状态应被置为 COMPLETED");
        verify(finalAcceptanceMapper, times(1)).updateById(any(FinalAcceptance.class));
        verify(projectMapper, times(1)).updateById(any(Project.class));
    }

    @Test
    @DisplayName("approve: 终验申请不存在抛出业务异常")
    void approve_notFound_throws() {
        when(finalAcceptanceMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> finalAcceptanceService.approve(99L, "ok"));
        verify(finalAcceptanceMapper, never()).updateById(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("approve: 非待审批状态不允许审批")
    void approve_wrongStatus_throws() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_APPROVED).build();
        acceptance.setId(1L);
        when(finalAcceptanceMapper.selectById(1L)).thenReturn(acceptance);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> finalAcceptanceService.approve(1L, "ok"));
        assertTrue(ex.getMessage().contains("状态"));
        verify(finalAcceptanceMapper, never()).updateById(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("reject: 将待审批终验申请置为 REJECTED")
    void reject_shouldSetRejectedStatus() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_PENDING).build();
        acceptance.setId(2L);
        when(finalAcceptanceMapper.selectById(2L)).thenReturn(acceptance);
        when(finalAcceptanceMapper.updateById(any(FinalAcceptance.class))).thenReturn(1);

        Result result = finalAcceptanceService.reject(2L, "资料不全");

        assertTrue(result.isSuccess());
        FinalAcceptance rejected = (FinalAcceptance) result.getData();
        assertEquals(STATUS_REJECTED, rejected.getStatus());
        assertEquals("资料不全", rejected.getAcceptanceOpinion());
        assertNotNull(rejected.getAcceptTime());
        verify(finalAcceptanceMapper, times(1)).updateById(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("reject: opinion 为空时存为空串")
    void reject_emptyOpinion_storedAsEmpty() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_PENDING).build();
        acceptance.setId(2L);
        when(finalAcceptanceMapper.selectById(2L)).thenReturn(acceptance);
        when(finalAcceptanceMapper.updateById(any(FinalAcceptance.class))).thenReturn(1);

        Result result = finalAcceptanceService.reject(2L, null);

        assertTrue(result.isSuccess());
        FinalAcceptance rejected = (FinalAcceptance) result.getData();
        assertEquals(STATUS_REJECTED, rejected.getStatus());
        assertEquals("", rejected.getAcceptanceOpinion());
    }

    @Test
    @DisplayName("reject: 终验申请不存在抛出业务异常")
    void reject_notFound_throws() {
        when(finalAcceptanceMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> finalAcceptanceService.reject(99L, "no"));
        verify(finalAcceptanceMapper, never()).updateById(any(FinalAcceptance.class));
    }

    @Test
    @DisplayName("getByProjectId: 返回最新终验记录")
    void getByProjectId_returnsLatest() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(10L).status(STATUS_APPROVED).build();
        acceptance.setId(3L);
        when(finalAcceptanceMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(acceptance);

        Result<FinalAcceptance> result = finalAcceptanceService.getByProjectId(10L);

        assertTrue(result.isSuccess());
        assertEquals(3L, result.getData().getId());
    }

    @Test
    @DisplayName("getByProjectId: projectId 为 null 抛出业务异常")
    void getByProjectId_null_throws() {
        assertThrows(BusinessException.class, () -> finalAcceptanceService.getByProjectId(null));
    }

    @Test
    @DisplayName("approve: 项目不存在时仅更新终验记录，不抛错")
    void approve_projectMissing_stillUpdatesAcceptance() {
        FinalAcceptance acceptance = FinalAcceptance.builder()
                .projectId(99L).status(STATUS_PENDING).build();
        acceptance.setId(1L);
        when(finalAcceptanceMapper.selectById(1L)).thenReturn(acceptance);
        when(finalAcceptanceMapper.updateById(any(FinalAcceptance.class))).thenReturn(1);
        when(projectMapper.selectById(99L)).thenReturn(null);

        Result result = finalAcceptanceService.approve(1L, "ok");

        assertTrue(result.isSuccess());
        verify(finalAcceptanceMapper, times(1)).updateById(any(FinalAcceptance.class));
        verify(projectMapper, never()).updateById(any(Project.class));
    }
}
