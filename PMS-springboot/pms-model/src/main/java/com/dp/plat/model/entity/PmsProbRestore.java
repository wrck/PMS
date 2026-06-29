package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技术公告恢复任务 - 对应 pm_prob_restore 表
 * 记录受影响的项目设备需要执行的恢复操作
 */
@Data
@TableName("pm_prob_restore")
public class PmsProbRestore extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 技术公告ID */
    @TableField("probId")
    private Long probId;

    /** 流程ID */
    @TableField("processId")
    private Long processId;

    /** 序列号 */
    @TableField("serialNum")
    private String serialNum;

    /** 物料型号 */
    @TableField("itemModel")
    private String itemModel;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 办事处名称 */
    @TableField("officeName")
    private String officeName;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 恢复状态 (关联fnd_basic_data dataTypeCode=33) */
    @TableField("restoreStatus")
    private Integer restoreStatus;

    /** 恢复状态名称 */
    @TableField(exist = false)
    private String restoreStatusName;

    /** 恢复备注 */
    @TableField("restoreRemark")
    private String restoreRemark;

    /** 当前App版本 */
    @TableField("conp")
    private String conp;

    /** 当前CPLD版本 */
    @TableField("cpld")
    private String cpld;

    /** 当前Boot版本 */
    @TableField("boot")
    private String boot;

    /** 当前PCB版本 */
    @TableField("pcb")
    private String pcb;

    /** 最新App版本 */
    @TableField("latestConp")
    private String latestConp;

    /** 最新CPLD版本 */
    @TableField("latestCpld")
    private String latestCpld;

    /** 最新Boot版本 */
    @TableField("latestBoot")
    private String latestBoot;

    /** 最新PCB版本 */
    @TableField("latestPcb")
    private String latestPcb;

    /** 执行时间 */
    @TableField("executeTime")
    private LocalDateTime executeTime;

    /** 责任人 */
    @TableField("assignee")
    private String assignee;

    /** 责任人角色 */
    @TableField("assigneeRole")
    private Integer assigneeRole;

    /** 是否已确认 */
    @TableField("ischecked")
    private Integer ischecked;
}
