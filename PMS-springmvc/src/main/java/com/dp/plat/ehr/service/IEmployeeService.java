package com.dp.plat.ehr.service;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.vo.EmployeeAppraiserVO;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.ehr.vo.Select2Data;

/**
 *
 * Created by CodeGenerator
 */
public interface IEmployeeService extends IAbstractBaseService<Employee> {

    /**
	 * @param pageParam
	 * @return
	 */
    long countBySelectivePageableVO(PageParam<EmployeeVO> pageParam);

	/**
	 * @param pageParam
	 * @return
	 */
	List<EmployeeVO> selectBySelectivePageableVO(PageParam<EmployeeVO> pageParam);

    /**
	 * @param id
	 * @return
	 */
    EmployeeVO selectVOByPrimaryKey(Integer id);

	/**
	 * @param ids
	 * @return
	 */
	List<EmployeeVO> selectEmployeeVOByIds(String ids);
	/**
	 * 查询员工列表数据
	 * @return
	 */
	List<Select2Data> selectEmployeeSelect2Data(Select2Data select2Data);

	/**
	 * @param employeeList
	 */
	void initUser(List<EmployeeVO> employeeList);
	/**
	 * 根据工号查询
	 */
	EmployeeVO selectByWorkNo(String workNo);
	/**
	 * 查询评估人关系
	 * @param pageParam
	 * @return
	 */
	List<EmployeeAppraiserVO> selectEmployeeAppraiserBySelectivePageableVO(PageParam<EmployeeVO> pageParam);

	/**
	 * @param employee
	 * @return
	 */
	List<EmployeeVO> selectEmployeeWithAccount(EmployeeVO employee);
	
}
