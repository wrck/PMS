package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.ApprovalCommentMapper;
import com.dp.plat.model.entity.ApprovalComment;
import com.dp.plat.model.vo.WorkflowTaskVO;
import com.dp.plat.service.WorkflowService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流服务实现 - 迁移自老系统 WorkFlowServiceImpl
 * 使用Flowable引擎替代Activiti
 *
 * 老系统核心方法映射:
 * - startProcess() → startProcess()
 * - doSelfTask() → completeTask()
 * - addSelfActComment() → addApprovalComment()
 * - queryTaskByBussinessKeyUser() → getTaskByBusinessKeyAndUser()
 * - getTaskIdByProcessInstanceId() → getTaskByProcessInstanceAndAssignee()
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private org.flowable.engine.RepositoryService repositoryService;
    @Autowired
    private ApprovalCommentMapper commentMapper;

    // ===== 流程实例管理 =====

    @Override
    @Transactional
    public String startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        // 迁移自: WorkFlowServiceImpl.startProcess()
        // 老系统: Authentication.setAuthenticatedUserId(username)
        //         runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, vars)
        String currentUsername = getCurrentUsernameSafe();
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("initiator", currentUsername);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                processDefinitionKey, businessKey, variables);
        return processInstance.getId();
    }

    @Override
    @Transactional
    public void deleteProcessInstance(String processInstanceId, String reason) {
        // 迁移自: WorkFlowServiceImpl.deleteProcessInstance()
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    // ===== 任务管理 =====

    @Override
    public List<WorkflowTaskVO> findPersonalTasks(String userId) {
        // 迁移自: WorkFlowServiceImpl.findPersonalTask(userId)
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime().desc()
                .list();
        return tasks.stream().map(this::convertTask).collect(Collectors.toList());
    }

    @Override
    public WorkflowTaskVO getTaskByProcessInstanceAndAssignee(String processInstanceId, String assignee) {
        // 迁移自: WorkFlowServiceImpl.getTaskIdByProcessInstanceId(piid, assignee)
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(assignee)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            return convertTask(tasks.get(0));
        }
        return null;
    }

    @Override
    public WorkflowTaskVO getTaskByBusinessKeyAndUser(String businessKey, String userId) {
        // 迁移自: WorkFlowServiceImpl.queryTaskByBussinessKeyUser(businessKey, userId)
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .taskAssignee(userId)
                .singleResult();
        return task != null ? convertTask(task) : null;
    }

    @Override
    public WorkflowTaskVO getTaskByBusinessKey(String businessKey) {
        // 迁移自: WorkFlowServiceImpl.queryTaskByBussinessKey(businessKey)
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        return task != null ? convertTask(task) : null;
    }

    @Override
    public List<WorkflowTaskVO> getTasksByProcessInstanceId(String processInstanceId) {
        // 迁移自: WorkFlowServiceImpl.getTaskByInstId(procInstId)
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        return tasks.stream().map(this::convertTask).collect(Collectors.toList());
    }

    @Override
    public List<WorkflowTaskVO> queryCurrentApprovers(String processInstanceId) {
        // 迁移自: WorkFlowServiceImpl.queryCurrentApprover(instId)
        return getTasksByProcessInstanceId(processInstanceId);
    }

    // ===== 任务办理 =====

    @Override
    @Transactional
    public void completeTask(String taskId, String processInstanceId, String comment, Map<String, Object> variables) {
        // 迁移自: WorkFlowServiceImpl.doSelfTask(task, instId, comment, vars)
        // 老系统逻辑:
        //   Authentication.setAuthenticatedUserId(task.getAssignee())
        //   taskService.addComment(task.getId(), instId, comment)
        //   taskService.setVariablesLocal(task.getId(), vars)
        //   taskService.complete(task.getId(), vars)
        String currentUsername = getCurrentUsernameSafe();

        if (variables == null) {
            variables = new HashMap<>();
        }

        // 添加Flowable原生批注
        if (StringUtils.hasText(comment)) {
            taskService.addComment(taskId, processInstanceId, comment);
        }

        // 设置流程变量
        taskService.setVariablesLocal(taskId, variables);

        // 完成任务
        taskService.complete(taskId, variables);
    }

    @Override
    @Transactional
    public void completeTaskWithoutComment(String taskId, Map<String, Object> variables) {
        // 迁移自: WorkFlowServiceImpl.submitTaskNoComment(param, vars)
        if (variables == null) {
            variables = new HashMap<>();
        }
        taskService.complete(taskId, variables);
    }

    @Override
    @Transactional
    public void claimTask(String taskId, String userId) {
        // 迁移自: WorkFlowServiceImpl.claimTask(taskId, userId)
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional
    public void assignTask(String taskId, String userId) {
        // 迁移自: WorkFlowServiceImpl.assigneeTask(taskId, userId, variableName)
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.setAssignee(taskId, userId);
        }
    }

    // ===== 审批意见管理 =====

    @Override
    @Transactional
    public Long addApprovalComment(Long objId, String procdefKey, String taskId,
                                   String instId, int result, String message) {
        // 迁移自: WorkFlowServiceImpl.addSelfActComment()
        return addApprovalComment(objId, procdefKey, taskId, instId, result, message, null, null);
    }

    @Override
    @Transactional
    public Long addApprovalComment(Long objId, String procdefKey, String taskId,
                                   String instId, int result, String message,
                                   String nextAssignee, String nextAssigneeName) {
        // 迁移自: WorkFlowServiceImpl.addSelfActComment(objId, procdefKey, taskId, instId, result, message, nextAssignee, nextAssigneeName)
        ApprovalComment comment = new ApprovalComment();
        comment.setObjId(objId);
        comment.setProcdefKey(procdefKey);
        comment.setTaskId(taskId);
        comment.setInstId(instId);
        comment.setResult(result);
        comment.setMessage(message);
        comment.setAssignee(getCurrentUsernameSafe());
        comment.setAssigneeTime(LocalDateTime.now());
        comment.setNextAssignee(nextAssignee);
        comment.setNextAssigneeName(nextAssigneeName);
        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public List<ApprovalComment> queryApprovalComments(Long objId, String procdefKey) {
        // 迁移自: WorkFlowServiceImpl.queryActComment(objId, procdefKey)
        return commentMapper.selectList(
                new LambdaQueryWrapper<ApprovalComment>()
                        .eq(ApprovalComment::getObjId, objId)
                        .eq(ApprovalComment::getProcdefKey, procdefKey)
                        .orderByAsc(ApprovalComment::getAssigneeTime));
    }

    @Override
    public List<ApprovalComment> queryApprovalCommentsByInstanceId(String processInstanceId) {
        // 迁移自: WorkFlowServiceImpl.getProcessComments(taskId, instId)
        // 先查询Flowable原生批注
        List<org.flowable.task.api.Comment> flowableComments = taskService.getProcessInstanceComments(processInstanceId);

        // 同时查询自定义审批意见表
        List<ApprovalComment> customComments = commentMapper.selectList(
                new LambdaQueryWrapper<ApprovalComment>()
                        .eq(ApprovalComment::getInstId, processInstanceId)
                        .orderByAsc(ApprovalComment::getAssigneeTime));

        return customComments;
    }

    // ===== 流程变量 =====

    @Override
    public Map<String, Object> getProcessVariables(String taskId) {
        // 迁移自: WorkFlowServiceImpl.queryProcessVarMap(taskId)
        return taskService.getVariables(taskId);
    }

    @Override
    @Transactional
    public void setVariable(String processInstanceId, String variableName, Object value) {
        // 迁移自: WorkFlowServiceImpl.setVariable()
        // 通过运行时服务设置流程变量
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            taskService.setVariable(tasks.get(0).getId(), variableName, value);
        }
    }

    // ===== 流程查询 =====

    @Override
    public String getLatestProcessDefinitionKey(String processDefinitionKey) {
        // 迁移自: WorkFlowServiceImpl.getProcessDefinitionByClassType()
        org.flowable.engine.repository.ProcessDefinition pd = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
        return pd != null ? pd.getId() : null;
    }

    @Override
    public boolean isProcessEnded(String processInstanceId) {
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        return hpi != null && hpi.getEndTime() != null;
    }

    // ===== 内部辅助方法 =====

    /**
     * 获取当前用户名（安全版本，不抛异常）
     */
    private String getCurrentUsernameSafe() {
        try {
            return SecurityUtil.getCurrentUsername();
        } catch (Exception e) {
            return "system";
        }
    }

    /**
     * Flowable Task → WorkflowTaskVO 转换
     */
    private WorkflowTaskVO convertTask(Task task) {
        WorkflowTaskVO vo = new WorkflowTaskVO();
        vo.setTaskId(task.getId());
        vo.setTaskName(task.getName());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionKey(task.getProcessDefinitionKey());
        vo.setAssignee(task.getAssignee());
        if (task.getCreateTime() != null) {
            vo.setCreateTime(task.getCreateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 从businessKey解析业务对象信息
        String businessKey = task.getProcessInstanceBusinessKey();
        if (StringUtils.hasText(businessKey)) {
            String[] parts = businessKey.split("\\.");
            if (parts.length >= 2) {
                vo.setBusinessObjType(parts[0]);
                try {
                    vo.setBusinessObjId(Long.parseLong(parts[1]));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
        }

        return vo;
    }


}
