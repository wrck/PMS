package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SAP订单数据实体 - 对应老系统 OrderDataFromSap
 * 记录项目的产品清单和发货数量
 */
@Data
@TableName("pm_order_data_from_sap")
public class PmsOrderData extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 退货合同号 */
    @TableField("rmaContractNo")
    private String rmaContractNo;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 物料编码 */
    @TableField("itemCode")
    private String itemCode;

    /** 物料名称 */
    @TableField("itemName")
    private String itemName;

    /** 型号 */
    @TableField("model")
    private String model;

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

    /** 订单号 */
    @TableField("orderNumber")
    private String orderNumber;

    /** 行号 */
    @TableField("lineNum")
    private String lineNum;

    /** 条码 */
    @TableField("barcode")
    private String barcode;

    /** 收货人 */
    @TableField("receiveName")
    private String receiveName;

    /** 快递单号 */
    @TableField("emsNum")
    private String emsNum;

    /** 快递公司 */
    @TableField("emsCompany")
    private String emsCompany;

    /** 包装日期 */
    @TableField("packdate")
    private LocalDateTime packdate;
}
