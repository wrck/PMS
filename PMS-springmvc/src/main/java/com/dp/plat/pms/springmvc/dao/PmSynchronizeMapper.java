package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.pms.springmvc.entity.OfstContractHead;
import com.dp.plat.pms.springmvc.entity.OfstContractHeadSAP;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;

public interface PmSynchronizeMapper {

	List<AfPrjProperty> selectAllAfPrjProperty();

	void clearAllAfPrjProperty();

	void insertAfPrjProperty(List<AfPrjProperty> list);

	List<ProjectProduct> selectAllProjectProduct();

	void clearAllProjectProduct();

	void insertProjectProduct(List<ProjectProduct> list);

	/**
	 * 拆分工程实施项目以及安服项目
	 * 
	 * @param productCode
	 */
	void splitAfProjectByProductCode(String orDefault);

	int insertOfstContractHeadSAP(List<OfstContractHeadSAP> record);
	List<OfstContractHead> selectAllOfstContractHeadSAP();
	void clearAllOfstContractHeadSAP();
}
