package com.dp.plat.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.dao.ProjectDaoImpl;
import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.StringEscUtil;

/**
 * 项目维护服务交付邮件提醒定时器（策略调优服务、高级维保服务）
 * 
 * @author admin
 */
public class MaintenanceServiceQuarterMailer implements Job {
	private final static String MAINTENANCE_SERVICE_REPORT_INFO_TEMPLATE = "maintenanceServiceReportInfo";
	private final static String MAIL_PATTERN = "^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$";

	private ApplicationContext applicationContext = null;

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("###############项目维护服务交付发送开始################");
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		ProjectDaoImpl projectDao = applicationContext.getBean("projectDao", ProjectDaoImpl.class);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> params = new HashMap<>();
		Date reportDate = new Date();
		params.put("currentDate", reportDate);
		params.put("currentTime", reportDate);

		try {
			// 获取交付件、服务、次数参数
			BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);
			String serviceConfig = basicDataDao.querySysArg("pm.project.maintenance.serviceDelivery.deliverFile");
			if (StringUtils.isBlank(serviceConfig)) {
				serviceConfig = StringEscUtil.getText("pm.project.maintenance.serviceDelivery.deliverFile");
			}
			// 根据键值对匹配每个服务对应的交付件、服务名、编码、年服务次数等信息
			Map<String, Map<String, Object>> serviceMap = matchServiceDeliveryConfigMap(serviceConfig, null);
			if (serviceMap == null || serviceMap.isEmpty()) {
				return;
			}
			
			// 设置group_contract的最大长度，默认1024
			projectDao.getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
			projectDao.getSqlMapClientTemplate().insert("createTempProjectWarrantyStateTable");
			params.put("skipDropTempTable", true);
			params.put("serviceTypes", serviceMap.keySet().toArray());
			params.put("serviceDate", reportDate);
			params.put("serviceQuarter", true);
			params.put("hasQuaryerDeliveried", false);
			for (Entry<String, Map<String, Object>> serviceTypeMap : serviceMap.entrySet()) {
				String serviceType = serviceTypeMap.getKey();
				Map<String, Object> config = serviceTypeMap.getValue();
				
				// 查询服务的每年次数、服务类型
				String deliverNames = (String) (config.get("deliverNames") != null ? config.get("deliverNames") : "");
				String serviceName = (String) (config.get("serviceName") != null ? config.get("serviceName") : "");
				Integer yearCount = (Integer) (config.get("yearCount") != null ? config.get("yearCount") : 4);
//				String serviceYear = (String) (config.get("serviceYear") != null ? config.get("serviceYear") : "");
				String serviceCode = (String) (config.get("serviceCode") != null ? config.get("serviceCode") : "");
				Integer deliverId = (Integer) (config.get("deliverId") != null ? config.get("deliverId") : 0);

				params.put("serviceType", serviceType);
//				params.put(serviceType + "Date", reportDate);
				List<Map<String, Object>> serviceProjectList = projectDao.selectProjectMaintenanceServiceDeliveryMapList(params);
				for (Map<String, Object> serviceProject : serviceProjectList) {
					try {
						// 判断是否在服务期限内
						if (!Boolean.TRUE.equals(
								Boolean.parseBoolean(String.valueOf(serviceProject.get(serviceCode + "Enable"))))) {
							continue;
						}

						// 查询项目维护对应的项目上传交付件的次数
						Date[] nearlyYearDates = DateUtil.getNearlyYearDates(reportDate,
								(Date) serviceProject.get(serviceCode + "StartTime"),
								(Date) serviceProject.get(serviceCode + "EndTime"));
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("projectId", serviceProject.get("projectId"));
						map.put("deliverId", deliverId);
						map.put("checkDate", true);
						map.put("serviceType", serviceType);
						map.put("serviceDate", reportDate);
						map.put("startDate", nearlyYearDates[0]);
						map.put("endDate", nearlyYearDates[1]);
						map.put("mergeQuarterCount", true);
						Map<String, Long> counts = projectDao.queryProjectMaintenanceDeliverCount(map);
						Long serviceCount = counts.get("count");
						Long quarterCount = Long.valueOf(String.valueOf(counts.get("quarterCount")));
						
						if (quarterCount > 0 && serviceCount >= yearCount) {
							continue;
						}

						String startDate = simpleDateFormat.format(nearlyYearDates[0]);
						String endDate = simpleDateFormat.format(nearlyYearDates[1]);
						Map<String, Object> context = new HashMap<>();
						context.put("infoType", "提醒");
						context.put("deliverName", deliverNames);
						context.put("serviceName", serviceName);
						context.put("quarterCount", quarterCount);
						context.put("serviceCount", serviceCount);
						context.put("remainedCount", yearCount - serviceCount);
						context.put("content", String.format("请尽快在服务周期内（%s ~ %s）主动完成服务交付工作", startDate, endDate));
						context.put("startDate", startDate);
						context.put("endDate", endDate);
						infoMaintenanceServiceReport(serviceProject, context);
					} catch (Exception e) {
						System.out.println("###############项目维护服务交付发送发生错误################");
						e.printStackTrace();
					}
				}
				params.remove(serviceType + "Date");
			}
		} catch (Exception e) {
			System.out.println("###############项目维护服务交付发送发生错误################");
			e.printStackTrace();
		} finally {
			projectDao.getSqlMapClientTemplate().delete("deleteTempProjectWarrantyStateTable");
		}
		System.out.println("###############项目维护服务交付发送结束################");
	}

	/**
	 * @param serviceProjectList
	 * @param context
	 */
	public void infoMaintenanceServiceReport(Map<String, Object> serviceProject, Map<String, Object> context) {
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
		UserManageDao userManageDao = applicationContext.getBean("userManageDao", UserManageDao.class);
		BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);

		NotificationTemplate officeInfoTemplate = projectDao
				.queryNotificationTemplate(MAINTENANCE_SERVICE_REPORT_INFO_TEMPLATE);
		String table = officeInfoTemplate.getNotificationContent();
		if (StringUtils.isBlank(table)) {
			return;
		}
		// 主送给销售、项目经理、服务经理
		List<ProjectMember> projectMembers = projectDao
				.queryValidMemberEmailByProjectIdAndRoles((Integer) serviceProject.get("projectId"), "10,20,30");
		HashSet<String> tos = new HashSet<String>();
		HashSet<String> ccs = new HashSet<String>();
		HashSet<String> salesNames = new HashSet<String>();
		for (ProjectMember member : projectMembers) {
			String email = member.getEmail();
			if (StringUtils.isBlank(email) && StringUtils.isNotBlank(member.getMemberCode())) {
				email = projectDao.queryMailByUserNameFromOA(member.getMemberCode());
			}
			if (StringUtils.isNotBlank(email)) {
				if (MessageUtil.MEMBER_SALESMAN.equals(member.getMemberRole())) {
					ccs.add(email);
					if (StringUtils.isNotBlank(member.getMemberName())) {
						salesNames.add(member.getMemberName());
					}
				} else if (MessageUtil.MEMBER_PM.equals(member.getMemberRole())
						|| MessageUtil.MEMBER_SM.equals(member.getMemberRole())) {
					tos.add(email);
				}
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		String officeCode = (String) serviceProject.get("officeCode");
		// 办事处不为空，则抄送办事处主任
		if (StringUtils.isNotBlank(officeCode)) {
			params.put("roleid", String.valueOf(MessageUtil.ROLE_AREA_LEADER));
	//					params.put("dpNo", projectMaintenance.getOfficeCode());
			params.put("areaPower", officeCode);
			// 抄送办事处主任
			List<User> users = userManageDao.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			for (User user : users) {
				ccs.add(user.getEmail());
			}
		}
		// 抄送服务交付验收小组群组邮箱
		String acceptanceMail = basicDataDao.querySysArg("pm.project.maintenance.serviceDelivery.mail.user");
		if (StringUtils.isNotBlank(acceptanceMail)) {
			ccs.add(acceptanceMail);
		} else {
			String cc = StringEscUtil.getText("pm.project.maintenance.serviceDelivery.mail.user");
			cc = projectDao.queryMailByUsername(cc);
			if (StringUtils.isNotBlank(cc)) {
				ccs.add(cc);
			}
		}

//		Map<String, Object> context = new HashMap<String, Object>();
		// context.put("username", salesNames.toString());
		context.put("username", StringUtils.join(salesNames, "、"));
		context.put("tos", StringUtils.join(tos, ";"));
		context.put("ccs", StringUtils.join(ccs, ";"));
//					context.put("attachFileNames", attachFiles.toString());
		context.put("templateCode", "maintenanceServiceReportInfo");
		context.put("projectName", serviceProject.get("projectName"));
//		context.put("deliverName", deliverName);
//		context.put("serviceName", serviceName);
//		context.put("serviceCount", serviceCount);
//		context.put("remainedCount", yearCount - serviceCount);
//		context.put("content", yearCount - serviceCount > 0 ? "请持续跟踪" : "服务周期内已全部交付完成");
		context.put("officeName", serviceProject.get("officeName"));
		NotificationTemplateUtil.keepMail(context);
	}

	public Set<String> filterEmail(Set<String> mails) {
		if (mails == null || mails.isEmpty()) {
			return mails;
		}
		for (Iterator<String> iterator = mails.iterator(); iterator.hasNext();) {
			String mail = (String) iterator.next();
			if (!(StringUtils.isNotBlank(mail) && Pattern.matches(MAIL_PATTERN, mail.toLowerCase()))) {
				iterator.remove();
			}
		}
		return mails;
	}
	
	/**
	 * 根据键值对匹配每个服务对应的交付件、服务名、编码、年服务次数等信息
	 * @param config
	 * @return
	 */
	public static Map<String, Map<String, Object>> matchServiceDeliveryConfigMap(String config, String key) {
		Map<String, Map<String, Object>> serviceConfigMap = new HashMap<String, Map<String,Object>>();
		try {
			if (StringUtils.isNotBlank(config)) {
				// 默认按serviceCode分组
				key = StringUtils.defaultIfEmpty(key, "serviceCode");
				// 查询服务的每年次数、服务类型
				Pattern p = Pattern.compile("([^$;]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)(\\$;?)");
				Matcher m = p.matcher(config);
				String deliverNames = "";
				String serviceName = "";
				Integer yearCount = 4;
				String serviceYear = "";
				String serviceCode = "";
				String deliverIds = "0";
				while (m.find()) {
					deliverNames = m.group(1);
					serviceName = m.group(2);
					serviceYear = m.group(3);
					yearCount = Integer.parseInt(m.group(4));
					serviceCode = m.group(5);
					deliverIds = m.group(6);
					
					String[] pdName = deliverNames.split(",");
					for (int i = 0; i < pdName.length; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("deliverName", pdName[i]);
						map.put("deliverNames", deliverNames);
						map.put("serviceName", serviceName);
						map.put("serviceYear", serviceYear);
						map.put("yearCount", yearCount);
						map.put("serviceCode", serviceCode);
						map.put("deliverIds", deliverIds);
						serviceConfigMap.put(String.valueOf(map.get(key)), map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceConfigMap;
	}

	public static void main(String[] args) throws JobExecutionException {
		new MaintenanceServiceQuarterMailer().execute(null);
//		System.out.println(matchServiceDeliveryConfigMap("策略调优报告$WAF策略调优服务$wafYearCount$4$wafService$82$;高级巡检报告$高级维保服务$wsYearCount$4$warrantyGrade$83$;", ""));
	}
}
