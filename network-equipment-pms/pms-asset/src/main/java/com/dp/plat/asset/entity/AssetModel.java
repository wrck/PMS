package com.dp.plat.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Equipment model entity belonging to a category.
 * status: 1=active, 0=disabled.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset_model")
public class AssetModel extends BaseEntity {

    private Long categoryId;

    private String modelName;

    private String modelCode;

    private String brand;

    /** JSON string of specifications. */
    private String specParams;

    private BigDecimal standardPrice;

    /** Unit, e.g. 台/套/个. */
    private String unit;

    /** 1=active, 0=disabled. */
    private Integer status;
}
