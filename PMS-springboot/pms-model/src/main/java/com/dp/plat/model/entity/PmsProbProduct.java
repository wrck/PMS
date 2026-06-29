package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 技术公告产品 - 对应 pm_prob_product 表
 */
@Data
@TableName("pm_prob_product")
public class PmsProbProduct extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 技术公告ID */
    @TableField("probId")
    private Long probId;

    /** 产品型号 */
    @TableField("productModel")
    private String productModel;

    /** 产品名称 */
    @TableField("productName")
    private String productName;

    /** 物料编码 */
    @TableField("itemCode")
    private String itemCode;

    /** 物料名称 */
    @TableField("itemName")
    private String itemName;

    /** 状态 (1=有效 0=无效) */
    @TableField("status")
    private Integer status;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
