package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 技术公告受影响软件版本 - 对应 pm_prob_soft_version 表
 */
@Data
@TableName("pm_prob_soft_version")
public class PmsProbSoftVersion extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 技术公告ID */
    @TableField("probId")
    private Long probId;

    /** App版本 */
    @TableField("conp")
    private String conp;

    /** App版本条件 (eq/like/between等) */
    @TableField("conpCondition")
    private String conpCondition;

    /** App版本掩码 */
    @TableField("conpMark")
    private String conpMark;

    /** CPLD版本 */
    @TableField("cpld")
    private String cpld;

    /** CPLD条件 */
    @TableField("cpldCondition")
    private String cpldCondition;

    /** Boot版本 */
    @TableField("boot")
    private String boot;

    /** Boot条件 */
    @TableField("bootCondition")
    private String bootCondition;

    /** PCB版本 */
    @TableField("pcb")
    private String pcb;

    /** PCB条件 */
    @TableField("pcbCondition")
    private String pcbCondition;

    /** 手动录入标识 */
    @TableField("manualEntry")
    private String manualEntry;

    /** 录入类型 */
    @TableField("entryType")
    private String entryType;

    /** 录入系列 */
    @TableField("entrySeries")
    private String entrySeries;

    /** 录入起始 */
    @TableField("entryStart")
    private String entryStart;

    /** 录入结束 */
    @TableField("entryEnd")
    private String entryEnd;

    /** 受影响类型 */
    @TableField("affectedType")
    private Integer affectedType;

    /** 平台类型 */
    @TableField("platformType")
    private String platformType;

    /** 发布类型 */
    @TableField("releaseType")
    private String releaseType;

    /** 架构类型 */
    @TableField("architectureType")
    private String architectureType;

    /** 分支类型 */
    @TableField("branchType")
    private String branchType;

    /** 分组ID */
    @TableField("groupId")
    private Long groupId;

    /** 是否已拆分 (1=是 0=否) */
    @TableField("splited")
    private Integer splited;
}
