package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.AgentScore;

import java.util.List;

/**
 * Service for {@link AgentScore}.
 */
public interface IAgentScoreService extends IService<AgentScore> {

    /**
     * Evaluate an agent: create an evaluation, then recalculate the agent's
     * overall_score as the average of all evaluations.
     */
    AgentScore evaluate(AgentScore score);

    /**
     * List evaluations by agent id.
     */
    List<AgentScore> listByAgentId(Long agentId);
}
