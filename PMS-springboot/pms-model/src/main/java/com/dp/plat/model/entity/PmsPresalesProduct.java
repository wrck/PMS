package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 售前产品实体 - 对应 pm_presales_product 表
 */
@Data
@TableName("pm_presales_product")
public class PmsPresalesProduct extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 售前项目ID */
    @TableField("presalesId")
    private Long presalesId;

    /** 物料编码 */
    @TableField("itemCode")
    private String itemCode;

    /** 物料名称 */
    @TableField("itemName")
    private String itemName;

    /** 型号 */
    @TableField("model")
    private String model;

    /** 数量 */
    @TableField("quantity")
    private Integer quantity;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
