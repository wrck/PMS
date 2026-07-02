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
 * PmClCBData entity - migrated from Struts
 */
@Data
@TableName("pm_cl_cb_data")
public class PmClCBData extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("cbPerson")
    private String cbPerson;

    @TableField("cbResult")
    private String cbResult;

    @TableField("cbTime")
    private LocalDateTime cbTime;

    @TableField("cbStatus")
    private String cbStatus;

}