package com.dp.plat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.service.WorkFlowService;

/**
 * 流程任务工具类
 * 
 * @author w02611
 */
public class WorkflowUtil {
    
    /**
     * 根据taskId终止流程
     * 
     * @param taskId
     */
    public static void terminateActivities(String taskId) {
        terminateActivities(taskId, null);
    }
    
    /**
     * 根据taskId终止流程
     * 
     * @param taskId
     * @param comment
     */
    public static void terminateActivities(String taskId, String comment) {
        terminateActivities(Collections.singletonList(taskId), comment);
    }
    
    /**
     * 根据taskIds终止流程
     * 
     * @param taskIds
     */
    public static void terminateActivities(List<String> taskIds) {
        terminateActivities(taskIds, null);
    }

    /**
     * 根据taskIds终止流程
     * 
     * @param taskIds
     * @param comment 备注
     */
    public static void terminateActivities(List<String> taskIds, String comment) {
        if (taskIds == null || taskIds.isEmpty()) {
            return;
        }
        ServletContext sc = ServletActionContext.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
        TaskService taskService = ctx.getBean("taskService", TaskService.class);
        WorkFlowService workFlowService = ctx.getBean("workFlowService", WorkFlowService.class);
        for (String taskId : taskIds) {
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) workFlowService
                    .getProcessDefinitionByTaskId(taskId);
            List<ActivityImpl> endActivityImpls = new ArrayList<ActivityImpl>();
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    endActivityImpls.add(activityImpl);
                }
            }
            for (Iterator<ActivityImpl> iterator = endActivityImpls.iterator(); iterator.hasNext();) {
                ActivityImpl endActivityImpl = (ActivityImpl) iterator.next();
                if (!iterator.hasNext()) {
                    // 当前节点
                    ActivityImpl currActivity = findActivitiImpl(taskId, null);

                    // 清空当前流向
                    List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

                    // 创建新流向
                    TransitionImpl newTransition = currActivity.createOutgoingTransition();
                    // 目标节点
                    ActivityImpl pointActivity = endActivityImpl;
                    // 设置新流向的目标节点
                    newTransition.setDestination(pointActivity);

                    // 执行转向任务
                    if (comment != null) {
                        taskService.addComment(taskId, null, comment);
                    }
                    taskService.complete(taskId, new HashMap<String, Object>());
                    // taskService.deleteTask(taskId, true);
                    // 删除目标节点新流入
                    pointActivity.getIncomingTransitions().remove(newTransition);

                    // 还原以前流向
                    restoreTransition(currActivity, oriPvmTransitionList);
                    break;
                }
            }
        }

    }

    /**
     * 根据任务ID和节点ID获取活动节点 <br>
     * 
     * @param taskId     任务ID
     * @param activityId 活动节点ID <br>
     *                   如果为null或""，则默认查询当前活动节点 <br>
     *                   如果包含"end"，则查询结束节点 <br>
     * @return
     * @throws Exception
     */
    private static ActivityImpl findActivitiImpl(String taskId, String activityId) {
        ServletContext sc = ServletActionContext.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
        WorkFlowService workFlowService = ctx.getBean("workFlowService", WorkFlowService.class);
        TaskService taskService = ctx.getBean("taskService", TaskService.class);
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) workFlowService
                .getProcessDefinitionByTaskId(taskId);

        // 获取当前活动节点ID
        if (StringUtils.isBlank(activityId)) {
            activityId = taskService.createTaskQuery().taskId(taskId).singleResult().getTaskDefinitionKey();
        }

        // 根据流程定义，获取该流程实例的结束节点
        if (activityId.contains("end")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // 根据节点ID，获取对应的活动节点
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);

        return activityImpl;
    }

    /**
     * 清空指定活动节点流向
     * 
     * @param activityImpl 活动节点
     * @return 节点流向集合
     */
    private static List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     * 
     * @param activityImpl         活动节点
     * @param oriPvmTransitionList 原有节点流向集合
     */
    private static void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }
}
