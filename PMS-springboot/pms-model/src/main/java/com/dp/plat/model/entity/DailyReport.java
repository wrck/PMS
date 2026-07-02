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
 * DailyReport entity - migrated from Struts
 */
@Data
@TableName("pm_daily_report")
public class DailyReport extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("reportPerson")
    private String reportPerson;

    @TableField("reportContent")
    private String reportContent;

    @TableField("reportDate")
    private LocalDateTime reportDate;

    @TableField("reportStatus")
    private String reportStatus;

}