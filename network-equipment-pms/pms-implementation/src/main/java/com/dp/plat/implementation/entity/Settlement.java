package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agent settlement entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_settlement")
public class Settlement extends BaseEntity {

    private Long taskId;

    private Long agentId;

    private Long projectId;

    private String settlementNo;

    private BigDecimal totalAmount;

    /** Tax rate (%), default 13.00. */
    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal totalWithTax;

    /** PENDING, APPROVED, REJECTED, PUSHED. */
    private String status;

    private Long applyUserId;

    private String applyUserName;

    private LocalDateTime applyTime;

    private Long approveUserId;

    private String approveUserName;

    private LocalDateTime approveTime;

    private String approveOpinion;

    /** Push status: NULL, SUCCESS, FAILED. */
    private String pushStatus;

    private LocalDateTime pushTime;

    private String pushResponse;

    /** Workflow process instance id for the settlement approval flow. */
    private String processInstanceId;
}
