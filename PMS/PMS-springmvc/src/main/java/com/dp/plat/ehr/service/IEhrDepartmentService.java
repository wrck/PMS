package com.dp.plat.ehr.service;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.vo.DepartmentVO;

/**
 *
 * Created by CodeGenerator
 */
public interface IEhrDepartmentService extends IAbstractBaseService<Department> {

	/**
	 * @param department
	 * @return
	 * @throws Exception 
	 */
	List<TreeNode> getTreeData(Department department) throws Exception;

	List<TreeNode> getTreeData(DepartmentVO departmentVO) throws Exception;

	long countVOBySelective(DepartmentVO departmentVO);

	List<DepartmentVO> selectVOBySelective(DepartmentVO departmentVO);

}
