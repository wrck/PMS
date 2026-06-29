package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售前任务实体 - 对应 pm_presales_task 表
 */
@Data
@TableName("pm_presales_task")
public class PmsPresalesTask extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 售前项目ID */
    @TableField("presalesId")
    private Long presalesId;

    /** 任务类型 */
    @TableField("taskType")
    private String taskType;

    /** 任务名称 */
    @TableField("taskName")
    private String taskName;

    /** 责任人 */
    @TableField("assignee")
    private String assignee;

    /** 任务状态 */
    @TableField("status")
    private Integer status;

    /** 完成时间 */
    @TableField("finishTime")
    private LocalDateTime finishTime;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 交付件文件ID */
    @TableField("deliverFileIds")
    private String deliverFileIds;
}
