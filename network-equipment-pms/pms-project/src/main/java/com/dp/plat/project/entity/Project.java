package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Project main entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project")
public class Project extends BaseEntity {

    /** Project code (PMS-YYYY-XXXX). */
    private String projectCode;

    /** Project name. */
    private String projectName;

    /** Project type (NETWORK_DEVICE, SECURITY, DATACENTER, etc.). */
    private String projectType;

    /** Status (PENDING, APPROVED, IN_PROGRESS, INITIAL_ACCEPTANCE, FINAL_ACCEPTANCE, COMPLETED, CLOSED, REJECTED). */
    private String status;

    /** Customer name. */
    private String customerName;

    /** Customer contact. */
    private String customerContact;

    /** Customer phone. */
    private String customerPhone;

    /** Contract number. */
    private String contractNo;

    /** Contract amount. */
    private BigDecimal contractAmount;

    /** Planned start date. */
    private LocalDate planStartDate;

    /** Planned end date. */
    private LocalDate planEndDate;

    /** Actual start date. */
    private LocalDate actualStartDate;

    /** Actual end date. */
    private LocalDate actualEndDate;

    /** Project manager user id. */
    private Long projectManagerId;

    /** Project manager name. */
    private String projectManagerName;

    /** Project description. */
    private String description;

    /** Progress percentage 0-100. */
    private Integer progress;

    /** Priority (HIGH, NORMAL, LOW). */
    private String priority;

    /** Workflow process instance id for the project approval flow. */
    private String processInstanceId;
}
