package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 质保回访实体 - 对应 pm_project_warranty_callback 表
 */
@Data
@TableName("pm_project_warranty_callback")
public class PmsWarrantyCallback extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 项目ID */
    @TableField("projectId")
    private Long projectId;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 关联合同号 */
    @TableField("contractNos")
    private String contractNos;

    /** 关联项目ID */
    @TableField("projectIds")
    private String projectIds;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 服务实施方式 */
    @TableField("serviceImpl")
    private String serviceImpl;

    /** 行业名称 */
    @TableField("industryName")
    private String industryName;

    /** 代理商渠道 */
    @TableField("agentChannel")
    private String agentChannel;

    /** 最终客户名称 */
    @TableField("finalCustomerName")
    private String finalCustomerName;

    /** 客户1 */
    @TableField("customer1")
    private String customer1;

    /** 客户联系方式1 */
    @TableField("customerContact1")
    private String customerContact1;

    /** 客户2 */
    @TableField("customer2")
    private String customer2;

    /** 客户联系方式2 */
    @TableField("customerContact2")
    private String customerContact2;

    /** 质保开始时间 */
    @TableField("warrantyStartTime")
    private LocalDateTime warrantyStartTime;

    /** 质保结束时间 */
    @TableField("warrantyEndTime")
    private LocalDateTime warrantyEndTime;

    /** 续保意向 */
    @TableField("renewalIntention")
    private Integer renewalIntention;

    /** 回访时间 */
    @TableField("callbackTime")
    private LocalDateTime callbackTime;

    /** 下次回访时间 */
    @TableField("nextCallbackTime")
    private LocalDateTime nextCallbackTime;

    /** 任务ID */
    @TableField("taskId")
    private String taskId;

    /** 问卷ID */
    @TableField("quesnaireId")
    private Long quesnaireId;

    /** 问卷版本 */
    @TableField("quesnaireVersion")
    private Integer quesnaireVersion;

    /** 问卷状态 */
    @TableField("quesnaireState")
    private Integer quesnaireState;

    /** 是否删除 */
    @TableField("isDelete")
    private Integer isDelete;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 公司ID */
    @TableField("compId")
    private Long compId;

    // ===== 非数据库字段 =====

    @TableField(exist = false)
    private String officeName;

    @TableField(exist = false)
    private Boolean hasPower;
}
