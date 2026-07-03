package com.dp.plat.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Equipment category entity (tree structure).
 * status: 1=active, 0=disabled.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset_category")
public class AssetCategory extends BaseEntity {

    private Long parentId;

    private String categoryName;

    private String categoryCode;

    private Integer sortOrder;

    /** 1=active, 0=disabled. */
    private Integer status;

    /** Child categories, not persisted. */
    @TableField(exist = false)
    private List<AssetCategory> children;
}
