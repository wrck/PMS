package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 回访实体 - 对应老系统 pm_callback_header 表
 */
@Data
@TableName("pm_callback_header")
public class PmsCallBack extends BaseEntity {
    @TableId(value = "callBackId", type = IdType.AUTO)
    private Long id;
    @TableField("projectId")
    private Long projectId;
    @TableField("projectCode")
    private String projectCode;
    @TableField("projectName")
    private String projectName;
    @TableField("officeCode")
    private String officeCode;
    @TableField("applyState")
    private Integer applyState;
    @TableField("applyBy")
    private String applyBy;
    @TableField("applyTime")
    private LocalDateTime applyTime;
    @TableField("endTime")
    private LocalDateTime endTime;
    @TableField("instId")
    private String instId;
    @TableField("taskDefKey")
    private String taskDefKey;
    @TableField("quesnaireId")
    private Long quesnaireId;
    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String officeName;
    @TableField(exist = false)
    private String serviceManagerName;
    @TableField(exist = false)
    private String projectManagerName;
}
