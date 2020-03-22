package com.dp.plat.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.param.DisplayParam;

public interface DepartmentManageService {
	/**
	 * 查询部门列表
	 * @param displayParam
	 * @param department
	 * @return
	 */
	List<Department> queryDepartmentList(DisplayParam displayParam,Department department);
	/**
	 * 增加部门（废弃方法）
	 * @param department
	 * @return
	 */
	int addDepartmentSubmit(Department department);
	/**
	 * 从ERP刷新部门数据
	 */
	void refreshDepartment();
	/**
	 * 根据条件查询普通部门
	 * @param department
	 * @return
	 */
	List<Department> queryAllDepartments(Department department);
	
	/**
	 * 根据可以具有系统参数的部门信息集合
	 * @param department
	 * @return
	 */
	List<Department> queryDepartments();
	/**
	 * 查询officeCode ,officeName Map
	 * @return
	 */
	Map<String, String> queryDepartmentMap();
	/**
	 * 根据办事处编码，查询具体办事处
	 * @param officeCode
	 * @return
	 */
	Department queryDepartmentByDepartmentNum(String officeCode);
    /**
     * @param company
     * @return
     */
    List<Company> queryCompanyList(Company company);
    /**
     * @param company
     * @return
     */
    Company queryCompanyOne(Company company);
}
