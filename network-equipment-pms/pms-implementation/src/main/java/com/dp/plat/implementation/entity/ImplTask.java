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
}
