package com.dp.plat.activiti.process.cmd;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.cmd.AddCommentCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.PersistentObject;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.CommentEntityManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Comment;

public class JumpTaskCmdService implements Command<Comment> {

	protected String executionId; // 当前任务的executionID
	protected String activityId; // 跳转目标activityID
	protected String type; // 跳转类型
	protected String reason; // 跳转理由
	private Map<String, ? extends Object> variables;// 任务参数

	public JumpTaskCmdService() {

	}

	public JumpTaskCmdService(String executionId, String activityId, String reason) {
		this.executionId = executionId;
		this.activityId = activityId;
		this.reason = reason;
	}
	
	public JumpTaskCmdService(String executionId, String activityId, String type, String reason) {
		this.executionId = executionId;
		this.activityId = activityId;
		this.type = type;
		this.reason = reason;
	}

	public Comment execute(CommandContext commandContext) {
		TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
		List<TaskEntity> list = taskEntityManager.findTasksByExecutionId(executionId);
		CommentEntityManager commentEntityManager = commandContext.getCommentEntityManager();
		for (TaskEntity taskEntity : list) {
			new AddCommentCmd(taskEntity.getId(), taskEntity.getProcessInstanceId(), reason).execute(commandContext);
			taskEntity.createVariablesLocal(variables);
			taskEntityManager.deleteTask(taskEntity, type, false);
		}
		ExecutionEntity executionEntity = Context.getCommandContext().getExecutionEntityManager()
				.findExecutionById(executionId);
		ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
		ActivityImpl activity = processDefinition.findActivity(activityId);
		executionEntity.executeActivity(activity);
		return null;
	}

	public Map<String, ? extends Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, ? extends Object> variables) {
		this.variables = variables;
	}

}