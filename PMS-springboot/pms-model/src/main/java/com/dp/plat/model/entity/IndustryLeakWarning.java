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
 * IndustryLeakWarning entity - migrated from Struts
 */
@Data
@TableName("pm_industry_leak_warning")
public class IndustryLeakWarning extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("leakId")
    private Long leakId;

    @TableField("warningLevel")
    private String warningLevel;

    @TableField("warningContent")
    private String warningContent;

    @TableField("warningTime")
    private LocalDateTime warningTime;

}