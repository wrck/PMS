package com.dp.plat.action;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Project;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.MessageUtil;

/**
 * 项目管理 Action 基类
 * 包含公共变量、Service 引用和基础方法
 * 
 * @author PMS Team
 */
public abstract class ProjectBaseAction extends BaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // Service 引用
    protected ProjectService projectService;
    protected DepartmentManageService departmentManageService;
    protected BasicDataService basicDataService;
    
    // 页面参数
    protected DisplayParam displayParam;
    protected Project project;
    protected int projectId;
    protected String contractNo;
    protected int result;
    protected String message;
    protected String redirect;
    
    // 页面数据
    protected List<Department> departmentList;
    protected List<Company> companyList;
    protected List<BasicDataBean> projectTypeList;
    protected List<BasicDataBean> projectRankList;
    protected List<BasicDataBean> deliverStateList;
    protected List<BasicDataBean> projectPlanStateList;
    protected List<BasicDataBean> projectExecutionStateList;
    protected List<BasicDataBean> projectCloseProcessStateList;
    protected List<BasicDataBean> projectTimeList;
    protected List<BasicDataBean> ssfsList;
    protected List<BasicDataBean> majorProjectLevelList;
    protected List<BasicDataBean> navTabList;
    
    // Setter 方法
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    public void setDepartmentManageService(DepartmentManageService departmentManageService) {
        this.departmentManageService = departmentManageService;
    }
    
    public void setBasicDataService(BasicDataService basicDataService) {
        this.basicDataService = basicDataService;
    }
    
    public void setDisplayParam(DisplayParam displayParam) {
        this.displayParam = displayParam;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    // Getter 方法
    public Project getProject() {
        return project;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public String getContractNo() {
        return contractNo;
    }
    
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
    
    public int getResult() {
        return result;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getRedirect() {
        return redirect;
    }
    
    public List<Department> getDepartmentList() {
        return departmentList;
    }
    
    public List<Company> getCompanyList() {
        return companyList;
    }
    
    public List<BasicDataBean> getProjectTypeList() {
        return projectTypeList;
    }
    
    public List<BasicDataBean> getProjectRankList() {
        return projectRankList;
    }
    
    public List<BasicDataBean> getDeliverStateList() {
        return deliverStateList;
    }
    
    public List<BasicDataBean> getProjectPlanStateList() {
        return projectPlanStateList;
    }
    
    public List<BasicDataBean> getProjectExecutionStateList() {
        return projectExecutionStateList;
    }
    
    public List<BasicDataBean> getProjectCloseProcessStateList() {
        return projectCloseProcessStateList;
    }
    
    public List<BasicDataBean> getProjectTimeList() {
        return projectTimeList;
    }
    
    public List<BasicDataBean> getSsfsList() {
        return ssfsList;
    }
    
    public List<BasicDataBean> getMajorProjectLevelList() {
        return majorProjectLevelList;
    }
    
    public List<BasicDataBean> getNavTabList() {
        return navTabList;
    }
    
    /**
     * 初始化项目查询参数
     */
    protected void initProject() {
        if (project == null) {
            project = new Project();
        }
        if (displayParam == null) {
            displayParam = new DisplayParam();
        }
    }
    
    /**
     * 初始化公共数据
     */
    protected void prepareCommonData() {
        departmentList = departmentManageService.queryDepartments();
        
        Company company = new Company();
        company.setStatus(1);
        companyList = departmentManageService.queryCompanyList(company);
        
        projectTypeList = basicDataService.queryBasicDataBeans("02");
        deliverStateList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_DELIVERSTATE);
        projectPlanStateList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_ENGINEERSTATE);
        projectExecutionStateList = basicDataService.queryBasicDataBeans("projectExecutionState");
        projectCloseProcessStateList = basicDataService.queryBasicDataBeans("projectCloseProcessState");
        projectRankList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PRORANK);
        majorProjectLevelList = basicDataService.queryBasicDataBeans("majorProjectLevel");
        projectTimeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PORJECT_TIME);
        ssfsList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
    }
}
