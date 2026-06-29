package com.dp.plat.ehr.service;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.ehr.entity.Company;

/**
 *
 * Created by CodeGenerator
 */
public interface IEhrCompanyService extends IAbstractBaseService<Company> {

	/**
	 * @param company
	 * @return
	 * @throws Exception 
	 */
	List<TreeNode> getTreeData(Company company) throws Exception;
}
