package com.dp.plat.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.param.DisplayParam;

public interface DepartmentManageDao {
	List<Department> queryDepartmentList(DisplayParam displayParam,Department department);
	
	int addDepartmentSubmit(Department department);
	
	void refreshDepartment();
	
	List<Department> queryAllDepartments(Department department);

	Map<String, String> queryDepartmentMap();

	/**
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
