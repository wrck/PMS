package com.dp.plat.warrantyCallback.action;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

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
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.QuestionnarieUtil;
import com.dp.plat.warrantyCallback.service.WarrantyCallbackService;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Preparable;

/**
 * 项目维保回访记录
 * 
 * @author w02611
 */
public class WarrantyCallbackAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 1L;

    // 全局变量
    private User user;

    private ProjectService projectService;
    private UserManageService userManageService;
    private DepartmentManageService departmentManageService;
    private BasicDataService basicDataService;
    private WarrantyCallbackService warrantyCallbackService;

    private List<Department> departmentList;
    private DisplayParam displayParam;
    private Project project;
    private String namespace;
    private String redirect;
    private String result;
    private String message;

    // 附件上传
    private ProjectDeliver projectDeliver;
    private List<ProjectDeliver> projectDeliverList;

    // 项目维护记录
    private ProjectWarrantyCallbackVO projectWarrantyCallback;
    private List<ProjectWarrantyCallbackVO> warrantyCallbackList;
    private List<Map<String, Object>> warrantyCallbackMapList;

    private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;

    private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;

    private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;

    private Map<String, Object> cbForm;

    private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;

    private List<BasicDataBean> warrantyCallbackTypeList;
    private List<User> powerUserList;
    
    @Override
    public void prepare() throws Exception {
    	HttpServletRequest request = getServletRequest();
    	ActionMapping actionMapping = (ActionMapping) request.getAttribute("struts.actionMapping");
    	namespace = actionMapping.getNamespace();
		if (namespace == null) {
			String referer = request.getHeader("Referer");
			if (StringUtils.isNotBlank(referer)) {
				URL refererUrl = new URL(referer);
//			if (refererUrl.getHost().equals(request.getRemoteHost())) {
				referer = refererUrl.getPath().replace(request.getContextPath(), "");
				namespace = referer.substring(0, referer.lastIndexOf("/"));
//			}
			}
		}
		if (namespace.startsWith("/")) {
			namespace = namespace.substring(1, namespace.length());
		}
		if (!namespace.startsWith("module")) {
			namespace = "module";
		}
		user = UserContext.getUserContext().getUser();
    }
    
    public void prepareExecute() {
        // 办事处集合
        departmentList = departmentManageService.queryDepartments();

        // 项目分类
//        warrantyCallbackTypeList = basicDataService.queryBasicDataBeans("warrantyCallbackType");
        
    	//施工类型--服务类型
    	warrantyCallbackTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
    }

    /**
     * 项目列表页面
     */
    public String execute() throws Exception {
        if (projectWarrantyCallback == null) {
            projectWarrantyCallback = new ProjectWarrantyCallbackVO();
        }
        if (displayParam == null) {
            displayParam = new DisplayParam();
        }
        displayParam.getParam();
        projectWarrantyCallback();
        return SUCCESS;
    }

    /**
     * 获取项目维护记录
     * 
     * @return
     */
    public String projectWarrantyCallback() {
        user = UserContext.getUserContext().getUser();
        if (!(user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER) || user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
                || user.isHasRole(MessageUtil.ROLE_AREA_LEADER))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
        
        if (projectWarrantyCallback == null) {
        	projectWarrantyCallback = new ProjectWarrantyCallbackVO();
        }
        
        if (displayParam == null) {
        	displayParam = new DisplayParam();
        	if (projectWarrantyCallback != null && projectWarrantyCallback.getProjectId() != null) {
        		displayParam.setPagesize(-1);
        	}
        }
        try {
        	displayParam.getParam();
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
        }
        
        if (StringUtils.isBlank(displayParam.getSort())) {
        	displayParam.setSort("pwc.id desc");
        }
        
        
        if (projectWarrantyCallback != null) {
            String officeCode = StringUtils.trimToEmpty(projectWarrantyCallback.getOfficeCode());
            if (projectWarrantyCallback.getProjectId() != null) {
                project = projectService.queryProjectSimplifyByProjectId(projectWarrantyCallback.getProjectId());
            }
            if (project != null) {
                officeCode = StringUtils.trimToEmpty(project.getColumn001());
                projectWarrantyCallback.setOfficeCode(officeCode);
                projectWarrantyCallback.setProjectName(project.getProjectName());
            }
//            if (user.getAreapower().contains(officeCode) && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))
//                    || (project != null && (user.getUsername().equals(project.getServiceManagerCode()) || user.getUsername().equals(project.getProgramManagerCode())
//                            ||	 user.getUsername().equals(project.getProgramManagerCodeB())))) {
//                projectWarrantyCallback.setHasPower(true);
//            }
			if (user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER)
					|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
				projectWarrantyCallback.setHasPower(true);
			}
            // warrantyCallbackList =
            // projectService.selectProjectWarrantyCallbackVOList(projectWarrantyCallback);
			if (!(user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
					|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
					|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
					|| user.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER))) {
				projectWarrantyCallback.setAreaPower(user.getAreapower());
				projectWarrantyCallback.setUserPower(user.getUsername());
			}
            projectWarrantyCallback.setIsDelete(false);
            warrantyCallbackMapList = warrantyCallbackService.selectProjectWarrantyCallbackMapList(projectWarrantyCallback, displayParam);
        } else {
            warrantyCallbackList = new ArrayList<>();
            warrantyCallbackMapList = new ArrayList<>();
        }
        // 办事处集合
        departmentList = departmentManageService.queryDepartments();
        //施工类型--服务类型
        warrantyCallbackTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
        return SUCCESS;
    }

    public String createProjectWarrantyCallback() {
        if (project == null || project.getProjectId() == 0) {
            setErrmsg("非法操作！");
            return ERROR;
        }
        project = projectService.queryProjectSimplifyByProjectId(project.getProjectId());
        user = UserContext.getUserContext().getUser();
        if (project == null || !(user.getAreapower().contains(StringUtils.trimToEmpty(project.getColumn001()))
                && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))
                || (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER))
                || (project != null && (user.getUsername().equals(project.getServiceManagerCode()) || user.getUsername().equals(project.getProgramManagerCode())
                        || user.getUsername().equals(project.getProgramManagerCodeB()))))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }

        if (projectWarrantyCallback == null || projectWarrantyCallback.getProjectId() == null) {
            Integer quesnaireId = null;
            if (projectWarrantyCallback != null && projectWarrantyCallback.getId() != null) {
                projectWarrantyCallback = warrantyCallbackService.selectProjectWarrantyCallbackVOById(projectWarrantyCallback.getId());
                quesnaireId = projectWarrantyCallback.getQuesnaireId();
            } else {
            	if (projectWarrantyCallback == null) {
            		projectWarrantyCallback = new ProjectWarrantyCallbackVO();
            	}
            	projectWarrantyCallback.setProjectId(project.getProjectId());
            	List<Map<String, Object>> list = warrantyCallbackService.selectProjectWarranty(projectWarrantyCallback, null);
            	if (list != null && !list.isEmpty()) {
            		try {
						org.apache.commons.beanutils.BeanUtils.populate(projectWarrantyCallback, list.get(0));
					} catch (Exception e) {
					}
            	}
            }
            warrantyCallbackTypeList = basicDataService.queryBasicDataBeans("warrantyCallbackType");
            PmClosedLoopQuesnaire quesObj = new PmClosedLoopQuesnaire();
            quesObj.setQuesType("projectWarrantyCallback");
//            quesObj.setQuesType("projectSupervision");
            
            // 获取生效的问卷分类
            pmClosedLoopQuesnaireList = QuestionnarieUtil.findPmClosedLoopQuesnaireList(quesObj);
            if (pmClosedLoopQuesnaireList != null && !pmClosedLoopQuesnaireList.isEmpty() && pmClosedLoopQuesnaire == null) {
                pmClosedLoopQuesnaire = pmClosedLoopQuesnaireList.get(0);
            }
            // 获取问卷模板的内容或者已填写的问卷内容
            if ((pmClosedLoopQuesnaire != null && pmClosedLoopQuesnaire.getId() != 0) || (projectWarrantyCallback != null && quesnaireId != null && !Integer.valueOf(0).equals(quesnaireId))) {
                int quesnaireState = 0;
                cbForm = QuestionnarieUtil.getCbForm(quesnaireId, pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, quesnaireState);
            }
            if (project != null) {
	            // 查询项目维保状态和维保级别、增值服务等
				ProjectWarrantyCallbackVO temp = new ProjectWarrantyCallbackVO();
				temp.setProjectId(project.getProjectId());
				List<Map<String, Object>> list = warrantyCallbackService.selectProjectWarranty(temp  , null);
				Map<String, Object> warrantyState = null;
            	if (list != null && !list.isEmpty()) {
            		warrantyState = list.get(0);
            	} else {
            		projectService.queryProjectWarrantyState(project.getProjectId());
            	}
	            projectWarrantyCallback.setWarrantyState(warrantyState);
            }
        } else {
        	projectWarrantyCallback.setProjectId(project.getProjectId());
        	List<Map<String, Object>> list = warrantyCallbackService.selectProjectWarranty(projectWarrantyCallback, null);
        	if (list != null && !list.isEmpty()) {
        		try {
					org.apache.commons.beanutils.BeanUtils.populate(projectWarrantyCallback, list.get(0));
				} catch (Exception e) {
				}
        	}
            // 问卷提交
            if (pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0) {
                if (pmClQuesnaireResultHeader.getStatus() == 1) {// 已提交，计算分数
                    QuestionnarieUtil.queryQuesnaireScore(pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                }
                // 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
                pmClQuesnaireResultHeader.setStatus(1);
                int quesnaireId = QuestionnarieUtil.addQuestionnaireResult(pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                projectWarrantyCallback.setQuesnaireId(quesnaireId);
//                projectWarrantyCallback.setState(true);
            }
            projectWarrantyCallback.setProjectId(project.getProjectId());
            projectWarrantyCallback.setProjectCode(project.getProjectCode());
            projectWarrantyCallback.setProjectName(project.getProjectName());
            projectWarrantyCallback.setOfficeCode(project.getColumn001());

            warrantyCallbackService.insertOrUpdateProjectWarrantyCallback(projectWarrantyCallback);
            return "redirect";
        }
        return SUCCESS;
    }

    public String deleteProjectWarrantyCallback() {
        result = "error";
        if (projectWarrantyCallback != null && projectWarrantyCallback.getId() != null) {
            user = UserContext.getUserContext().getUser();
            projectWarrantyCallback = warrantyCallbackService.selectProjectWarrantyCallbackVOById(projectWarrantyCallback.getId());
            if (projectWarrantyCallback == null || !((user.getUsername().equals(projectWarrantyCallback.getCreateBy())
                    || (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER))))) {
                message = "没有删除权限！";
            } else {
                projectWarrantyCallback.setIsDelete(true);
                warrantyCallbackService.insertOrUpdateProjectWarrantyCallback(projectWarrantyCallback);
                result = "success";
            }
        }
        return SUCCESS;
    }
    
    public String projectWarranty() throws Exception {
    	if (!(user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER) || user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
                || user.isHasRole(MessageUtil.ROLE_AREA_LEADER))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
    	if (displayParam == null) {
    		displayParam = new DisplayParam();
    	}
    	displayParam.setSort("officeCode, customerContact1 desc, customerContact2 desc");
    	displayParam.getParam();
    	if (projectWarrantyCallback == null) {
            projectWarrantyCallback = new ProjectWarrantyCallbackVO();
            warrantyCallbackMapList = new ArrayList<Map<String,Object>>();
            message = "暂未查询";
        } else {
	        warrantyCallbackMapList = warrantyCallbackService.selectProjectWarranty(projectWarrantyCallback, displayParam);
        }
    	if (user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
			projectWarrantyCallback.setHasPower(true);
		}
    	// 办事处集合
    	departmentList = departmentManageService.queryDepartments();
    	//施工类型--服务类型
    	warrantyCallbackTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
		return "projectWarranty";
    }
    
    public String customerProject() throws Exception {
    	if (!(user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER) || user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
                || user.isHasRole(MessageUtil.ROLE_AREA_LEADER))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
    	if (displayParam == null) {
    		displayParam = new DisplayParam();
    	}
//    	displayParam.setSort("finalCustomerName, customerContact1 desc, customerContact2 desc");
    	displayParam.getParam();
    	if (projectWarrantyCallback == null) {
            projectWarrantyCallback = new ProjectWarrantyCallbackVO();
            warrantyCallbackMapList = new ArrayList<Map<String,Object>>();
            message = "暂未查询";
        } else {
	        warrantyCallbackMapList = warrantyCallbackService.selectCustomerProjectWarrantyCallbackStatistics(projectWarrantyCallback, displayParam);
        }
    	if (user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
			projectWarrantyCallback.setHasPower(true);
		}
    	// 办事处集合
    	departmentList = departmentManageService.queryDepartments();
    	//施工类型--服务类型
    	warrantyCallbackTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
		return "customerProject";
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

    public WarrantyCallbackService getWarrantyCallbackService() {
		return warrantyCallbackService;
	}

	public void setWarrantyCallbackService(WarrantyCallbackService warrantyCallbackService) {
		this.warrantyCallbackService = warrantyCallbackService;
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

    public ProjectWarrantyCallbackVO getProjectWarrantyCallback() {
        return projectWarrantyCallback;
    }

    public void setProjectWarrantyCallback(ProjectWarrantyCallbackVO projectWarrantyCallback) {
        this.projectWarrantyCallback = projectWarrantyCallback;
    }

    public List<ProjectWarrantyCallbackVO> getWarrantyCallbackList() {
        return warrantyCallbackList;
    }

    public void setWarrantyCallbackList(List<ProjectWarrantyCallbackVO> warrantyCallbackList) {
        this.warrantyCallbackList = warrantyCallbackList;
    }

    public List<Map<String, Object>> getWarrantyCallbackMapList() {
        return warrantyCallbackMapList;
    }

    public void setWarrantyCallbackMapList(List<Map<String, Object>> warrantyCallbackMapList) {
        this.warrantyCallbackMapList = warrantyCallbackMapList;
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

    public List<BasicDataBean> getWarrantyCallbackTypeList() {
        return warrantyCallbackTypeList;
    }

    public void setWarrantyCallbackTypeList(List<BasicDataBean> warrantyCallbackTypeList) {
        this.warrantyCallbackTypeList = warrantyCallbackTypeList;
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

    
}
