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
 * DispatchProject entity - migrated from Struts
 */
@Data
@TableName("pm_dispatch_project")
public class DispatchProject extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("dispatchCode")
    private String dispatchCode;

    @TableField("dispatchStatus")
    private String dispatchStatus;

    @TableField("dispatchPerson")
    private String dispatchPerson;

    @TableField("dispatchTime")
    private LocalDateTime dispatchTime;

}