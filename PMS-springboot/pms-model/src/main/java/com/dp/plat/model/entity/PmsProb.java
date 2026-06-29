package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技术公告实体 - 对应 pm_prob_header 表
 */
@Data
@TableName("pm_prob_header")
public class PmsProb extends BaseEntity {

    @TableId(value = "probId", type = IdType.AUTO)
    private Long id;

    /** 公告编号 */
    @TableField("probNum")
    private String probNum;

    /** 工单号 */
    @TableField("probTicketNo")
    private String probTicketNo;

    /** 关注级别 (关联fnd_basic_data dataTypeCode=30) */
    @TableField("watch")
    private String watch;

    /** 公告主题 */
    @TableField("theme")
    private String theme;

    /** 问题描述 */
    @TableField("desc")
    private String desc;

    /** 解决方案 */
    @TableField("solution")
    private String solution;

    /** 状态 (关联fnd_basic_data dataTypeCode=31: 0=草稿 1=已发布 2=已关闭) */
    @TableField("status")
    private String status;

    /** 生效日期 */
    @TableField("startdate")
    private LocalDateTime startdate;

    /** 截止日期 */
    @TableField("duedate")
    private LocalDateTime duedate;

    /** 附件文件名(逗号分隔) */
    @TableField("attachmentNames")
    private String attachmentNames;

    /** 附件ID(逗号分隔) */
    @TableField("attachments")
    private String attachments;

    /** 优先级 (关联fnd_basic_data dataTypeCode=32) */
    @TableField("priority")
    private String priority;

    /** 受影响版本(JSON) */
    @TableField("affectedVersion")
    private String affectedVersion;

    /** 产品类型 */
    @TableField("productType")
    private String productType;

    /** 关联场景类型 */
    @TableField("relatedSceneTypes")
    private String relatedSceneTypes;

    /** 缓解措施类型 */
    @TableField("mitigationActionTypes")
    private String mitigationActionTypes;

    /** 解决方案类型 */
    @TableField("solutionActionTypes")
    private String solutionActionTypes;

    /** 跟踪人 */
    @TableField("trackingUser")
    private String trackingUser;

    /** 受影响类型 */
    @TableField("affectedType")
    private Integer affectedType;

    /** 可见范围 */
    @TableField("visibleRange")
    private Integer visibleRange;

    /** 备注 */
    @TableField("remark")
    private String remark;

    // ===== 非数据库字段 =====

    @TableField(exist = false)
    private String watchName;

    @TableField(exist = false)
    private String statusName;

    @TableField(exist = false)
    private String priorityName;

    @TableField(exist = false)
    private String trackingUsername;

    @TableField(exist = false)
    private String relatedSceneTypesName;

    @TableField(exist = false)
    private String mitigationActionTypesName;

    @TableField(exist = false)
    private String solutionActionTypesName;
}
