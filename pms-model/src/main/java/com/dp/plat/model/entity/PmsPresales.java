package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售前项目实体 - 对应老系统 pm_presales 表
 */
@Data
@TableName("pm_presales")
public class PmsPresales extends BaseEntity {

    @TableId(value = "presalesId", type = IdType.AUTO)
    private Long id;

    /** 售前编码 */
    @TableField("presalesCode")
    private String presalesCode;

    /** 售前名称 */
    @TableField("presalesName")
    private String presalesName;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 项目类型 */
    @TableField("projectType")
    private Integer projectType;

    /** 状态 */
    @TableField("state")
    private Integer state;

    /** 服务经理编码 */
    @TableField("smCode")
    private String smCode;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
