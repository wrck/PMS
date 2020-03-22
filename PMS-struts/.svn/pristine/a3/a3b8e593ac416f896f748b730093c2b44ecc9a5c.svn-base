package com.dp.plat.job;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.init.SpringInit;
import com.dp.plat.service.ReportService;


public class ReportDataTask{
	
	
	//数据类型定义
	
	/**
	 * 任务内容
	 */
	public static void work() {
		try {
			ReportService reportService = SpringInit.getApplicationContext().getBean("reportServiceAgent" , ReportService.class);
			reportService.keepReportLineData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ReportService reportService = context.getBean("reportServiceAgent" , ReportService.class);
		reportService.keepReportLineData();
	}
}
