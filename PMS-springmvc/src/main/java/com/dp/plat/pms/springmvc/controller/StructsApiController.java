package com.dp.plat.pms.springmvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dp.plat.context.SpringContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;

@Controller
@RequestMapping("api")
public class StructsApiController {
	
//	@Autowired
//	@Qualifier("departmentManageService")
	private DepartmentManageService departmentManageService;
	
	/**
	 * 查询带系统参数的办事处信息
	 * @param model
	 */
	@RequestMapping("/departmentList")
	public void queryDepartment(Department department, Model model) {
		List<Department> departments = departmentManageService.queryDepartments();
		model.addAttribute("data", departments);
	}
	
	/**
	 * 查询生效的公司列表
	 */
	@RequestMapping("/companyList")
	public void queryCompany(Company company, Model model) {
        company.setStatus(1);
        DepartmentManageService departmentManageService = SpringContext.getApplicationContext().getBean("departmentManageService", DepartmentManageService.class);
		List<Company> companyList = departmentManageService .queryCompanyList(company);
		model.addAttribute("data", companyList);
	}
	
	/**
	 * 查询生效的公司列表
	 */
	@RequestMapping("/basicDataByType")
	public void queryDataBasic(BasicDataBean basicData, Model model) {
		BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService", BasicDataService.class);
		List<BasicDataBean> basicDataBeans = basicDataService.queryBasicDataBeans(basicData.getBasicDataTypeCode());
		model.addAttribute("data", basicDataBeans);
	}
	
}