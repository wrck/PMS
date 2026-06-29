package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 分包设备行 - 对应 pm_subcontract_line 表
 */
@Data
@TableName("pm_subcontract_line")
public class PmsSubcontractLine extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 分包项目ID */
    @TableField("subcontractId")
    private Long subcontractId;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 序列号 */
    @TableField("barCode")
    private String barCode;

    /** 物料编码 */
    @TableField("itemCode")
    private String itemCode;

    /** 物料型号 */
    @TableField("itemModel")
    private String itemModel;

    /** 物料名称 */
    @TableField("itemName")
    private String itemName;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;
}
