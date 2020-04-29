package com.dp.plat.activiti.process.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * 删除未完成的任务
 * 
 * @author w02611
 *
 */
public class DeleteActiveTaskCmd implements Command<java.lang.Void> {

	private TaskEntity currentTaskEntity;
	private Boolean cascade;
	private String deleteReason;

	public DeleteActiveTaskCmd(TaskEntity currentTaskEntity, String deleteReason, Boolean cascade) {
		this.currentTaskEntity = currentTaskEntity;
		this.deleteReason = deleteReason;
		this.cascade = cascade;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		Context.getCommandContext().getTaskEntityManager().deleteTask(this.currentTaskEntity, this.deleteReason,
				this.cascade);
		return null;
	}

}
