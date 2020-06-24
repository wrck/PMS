package com.dp.plat.ehr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.dao.EhrCompanyMapper;
import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.service.IEhrCompanyService;
import com.dp.plat.ehr.utils.TreeNodeUtils;

/**
 *
 * Created by CodeGenerator
 */
@Service("ehrCompanyService")
public class EhrCompanyService extends AbstractBaseService<EhrCompanyMapper, Company> implements IEhrCompanyService {

	@Override
	public List<TreeNode> getTreeData(Company company) throws Exception {
		List<Company> companyList = dao.selectBySelective(company);
		List<TreeNode> treeList = TreeNodeUtils.constructTreeNodeData(companyList, null);
		return treeList;
	}
}
