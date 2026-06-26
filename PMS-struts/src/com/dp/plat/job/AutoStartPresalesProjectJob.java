package com.dp.plat.job;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.ServletActionContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.MailContent;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.exception.CustomRuntimeException;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.PresalesService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.SendMailService;
import com.dp.plat.util.AviatorUtils;
import com.dp.plat.util.MailHandleUtil;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.UserUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 自动开始售前测试项目
 * 
 * @author w02611
 */
@SuppressWarnings({ "unused", "unchecked" })
public class AutoStartPresalesProjectJob implements Job {
    private static final JobLogger logger = new JobLogger(AutoStartPresalesProjectJob.class);
    private final static TypeReference<HashMap<String, Object>> TYPE_MAP = new TypeReference<HashMap<String, Object>>() {};
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        logger.logStart();
        try {
            work();
            logger.logComplete();
        } catch (Exception e) {
            logger.logError(e);
        }
    }

    public void work() throws Exception {
        ApplicationContext ctx = SpringContext.getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
        SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        
        // 向上下文注册session，service层需要UserContext，scope=session，
        ((AbstractRefreshableApplicationContext) ctx).getBeanFactory().registerScope("session", new SessionScope());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserContext userContext = UserContext.getUserContext();
        
        PresalesService presalesService = ctx.getBean("presalesService", PresalesService.class);
        BasicDataService basicDataService = ctx.getBean("basicDataService", BasicDataService.class);
        
        Map<String, Object> config = JSON.parseObject(
                StringUtils.defaultIfBlank(basicDataService.querySysArg("pm.presales.auto.start.config"), "{}"),
                TYPE_MAP);
        // 是否启用自动开始售前测试项目
        if (Boolean.FALSE.equals(Boolean.parseBoolean(String.valueOf(config.get("enable"))))) {
            return;
        }
        
        String autoUser = String.valueOf(config.getOrDefault("user", "sys"));
        // 模拟用户登录
        User user = new User();
        user.setUsername(autoUser);
        user.setRoleids(";1;");
        userContext.login(user, "", null, null, null);

        Presales temp = new Presales();
        temp.setProjectStates(MessageUtil.PROJECT_STATE_CREATING);
        DisplayParam displayParam = new DisplayParam();
        displayParam.setExport(true);
//        displayParam.setPagesize(1);
        List<Presales> projects = presalesService.queryPresalesList(temp, displayParam);
//        projects = new ArrayList<Presales>(projects.stream().limit(10).collect(Collectors.toList()));

        Map<String, String> presalesTypeMap = basicDataService.queryBasicDataBeanMap("presalesType");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("typeMap", presalesTypeMap);

        Map<String, List<ProjectMember>> depSmCache = new HashMap<String, List<ProjectMember>>();
        for (Presales project : projects) {
            try {
                String officeCode = project.getOfficeCode();
                List<ProjectMember> serviceManagers = depSmCache.get(officeCode);
                if (serviceManagers == null) {
                    serviceManagers = sqlMap.queryForList("query_project_serviceManager_by_officeCode", officeCode);
                    if (serviceManagers == null || serviceManagers.isEmpty()) {
                        String marketOfficeCode = UserUtil.transferDepNo(project.getOfficeCode(), 1);
                        // 如果转换后的市场办事处编码与当前编码不一致，则重新查询市场部编码的服务经理
                        if (!StringUtils.trimToEmpty(officeCode).equals(marketOfficeCode)) {
                            serviceManagers = sqlMap.queryForList("query_project_serviceManager_by_officeCode", marketOfficeCode);
                            // 把查找到的结果添加到缓存中
                            depSmCache.put(officeCode, serviceManagers);
                            depSmCache.put(marketOfficeCode, serviceManagers);
                            // 如果转换后的编码有服务经理，则更新当前办事处编码为转换后的编码
                            if (serviceManagers != null && !serviceManagers.isEmpty()) {
                                officeCode = marketOfficeCode;
                            }
                        }
                    }
                }
                // 服务经理为空不创建项目
                if (serviceManagers.isEmpty() || StringUtils.isBlank(serviceManagers.get(0).getMemberCode())) {
                    continue;
                }
                ProjectMember serviceManager = serviceManagers.get(0);
                project = presalesService.queryPresalesById(project.getPresalesId());
                project.setOfficeCode(officeCode);

                // 指派服务经理
                project.setServiceManager(serviceManager.getMemberCode());
                project.setServiceManagerName(serviceManager.getMemberName());

                // 售前测试触发脚本
                execScripts(project, params, config, "prepareProjectType");

                PresalesComment param = new PresalesComment();
                // 指派了项目经理则为2，不指派为1
                param.setResult(StringUtils.isNotBlank(project.getProjectManager()) ? 2 : 1);
                param.setMessage("系统自动开始售前测试项目！");
                presalesService.startPresalesFlow(project, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行一些脚本
     * 
     * @param presales
     * @param config
     * @return
     */
    private Object execScripts(Presales presales, Map<String, Object> config, String scriptName) {
        return execScripts(presales, null, config, scriptName);
    }

    /**
     * 执行一些脚本
     * 
     * @param presales
     * @param config
     * @return
     */
    private Object execScripts(
            Presales presales, Map<String, Object> entity, Map<String, Object> config, String scriptName
    ) {
        if (entity == null) {
            entity = new HashMap<String, Object>();
        }
        entity.put("presales", presales);
        return execScripts(entity, config, scriptName);
    }

    /**
     * 执行一些脚本
     * 
     * @param presales
     * @param config
     * @return
     */
    private Object execScripts(Map<String, Object> entity, Map<String, Object> config, String scriptName) {
        // 售前测试触发脚本
        String scripts = String.valueOf(config.getOrDefault("scripts", ""));
        if (StringUtils.isBlank(scripts)) {
            return null;
        }

        Map<String, Object> env = new HashMap<String, Object>();
        env.put("entity", entity);
        env.put("config", config);
        env.put("context", this);

        Map<String, Object> scriptMap = JSON.parseObject(scripts, TYPE_MAP);
        List<Map<String, Object>> scriptList = new ArrayList<Map<String, Object>>(scriptMap.size());
        // 默认获取所有的脚本值
        Collection<Object> values = scriptMap.values();
        // 如果指定了具体的脚本名称，则只运行具体的脚本
        if (StringUtils.isNotBlank(scriptName)) {
            values = Collections.singletonList(scriptMap.get(scriptName));
        }
        // 循环解析脚本放入脚本列表
        for (Object value : values) {
            if (value != null) {
                Map<String, Object> script = null;
                if (value instanceof String) {
                    script = JSON.parseObject((String) value, TYPE_MAP);
                } else if (value instanceof Map) {
                    script = (Map<String, Object>) value;
                }
                scriptList.add(script);
            }
        }
        
        
        // 循环执行脚本
        List<Object> results = new ArrayList<Object>();
        for (Map<String, Object> script : scriptList) {
            try {
                if (ObjectUtils.isNotEmpty(script.get("script"))) {
                    Object result = AviatorUtils.exceute(String.valueOf(script.get("script")), env);
                    results.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 判断是否正确执行并返回，即使没有返回值的脚本，也会返回NULL
        if (results.size() != scriptList.size()) {
            throw new CustomRuntimeException("规则脚本执行发生错误，请检查日志！");
        }

        return results;
    }

    /**
     * @param project
     * @throws Exception
     */
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
    
//    public static String sciprt(Presales presales, Map<String, Object> config) {
//        String projectType = "";
//        if ("专网营销部" == presales.getOfficeName()) {
//            projectType = "专网项目";
//        } else if ("战略合作部" == presales.getOfficeName()) {
//            projectType = "战略合作部项目";
//        } else if (presales.getProjectName().contains("集采")) {
//            projectType = "集采项目";
//        } else if (presales.getProjectName().contains("展会")) {
//            projectType = "展会";
//        } else {
//            projectType = "销售测试";
//        }
//        return projectType;
//    }
//
////    @Test
//    public void testStartScript() {
//        String script = "let projectType = '';\r\n" + 
//                "      let presales = entity.presales;\r\n" + 
//                "      let officeName = presales.officeName;\r\n" + 
//                "      let officeCode = presales.officeCode;\r\n" + 
//                "      let projectName = presales.projectName;\r\n" + 
//                "      p(officeName);p(officeCode);p(projectName);\r\n" + 
//                "      if ('161000' == officeCode || contains(officeName, '专网营销部')) {\r\n" + 
//                "          projectType = '专网项目';\r\n" + 
//                "      } elsif ('161100' == officeCode || contains(officeName, '战略合作部')) {\r\n" + 
//                "          projectType = '战略合作部项目';\r\n" + 
//                "      } elsif (contains(projectName, '集采')) {\r\n" + 
//                "          projectType = '集采项目';\r\n" + 
//                "      } elsif (contains(projectName, '展会')) {\r\n" + 
//                "          projectType = '展会';\r\n" + 
//                "      } else {\r\n" + 
//                "          projectType = '销售测试';\r\n" + 
//                "      }\r\n" + 
//                "      setProjectType(presales, projectType);";
//
//        Map<String, Object> env = new HashMap<String, Object>();
//        Map<String, Object> entity = new HashMap<String, Object>();
//        Presales presales = new Presales();
//        presales.setOfficeName("");
//        presales.setProjectName("");
//        entity.put("presales", presales);
//        env.put("entity", entity);
//        env.put("config", null);
//
//        presales.setOfficeName("战略合作部");
//        AviatorUtils.exceute(script, env);
//        assertEquals(sciprt(presales, env), presales.getProjectType());
//
//        presales.setOfficeName("专网营销部");
//        AviatorUtils.exceute(script, env);
//        assertEquals(sciprt(presales, env), presales.getProjectType());
//
//        presales.setOfficeName("运营商市场部");
//        presales.setProjectName("集采");
//        AviatorUtils.exceute(script, env);
//        assertEquals(sciprt(presales, env), presales.getProjectType());
//
//        presales.setProjectName("展会");
//        AviatorUtils.exceute(script, env);
//        assertEquals(sciprt(presales, env), presales.getProjectType());
//
//        presales.setProjectName("颠三倒四");
//        AviatorUtils.exceute(script, env);
//        assertEquals(sciprt(presales, env), presales.getProjectType());
//    }

    public static void main(String[] args) throws Exception {
//		ColseNotTrackProject.ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        new AutoStartPresalesProjectJob().work();
    }

}
