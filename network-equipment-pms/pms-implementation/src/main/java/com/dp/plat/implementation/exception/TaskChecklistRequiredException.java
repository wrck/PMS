package com.dp.plat.implementation.exception;

import com.dp.plat.implementation.entity.TaskChecklist;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

/**
 * 提交评审时强制检查项未全部勾选所抛出的异常。
 *
 * <p>关联设计文档 §3.3 Story 3 验收 1：强制检查项拦截。携带未勾选的强制检查项列表，
 * 供全局异常处理器转换为结构化响应（errorCode=TASK_CHECKLIST_REQUIRED）。</p>
 */
@Getter
public class TaskChecklistRequiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ERROR_CODE = "TASK_CHECKLIST_REQUIRED";
    public static final String ERROR_MESSAGE = "存在未完成的强制检查项";

    /** 未勾选的强制检查项列表。 */
    private final List<TaskChecklist> uncheckedMandatoryItems;

    /** 当前任务状态（提交评审被拦截时保持原状态）。 */
    private final String taskStatus;

    public TaskChecklistRequiredException(List<TaskChecklist> uncheckedMandatoryItems, String taskStatus) {
        super(ERROR_MESSAGE);
        this.uncheckedMandatoryItems = uncheckedMandatoryItems;
        this.taskStatus = taskStatus;
    }
}
