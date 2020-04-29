/**
 * 
 */
package com.dp.plat.activiti.service;

import java.util.List;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * @author w02611
 *
 */
public interface IWorkflowService {

	/**
	 * @param processInstance
	 * @return
	 */
	List<Task> getCurrentTaskInfo(ProcessInstance processInstance);

}
