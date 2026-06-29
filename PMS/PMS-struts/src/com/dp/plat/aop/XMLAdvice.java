package com.dp.plat.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import com.dp.plat.context.SpringContext;
import com.dp.plat.data.bean.Project;
import com.dp.plat.service.ProjectService;

public class XMLAdvice {
	 /** 
     * 在核心业务执行前执行，不能阻止核心业务的调用。 
     * @param joinPoint 
     */  
    @SuppressWarnings("unused")
	private void doBefore(JoinPoint joinPoint ) { 
    	Project p = (Project) joinPoint.getArgs()[0];
    	  System.out.println("----doBefore---");
    }  
      
    /** 
     * 手动控制调用核心业务逻辑，以及调用前和调用后的处理, 
     *  
     * 注意：当核心业务抛异常后，立即退出，转向After Advice 
     * 执行完毕After Advice，再转到Throwing Advice 
     * @param pjp 
     * @return 
     * @throws Throwable 
     */  
    @SuppressWarnings("unused")
	private Object doAround(ProceedingJoinPoint pjp) throws Throwable {  
        //调用核心逻辑  
        Object retVal = pjp.proceed();
        System.err.println("-----doAround----");
        return retVal;  
    }  
  
    /** 
     * 核心业务逻辑退出后（包括正常执行结束和异常退出），执行此Advice 
     * @param joinPoint 
     */  
    @SuppressWarnings("unused")
	private void doAfter(JoinPoint joinPoint) {  
    	System.err.println("-----doAfter----");
    	Project p = (Project) joinPoint.getArgs()[0];
    	ProjectService projectService = (ProjectService) SpringContext.getBean("projectService");
    	projectService.insertLog(joinPoint.getStaticPart().toString(), null, p.getProjectId());
    }  
      
    /** 
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice 
     * @param joinPoint 
     */  
    @SuppressWarnings("unused")
	private void doReturn(JoinPoint joinPoint ) {  
    	System.err.println("-----doReturn----");
    }  
      
    /** 
     * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息 
     * @param joinPoint 
     * @param ex 
     */  
    @SuppressWarnings("unused")
	private void doThrowing(JoinPoint joinPoint,Throwable ex) {  
    	System.err.println("-----doThrowing----");
    }  
}
