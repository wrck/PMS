package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.pms.springmvc.vo.AfPrjProperty;

public interface PmSynchronizeMapper {

	List<AfPrjProperty> selectAllAfPrjProperty();

	void clearAllAfPrjProperty();

	void insertAfPrjProperty(List<AfPrjProperty> list);

}
