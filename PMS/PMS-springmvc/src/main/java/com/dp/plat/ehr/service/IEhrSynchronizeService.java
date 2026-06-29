package com.dp.plat.ehr.service;


import java.util.List;

import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.entity.EHRLoginAccount;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.entity.Holiday;
import com.dp.plat.ehr.entity.Job;

public interface IEhrSynchronizeService extends ISynchronizeService{

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
	List<Job> selectAllJob();

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
	void insertJob(List<Job> list);

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

}
