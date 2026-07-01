package com.dp.plat.service;

import com.dp.plat.model.entity.ApprovalComment;
import com.dp.plat.model.vo.WorkflowTaskVO;

import java.util.List;
import java.util.Map;

/**
 * 工作流服务接口 - 迁移自老系统 WorkFlowService
 * 使用Flowable引擎替代Activiti
 *
 * 核心方法对应老系统 WorkFlowServiceImpl 中的关键操作
 */
public interface WorkflowService {

    // ===== 流程实例管理 =====

    /**
     * 启动流程实例
     * 迁移自: WorkFlowServiceImpl.startProcess(key, businessKey, vars)
     *
     * @param processDefinitionKey 流程定义Key（如"callback"、"closedloop"）
     * @param businessKey 业务Key（格式: ClassName.objId.projectId）
     * @param variables 流程变量
     * @return 流程实例ID
     */
    String startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables);

    /**
     * 删除流程实例
     * 迁移自: WorkFlowServiceImpl.deleteProcessInstance(proInstId, comment)
     */
    void deleteProcessInstance(String processInstanceId, String reason);

    // ===== 任务管理 =====

    /**
     * 查询指定用户的待办任务
     * 迁移自: WorkFlowServiceImpl.findPersonalTask(userId)
     */
    List<WorkflowTaskVO> findPersonalTasks(String userId);

    /**
     * 根据流程实例ID和办理人查询任务
     * 迁移自: WorkFlowServiceImpl.getTaskIdByProcessInstanceId(piid, assignee)
     */
    WorkflowTaskVO getTaskByProcessInstanceAndAssignee(String processInstanceId, String assignee);

    /**
     * 根据BusinessKey和用户查询任务
     * 迁移自: WorkFlowServiceImpl.queryTaskByBussinessKeyUser(businessKey, userId)
     */
    WorkflowTaskVO getTaskByBusinessKeyAndUser(String businessKey, String userId);

    /**
     * 根据BusinessKey查询任务
     * 迁移自: WorkFlowServiceImpl.queryTaskByBussinessKey(businessKey)
     */
    WorkflowTaskVO getTaskByBusinessKey(String businessKey);

    /**
     * 查询流程实例下的所有任务
     * 迁移自: WorkFlowServiceImpl.getTaskByInstId(procInstId)
     */
    List<WorkflowTaskVO> getTasksByProcessInstanceId(String processInstanceId);

    /**
     * 查询当前流程的审批人列表
     * 迁移自: WorkFlowServiceImpl.queryCurrentApprover(instId)
     */
    List<WorkflowTaskVO> queryCurrentApprovers(String processInstanceId);

    // ===== 任务办理 =====

    /**
     * 办理任务（完成任务并添加批注）
     * 迁移自: WorkFlowServiceImpl.doSelfTask(task, instId, comment, vars)
     *
     * @param taskId 任务ID
     * @param processInstanceId 流程实例ID
     * @param comment 审批意见
     * @param variables 流程变量
     */
    void completeTask(String taskId, String processInstanceId, String comment, Map<String, Object> variables);

    /**
     * 办理任务（无需批注）
     * 迁移自: WorkFlowServiceImpl.submitTaskNoComment(param, vars)
     */
    void completeTaskWithoutComment(String taskId, Map<String, Object> variables);

    /**
     * 认领任务
     * 迁移自: WorkFlowServiceImpl.claimTask(taskId, userId)
     */
    void claimTask(String taskId, String userId);

    /**
     * 委派任务
     * 迁移自: WorkFlowServiceImpl.assigneeTask(taskId, userId, variableName)
     */
    void assignTask(String taskId, String userId);

    // ===== 审批意见管理 =====

    /**
     * 添加自定义审批意见
     * 迁移自: WorkFlowServiceImpl.addSelfActComment(objId, procdefKey, taskId, instId, result, message)
     *
     * @param objId 业务对象ID
     * @param procdefKey 流程定义Key
     * @param taskId 任务ID
     * @param instId 流程实例ID
     * @param result 审批结果（1=通过, -1=驳回）
     * @param message 审批意见
     * @return 审批意见ID
     */
    Long addApprovalComment(Long objId, String procdefKey, String taskId,
                            String instId, int result, String message);

    /**
     * 添加自定义审批意见（含下一环节审批人）
     * 迁移自: WorkFlowServiceImpl.addSelfActComment(objId, procdefKey, taskId, instId, result, message, nextAssignee, nextAssigneeName)
     */
    Long addApprovalComment(Long objId, String procdefKey, String taskId,
                            String instId, int result, String message,
                            String nextAssignee, String nextAssigneeName);

    /**
     * 查询业务对象的审批意见列表
     * 迁移自: WorkFlowServiceImpl.queryActComment(objId, procdefKey)
     */
    List<ApprovalComment> queryApprovalComments(Long objId, String procdefKey);

    /**
     * 查询流程实例的审批意见列表
     * 迁移自: WorkFlowServiceImpl.getProcessComments(taskId, instId)
     */
    List<ApprovalComment> queryApprovalCommentsByInstanceId(String processInstanceId);

    // ===== 流程变量 =====

    /**
     * 获取流程变量
     * 迁移自: WorkFlowServiceImpl.queryProcessVarMap(taskId)
     */
    Map<String, Object> getProcessVariables(String taskId);

    /**
     * 设置流程变量
     * 迁移自: WorkFlowServiceImpl.setVariable(instId, variableName, oldValue, newValue)
     */
    void setVariable(String processInstanceId, String variableName, Object value);

    // ===== 流程查询 =====

    /**
     * 查询流程定义Key对应的最新版本
     * 迁移自: WorkFlowServiceImpl.getProcessDefinitionByClassType(simpleName)
     */
    String getLatestProcessDefinitionKey(String processDefinitionKey);

    /**
     * 判断流程实例是否已结束
     */
    boolean isProcessEnded(String processInstanceId);
}
