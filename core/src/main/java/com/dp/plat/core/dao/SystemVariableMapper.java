package com.dp.plat.core.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.pojo.SystemVariable;

public interface SystemVariableMapper extends AbstractBaseMapper<SystemVariable> {

	/**
	 * 查询系统变量
	 * @return
	 */
	List<Map<String, String>> querySystemVariables();

	/**
	 * @param id
	 * @return
	 */
	SystemVariable selectById(Integer id);

	/**
	 * @param id
	 */
	void deleteById(Integer id);

}