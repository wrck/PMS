package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 软件版本变更记录实体 - 对应老系统 SoftChangeLog
 */
@Data
@TableName("pm_soft_change_log")
public class PmsSoftChangeLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 软件变更版本 */
    @TableField("changeVersion")
    private String changeVersion;

    /** 变更说明 */
    @TableField("changeRemark")
    private String changeRemark;

    /** 是否为最新版本 0=否 1=是 */
    @TableField("latest")
    private Integer latest;
}
