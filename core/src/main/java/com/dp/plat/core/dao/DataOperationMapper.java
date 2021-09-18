package com.dp.plat.core.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.entity.DataOperation;
import com.dp.plat.core.vo.PageParam;

public interface DataOperationMapper extends AbstractBaseMapper<DataOperation> {

	DataOperation selectByOperationName(String operationName);
	
	int checkOperationName(String operationName);

	Map<String, Object> queryExportColumns(String sql);

	List<Map<String, Object>> queryExportColumns(Map<String, Object> params);

	List<Map<String, Object>> queryExportDataByMap(Map<String, Object> params);

	List<Map<String, Object>> queryExportData(PageParam<?> pageParam);

	long countExportData(PageParam<?> pageParam);
}
