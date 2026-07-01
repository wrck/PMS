package com.dp.plat.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 工作流任务VO - 用于待办任务列表展示
 */
@Data
public class WorkflowTaskVO {
    /** 任务ID */
    private String taskId;
    /** 任务名称 */
    private String taskName;
    /** 流程实例ID */
    private String processInstanceId;
    /** 流程定义Key */
    private String processDefinitionKey;
    /** 流程定义名称 */
    private String processDefinitionName;
    /** 任务办理人 */
    private String assignee;
    /** 任务创建时间 */
    private LocalDateTime createTime;
    /** 业务对象ID（从businessKey解析） */
    private Long businessObjId;
    /** 业务对象类型（从businessKey解析） */
    private String businessObjType;
    /** 流程变量 */
    private Map<String, Object> variables;
}
