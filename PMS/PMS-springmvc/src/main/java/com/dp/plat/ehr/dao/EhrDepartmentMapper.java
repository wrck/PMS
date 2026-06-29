package com.dp.plat.ehr.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.vo.DepartmentVO;

public interface EhrDepartmentMapper extends AbstractBaseMapper<Department> {

	List<DepartmentVO> selectDepartmentWithChildren(DepartmentVO departmentVO);

	List<TreeNode> selectDepartmentWithChildrenTreeNode(DepartmentVO departmentVO);

	List<DepartmentVO> selectVOBySelective(DepartmentVO departmentVO);

	long countVOBySelective(DepartmentVO departmentVO);
}
