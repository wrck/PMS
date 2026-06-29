package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分包付款 - 对应 pm_subcontract_payment 表
 */
@Data
@TableName("pm_subcontract_payment")
public class PmsSubcontractPayment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 分包项目ID */
    @TableField("subcontractId")
    private Long subcontractId;

    /** 付款比例 */
    @TableField("ratio")
    private String ratio;

    /** 付款金额 */
    @TableField("amount")
    private String amount;

    /** 确认时间 */
    @TableField("confirmTime")
    private LocalDateTime confirmTime;

    /** 付款时间 */
    @TableField("paymentTime")
    private LocalDateTime paymentTime;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** SSE ID */
    @TableField("sseId")
    private Long sseId;
}
