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
 * CommonRelatedData entity - migrated from Struts
 */
@Data
@TableName("pm_common_related_data")
public class CommonRelatedData extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("dataType")
    private String dataType;

    @TableField("dataKey")
    private String dataKey;

    @TableField("dataValue")
    private String dataValue;

    @TableField("relatedType")
    private String relatedType;

    @TableField("relatedId")
    private Long relatedId;

}