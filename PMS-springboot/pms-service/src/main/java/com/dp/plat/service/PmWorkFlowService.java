package com.dp.plat.service;

import com.dp.plat.model.entity.PmWorkFlow;
import java.util.List;
import java.util.Map;

/**
 * PM工作流服务 - 封装Flowable工作流引擎的业务操作
 */
public interface PmWorkFlowService extends BaseService<PmWorkFlow> {
    void deployProcess(String processName, String processKey);
    void deleteDeployment(String deploymentId);
    void submitTask(String taskId, String processInstanceId, String comment, Map<String, Object> variables);
    List<Map<String, Object>> getMyTasks(String username);
    List<Map<String, Object>> getHistoryTasks(String username);
    byte[] getProcessImage(String deploymentId);
    byte[] getCurrentProcessImage(String processInstanceId);
    void addDelegate(Map<String, Object> delegateInfo);
    void updateDelegate(Map<String, Object> delegateInfo);
    List<Map<String, Object>> getDelegates();
}
