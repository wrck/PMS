package com.dp.plat.governance.risk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Risk entity (governance three-books: risk register).
 *
 * <p>Tracks identified project risks with likelihood/impact scoring, mitigation
 * strategy and owner. Risks can be escalated to change requests or converted to
 * issues when they materialize.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_risk")
public class Risk extends BaseEntity {

    /** Risk number (RISK-YYYY-XXXX). */
    private String riskNo;

    /** Project id. */
    private Long projectId;

    /** Risk description. */
    private String description;

    /** Risk category (TECHNICAL, EXTERNAL, ORGANIZATIONAL, PM). */
    private String category;

    /** Likelihood score (1-5). */
    private Integer likelihood;

    /** Impact score (1-5). */
    private Integer impact;

    /** Risk score (likelihood * impact). */
    private Integer score;

    /** Priority (LOW, MEDIUM, HIGH) - computed from score. */
    private String priority;

    /** Mitigation strategy (AVOID, MITIGATE, TRANSFER, ACCEPT). */
    private String mitigation;

    /** Contingency plan (TEXT). */
    private String contingencyPlan;

    /** Owner user id. */
    private Long ownerId;

    /** Owner user name. */
    private String ownerName;

    /** Status (OPEN, IN_PROGRESS, CLOSED, ESCALATED). */
    private String status;

    /** Next review date. */
    private LocalDate reviewDate;

    /** Source issue id if converted from an issue (nullable). */
    private Long sourceIssueId;

    /** Time the risk was identified. */
    private LocalDateTime identifiedAt;

    /** Time the risk was closed. */
    private LocalDateTime closedAt;
}
