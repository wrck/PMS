/**
 * 
 */
package com.dp.plat.activiti.service;

import java.util.List;

import org.activiti.engine.runtime.ProcessInstance;

import com.dp.plat.activiti.vo.ActivityVo;

/**
 * @author w02611
 *
 */
public interface IRuntimePageService {
	
	 /**
     * 获取流程启动人ID
     * @param processInstance 流程实例
     * @return
     */
    String getStartUserId(ProcessInstance processInstance);

    /**
     * 根据任务id获取流程启动人
     */
    String getStartUserId(String taskId);

	public List<ActivityVo> getActivityList(String processInstanceId);

}
