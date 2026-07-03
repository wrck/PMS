package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.AgentScoreMapper;
import com.dp.plat.implementation.service.IAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IAgentService}.
 */
@Service
@RequiredArgsConstructor
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements IAgentService {

    private final AgentScoreMapper agentScoreMapper;

    @Override
    public Map<String, Object> getScore(Long agentId) {
        Map<String, Object> result = new HashMap<>();
        Agent agent = this.getById(agentId);
        result.put("agentId", agentId);
        result.put("agentName", agent == null ? null : agent.getAgentName());
        result.put("overallScore", agent == null ? BigDecimal.ZERO : agent.getOverallScore());

        List<AgentScore> scores = agentScoreMapper.selectList(
                new LambdaQueryWrapper<AgentScore>().eq(AgentScore::getAgentId, agentId));
        result.put("evaluationCount", scores.size());

        if (scores.isEmpty()) {
            result.put("avgResponseSpeed", BigDecimal.ZERO);
            result.put("avgConstructionQuality", BigDecimal.ZERO);
            result.put("avgDocumentCompleteness", BigDecimal.ZERO);
            return result;
        }

        double avgResponse = scores.stream()
                .filter(s -> s.getResponseSpeedScore() != null)
                .mapToInt(AgentScore::getResponseSpeedScore)
                .average().orElse(0);
        double avgQuality = scores.stream()
                .filter(s -> s.getConstructionQualityScore() != null)
                .mapToInt(AgentScore::getConstructionQualityScore)
                .average().orElse(0);
        double avgDoc = scores.stream()
                .filter(s -> s.getDocumentCompletenessScore() != null)
                .mapToInt(AgentScore::getDocumentCompletenessScore)
                .average().orElse(0);

        result.put("avgResponseSpeed", BigDecimal.valueOf(avgResponse).setScale(1, RoundingMode.HALF_UP));
        result.put("avgConstructionQuality", BigDecimal.valueOf(avgQuality).setScale(1, RoundingMode.HALF_UP));
        result.put("avgDocumentCompleteness", BigDecimal.valueOf(avgDoc).setScale(1, RoundingMode.HALF_UP));
        return result;
    }

    @Override
    public Page<Agent> list(int page, int size, Agent filters) {
        LambdaQueryWrapper<Agent> wrapper = new LambdaQueryWrapper<>();
        if (filters != null) {
            wrapper.like(filters.getAgentName() != null, Agent::getAgentName, filters.getAgentName())
                    .like(filters.getAgentCode() != null, Agent::getAgentCode, filters.getAgentCode())
                    .eq(filters.getStatus() != null, Agent::getStatus, filters.getStatus());
        }
        wrapper.orderByDesc(Agent::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
