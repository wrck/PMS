package com.dp.plat.ehr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.constants.UrlPrefixConstant;
import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.entity.Job;
import com.dp.plat.ehr.job.EhrDataJob;
import com.dp.plat.ehr.service.IEhrCompanyService;
import com.dp.plat.ehr.service.IEhrDepartmentService;
import com.dp.plat.ehr.service.IEmployeeService;
import com.dp.plat.ehr.service.IJobService;
import com.dp.plat.ehr.utils.TreeNodeUtils;
import com.dp.plat.ehr.vo.DepartmentVO;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.ehr.vo.Select2Data;
import com.dp.plat.ehr.vo.SimpleEmployeeVO;

@RequestMapping(UrlPrefixConstant.EHR_DATA_URL)
@Controller
public class EHRDataController {
	@Autowired
	private IEhrCompanyService ehrCompanyService;

	@Autowired
	private IEhrDepartmentService ehrDepartmentService;

	@Autowired
	private IJobService jobService;

	@Autowired
	private IEmployeeService employeeService;
	
	@Autowired
	private IdentityService identityService;
	
	@RequestMapping
	public String listView() {
		return UrlPrefixConstant.EHR_DATA_URL + "company_list";
	}

	@RequestMapping("/company/list")
	public String findCompanies(PageParam<Object> pageParam, Company company, Model model) {
		pageParam.setModel(company);
		pageParam.setTotal(ehrCompanyService.countBySelective(null));
		pageParam.setFiltered(ehrCompanyService.countBySelectivePageable(pageParam));
		List<Object> companyList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		companyList = ehrCompanyService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", companyList);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("类型编码", "codeType"));
		columns.add(new DataTableColumn("类型名称", "codeTypeName"));
		columns.add(new DataTableColumn("属性编码", "code"));
		columns.add(new DataTableColumn("属性名称", "codeName"));
		columns.add(new DataTableColumn("描述", "description"));
		columns.add(new DataTableColumn("状态", "state", "initStateName"));
		columns.add(new DataTableColumn("创建时间", "createTime"));
		pageParam.setColumns(columns);
		pageParam.setRowId("compID");
		return UrlPrefixConstant.EHR_DATA_URL + "company_list";
	}

	@RequestMapping("/company/{id}")
	public String findCompany(@PathVariable("id") Integer id, Model model) {
		Company company = ehrCompanyService.selectByPrimaryKey(id);
		model.addAttribute("data", company);
		return UrlPrefixConstant.EHR_DATA_URL + "company_detail";
	}
	
	@RequestMapping("/company/tree")
	public String findCompaniesTree(Company company, Model model) throws Exception {
		List<TreeNode> companyList = ehrCompanyService.getTreeData(company);
		model.addAttribute("data", companyList);
		return null;
	}

	@RequestMapping("/department/list")
	public String findDepartments(PageParam<Object> pageParam, Department department, Model model) {
		pageParam.setModel(department);
		pageParam.setTotal(ehrDepartmentService.countBySelective(null));
		pageParam.setFiltered(ehrDepartmentService.countBySelectivePageable(pageParam));
		List<Object> departmentList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		departmentList = ehrDepartmentService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", departmentList);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("类型编码", "codeType"));
		columns.add(new DataTableColumn("类型名称", "codeTypeName"));
		columns.add(new DataTableColumn("属性编码", "code"));
		columns.add(new DataTableColumn("属性名称", "codeName"));
		columns.add(new DataTableColumn("描述", "description"));
		columns.add(new DataTableColumn("状态", "state", "initStateName"));
		columns.add(new DataTableColumn("创建时间", "createTime"));
		pageParam.setColumns(columns);
		pageParam.setRowId("depID");
		return UrlPrefixConstant.EHR_DATA_URL + "department_list";
	}

	@RequestMapping("/department/{id}")
	public String findDepartment(@PathVariable("id") Integer id, Model model) {
		Department department = ehrDepartmentService.selectByPrimaryKey(id);
		model.addAttribute("data", department);
		return UrlPrefixConstant.EHR_DATA_URL + "department_detail";
	}

	@RequestMapping("/department/tree")
	public String findDepartmentTree(DepartmentVO department, Model model) throws Exception {
//		DepartmentVO departmentVO = new DepartmentVO();
//		department.setDepID(115);
//		department.setDepGrade(1);
//		List<TreeNode> nodeList = ehrDepartmentService.getTreeData(department);
//		model.addAttribute("data", nodeList);
//		List<Company> companies = ehrCompanyService.selectBySelective(null);
//		List<Department> departmentList = ehrDepartmentService.selectBySelective(department);
//		Employee employee = new Employee();
//		employee.setEmpStatus(1);
//		List<Employee> employees = employeeService.selectBySelective(employee);
//		List<Object> list = new ArrayList<>();
//		list.addAll(companies);
//		list.addAll(departmentList);
//		list.addAll(employees);
//		List<TreeNode> treeList = TreeNodeUtils.constructTreeNodeData(list, null);
//		model.addAttribute("data", treeList);
		//department.setDepLV1ID("115");
		List<DepartmentVO> departmentList = ehrDepartmentService.selectVOBySelective(department);
		List<TreeNode> treeList = TreeNodeUtils.constructTreeNodeData(departmentList, null);
		model.addAttribute("data", treeList);
		return UrlPrefixConstant.PERFORMANCE_MANAGER + "department_tree";
	}
	
	@RequestMapping("/job/list")
	public String findJobs(PageParam<Object> pageParam, Job job, Model model) {
		pageParam.setModel(job);
		pageParam.setTotal(jobService.countBySelective(null));
		pageParam.setFiltered(jobService.countBySelectivePageable(pageParam));
		List<Object> jobList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		jobList = jobService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", jobList);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("类型编码", "codeType"));
		columns.add(new DataTableColumn("类型名称", "codeTypeName"));
		columns.add(new DataTableColumn("属性编码", "code"));
		columns.add(new DataTableColumn("属性名称", "codeName"));
		columns.add(new DataTableColumn("描述", "description"));
		columns.add(new DataTableColumn("状态", "state", "initStateName"));
		columns.add(new DataTableColumn("创建时间", "createTime"));
		pageParam.setColumns(columns);
		pageParam.setRowId("jobID");
		return UrlPrefixConstant.EHR_DATA_URL + "job_list";
	}
	
	@RequestMapping("/job/{id}")
	public String findJob(@PathVariable("id") Integer id, Model model) {
		Job job = jobService.selectByPrimaryKey(id);
		model.addAttribute("data", job);
		return UrlPrefixConstant.EHR_DATA_URL + "job_detail";
	}

	@RequestMapping("/employee/list")
	public String findEmployees(PageParam<EmployeeVO> pageParam, EmployeeVO employeeVO, Model model) {
		Long t= System.currentTimeMillis();
		List<EmployeeVO> employeeVOList = new ArrayList<>();
		pageParam.setTotal(employeeService.countBySelectivePageableVO(null));
		if(!pageParam.isLazyLoad()){
			pageParam.setModel(employeeVO);
			pageParam.setFiltered(employeeService.countBySelectivePageableVO(pageParam));
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
			employeeVOList = employeeService.selectBySelectivePageableVO(pageParam);
		}else{
			pageParam.setFiltered(0);
		}
		String isSimple = HttpContext.getCurrentRequest().getParameter("isSimple");
		if (isSimple != null && "true".equalsIgnoreCase(isSimple)) {
			List<SimpleEmployeeVO> simpleEmployeeVOList  = new ArrayList<>(employeeVOList.size());
			for (EmployeeVO temp : employeeVOList) {
				SimpleEmployeeVO vo = new SimpleEmployeeVO();
				BeanUtils.copyProperties(temp, vo);
				simpleEmployeeVOList.add(vo);
			}
			model.addAttribute("data", simpleEmployeeVOList);
		} else {
			model.addAttribute("data", employeeVOList);
		}
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("工号", "workNo"));
		columns.add(new DataTableColumn("姓名", "name"));
		columns.add(new DataTableColumn("公司", "compName"));
		columns.add(new DataTableColumn("部门", "depAllName"));
		columns.add(new DataTableColumn("岗位", "jobName"));
		pageParam.setColumns(columns);
		pageParam.setRowId("empID");
		System.out.println(System.currentTimeMillis()  - t);
		return UrlPrefixConstant.EHR_DATA_URL + "employee_list";
	}

	@RequestMapping("/employee/{id}")
	public String findEmployee(@PathVariable("id") Integer id, Model model) {
		EmployeeVO employeeVO = employeeService.selectVOByPrimaryKey(id);
		model.addAttribute("data", employeeVO);
		return UrlPrefixConstant.EHR_DATA_URL + "employee_detail";
	}
	
	/**
	 * 获取所有用户的select2 data
	 */
	@RequestMapping("/employeeDataList")
	public String listEmployeeSelect2Data(Select2Data select2Data,Integer index,Model model){
		List<Select2Data> employeeDataList = employeeService.selectEmployeeSelect2Data(select2Data);
		if (employeeDataList.isEmpty()) {
			List<Group> groups = identityService.createGroupQuery().groupNameLike("%" + select2Data.getText() + "%").list();
			for (Group group : groups) {
				Select2Data data = new Select2Data();
				data.setId("候选组");
				data.setText("候选组-" + group.getName());
				data.setInfo("候选组-" + group.getName());
				employeeDataList.add(data);
			}
		}
		model.addAttribute("employeeDataList",employeeDataList);
		if(index!=null){
			model.addAttribute("index",index);
		}
		return UrlPrefixConstant.PERFORMANCE_MANAGER + "modals/editAppraiser_detail";
	}
	
	@RequestMapping("/initUser")
	public void initUser(EmployeeVO employee, Model model){
		List<EmployeeVO> employeeList = new ArrayList<EmployeeVO>();
		employee.setEmpStatus(1);
		employee.setEmpType(1);
		HashMap<String, String> emp = JSON.parseObject(JSON.toJSONString(employee), HashMap.class);
		
		// 组合条件，例如某部门下的某岗位
		String empParams = SystemConfig.systemVariables.get("pm.sync.user.empParams");
		if (StringUtils.isNotBlank(empParams)) {
			HashMap<String, String> params = JSON.parseObject(empParams, HashMap.class);
			params.putAll(emp);
			employee = JSON.parseObject(JSON.toJSONString(params), EmployeeVO.class);
			employeeList = employeeService.selectEmployeeWithAccount(employee);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			
			// 按部门同步
			String depCodes = SystemConfig.systemVariables.get("pm.sync.user.officeCodes");
			String depIDs = SystemConfig.systemVariables.get("pm.sync.user.depIDs");
			if (StringUtils.isNoneBlank(depCodes, depIDs)) {
				params.put("depCodes", depCodes);
				params.put("depIDs", depIDs);
				params.putAll(emp);
				employee = JSON.parseObject(JSON.toJSONString(params), EmployeeVO.class);
				List<EmployeeVO> list = employeeService.selectEmployeeWithAccount(employee);
				employeeList.addAll(list);
			}
			params.clear();
			
			// 按岗位同步
			String jobIDs = SystemConfig.systemVariables.get("pm.sync.user.jobIDs");
			String jobCodes = SystemConfig.systemVariables.get("pm.sync.user.jobCodes");
			if (StringUtils.isNoneBlank(jobIDs, jobCodes)) {
				params.put("jobCodes", jobCodes);
				params.put("jobIDs", jobIDs);
				params.putAll(emp);
				
				employee = JSON.parseObject(JSON.toJSONString(params), EmployeeVO.class);
				List<EmployeeVO> list = employeeService.selectEmployeeWithAccount(employee);
				employeeList.addAll(list);
			}
		}
		employeeService.initUser(employeeList);
	}
	
	@RequestMapping("/syncData")
	public void syncData(Employee employee, Model model){
		EhrDataJob ehrDataJob = new EhrDataJob();
		ehrDataJob.execute();
	}
}
