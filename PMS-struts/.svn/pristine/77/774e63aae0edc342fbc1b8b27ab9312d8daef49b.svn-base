package com.dp.plat.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 运营商直签项目未验收项目邮件汇总提醒
 * 
 * @author admin
 *
 */
public class ProjectInspectionMailer implements Job {
	private final static String OFFICE_INFO_TABLE_TEMPLATE = "officeInspectTable";
	private final static String OFFICE_INFO_TEMPLATE = "officeInspectInfo";
	private final static String TOTAL_INFO_TABLE_TEMPLATE = "totalInspectTable";
	private final static String TOTAL_INFO_TEMPLATE = "totalInspectInfo";
	private final static String ARRIVAL_INFO_TABLE_TEMPLATE = "arrivalInspectTable";
    private final static String ARRIVAL_INFO_TEMPLATE = "arrivalInspectInfo";
	private final static String ACCEPT_STATE_KEY = "43";
	// private final static String INSTALLATION_STATE_KEY = "44";
	private final static String FINAL_INSPECT_STATE_KEY = "46";

	private final static Map<String, Integer> pointDelayLimit;
	private Set<String> pointDelayProjectStateSet;
	private Map<String, Map<String, Integer>> pointCounts;
	private Map<String, Map<String, Integer>> delayCounts;
	private ApplicationContext applicationContext = null;

	static {
		pointDelayLimit = new HashMap<>();
		pointDelayLimit.put("43_after", 2);
		pointDelayLimit.put("44", 5);
		pointDelayLimit.put("45", 9);
		pointDelayLimit.put("46", -1);
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);

		List<Map<String, Object>> projectInspect = projectDao.queryProjectInspection(null);
		NotificationTemplate officeInfoTemplate = projectDao.queryNotificationTemplate(OFFICE_INFO_TABLE_TEMPLATE);
		String table = officeInfoTemplate.getNotificationContent();
		if (StringUtils.isBlank(table)) {
			return;
		}
		int tbodyStart = table.indexOf("<tbody>");
		int tbodyEnd = table.indexOf("</tbody>") + "</tbody>".length();
		String tableLine = StringUtils
				.trimToEmpty(table.substring(tbodyStart + "<tbody>".length(), tbodyEnd - "</tbody>".length()));

		Map<String, Map<String, Object>> contexts = new HashMap<>();
		List<Map<String, Object>> arrivalDelayProjects = new ArrayList<>();
		for (Map<String, Object> bean : projectInspect) {
			String officeCode = (String) bean.get("officeCode");
			Map<String, Object> context = null;
			if (contexts.containsKey(officeCode)) {
				context = contexts.get(officeCode);
			} else {
				context = new HashMap<>();
				context.put("officeCode", officeCode);
				context.put("officeName", String.valueOf(bean.get("officeName")));
			}
			countLimitDelays(bean);
			for (Entry<String, Object> entry : bean.entrySet()) {
				// String key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof Date) {
					Date date = (Date) value;
					value = simpleDateFormat.format(date);
					entry.setValue(value);
				}
			}
			Integer arrivalDays = (Integer) bean.get("arrivalDays");
			Integer preInspectDays = (Integer) bean.get("preInspectDays");
			Integer finalInspectDays = (Integer) bean.get("finalInspectDays");
			String arrival = (String) bean.get("arrival");
			String preInspect = (String) bean.get("preInspect");
			String finalInspect = (String) bean.get("finalInspect");

			fillInspectState(arrival, arrivalDays, "arrival", bean);
			fillInspectState(preInspect, preInspectDays, "preInspect", bean);
			fillInspectState(finalInspect, finalInspectDays, "finalInspect", bean);

			StringBuilder tableTrs = new StringBuilder(StringUtils.trimToEmpty((String) context.get("content")));
			String tableTr = NotificationTemplateUtil.replace(tableLine, bean);
			tableTrs.append(tableTr);
			context.put("content", tableTrs.toString());

			StringBuilder projectIds = new StringBuilder(StringUtils.trimToEmpty((String) context.get("projectIds")));
			projectIds.append(bean.get("projectId")).append(",");
			context.put("projectIds", projectIds.toString());
			contexts.put(officeCode, context);
			
			// 到货验收超期项目单独汇总一份邮件，筛选出到货验收超期的项目
			if (StringUtils.isNotBlank(arrival) && Integer.signum(arrivalDays) > 0) {
			    String arrivalFinishDate = (String) bean.get("arrivalFinishDate");
			    if (StringUtils.isBlank(arrivalFinishDate)) {
			        arrivalDelayProjects.add(bean);
			    }
			}
		}
		UserManageDao userManageDao = applicationContext.getBean("userManageDao", UserManageDao.class);
		BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);

		for (Entry<String, Map<String, Object>> entry : contexts.entrySet()) {
			Map<String, Object> context = entry.getValue();
			context.put("templateCode", OFFICE_INFO_TEMPLATE);
			String content = (String) context.get("content");
			content = table.replace(tableLine, content);
			context.put("content", content);

			String officeCode = (String) context.get("officeCode");
			String projectIds = (String) context.get("projectIds");
			List<ProjectMember> members = projectDao.queryValidMemberByProjectIdsAndRoles(projectIds, "10,20,30");

			Map<String, String> params = new HashMap<>();
			params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
			params.put("areaPower", officeCode);
			List<User> userList = userManageDao.queryUserWithRoleIdAndDpNoOrInAreaPower(params);

			StringBuilder tos = new StringBuilder();
			StringBuilder ccs = new StringBuilder();
			// 主送服务经理和项目经理
			for (ProjectMember projectMember : members) {
//				String to = projectMember.getEmail();
//				if (StringUtils.isBlank(to)) {
//					to = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
//				}
//				if (StringUtils.isNotBlank(to)) {
//					tos.append(";").append(to);
//				}
				if (MessageUtil.MEMBER_PM.equals(projectMember.getMemberRole())
						|| MessageUtil.MEMBER_SM.equals(projectMember.getMemberRole())) {
					String to = projectMember.getEmail();
					if (StringUtils.isBlank(to)) {
						to = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(to) && tos.indexOf(to) == -1) {
						tos.append(";").append(to);
					}
				} else if (MessageUtil.MEMBER_SALESMAN.equals(projectMember.getMemberRole())) {
					String cs = projectMember.getEmail();
					if (StringUtils.isBlank(cs)) {
						cs = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
					}
					if (StringUtils.isNotBlank(cs) && ccs.indexOf(cs) == -1) {
						ccs.append(";").append(cs);
					}
				}
			}
			// 抄送主任
			for (User user : userList) {
				String cs = user.getEmail();
				if (StringUtils.isBlank(cs)) {
					cs = projectDao.queryMailByUserNameFromOA(user.getUsername());
				}
				if (StringUtils.isNotBlank(cs) && ccs.indexOf(cs) == -1) {
					ccs.append(";").append(cs);
				}
			}
			// 北京办和运营商抄送李小雅
			if ("162001".equals(officeCode) || "165000".equals(officeCode)) {
				String	cs = projectDao.queryMailByUserNameFromOA("l00673");
				if (StringUtils.isNotBlank(cs) && ccs.indexOf(cs) == -1) {
					ccs.append(";").append(cs);
				}
			}
//			// 去重复邮件地址
//			Set<String> tosSet = new HashSet<>();
//			tosSet.addAll(Arrays.asList(StringUtils.split(tos.toString(), ";")));
//			String toss = StringUtils.join(tosSet, ";");
			context.put("tos", tos);

			// 抄送验收小组群组邮箱
			String acceptanceMail = basicDataDao.querySysArg("acceptance.mail");
			if (StringUtils.isNotBlank(acceptanceMail)) {
				ccs.append(";").append(acceptanceMail);
			}
			context.put("ccs", ccs.toString());
			NotificationTemplateUtil.keepMailByDao(context);
		}
		infoTotal();
		infoArrivalDelay(arrivalDelayProjects);
	}

    @SuppressWarnings("unchecked")
	public void infoTotal() {
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
		BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);
		NotificationTemplate officeInfoTemplate = projectDao.queryNotificationTemplate(TOTAL_INFO_TABLE_TEMPLATE);
		String table = officeInfoTemplate.getNotificationContent();
		if (StringUtils.isBlank(table)) {
			return;
		}
		int tbodyStart = table.indexOf("<tbody>");
		int tbodyEnd = table.indexOf("</tbody>") + "</tbody>".length();
		String tableLine = StringUtils
				.trimToEmpty(table.substring(tbodyStart + "<tbody>".length(), tbodyEnd - "</tbody>".length()));

		Map<String, String> planStateSet = new HashMap<>();
		List<Map<String, Object>> counts = projectDao.queryProjectInspectionCounts();
		LinkedHashMap<String, Map<String, Object>> contexts = new LinkedHashMap<>();

		Map<String, Object> qg = new HashMap<>();
		Long qgAll = 0l;
		qg.put("officeCode", "all");
		qg.put("officeName", "全国");
		qg.put("countAll", qgAll);

		LinkedHashMap<Object, Object> qgPlanState = new LinkedHashMap<>();
		qg.put("planStateMapList", qgPlanState);
		for (Map<String, Object> bean : counts) {
			String officeCode = StringUtils.trimToEmpty((String) bean.get("officeCode"));
			Map<String, Object> context = null;
			if (contexts.containsKey(officeCode)) {
				context = contexts.get(officeCode);
			} else {
				context = new HashMap<>();
				context.put("officeCode", officeCode);
				context.put("officeName", String.valueOf(bean.get("officeName")));
			}

			Long countAll = (Long) context.get("countAll");
			if (countAll == null) {
				countAll = 0l;
			}

			String projectPlanState = (String) bean.get("projectPlanState");
			Long count = (Long) bean.get("count");

			Map<String, Object> planState = null;
			if (context.containsKey("planStateMapList")) {
				planState = (Map<String, Object>) context.get("planStateMapList");
			} else {
				planState = new LinkedHashMap<>();
			}
			projectPlanState = putPlanStateSet(planStateSet, projectPlanState, (String) bean.get("planState"));
			// planStateSet.put(projectPlanState, (String)
			// bean.get("planState"));
			Long prevCount = (Long) planState.get(projectPlanState);
			if (prevCount == null) {
				prevCount = 0l;
			}
			planState.put(projectPlanState, prevCount + count);

			Long qgStateCount = (Long) qgPlanState.get(projectPlanState);
			if (qgStateCount == null) {
				qgStateCount = 0l;
			}
			qgPlanState.put(projectPlanState, qgStateCount + count);
			qgAll += count;

			context.put("countAll", countAll + count);
			context.put("planStateMapList", planState);

			contexts.put(officeCode, context);
		}
		qg.put("countAll", qgAll);
		contexts.put("all", qg);

		// 查找实施状态的有序列表
		List<BasicDataBean> planStateList = basicDataDao.queryBasicDataBeanAll("22");
		List<String> planStateSortList = new ArrayList<>();
		int accepetIndex = 0;
		int accepetAfterIndex = 0;
		for (Iterator<BasicDataBean> iterator = planStateList.iterator(); iterator.hasNext();) {
			BasicDataBean basicDataBean = (BasicDataBean) iterator.next();
			if (basicDataBean.getEffectiveTo() == null) {
				String planState = basicDataBean.getBasicDataId();
				String[] state = transferPlanState(planState, null);
				planState = state[0];
				if (!planStateSortList.contains(planState)) {
					if (planState.startsWith(ACCEPT_STATE_KEY) && planState.endsWith("_after")) {
						planStateSortList.add(Math.min(planStateSortList.size(), accepetIndex + 1), planState);
						accepetAfterIndex = planStateSortList.size() - 1;
					} else {
						if (ACCEPT_STATE_KEY.equalsIgnoreCase(planState)) {
							planStateSortList.add(Math.min(planStateSortList.size(), accepetAfterIndex), planState);
							accepetIndex = planStateSortList.size() - 1;
						} else {
							planStateSortList.add(planState);
						}
					}
				}
			} else {
				iterator.remove();
			}
		}
		List<String> planStateKeyList = new ArrayList<String>(planStateSet.keySet());
		Collections.sort(planStateKeyList);
		planStateSortList.retainAll(planStateKeyList);

		StringBuilder tableTrs = new StringBuilder();
		for (Entry<String, Map<String, Object>> entry : contexts.entrySet()) {
			Map<String, Object> bean = entry.getValue();
			String officeCode = (String) bean.get("officeCode");
			// Long countAll = (Long) bean.get("countAll");
			Map<String, Integer> officeCounts = pointCounts.get(officeCode);
			if (officeCounts == null) {
				officeCounts = new HashMap<>();
			}
			Integer preInspectCount = officeCounts.get("preInspect");
			preInspectCount = preInspectCount != null ? preInspectCount : 0;
			Integer finalInspectCount = officeCounts.get("finalInspect");
			finalInspectCount = finalInspectCount != null ? finalInspectCount : 0;

			Map<String, Integer> officeDelays = delayCounts.get(officeCode);
			if (officeDelays == null) {
				officeDelays = new HashMap<>();
			}
			Integer preInspDelayCount = officeDelays.get("preInspect");
			preInspDelayCount = preInspDelayCount != null ? preInspDelayCount : 0;
			Integer finalInspDelayCount = officeDelays.get("finalInspect");
			finalInspDelayCount = finalInspDelayCount != null ? finalInspDelayCount : 0;

			bean.put("preInspDelayCount", preInspDelayCount);
			bean.put("finalInspDelayCount", finalInspDelayCount);
			bean.put("preInspectCount", preInspectCount);
			bean.put("finalInspectCount", finalInspectCount);
			bean.put("preInspectRatio", String.format("%.2f%%", preInspDelayCount * 1d / preInspectCount * 100));
			bean.put("finalInspectRatio", String.format("%.2f%%", finalInspDelayCount * 1d / finalInspectCount * 100));

			StringBuilder planStateTd = new StringBuilder();
			Map<String, Object> planStateMap = (Map<String, Object>) bean.get("planStateMapList");
			for (String planState : planStateSortList) {
				Object value = planStateMap.get(planState);
				if (value == null) {
					value = 0;
				}
//				planStateTd.append("<td>").append(value);
//				// if (INSTALLATION_STATE_KEY.equals(planState)) {
//				if (pointDelayLimit.containsKey(planState)) {
//					Integer installDelayCount = officeDelays.get(planState);
//					if (installDelayCount == null) {
//						installDelayCount = 0;
//					}
//					planStateTd.append(" <small>(").append(installDelayCount).append(")</small>");
//				}
//				planStateTd.append("</td>");
				
				planStateTd.append("<td>").append(value).append("</td>");
				if (pointDelayLimit.containsKey(planState)) {
					Integer installDelayCount = officeDelays.get(planState);
					if (installDelayCount == null) {
						installDelayCount = 0;
					}
					planStateTd.append("<td>").append(installDelayCount).append("</td>");
				}
			}
			bean.put("planStateTd", planStateTd.toString());

			String tableTr = NotificationTemplateUtil.replace(tableLine, bean);
			if ("all".equals(officeCode)) {
				tableTrs.insert(0, tableTr);
			} else {
				tableTrs.append(tableTr);
			}
		}

		Map<String, Object> context = new HashMap<>();
		context.put("templateCode", TOTAL_INFO_TEMPLATE);
		String content = table.replace(tableLine, tableTrs);

		Map<String, Object> tableBean = new HashMap<>();
		int planStateColspan = planStateSortList.size();

		StringBuilder planStateTh = new StringBuilder();
		StringBuilder planStateCountTh = new StringBuilder();
		for (String planState : planStateSortList) {
			Object value = planStateSet.get(planState);
			if (value == null) {
				value = "";
			}
			planStateTh.append("<th");
			if (pointDelayLimit.containsKey(planState)) {
				// 需要显示超期数的节点，标题的colspan需要额外+1列
				planStateColspan += 1;
				planStateTh.append(" colspan='2'").append(">").append(value);
				planStateCountTh.append("<th>项目总数</th><th>超期数</th>");
			} else {
				planStateTh.append(" rowspan='2'").append(">").append(value);
			}
			planStateTh.append("</th>");
		}
		tableBean.put("planStateColspan", planStateColspan);
		tableBean.put("planStateTh", planStateTh.toString());
		tableBean.put("planStateCountTh", planStateCountTh.toString());
		content = NotificationTemplateUtil.replace(content, tableBean);
		context.put("content", content);

		StringBuilder tos = new StringBuilder();
		// 验收小组群组邮箱
		String acceptanceMail = basicDataDao.querySysArg("acceptance.mail");
		if (StringUtils.isNotBlank(acceptanceMail)) {
			tos.append(";").append(acceptanceMail);
		}
		// 用服领导群组
		String projectInspectTotalExtMail = basicDataDao.querySysArg("projectInspectTotalExt.mail");
		if (StringUtils.isNotBlank(projectInspectTotalExtMail)) {
			tos.append(";").append(projectInspectTotalExtMail);
		}
		context.put("tos", tos);
		Date mailExpectSendTime = DateUtils.addMinutes(new Date(), 30);
		context.put("mailExpectSendTime", mailExpectSendTime);
		NotificationTemplateUtil.keepMailByDao(context);
	}
    
    /**
     * @param arrivalDelayProjects
     */
    public void infoArrivalDelay(List<Map<String, Object>> arrivalDelayProjects) {
        if (SpringContext.getApplicationContext() != null) {
            applicationContext = SpringContext.getApplicationContext();
        } else {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
        BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);
        
        NotificationTemplate officeInfoTemplate = projectDao.queryNotificationTemplate(ARRIVAL_INFO_TABLE_TEMPLATE);
        String table = officeInfoTemplate.getNotificationContent();
        if (StringUtils.isBlank(table)) {
            return;
        }
        int tbodyStart = table.indexOf("<tbody>");
        int tbodyEnd = table.indexOf("</tbody>") + "</tbody>".length();
        String tableLine = StringUtils
                .trimToEmpty(table.substring(tbodyStart + "<tbody>".length(), tbodyEnd - "</tbody>".length()));

        Map<String, Object> context = new HashMap<>();
        StringBuilder tableTrs = new StringBuilder();
        for (Map<String, Object> bean : arrivalDelayProjects) {
            String officeCode = (String) bean.get("officeCode");
            context.put("officeCode", officeCode);
            context.put("officeName", String.valueOf(bean.get("officeName")));
            String tableTr = NotificationTemplateUtil.replace(tableLine, bean);
            tableTrs.append(tableTr);
        }
        
        context.put("templateCode", ARRIVAL_INFO_TEMPLATE);
        String content = tableTrs.toString();
        content = table.replace(tableLine, content);
        context.put("content", content);

        Map<String, String> params = new HashMap<>();
        params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");

        String businessMail = basicDataDao.querySysArg("business.mail");
        // 主送商务
        context.put("tos", businessMail);

        // 抄送验收小组群组邮箱
        String acceptanceMail = basicDataDao.querySysArg("acceptance.mail");
        context.put("ccs", acceptanceMail);
        NotificationTemplateUtil.keepMailByDao(context);
    }

	private void fillInspectState(String inspectDate, Integer days, String key, Map<String, Object> map) {
		if (StringUtils.isNotBlank(inspectDate)) {
			countInspectPoints(key, map);
			String arrivalFinishDate = (String) map.get(key + "FinishDate");
			StringBuilder temp = new StringBuilder();
			if (StringUtils.isNotBlank(arrivalFinishDate)) {
				temp.append("已完成");
				// if (Integer.signum(days) > 0) {
				// temp.append("超期完成<br>(").append(days).append("天)");
				// } else {
				// temp.append("已完成");
				// }
			} else {
				if (Integer.signum(days) > 0) {
					temp.append("<span style='color:red'>已超期<br>(").append(days).append("天)</span>");
					countDelays(key, map);
				} else {
					temp.append("未超期");
				}
			}
			map.put(key + "State", temp.toString());
		} else {
			map.put(key, "-");
			map.put(key + "State", "-");
		}
	}

	/**
	 * 填充实施状态节点
	 * 
	 * @param planStateSet
	 * @param planState
	 * @param stateName
	 * @return transferedPlanState
	 */
	private String putPlanStateSet(Map<String, String> planStateSet, String planState, String stateName) {
		// if (ACCEPT_STATE_KEY.compareToIgnoreCase(planState) > 0) {
		// planState = ACCEPT_STATE_KEY + "_afrer";
		// stateName = "准备实施";
		// }
		// planStateSet.put(planState, stateName);
		String[] state = transferPlanState(planState, stateName);
		planStateSet.put(state[0], state[1]);
		return state[0];
	}

	/**
	 * 填充实施状态节点
	 * 
	 * @param planStateSet
	 * @param planState
	 * @param stateName
	 * @return transferedPlanState
	 */
	private String[] transferPlanState(String planState, String stateName) {
		if (ACCEPT_STATE_KEY.compareToIgnoreCase(planState) > 0) {
			planState = ACCEPT_STATE_KEY + "_after";
			stateName = "准备实施";
		}
		String[] state = new String[] { planState, stateName };
		return state;
	}

	private void countLimitDelays(Map<String, Object> bean) {
		Date packdate = (Date) bean.get("packdate");

		Integer projectId = (Integer) bean.get("projectId");
		String planState = (String) bean.get("planState");
		String[] state = transferPlanState(planState, null);
		planState = state[0];
		if (pointDelayProjectStateSet == null) {
			pointDelayProjectStateSet = new HashSet<>();
		}
		StringBuilder projectState = new StringBuilder();
		projectState.append(projectId).append("_").append(planState);
		if (pointDelayProjectStateSet.contains(projectState.toString())) {
			return;
		}
		if (packdate != null && pointDelayLimit.containsKey(planState)) {
			boolean needCount = false;
			if (!FINAL_INSPECT_STATE_KEY.equalsIgnoreCase(planState)) {
				Integer delayMonth = pointDelayLimit.get(planState);
				Date lastDate = DateUtils.addMonths(packdate, delayMonth);
				if (lastDate.before(new Date())) {
					needCount = true;
					// delayCounts = counts(planState, bean, delayCounts);
				}
			} else {
				Integer preInspectDays = (Integer) bean.get("preInspectDays");
				Integer finalInspectDays = (Integer) bean.get("finalInspectDays");
				if (preInspectDays == null) {
					preInspectDays = 0;
				}
				if (finalInspectDays == null) {
					finalInspectDays = 0;
				}
				Integer finalDelayAfterPre = preInspectDays > 0 ? finalInspectDays - preInspectDays : finalInspectDays;
				// Integer finalDelayAfterPre = (Integer)
				// bean.get("finalDelayAfterPre");
				// if (finalDelayAfterPre == null) {
				// finalDelayAfterPre = 0;
				// }
				if (Integer.signum(finalDelayAfterPre) > 0) {
					needCount = true;
					// delayCounts = counts(planState, bean, delayCounts);
				}
			}
			if (needCount) {
				delayCounts = counts(planState, bean, delayCounts);
				pointDelayProjectStateSet.add(projectState.toString());
			}
		}
	}

	private void countDelays(String key, Map<String, Object> bean) {
		delayCounts = counts(key, bean, delayCounts);
	}

	private void countInspectPoints(String key, Map<String, Object> bean) {
		pointCounts = counts(key, bean, pointCounts);
	}

	private Map<String, Map<String, Integer>> counts(String key, Map<String, Object> bean,
			Map<String, Map<String, Integer>> result) {
		String officeCode = (String) bean.get("officeCode");
		if (result == null) {
			result = new HashMap<>();
		}
		Map<String, Integer> context = null;
		if (result.containsKey(officeCode)) {
			context = result.get(officeCode);
		} else {
			context = new HashMap<>();
		}

		Integer count = context.get(key);
		if (count == null) {
			count = 0;
		}
		context.put(key, count + 1);
		result.put(officeCode, context);

		// 统计全国的和
		if (!"all".equals(officeCode)) {
			HashMap<String, Object> allBean = new HashMap<>();
			allBean.put("officeCode", "all");
			result = counts(key, allBean, result);
		}
		return result;
	}

	public static void main(String[] args) throws JobExecutionException {
		new ProjectInspectionMailer().execute(null);
	}
}
