package com.dp.plat.core.dao;

import java.util.Map;

import com.dp.plat.core.pojo.Resource;

public interface ResourceMapper extends AbstractBaseMapper<Resource> {

	/**
	 * 更新资源的优先级排序
	 * @param params
	 */
	void updatePriorities(Map<String, Object> params);

}