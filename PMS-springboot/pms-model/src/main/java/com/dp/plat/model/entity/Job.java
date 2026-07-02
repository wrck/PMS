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
 * Job entity - migrated from Struts
 */
@Data
@TableName("ehr_job")
public class Job extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("jobCode")
    private String jobCode;

    @TableField("jobName")
    private String jobName;

    @TableField("jobDesc")
    private String jobDesc;

    @TableField("status")
    private Integer status;

}