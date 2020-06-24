package com.dp.plat.action;

import java.util.ArrayList;
import java.util.List;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.PmClCBData;
import com.dp.plat.param.DataQueryParam;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DataAnalysisService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.util.MessageUtil;
/**
 *   回访数据信息统计
 * @author admin
 *
 */
public class DataAnalysisAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private BasicDataService basicDataService;
	private DataAnalysisService dataAnalysisService;
	private DepartmentManageService departmentManageService;
	private DataQueryParam dataQueryParam;
	private List<Department> officeList;
	private List<BasicDataBean> serviceTypeList;
	private List<BasicDataBean> phaseList;
	private List<BasicDataBean> projectTypeList;
	private List<BasicDataBean> navTabList;//项目维护页面选项卡集合
	private List<PmClCBData>pmClCBDataList=new ArrayList<PmClCBData>();
	private int returnType;
	private DisplayParam displayParam;
	
	public String execute(){
		
		//办事处
		officeList = departmentManageService.queryDepartments();
		//项目阶段划分集合
		phaseList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PRJ_PHASE);
		//施工类型--服务类型
		serviceTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
		//项目类型
		projectTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PROTYPE);
		
		//选项卡
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_DATA_TAB);
		
		if(getcbdata()==-1) {//回访数据统计
			return ERROR;
		}
		return SUCCESS;
	}
	
	private int getcbdata(){
		if(dataQueryParam==null)
			dataQueryParam=new DataQueryParam();
		else
			returnType = 2;
		pmClCBDataList=dataAnalysisService.quesyCbDataList(dataQueryParam);
/*		Project proObj=null;
		for (PmClCBData cbdata : pmClCBDataList) { 
			proObj=projectService.queryProjectById(cbdata.getProjectId());
			if(proObj==null)return -1;
			cbdata.setOfficeName(proObj.getOfficeName());
			cbdata.setPmRealName(proObj.getProgramManagerCodeforjson());
		}*/
		
		return 1;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public DataQueryParam getDataQueryParam() {
		return dataQueryParam;
	}

	public void setDataQueryParam(DataQueryParam dataQueryParam) {
		this.dataQueryParam = dataQueryParam;
	}

	public void setDepartmentManageService(
			DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public List<Department> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Department> officeList) {
		this.officeList = officeList;
	}

	public List<BasicDataBean> getServiceTypeList() {
		return serviceTypeList;
	}

	public void setServiceTypeList(List<BasicDataBean> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	public List<BasicDataBean> getPhaseList() {
		return phaseList;
	}

	public void setPhaseList(List<BasicDataBean> phaseList) {
		this.phaseList = phaseList;
	}

	public List<BasicDataBean> getProjectTypeList() {
		return projectTypeList;
	}

	public void setProjectTypeList(List<BasicDataBean> projectTypeList) {
		this.projectTypeList = projectTypeList;
	}

	public void setDataAnalysisService(DataAnalysisService dataAnalysisService) {
		this.dataAnalysisService = dataAnalysisService;
	}

	public List<BasicDataBean> getNavTabList() {
		return navTabList;
	}

	public void setNavTabList(List<BasicDataBean> navTabList) {
		this.navTabList = navTabList;
	}

	public List<PmClCBData> getPmClCBDataList() {
		return pmClCBDataList;
	}

	public void setPmClCBDataList(List<PmClCBData> pmClCBDataList) {
		this.pmClCBDataList = pmClCBDataList;
	}

	public int getReturnType() {
		return returnType;
	}

	public void setReturnType(int returnType) {
		this.returnType = returnType;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
	
	
}
