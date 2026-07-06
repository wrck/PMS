package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.AgentScoreMapper;
import com.dp.plat.implementation.service.impl.AgentScoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AgentScoreServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AgentScoreServiceImplTest {

    @Mock
    private AgentScoreMapper agentScoreMapper;

    @Mock
    private AgentMapper agentMapper;

    private AgentScoreServiceImpl agentScoreService;

    @BeforeEach
    void setUp() {
        agentScoreService = Mockito.spy(new AgentScoreServiceImpl(agentMapper));
        ReflectionTestUtils.setField(agentScoreService, "baseMapper", agentScoreMapper);
    }

    private AgentScore sampleScore(Long id, Long agentId, Integer response, Integer quality, Integer doc,
                                   BigDecimal overall) {
        AgentScore score = AgentScore.builder()
                .agentId(agentId)
                .responseSpeedScore(response)
                .constructionQualityScore(quality)
                .documentCompletenessScore(doc)
                .overallScore(overall)
                .build();
        score.setId(id);
        return score;
    }

    // ==================== evaluate ====================

    @Test
    @DisplayName("evaluate: 缺省 evaluateTime/evaluator 时自动填充，计算 overallScore 并更新设备商总分")
    void evaluate_defaultFieldsFilled() {
        AgentScore score = AgentScore.builder()
                .agentId(1L)
                .responseSpeedScore(8)
                .constructionQualityScore(9)
                .documentCompletenessScore(7)
                .build();
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        // recalculate: list 返回当前评分
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(score));
        Agent agent = Agent.builder().agentName("AgentA").build();
        agent.setId(1L);
        when(agentMapper.selectById(1L)).thenReturn(agent);
        when(agentMapper.updateById(any(Agent.class))).thenReturn(1);

        AgentScore result = agentScoreService.evaluate(score);

        assertEquals(score, result);
        assertNotNull(score.getEvaluateTime(), "evaluateTime 缺省时应自动填充");
        // SecurityUtils.getCurrentUserId() 在无认证上下文的单元测试中返回 null，但代码确实调用了它
        // evaluatorName 来自 SecurityUtils.getCurrentUsername()，无认证时返回 "system"
        assertEquals("system", score.getEvaluatorName(), "evaluatorName 缺省时应填充为 system");
        // overall = (8+9+7)/3 = 8.0
        assertEquals(new BigDecimal("8.0"), score.getOverallScore());
        verify(agentScoreService, times(1)).save(any(AgentScore.class));
        verify(agentMapper, times(1)).updateById(any(Agent.class));
    }

    @Test
    @DisplayName("evaluate: 显式 evaluateTime/evaluator 时不被覆盖")
    void evaluate_keepsExplicitFields() {
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 9, 0);
        AgentScore score = AgentScore.builder()
                .agentId(1L)
                .responseSpeedScore(8)
                .constructionQualityScore(8)
                .documentCompletenessScore(8)
                .evaluateTime(fixed)
                .evaluatorId(999L)
                .evaluatorName("alice")
                .build();
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(score));
        when(agentMapper.selectById(anyLong())).thenReturn(null);

        agentScoreService.evaluate(score);

        assertEquals(fixed, score.getEvaluateTime());
        assertEquals(999L, score.getEvaluatorId());
        assertEquals("alice", score.getEvaluatorName());
    }

    @Test
    @DisplayName("evaluate: 仅部分维度有评分时 overallScore 取已有维度平均")
    void evaluate_partialDimensions() {
        AgentScore score = AgentScore.builder()
                .agentId(1L)
                .responseSpeedScore(10)
                .constructionQualityScore(null)
                .documentCompletenessScore(null)
                .build();
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(score));
        when(agentMapper.selectById(anyLong())).thenReturn(null);

        agentScoreService.evaluate(score);

        // overall = 10/1 = 10.0
        assertEquals(new BigDecimal("10.0"), score.getOverallScore());
    }

    @Test
    @DisplayName("evaluate: 三维度全为 null 时 overallScore 为 0.0")
    void evaluate_allDimensionsNull() {
        AgentScore score = AgentScore.builder()
                .agentId(1L)
                .build();
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(score));
        when(agentMapper.selectById(anyLong())).thenReturn(null);

        agentScoreService.evaluate(score);

        assertEquals(new BigDecimal("0.0"), score.getOverallScore());
    }

    @Test
    @DisplayName("evaluate: 多条评分时重新计算设备商总分取平均")
    void evaluate_recalculatesAgentOverallScore() {
        AgentScore newScore = sampleScore(null, 1L, 8, 8, 8, null);
        AgentScore existing1 = sampleScore(1L, 1L, 9, 9, 9, new BigDecimal("9.0"));
        // recalculate 时 list 返回已有 + 新评分（overallScore 已计算）
        AgentScore newScoreWithOverall = sampleScore(2L, 1L, 8, 8, 8, new BigDecimal("8.0"));
        when(agentScoreMapper.selectList(any(Wrapper.class)))
                .thenReturn(Arrays.asList(existing1, newScoreWithOverall));
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        Agent agent = Agent.builder().agentName("AgentA").build();
        agent.setId(1L);
        when(agentMapper.selectById(1L)).thenReturn(agent);
        when(agentMapper.updateById(any(Agent.class))).thenReturn(1);

        agentScoreService.evaluate(newScore);

        // agent.overallScore 应为 (9.0 + 8.0) / 2 = 8.5
        assertEquals(new BigDecimal("8.5"), agent.getOverallScore());
        verify(agentMapper, times(1)).updateById(any(Agent.class));
    }

    @Test
    @DisplayName("evaluate: 设备商不存在时不更新总分")
    void evaluate_agentNotFound_noUpdate() {
        AgentScore score = AgentScore.builder()
                .agentId(99L)
                .responseSpeedScore(8)
                .constructionQualityScore(8)
                .documentCompletenessScore(8)
                .build();
        Mockito.doReturn(true).when(agentScoreService).save(any(AgentScore.class));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(score));
        when(agentMapper.selectById(99L)).thenReturn(null);

        agentScoreService.evaluate(score);

        verify(agentMapper, never()).updateById(any(Agent.class));
    }

    // ==================== listByAgentId ====================

    @Test
    @DisplayName("listByAgentId: 返回设备商的所有评分记录")
    void listByAgentId_returnsList() {
        List<AgentScore> scores = Arrays.asList(
                sampleScore(1L, 1L, 8, 9, 7, new BigDecimal("8.0")),
                sampleScore(2L, 1L, 10, 7, 9, new BigDecimal("8.7")));
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(scores);

        List<AgentScore> result = agentScoreService.listByAgentId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("listByAgentId: 无评分记录返回空列表")
    void listByAgentId_empty() {
        when(agentScoreMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        List<AgentScore> result = agentScoreService.listByAgentId(1L);

        assertEquals(0, result.size());
    }
}
