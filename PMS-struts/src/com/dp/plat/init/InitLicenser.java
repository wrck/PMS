package com.dp.plat.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 本类中无法使用PContext.getBean, 需要使用的初始化需要放到InitLastLicenseser中
 * @author admin
 *
 */
public class InitLicenser implements ServletContextListener 
{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		//dut.flag = false;
		//dut.interrupt();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletcontextevent) 
	{
		/**
		 * 初始化系统属性
		 * webapp.rootpath web应用的根目录
		 * */
		String rootpath = servletcontextevent.getServletContext().getRealPath("/");
		System.setProperty("webapp.rootpath", rootpath);
	}
}

