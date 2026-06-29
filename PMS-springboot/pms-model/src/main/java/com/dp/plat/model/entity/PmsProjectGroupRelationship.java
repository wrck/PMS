package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 项目组关系实体 - 对应 pm_project_group_relationship 表
 */
@Data
@TableName("pm_project_group_relationship")
public class PmsProjectGroupRelationship extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目组编码 */
    @TableField("projectGroupCode")
    private String projectGroupCode;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** SMS项目编码 */
    @TableField("smsProjectCode")
    private String smsProjectCode;

    /** 合并拆分标记 */
    @TableField("mergeBranchMark")
    private String mergeBranchMark;
}
