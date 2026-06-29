package com.dp.plat.taskHandler;

import javax.servlet.ServletContext;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.service.CallBackService;
import com.dp.plat.util.ActivityMessage;
/**
 *  回访审批通过后发生的事
 * @author admin
 *
 */
public class CallBackTaskHandler implements ExecutionListener{

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
			int callBackId = Integer.parseInt(split[1]);
			CallBackService callBackService = ctx.getBean("callBackService",CallBackService.class);
			callBackService.updateCallBackApplyState(callBackId , ActivityMessage.FLOW_PASS);
		}
	}

}
