package com.dp.plat.activiti.process.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.delegate.ExecutionListenerInvocation;
import org.activiti.engine.impl.history.handler.ActivityInstanceStartHandler;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.dp.plat.activiti.process.exception.CustomActivitiException;
import com.dp.plat.core.context.UserContext;

/**
 * Activiti 命令拦截器 Command
 * 
 * @author ZML
 *
 */

@Component
public class WithdrawTaskCmd implements Command<Integer> {
	
	// Variable names for outer instance(as described in spec)
	protected final String NUMBER_OF_INSTANCES = "nrOfInstances";
	protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
	protected final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
	// default variable name for loop counter for inner instances (as described in the spec)
	protected String collectionElementIndexVariable = "loopCounter";
	
	private TaskEntity currentTaskEntity;
	private String targetTaskDefinitionKey;
	
	public WithdrawTaskCmd() {

	}

	public WithdrawTaskCmd(String targetTaskDefinitionKey, TaskEntity currentTaskEntity) {
		super();
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.currentTaskEntity = currentTaskEntity;
	}

	/**
	 * 0-撤销成功 1-流程结束 2-下一结点已经通过,不能撤销
	 * 
	 * @param historyTaskId
	 * @param processInstanceId
	 * @return
	 */
	@Override
	public Integer execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl processEngineConfiguration = commandContext.getProcessEngineConfiguration();
		RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
		RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
		TaskService taskService = processEngineConfiguration.getTaskService();
		HistoryService historyService = processEngineConfiguration.getHistoryService();

		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(currentTaskEntity.getProcessDefinitionId());
		ActivityImpl activity = processDefinition.findActivity(targetTaskDefinitionKey);
		ActivityImpl currentActivity = processDefinition.findActivity(currentTaskEntity.getTaskDefinitionKey());
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(currentTaskEntity.getExecutionId()).singleResult();

//		Object nrOfInstances = runtimeService.getVariable(execution.getId(), "nrOfInstances");
//		Object nrOfCompletedInstances = runtimeService.getVariable(execution.getId(), "nrOfCompletedInstances");
//		Object loopCounter = runtimeService.getVariable(execution.getId(), "loopCounter");
//		Object nrOfActiveInstances = runtimeService.getVariable(execution.getId(), "nrOfActiveInstances");
//		System.out.println(nrOfInstances);
//		System.out.println(nrOfCompletedInstances);
//		System.out.println(loopCounter);
//		System.out.println(nrOfActiveInstances);

		// 删除当前的任务
		// 不能删除当前正在执行的任务，所以要先清除掉关联
		currentTaskEntity.setExecutionId(null);
		taskService.saveTask(currentTaskEntity);
		taskService.deleteTask(currentTaskEntity.getId(), true);
//		if (nrOfActiveInstances != null && (Integer) loopCounter == 0) {
//			runtimeService.removeVariable(execution.getId(), "nrOfInstances");
//			runtimeService.removeVariable(execution.getId(), "nrOfCompletedInstances");
//			runtimeService.removeVariable(execution.getId(), "loopCounter");
//			runtimeService.removeVariable(execution.getId(), "nrOfActiveInstances");
//		} else if (nrOfActiveInstances != null && (Integer) nrOfActiveInstances > 0) {
//			runtimeService.setVariable(execution.getId(), "nrOfActiveInstances", 1);
//			runtimeService.setVariable(execution.getId(), "loopCounter", (Integer) loopCounter - 1);
//			runtimeService.setVariable(execution.getId(), "nrOfCompletedInstances",
//					(Integer) nrOfCompletedInstances - 1);
//		} else {
//
//		}

		// 创建新任务
		// 多实例节点撤回至单个用户节点
		if (StringUtils.isNotBlank(execution.getParentId())
				&& activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
			String execParentId = execution.getParentId();
			ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
			execution.destroy();
			execution.deleteCascade(UserContext.getCurrentUser().getUserName() + "撤回");
			ExecutionEntity executionParent = executionEntityManager.findExecutionById(execParentId);
			executionParent.setActive(true);
			executionParent.executeActivity(activity);
		} else if (activity.getId().equals(currentActivity.getId())) {// 多实例节点内部撤回
			// FIXME 多实例节点回退后，回退节点之前的流程明细消失的问题
			Object loopCounter = execution.getVariable(collectionElementIndexVariable);
			
			String execParentId = execution.getParentId();
			ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
			// 删除原来的分支
//			execution.destroyScope(UserContext.getCurrentUser().getUserName() + "撤回");
			execution.destroy();
			execution.deleteCascade(UserContext.getCurrentUser().getUserName() + "撤回");
			
			// 创建新分支
			ExecutionEntity executionParent = executionEntityManager.findExecutionById(execParentId);
			execution = executionParent.createExecution();
			execution.setActivity(activity);
			try {
				multiInstanceWithdraw(activity, execution, (Integer) loopCounter - 1);
			} catch (Exception e) {
				throw new CustomActivitiException(e.getMessage());
			}
		} else if (activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior) { // 单个用户节点回退至多实例节点
			execution.setActivity(activity);
			ExecutionEntity childExecution = execution.createExecution();

			try {
				multiInstanceWithdraw(activity, childExecution);
//				NativeHistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
//						.createNativeHistoricVariableInstanceQuery()
//						.sql("select RES.* from ACT_HI_VARINST RES WHERE RES.PROC_INST_ID_ = #{procInstId} and RES.NAME_ = #{variableName} order by LAST_UPDATED_TIME_ LIMIT 1 ");
//				historicVariableInstanceQuery.parameter("procInstId", execution.getProcessInstanceId());
//				historicVariableInstanceQuery.parameter("variableName", "nrOfInstances");
//				HistoricVariableInstance variableInstance = historicVariableInstanceQuery.singleResult();
//				nrOfInstances = variableInstance.getValue();
//				System.out.println(runtimeService.getVariable(childExecution.getId(), "nrOfCompletedInstances"));
//				runtimeService.setVariableLocal(childExecution.getId(), "nrOfInstances", nrOfInstances);
//				runtimeService.setVariableLocal(childExecution.getId(), "nrOfActiveInstances", 1);
//				runtimeService.setVariableLocal(childExecution.getId(), "loopCounter", (Integer) nrOfInstances - 1);
//				runtimeService.setVariableLocal(childExecution.getId(), "nrOfCompletedInstances",
//						(Integer) nrOfInstances - 1);
//				System.out.println(childExecution.getId());
//				System.out.println(runtimeService.getVariable(childExecution.getId(), "nrOfCompletedInstances"));
//				
//				executeOriginalBehavior(activity, childExecution, ((Integer) nrOfInstances - 1));
			} catch (Exception e) {
				throw new CustomActivitiException(e.getMessage());
			}
		} else {
			execution.executeActivity(activity);
		}
		return null;
	}

	/**
	 * 多实例节点回退至指定循环
	 * @param activity
	 * @param execution
	 * @param loopCounter
	 * @throws Exception
	 */
	protected void multiInstanceWithdraw(ActivityImpl activity, ActivityExecution execution, int loopCounter) throws Exception {
		MultiInstanceActivityBehavior behavior = (MultiInstanceActivityBehavior) activity.getActivityBehavior();
		if (behavior instanceof SequentialMultiInstanceBehavior) {
			com.dp.plat.activiti.process.behavior.SequentialMultiInstanceBehavior sequentialMultiInstanceBehavior = new com.dp.plat.activiti.process.behavior.SequentialMultiInstanceBehavior(activity, behavior.getInnerActivityBehavior());
			BeanUtils.copyProperties(behavior, sequentialMultiInstanceBehavior);
			sequentialMultiInstanceBehavior.createInstances(execution, loopCounter);
		} else if (behavior instanceof ParallelMultiInstanceBehavior) {
			// TODO 并行多实例节点的回退
		}
	}
	
	/**
	 * 多实例节点回退至最后一次循环
	 * @param activity
	 * @param execution
	 * @throws Exception
	 */
	protected void multiInstanceWithdraw(ActivityImpl activity, ActivityExecution execution) throws Exception {
		MultiInstanceActivityBehavior behavior = (MultiInstanceActivityBehavior) activity.getActivityBehavior();
		if (behavior  instanceof SequentialMultiInstanceBehavior) {
			com.dp.plat.activiti.process.behavior.SequentialMultiInstanceBehavior sequentialMultiInstanceBehavior = new com.dp.plat.activiti.process.behavior.SequentialMultiInstanceBehavior(activity, behavior.getInnerActivityBehavior());
			BeanUtils.copyProperties(behavior, sequentialMultiInstanceBehavior);
			int nrOfInstances = sequentialMultiInstanceBehavior.resolveNrOfInstances(execution);
			sequentialMultiInstanceBehavior.createInstances(execution, nrOfInstances - 1);
		} else if (behavior instanceof ParallelMultiInstanceBehavior) {
			// TODO 并行多实例节点的回退
		}
	}
	
//	protected void createInstances(ActivityExecution execution) throws Exception {
//		int nrOfInstances = resolveNrOfInstances(execution);
//		if (nrOfInstances < 0) {
//			throw new ActivitiIllegalArgumentException(
//					"Invalid number of instances: must be a non-negative integer value" + ", but was " + nrOfInstances);
//		}
//
//		setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
//		setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, 0);
//		setLoopVariable(execution, getCollectionElementIndexVariable(), 0);
//		setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, 1);
//		logLoopDetails(execution, "initialized", 0, 0, 1, nrOfInstances);
//
//		if (nrOfInstances > 0) {
//			executeOriginalBehavior(execution, 0);
//		}
//	}
//
//
//	private int resolveNrOfInstances(ActivityExecution execution) {
//		int nrOfInstances = -1;
//	    if (loopCardinalityExpression != null) {
//	      nrOfInstances = resolveLoopCardinality(execution);
//	    } else if (collectionExpression != null) {
//	      Object obj = collectionExpression.getValue(execution);
//	      if (!(obj instanceof Collection)) {
//	        throw new ActivitiIllegalArgumentException(collectionExpression.getExpressionText()+"' didn't resolve to a Collection");
//	      }
//	      nrOfInstances = ((Collection) obj).size();
//	    } else if (collectionVariable != null) {
//	      Object obj = execution.getVariable(collectionVariable);
//	      if (obj == null) {
//	        throw new ActivitiIllegalArgumentException("Variable " + collectionVariable + " is not found");
//	      }
//	      if (!(obj instanceof Collection)) {
//	        throw new ActivitiIllegalArgumentException("Variable " + collectionVariable+"' is not a Collection");
//	      }
//	      nrOfInstances = ((Collection) obj).size();
//	    } else {
//	      throw new ActivitiIllegalArgumentException("Couldn't resolve collection expression nor variable reference");
//	    }
//	    return nrOfInstances;
//	}

	protected void executeOriginalBehavior(ActivityImpl activity, ActivityExecution execution, int loopCounter)
			throws Exception {
		MultiInstanceActivityBehavior behavior = (MultiInstanceActivityBehavior) activity.getActivityBehavior();
		if (usesCollection(behavior) && behavior.getCollectionElementVariable() != null) {
			Collection collection = null;
			Expression collectionExpression = behavior.getCollectionExpression();
			String collectionVariable = behavior.getCollectionVariable();
			if (collectionExpression != null) {
				collection = (Collection) collectionExpression.getValue(execution);
			} else if (collectionVariable != null) {
				collection = (Collection) execution.getVariable(collectionVariable);
			}

			Object value = null;
			int index = 0;
			Iterator it = collection.iterator();
			while (index <= loopCounter) {
				value = it.next();
				index++;
			}
			setLoopVariable(execution, behavior.getCollectionElementVariable(), value);
		}

		// If loopcounter == 1, then historic activity instance already created,
		// no need to
		// pass through executeActivity again since it will create a new
		// historic activity
		if (loopCounter == 0) {
			callCustomActivityStartListeners(activity, execution);
			behavior.getInnerActivityBehavior().execute(execution);
		} else {
			execution.executeActivity(activity);
		}
	}

	private void callCustomActivityStartListeners(ActivityImpl activity, ActivityExecution execution) {
		List<ExecutionListener> listeners = activity
				.getExecutionListeners(org.activiti.engine.impl.pvm.PvmEvent.EVENTNAME_START);

		List<ExecutionListener> filteredExecutionListeners = new ArrayList<ExecutionListener>(listeners.size());
		if (listeners != null) {
			// Sad that we have to do this, but it's the only way I could find
			// (which is also safe for backwards compatibility)

			for (ExecutionListener executionListener : listeners) {
				if (!(executionListener instanceof ActivityInstanceStartHandler)) {
					filteredExecutionListeners.add(executionListener);
				}
			}

			CallActivityListenersOperation atomicOperation = new CallActivityListenersOperation(
					filteredExecutionListeners);
			Context.getCommandContext().performOperation(atomicOperation, (InterpretableExecution) execution);
		}
	}

	protected void setLoopVariable(ActivityExecution execution, String variableName, Object value) {
		execution.setVariableLocal(variableName, value);
	}

	protected boolean usesCollection(MultiInstanceActivityBehavior behavior) {
		return behavior.getCollectionExpression() != null || behavior.getCollectionVariable() != null;
	}

	/**
	 * ACT-1339. Calling ActivityEndListeners within an {@link AtomicOperation}
	 * so that an executionContext is present.
	 * 
	 * @author Aris Tzoumas
	 * @author Joram Barrez
	 *
	 */
	private static final class CallActivityListenersOperation implements AtomicOperation {

		private List<ExecutionListener> listeners;

		private CallActivityListenersOperation(List<ExecutionListener> listeners) {
			this.listeners = listeners;
		}

		@Override
		public void execute(InterpretableExecution execution) {
			for (ExecutionListener executionListener : listeners) {
				try {
					Context.getProcessEngineConfiguration().getDelegateInterceptor()
							.handleInvocation(new ExecutionListenerInvocation(executionListener, execution));
				} catch (Exception e) {
					throw new ActivitiException("Couldn't execute listener", e);
				}
			}
		}

		@Override
		public boolean isAsync(InterpretableExecution execution) {
			return false;
		}

	}
}
