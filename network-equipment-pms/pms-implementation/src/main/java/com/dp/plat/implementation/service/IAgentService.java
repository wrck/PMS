package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.Agent;

import java.util.Map;

/**
 * Service for {@link Agent}.
 */
public interface IAgentService extends IService<Agent> {

    /**
     * Get the average scores for an agent across all evaluations.
     *
     * @return a map containing avgResponseSpeed, avgConstructionQuality,
     *         avgDocumentCompleteness, overallScore and evaluationCount
     */
    Map<String, Object> getScore(Long agentId);

    /**
     * Paginated agent query with optional filters.
     */
    Page<Agent> list(int page, int size, Agent filters);
}
