package com.dp.plat.governance.issue.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
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
 * Issue entity (governance three-books: issue log).
 *
 * <p>Tracks issues raised on a project. Issues can originate from materialized
 * risks or change requests, and can themselves be escalated to change requests.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_issue")
public class Issue extends BaseEntity {

    /** Issue number (ISSUE-YYYY-XXXX). */
    @NotBlank(message = "问题编号不能为空")
    @Size(max = 50, message = "问题编号长度不能超过 50 个字符")
    private String issueNo;

    /** Project id. */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** Issue description. */
    @NotBlank(message = "问题描述不能为空")
    @Size(max = 2000, message = "问题描述长度不能超过 2000 个字符")
    private String description;

    /** User id who raised the issue. */
    private Long raisedBy;

    /** User name who raised the issue. */
    @Size(max = 50, message = "提出人名称长度不能超过 50 个字符")
    private String raisedByName;

    /** Assignee user id. */
    private Long assigneeId;

    /** Assignee user name. */
    @Size(max = 50, message = "处理人名称长度不能超过 50 个字符")
    private String assigneeName;

    /** Priority (LOW, MEDIUM, HIGH, CRITICAL). */
    @NotBlank(message = "优先级不能为空")
    @Size(max = 20, message = "优先级长度不能超过 20 个字符")
    private String priority;

    /** Target resolution date. */
    private LocalDate targetResolveDate;

    /** Status (OPEN, IN_PROGRESS, RESOLVED, CLOSED). */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    /** Source risk id (nullable, if from risk). */
    private Long sourceRiskId;

    /** Source risk number (nullable, if from risk). */
    @Size(max = 50, message = "来源风险编号长度不能超过 50 个字符")
    private String sourceRiskNo;

    /** Source change request id (nullable, if from change). */
    private Long sourceChangeId;

    /** Source change request number (nullable, if from change). */
    @Size(max = 50, message = "来源变更单号长度不能超过 50 个字符")
    private String sourceCrNo;

    /** Resolution time. */
    private LocalDateTime resolvedAt;

    /** Closure time. */
    private LocalDateTime closedAt;

    /** Resolution description (TEXT). */
    @Size(max = 2000, message = "解决方案长度不能超过 2000 个字符")
    private String resolution;
}
