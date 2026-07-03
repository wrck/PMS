package com.dp.plat.implementation.entity;

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
 * Implementation task entity (OEM or agent).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_impl_task")
public class ImplTask extends BaseEntity {

    private Long projectId;

    private Long milestoneId;

    private String taskName;

    /** OEM=原厂实施, AGENT=代理商实施. */
    private String taskType;

    /** Agent id (for AGENT type). */
    private Long agentId;

    /** OEM engineer user id (for OEM type). */
    private Long engineerId;

    private String engineerName;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    /** PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CONFIRMED, REJECTED. */
    private String status;

    /** Progress percent 0-100. */
    private Integer progress;

    private String workDescription;

    private String acceptOpinion;

    private Long acceptUserId;

    private String acceptUserName;

    private LocalDateTime acceptTime;

    /** Service execution fields. */
    private String customerContact;

    private String serviceAddress;

    /** SITE_SURVEY/INSTALL/DEBUG/MAINTENANCE. */
    private String serviceType;

    /** Standard operating procedure steps (long text). */
    private String sopSteps;

    /** Required materials list (long text). */
    private String materialList;

    private Integer plannedHours;

    /** Required skill level: JUNIOR/SENIOR/EXPERT. */
    private String skillLevel;

    /** Safety requirements: PPE/LOTO/PERMIT. */
    private String safetyPpe;

    /** Evidence checkpoint definition (long text). */
    private String evidenceCheckpoints;

    /** Whether a formal sign-off is required (default true). */
    @Builder.Default
    private Boolean signOffRequired = true;
}
