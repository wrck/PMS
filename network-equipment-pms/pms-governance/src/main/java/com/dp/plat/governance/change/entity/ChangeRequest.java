package com.dp.plat.governance.change.entity;

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
 * Change request entity (governance three-books: change request book).
 *
 * <p>Tracks formal change requests raised against a project, including impact
 * assessment, CCB (Change Control Board) review outcome and baseline updates.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_change_request")
public class ChangeRequest extends BaseEntity {

    /** Change request number (CR-YYYY-XXXX). */
    private String crNo;

    /** Project id. */
    private Long projectId;

    /** Project name (denormalized for display). */
    private String projectName;

    /** Change title. */
    private String title;

    /** Change description. */
    private String description;

    /** Requester user id. */
    private Long requesterId;

    /** Requester user name. */
    private String requesterName;

    /** Request date. */
    private LocalDate requestDate;

    /** Impact scope (TEXT). */
    private String impactScope;

    /** Impact on schedule. */
    private String impactSchedule;

    /** Impact on cost. */
    private String impactCost;

    /** Impact on quality. */
    private String impactQuality;

    /** Priority (LOW, MEDIUM, HIGH, CRITICAL). */
    private String priority;

    /** Status (SUBMITTED, UNDER_REVIEW, CCB_APPROVED, CCB_REJECTED, IMPLEMENTING, CLOSED). */
    private String status;

    /** Approver user id. */
    private Long approverId;

    /** Approver user name. */
    private String approverName;

    /** Workflow process instance id. */
    private String processInstanceId;

    /** Whether the project baseline has been updated (default false). */
    @Builder.Default
    private Boolean baselineUpdated = false;

    /** Approval time. */
    private LocalDateTime approvedAt;

    /** Closure time. */
    private LocalDateTime closedAt;
}
