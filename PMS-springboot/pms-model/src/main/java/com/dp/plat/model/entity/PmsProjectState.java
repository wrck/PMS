package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 项目状态实体 - 对应 pm_project_state 表
 */
@Data
@TableName("pm_project_state")
public class PmsProjectState extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("projectPlanState")
    private String projectPlanState;

    @TableField("executionState")
    private String executionState;

    @TableField("closeProcessState")
    private String closeProcessState;
}
