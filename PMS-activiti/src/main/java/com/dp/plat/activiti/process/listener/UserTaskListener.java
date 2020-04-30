package com.dp.plat.activiti.process.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dp.plat.activiti.entity.ActUserTask;
import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.service.IActUserTaskService;

/**
 * 动态用户任务分配
 * 
 * @author w02611
 *
 */
@Component("userTaskListener")
public class UserTaskListener implements TaskListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2190559253653576032L;

	private static final Logger logger = Logger.getLogger(UserTaskListener.class);
	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	private IActUserTaskService actUserTaskService;

	@Override
	public void notify(DelegateTask delegateTask) {
		String processDefinitionId = delegateTask.getProcessDefinitionId(); // com.zml.oa.vacation:8:30012
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		// String processDefinitionName = processDefinition.getName(); //流程定义的名称
		String processDefinitionKey = processDefinition.getKey(); // 流程定义的key
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey(); // 分配的代理
		try {
			List<ActUserTask> taskList = this.actUserTaskService.selectByProcessDefinitionKey(processDefinitionKey);
			for (ActUserTask userTask : taskList) {
				String taskKey = userTask.getTaskDefKey();
				String taskType = userTask.getTaskType();
				String ids = userTask.getCandidateIds();
				if (taskDefinitionKey.equals(taskKey)) {
					switch (taskType) {
					case "assignee":
						delegateTask.setAssignee(ids);
						logger.info("assignee id: " + ids);
						break;
					case "candidateUser":
						String[] userIds = ids.split(",");
						List<String> users = new ArrayList<String>();
						for (String user : userIds) {
							if (StringUtils.isNoneBlank(user)) {
								users.add(user);
							}
						}
						if (users.size() == 1) {
							delegateTask.setAssignee(users.get(0));
						} else {
							delegateTask.addCandidateUsers(users);
						}
						logger.info("候选人审批 ids: " + ids);
						break;
					case "candidateGroup":
						String[] groupIds = ids.split(",");
						List<String> groups = new ArrayList<String>();
						for (int i = 0; i < groupIds.length; i++) {
							groups.add(groupIds[i]);
						}
						delegateTask.addCandidateGroups(groups);
						logger.info("候选组审批 ids: " + ids);
						break;
					case "modify":
						BaseVO entity = delegateTask.getVariable("entity", BaseVO.class);
						String userId = entity.getUserId().toString();
						delegateTask.setAssignee(userId);
						logger.info("审批人 id: " + userId);
						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
