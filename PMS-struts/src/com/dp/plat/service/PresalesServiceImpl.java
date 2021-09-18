package com.dp.plat.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PmClosedLoopDao;
import com.dp.plat.dao.PresalesDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.PresalesProduct;
import com.dp.plat.data.bean.PresalesTask;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.FileParam;
import com.dp.plat.param.ProjectTypeParam;
import com.dp.plat.util.ActivityMessage;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.Util;

public class PresalesServiceImpl extends BaseServiceImpl implements PresalesService {
    /**
     * 流程管理
     */
    private WorkFlowService workFlowService;
    /**
     * 回访管理
     */
    private PresalesDao presalesDao;
    /**
     * 闭环,借用对回访问卷的操作
     */
    private PmClosedLoopDao pmClosedLoopDao;

    private ProjectDao projectDao;

    private UserManageDao userManageDao;

    private BasicDataService basicDataService;

    public void setWorkFlowService(WorkFlowService workFlowService) {
        this.workFlowService = workFlowService;
    }

    public void setPresalesDao(PresalesDao presalesDao) {
        this.presalesDao = presalesDao;
    }

    public void setPmClosedLoopDao(PmClosedLoopDao pmClosedLoopDao) {
        this.pmClosedLoopDao = pmClosedLoopDao;
    }

    public void setBasicDataService(BasicDataService basicDataService) {
        this.basicDataService = basicDataService;
    }

    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public void setUserManageDao(UserManageDao userManageDao) {
        this.userManageDao = userManageDao;
    }

    @Override
    public Presales queryPresalesById(int presalesId) {
        Presales presales = presalesDao.queryPresalesById(presalesId);
        if (!(UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN)
                || UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PRESALES_STAFF) || getLoginName().equals(presales.getApplyBy()) || getLoginName().equals(presales.getProjectManager())
                || getLoginName().equals(presales.getServiceManager()))) {
            return null;
        }
        List<FileParam> fileParams = new ArrayList<FileParam>();
        // 有从SMS系统同步过来的借货交付件
        if (presales.getLendfiles() != null && presales.getLendfiles().length() > 0) {
            String[] files = presales.getLendfiles().split(",");
            for (String file : files) {
                FileParam param = new FileParam(file.substring(file.lastIndexOf("/") + 1), file, presales.getSalesman(), 1);
                fileParams.add(param);
            }
        }

        // 新交付件
//        List<ProjectDeliver> deliverList = projectDao.queryDeliverDetailByProjectIdAndDeliverType(presalesId, "29");
        List<ProjectDeliver> deliverList = projectDao.queryDeliverDetailByProjectIdAndProjectType(presalesId, MessageUtil.PROJECT_TYPE_PRESALES);
        for (ProjectDeliver projectDeliver : deliverList) {
            FileParam fileParam = new FileParam(projectDeliver.getDeliverableName(), projectDeliver.getDeliverablePath(), projectDeliver.getUploadUser(), 2);
            fileParam.setId(projectDeliver.getId());
            fileParam.setFileType(projectDeliver.getDeliverableType());
            fileParam.setUploadTime(projectDeliver.getUploadTime());
            fileParam.setFlag(projectDeliver.getEventKey()); // 用flag来保存eventKey 表示哪个阶段的附件
            fileParams.add(fileParam);
        }
        /**
         * 有测试现场确认单, 历史交付件
         */
        if (presales.getConfirmFileIds() != null && presales.getConfirmFileIds().length() > 0) {
            List<FileParam> files = basicDataService.queryFileList(presales.getConfirmFileIds());
            fileParams.addAll(files);
        }

        presales.setFileParams(fileParams);
        return presales;
    }

    @Override
    public List<PresalesProduct> queryPresalesProductByPresalesId(int presalesId) {
        return presalesDao.queryPresalesProductByPresalesId(presalesId);
    }

    @Override
    public void startPresalesFlow(Presales presales, PresalesComment param) {
        // 保存售前项目分类
        if (StringUtils.isNotBlank(presales.getProjectType()) || param.getResult() == ActivityMessage.COMMENT_REJECT) {
            if (param.getResult() == ActivityMessage.COMMENT_REJECT) {
                presales.setCloseRemark(param.getMessage());
            }
            presalesDao.updatePresaleHeader(presales);
        }

        // 更新项目编码
        this.updatePresalesProjectCode(presales);
        // 将售前项目主键更新到产品明细表中
        presalesDao.updatePresalesProduct(presales.getPresalesId());
        // 保存或更新业务信息
        this.addProjectMemeber(presales.getPresalesId(), presales.getServiceManager(), MessageUtil.MEMBER_SM);
        // this.addProjectMemeber( presales.getPresalesId() ,presales.getSalesman() ,
        // MessageUtil.MEMBER_SALESMAN);
        this.addProjectMemeber(presales.getPresalesId(), presales.getProjectManager(), MessageUtil.MEMBER_PM);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("presalesId", presales.getPresalesId());
        paramMap.put("projectState", param.getResult() == 2 ? MessageUtil.PROJECT_STATE_32 : MessageUtil.PROJECT_STATE_31);
        presalesDao.updatePresalesState(paramMap);
        // 工程管理部直接指定项目经理，加入工程计划
        if (param.getResult() == 2) {
            // 0.1查询是否有了工程计划
            boolean ishasplan = presalesDao.queryIsHasProjectTask(presales.getPresalesId(), ProjectTypeParam.TYPE_OF_PRESALES);
            // 0.2增加工程计划
            if (!ishasplan) {
                presalesDao.insertPresaleTasks(presales.getPresalesId(), ProjectTypeParam.TYPE_OF_PRESALES, MessageUtil.BASIC_DATA_PROJECT_TYPE);
            }
        }

        // 1.获取流程变量
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("presalesId", presales.getPresalesId());
        vars.put("applyBy", getLoginName());
        // 2.拼接businessKey
        String key = presales.getClass().getSimpleName();

        String businessKey = key + "." + presales.getPresalesId();

        // 3.启动流程
        ProcessInstance process = workFlowService.startProcess(key, businessKey, vars);

        String instId = process.getId();
        // 4.将instId以及其他信息 回写致申请表,并更新申请状态
        workFlowService.updateApplytableInfo("pm_presales_project_header", instId, presales.getPresalesId(), Util.methodToProperty("getPresalesId"));

        // 5.办理流程
        Task task = workFlowService.queryTaskByBussinessKeyUser(businessKey, getLoginName());
        vars.clear();
        int result = param.getResult();
        vars.put("result", param.getResult());// 流程走向
        vars.put("sm", presales.getServiceManager());// 下一个办理人
        vars.put("pm", presales.getProjectManager());
        workFlowService.doSelfTask(task, instId, param.getMessage(), vars);
        // 6.增加自定义的审批意见
        // result = -1 ： -20直接驳回
        workFlowService.addSelfActComment(presales.getPresalesId(), key, task.getTaskDefinitionKey(), task.getId(), instId, result == ActivityMessage.COMMENT_REJECT ? -20 : ActivityMessage.COMMENT_APPLY, param.getMessage());
        // 7.增加通知下一步审批人邮件
        User user = userManageDao.queryUserByUserName(param.getResult() == 2 ? presales.getProjectManager() : presales.getServiceManager());
        if (user != null) {
            presales = presalesDao.queryPresalesById(presales.getPresalesId());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("tos", user.getEmail());
            mailMap.put("templateCode", "27");
            mailMap.put("username", user.getRealName());
            mailMap.put("projectName", presales.getProjectName());
            mailMap.put("taskName", param.getResult() == 2 ? "项目经理跟踪项目" : "服务经理指定项目经理");
            NotificationTemplateUtil.keepMail(mailMap);
        }
        
        // 更新各阶段耗时
        this.updatePresalesDuration(presales.getPresalesId());
    }

    /**
     * 更新售前项目项目编码
     * 
     * @param presales
     */
    private void updatePresalesProjectCode(Presales presales) {
        int num = presalesDao.queryPresalesCodeNum(presales.getPresalesId());
        presalesDao.updatePresalesCode(presales.getPresalesId(), num);
    }

    /**
     * 添加项目成员
     * 
     * @param objId
     * @param serviceManager
     * @param memberSm
     */
    private boolean addProjectMemeber(int objId, String memberCode, String memberRole) {
        if (memberCode != null && !"".equals(memberCode)) {
            // 失效原有成员
            presalesDao.invalidProjectMember(objId, memberRole);
            // 查询memberCode其他信息
            User user = userManageDao.queryUserByUserName(memberCode);

            // 增加新成员
            ProjectMember member = new ProjectMember();
            member.setProjectType(MessageUtil.PROJECT_TYPE_PRESALES);
            member.setMemberCode(memberCode);
            member.setMemberName(user.getUsername() + "-" + user.getRealName());
            member.setMemberRole(memberRole);
            member.setEffectiveFrom(new Date());
            member.setEmail(user.getEmail());
            member.setCreateBy(getLoginName());
            member.setCreateTime(new Date());
            member.setProjectId(objId);
            member.setFromFlag("1");
            projectDao.insertProjectMember(member);
            return true;
        }
        return false;
    }

    @Override
    public List<Presales> queryPresalesList(Presales presales, DisplayParam displayParam) throws UnsupportedEncodingException {
        return presalesDao.queryPresalesList(presales, displayParam);
    }

    @Override
    public List<PresalesComment> queryPresalesCommentList(int presalesId) {
        return presalesDao.queryActComment(presalesId, Presales.class.getSimpleName());
    }

    @Override
    public void submitSmAduit(Presales presales, PresalesComment param) {
        // 保存业务表单信息
        this.addProjectMemeber(presales.getPresalesId(), presales.getProjectManager(), MessageUtil.MEMBER_PM);
        // 增加项目经理跟踪的工程计划
        // 0.1查询是否有了工程计划
        boolean ishasplan = presalesDao.queryIsHasProjectTask(presales.getPresalesId(), ProjectTypeParam.TYPE_OF_PRESALES);
        // 0.2增加工程计划
        if (!ishasplan) {
            presalesDao.insertPresaleTasks(presales.getPresalesId(), ProjectTypeParam.TYPE_OF_PRESALES, MessageUtil.BASIC_DATA_PROJECT_TYPE);
        }
        if (!"usertask2".equals(presales.getTaskDefKey())) {
            Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), presales.getOldProjectManager());
            if (task != null) {
                workFlowService.assigneeTask(task.getId(), presales.getProjectManager(), "pm");
            } else {
                workFlowService.setVariable(param.getInstId(), "pm", presales.getOldProjectManager(), presales.getProjectManager());
            }
            return;
        }

        // 更新项目状态
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("presalesId", presales.getPresalesId());
        if (param.getResult() == ActivityMessage.COMMENT_AGREE) {
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_32);
        } else {
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_30);
        }
        presalesDao.updatePresalesState(paramMap);

        // 1.获取流程变量
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("pm", presales.getProjectManager());
        vars.put("result", param.getResult());
        vars.put("applyBy", "emRole");
//        vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
        vars.put("emRole", MessageUtil.ROLE_PRESALES_STAFF + "");
        // 2.流程走向下一步,因这里涉及到某个角色审批的问题，特殊写好，后续待改进
        Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), getLoginName());

        if (task == null) {
            List<Task> tasks = workFlowService.getTaskByInstId(param.getInstId());
            task = !tasks.isEmpty() ? tasks.get(0) : null;
            workFlowService.assigneeTask(task.getId(), getLoginName(), "sm");
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_32);
            presalesDao.updatePresalesState(paramMap);
            task.setAssignee(getLoginName());
            vars.put("result", 1);
        }
        workFlowService.doSelfTask(task, param.getInstId(), param.getMessage(), vars);
        // 3.增加自定义的审批意见
        workFlowService.addSelfActComment(presales.getPresalesId(), presales.getClass().getSimpleName(), task.getTaskDefinitionKey(), task.getId(), param.getInstId(), param.getResult(), param.getMessage());
        // 4.增加通知下一步审批人邮件
        // User user = userManageDao.queryUserByUserName(presales.getProjectManager());
        User user = null;
        if (param.getResult() == ActivityMessage.COMMENT_AGREE) {
            user = userManageDao.queryUserByUserName(presales.getProjectManager());
        } else {
            user = new User();
            user.setRealName("工程管理部");
            String mails = basicDataService.querySysArg("gongcheng.mail");
            String presalesStaff = projectDao.queryMailByRoleId(MessageUtil.ROLE_PRESALES_STAFF);
            if (StringUtils.isNotBlank(presalesStaff)) {
                mails += ";" + presalesStaff;
            }
            user.setEmail(mails);
        }
        if (user != null) {
            presales = presalesDao.queryPresalesById(presales.getPresalesId());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("tos", user.getEmail());
            mailMap.put("templateCode", "27");
            mailMap.put("username", user.getRealName());
            mailMap.put("projectName", presales.getProjectName());
            mailMap.put("taskName", param.getResult() == ActivityMessage.COMMENT_AGREE ? "项目经理跟踪项目" : "待重新指定服务经理");
            NotificationTemplateUtil.keepMail(mailMap);
        }
        
        // 更新各阶段耗时
        this.updatePresalesDuration(presales.getPresalesId());
    }

    @Override
    public void submitpmAduit(Presales presales, PresalesComment param) {
        // 保存业务表单信息
        // 将上传附件更新
        if (presales.getConfirmFileIds() != null && !"".equals(presales.getConfirmFileIds())) {
            presalesDao.updatePresaleHeader(presales);
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("presalesId", presales.getPresalesId());
        if (param.getResult() == ActivityMessage.COMMENT_AGREE) {
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_33);
        } else {
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_31);
        }
        presalesDao.updatePresalesState(paramMap);

        // 1.获取流程变量
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("em", "emRole");
//        vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
        vars.put("emRole", MessageUtil.ROLE_PRESALES_STAFF + "");
        vars.put("result", param.getResult());
        // 2.流程走向下一步,因这里涉及到某个角色审批的问题，特殊写好，后续待改进
        Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), getLoginName());

        workFlowService.doSelfTask(task, param.getInstId(), param.getMessage(), vars);
        // 3.增加自定义的审批意见
        workFlowService.addSelfActComment(presales.getPresalesId(), presales.getClass().getSimpleName(), task.getTaskDefinitionKey(), task.getId(), param.getInstId(), param.getResult(), param.getMessage());

        // 4.增加通知下一步审批人邮件
        User nextUser = null;
        if (param.getResult() == ActivityMessage.COMMENT_AGREE) {
            nextUser = new User();
            nextUser.setRealName("工程管理部");
            String mails = basicDataService.querySysArg("gongcheng.mail");
            String presalesStaff = projectDao.queryMailByRoleId(MessageUtil.ROLE_PRESALES_STAFF);
            if (StringUtils.isNotBlank(presalesStaff)) {
                mails += ";" + presalesStaff;
            }
            nextUser.setEmail(mails);
        } else {
            nextUser = userManageDao.queryUserByUserName(presales.getServiceManager());
        }
        presales = presalesDao.queryPresalesById(presales.getPresalesId());
        Map<String, Object> mailMap = new HashMap<String, Object>();
        mailMap.put("tos", nextUser.getEmail());
        mailMap.put("templateCode", "27");
        mailMap.put("username", nextUser.getRealName());
        mailMap.put("projectName", presales.getProjectName());
        mailMap.put("taskName", param.getResult() == ActivityMessage.COMMENT_AGREE ? "工程管理部回访销售" : "服务经理指定项目经理");
        NotificationTemplateUtil.keepMail(mailMap);
        
        // 更新各阶段耗时
        this.updatePresalesDuration(presales.getPresalesId());
    }

    @Override
    public void insertPresalesQuesnaire(Presales presales, PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        // 1.插入问卷头
        pmClQuesnaireResultHeader.setEvaluationHeaderId(0);
        int pmClQuesnaireResultHeaderId = pmClosedLoopDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
        // 2.插入问卷结果行信息
        pmClosedLoopDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);

        // 3.将问卷信息保存进回访流程中
        // 3.0查询本次审批的问卷是否已经保存过
        int callbackQuesnaireId = presalesDao.queryCallBackQuesnaireId(presales);

        if (callbackQuesnaireId != 0) {
            // 3.1将新的问卷ID更新到回访问卷表中
            presalesDao.updateCallBackQuesnaire(callbackQuesnaireId, pmClQuesnaireResultHeaderId, pmClQuesnaireResultHeader.getStatus());
        } else {
            // 3.1查询问卷版本号
            int version = presalesDao.queryCallBackQuesnaireVersion(presales.getPresalesId());
            // 3.2保存问卷与回访关联关系表
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("presalesId", presales.getPresalesId());
            paramMap.put("taskId", presales.getTaskId());
            paramMap.put("quesnaireId", pmClQuesnaireResultHeaderId);
            paramMap.put("quesnaireVersion", version);
            paramMap.put("quesnaireState", pmClQuesnaireResultHeader.getStatus());
            paramMap.put("createBy", getLoginName());
            paramMap.put("createTime", new Date());
            presalesDao.insertCallBackQuesnaire(paramMap);
        }
    }

    @Override
    public int queryPresalesQuesnaireId(Presales presales) {
        return presalesDao.queryQuesnaireIdBycallbackId(presales);
    }

    @Override
    public void updateEndingPresalesProject(int presalesId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("presalesId", presalesId);
        map.put("applyState", ActivityMessage.FLOW_PASS);
        map.put("projectState", MessageUtil.PROJECT_STATE_CLOSEDLOOP);
        map.put("endTime", new Date());
        presalesDao.updatePresalesState(map);
    }

    @Override
    public void updateEnding20PresalesProject(int presalesId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("presalesId", presalesId);
        map.put("applyState", ActivityMessage.FLOW_PASS);
        map.put("projectState", MessageUtil.PROJECT_STATE_DENY);
        map.put("endTime", new Date());
        presalesDao.updatePresalesState(map);
    }

    @Override
    public void submitEmAduit(Presales presales, PresalesComment param) {
        // 保存售前项目分类
        if (StringUtils.isNotBlank(presales.getProjectType())) {
            presalesDao.updatePresaleHeader(presales);
        }
        // 更新项目状态
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("presalesId", presales.getPresalesId());
        if (param.getResult() != ActivityMessage.COMMENT_AGREE) {
            paramMap.put("projectState", MessageUtil.PROJECT_STATE_32);
        }
        presalesDao.updatePresalesState(paramMap);

        // 1.获取流程变量
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("em", getLoginName());
        vars.put("emRole", MessageUtil.ROLE_PRESALES_STAFF + "");
        vars.put("result", param.getResult());
        // 2.流程走向下一步,因这里涉及到某个角色审批的问题，特殊写好，后续待改进
        Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), "emRole");

        workFlowService.doSelfTask(task, param.getInstId(), param.getMessage(), vars);
        // 3.增加自定义的审批意见
        workFlowService.addSelfActComment(presales.getPresalesId(), presales.getClass().getSimpleName(), task.getTaskDefinitionKey(), task.getId(), param.getInstId(), param.getResult(), param.getMessage());

        // 4.增加通知下一步审批人邮件
        User user = userManageDao.queryUserByUserName(param.getResult() == -1 ? presales.getProjectManager() : null);
        if (user != null) {
            presales = presalesDao.queryPresalesById(presales.getPresalesId());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("tos", user.getEmail());
            mailMap.put("templateCode", "27");
            mailMap.put("username", user.getRealName());
            mailMap.put("projectName", presales.getProjectName());
            mailMap.put("taskName", param.getResult() == -1 ? "项目经理跟踪项目" : "闭环");
            NotificationTemplateUtil.keepMail(mailMap);
        }
        
        // 更新各阶段耗时
        this.updatePresalesDuration(presales.getPresalesId());
    }

    @Override
    public void submitReApply(Presales presales, PresalesComment param) {
        // 保存售前项目分类
        if (StringUtils.isNotBlank(presales.getProjectType()) || param.getResult() == ActivityMessage.COMMENT_REJECT) {
            if (param.getResult() == ActivityMessage.COMMENT_REJECT) {
                presales.setCloseRemark(param.getMessage());
            }
            presalesDao.updatePresaleHeader(presales);
        }

        // 保存或更新业务信息
        this.addProjectMemeber(presales.getPresalesId(), presales.getServiceManager(), MessageUtil.MEMBER_SM);
        this.addProjectMemeber(presales.getPresalesId(), presales.getProjectManager(), MessageUtil.MEMBER_PM);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("presalesId", presales.getPresalesId());
        int result = param.getResult();
        if (result != ActivityMessage.COMMENT_REJECT) {
            paramMap.put("projectState", result == 2 ? MessageUtil.PROJECT_STATE_32 : MessageUtil.PROJECT_STATE_31);
            presalesDao.updatePresalesState(paramMap);
        }

        // 1.获取流程变量
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("sm", presales.getServiceManager());
        vars.put("result", param.getResult());
        vars.put("pm", presales.getProjectManager());
        vars.put("emRole", MessageUtil.ROLE_PRESALES_STAFF + "");
        // 2.流程走向下一步,因这里涉及到某个角色审批的问题，特殊写好，后续待改进
        Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), "emRole");

        workFlowService.doSelfTask(task, param.getInstId(), param.getMessage(), vars);
        // 3.增加自定义的审批意见
        workFlowService.addSelfActComment(presales.getPresalesId(), presales.getClass().getSimpleName(), task.getTaskDefinitionKey(), task.getId(), param.getInstId(), param.getResult(), param.getMessage());

        // 4.增加通知下一步审批人邮件
        if (result != ActivityMessage.COMMENT_REJECT) {
            User user = userManageDao.queryUserByUserName(result == 2 ? presales.getProjectManager() : presales.getServiceManager());
            if (user != null) {
                presales = presalesDao.queryPresalesById(presales.getPresalesId());
                Map<String, Object> mailMap = new HashMap<String, Object>();
                mailMap.put("tos", user.getEmail());
                mailMap.put("templateCode", "27");
                mailMap.put("username", user.getRealName());
                mailMap.put("projectName", presales.getProjectName());
                mailMap.put("taskName", param.getResult() == 2 ? "项目经理跟踪项目" : "服务经理指定项目经理");
                NotificationTemplateUtil.keepMail(mailMap);
            }
        }
        
        // 更新各阶段耗时
        this.updatePresalesDuration(presales.getPresalesId());
    }

    @Override
    public List<PresalesTask> queryPresalesTaskList(int presalesId, int projectType) {
        // 新的交付件
        Map<String, List<FileParam>> fileMaps = new HashMap<>();
        //List<ProjectDeliver> deliverList = projectDao.queryDeliverDetailByProjectIdAndDeliverType(presalesId, "29");
        List<ProjectDeliver> deliverList = projectDao.queryDeliverDetailByProjectIdAndProjectType(presalesId, MessageUtil.PROJECT_TYPE_PRESALES);
        if (!deliverList.isEmpty()) {
            for (ProjectDeliver pd : deliverList) {
                FileParam fileParam = new FileParam(pd.getDeliverableName(), pd.getDeliverablePath(), pd.getUploadUser(), 2);
                fileParam.setId(pd.getId());

                String eventKey = pd.getEventKey();
                List<FileParam> fileParams = null;
                if (fileMaps.containsKey(eventKey)) {
                    fileParams = fileMaps.get(eventKey);
                }
                if (fileParams == null) {
                    fileParams = new ArrayList<>();
                }
                fileParams.add(fileParam);
                fileMaps.put(eventKey, fileParams);
            }
        }

        // 历史交付件
        List<PresalesTask> taskList = presalesDao.queryPresalesTaskList(presalesId, projectType);
        for (PresalesTask task : taskList) {
            Map<Integer, String> fileMap = null;
            if (task.getDeliverFileIds() != null && !"".equals(task.getDeliverFileIds())) {
                fileMap = basicDataService.queryFileMap(task.getDeliverFileIds());
            }
            task.setFileMap(fileMap);
            task.setFileParams(fileMaps.get(task.getTaskTypeCode() + "-" + task.getTaskTypeId()));
        }
        return taskList;
    }

    @Override
    public void updatePresalesTaskDeliverFiles(int taskId, String fileIds) {
        if (fileIds != null && taskId != 0) {
            presalesDao.updatePresalesTaskDeliverFiles(taskId, fileIds);
        }
    }

    @Override
    public void updatePresalesTask(Date taskFinshedTime, int presalesTaskId) {
        // presalesDao.updatePresalesTask(taskFinshedTime ,presalesTaskId);
        this.updatePresalesTask(taskFinshedTime, null, presalesTaskId);
    }

    @Override
    public void updatePresalesTask(Date taskFinshedTime, String remark, int presalesTaskId) {
        presalesDao.updatePresalesTask(taskFinshedTime, remark, presalesTaskId);
    }

    @Override
    public void updatePresalesConfirmFileIds(int presalesId, String fileIds) {
        if (fileIds != null && presalesId > 0) {
            presalesDao.updatePresalesConfirmFileIds(presalesId, fileIds);
        }
    }

    @Override
    public void updatePrealesFileIds(int presalesId, int taskId, int fileId) {
        if (fileId > 0 && presalesId > 0 && taskId > 0) {
            presalesDao.updatePrealesFileIds(presalesId, taskId, fileId);
        }
    }

    @Override
    public List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode) {
        return presalesDao.queryPresaleShipmentInfo(projectCode);
    }

    @Override
    public List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode, boolean containRma) {
        return presalesDao.queryPresaleShipmentInfo(projectCode, containRma);
    }
    
    @Override
    public List<Map<String, Object>> queryPresaleLend2SaleInfo(String projectCode) {
        return presalesDao.queryPresaleLend2SaleInfo(projectCode);
    }

    @Override
    public List<Map<String, Object>> queryPresaleLend2RmaInfo(String projectCode) {
        return presalesDao.queryPresaleLend2RmaInfo(projectCode);
    }

    @Override
    @Transactional
    public void terminate2Close(String presalesIds, String comment) {
        if (StringUtils.isNotBlank(presalesIds)) {
            String[] presalesIdArr = StringUtils.split(presalesIds, ",");
            comment = StringUtils.isNotBlank(comment) ? comment : "终止流程直接关闭";
            for (String presalesIdStr : presalesIdArr) {
                Integer presalesId = Integer.valueOf(StringUtils.trimToEmpty(presalesIdStr));
                Presales presales = presalesDao.queryPresalesById(presalesId);
                List<Task> taskList = workFlowService.getTaskByInstId(presales.getInstId());
                if (!taskList.isEmpty()) {
                    workFlowService.deleteProcessInstance(presales.getInstId(), comment);
                }
                workFlowService.addSelfActComment(presales.getPresalesId(), presales.getClass().getSimpleName(), presales.getTaskDefKey(), presales.getTaskId(), presales.getInstId(), -1, comment);
                this.updateEnding20PresalesProject(presalesId);
                
                presales.setCloseRemark(comment);
                presalesDao.updatePresaleHeader(presales);
                // 更新各阶段耗时
                this.updatePresalesDuration(presales.getPresalesId());
            }
        }
    }

    @Override
    public List<PresalesExportVO> queryPresalesExportData(Presales presales) {
        return presalesDao.queryPresalesExportData(presales);
    }

    @Override
    public boolean uploadFile(ProjectDeliver pd, String did, File[] ul, String ufname) {
        String username = UserContext.getUserContext().getUsername();
        boolean flag = false;
        StringBuilder attachFiles = new StringBuilder();
        if (ul != null && !ul.equals("")) {
            List<ProjectDeliver> pdlist = new ArrayList<ProjectDeliver>();

            /** 分隔符 **/
            String separator = java.io.File.separator;
            String path = separator + UploadFileUtil.UPLOAD_PATH + separator + "delivery" + separator + new Date().getTime();
            boolean bool = Util.mkdir(path);
            if (bool) {
            	String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
                String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
                String[] uploaddeliveryFileNames = ufname.split(",");

                for (int i = 0; i < uploaddeliveryFileNames.length; i++) {
                    String ufn = uploaddeliveryFileNames[i];// 附件名称
                    String targetFileName = ufn.trim();
                    // 检查文件上传类型
        			if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
        				return false;
        			}
                    String newName = UploadFileUtil.getUploadFileRename(targetFileName);// 对上传附件进行重命名
                    if (newName == null) {
                        newName = targetFileName;
                    }
                    File target = new File(targetDirectory, newName);
                    try {
                        FileUtils.copyFile(ul[i], target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ProjectDeliver pdeliver = new ProjectDeliver();
                    pdeliver.setProjectId(pd.getProjectId());
                    pdeliver.setContractNo(pd.getContractNo());
                    pdeliver.setDeliverId(did);
                    pdeliver.setDeliverableName(targetFileName);
                    pdeliver.setDeliverablePath(path + separator + newName);
                    pdeliver.setDeliverableType(pd.getDeliverableType());
                    pdlist.add(pdeliver);
                    pdeliver = null;

                    attachFiles.append(target.getAbsolutePath()).append(",").append(targetFileName).append("&&");
                }
                flag = this.insertProjectDeliverFiles(pd, pdlist, username);
            }
        }
        return flag;
    }

    @Override
    public List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver) {
        return projectDao.queryProjectDeliverList(projectDeliver);
    }

    @Override
    public int deleteDeliverById(int fileId) {
        projectDao.deleteDeliverById(fileId);// 交付件置为失效
        ProjectDeliver pd = projectDao.queryProjectDeliverById(fileId);
        this.updateEventActualFinishDateByTask(pd);
        return pd.getProjectId();
    }
    
    @Override
    public void updateProjectDeliverById(ProjectDeliver projectDeliver) {
        projectDao.updateProjectDeliverById(projectDeliver);
    }

    private boolean insertProjectDeliverFiles(ProjectDeliver pd, List<ProjectDeliver> pdlist, String username) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (pdlist != null && pdlist.size() > 0) {
            paramMap.put("uploadUser", username);
            paramMap.put("list", handlePDList(pdlist));
            paramMap.put("projectType", MessageUtil.PROJECT_TYPE_PRESALES);
            projectDao.batchInsertDeliverFiles(paramMap);
        }
        return this.updateEventActualFinishDateByTask(pd);
    }

    private Object handlePDList(List<ProjectDeliver> pdlist) {
        Iterator<ProjectDeliver> it = pdlist.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
            }
        }
        return pdlist;
    }

    private boolean updateEventActualFinishDateByTask(ProjectDeliver pd) {
        Integer count = projectDao.queryDeliverDetailCountByProjectDeliver(pd);
        ProjectTask pt = new ProjectTask();
        pt.setProjectId(pd.getProjectId());
        pt.setTaskTypeCode(pd.getDataTypeCode());
        pt.setTaskTypeId(pd.getBasicDataId());
        if (count == 0) {// 如果当前节点下必上传交付件完整，则置当前时间为完成时间
            pt.setEventActualFinishDate(new Date());
        } else {
            pt.setEventActualFinishDate(null);
        }
        projectDao.updateEventActualFinishDateByTask(pt);
        return count == 0;
    }

    /**
     * 更新售前测试项目各个阶段的耗时情况
     * @param presalesId
     */
    private void updatePresalesDuration(final int presalesId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                presalesDao.updatePresalesDuration(presalesId);
            }
        });
        thread.start();
    }
//    private void inserOrUpdatePresalesTimeline(PresalesTimeline timeline) {
//        presalesDao.inserOrUpdatePresalesTimeline(timeline);
//    }
}
