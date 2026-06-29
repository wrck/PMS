/**
 * 
 */
package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.DataExportMapper;
import com.dp.plat.core.service.IDataExportService;
import com.dp.plat.core.util.ExportUtils;
import com.dp.plat.core.util.MessageUtils;
import com.dp.plat.core.vo.MapParam;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

/**
 * @author w02611
 *
 */
@Service("dataExportService")
public class DataExportService implements IDataExportService{
	@Resource
	private DataExportMapper mapper;

	@Override
	public List<UserDetail> exportUserDetail(PageParam<UserDetail> pageParam) {
		return mapper.exportUserDetail(pageParam);
	}

	@Override
	public Map<String, String> queryDynamicColumn(String objectName) {
		//查询自视图view_dynamic_column_4_export
		Map<String, String> map = null;
		try {
			List<MapParam> params = mapper.queryDynamicColumn(objectName);
			map = new HashMap<String, String>();
			for(MapParam param:params) {
				map.put(param.getKey(), param.getValue());
			}
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	@Override
	public String queryDynamicColumnSort(String objectName) {
	    String columnSort = null;
	    try {
	        Class<?> clazz = ExportUtils.getClass(objectName);
	        columnSort = MessageUtils.getLocaleMessage("export." + clazz.getSimpleName() + ".sort");
	    } catch(Exception e) {
	        columnSort = MessageUtils.getLocaleMessage("export." + objectName + ".sort");
	    }
		return columnSort;
	}
}
