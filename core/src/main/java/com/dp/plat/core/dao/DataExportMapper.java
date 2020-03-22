/**
 * 
 */
package com.dp.plat.core.dao;

import java.util.List;

import com.dp.plat.core.vo.MapParam;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

/**
 * @author w02611
 *
 */
public interface DataExportMapper {

	/**
	 * @param pageParam
	 * @return
	 */
	List<UserDetail> exportUserDetail(PageParam<UserDetail> pageParam);

	List<MapParam> queryDynamicColumn(String objectName);

}
