package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.model.entity.ApprovalComment;
import com.dp.plat.model.vo.WorkflowTaskVO;
import com.dp.plat.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流控制器 - 迁移自老系统 WorkFlowAction
 * 提供待办任务、审批、流程查询等接口
 */
@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 查询当前用户待办任务
     * 迁移自: WorkFlowAction.findPersonalTask()
     */
    @GetMapping("/tasks/todo")
    public R<List<WorkflowTaskVO>> getTodoTasks() {
        String username = SecurityUtil.getCurrentUsername();
        return R.ok(workflowService.findPersonalTasks(username));
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public R<WorkflowTaskVO> getTaskDetail(@PathVariable String taskId) {
        // 通过流程实例查询当前用户的任务
        String username = SecurityUtil.getCurrentUsername();
        // taskId即为流程实例ID，查询当前用户的任务
        return R.ok(workflowService.getTaskByProcessInstanceAndAssignee(taskId, username));
    }

    /**
     * 办理任务（审批通过/驳回）
     * 迁移自: WorkFlowAction.submitTask() -> WorkFlowServiceImpl.doSelfTask()
     */
    @PostMapping("/tasks/{taskId}/complete")
    public R<Void> completeTask(@PathVariable String taskId,
                                 @RequestParam String processInstanceId,
                                 @RequestBody Map<String, Object> params) {
        String comment = (String) params.getOrDefault("comment", "");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) params.get("variables");
        workflowService.completeTask(taskId, processInstanceId, comment, variables);
        return R.ok();
    }

    /**
     * 认领任务
     * 迁移自: WorkFlowAction.claimTask()
     */
    @PostMapping("/tasks/{taskId}/claim")
    public R<Void> claimTask(@PathVariable String taskId) {
        String username = SecurityUtil.getCurrentUsername();
        workflowService.claimTask(taskId, username);
        return R.ok();
    }

    /**
     * 委派任务
     */
    @PostMapping("/tasks/{taskId}/assign")
    public R<Void> assignTask(@PathVariable String taskId, @RequestParam String userId) {
        workflowService.assignTask(taskId, userId);
        return R.ok();
    }

    /**
     * 查询审批意见
     * 迁移自: WorkFlowAction.queryActComment()
     */
    @GetMapping("/comments")
    public R<List<ApprovalComment>> getComments(@RequestParam Long objId,
                                                 @RequestParam String procdefKey) {
        return R.ok(workflowService.queryApprovalComments(objId, procdefKey));
    }

    /**
     * 查询流程实例的审批意见
     */
    @GetMapping("/comments/instance/{processInstanceId}")
    public R<List<ApprovalComment>> getCommentsByInstance(@PathVariable String processInstanceId) {
        return R.ok(workflowService.queryApprovalCommentsByInstanceId(processInstanceId));
    }

    /**
     * 查询流程变量
     * 迁移自: WorkFlowAction.queryProcessVarMap()
     */
    @GetMapping("/tasks/{taskId}/variables")
    public R<Map<String, Object>> getProcessVariables(@PathVariable String taskId) {
        return R.ok(workflowService.getProcessVariables(taskId));
    }

    /**
     * 判断流程是否已结束
     */
    @GetMapping("/instances/{processInstanceId}/ended")
    public R<Boolean> isProcessEnded(@PathVariable String processInstanceId) {
        return R.ok(workflowService.isProcessEnded(processInstanceId));
    }
}
