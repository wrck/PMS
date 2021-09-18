/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

/**
 * @author w02611
 *
 */
public interface IDataExportService {

	/**
	 * @param pageParam
	 * @return
	 */
	List<UserDetail> exportUserDetail(PageParam<UserDetail> pageParam);
	
	/**
	 * 	查询动态列数据
	 * @param objectName
	 * @return
	 */
	Map<String, String> queryDynamicColumn(String objectName);
	/**
	 * 	查询列动态排序
	 * @param objectName
	 * @return
	 */
	String queryDynamicColumnSort(String objectName);
}
