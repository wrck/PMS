/**
 * 
 */
package com.dp.plat.service;

import java.util.ArrayList;
import java.util.List;

import com.dp.plat.data.bean.OperateLog;
import com.dp.plat.param.DisplayParam;

/**
 * @author Administrator
 * 
 */
public interface OpLogService extends BaseService
{
	/**
	 * @param displayParam
	 * @return
	 */
	List<OperateLog> queryLogList(DisplayParam displayParam);

	/**
	 * @param module
	 * @param action
	 * @param result
	 * @param info
	 */
	void insertLog();
	
	void delete(ArrayList<String> selected);
	
	List<com.dp.plat.data.OperateLog> queryLogAllList(DisplayParam displayParam);
}
