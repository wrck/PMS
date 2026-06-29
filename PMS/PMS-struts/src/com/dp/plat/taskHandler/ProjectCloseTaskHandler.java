package com.dp.plat.taskHandler;

import javax.servlet.ServletContext;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.service.ProjectService;
import com.dp.plat.util.MessageUtil;
/**
 * 
 * 闭环申请审批通过后更新项目闭环时间
 * 更新项目计划状态到“项目闭环”
 * @author admin
 *
 */
public class ProjectCloseTaskHandler implements ExecutionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		ServletContext sc = ServletActionContext.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		String businessKey = execution.getProcessBusinessKey();
		if(StringUtils.isNotBlank(businessKey)){
			String[] split = businessKey.split("\\.");
			int closeObjId = Integer.parseInt(split[2]);
			ProjectService projectService = ctx.getBean("projectService",ProjectService.class);
			projectService.updateProjectCloseTime(closeObjId);//更新项目闭环时间
			projectService.updateProjectPlanStateToClose(closeObjId);//更新项目工程计划状态
			int projectId = projectService.queryProjectIdBycloseId(closeObjId);
			projectService.updateProjectLastRefreshTime(projectId);
			//系统通知
			projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_119, projectId);
		}
		
	}
}
