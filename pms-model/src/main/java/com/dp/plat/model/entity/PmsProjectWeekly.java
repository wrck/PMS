package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目周报实体 - 对应老系统 pm_project_weekly 表
 */
@Data
@TableName("pm_project_weekly")
public class PmsProjectWeekly extends BaseEntity {

    @TableId(value = "weeklyId", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 周报日期 */
    @TableField("weeklyDate")
    private LocalDateTime weeklyDate;

    /** 周报内容（JSON格式） */
    @TableField("content")
    private String content;

    /** 状态: 1=已提交, 0=草稿 */
    @TableField("status")
    private Integer status;

    /** 创建人 */
    @TableField("createBy")
    private String createBy;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
