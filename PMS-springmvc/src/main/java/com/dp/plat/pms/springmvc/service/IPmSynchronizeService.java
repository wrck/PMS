package com.dp.plat.pms.springmvc.service;

import java.util.List;

import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;

public interface IPmSynchronizeService extends ISynchronizeService {

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

}
