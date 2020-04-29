/**
 * 
 */
package com.dp.plat.activiti.service;

import java.util.List;
import java.util.Map;

/**
 * @author w02611
 *
 */
public interface ITraceService {

	/**
	 * @param processInstanceId
	 * @return
	 */
	List<Map<String, Object>> traceProcess(String processInstanceId);

}
