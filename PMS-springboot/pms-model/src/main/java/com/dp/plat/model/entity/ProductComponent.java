package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 产品组件实体 - 对应老系统 ProductComponent (11字段)
 * 对应表: prob_product_component
 */
@Data
@TableName("prob_product_component")
public class ProductComponent extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("component_code") private String componentCode;
    @TableField("component_name") private String componentName;
    @TableField("component_model") private String componentModel;
    @TableField("component_desc") private String componentDesc;
    @TableField("product_code") private String productCode;
    @TableField("status") private Integer status;
}
