package com.dp.plat.project.punchlist.entity;

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
 * Punch list defect entity.
 *
 * <p>Records defects/issues discovered during the walkdown phase of a project milestone.
 * Safety-severity items block the related milestone until resolved and verified.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_punch_list")
public class PunchList extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Milestone id the punch list item is associated with. */
    private Long milestoneId;

    /** Severity (SAFETY, FUNCTIONAL, COSMETIC). */
    private String severity;

    /** Defect title. */
    private String title;

    /** Defect description. */
    private String description;

    /** Walkdown stage (PRE_PUNCH, FORMAL). */
    private String walkdownStage;

    /** Assignee user id. */
    private Long assigneeId;

    /** Assignee user name. */
    private String assigneeName;

    /** Deadline for resolution. */
    private LocalDate deadline;

    /** Status (OPEN, RESOLVED, VERIFIED). */
    private String status;

    /** Resolution time. */
    private LocalDateTime resolvedAt;

    /** Verification time. */
    private LocalDateTime verifiedAt;

    /** Verifier user id. */
    private Long verifiedBy;

    /** Verifier user name. */
    private String verifiedByName;

    /** Comma-separated attachment ids (reserved for future attachment integration). */
    private String attachmentIds;
}
