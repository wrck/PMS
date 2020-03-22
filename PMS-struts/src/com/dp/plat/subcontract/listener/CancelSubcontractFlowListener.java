package com.dp.plat.subcontract.listener;

import java.util.List;

import javax.servlet.ServletContext;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.service.WorkFlowService;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.subcontract.utils.ActivitiUtils;
import com.dp.plat.util.ActivityMessage;

/**
 * 回访审批通过后发生的事
 * 
 * @author admin
 *
 */
public class CancelSubcontractFlowListener implements JavaDelegate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@Override
//	public void notify(DelegateExecution execution) throws Exception {
//		ServletContext sc = ServletActionContext.getServletContext();
//		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
//		// String businessKey = execution.getProcessBusinessKey();
//		Integer subcontractId = (Integer) execution.getVariable("subcontractId");
//		if (subcontractId != null && subcontractId != 0) {
//			TaskService taskService = ctx.getBean("taskService", TaskService.class);
//			List<Task> taskList = taskService.createTaskQuery()
//					.processDefinitionKey(SubcontractConstant.PROCESS_SUBCONTRACT_KEY)
//					.processVariableValueEquals("subcontractId", subcontractId).active().list();
//			ActivitiUtils.terminateActivities(taskList, "回访不通过，终止项目转包流程！");
//			WorkFlowService workFlowService = ctx.getBean("workFlowService", WorkFlowService.class);
//			for (Task task : taskList) {
//				workFlowService.addSelfActComment(subcontractId, SubcontractConstant.PROCESS_SUBCONTRACT_KEY,
//						task.getId(), task.getProcessInstanceId(), ActivityMessage.COMMENT_REJECT, "回访不通过，终止项目转包流程！");
//			}
//		}
//	}

//	@Override
//	public void notify(DelegateTask delegateTask) {
//		ServletContext sc = ServletActionContext.getServletContext();
//		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
//		// String businessKey = execution.getProcessBusinessKey();
//		Integer subcontractId = (Integer) delegateTask.getVariable("subcontractId");
//		if (subcontractId != null && subcontractId != 0) {
//			TaskService taskService = ctx.getBean("taskService", TaskService.class);
//			List<Task> taskList = taskService.createTaskQuery()
//					.processDefinitionKey(SubcontractConstant.PROCESS_SUBCONTRACT_KEY)
//					.processVariableValueEquals("subcontractId", subcontractId).active().list();
//			ActivitiUtils.terminateActivities(taskList, "回访不通过，终止项目转包流程！");
//			WorkFlowService workFlowService = ctx.getBean("workFlowService", WorkFlowService.class);
//			for (Task task : taskList) {
//				workFlowService.addSelfActComment(subcontractId, SubcontractConstant.PROCESS_SUBCONTRACT_KEY,
//						task.getId(), task.getProcessInstanceId(), ActivityMessage.COMMENT_REJECT, "回访不通过，终止项目转包流程！");
//			}
//		}
//	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ServletContext sc = ServletActionContext.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		// String businessKey = execution.getProcessBusinessKey();
		Integer subcontractId = (Integer) execution.getVariable("subcontractId");
		if (subcontractId != null && subcontractId != 0) {
			TaskService taskService = ctx.getBean("taskService", TaskService.class);
			List<Task> taskList = taskService.createTaskQuery()
					.processDefinitionKey(SubcontractConstant.PROCESS_SUBCONTRACT_KEY)
					.processVariableValueEquals("subcontractId", subcontractId).active().list();
			ActivitiUtils.terminateActivities(taskList, "回访不通过，终止项目转包流程！");
			WorkFlowService workFlowService = ctx.getBean("workFlowService", WorkFlowService.class);
			for (Task task : taskList) {
				workFlowService.addSelfActComment(subcontractId, SubcontractConstant.PROCESS_SUBCONTRACT_KEY, task.getTaskDefinitionKey(),
						task.getId(), task.getProcessInstanceId(), ActivityMessage.COMMENT_REJECT, "回访不通过，终止项目转包流程！", "回访不通过", "回访不通过");
			}
		}
	}

}
