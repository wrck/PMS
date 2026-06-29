package com.dp.plat.supervision.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.action.BaseAction;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.supervision.vo.ProjectSupervisionVO;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.QuestionnarieUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Preparable;

/**
 * 项目督查记录
 * 
 * @author w02611
 */
public class SupervisionAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 1L;

    // 全局变量
    private User user;

    private ProjectService projectService;
    private UserManageService userManageService;
    private DepartmentManageService departmentManageService;
    private BasicDataService basicDataService;

    private List<Department> departmentList;
    private DisplayParam displayParam;
    private Project project;
    private String redirect;
    private String result;
    private String message;

    // 附件上传
    private ProjectDeliver projectDeliver;
    private List<ProjectDeliver> projectDeliverList;

    // 项目维护记录
    private ProjectSupervisionVO projectSupervision;
    private List<ProjectSupervisionVO> supervisionList;
    private List<Map<String, Object>> supervisionMapList;

    private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;

    private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;

    private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;

    private Map<String, Object> cbForm;

    private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;

    private List<BasicDataBean> supervisionTypeList;
    private List<User> powerUserList;
    
    public void prepareExecute() {
        // 办事处集合
        departmentList = departmentManageService.queryDepartments();

        // 项目分类
//        supervisionTypeList = basicDataService.queryBasicDataBeans("supervisionType");
    }

    /**
     * 项目列表页面
     */
    public String execute() throws Exception {
        if (projectSupervision == null) {
            projectSupervision = new ProjectSupervisionVO();
        }
        if (displayParam == null) {
            displayParam = new DisplayParam();
        }
        displayParam.getParam();
        projectSupervision();
        return SUCCESS;
    }

    /**
     * 获取项目维护记录
     * 
     * @return
     */
    public String projectSupervision() {
        user = UserContext.getUserContext().getUser();
        if (!(user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER) || user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
                || user.isHasRole(MessageUtil.ROLE_AREA_LEADER) || user.isHasRole(MessageUtil.ROLE_PROJECT_ADMIN) || user.isHasRole(MessageUtil.ROLE_PROJECT_VIEWER))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
        if (displayParam == null) {
            try {
                displayParam = new DisplayParam();
                displayParam.setPagesize(-1);
                displayParam.getParam();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        
        if (projectSupervision != null) {
            String officeCode = StringUtils.trimToEmpty(projectSupervision.getOfficeCode());
            if (projectSupervision.getProjectId() != null) {
                project = projectService.queryProjectSimplifyByProjectId(projectSupervision.getProjectId());
            }
            if (project != null) {
                officeCode = StringUtils.trimToEmpty(project.getColumn001());
                projectSupervision.setOfficeCode(officeCode);
                projectSupervision.setProjectName(project.getProjectName());
            }
            if (user.getAreapower().contains(officeCode) && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))
                    || (project != null && (user.getUsername().equals(project.getServiceManagerCode()) || user.getUsername().equals(project.getProgramManagerCode())
                            || user.getUsername().equals(project.getProgramManagerCodeB())))) {
                projectSupervision.setHasPower(true);
            }
            // supervisionList =
            // projectService.selectProjectSupervisionVOList(projectSupervision);
            if (!(user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
                    || user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_PROJECT_ADMIN))) {
                projectSupervision.setAreaPower(user.getAreapower());
                projectSupervision.setUserPower(user.getUsername());
            }
            projectSupervision.setIsDelete(false);
            supervisionMapList = projectService.selectProjectSupervisionMapList(projectSupervision, displayParam);
        } else {
            supervisionList = new ArrayList<>();
            supervisionMapList = new ArrayList<>();
        }
        return SUCCESS;
    }

    public String createProjectSupervision() {
        if (project == null || project.getProjectId() == 0) {
            setErrmsg("非法操作！");
            return ERROR;
        }
        project = projectService.queryProjectSimplifyByProjectId(project.getProjectId());
        user = UserContext.getUserContext().getUser();
        if (project == null || !((user.isHasRole(MessageUtil.ROLE_ADMIN)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
                || user.isHasRole(MessageUtil.ROLE_PROJECT_ADMIN))
                || (user.getAreapower().contains(StringUtils.trimToEmpty(project.getColumn001()))
                        && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)
                                || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)
                                || user.isHasRole(MessageUtil.ROLE_PROJECT_VIEWER)))
                || (project != null && (user.getUsername().equals(project.getServiceManagerCode())
                        || user.getUsername().equals(project.getProgramManagerCode())
                        || user.getUsername().equals(project.getProgramManagerCodeB()) || StringUtils.contains(
                                project.getTeamMemberCodes(), user.getUsername().replaceFirst("\\w", "")))))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }

        if (projectSupervision == null || projectSupervision.getProjectId() == null) {
            Integer quesnaireId = null;
            if (projectSupervision != null && projectSupervision.getId() != null) {
                projectSupervision = projectService.selectProjectSupervisionById(projectSupervision.getId());
                quesnaireId = projectSupervision.getQuesnaireId();
            }
            supervisionTypeList = basicDataService.queryBasicDataBeans("supervisionType");
            PmClosedLoopQuesnaire quesObj = new PmClosedLoopQuesnaire();
            quesObj.setQuesType("projectSupervision");
            // 获取生效的问卷分类
            pmClosedLoopQuesnaireList = QuestionnarieUtil.findPmClosedLoopQuesnaireList(quesObj);
            if (pmClosedLoopQuesnaireList != null && !pmClosedLoopQuesnaireList.isEmpty() && pmClosedLoopQuesnaire == null) {
                pmClosedLoopQuesnaire = pmClosedLoopQuesnaireList.get(0);
            }
            // 获取问卷模板的内容或者已填写的问卷内容
            if ((pmClosedLoopQuesnaire != null && pmClosedLoopQuesnaire.getId() != 0) || (projectSupervision != null && quesnaireId != null && !Integer.valueOf(0).equals(quesnaireId))) {
                int quesnaireState = 0;
                cbForm = QuestionnarieUtil.getCbForm(quesnaireId, pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, quesnaireState);
            }
        } else {
            // 问卷提交
            if (pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0) {
                if (pmClQuesnaireResultHeader.getStatus() == 1) {// 已提交，计算分数
                    QuestionnarieUtil.queryQuesnaireScore(pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                }
                // 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
                pmClQuesnaireResultHeader.setStatus(1);
                int quesnaireId = QuestionnarieUtil.addQuestionnaireResult(pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                projectSupervision.setQuesnaireId(quesnaireId);
                projectSupervision.setState(true);
            }
            projectSupervision.setProjectId(project.getProjectId());
            projectSupervision.setProjectCode(project.getProjectCode());
            projectSupervision.setProjectName(project.getProjectName());
            projectSupervision.setOfficeCode(project.getColumn001());

            projectService.insertOrUpdateProjectSupervision(projectSupervision);

            // if(projectDeliverList != null && projectDeliverList.size() > 0){
            // String[] deliverIds = projectDeliver.getDeliverId().split(",");
            // for(int i = 0;i < projectDeliverList.size();i++){
            // ProjectDeliver deliverFile = projectDeliverList.get(i);
            // if(deliverFile == null){
            // continue;
            // }
            // ProjectDeliver deliver = new ProjectDeliver();
            // BeanUtils.copyProperties(projectDeliver, deliver);
            // deliver.setDeliverableType(projectDeliverList.get(i).getDeliverableType());
            // projectService.uploadFile(deliver, deliverIds[i], deliverFile);
            // }
            // }
            return "redirect";
        }
        return SUCCESS;
    }

    public String deleteProjectSupervision() {
        result = "error";
        if (projectSupervision != null && projectSupervision.getId() != null) {
            user = UserContext.getUserContext().getUser();
            projectSupervision = projectService.selectProjectSupervisionById(projectSupervision.getId());
            if (projectSupervision == null || !(Boolean.FALSE.equals(projectSupervision.getState()) && (user.getUsername().equals(projectSupervision.getCreateBy())
                    || (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER))))) {
                message = "没有删除权限！";
            } else {
                projectSupervision.setIsDelete(true);
                projectService.insertOrUpdateProjectSupervision(projectSupervision);
                result = "success";
            }
        }
        return SUCCESS;
    }

    public String queryPowerUser() {
        List<User> smList = userManageService.queryUserWithRoleId(MessageUtil.ROLE_SERVICEMANAGER);
        List<User> pmList = userManageService.queryUserWithRoleId(MessageUtil.ROLE_PROGRAMMANAGER);
        List<User> powerUsers = new ArrayList<>();
        Set<String> uniqueUser = new HashSet<String>();
        for (User user : smList) {
            if (!uniqueUser.contains(user.getUsername())) {
                uniqueUser.add(user.getUsername());
                powerUsers.add(user);
            }
        }
        for (User user : pmList) {
            if (!uniqueUser.contains(user.getUsername())) {
                uniqueUser.add(user.getUsername());
                powerUsers.add(user);
            }
        }
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);  
        try {
            message = objectMapper.writeValueAsString(powerUsers);
        } catch (JsonProcessingException e) {
            message = null;
        }
        return SUCCESS;
    }
    
    public ProjectService getProjectService() {
        return projectService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public UserManageService getUserManageService() {
        return userManageService;
    }

    public void setUserManageService(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    public DepartmentManageService getDepartmentManageService() {
        return departmentManageService;
    }

    public void setDepartmentManageService(DepartmentManageService departmentManageService) {
        this.departmentManageService = departmentManageService;
    }

    public BasicDataService getBasicDataService() {
        return basicDataService;
    }

    public void setBasicDataService(BasicDataService basicDataService) {
        this.basicDataService = basicDataService;
    }

    public DisplayParam getDisplayParam() {
        return displayParam;
    }

    public void setDisplayParam(DisplayParam displayParam) {
        this.displayParam = displayParam;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectDeliver getProjectDeliver() {
        return projectDeliver;
    }

    public void setProjectDeliver(ProjectDeliver projectDeliver) {
        this.projectDeliver = projectDeliver;
    }

    public List<ProjectDeliver> getProjectDeliverList() {
        return projectDeliverList;
    }

    public void setProjectDeliverList(List<ProjectDeliver> projectDeliverList) {
        this.projectDeliverList = projectDeliverList;
    }

    public ProjectSupervisionVO getProjectSupervision() {
        return projectSupervision;
    }

    public void setProjectSupervision(ProjectSupervisionVO projectSupervision) {
        this.projectSupervision = projectSupervision;
    }

    public List<ProjectSupervisionVO> getSupervisionList() {
        return supervisionList;
    }

    public void setSupervisionList(List<ProjectSupervisionVO> supervisionList) {
        this.supervisionList = supervisionList;
    }

    public List<Map<String, Object>> getSupervisionMapList() {
        return supervisionMapList;
    }

    public void setSupervisionMapList(List<Map<String, Object>> supervisionMapList) {
        this.supervisionMapList = supervisionMapList;
    }

    public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
        return pmClosedLoopQuesnaireList;
    }

    public void setPmClosedLoopQuesnaireList(List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
        this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
    }

    public PmClosedLoopQuesnaire getPmClosedLoopQuesnaire() {
        return pmClosedLoopQuesnaire;
    }

    public void setPmClosedLoopQuesnaire(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
        this.pmClosedLoopQuesnaire = pmClosedLoopQuesnaire;
    }

    public PmClQuesnaireResultHeader getPmClQuesnaireResultHeader() {
        return pmClQuesnaireResultHeader;
    }

    public void setPmClQuesnaireResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
        this.pmClQuesnaireResultHeader = pmClQuesnaireResultHeader;
    }

    public List<PmClQuesnaireResultLine> getPmClQuesnaireResultLineList() {
        return pmClQuesnaireResultLineList;
    }

    public void setPmClQuesnaireResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        this.pmClQuesnaireResultLineList = pmClQuesnaireResultLineList;
    }

    public Map<String, Object> getCbForm() {
        return cbForm;
    }

    public void setCbForm(Map<String, Object> cbForm) {
        this.cbForm = cbForm;
    }

    public List<BasicDataBean> getSupervisionTypeList() {
        return supervisionTypeList;
    }

    public void setSupervisionTypeList(List<BasicDataBean> supervisionTypeList) {
        this.supervisionTypeList = supervisionTypeList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getPowerUserList() {
        return powerUserList;
    }

    public void setPowerUserList(List<User> powerUserList) {
        this.powerUserList = powerUserList;
    }

    @Override
    public void prepare() throws Exception {
    }

}
