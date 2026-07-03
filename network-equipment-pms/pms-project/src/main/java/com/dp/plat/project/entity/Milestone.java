package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Project milestone entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_milestone")
public class Milestone extends BaseEntity {

    /** Project id. */
    private Long projectId;

    /** Milestone name. */
    private String milestoneName;

    /** Milestone type (ARRIVAL, INSTALL, DEBUG, INITIAL_ACCEPTANCE, FINAL_ACCEPTANCE). */
    private String milestoneType;

    /** Planned date. */
    private LocalDate planDate;

    /** Actual date. */
    private LocalDate actualDate;

    /** Status (PENDING, IN_PROGRESS, COMPLETED, OVERDUE). */
    private String status;

    /** Description. */
    private String description;

    /** Sort order. */
    private Integer sortOrder;
}
