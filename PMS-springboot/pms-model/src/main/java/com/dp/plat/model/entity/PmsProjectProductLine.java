package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 项目产品线实体 - 对应 pm_project_product_line 表
 */
@Data
@TableName("pm_project_product_line")
public class PmsProjectProductLine extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 物料编码 */
    @TableField("itemCode")
    private String itemCode;

    /** 物料名称 */
    @TableField("itemName")
    private String itemName;

    /** 项目数量 */
    @TableField("projectQuantity")
    private Integer projectQuantity;

    /** 订单数量 */
    @TableField("orderQuantity")
    private Integer orderQuantity;

    /** 发货数量 */
    @TableField("deliverQuantity")
    private Integer deliverQuantity;

    /** 未清数量 */
    @TableField("openQuantity")
    private Integer openQuantity;
}
