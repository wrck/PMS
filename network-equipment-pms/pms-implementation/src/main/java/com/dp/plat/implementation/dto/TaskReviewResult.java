package com.dp.plat.implementation.dto;

import com.dp.plat.implementation.entity.TaskChecklist;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 提交评审 / 验收任务的返回结果。
 *
 * <p>关联设计文档 §5.4 Story 3 验收 1：强制检查项拦截响应结构。
 * 当强制检查项未全部勾选时，success=false 且携带 uncheckedMandatoryItems；
 * 评审提交成功时 success=true 且 taskStatus=REVIEW。</p>
 */
@Data
@Builder
public class TaskReviewResult {

    /** 是否操作成功。 */
    private Boolean success;

    /** 错误码（失败时，如 TASK_CHECKLIST_REQUIRED）。 */
    private String errorCode;

    /** 错误信息（失败时）。 */
    private String errorMessage;

    /** 未勾选的强制检查项列表（仅强制检查项拦截时存在）。 */
    private List<TaskChecklist> uncheckedMandatoryItems;

    /** 任务当前状态。 */
    private String taskStatus;
}
