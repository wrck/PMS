package com.dp.plat.activiti.dao;

import java.util.List;

import com.dp.plat.activiti.entity.ActUserTask;
import com.dp.plat.core.dao.AbstractBaseMapper;

public interface ActUserTaskMapper extends AbstractBaseMapper<ActUserTask> {

	/**
	 * 根据流程定义的key查询用户任务
	 * 
	 * @param processDefinitionKey
	 * @return
	 */
	List<ActUserTask> selectByProcessDefinitionKey(String processDefinitionKey);

}
