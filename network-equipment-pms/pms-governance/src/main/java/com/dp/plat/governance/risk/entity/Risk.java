package com.dp.plat.governance.risk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "风险编号不能为空")
    @Size(max = 50, message = "风险编号长度不能超过 50 个字符")
    private String riskNo;

    /** Project id. */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** Risk description. */
    @NotBlank(message = "风险描述不能为空")
    @Size(max = 2000, message = "风险描述长度不能超过 2000 个字符")
    private String description;

    /** Risk category (TECHNICAL, EXTERNAL, ORGANIZATIONAL, PM). */
    @Size(max = 50, message = "风险类别长度不能超过 50 个字符")
    private String category;

    /** Likelihood score (1-5). */
    @NotNull(message = "可能性评分不能为空")
    @Min(value = 1, message = "可能性评分最小为 1")
    @Max(value = 5, message = "可能性评分最大为 5")
    private Integer likelihood;

    /** Impact score (1-5). */
    @NotNull(message = "影响评分不能为空")
    @Min(value = 1, message = "影响评分最小为 1")
    @Max(value = 5, message = "影响评分最大为 5")
    private Integer impact;

    /** Risk score (likelihood * impact). */
    @Min(value = 1, message = "风险评分最小为 1")
    @Max(value = 25, message = "风险评分最大为 25")
    private Integer score;

    /** Priority (LOW, MEDIUM, HIGH) - computed from score. */
    @Size(max = 20, message = "优先级长度不能超过 20 个字符")
    private String priority;

    /** Mitigation strategy (AVOID, MITIGATE, TRANSFER, ACCEPT). */
    @Size(max = 50, message = "缓解策略长度不能超过 50 个字符")
    private String mitigation;

    /** Contingency plan (TEXT). */
    @Size(max = 2000, message = "应急预案长度不能超过 2000 个字符")
    private String contingencyPlan;

    /** Owner user id. */
    private Long ownerId;

    /** Owner user name. */
    @Size(max = 50, message = "负责人名称长度不能超过 50 个字符")
    private String ownerName;

    /** Status (OPEN, IN_PROGRESS, CLOSED, ESCALATED). */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
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
