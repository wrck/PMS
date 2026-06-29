package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发货信息实体 - 对应老系统 ShipmentInfo
 * 记录项目设备发货的序列号、安装地址、软件版本等信息
 */
@Data
@TableName("pm_shipment_info")
public class PmsShipmentInfo extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 序列号/条码 */
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

    /** 安装地址 */
    @TableField("installAddress")
    private String installAddress;

    /** 硬件版本 (PCB) */
    @TableField("pcb")
    private String pcb;

    /** 驱动版本 (CPLD) */
    @TableField("cpld")
    private String cpld;

    /** Boot版本 */
    @TableField("boot")
    private String boot;

    /** App版本 (CONP) */
    @TableField("conp")
    private String conp;

    /** App版本类型 */
    @TableField("conpType")
    private String conpType;

    /** App版本系列 */
    @TableField("conpSeries")
    private String conpSeries;

    /** App版本掩码 */
    @TableField("conpMark")
    private String conpMark;

    /** 项目执行更新时间 */
    @TableField("executeTime")
    private LocalDateTime executeTime;

    /** 软件版本是否更新 0=未更新 1=已更新 */
    @TableField("conpChange")
    private Integer conpChange = 0;

    /** CPLD是否更新 */
    @TableField("cpldChange")
    private Integer cpldChange = 0;

    /** Boot是否更新 */
    @TableField("bootChange")
    private Integer bootChange = 0;

    /** PCB是否更新 */
    @TableField("pcbChange")
    private Integer pcbChange = 0;

    /** 备份变更之前的App版本 */
    @TableField("conpBak")
    private String conpBak;

    /** 备份变更之前的CPLD版本 */
    @TableField("cpldBak")
    private String cpldBak;

    /** 备份变更之前的Boot版本 */
    @TableField("bootBak")
    private String bootBak;

    /** 备份变更之前的PCB版本 */
    @TableField("pcbBak")
    private String pcbBak;

    /** 串货项目ID */
    @TableField("chProjectId")
    private Long chProjectId;

    /** 串货项目合同号 */
    @TableField("chContractNo")
    private String chContractNo;

    /** 转出项目ID */
    @TableField("transferProjectId")
    private Long transferProjectId;

    /** 转出项目合同号 */
    @TableField("transferContractNo")
    private String transferContractNo;

    /** 转移标识: -1=默认, 1=转出, 0=转入 */
    @TableField("transferFlag")
    private String transferFlag;

    /** 退换货标识 */
    @TableField("rmaNo")
    private String rmaNo;

    /** 母子公司发货条形码2 */
    @TableField("barCode2")
    private String barCode2;

    /** 物料编码2 */
    @TableField("itemCode2")
    private String itemCode2;

    /** 物料型号2 */
    @TableField("itemModel2")
    private String itemModel2;

    /** 物料名称2 */
    @TableField("itemName2")
    private String itemName2;
}
