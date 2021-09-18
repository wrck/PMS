package com.dp.plat.job;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.dp.plat.param.Person;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 项目维护日报邮件发送
 * 
 * @author admin
 */
public class MaintenanceDailyReportMailer implements Job {
    private final static String MAINTENANCE_DAILY_REPORT_TABLE_TEMPLATE = "maintenanceDailyReportTable";
    private final static String MAINTENANCE_DAILY_REPORT_TEMPLATE = "maintenanceDailyReportInfo";
    private final static String MAIL_PATTERN = "^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@(dptech.com|dp.com)?$";

    private ApplicationContext applicationContext = null;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("###############项目维护日报发送开始################");
        if (SpringContext.getApplicationContext() != null) {
            applicationContext = SpringContext.getApplicationContext();
        } else {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        ProjectDaoImpl projectDao = applicationContext.getBean("projectDao", ProjectDaoImpl.class);

        Calendar reportTime = Calendar.getInstance();
        reportTime.add(Calendar.DATE, -1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> params = new HashMap<>();
        String reportDate = simpleDateFormat.format(reportTime.getTime());
        try {
            // 2019-07-03上线，当天不发送
            if (reportTime.before(simpleDateFormat.parse("2019-07-04"))) {
                System.out.println(reportDate + "(2019-07-03上线，当天不发送)");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        params.put("currentDate", reportDate);
        params.put("currentTime", new Date());
        List<String> userList = projectDao.selectDailyMaintenanceUsers(params);
        if (!userList.isEmpty()) {
            projectDao.getSqlMapClientTemplate().insert("createTempMaintenanceContractNoTable");
            projectDao.getSqlMapClientTemplate().insert("createTempWarrantyStateTable");
            params.put("skipDropTempTable", true);
            for (String createBy : userList) {
                try {
                    params.put("createBy", createBy);
                    List<Map<String, Object>> maintenanceList = projectDao.selectDailyMaintenanceMapList(params);

                    Map<String, Object> context = new HashMap<>();
                    context.put("currentDate", reportDate);
                    Person currentUser = projectDao.queryPersonFromOaByCode(StringUtils.substring(createBy, 1));
                    if (currentUser != null) {
                        context.put("currentName", currentUser.getSalesmanName());
                        context.put("mobile", currentUser.getSalesmanTel());
                    }
                    infoMaintenanceDailyReport(maintenanceList, context);
                } catch (Exception e) {
                    System.out.println("###############项目维护日报发送发生错误################");
                    e.printStackTrace();
                }
            }
            projectDao.getSqlMapClientTemplate().delete("deleteTempMaintenanceContractNoTable");
            projectDao.getSqlMapClientTemplate().delete("deleteTempWarrantyStateTable");
        }
        System.out.println("###############项目维护日报发送结束################");
       
        Connection connection = null;
        CallableStatement statement = null;
        try {
        	System.out.println("###############项目维护每日数据固化开始################");
			connection = projectDao.getSqlMapClientTemplate().getDataSource().getConnection();
			statement = connection.prepareCall("Call `queryProjectMaintenanceInfo`(1);");
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("###############项目维护每日数据固化结束################");
		}
    }
    
    public void sendMaintenanceDailyReport(Map<String, Object> params, String sendUser, String reportDate) {
    	if (SpringContext.getApplicationContext() != null) {
            applicationContext = SpringContext.getApplicationContext();
        } else {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        ProjectDaoImpl projectDao = applicationContext.getBean("projectDao", ProjectDaoImpl.class);
    	try {
            params.put("createBy", sendUser);
            List<Map<String, Object>> maintenanceList = projectDao.selectDailyMaintenanceMapList(params);

            Map<String, Object> context = new HashMap<>();
            context.put("currentDate", reportDate);
            Person currentUser = projectDao.queryPersonFromOaByCode(StringUtils.substring(sendUser, 1));
            if (currentUser != null) {
                context.put("currentName", currentUser.getSalesmanName());
                context.put("mobile", currentUser.getSalesmanTel());
            }
            infoMaintenanceDailyReport(maintenanceList, context);
        } catch (Exception e) {
            System.out.println("###############项目维护日报发送发生错误################");
            e.printStackTrace();
        }
    }

    /**
     * @param maintenanceList
     * @param context
     */
    public void infoMaintenanceDailyReport(List<Map<String, Object>> maintenanceList, Map<String, Object> context) {
        if (SpringContext.getApplicationContext() != null) {
            applicationContext = SpringContext.getApplicationContext();
        } else {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);
        UserManageDao userManageDao = applicationContext.getBean("userManageDao", UserManageDao.class);
        BasicDataDao basicDataDao = applicationContext.getBean("basicDataDao", BasicDataDao.class);

        NotificationTemplate officeInfoTemplate = projectDao.queryNotificationTemplate(MAINTENANCE_DAILY_REPORT_TABLE_TEMPLATE);
        String table = officeInfoTemplate.getNotificationContent();
        if (StringUtils.isBlank(table)) {
            return;
        }
        int tbodyStart = table.indexOf("<tbody>");
        int tbodyEnd = table.indexOf("</tbody>") + "</tbody>".length();
        String tableLine = StringUtils.trimToEmpty(table.substring(tbodyStart + "<tbody>".length(), tbodyEnd - "</tbody>".length()));

        // Map<String, Object> context = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder tableTrs = new StringBuilder();
        Set<String> customTosSet = new HashSet<>();
        Set<String> customCcsSet = new HashSet<>();
        Set<String> officeSet = new HashSet<>();
        Set<Integer> projectIdSet = new HashSet<>();
        for (Map<String, Object> bean : maintenanceList) {
            // 为上下文添加当前日报用户
            if (!context.containsKey("currentName")) {
                String createBy = (String) bean.get("createBy");
                if (StringUtils.isNotBlank(createBy)) {
                    User user = userManageDao.queryUserByUserName((String) bean.get("createBy"));
                    context.put("currentName", user.getRealName());
                }
            }
            // 格式化创建时间，并为日报上下文添加当前日期
            if (!context.containsKey("currentDate")) {
                Date createTime = (Date) bean.get("createTime");
                String createDate = simpleDateFormat.format(createTime);
//                Date currentDate = (Date) bean.get("updateTime");
//                if (currentDate == null) {
//                    currentDate = (Date) bean.get("createTime");
//                }
//                String createDate = simpleDateFormat.format(currentDate);
                bean.put("createDate", createDate);
                context.put("currentDate", createDate);
            } else {
                bean.put("createDate", context.get("currentDate"));
            }
            Date processTime = (Date) bean.get("processTime");
            String processDate = simpleDateFormat.format(processTime);
            bean.put("processDate", processDate);
            // 转换维保状态，-1：保内，0：部分保外，1：保外
            // Object diff = bean.get("diff");
            // if (diff != null) {
            // if (Integer.valueOf(diff.toString()).equals(1)) {
            // bean.put("diffName", "维保外");
            // } else if (Integer.valueOf(diff.toString()).equals(0)) {
            // bean.put("diffName", "部分保外");
            // } else if (Integer.valueOf(diff.toString()).equals(-1)) {
            // bean.put("diffName", "维保内");
            // }
            // }
            // 获取当前日报涉及的办事处
            String officeCode = (String) bean.get("officeCode");
            if (StringUtils.isNotBlank(officeCode) && !officeSet.contains(officeCode)) {
                officeSet.add(officeCode);
            }
            // 获取当前日报涉及的项目
            Integer projectId = (Integer) bean.get("projectId");
            if (projectId != null & projectId > 0 && !projectIdSet.contains(projectId)) {
                projectIdSet.add(projectId);
            }
            // 获取当前所有自定义主送人员地址
            String customTos = (String) bean.get("customTos");
            if (StringUtils.isNotBlank(customTos)) {
                List<String> tos = Arrays.asList(StringUtils.split(customTos, ";"));
                customTosSet.addAll(tos);
            }
            // 获取当前所有自定义抄送人员地址
            String customCcs = (String) bean.get("customCcs");
            if (StringUtils.isNotBlank(customCcs)) {
                List<String> ccs = Arrays.asList(StringUtils.split(customCcs, ";"));
                customCcsSet.addAll(ccs);
            }
            String tableTr = NotificationTemplateUtil.replace(tableLine, bean);
            tableTrs.append(tableTr);
        }

        context.put("templateCode", MAINTENANCE_DAILY_REPORT_TEMPLATE);
        String content = tableTrs.toString();
        content = table.replace(tableLine, content);
        content = NotificationTemplateUtil.replace(content, context);
        context.put("content", content);

        // Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Set<String> tos = new HashSet<>();
        Set<String> ccs = new HashSet<>();
        // 主送服务经理和项目经理,销售
        String projectIds = StringUtils.join(projectIdSet, ",");
        if (!projectIdSet.isEmpty()) {
            List<ProjectMember> members = projectDao.queryValidMemberByProjectIdsAndRoles(projectIds, "10,20,30");
            for (ProjectMember projectMember : members) {
                String to = projectMember.getEmail();
                if (StringUtils.isBlank(to)) {
                    to = projectDao.queryMailByUserNameFromOA(projectMember.getMemberCode());
                }
                if (StringUtils.isNotBlank(to)) {
                    tos.add(to);
                }
            }
        }
        // 将项目自定义主送人员加入邮件抄送数组
        tos.addAll(customTosSet);

        // 抄送主任
        Map<String, String> params = new HashMap<>();
        params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
        for (String officeCode : officeSet) {
            params.put("areaPower", officeCode);
            List<User> userList = userManageDao.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
            for (User user : userList) {
                String cs = user.getEmail();
                if (StringUtils.isBlank(cs)) {
                    cs = projectDao.queryMailByUserNameFromOA(user.getUsername());
                }
                if (StringUtils.isNotBlank(cs)) {
                    ccs.add(cs);
                }
            }
        }

        // 将项目自定义抄送人员加入邮件抄送数组
        ccs.addAll(customCcsSet);
        // 抄送项目维护的sp_core、tsc群组
        String maintenanceMail = basicDataDao.querySysArg("maintenance.mail");
        if (StringUtils.isNotBlank(maintenanceMail)) {
            List<String> mails = Arrays.asList(StringUtils.split(maintenanceMail, ";"));
            ccs.addAll(mails);
        }
        context.put("tos", StringUtils.join(filterEmail(tos), ";"));
        context.put("ccs", StringUtils.join(filterEmail(ccs), ";"));
        try {
            NotificationTemplateUtil.keepMailByDao(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) throws JobExecutionException {
        new MaintenanceDailyReportMailer().execute(null);
    }
}
