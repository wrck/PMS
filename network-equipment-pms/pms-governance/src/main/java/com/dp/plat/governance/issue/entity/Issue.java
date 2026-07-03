package com.dp.plat.governance.issue.entity;

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
    private String issueNo;

    /** Project id. */
    private Long projectId;

    /** Issue description. */
    private String description;

    /** User id who raised the issue. */
    private Long raisedBy;

    /** User name who raised the issue. */
    private String raisedByName;

    /** Assignee user id. */
    private Long assigneeId;

    /** Assignee user name. */
    private String assigneeName;

    /** Priority (LOW, MEDIUM, HIGH, CRITICAL). */
    private String priority;

    /** Target resolution date. */
    private LocalDate targetResolveDate;

    /** Status (OPEN, IN_PROGRESS, RESOLVED, CLOSED). */
    private String status;

    /** Source risk id (nullable, if from risk). */
    private Long sourceRiskId;

    /** Source risk number (nullable, if from risk). */
    private String sourceRiskNo;

    /** Source change request id (nullable, if from change). */
    private Long sourceChangeId;

    /** Source change request number (nullable, if from change). */
    private String sourceCrNo;

    /** Resolution time. */
    private LocalDateTime resolvedAt;

    /** Closure time. */
    private LocalDateTime closedAt;

    /** Resolution description (TEXT). */
    private String resolution;
}
