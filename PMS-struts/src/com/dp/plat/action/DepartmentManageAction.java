package com.dp.plat.action;

import java.util.List;

import com.dp.plat.data.bean.Department;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.DepartmentManageService;

public class DepartmentManageAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private Department department;
	private List<Department>departmentList;
	private DisplayParam displayParam;
	private DepartmentManageService departmentManageService;
	
	
	@Override
	public String execute() throws Exception {
		if(displayParam == null){
			displayParam = new DisplayParam();
		}
		if(departmentList!=null){
			departmentList.clear();
		}
		if(department==null){
			department=new Department();
		}
		displayParam.getParam();
		departmentList=departmentManageService.queryDepartmentList(displayParam, department);
		return SUCCESS;
	}
	
public String refresh() throws Exception {
	departmentManageService.refreshDepartment();
	return SUCCESS;
	}
	
	public String add() throws Exception {
		
		return INPUT;
	}
	
	public String addSubmit() throws Exception{
		int addId=departmentManageService.addDepartmentSubmit(department);
		if(addId<=0){
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String edit() throws Exception {
		return super.execute();
	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public DepartmentManageService getDepartmentManageService() {
		return departmentManageService;
	}

	public void setDepartmentManageService(
			DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Department> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<Department> departmentList) {
		this.departmentList = departmentList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
