package com.dp.plat.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.dao.DepartmentManageDao;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.param.DisplayParam;



public class DepartmentManageServiceImpl extends BaseServiceImpl implements DepartmentManageService{
	
	private DepartmentManageDao departmentManageDao;

	public List<Department> queryDepartmentList(DisplayParam displayParam,Department department){
		return departmentManageDao.queryDepartmentList(displayParam, department);
	}
	
	public int addDepartmentSubmit(Department department){
		return departmentManageDao.addDepartmentSubmit(department);
	}
	
	public void refreshDepartment(){
		departmentManageDao.refreshDepartment();	
	}
	
	public DepartmentManageDao getDepartmentManageDao() {
		return departmentManageDao;
	}

	public void setDepartmentManageDao(DepartmentManageDao departmentManageDao) {
		this.departmentManageDao = departmentManageDao;
	}

	@Override
	public List<Department> queryAllDepartments(Department department) {
		return departmentManageDao.queryAllDepartments(department);
	}

	@Override
	public List<Department> queryDepartments() {
		Department department = new Department();
		department.setIsparam(1);
		return this.queryAllDepartments(department);
	}

	@Override
	public Map<String, String> queryDepartmentMap() {
		return departmentManageDao.queryDepartmentMap();
	}

	@Override
	public Department queryDepartmentByDepartmentNum(String officeCode) {
		return departmentManageDao.queryDepartmentByDepartmentNum(officeCode);
	}

    @Override
    public List<Company> queryCompanyList(Company company) {
        return departmentManageDao.queryCompanyList(company);
    }

    @Override
    public Company queryCompanyOne(Company company) {
        return departmentManageDao.queryCompanyOne(company);
    }
}
