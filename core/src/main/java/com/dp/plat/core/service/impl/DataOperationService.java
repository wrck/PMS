package com.dp.plat.core.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.dao.DataOperationMapper;
import com.dp.plat.core.entity.DataOperation;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.dp.plat.core.service.IDataOperationService;

/**
 *
 * Created by CodeGenerator
 */
@Service("dataOperationService")
public class DataOperationService extends AbstractBaseService<DataOperationMapper, DataOperation> implements IDataOperationService {

	@Override
	public DataOperation selectByOperationName(String operationName) {
		return dao.selectByOperationName(operationName);
	}

	@Override
	public int checkOperationName(String operationName) {
		return dao.checkOperationName(operationName);
	}

	@Override
	public Map<String, Object> queryExportColumns(String sql) {
//		sql = sql.replaceAll("<[^>]+>", "");
		return dao.queryExportColumns(sql);
	}

	@Override
	public List<Map<String, Object>> queryExportDataByMap(Map<String, Object> params) {
		return dao.queryExportDataByMap(params);
	}

	@Override
	public List<Map<String, Object>> queryExportData(PageParam<?> pageParam) {
		return dao.queryExportData(pageParam);
	}

	@Override
	public long countExportData(PageParam<?> pageParam) {
		return dao.countExportData(pageParam);
	}
	
}
