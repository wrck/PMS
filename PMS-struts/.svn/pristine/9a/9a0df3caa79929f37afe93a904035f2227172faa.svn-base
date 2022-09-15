package com.dp.plat.job;

import java.io.Reader;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.MailContent;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.SendMailService;
import com.dp.plat.util.MailHandleUtil;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.Util;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class CloseNotTrackProject implements Job{
//	private static ApplicationContext ctx;
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            work();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@SuppressWarnings("unchecked")
	public static void work() throws Exception {
		ApplicationContext ctx = SpringContext.getApplicationContext();
		if (ctx == null) {
		    ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
//		ProjectDao projectDao = ctx.getBean("projectDao", ProjectDao.class);
		ProjectService projectService = ctx.getBean("projectServiceAgent", ProjectService.class);
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		// 创建临时表，得出项目发货状态
		List<Project> projects = sqlMap.queryForList("query_pm_self_service_project");
		for (Project project : projects) {
		    try {
    			List<ProjectMember> serviceManagers = sqlMap.queryForList("query_project_serviceManager_by_officeCode", project.getColumn001());
    			// 服务经理为空不创建项目
    			if (serviceManagers.isEmpty() || StringUtils.isBlank(serviceManagers.get(0).getMemberCode())) {
    				continue;
    			}
    			ProjectMember serviceManager = serviceManagers.get(0);
    			project = projectService.queryProjectByContractNo(project.getContractNo());
//    			String projectCode = project.getProjectCode();
//    			int lastCode = projectService.queryProjectGroupSize(projectCode);
//    			project.setProjectCode(projectCode + "-" +lastCode);
    			String projectCode =  projectService.queryProjectCode(project);
    			project.setProjectCode(projectCode);
    			Integer count = projectService.queryProjectContractCountByContractNo(Util.appendChar(project.getContractNo(), "'"));
    			if (count != null && count != 0) {
    				continue;
    			}
    			
    			project.setServiceManagerCode(serviceManager.getMemberCode());
    			project.setServiceManagerCodeforjson(serviceManager.getMemberName());
    			project.setColumn008("系统自动不予跟踪代理商/用户自服项目！");
    			project.setColumn010(MessageUtil.PROJECT_TYPE_NORMAL);
    			String contractNo = project.getContractNo();
    			String diff = contractNo.substring(0,3);
    			// 合同号不为312，313开头的都是非直签项目
    			if(!"312".equals(diff) && !"313".equals(diff)){
    				project.setColumn011("20");
    			}else{
    				project.setColumn011("10");
    			}
    			projectService.insertProject(project);// 保存
    			
    			// 立项通知邮件
    			sendMailForApproval(project, projectService);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}

	/**
	 * @param project
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
    private static void sendMailForApproval(Project project, ProjectDao projectDao) throws Exception {
		if (!MessageUtil.PROJECT_STATE_DENY.equals(project.getProjectState())) {
			// 通知邮件
			NotificationTemplate template = null;
			if (MessageUtil.PROJECT_TYPE_NORMAL.equals(project.getColumn010())) {// 普通类
				template = projectDao.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_NORMAL);
			} else if (MessageUtil.PROJECT_TYPE_ENGINEE.equals(project.getColumn010())) {// 工程类
				template = projectDao.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_ENGINEE);
			}
			String serviceUsername = project.getServiceManagerCode();
			String serviceMail = projectDao.queryMailByUsername(serviceUsername);
			project.setTos(serviceMail);// 主送服务经理
			// 立项通知抄送工程管理部屏蔽掉
			keepMailInfo(project, template, project.getServiceManagerCodeforjson());
		}
	}

	/**
     * @param project
     * @throws Exception
     */
    private static void sendMailForApproval(Project project, ProjectService projectService) throws Exception {
        if (!MessageUtil.PROJECT_STATE_DENY.equals(project.getProjectState())) {
            // 通知邮件
            NotificationTemplate template = null;
            if (MessageUtil.PROJECT_TYPE_NORMAL.equals(project.getColumn010())) {// 普通类
                template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_NORMAL);
            } else if (MessageUtil.PROJECT_TYPE_ENGINEE.equals(project.getColumn010())) {// 工程类
                template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_ENGINEE);
            }
            String serviceUsername = project.getServiceManagerCode();
            String serviceMail = projectService.getMails(serviceUsername);
            project.setTos(serviceMail);// 主送服务经理
            // 立项通知抄送工程管理部屏蔽掉
            keepMailInfo(project, template, project.getServiceManagerCodeforjson());
        }
    }
    
	/**
	 * @param project
	 * @param template
	 * @param serviceManagerCodeforjson
	 * @throws Exception
	 */
	private static void keepMailInfo(Project project, NotificationTemplate template, String userName) throws Exception {
		if (template != null) {// 如果有模板，则后续保存到邮件表中
			MailSenderInfo info = new MailSenderInfo();
			// 创建替换变量对象，将需要替换的变量置入
			MailContent mc = new MailContent();
			mc.setProjectName(project.getProjectName());
			mc.setUsername(userName);
			mc.setOfficeName(project.getOfficeName());
			mc.setBackcase(project.getColumn014());
			info.setSubject(MailHandleUtil.dealwithMail(template.getNotificationSubject(), mc));// 邮件主题替换
			info.setContent(MailHandleUtil.dealwithMail(template.getNotificationContent(), mc));// 邮件内容替换

			ServletContext sc = ServletActionContext.getServletContext();
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
			BasicDataService basicDataService = ctx.getBean("basicDataService", BasicDataService.class);
			SendMailService sendMailService = ctx.getBean("sendMailService", SendMailService.class);
			String arg = basicDataService.querySysArg("sys.envirment.argu");
			if (arg.equals("0")) {// 测试环境
				info.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
			} else {
				info.setTos(project.getTos());
				info.setCcs(project.getCos());
			}
			sendMailService.keepMailInfo(info);
		}
	}

	public static void main(String[] args) throws Exception {
//		ColseNotTrackProject.ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		CloseNotTrackProject.work();
	}

}
