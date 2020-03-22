package com.dp.plat.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.service.OpLogService;

public class PreformanceThresholdInterceptor implements MethodInterceptor
{	
	private OpLogService opLogService;
	

	public OpLogService getOpLogService()
	{
		return opLogService;
	}
	public void setOpLogService(OpLogService opLogService)
	{
		this.opLogService = opLogService;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable
	{
		Object o = mi.proceed();
		try {
		    UserContext userContext = (UserContext) SpringContext.getBean("userContext");
			if(!userContext.getOption().equals("")){
				opLogService.insertLog();
				userContext.setOption("");
			}
		} catch (Exception e) {
		}
		return o;
	}

}
