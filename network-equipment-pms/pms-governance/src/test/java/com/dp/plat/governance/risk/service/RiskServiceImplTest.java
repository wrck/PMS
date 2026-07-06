package com.dp.plat.governance.risk.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.service.IChangeRequestService;
import com.dp.plat.governance.issue.entity.Issue;
import com.dp.plat.governance.issue.service.IIssueService;
import com.dp.plat.governance.risk.dto.RiskMatrixDto;
import com.dp.plat.governance.risk.entity.Risk;
import com.dp.plat.governance.risk.mapper.RiskMapper;
import com.dp.plat.governance.risk.service.impl.RiskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RiskServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RiskServiceImplTest {

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String STATUS_ESCALATED = "ESCALATED";

    private static final String PRIORITY_LOW = "LOW";
    private static final String PRIORITY_MEDIUM = "MEDIUM";
    private static final String PRIORITY_HIGH = "HIGH";

    @Mock
    private RiskMapper riskMapper;

    @Mock
    private IIssueService issueService;

    @Mock
    private IChangeRequestService changeRequestService;

    private RiskServiceImpl riskService;

    @BeforeEach
    void setUp() {
        riskService = Mockito.spy(new RiskServiceImpl(issueService, changeRequestService));
        ReflectionTestUtils.setField(riskService, "baseMapper", riskMapper);
    }

    private Risk sampleRisk(Long id, String riskNo, String status, Integer likelihood, Integer impact) {
        Risk risk = Risk.builder()
                .riskNo(riskNo)
                .projectId(100L)
                .description("风险描述")
                .likelihood(likelihood)
                .impact(impact)
                .status(status)
                .ownerId(100L)
                .ownerName("owner")
                .priority(PRIORITY_MEDIUM)
                .build();
        risk.setId(id);
        return risk;
    }

    // ==================== create ====================

    @Test
    @DisplayName("create: risk 为 null 抛出业务异常")
    void create_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> riskService.create(null));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("create: description 为空抛出业务异常")
    void create_emptyDescription_throws() {
        Risk risk = Risk.builder().projectId(100L).likelihood(3).impact(3).build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> riskService.create(risk));
        assertTrue(ex.getMessage().contains("描述"));
    }

    @Test
    @DisplayName("create: 正常创建，生成 riskNo、计算 score 和 priority、状态置 OPEN")
    void create_normal_success() {
        Risk risk = Risk.builder()
                .projectId(100L)
                .description("进度风险")
                .likelihood(3)
                .impact(4)
                .build();
        when(riskMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(riskService).save(any(Risk.class));

        Result<Risk> result = riskService.create(risk);

        assertTrue(result.isSuccess());
        assertNotNull(risk.getRiskNo(), "riskNo 应自动生成");
        assertEquals(STATUS_OPEN, risk.getStatus());
        assertEquals(12, risk.getScore(), "score = likelihood * impact = 3*4 = 12");
        assertEquals(PRIORITY_MEDIUM, risk.getPriority(), "score 12 应为 MEDIUM");
        assertNotNull(risk.getIdentifiedAt());
    }

    @Test
    @DisplayName("create: likelihood/impact 为 null 时不计算 score")
    void create_nullScores_noCompute() {
        Risk risk = Risk.builder()
                .projectId(100L)
                .description("风险")
                .build();
        when(riskMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(riskService).save(any(Risk.class));

        riskService.create(risk);

        assertEquals(null, risk.getScore(), "likelihood/impact 为 null 时 score 应保持 null");
        assertEquals(null, risk.getPriority(), "未计算时 priority 应保持 null");
    }

    // ==================== computeScore ====================

    @Test
    @DisplayName("computeScore: score ≤ 6 时 priority 为 LOW")
    void computeScore_lowPriority() {
        Risk risk = Risk.builder().likelihood(2).impact(3).build();
        riskService.computeScore(risk);
        assertEquals(6, risk.getScore());
        assertEquals(PRIORITY_LOW, risk.getPriority());
    }

    @Test
    @DisplayName("computeScore: 7 ≤ score ≤ 12 时 priority 为 MEDIUM")
    void computeScore_mediumPriority() {
        Risk risk = Risk.builder().likelihood(3).impact(4).build();
        riskService.computeScore(risk);
        assertEquals(12, risk.getScore());
        assertEquals(PRIORITY_MEDIUM, risk.getPriority());
    }

    @Test
    @DisplayName("computeScore: score ≥ 13 时 priority 为 HIGH")
    void computeScore_highPriority() {
        Risk risk = Risk.builder().likelihood(5).impact(5).build();
        riskService.computeScore(risk);
        assertEquals(25, risk.getScore());
        assertEquals(PRIORITY_HIGH, risk.getPriority());
    }

    @Test
    @DisplayName("computeScore: 越界值被 clamp 到 [1,5]")
    void computeScore_clampsOutOfRange() {
        Risk risk = Risk.builder().likelihood(10).impact(0).build();
        riskService.computeScore(risk);
        // clamp(10)=5, clamp(0)=1, score=5*1=5 → LOW
        assertEquals(5, risk.getScore());
        assertEquals(PRIORITY_LOW, risk.getPriority());
    }

    @Test
    @DisplayName("computeScore: risk 为 null 时无操作")
    void computeScore_nullRisk_noOp() {
        riskService.computeScore(null);
        // 无异常即通过
    }

    // ==================== update / delete / listAll / getById / listByProject ====================

    @Test
    @DisplayName("update: risk 或 id 为 null 抛出业务异常")
    void update_null_throws() {
        assertThrows(BusinessException.class, () -> riskService.update((Risk) null));
        Risk risk = Risk.builder().build();
        assertThrows(BusinessException.class, () -> riskService.update(risk));
    }

    @Test
    @DisplayName("update: 风险不存在抛出业务异常")
    void update_notFound_throws() {
        when(riskMapper.selectById(anyLong())).thenReturn(null);
        Risk risk = sampleRisk(99L, "RISK-2024-0001", STATUS_OPEN, 3, 3);
        assertThrows(BusinessException.class, () -> riskService.update(risk));
    }

    @Test
    @DisplayName("update: 正常更新并重新计算 score")
    void update_success() {
        Risk existing = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 2, 2);
        when(riskMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(riskService).updateById(any(Risk.class));

        Risk input = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 5, 5);
        Result<?> result = riskService.update(input);

        assertTrue(result.isSuccess());
        assertEquals(25, input.getScore(), "update 应重新计算 score");
        verify(riskService, times(1)).updateById(any(Risk.class));
    }

    @Test
    @DisplayName("delete: 风险不存在抛出业务异常")
    void delete_notFound_throws() {
        when(riskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> riskService.delete(99L));
    }

    @Test
    @DisplayName("delete: 正常删除成功")
    void delete_success() {
        Risk existing = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3);
        when(riskMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(riskService).removeById(1L);

        Result<?> result = riskService.delete(1L);

        assertTrue(result.isSuccess());
        verify(riskService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("listAll: 返回全部风险列表")
    void listAll_returnsList() {
        List<Risk> list = Arrays.asList(
                sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3),
                sampleRisk(2L, "RISK-2024-0002", STATUS_CLOSED, 5, 5));
        when(riskMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<Risk>> result = riskService.listAll();

        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("getById: 风险不存在抛出业务异常")
    void getById_notFound_throws() {
        when(riskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> riskService.getById(99L));
    }

    @Test
    @DisplayName("getById: 返回风险详情")
    void getById_success() {
        Risk risk = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3);
        when(riskMapper.selectById(1L)).thenReturn(risk);

        Result<Risk> result = riskService.getById(1L);

        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("listByProject: projectId 为 null 返回空列表")
    void listByProject_nullId_returnsEmpty() {
        Result<List<Risk>> result = riskService.listByProject(null);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("listByProject: 返回项目下的风险列表")
    void listByProject_returnsList() {
        List<Risk> list = Collections.singletonList(
                sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3));
        when(riskMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<Risk>> result = riskService.listByProject(100L);

        assertEquals(1, result.getData().size());
    }

    // ==================== markOccurred ====================

    @Test
    @DisplayName("markOccurred: 风险不存在抛出业务异常")
    void markOccurred_notFound_throws() {
        when(riskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> riskService.markOccurred(99L));
    }

    @Test
    @DisplayName("markOccurred: 创建问题并关闭风险")
    void markOccurred_createsIssueAndClosesRisk() {
        Risk risk = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3);
        when(riskMapper.selectById(1L)).thenReturn(risk);
        when(issueService.generateIssueNo()).thenReturn("ISSUE-2024-0001");
        Mockito.doReturn(true).when(issueService).save(any(Issue.class));
        Mockito.doReturn(true).when(riskService).updateById(any(Risk.class));

        Result<?> result = riskService.markOccurred(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_CLOSED, risk.getStatus(), "风险应被关闭");
        assertNotNull(risk.getClosedAt());
        verify(issueService, times(1)).save(any(Issue.class));
        verify(riskService, times(1)).updateById(any(Risk.class));
    }

    // ==================== escalate ====================

    @Test
    @DisplayName("escalate: 风险不存在抛出业务异常")
    void escalate_notFound_throws() {
        when(riskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> riskService.escalate(99L));
    }

    @Test
    @DisplayName("escalate: 创建变更请求并标记风险为 ESCALATED")
    void escalate_createsChangeRequest() {
        Risk risk = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 5, 5);
        when(riskMapper.selectById(1L)).thenReturn(risk);
        ChangeRequest createdCr = ChangeRequest.builder().title("由RISK-2024-0001升级的变更请求").build();
        createdCr.setId(50L);
        when(changeRequestService.create(any(ChangeRequest.class))).thenReturn(Result.ok(createdCr));
        Mockito.doReturn(true).when(riskService).updateById(any(Risk.class));

        Result<?> result = riskService.escalate(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_ESCALATED, risk.getStatus(), "风险应标记为 ESCALATED");
        verify(changeRequestService, times(1)).create(any(ChangeRequest.class));
        verify(riskService, times(1)).updateById(any(Risk.class));
    }

    // ==================== generateRiskNo ====================

    @Test
    @DisplayName("generateRiskNo: 当年无已有风险时生成 0001 序号")
    void generateRiskNo_emptySequence() {
        when(riskMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        String riskNo = riskService.generateRiskNo();

        assertTrue(riskNo.startsWith("RISK-" + LocalDate.now().getYear() + "-"));
        assertTrue(riskNo.endsWith("0001"));
    }

    @Test
    @DisplayName("generateRiskNo: 当年已有 N 条时生成 N+1 序号")
    void generateRiskNo_existingSequence() {
        when(riskMapper.selectCount(any(Wrapper.class))).thenReturn(3L);

        String riskNo = riskService.generateRiskNo();

        assertTrue(riskNo.endsWith("0004"));
    }

    // ==================== riskMatrix ====================

    @Test
    @DisplayName("riskMatrix: projectId 为 null 时返回全部风险矩阵")
    void riskMatrix_nullProjectId() {
        List<Risk> risks = Arrays.asList(
                sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 4),
                sampleRisk(2L, "RISK-2024-0002", STATUS_OPEN, 5, 5));
        // riskMatrix null projectId calls this.list() → baseMapper.selectList(null)
        when(riskMapper.selectList(any(Wrapper.class))).thenReturn(risks);

        Result<RiskMatrixDto> result = riskService.riskMatrix(null);

        RiskMatrixDto dto = result.getData();
        assertEquals(2, dto.getTotalRisks());
        // matrix[5-1][5-1] = matrix[4][4] 应为 1（likelihood=5,impact=5）
        assertEquals(1, dto.getMatrix().get(4).get(4));
        // matrix[3-1][4-1] = matrix[2][3] 应为 1（likelihood=3,impact=4）
        assertEquals(1, dto.getMatrix().get(2).get(3));
        // likelihood=5,impact=5 → score=25 → HIGH
        assertEquals(1, dto.getHighPriorityCount());
    }

    @Test
    @DisplayName("riskMatrix: 指定 projectId 时按项目过滤")
    void riskMatrix_withProjectId() {
        List<Risk> risks = Collections.singletonList(
                sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 2, 3));
        when(riskMapper.selectList(any(Wrapper.class))).thenReturn(risks);

        Result<RiskMatrixDto> result = riskService.riskMatrix(100L);

        RiskMatrixDto dto = result.getData();
        assertEquals(1, dto.getTotalRisks());
        // score=6 → LOW，highPriorityCount=0
        assertEquals(0, dto.getHighPriorityCount());
        // matrix[2-1][3-1] = matrix[1][2] 应为 1
        assertEquals(1, dto.getMatrix().get(1).get(2));
    }

    @Test
    @DisplayName("riskMatrix: 空风险列表时矩阵全 0")
    void riskMatrix_emptyRisks() {
        when(riskMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        Result<RiskMatrixDto> result = riskService.riskMatrix(null);

        RiskMatrixDto dto = result.getData();
        assertEquals(0, dto.getTotalRisks());
        assertEquals(0, dto.getHighPriorityCount());
        // 验证 5x5 矩阵全 0
        for (int l = 0; l < 5; l++) {
            for (int i = 0; i < 5; i++) {
                assertEquals(0, dto.getMatrix().get(l).get(i));
            }
        }
    }

    @Test
    @DisplayName("riskMatrix: likelihood/impact 为 null 的风险不计入矩阵但计入总数")
    void riskMatrix_nullScoresNotInMatrix() {
        Risk riskWithScores = sampleRisk(1L, "RISK-2024-0001", STATUS_OPEN, 3, 3);
        Risk riskNoScores = Risk.builder()
                .riskNo("RISK-2024-0002")
                .projectId(100L)
                .description("无评分风险")
                .priority(PRIORITY_HIGH)
                .build();
        riskNoScores.setId(2L);
        Mockito.doReturn(Arrays.asList(riskWithScores, riskNoScores)).when(riskService).list();

        Result<RiskMatrixDto> result = riskService.riskMatrix(null);

        RiskMatrixDto dto = result.getData();
        assertEquals(2, dto.getTotalRisks(), "总数应包含无评分风险");
        assertEquals(1, dto.getHighPriorityCount(), "无评分但 priority=HIGH 也应计入高优先级数");
    }
}
