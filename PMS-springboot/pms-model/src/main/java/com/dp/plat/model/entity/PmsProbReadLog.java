package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技术公告阅读日志 - 对应 pm_prob_read_log 表
 */
@Data
@TableName("pm_prob_read_log")
public class PmsProbReadLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 技术公告ID */
    @TableField("probId")
    private Long probId;

    /** 阅读人 */
    @TableField("reader")
    private String reader;

    /** 阅读状态 (0=未读 1=已读) */
    @TableField("readStatus")
    private Integer readStatus;

    /** 阅读时间 */
    @TableField("readTime")
    private LocalDateTime readTime;
}
