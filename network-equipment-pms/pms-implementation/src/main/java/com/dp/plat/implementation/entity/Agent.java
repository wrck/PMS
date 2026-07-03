package com.dp.plat.implementation.entity;

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
 * Agent / partner company entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_agent")
public class Agent extends BaseEntity {

    private String agentName;

    private String agentCode;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String qualification;

    /** 1=enabled, 0=disabled. */
    private Integer status;

    /** Overall score 0-10 (average of all evaluations). */
    private BigDecimal overallScore;

    /** Certification tier: SELECT/PREMIER/SILVER/GOLD. */
    private String certLevel;

    /** Number of CCIE-certified engineers. */
    private Integer ccieCount;

    /** Specializations (JSON array stored as text). */
    private String specializations;

    private LocalDate certExpiryDate;
}
