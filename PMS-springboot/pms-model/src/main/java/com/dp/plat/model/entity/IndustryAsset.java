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
 * IndustryAsset entity - migrated from Struts
 */
@Data
@TableName("pm_industry_asset")
public class IndustryAsset extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("assetCode")
    private String assetCode;

    @TableField("assetName")
    private String assetName;

    @TableField("assetType")
    private String assetType;

    @TableField("assetDesc")
    private String assetDesc;

}