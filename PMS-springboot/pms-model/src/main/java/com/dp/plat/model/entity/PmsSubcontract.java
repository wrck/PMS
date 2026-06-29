package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分包项目实体 - 对应 pm_subcontract_project 表
 */
@Data
@TableName("pm_subcontract_project")
public class PmsSubcontract extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 分包名称 */
    @TableField("subcontractName")
    private String subcontractName;

    /** 关联合同号(逗号分隔) */
    @TableField("contractNos")
    private String contractNos;

    /** 关联项目ID(逗号分隔) */
    @TableField("projectIds")
    private String projectIds;

    /** 分包类型 */
    @TableField("type")
    private Integer type;

    /** 分包状态 */
    @TableField("state")
    private Integer state;

    /** 回访状态 */
    @TableField("callbackState")
    private Integer callbackState;

    /** 服务商ID */
    @TableField("facilitatorId")
    private Long facilitatorId;

    /** 服务商名称 */
    @TableField("facilitatorName")
    private String facilitatorName;

    /** 银行信息 */
    @TableField("bankInfo")
    private String bankInfo;

    /** 银行账号 */
    @TableField("bankAccount")
    private String bankAccount;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 利润中心部门编码 */
    @TableField("profitDepCode")
    private String profitDepCode;

    /** 分包编号 */
    @TableField("subcontractNo")
    private String subcontractNo;

    /** 是否已计提 */
    @TableField("isAccrued")
    private Integer isAccrued;

    /** 是否已开票 */
    @TableField("isInvoiced")
    private Integer isInvoiced;

    /** 分包金额 */
    @TableField("subcontractAmount")
    private String subcontractAmount;

    /** 原因 */
    @TableField("reason")
    private String reason;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 审批通过时间 */
    @TableField("zrApproveTime")
    private LocalDateTime zrApproveTime;

    /** 流程实例ID */
    @TableField("instId")
    private String instId;

    // ===== 非数据库字段 =====

    @TableField(exist = false)
    private String officeName;

    @TableField(exist = false)
    private String typeName;

    @TableField(exist = false)
    private String stateName;

    @TableField(exist = false)
    private String callbackStateName;
}
