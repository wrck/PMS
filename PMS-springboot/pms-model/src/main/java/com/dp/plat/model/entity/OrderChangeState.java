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
 * OrderChangeState entity - migrated from Struts
 */
@Data
@TableName("pm_order_change_state")
public class OrderChangeState extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("orderNumber")
    private String orderNumber;

    @TableField("changeType")
    private String changeType;

    @TableField("oldState")
    private String oldState;

    @TableField("newState")
    private String newState;

    @TableField("changeTime")
    private LocalDateTime changeTime;

    @TableField("changePerson")
    private String changePerson;

}