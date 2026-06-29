package com.dp.plat.core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.dp.plat.core.annotation.DataSource;
import com.dp.plat.core.config.DataSourceHolder;

@Aspect
@Component
public class DataSourceAspect {
	//@Pointcut("@annotation(com.dp.plat.core.annotation.DataSource)")
	//@Pointcut("execution(* cn.us.service.impl.UserServiceImpl.*(..))")
	@Pointcut("@within(com.dp.plat.core.annotation.DataSource) || @annotation(com.dp.plat.core.annotation.DataSource)")
	public void serviceAspect() {
	}
	
	@Before("serviceAspect()")
	public void doBefore(JoinPoint joinPoint) {
		try {
			Class<?> targetClass = joinPoint.getTarget().getClass();
			String dataSource = "";
			Boolean isClassHasAnn = targetClass.isAnnotationPresent(DataSource.class);
			if (isClassHasAnn) {
				dataSource = targetClass.getAnnotation(DataSource.class).value();
			}
			String methodDataSource = getMethodAnnotationValue(joinPoint);
			if (methodDataSource != null && !methodDataSource.equals(dataSource)) {
				dataSource = methodDataSource;
			}
			DataSourceHolder.setDataSourceType(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After("serviceAspect()")
	public void doAfter(JoinPoint joinPoint) {
		try {
			DataSourceHolder.setDataSourceType("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getMethodAnnotationValue(JoinPoint joinPoint) throws Exception {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		DataSource methodDataSource = signature.getMethod().getAnnotation(DataSource.class);
		if (methodDataSource != null) {
			return methodDataSource.value();
		}
		return null;
	}
}