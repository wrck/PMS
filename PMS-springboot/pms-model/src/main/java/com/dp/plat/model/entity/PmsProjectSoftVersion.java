package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目软件版本实体 - 对应老系统 ProjectSoftVersion
 * 继承自发货信息，记录设备的软件版本详情
 */
@Data
@TableName("pm_project_soft_version")
public class PmsProjectSoftVersion extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 发货信息ID */
    @TableField("shipmentId")
    private Long shipmentId;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

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

    /** 变更记录ID */
    @TableField("logId")
    private Long logId;

    /** 数据状态 0=失效 1=有效 */
    @TableField("datastate")
    private Integer datastate;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 市场编码 */
    @TableField("marketCode")
    private String marketCode;

    /** 系统编码 */
    @TableField("systemCode")
    private String systemCode;

    /** 扩展编码 */
    @TableField("expendCode")
    private String expendCode;

    /** 行业编码 */
    @TableField("industryCode")
    private String industryCode;
}
