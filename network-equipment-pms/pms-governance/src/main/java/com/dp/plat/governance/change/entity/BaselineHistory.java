package com.dp.plat.governance.change.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Baseline history entity.
 *
 * <p>Audit trail of project baseline changes triggered by approved change
 * requests. Each record captures a single field-level change (schedule, cost
 * or scope).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_baseline_history")
public class BaselineHistory extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Change request id that triggered the baseline change. */
    private Long changeRequestId;

    /** Change request number (denormalized for traceability). */
    private String crNo;

    /** Change type (SCHEDULE, COST, SCOPE). */
    private String changeType;

    /** Field name that was changed. */
    private String fieldName;

    /** Old value before the change. */
    private String oldValue;

    /** New value after the change. */
    private String newValue;

    /** Description of the change. */
    private String description;

    /** Time the baseline was changed. */
    private LocalDateTime changedAt;

    /** User who performed the change. */
    private String changedBy;
}
