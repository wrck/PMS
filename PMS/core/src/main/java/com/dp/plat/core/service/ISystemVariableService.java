/**
 * 
 */
package com.dp.plat.core.service;

import java.util.HashMap;

import com.dp.plat.core.pojo.SystemVariable;

/**
 * @author w02611
 *
 */
public interface ISystemVariableService extends IAbstractBaseService<SystemVariable>{

	/**
	 * 查询所有系统变量
	 */
	HashMap<String, String> querySystemVariables();

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
