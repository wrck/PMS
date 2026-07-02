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
 * IndustryAssetProjectRelation entity - migrated from Struts
 */
@Data
@TableName("pm_industry_asset_project_relation")
public class IndustryAssetProjectRelation extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("assetId")
    private Long assetId;

    @TableField("projectId")
    private Long projectId;

}