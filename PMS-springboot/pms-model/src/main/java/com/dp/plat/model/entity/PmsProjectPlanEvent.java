package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程计划事件节点实体 - 对应老系统 ProjectPlanEvent
 */
@Data
@TableName("pm_project_plan_event")
public class PmsProjectPlanEvent extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 数据类型编码 */
    @TableField("dataTypeCode")
    private String dataTypeCode;

    /** 基础数据ID */
    @TableField("basicDataId")
    private String basicDataId;

    /** 事件Key (dataTypeCode-basicDataId) */
    @TableField("eventKey")
    private String eventKey;

    /** 事件值 */
    @TableField("eventValue")
    private String eventValue;

    /** 计划发生日期 */
    @TableField("eventPlanHappenDate")
    private LocalDateTime eventPlanHappenDate;

    /** 实际完成日期 */
    @TableField("eventActualFinishDate")
    private LocalDateTime eventActualFinishDate;

    /** 扩展字段10 */
    @TableField("column010")
    private String column010;

    /** 扩展字段11 */
    @TableField("column011")
    private String column011;
}
