package com.dp.plat.pms.extend.d365.service;

import java.util.List;

/**
 * @author w02611
 *
 */
public abstract interface IAbstractBaseService<T> {
	int deleteByPrimaryKey(Object pk);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Object pk);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param t
	 * @return
	 */
	long countBySelective(T t);

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param t
	 * @return
	 */
	List<T> selectBySelective(T t);

}
