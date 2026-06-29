package com.dp.plat.activiti.process.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;

public class StartActivityCmd implements Command<java.lang.Void> {

	private ActivityImpl activity;

	private String executionId;

	public StartActivityCmd(String executionId, ActivityImpl activity)
	{
		this.activity = activity;
		this.executionId = executionId;
	}
	
	@Override
	public Void execute(CommandContext commandContext) {
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(this.executionId);
		execution.setActivity(this.activity);

		execution.performOperation(AtomicOperation.ACTIVITY_START);
		return null;
	}

}
