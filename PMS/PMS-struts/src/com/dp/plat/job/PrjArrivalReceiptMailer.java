package com.dp.plat.job;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 邮件提醒到货验收计划时间周期提醒
 * 
 * @author admin
 *
 */
public class PrjArrivalReceiptMailer implements Job {

	public void execute1(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ProjectDao projectDao = SpringContext.getApplicationContext().getBean("projectDao", ProjectDao.class);
		List<ProjectTask> projectTasks = projectDao.queryProjectArrivalReceipt();
		for (ProjectTask projectTask : projectTasks) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP);
			context.put("projectName", projectTask.getProjectName());
			context.put("taskName", projectTask.getEventKeyStr());
			context.put("instruction", simpleDateFormat.format(projectTask.getEventPlanHappenDate()));
			String backcase = null;
			String content = null;
			String deliverName = null;
			String instruction = "将于" + simpleDateFormat.format(projectTask.getEventPlanHappenDate()) + "到期，";
			if ("41".equals(projectTask.getEventValueStr())) {
				content = "到货签收已于" + simpleDateFormat.format(projectTask.getEventPlanHappenDate()) + "到期，目前超期10天！";
				backcase = "请尽快联系客户办理到货签收。如该项目到货有问题，请另外单独邮件反馈说明原因";
				deliverName = "超期";
				instruction = "";
			} else if ("20".equals(projectTask.getEventValueStr())){
				content = "已经发货20天，";
				backcase = "请尽快联系客户办理到货签收，以免超期罚款";
				deliverName = "20天";
			} else {
				content = "已经发货10天，";
				backcase = "请及时联系客户办理到货签收";
				deliverName = "10天";
			}
			context.put("content", content);
			context.put("backcase", backcase);
			context.put("deliverName", deliverName);
			context.put("instruction", instruction);
			
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
//			// 抄送工程管理部人员
//			String engMail = projectDao.queryMailByRoleId(MessageUtil.ROLE_ENGINEEMANAGER);
//			if (StringUtils.isNotBlank(engMail)) {
//				ccs.append(";").append(engMail);
//			}
			// 抄送验收小组群组邮箱
			BasicDataDao basicDataDao = SpringContext.getApplicationContext().getBean("basicDataDao", BasicDataDao.class);
			String acceptanceMail = basicDataDao.querySysArg("acceptance.mail");
			if (StringUtils.isNotBlank(acceptanceMail)) {
				ccs.append(";").append(acceptanceMail);
			}
			
			context.put("username", tosUserName.substring(1));
			context.put("tos", tos);
			context.put("ccs", ccs);
			NotificationTemplateUtil.keepMailByDao(context);
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ApplicationContext applicationContext = null;
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
		List<ProjectTask> projectTasks = projectDao.queryProjectArrivalReceipt();
		
		BasicDataDao basicDataDao = SpringContext.getApplicationContext().getBean("basicDataDao", BasicDataDao.class);
		// 邮件提醒频率
		String timeLine = basicDataDao.querySysArg("arrivalReceipt.mailTimes");
		String[] days = StringUtils.split(timeLine, ";");
		String[] flagDays = StringUtils.split(days[0], ",");
		int endlineDay = Integer.valueOf(days[1]);
		int endlineDayIndex =  ArrayUtils.indexOf(flagDays, days[1]);
		for (ProjectTask projectTask : projectTasks) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP);
			context.put("projectName", projectTask.getProjectName());
			context.put("taskName", projectTask.getEventKeyStr());
			context.put("instruction", simpleDateFormat.format(projectTask.getEventPlanHappenDate()));
			String eventDays = projectTask.getEventValueStr(); 
			String backcase = null;
			String deliverName = eventDays + "天";
			String content = "已经发货" + deliverName + "，";
			String instruction = "将于" + simpleDateFormat.format(projectTask.getEventPlanHappenDate()) + "到期，";
			int index = ArrayUtils.indexOf(flagDays, eventDays);
			if (index > endlineDayIndex) {
				int delim = Integer.valueOf(eventDays) - endlineDay;
				content = "到货签收已于" + simpleDateFormat.format(projectTask.getEventPlanHappenDate()) + "到期，目前超期" + delim + "天！";
				backcase = "请尽快联系客户办理到货签收。如该项目到货有问题，请另外单独邮件反馈说明原因";
				deliverName = "超期";
				instruction = "";
			} else if (index == 0){
				backcase = "请及时联系客户办理到货签收";
			} else {
				backcase = "请尽快联系客户办理到货签收，以免超期罚款";
			}
			context.put("content", content);
			context.put("backcase", backcase);
			context.put("deliverName", deliverName);
			context.put("instruction", instruction);
			
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
//			// 抄送工程管理部人员
//			String engMail = projectDao.queryMailByRoleId(MessageUtil.ROLE_ENGINEEMANAGER);
//			if (StringUtils.isNotBlank(engMail)) {
//				ccs.append(";").append(engMail);
//			}
			// 抄送验收小组群组邮箱
			String acceptanceMail = basicDataDao.querySysArg("acceptance.mail");
			if (StringUtils.isNotBlank(acceptanceMail)) {
				ccs.append(";").append(acceptanceMail);
			}
			
			context.put("username", tosUserName.substring(1));
			context.put("tos", tos);
			context.put("ccs", ccs);
			NotificationTemplateUtil.keepMailByDao(context);
		}
	}
	
	public static void main(String[] args) throws JobExecutionException {
		new PrjArrivalReceiptMailer().execute(null);
	}
}
