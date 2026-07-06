package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "代理商ID不能为空")
    private Long agentId;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "结算单号不能为空")
    @Size(max = 50, message = "结算单号长度不能超过 50 个字符")
    private String settlementNo;

    @NotNull(message = "结算总金额不能为空")
    @DecimalMin(value = "0", message = "结算总金额不能为负数")
    private BigDecimal totalAmount;

    /** Tax rate (%), default 13.00. */
    @DecimalMin(value = "0", message = "税率不能为负数")
    private BigDecimal taxRate;

    @DecimalMin(value = "0", message = "税额不能为负数")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0", message = "含税总额不能为负数")
    private BigDecimal totalWithTax;

    /** PENDING, APPROVED, REJECTED, PUSHED. */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    private Long applyUserId;

    @Size(max = 50, message = "申请人名称长度不能超过 50 个字符")
    private String applyUserName;

    private LocalDateTime applyTime;

    private Long approveUserId;

    @Size(max = 50, message = "审批人名称长度不能超过 50 个字符")
    private String approveUserName;

    private LocalDateTime approveTime;

    @Size(max = 500, message = "审批意见长度不能超过 500 个字符")
    private String approveOpinion;

    /** Push status: NULL, SUCCESS, FAILED. */
    @Size(max = 20, message = "推送状态长度不能超过 20 个字符")
    private String pushStatus;

    private LocalDateTime pushTime;

    private String pushResponse;

    /** Workflow process instance id for the settlement approval flow. */
    private String processInstanceId;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）. */
    @Version
    private Integer version;
}
