package com.dp.plat.pms.springmvc.service;


import java.util.List;

import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;

public interface IPmSynchronizeService extends ISynchronizeService{

	List<AfPrjProperty> selectAllAfPrjProperty();

	void clearAllAfPrjProperty();

	/**
	 * @param list
	 */
	void insertAfPrjProperty(List<AfPrjProperty> list);

}
