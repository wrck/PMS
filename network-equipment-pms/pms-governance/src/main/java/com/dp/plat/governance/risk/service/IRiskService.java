package com.dp.plat.governance.risk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.risk.dto.RiskMatrixDto;
import com.dp.plat.governance.risk.entity.Risk;

import java.util.List;

/**
 * Service for {@link Risk}.
 *
 * <p>Implements the risk register lifecycle with automatic score/priority
 * computation. Supports three-book linkage: a materialized risk can be converted
 * to an issue ({@link #markOccurred}) and a risk requiring formal change can be
 * escalated to a change request ({@link #escalate}).</p>
 */
public interface IRiskService extends IService<Risk> {

    /**
     * Create a risk with auto-computed score and priority.
     *
     * @param risk risk to create
     * @return operation result containing the created risk
     */
    Result<Risk> create(Risk risk);

    /**
     * Update a risk with auto-computed score and priority.
     *
     * @param risk risk to update
     * @return operation result
     */
    Result<?> update(Risk risk);

    /**
     * Delete a risk by id.
     *
     * @param id risk id
     * @return operation result
     */
    Result<?> delete(Long id);

    /**
     * List all risks.
     *
     * @return operation result containing the list
     */
    Result<List<Risk>> listAll();

    /**
     * Get a risk by id.
     *
     * @param id risk id
     * @return operation result containing the risk
     */
    Result<Risk> getById(Long id);

    /**
     * List risks by project id.
     *
     * @param projectId project id
     * @return operation result containing the list
     */
    Result<List<Risk>> listByProject(Long projectId);

    /**
     * Compute the risk score (likelihood * impact) and priority.
     * Score 1-6 → LOW, 7-12 → MEDIUM, 13-25 → HIGH.
     *
     * @param risk risk to compute
     */
    void computeScore(Risk risk);

    /**
     * Mark a risk as occurred: convert it to a new issue and close the risk.
     *
     * @param id risk id
     * @return operation result containing the created issue
     */
    Result<?> markOccurred(Long id);

    /**
     * Escalate a risk: create a change request and set status to ESCALATED.
     *
     * @param id risk id
     * @return operation result containing the created change request
     */
    Result<?> escalate(Long id);

    /**
     * Generate the risk number in format RISK-YYYY-XXXX.
     *
     * @return generated risk number
     */
    String generateRiskNo();

    /**
     * Build a 5x5 risk matrix for a project.
     *
     * @param projectId project id
     * @return operation result containing the risk matrix
     */
    Result<RiskMatrixDto> riskMatrix(Long projectId);
}
