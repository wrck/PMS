package com.dp.plat.pms.springmvc.dao;

import java.util.Collection;
import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;

public interface IndustryLeakMapper extends AbstractBaseMapper<IndustryLeak> {

	void createTempTable(String tempTableName);

	void insertTempData(String tempTableName, List<Object> list, Collection<String> columns);

	List<?> selectTempData(PageParam<?> pageParam);

	long countTempData(PageParam<?> pageParam);

	void dropTempTable(String tempTableName);
}
