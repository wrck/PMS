package com.dp.plat.taskHandler;

import javax.servlet.ServletContext;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.service.PresalesService;
/**
 * 项目管理部直接闭环售前项目时触发
 * @author admin
 *
 */
public class PresalesClose20TaskHandler implements ExecutionListener{

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
			int presalesId = Integer.parseInt(split[1]);
			PresalesService presalesService = ctx.getBean("presalesService" , PresalesService.class);
			presalesService.updateEnding20PresalesProject(presalesId);
		}
	}

}
