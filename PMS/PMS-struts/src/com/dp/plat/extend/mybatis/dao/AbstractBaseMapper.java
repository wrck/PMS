package com.dp.plat.extend.mybatis.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Qualifier("commonAbstractBaseMapper")
@Repository("commonAbstractBaseMapper")
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