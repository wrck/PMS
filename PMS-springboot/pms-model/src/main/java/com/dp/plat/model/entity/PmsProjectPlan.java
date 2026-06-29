package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程计划实体 - 对应老系统 ProjectPlan
 */
@Data
@TableName("pm_project_plan")
public class PmsProjectPlan extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 批次编码 */
    @TableField("batchCode")
    private String batchCode;

    /** 基础数据名称 */
    @TableField("basicDataName")
    private String basicDataName;

    /** 引用事件名称 */
    @TableField("referenceEventName")
    private String referenceEventName;

    /** 计划发生日期 */
    @TableField("eventPlanHappenDate")
    private LocalDateTime eventPlanHappenDate;

    /** 间隔天数 */
    @TableField("afterDaysNum")
    private Integer afterDaysNum;

    /** 实际完成日期 */
    @TableField("eventActualFinishDate")
    private LocalDateTime eventActualFinishDate;

    /** 营销反馈 */
    @TableField("marketingFeedback")
    private String marketingFeedback;

    /** 附件 */
    @TableField("attachment")
    private String attachment;
}
