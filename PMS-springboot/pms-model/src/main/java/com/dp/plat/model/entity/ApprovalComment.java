package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批意见实体 - 迁移自老系统 DpComment / ActComment
 * 对应表: wf_approval_comment
 */
@Data
@TableName("wf_approval_comment")
public class ApprovalComment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 业务对象ID（如回访ID、闭环ID） */
    @TableField("obj_id")
    private Long objId;

    /** 流程定义Key（如callback、closedloop） */
    @TableField("procdef_key")
    private String procdefKey;

    /** 流程实例ID */
    @TableField("inst_id")
    private String instId;

    /** 任务ID */
    @TableField("task_id")
    private String taskId;

    /** 任务节点Key */
    @TableField("task_key")
    private String taskKey;

    /** 审批人 */
    @TableField("assignee")
    private String assignee;

    /** 审批人姓名 */
    @TableField("assignee_name")
    private String assigneeName;

    /** 审批意见 */
    @TableField("message")
    private String message;

    /** 审批结果: 1=通过, -1=驳回, 0=待审批 */
    @TableField("result")
    private Integer result;

    /** 审批时间 */
    @TableField("assignee_time")
    private LocalDateTime assigneeTime;

    /** 下一环节审批人 */
    @TableField("next_assignee")
    private String nextAssignee;

    /** 下一环节审批人姓名 */
    @TableField("next_assignee_name")
    private String nextAssigneeName;
}
