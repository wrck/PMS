package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.AgentScoreMapper;
import com.dp.plat.implementation.service.IAgentScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IAgentScoreService}.
 */
@Service
@RequiredArgsConstructor
public class AgentScoreServiceImpl extends ServiceImpl<AgentScoreMapper, AgentScore> implements IAgentScoreService {

    private final AgentMapper agentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentScore evaluate(AgentScore score) {
        if (score.getEvaluateTime() == null) {
            score.setEvaluateTime(LocalDateTime.now());
        }
        if (score.getEvaluatorId() == null) {
            score.setEvaluatorId(SecurityUtils.getCurrentUserId());
        }
        if (score.getEvaluatorName() == null) {
            score.setEvaluatorName(SecurityUtils.getCurrentUsername());
        }
        // Compute the overall score of this evaluation as the average of the
        // three dimension scores.
        score.setOverallScore(calculateOverallScore(score));
        this.save(score);

        // Recalculate the agent's overall_score as the average of all
        // evaluations' overall_score.
        recalculateAgentOverallScore(score.getAgentId());
        return score;
    }

    @Override
    public List<AgentScore> listByAgentId(Long agentId) {
        return this.list(new LambdaQueryWrapper<AgentScore>()
                .eq(AgentScore::getAgentId, agentId)
                .orderByDesc(AgentScore::getEvaluateTime));
    }

    private BigDecimal calculateOverallScore(AgentScore score) {
        int sum = 0;
        int count = 0;
        if (score.getResponseSpeedScore() != null) {
            sum += score.getResponseSpeedScore();
            count++;
        }
        if (score.getConstructionQualityScore() != null) {
            sum += score.getConstructionQualityScore();
            count++;
        }
        if (score.getDocumentCompletenessScore() != null) {
            sum += score.getDocumentCompletenessScore();
            count++;
        }
        if (count == 0) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf((double) sum / count).setScale(1, RoundingMode.HALF_UP);
    }

    private void recalculateAgentOverallScore(Long agentId) {
        List<AgentScore> scores = this.list(new LambdaQueryWrapper<AgentScore>()
                .eq(AgentScore::getAgentId, agentId));
        if (scores.isEmpty()) {
            return;
        }
        double avg = scores.stream()
                .filter(s -> s.getOverallScore() != null)
                .mapToDouble(s -> s.getOverallScore().doubleValue())
                .average().orElse(0);
        Agent agent = agentMapper.selectById(agentId);
        if (agent != null) {
            agent.setOverallScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            agentMapper.updateById(agent);
        }
    }
}
