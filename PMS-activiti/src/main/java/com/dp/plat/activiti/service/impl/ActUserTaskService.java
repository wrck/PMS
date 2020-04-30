package com.dp.plat.activiti.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dp.plat.activiti.dao.ActUserTaskMapper;
import com.dp.plat.activiti.entity.ActUserTask;
import com.dp.plat.activiti.service.IActUserTaskService;
import com.dp.plat.core.service.impl.AbstractBaseService;

@Service("actUserTaskService")
public class ActUserTaskService extends AbstractBaseService<ActUserTaskMapper, ActUserTask>
		implements IActUserTaskService {

	// public Serializable doAdd(UserTask userTask) throws Exception;
	//
	// public void doUpdate(UserTask userTask) throws Exception;
	//
	// public void doDelete(UserTask userTask) throws Exception;
	//
	// public List<UserTask> toList(String procDefKey) throws Exception;
	//
	// public Integer deleteAll() throws Exception;
	//
	// public UserTask findById(Integer id) throws Exception;
	//
	// public List<UserTask> findByWhere(String procDefKey) throws Exception;
	//
	// public List<UserTask> getAll() throws Exception;

	/**
	 * 根据流程定义的key查询用户任务
	 * 
	 * @param processDefinitionKey
	 * @return
	 */
	public List<ActUserTask> selectByProcessDefinitionKey(String processDefinitionKey) {
		return dao.selectByProcessDefinitionKey(processDefinitionKey);
	}

}
