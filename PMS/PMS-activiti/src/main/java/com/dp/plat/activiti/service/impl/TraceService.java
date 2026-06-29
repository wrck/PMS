/**
 * 
 */
package com.dp.plat.activiti.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dp.plat.activiti.service.ITraceService;

/**
 * @author w02611
 *
 */
@Service("traceService")
public class TraceService implements ITraceService{

	/**
	 * @param processInstanceId
	 * @return
	 */
	public List<Map<String, Object>> traceProcess(String processInstanceId) {
		return null;
	}

}
