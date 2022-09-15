package com.dp.plat.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.dao.SendMailDao;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.User;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.SendMailService;

/**
 * 持久化邮件或通知数据到数据库的公共类
 * 
 * @author admin
 */
public class NotificationTemplateUtil {

    private static final String DEFAULT_BEFORE_SPLIT = "$";
    private static final String DEFAULT_AFTER_SPLIT = "$";

    /**
     * 保存邮件有邮件模板
     * 
     * @param context
     */
    public static void keepMail(Map<String, Object> context) {
        String[] template = null;
        template = findTemplate(context);
        if (template == null) {
            return;
        }
        SendMailService sendMailService = (SendMailService) SpringContext.getBean("sendMailService");
        MailSenderInfo info = new MailSenderInfo();
        info.setSubject(template[1]);
        info.setContent(template[0]);
        BasicDataService basicDataService = (BasicDataService) SpringContext.getBean("basicDataService");
        String arg = basicDataService.querySysArg("sys.envirment.argu");
        if (arg.equals("0")) {
            info.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
            info.setCcs(StringEscUtil.getText("plat.develop.mail.tos"));
        } else {
            if (context.get("tos") != null) {
                String tos = context.get("tos").toString();
                info.setTos(tos);
            }
            if (context.get("ccs") != null) {
                info.setCcs(context.get("ccs").toString());
            }
        }
        if (context.get("attachFileNames") != null) {
            info.setAttachFileNames(context.get("attachFileNames").toString());
        }
        if (context.get("mailExpectSendTime") != null) {
            info.setMailExpectSendTime((Date) context.get("mailExpectSendTime"));
        }
        sendMailService.keepMailInfo(info);
    }

    /**
     * 保存邮件信息无邮件模板
     */
    public static void keepMailNoTemplate(Map<String, Object> context) {
        SendMailService sendMailService = (SendMailService) SpringContext.getBean("sendMailService");
        BasicDataService basicDataService = (BasicDataService) SpringContext.getBean("basicDataService");
        String arg = basicDataService.querySysArg("sys.envirment.argu");
        MailSenderInfo info = new MailSenderInfo();
        if (arg.equals("0")) {// 非正式环境
            info.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
            info.setCcs(StringEscUtil.getText("plat.develop.mail.tos"));
            info.setBcc(StringEscUtil.getText("plat.develop.mail.tos"));
        } else {// 正式环境
            if (context.get("tos") != null) {
                String tos = context.get("tos").toString();
                info.setTos(tos);
            }
            if (context.get("ccs") != null) {
                info.setCcs(context.get("ccs").toString());
            }
            if (context.get("bcc") != null) {
                info.setBcc(context.get("bcc").toString());
            }
        }
        info.setSubject(context.get("subject").toString());
        info.setContent(context.get("content").toString());
        if (context.get("attachFileNames") != null) {
            info.setAttachFileNames(context.get("attachFileNames").toString());
        }
        if (context.get("mailExpectSendTime") != null) {
            info.setMailExpectSendTime((Date) context.get("mailExpectSendTime"));
        }
        sendMailService.keepMailInfo(info);
    }

    /**
     * 保存系统通知信息
     * 
     * @param context
     */
    @SuppressWarnings("unchecked")
    public static void KeepNotification(Map<String, Object> context) {
        String[] template = null;
        template = findTemplate(context);
        if (template == null) {
            return;
        }
        Notification notice = new Notification();
        String userName = "";
        try {
            User user = UserContext.getUserContext().getUser();
            userName = user.getUsername() + "-" + user.getRealName();
            notice.setCreateBy(user.getUsername());
        } catch (Exception e) {
        }
        context.put("username", userName);

        notice.setProjectId((Integer) context.get("projectId"));
        notice.setNotifySubject(template[1]);
        notice.setNotifyContent(template[0]);
        ProjectDao projectDao = (ProjectDao) SpringContext.getBean("projectDao");
        int notifyId = projectDao.insertNotification(notice);
        if ((List<String>) context.get("objs") != null) {
            projectDao.notificationSetObjectList(notifyId, (List<String>) context.get("objs"));
        }

    }

    /**
     * 保存邮件有邮件模板,通过获取dao层实现，解耦UserContext
     * 
     * @param context
     */
    public static void keepMailByDao(Map<String, Object> context) {
        String[] template = null;
        template = findTemplateByDao(context);

        MailSenderInfo info = new MailSenderInfo();
        info.setSubject(template[1]);
        info.setContent(template[0]);
        BasicDataDao basicDataDao = (BasicDataDao) SpringContext.getBean("basicDataDao");
        String arg = basicDataDao.querySysArg("sys.envirment.argu");
        if (arg.equals("0")) {
            info.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
            info.setCcs(StringEscUtil.getText("plat.develop.mail.tos"));
        } else {
            if (context.get("tos") != null) {
                String tos = context.get("tos").toString();
                info.setTos(tos);
            }
            if (context.get("ccs") != null) {
                info.setCcs(context.get("ccs").toString());
            }
            if (context.get("bcc") != null) {
                String bccs = context.get("bcc").toString();
                info.setBcc(bccs);
            }
        }
        if (context.get("attachFileNames") != null) {
            info.setAttachFileNames(context.get("attachFileNames").toString());
        }
        if (context.get("mailExpectSendTime") != null) {
            info.setMailExpectSendTime((Date) context.get("mailExpectSendTime"));
        }
        keepMailInfo(info);
    }

    /**
     * 调用接口，将邮件内容写入数据表
     */
    public static void keepMailInfo(MailSenderInfo info) {
        SendMailDao sendMailDao = (SendMailDao) SpringContext.getBean("sendMailDao");
        if (info.getMailExpectSendTime() == null) {
            info.setMailExpectSendTime(new Date());
        }
        if (info.getMailServerPort() == null) {
            info.setMailServerPort("25");
        }
        if (info.getMailServerHost() == null) {
            info.setMailServerHost("172.153.254.12");
        }
        if (info.getUserName() == null) {
            info.setUserName("pms@dptech.com");
        }
        if (info.getPassword() == null) {
            info.setPassword("2Bk29UamZr");
        }
        if (info.getFromAddress() == null) {
            info.setFromAddress("pms@dptech.com");
        }
        sendMailDao.keepMailInfo(info);
    }

    /**
     * @param context
     * @return [0] 正文 [1] 标题
     */
    private static String[] findTemplate(Map<String, Object> context) {
        NotificationTemplate template = getTemplate(String.valueOf(context.get("templateCode")));
        if (template == null) {
            return null;
        }
        return new String[] { replace(template.getNotificationContent(), context), replace(template.getNotificationSubject(), context) };
    }

    /**
     * @param context
     * @return [0] 正文 [1] 标题
     */
    private static String[] findTemplateByDao(Map<String, Object> context) {
        NotificationTemplate template = getTemplateByDao(String.valueOf(context.get("templateCode")));
        if (template == null) {
            return null;
        }
        return new String[] { replace(template.getNotificationContent(), context), replace(template.getNotificationSubject(), context) };
    }

    /**
     * 从数据库加载邮件模板
     * 
     * @param templateCode
     * @return
     */
    public static NotificationTemplate getTemplate(String templateCode) {
        ProjectService projectService = (ProjectService) SpringContext.getBean("projectService");
        return projectService.queryNotificationTemplate(templateCode);
    }

    /**
     * 从数据库加载邮件模板，使用Dao，解耦userContext
     * 
     * @param templateCode
     * @return
     */
    public static NotificationTemplate getTemplateByDao(String templateCode) {
        ProjectDao projectDao = (ProjectDao) SpringContext.getBean("projectDao");
        return projectDao.queryNotificationTemplate(templateCode);
    }

    public static String replace(String templet, Map<String, Object> context) {

        Set<String> paramNames = null;
        if (context.get("beforeSplit") != null && context.get("afterSplit") != null) {
            paramNames = getNotificationParams(context.get("beforeSplit").toString(), context.get("afterSplit").toString());
        } else {
            paramNames = getNotificationParams();
        }
        for (String name : paramNames) {
            try {
                Object value = context.get(name.substring(1, name.length() - 1));
                value = value == null ? "" : value;
                String regex = "\\Q" + name + "\\E";
                templet = templet.replaceAll(regex, value.toString());
            } catch (Exception e) {
            }
        }
        // 处理实体数据源以外的其他模板值
        String beforeSplit = DEFAULT_BEFORE_SPLIT;
        String afterSplit = DEFAULT_AFTER_SPLIT;
        if (context.get("beforeSplit") != null) {
            beforeSplit = (String) context.get("beforeSplit");
        }
        if (context.get("afterSplit") != null) {
            afterSplit = (String) context.get("afterSplit");
        }
        for (Entry<String, Object> entry : context.entrySet()) {
            try {
                String key = entry.getKey();
                Object value = entry.getValue();
                value = (value == null) ? "" : value;
                String regex = "\\Q" + key + "\\E";
                if (key.contains(beforeSplit) && key.contains(afterSplit)) {
                    regex = "\\Q" + key + "\\E";
                } else if (key.contains(beforeSplit) && !key.contains(afterSplit)) {
                    regex = "\\Q" + key + afterSplit + "\\E";
                } else if (!key.contains(beforeSplit) && key.contains(afterSplit)) {
                    regex = "\\Q" + beforeSplit + key + "\\E";
                } else {
                    regex = "\\Q" + beforeSplit + key + afterSplit + "\\E";
                }
                templet = templet.replaceAll(regex, value.toString());
            } catch (Exception e) {
            }
        }
        try {
            templet = templet.replaceAll("\\Q" + beforeSplit + "\\E" + "\\w+" + "\\Q" + afterSplit + "\\E", "");
        } catch (Exception e) {
        }
        return templet;
    }

    /**
     * 解析可能需要的替换参数
     * 
     * @param beforeSplit
     * @param afterSplit
     * @return
     */
    private static Set<String> getNotificationParams(String beforeSplit, String afterSplit) {
        Set<String> paramNames = new HashSet<String>();
        Class<NotificationParam> c = NotificationParam.class;
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            paramNames.add(beforeSplit + field.getName() + afterSplit);
        }
        return paramNames;
    }

    /**
     * 无参数传入，默认解析参数
     * 
     * @return
     */
    private static Set<String> getNotificationParams() {
        return getNotificationParams(DEFAULT_BEFORE_SPLIT, DEFAULT_AFTER_SPLIT);
    }

}