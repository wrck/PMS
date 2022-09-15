package com.dp.plat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.activiti.engine.TaskService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.Project;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.ProjectService;

public class ProjectUtils extends WorkflowUtil {

	public static String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb) {
		ServletContext sc = ServletActionContext.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
		PmClosedLoopService pmClosedLoopService = ctx.getBean("pmClosedLoopService", PmClosedLoopService.class);
		TaskService taskService = ctx.getBean("taskService", TaskService.class);

		String newMemberCode = batchCgMb.getNewMemberName().split("-")[0];
		String oldMemberCode = batchCgMb.getOldMemberCode();
		batchCgMb.setNewMemberCode(newMemberCode);
		String changeType = batchCgMb.getChangeType();
		// batchChangeResult =
		// userManageService.updateServiceAndProgramMember(batchCgMb);
		DisplayParam displayParam = new DisplayParam();
		displayParam.setExport(true);

		int serviceCount = 0;
		int programCount = 0;
		StringBuffer projectIds = new StringBuffer();

		// 查询项目状态30，31，32，指定部门的项目
		Project project = new Project();
		project.setColumn001(batchCgMb.getDpNo());
		// project.setProjectState(
		// MessageUtil.PROJECT_STATE_30 + "," + MessageUtil.PROJECT_STATE_31 +
		// "," + MessageUtil.PROJECT_STATE_32);

		// 保存更改的项目中存在工作流的taskID
		List<String> tasks = new ArrayList<String>();
		// 变更服务经理
		if (changeType.equals("service") || changeType.equals("both")) {
			project.setServiceManagerCode(oldMemberCode);
			List<Project> projects = projectService.queryProjectListByOfficeAndMemberCode(project);
			for (Project projectTemp : projects) {
				// 更新项目服务经理
				projectTemp.setDataTypeCode("20");
				projectTemp.setUpdateBy(UserContext.getUserContext().getUsername());
				projectTemp.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
				boolean flag = projectService.updateProjectMember(projectTemp, newMemberCode,
						batchCgMb.getNewMemberName());
				// 是否发生变更，如果变更则更新项目以及记录项目闭环流程taskID
				if (flag) {// 指定服务经理后，更新项目状态
					serviceCount++;
					projectService.updateProjectStatus(projectTemp.getProjectId(), projectTemp.getProjectState());
					projectIds.append("'" + projectTemp.getProjectId() + "',");
					String taskId = pmClosedLoopService.queryTaskByBussinessKeyAndUser(projectTemp, oldMemberCode);
					if (taskId != null)
						tasks.add(taskId);
				}
			}
			// 服务经理更新pm_cl_evaluation_header，中的nextPerson,审批人发生变更，并且变更流程审批人
			if (projectIds.length() > 0) {
				HashMap<String, String> params = new HashMap<>();
				params.put("oldNextAcceptPerson", oldMemberCode);
				params.put("nextAcceptPerson", newMemberCode);
				params.put("nextAcceptPersonName", batchCgMb.getNewMemberName().split("-")[1]);
				params.put("projectIds", projectIds.substring(0, projectIds.length() - 1));
				pmClosedLoopService.updateEvaluationHeaderNextAcceptPerson(params);
				for (String taskId : tasks) {
					taskService.setAssignee(taskId, batchCgMb.getNewMemberCode());
				}
				// // 服务经理更新项目闭环流程的审批人
				// List<Task> tasks=
				// taskService.createTaskQuery().processDefinitionKey("PmClosedLoop").taskAssignee(batchCgMb.getOldMemberCode()).list();
				// for (Task task : tasks) {
				// taskService.setAssignee(task.getId(),
				// batchCgMb.getNewMemberCode());
				// }
			}

		}
		// if(projectIds.length()>0)
		// projectIds.delete(0, projectIds.length() - 1);
		if (changeType.equals("program") || changeType.equals("both")) {
			project.setServiceManagerCode(null);
			project.setProgramManagerCode(oldMemberCode);
			List<Project> projects = projectService.queryProjectListByOfficeAndMemberCode(project);
			for (Project projectTemp : projects) {
				projectTemp.setDataTypeCode("30");
				projectTemp.setUpdateBy(UserContext.getUserContext().getUsername());
				projectTemp.setOldMemberCode(oldMemberCode);
				if (oldMemberCode.equals(projectTemp.getProgramManagerCodeB()))
					projectTemp.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
				else
					projectTemp.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
				boolean flag = projectService.updateProjectMember(projectTemp, newMemberCode,
						batchCgMb.getNewMemberName());
				if (flag) {
					programCount++;
					terminateProgramManagerActivities(projectTemp);
				}
			}
		}
		return serviceCount + ":" + programCount;
	}

	/**
	 * 项目经理更新时，终止在项目经理手中的闭环申请,回访申请
	 * @param project
	 */
	public synchronized static void terminateProgramManagerActivities(Project project){
		ServletContext sc = ServletActionContext.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
		CallBackService callBackService = ctx.getBean("callBackService", CallBackService.class);
		PmClosedLoopService pmClosedLoopService = ctx.getBean("pmClosedLoopService", PmClosedLoopService.class);
		List<String> taskIds = new ArrayList<String>();
		String oldMemberCode = project.getOldMemberCode();
		String taskId = pmClosedLoopService.queryTaskByBussinessKeyAndUser(project, oldMemberCode);
		if(taskId !=null){
			taskIds.add(taskId);
		}
		List<CallBack> callBacks = projectService.queryCallBackRunList(project.getProjectId(),ActivityMessage.FLOW_RUNING);
		for (CallBack callBack : callBacks) {
			taskId = callBack.getTaskId();
			// 存在回访流程，并且回访流程在项目经理环节，终止该回访流程
			if (StringUtils.isNotBlank(taskId)) {
				String assignee = callBack.getTaskAssignee();
				// 回访流程在项目经理手中则终止
				if (assignee.equals(oldMemberCode)) {
					taskIds.add(taskId);
					// 更新回访审批状态为驳回
					callBackService.updateCallBackApplyState(callBack.getCallBackId(), ActivityMessage.FLOW_REJECT);
				}
			}else{// 不存在回访流程，则将回访审批状态改为驳回
				callBackService.updateCallBackApplyState(callBack.getCallBackId(), ActivityMessage.FLOW_REJECT);
			}
		}
		terminateActivities(taskIds, "项目经理更新终止在待办流程");
	}
	
}
