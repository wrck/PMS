package com.dp.plat.ehr.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.dao.EhrDepartmentMapper;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.service.IEhrDepartmentService;
import com.dp.plat.ehr.utils.TreeNodeUtils;
import com.dp.plat.ehr.vo.DepartmentVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("ehrDepartmentService")
public class EhrDepartmentService extends AbstractBaseService<EhrDepartmentMapper, Department> implements IEhrDepartmentService {

	@Override
	public List<TreeNode> getTreeData(Department department) throws Exception {
		List<Department> departmentList = dao.selectBySelective(department);
		List<TreeNode> treeList = TreeNodeUtils.constructTreeNodeData(departmentList, null);
		return treeList;
	}

	@Override
	public List<TreeNode> getTreeData(DepartmentVO departmentVO) throws Exception {
		List<TreeNode> treeList = dao.selectDepartmentWithChildrenTreeNode(departmentVO);
		List<TreeNode> depL1 = new ArrayList<>();
		HashMap<String, TreeNode> hashMap = new HashMap<>();
		for (TreeNode dep : treeList) {
			if (dep.getParentId() == null) {
				depL1.add(dep);
			}
			hashMap.put(dep.getId().toString(), dep);
		}
		for (TreeNode dep : treeList) {
			TreeNode parentDep = hashMap.get(dep.getParentId());
			if (parentDep != null) {
				parentDep.getNodes().add(dep);
			}
		}
		return depL1;
	}
	
	
	@Override
	public long countVOBySelective(DepartmentVO departmentVO) {
		return dao.countVOBySelective(departmentVO);
	}

	@Override
	public List<DepartmentVO> selectVOBySelective(DepartmentVO departmentVO) {
		return dao.selectVOBySelective(departmentVO);
	}
}
