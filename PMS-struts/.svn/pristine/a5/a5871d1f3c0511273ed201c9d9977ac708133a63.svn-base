package com.dp.plat.job;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 邮件提醒初验。终验计划时间前一个月
 * 
 * @author admin
 *
 */
public class PrjPreAndFinalInspectionMailer implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ProjectDao projectDao = SpringContext.getApplicationContext().getBean("projectDao", ProjectDao.class);
		List<ProjectTask> projectTasks = projectDao.queryProjectPreAndFinalInspection();
		for (ProjectTask projectTask : projectTasks) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP);
			context.put("projectName", projectTask.getProjectName());
			context.put("taskName", projectTask.getEventKeyStr());
			context.put("instruction", simpleDateFormat.format(projectTask.getEventPlanHappenDateENG()));

			List<ProjectMember> members = projectDao.queryValidMemberByProjectId(projectTask.getProjectId());
			String tos = "";
			String tosUserName = "";
			String ccs = "";
			for (ProjectMember projectMember : members) {
				if (MessageUtil.MEMBER_PM.equals(projectMember.getMemberRole())) {
					String to = projectMember.getEmail();
					if (StringUtils.isBlank(to)) {
						to = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(to)) {
						tos += ";" + to;
						tosUserName += "、" + projectMember.getMemberName();
					}
				} else if (MessageUtil.MEMBER_SM.equals(projectMember.getMemberRole())
						|| MessageUtil.MEMBER_SALESMAN.equals(projectMember.getMemberRole())) {
					String cs = projectMember.getEmail();
					if (StringUtils.isBlank(cs)) {
						cs = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(cs)) {
						ccs += ";" + cs;
					}
				}
			}

			context.put("username", tosUserName.substring(1));
			context.put("tos", tos);
			context.put("ccs", ccs);
			NotificationTemplateUtil.keepMailByDao(context);
		}
	}

	public static void main(String[] args) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
		List<ProjectTask> projectTasks = projectDao.queryProjectPreAndFinalInspection();
		for (ProjectTask projectTask : projectTasks) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP);
			context.put("projectName", projectTask.getProjectName());
			context.put("taskName", projectTask.getEventKeyStr());
			context.put("instruction", simpleDateFormat.format(projectTask.getEventPlanHappenDate()));
			List<ProjectMember> members = projectDao.queryValidMemberByProjectId(projectTask.getProjectId());
			// String tos = "";
			// String tosUserName = "";
			// String ccs = "";
			StringBuilder tos = new StringBuilder();
			StringBuilder tosUserName = new StringBuilder();
			StringBuilder ccs = new StringBuilder();
			for (ProjectMember projectMember : members) {
				if (MessageUtil.MEMBER_PM.equals(projectMember.getMemberRole())
						|| MessageUtil.MEMBER_SM.equals(projectMember.getMemberRole())) {
					String to = projectMember.getEmail();
					if (StringUtils.isBlank(to)) {
						to = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(to) && tos.indexOf(to) == -1) {
						tos.append(";").append(to);
						tosUserName.append("、").append(projectMember.getMemberName());
						// tos += ";" + to;
						// tosUserName += "、" + projectMember.getMemberName();
					}
				} else if (MessageUtil.MEMBER_SALESMAN.equals(projectMember.getMemberRole())) {
					String cs = projectMember.getEmail();
					if (StringUtils.isBlank(cs)) {
						cs = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(cs)) {
						ccs.append(";").append(cs);
						// ccs += ";" + cs;
					}
				}
			}
			// 抄送工程管理部人员
			String engMail = projectDao.queryMailByRoleId(MessageUtil.ROLE_ENGINEEMANAGER);
			if (StringUtils.isNotBlank(engMail)) {
				ccs.append(";").append(projectDao.queryMailByRoleId(MessageUtil.ROLE_ENGINEEMANAGER));
				// ccs += ";" +
				// projectDao.queryMailByRoleId(MessageUtil.ROLE_ENGINEEMANAGER);
			}
			
			context.put("username", tosUserName.substring(1));
			context.put("tos", tos);
			context.put("ccs", ccs);
			NotificationTemplateUtil.keepMailByDao(context);
		}
	}
}
