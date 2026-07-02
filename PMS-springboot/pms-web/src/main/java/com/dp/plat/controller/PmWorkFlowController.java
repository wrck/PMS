package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.PmWorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PM工作流控制器 - 迁移自老系统 WorkFlowAction
 */
@RestController
@RequestMapping("/api/workflow/pm")
public class PmWorkFlowController {

    @Autowired
    private PmWorkFlowService pmWorkFlowService;

    /** 部署流程 */
    @PostMapping("/deploy")
    public R<Void> deploy(@RequestBody Map<String, String> params) {
        pmWorkFlowService.deployProcess(params.get("processName"), params.get("processKey"));
        return R.ok();
    }

    /** 删除流程部署 */
    @DeleteMapping("/deploy/{deploymentId}")
    public R<Void> deleteDeploy(@PathVariable String deploymentId) {
        pmWorkFlowService.deleteDeployment(deploymentId);
        return R.ok();
    }

    /** 提交任务 */
    @PostMapping("/task/submit")
    public R<Void> submitTask(@RequestBody Map<String, Object> params) {
        pmWorkFlowService.submitTask(
            (String) params.get("taskId"),
            (String) params.get("processInstanceId"),
            (String) params.get("comment"),
            (Map<String, Object>) params.get("variables"));
        return R.ok();
    }

    /** 查询我的待办任务 */
    @GetMapping("/my-tasks")
    public R<List<Map<String, Object>>> myTasks(@RequestParam String username) {
        return R.ok(pmWorkFlowService.getMyTasks(username));
    }

    /** 查询历史任务 */
    @GetMapping("/history")
    public R<List<Map<String, Object>>> history(@RequestParam String username) {
        return R.ok(pmWorkFlowService.getHistoryTasks(username));
    }

    /** 查看流程图 */
    @GetMapping("/deploy/{deploymentId}/image")
    public void viewImage(@PathVariable String deploymentId, jakarta.servlet.http.HttpServletResponse response) throws Exception {
        byte[] image = pmWorkFlowService.getProcessImage(deploymentId);
        response.setContentType("image/png");
        response.getOutputStream().write(image);
    }

    /** 查看当前流程图(带高亮) */
    @GetMapping("/instance/{processInstanceId}/image")
    public void viewCurrentImage(@PathVariable String processInstanceId, jakarta.servlet.http.HttpServletResponse response) throws Exception {
        byte[] image = pmWorkFlowService.getCurrentProcessImage(processInstanceId);
        response.setContentType("image/png");
        response.getOutputStream().write(image);
    }

    /** 添加委派规则 */
    @PostMapping("/delegate")
    public R<Void> addDelegate(@RequestBody Map<String, Object> delegateInfo) {
        pmWorkFlowService.addDelegate(delegateInfo);
        return R.ok();
    }

    /** 编辑委派规则 */
    @PutMapping("/delegate")
    public R<Void> updateDelegate(@RequestBody Map<String, Object> delegateInfo) {
        pmWorkFlowService.updateDelegate(delegateInfo);
        return R.ok();
    }

    /** 获取委派规则列表 */
    @GetMapping("/delegates")
    public R<List<Map<String, Object>>> delegates() {
        return R.ok(pmWorkFlowService.getDelegates());
    }
}
