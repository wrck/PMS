package com.dp.plat.ehr.dao;

import java.util.List;

import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.entity.EHRLoginAccount;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.entity.Holiday;
import com.dp.plat.ehr.entity.Job;

public interface EhrSynchronizeMapper {

	/**
	 * @return
	 */
	List<Company> selectAllCompany();

	/**
	 * @return
	 */
	List<Department> selectAllDepartment();

	/**
	 * @return
	 */
	List<Employee> selectAllEmployee();

	/**
	 * @return
	 */
	List<EHRLoginAccount> selectAllEHRLoginAccount();

	/**
	 * @return
	 */
	List<Holiday> selectAllHoliday();

	void clearAllHoliday();

	/**
	 * @param companyList
	 */
	void insertCompany(List<Company> companyList);

	/**
	 * @param list
	 */
	void insertDepartment(List<Department> list);

	/**
	 * @param list
	 */
	void insertEmployee(List<Employee> list);

	/**
	 * @param list
	 */
	void insertEHRLoginAccount(List<EHRLoginAccount> list);

	/**
	 * @param list
	 */
	void insertHoliday(List<Holiday> list);

	void insertJob(List<Job> list);

	List<Job> selectAllJob();

}
