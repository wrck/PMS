package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础数据实体 - 对应老系统 fnd_basic_data 表
 */
@Data
@TableName("fnd_basic_data")
public class SysBasicData extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 数据类型编码 */
    @TableField("dataTypeCode")
    private String dataType;

    /** 数据编码 */
    @TableField("basicDataCode")
    private String dataCode;

    /** 数据名称 */
    @TableField("basicDataName")
    private String dataName;

    /** 排序 */
    @TableField("sort")
    private Integer sort;

    /** 状态: 1=启用, 0=禁用 */
    @TableField("status")
    private Integer status;

    /** 创建时间 */
    @TableField("createTime")
    private LocalDateTime createTime;
}
