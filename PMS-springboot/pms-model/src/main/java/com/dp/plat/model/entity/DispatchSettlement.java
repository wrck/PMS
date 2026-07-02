package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DispatchSettlement entity - migrated from Struts
 */
@Data
@TableName("pm_dispatch_settlement")
public class DispatchSettlement extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("dispatchId")
    private Long dispatchId;

    @TableField("settlementCode")
    private String settlementCode;

    @TableField("settlementStatus")
    private String settlementStatus;

    @TableField("settlementAmount")
    private java.math.BigDecimal settlementAmount;

    @TableField("settlementTime")
    private LocalDateTime settlementTime;

}