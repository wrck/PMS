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
 * RunTask entity - migrated from Struts
 */
@Data
@TableName("fnd_run_task")
public class RunTask extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("taskName")
    private String taskName;

    @TableField("taskType")
    private String taskType;

    @TableField("taskStatus")
    private String taskStatus;

    @TableField("assignee")
    private String assignee;

    @TableField("processInstanceId")
    private String processInstanceId;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("dueDate")
    private LocalDateTime dueDate;

}