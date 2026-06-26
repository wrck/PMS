package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目实体 - 对应老系统 tmp_tb_project 视图/表
 */
@Data
@TableName("tmp_tb_project")
public class PmsProject extends BaseEntity {

    @TableId(value = "projectId", type = IdType.AUTO)
    private Long id;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 合同号 */
    @TableField("contractNo")
    private String contractNo;

    /** 公司ID */
    @TableField("compId")
    private Long companyId;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 项目状态 */
    @TableField("projectState")
    private Integer projectState;

    /** 项目状态名称 */
    @TableField("projectStateName")
    private String projectStateName;

    /** 实施状态 */
    @TableField("executionState")
    private Integer executionState;

    /** 服务经理编码 */
    @TableField("serviceManager")
    private String smCode;

    /** 项目经理编码 */
    @TableField("projectManager")
    private String pmCode;

    /** 销售人员编码 */
    @TableField("salesManCode")
    private String salesManCode;

    /** 销售人员姓名 */
    @TableField("salesManName")
    private String salesManName;

    /** 项目计划状态 */
    @TableField("projectPlanState")
    private Integer projectPlanState;

    /** 发货状态 */
    @TableField("shipmentState")
    private Integer shipmentState;

    /** 项目等级 */
    @TableField("rank")
    private String projectLevel;

    /** 重大项目级别 */
    @TableField("majorProjectLevel")
    private String majorProjectLevel;

    /** 合作渠道 */
    @TableField("partnerChannel")
    private String partnerChannel;

    /** 服务渠道 */
    @TableField("serviceChannel")
    private String serviceChannel;

    /** 代理商渠道 */
    @TableField("agentChannel")
    private String agentChannel;

    /** 项目创建时间 */
    @TableField("projectCreateTime")
    private LocalDateTime projectCreateTime;

    /** 项目开始时间 */
    @TableField("projectStartTime")
    private LocalDateTime projectStartTime;

    /** 项目刷新时间 */
    @TableField("projectRefreshTime")
    private LocalDateTime projectRefreshTime;

    // ===== 非数据库字段 =====

    /** 公司名称 */
    @TableField(exist = false)
    private String companyName;

    /** 办事处名称 */
    @TableField(exist = false)
    private String officeName;

    /** 服务经理姓名 */
    @TableField(exist = false)
    private String smName;

    /** 项目经理姓名 */
    @TableField(exist = false)
    private String pmName;

    /** 团队成员编码 */
    @TableField(exist = false)
    private String teamMemberCodes;

    /** 团队成员姓名 */
    @TableField(exist = false)
    private String teamMemberNames;

    /** 实施方式名称 */
    @TableField(exist = false)
    private String executionStateName;

    /** 计划状态名称 */
    @TableField(exist = false)
    private String planStateName;

    /** 发货状态名称 */
    @TableField(exist = false)
    private String shipmentStateName;
}
