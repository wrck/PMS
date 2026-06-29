package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 项目合同实体 - 对应 pm_project_contract 表
 */
@Data
@TableName("pm_project_contract")
public class PmsProjectContract extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 项目组编码 */
    @TableField("projectGroupCode")
    private String projectGroupCode;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;
}
