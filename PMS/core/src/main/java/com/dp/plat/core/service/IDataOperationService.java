package com.dp.plat.core.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.entity.DataOperation;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 *
 * Created by CodeGenerator
 */
public interface IDataOperationService extends IAbstractBaseService<DataOperation> {

	DataOperation selectByOperationName(String operationName);
	
	int checkOperationName(String operationName);
	
	Map<String, Object> queryExportColumns(String sql);

	List<Map<String, Object>> queryExportDataByMap(Map<String, Object> params);

	List<Map<String, Object>> queryExportData(PageParam<?> pageParam);

	long countExportData(PageParam<?> pageParam);
}
