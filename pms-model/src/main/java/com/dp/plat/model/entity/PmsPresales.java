package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售前项目实体 - 对应老系统 pm_presales_project_header 表
 */
@Data
@TableName("pm_presales_project_header")
public class PmsPresales extends BaseEntity {

    @TableId(value = "presalesId", type = IdType.AUTO)
    private Long id;

    /** 流程实例ID */
    @TableField("instId")
    private String instId;

    /** 申请状态: -1=未申请, 0=审批中, 1=通过, 2=驳回 */
    @TableField("applyState")
    private Integer applyState;

    /** 申请人 */
    @TableField("applyBy")
    private String applyBy;

    /** 申请时间 */
    @TableField("applyTime")
    private LocalDateTime applyTime;

    /** 结束时间 */
    @TableField("endTime")
    private LocalDateTime endTime;

    /** 售前编码 */
    @TableField("presalesCode")
    private String presalesCode;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 项目状态 */
    @TableField("projectState")
    private Integer projectState;

    /** 项目类型 */
    @TableField("projectType")
    private Integer projectType;

    /** 市场名称 */
    @TableField("marketName")
    private String marketName;

    /** 系统名称 */
    @TableField("systemName")
    private String systemName;

    /** 扩展名称 */
    @TableField("expendName")
    private String expendName;

    /** 行业名称 */
    @TableField("industryName")
    private String industryName;

    /** 办事处编码 */
    @TableField("officeCode")
    private String officeCode;

    /** 销售人员 */
    @TableField("salesman")
    private String salesman;

    /** 销售联系方式 */
    @TableField("salesmanLink")
    private String salesmanLink;

    /** 产品经理 */
    @TableField("productManager")
    private String productManager;

    /** 确认文件ID */
    @TableField("confirmFileIds")
    private String confirmFileIds;

    /** 任务ID */
    @TableField("taskId")
    private Long taskId;

    /** 问卷ID */
    @TableField("quesnaireId")
    private Long quesnaireId;

    /** 问卷状态 */
    @TableField("quesnaireState")
    private Integer quesnaireState;

    /** 任务定义Key */
    @TableField("taskDefKey")
    private String taskDefKey;

    /** 数据来源 */
    @TableField("source")
    private String source;

    // ===== 非数据库字段 =====

    @TableField(exist = false)
    private String officeName;
    @TableField(exist = false)
    private String serviceManager;
    @TableField(exist = false)
    private String serviceManagerName;
    @TableField(exist = false)
    private String projectManager;
    @TableField(exist = false)
    private String projectManagerName;
    @TableField(exist = false)
    private String projectTypeName;
    @TableField(exist = false)
    private String taskAssignee;
}
