package com.dp.plat.project.deliverable.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Final acceptance deliverable checklist entity.
 *
 * <p>Tracks the mandatory deliverables that must be uploaded before a project can
 * apply for final acceptance. Each project is initialised with 8 standard
 * deliverable records via {@code initChecklist}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable_checklist")
public class DeliverableChecklist extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Deliverable type (AS_BUILT, TEST_REPORT, ACCEPTANCE_CERT, TRAINING_RECORD,
     * OPERATION_MANUAL, ASSET_REGISTER, WARRANTY_CERT, SPARE_PARTS_LIST). */
    private String deliverableType;

    /** Whether the deliverable is mandatory for final acceptance. */
    private Boolean required;

    /** Whether the deliverable has been uploaded. */
    private Boolean uploaded;

    /** Attachment id (reserved for future attachment integration). */
    private Long attachmentId;

    /** Time the checklist record was last checked/updated. */
    private LocalDateTime checkedAt;

    /** User who last checked/updated the record. */
    private String checkedBy;
}
