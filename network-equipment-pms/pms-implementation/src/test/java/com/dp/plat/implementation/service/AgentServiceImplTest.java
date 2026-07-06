package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.AgentScoreMapper;
import com.dp.plat.implementation.service.impl.AgentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AgentServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    private AgentMapper agentMapper;

    @Mock
    private AgentScoreMapper agentScoreMapper;

    private AgentServiceImpl agentService;

    @BeforeEach
    void setUp() {
        agentService = Mockito.spy(new AgentServiceImpl(agentScoreMapper));
        ReflectionTestUtils.setField(agentService, "baseMapper", agentMapper);
    }

    private Agent sampleAgent(Long id, String name, String code, Integer status, BigDecimal overallScore) {
        Agent agent = Agent.builder()
                .agentName(name)
                .agentCode(code)
                .status(status)
                .overallScore(overallScore)
                .build();
        agent.setId(id);
        return agent;
    }

    private AgentScore sampleScore(Long id, Long agentId, Integer response, Integer quality, Integer doc) {
        AgentScore score = AgentScore.builder()
                .agentId(agentId)
                .responseSpeedScore(response)
                .constructionQualityScore(quality)
                .documentCompletenessScore(doc)
                .build();
        score.setId(id);
        return score;
    }

    // ==================== getScore ====================

    @Test
    @DisplayName("getScore: 设备商不存在时返回零值")
    void getScore_agentNotFound_returnsZeros() {
        when(agentMapper.selectById(99L)).thenReturn(null);
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        Map<String, Object> result = agentService.getScore(99L);

        assertEquals(99L, result.get("agentId"));
        assertNull(result.get("agentName"));
        assertEquals(BigDecimal.ZERO, result.get("overallScore"));
        assertEquals(0, result.get("evaluationCount"));
        assertEquals(BigDecimal.ZERO, result.get("avgResponseSpeed"));
        assertEquals(BigDecimal.ZERO, result.get("avgConstructionQuality"));
        assertEquals(BigDecimal.ZERO, result.get("avgDocumentCompleteness"));
    }

    @Test
    @DisplayName("getScore: 设备商存在但无评分记录时返回零平均分")
    void getScore_noScores_returnsZeros() {
        Agent agent = sampleAgent(1L, "AgentA", "A001", 1, new BigDecimal("8.5"));
        when(agentMapper.selectById(1L)).thenReturn(agent);
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        Map<String, Object> result = agentService.getScore(1L);

        assertEquals("AgentA", result.get("agentName"));
        assertEquals(new BigDecimal("8.5"), result.get("overallScore"));
        assertEquals(0, result.get("evaluationCount"));
    }

    @Test
    @DisplayName("getScore: 有评分记录时计算各维度平均分")
    void getScore_withScores_calculatesAverages() {
        Agent agent = sampleAgent(1L, "AgentA", "A001", 1, new BigDecimal("8.0"));
        when(agentMapper.selectById(1L)).thenReturn(agent);
        List<AgentScore> scores = Arrays.asList(
                sampleScore(1L, 1L, 8, 9, 7),
                sampleScore(2L, 1L, 10, 7, 9));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(scores);

        Map<String, Object> result = agentService.getScore(1L);

        assertEquals(2, result.get("evaluationCount"));
        // avg response = (8+10)/2 = 9.0
        assertEquals(new BigDecimal("9.0"), result.get("avgResponseSpeed"));
        // avg quality = (9+7)/2 = 8.0
        assertEquals(new BigDecimal("8.0"), result.get("avgConstructionQuality"));
        // avg doc = (7+9)/2 = 8.0
        assertEquals(new BigDecimal("8.0"), result.get("avgDocumentCompleteness"));
    }

    @Test
    @DisplayName("getScore: 评分维度含 null 时跳过 null 计算")
    void getScore_nullDimensionScores_skipped() {
        Agent agent = sampleAgent(1L, "AgentA", "A001", 1, new BigDecimal("8.0"));
        when(agentMapper.selectById(1L)).thenReturn(agent);
        // 一条评分缺 responseSpeedScore，一条完整
        List<AgentScore> scores = Arrays.asList(
                sampleScore(1L, 1L, null, 9, 7),
                sampleScore(2L, 1L, 10, 7, 9));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(scores);

        Map<String, Object> result = agentService.getScore(1L);

        assertEquals(2, result.get("evaluationCount"));
        // avg response 仅算非 null: 10/1 = 10.0
        assertEquals(new BigDecimal("10.0"), result.get("avgResponseSpeed"));
        // avg quality = (9+7)/2 = 8.0
        assertEquals(new BigDecimal("8.0"), result.get("avgConstructionQuality"));
    }

    // ==================== list ====================

    @Test
    @DisplayName("list: 无过滤条件返回分页结果")
    void list_noFilters_returnsPage() {
        Page<Agent> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
                sampleAgent(1L, "AgentA", "A001", 1, null),
                sampleAgent(2L, "AgentB", "A002", 1, null)));
        Mockito.doReturn(mockPage).when(agentService).page(any(Page.class), any(Wrapper.class));

        Page<Agent> result = agentService.list(1, 10, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
    }

    @Test
    @DisplayName("list: 带过滤条件查询")
    void list_withFilters_returnsPage() {
        Page<Agent> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(
                sampleAgent(1L, "AgentA", "A001", 1, null)));
        Mockito.doReturn(mockPage).when(agentService).page(any(Page.class), any(Wrapper.class));

        Agent filter = Agent.builder().agentName("Agent").status(1).build();
        Page<Agent> result = agentService.list(1, 10, filter);

        assertEquals(1, result.getRecords().size());
        assertEquals("AgentA", result.getRecords().get(0).getAgentName());
    }

    @Test
    @DisplayName("list: 空结果页")
    void list_emptyPage() {
        Page<Agent> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.emptyList());
        Mockito.doReturn(mockPage).when(agentService).page(any(Page.class), any(Wrapper.class));

        Page<Agent> result = agentService.list(1, 10, null);

        assertEquals(0, result.getRecords().size());
    }
}
