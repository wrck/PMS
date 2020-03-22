package com.dp.plat.core.dao;

import java.util.List;

import com.dp.plat.core.vo.PageParam;

public abstract interface AbstractBaseMapper<T> {
	int deleteByPrimaryKey(Object pk);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Object pk);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	long countBySelectivePageable(PageParam<Object> pageParam);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param t
	 * @return
	 */
	long countBySelective(T t);

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	List<Object> selectBySelectivePageable(PageParam<Object> pageParam);

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param t
	 * @return
	 */
	List<T> selectBySelective(T t);
}