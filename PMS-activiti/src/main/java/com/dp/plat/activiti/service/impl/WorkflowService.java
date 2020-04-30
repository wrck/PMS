/**
 * 
 */
package com.dp.plat.activiti.service.impl;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.activiti.service.IWorkflowService;

/**
 * @author w02611
 *
 */
@Service("workflowService")
public class WorkflowService implements IWorkflowService {
	
	@Autowired
	protected TaskService taskService;

	/**
	 * @param processInstance
	 * @return
	 */
	public List<Task> getCurrentTaskInfo(ProcessInstance processInstance) {
		List<Task> currentTasks = null;
		try {
			String activitiId = (String) PropertyUtils.getProperty(processInstance, "activityId");

			currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId())
					.taskDefinitionKey(activitiId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentTasks;
	}

}
