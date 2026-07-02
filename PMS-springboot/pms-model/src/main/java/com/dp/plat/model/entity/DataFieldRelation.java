package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DataFieldRelation entity - migrated from Struts
 */
@Data
@TableName("pm_data_field_relation")
public class DataFieldRelation extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("sourceField")
    private String sourceField;

    @TableField("targetField")
    private String targetField;

    @TableField("relationType")
    private String relationType;

}