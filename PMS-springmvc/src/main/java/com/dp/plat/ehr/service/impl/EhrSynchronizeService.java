package com.dp.plat.ehr.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.SynchronizeService;
import com.dp.plat.ehr.dao.EhrSynchronizeMapper;
import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.entity.EHRLoginAccount;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.entity.Holiday;
import com.dp.plat.ehr.entity.Job;
import com.dp.plat.ehr.service.IEhrSynchronizeService;

@Service("ehrSynchronizeService")
public class EhrSynchronizeService extends SynchronizeService implements IEhrSynchronizeService  {

	@Resource
	private EhrSynchronizeMapper ehrSynchronizeMapper;
	
	public List<Company> selectAllCompany() {
		return ehrSynchronizeMapper.selectAllCompany();
	}
	public List<Department> selectAllDepartment() {
		return ehrSynchronizeMapper.selectAllDepartment();
	}
	
	@Override
	public List<Job> selectAllJob() {
		return ehrSynchronizeMapper.selectAllJob();
	}
	public List<Employee> selectAllEmployee() {
		return ehrSynchronizeMapper.selectAllEmployee();
	}
	public List<EHRLoginAccount> selectAllEHRLoginAccount() {
		return ehrSynchronizeMapper.selectAllEHRLoginAccount();
	}
	public List<Holiday> selectAllHoliday() {	
		return ehrSynchronizeMapper.selectAllHoliday();
	}
	@Override
	public void clearAllHoliday() {
		ehrSynchronizeMapper.clearAllHoliday();
	}
	public void insertCompany(List<Company> list) {
		ehrSynchronizeMapper.insertCompany(list);
	}
	public void insertDepartment(List<Department> list) {
		ehrSynchronizeMapper.insertDepartment(list);
	}
	public void insertJob(List<Job> list) {
		ehrSynchronizeMapper.insertJob(list);
	}
	public void insertEmployee(List<Employee> list) {
		ehrSynchronizeMapper.insertEmployee(list);
	}
	public void insertEHRLoginAccount(List<EHRLoginAccount> list) {
		ehrSynchronizeMapper.insertEHRLoginAccount(list);
	}
	public void insertHoliday(List<Holiday> list) {
		ehrSynchronizeMapper.insertHoliday(list);
	}
}
