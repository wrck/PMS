package com.dp.plat.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 运营商直签项目到货超期项目邮件汇总提醒
 * 
 * @author admin
 *
 */
public class ProjectArrivalDelayMailer implements Job {
	private final static String ARRIVAL_INFO_TABLE_TEMPLATE = "arrivalInspectTable";
    private final static String ARRIVAL_INFO_TEMPLATE = "arrivalInspectInfo";

	private ApplicationContext applicationContext = null;

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if (SpringContext.getApplicationContext() != null) {
			applicationContext = SpringContext.getApplicationContext();
		} else {
			applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ProjectDao projectDao = applicationContext.getBean("projectDao", ProjectDao.class);

		List<Map<String, Object>> projectInspect = projectDao.queryProjectInspection(null);

		List<Map<String, Object>> arrivalDelayProjects = new ArrayList<>();
		for (Map<String, Object> bean : projectInspect) {
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
			String arrival = (String) bean.get("arrival");
			
			// 到货验收超期项目单独汇总一份邮件，筛选出到货验收超期的项目
			if (StringUtils.isNotBlank(arrival) && Integer.signum(arrivalDays) > 0) {
			    String arrivalFinishDate = (String) bean.get("arrivalFinishDate");
			    if (StringUtils.isBlank(arrivalFinishDate)) {
			        arrivalDelayProjects.add(bean);
			    }
			}
		}
		infoArrivalDelay(arrivalDelayProjects);
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

	public static void main(String[] args) throws JobExecutionException {
		new ProjectArrivalDelayMailer().execute(null);
	}
}
