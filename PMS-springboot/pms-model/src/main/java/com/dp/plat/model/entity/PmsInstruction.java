package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目批示实体 - 对应 pm_project_instruction 表
 */
@Data
@TableName("pm_project_instruction")
public class PmsInstruction extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 批示内容 */
    @TableField("instructionsInfo")
    private String instructionsInfo;

    /** 批示时间 */
    @TableField("instructionsTime")
    private LocalDateTime instructionsTime;

    /** 批示人 */
    @TableField("instructionsUser")
    private String instructionsUser;

    /** 数据类型: 0=批示, 1=反馈 */
    @TableField("dataType")
    private Integer dataType;

    /** 回复的批示ID (dataType=1时有效) */
    @TableField("instructionsId")
    private Long instructionsId;
}
