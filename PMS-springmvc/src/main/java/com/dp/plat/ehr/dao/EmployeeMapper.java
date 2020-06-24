package com.dp.plat.ehr.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.vo.EmployeeAppraiserVO;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.ehr.vo.Select2Data;

public interface EmployeeMapper extends AbstractBaseMapper<Employee> {

    /**
	 * @param ids
	 * @return
	 */
    List<EmployeeVO> selectEmployeeVOByIds(String ids);

    /**
     * 查询员工数据
     * @return
     */
	List<Select2Data> selectEmployeeSelect2Data(Select2Data select2Data);

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

	EmployeeVO selectByWorkNo(String workNo);

	EmployeeVO selectVOByPrimaryKey(Integer id);

	List<EmployeeAppraiserVO> selectEmployeeAppraiserBySelectivePageableVO(PageParam<EmployeeVO> pageParam);

	/**
	 * @param employee
	 * @return
	 */
	List<EmployeeVO> selectEmployeeWithAccount(EmployeeVO employee);
}
