package com.dp.plat.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.data.bean.WeeklyFeedback;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 * 项目周报管理 Action
 * 处理周报的创建、保存、提交、更新等操作
 * 
 * @author PMS Team
 */
public class ProjectWeeklyAction extends ProjectBaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // 周报参数
    private ProjectWeekly projectWeekly;
    private List<ProjectWeekly> weeklyList;
    private List<ProjectTask> projectTaskList;
    private int weeklyId;
    private String feedback;
    private List<WeeklyFeedback> feedbackList;
    
    // 周报内容
    private List<WeeklyContent> workcontentList;
    private List<WeeklyContent> riskcontentList;
    private List<WeeklyContent> helpcontentList;
    private List<WeeklyContent> progresscontentList;
    private List<WeeklyContent> plancontentList;
    private List<WeeklyContent> filecontentList;
    private List<WeeklyContent> mailcontentList;
    
    /**
     * 创建周报
     * @return
     */
    public String createWeekly() {
        try {
            if (projectWeekly == null) {
                projectWeekly = new ProjectWeekly();
            }
            projectWeekly.setWeeklyStartTime(getWeeklyDateTime(new Date()).get(0));
            projectWeekly.setWeeklyEndTime(getWeeklyDateTime(new Date()).get(1));
            projectTaskList = projectService.queryProjectTaskByProjectId(project.getProjectId());
            int lastWeeklyId = projectService.queryLastWeeklyId(project.getProjectId());
            if (lastWeeklyId != 0) {
                ProjectWeekly Weekly = projectService.queryPorjectWeekly(lastWeeklyId);
                projectWeekly.setTaskDeviation(Weekly.getTaskDeviation());
                projectWeekly.setRemark(Weekly.getRemark());
                workcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_WORK);
                riskcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_RISK);
                plancontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_PLAN);
                helpcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_HELP);
                progresscontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_PROPGRESS);
                mailcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_MAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    /**
     * 根据提供日期的及周报时间规则计算开始结束时间
     * @param date
     * @return List<Date> list[0] 为开始时间 list[1]为结束时间
     */
    public static List<Date> getWeeklyDateTime(Date date) {
        List<Date> dates = new ArrayList<Date>();
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int day = ca.get(Calendar.DAY_OF_WEEK);
        ca.add(Calendar.DATE, 2 - day);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        dates.add(ca.getTime());
        ca.add(Calendar.DATE, 6);
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.SECOND, 59);
        dates.add(ca.getTime());
        return dates;
    }
    
    /**
     * 保存周报
     * @return
     */
    public String saveWeekly() {
        try {
            if (projectWeekly.getWeeklyId() == 0) {
                projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_RAFT);
                int weeklyId = projectService.insertPorjectWeekly(projectWeekly, workcontentList, riskcontentList, helpcontentList, progresscontentList, plancontentList, mailcontentList);
                result = weeklyId;
            } else {
                projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_RAFT);
                projectService.updatePorjectWeekly(projectWeekly, workcontentList, riskcontentList, helpcontentList, progresscontentList, plancontentList, mailcontentList);
                result = projectWeekly.getWeeklyId();
            }
            projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return SUCCESS;
    }
    
    /**
     * 提交周报
     * @return
     */
    public String submitWeekly() {
        try {
            if (projectWeekly.getWeeklyId() == 0) {
                projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_SUBMIT);
                int weeklyId = projectService.insertPorjectWeekly(projectWeekly, workcontentList, riskcontentList, helpcontentList, progresscontentList, plancontentList, mailcontentList);
                result = weeklyId;
            } else {
                projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_SUBMIT);
                projectService.updatePorjectWeekly(projectWeekly, workcontentList, riskcontentList, helpcontentList, progresscontentList, plancontentList, mailcontentList);
                result = projectWeekly.getWeeklyId();
            }
            
            // 生成周报附件
            String path = projectService.createProjectWeeklyExecl(projectWeekly, workcontentList, riskcontentList, helpcontentList, progresscontentList, plancontentList);
            
            String ccs = dealWith(mailcontentList);
            ccs += UserContext.getUserContext().getUser().getEmail() + ";";
            String sp_dr = basicDataService.querySysArg("weekly.css.address");
            if (sp_dr != null) {
                ccs += sp_dr;
            }
            String memberAddress = projectService.queryMemberAddress(projectWeekly.getProjectId());
            if (memberAddress != null && memberAddress.length() > 0) {
                ccs += memberAddress;
            }
            
            project = projectService.queryProjectById(projectWeekly.getProjectId());
            // 发送邮件
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("templateCode", MessageUtil.NOTIFICATION_CODE_WEEKLY_SUBMIT);
            context.put("username", UserContext.getUserContext().getUser().getRealName());
            context.put("projectName", project.getProjectName());
            context.put("attachFileNames", path);
            context.put("tos", ccs + basicDataService.querySysArg(MessageUtil.GCGLB) + projectService.getMails(project.getServiceManagerCode()));
            NotificationTemplateUtil.keepMail(context);
            // 系统通知
            projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_118, projectWeekly.getProjectId());
            projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return SUCCESS;
    }
    
    /**
     * 处理提交时填写的需要邮件抄送的邮件地址
     * @param mailcontentList
     * @return
     */
    private String dealWith(List<WeeklyContent> mailcontentList) {
        StringBuilder sb = new StringBuilder();
        if (mailcontentList != null && mailcontentList.size() > 0) {
            for (WeeklyContent content : mailcontentList) {
                if (content.getOptionDesc002() != null && !"".equals(content.getOptionDesc002())) {
                    sb.append(content.getOptionDesc002());
                    sb.append(";");
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 更新周报
     * @return
     */
    public String updateWeekly() {
        projectWeekly = projectService.queryPorjectWeekly(projectWeekly.getWeeklyId());
        
        workcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_WORK);
        riskcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_RISK);
        helpcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_HELP);
        progresscontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_PROPGRESS);
        plancontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_PLAN);
        filecontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_FILE);
        mailcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(), MessageUtil.OPTION_TYPE_MAIL);
        if (projectWeekly.getWeeklyState() == 1) {
            feedbackList = projectService.queryFeedbackList(projectWeekly.getWeeklyId());
        }
        projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
        return SUCCESS;
    }
    
    /**
     * 周报回复
     * @return
     */
    public String feedback() {
        try {
            projectService.saveWeeklyFeedback(weeklyId, feedback, projectId);
            result = 302;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    // Getter/Setter 方法
    public ProjectWeekly getProjectWeekly() {
        return projectWeekly;
    }
    
    public void setProjectWeekly(ProjectWeekly projectWeekly) {
        this.projectWeekly = projectWeekly;
    }
    
    public List<ProjectWeekly> getWeeklyList() {
        return weeklyList;
    }
    
    public List<ProjectTask> getProjectTaskList() {
        return projectTaskList;
    }
    
    public int getWeeklyId() {
        return weeklyId;
    }
    
    public void setWeeklyId(int weeklyId) {
        this.weeklyId = weeklyId;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public List<WeeklyFeedback> getFeedbackList() {
        return feedbackList;
    }
    
    public List<WeeklyContent> getWorkcontentList() {
        return workcontentList;
    }
    
    public List<WeeklyContent> getRiskcontentList() {
        return riskcontentList;
    }
    
    public List<WeeklyContent> getHelpcontentList() {
        return helpcontentList;
    }
    
    public List<WeeklyContent> getProgresscontentList() {
        return progresscontentList;
    }
    
    public List<WeeklyContent> getPlancontentList() {
        return plancontentList;
    }
    
    public List<WeeklyContent> getFilecontentList() {
        return filecontentList;
    }
    
    public List<WeeklyContent> getMailcontentList() {
        return mailcontentList;
    }
}
