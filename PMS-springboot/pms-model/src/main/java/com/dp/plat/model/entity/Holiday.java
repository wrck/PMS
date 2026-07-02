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
 * Holiday entity - migrated from Struts
 */
@Data
@TableName("ehr_holiday")
public class Holiday extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("holidayName")
    private String holidayName;

    @TableField("holidayDate")
    private LocalDateTime holidayDate;

    @TableField("holidayType")
    private String holidayType;

}