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
 * IndustryLeak entity - migrated from Struts
 */
@Data
@TableName("pm_industry_leak")
public class IndustryLeak extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("leakCode")
    private String leakCode;

    @TableField("leakName")
    private String leakName;

    @TableField("leakDesc")
    private String leakDesc;

    @TableField("leakLevel")
    private String leakLevel;

}