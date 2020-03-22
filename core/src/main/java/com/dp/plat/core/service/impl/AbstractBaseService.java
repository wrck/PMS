/**
 * 
 */
package com.dp.plat.core.service.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 * @author w02611
 *
 */
public abstract class AbstractBaseService<Mapper extends AbstractBaseMapper<T>, T> implements IAbstractBaseService<T> {

	@Autowired
	protected Mapper dao;

	@Override
	public int deleteByPrimaryKey(Object pk) {
		return dao.deleteByPrimaryKey(pk);
	}

	@Override
	public int insert(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insert(record);
	}

	@Override
	public int insertSelective(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insertSelective(record);
	}

	@Override
	public T selectByPrimaryKey(Object pk) {
		return dao.selectByPrimaryKey(pk);
	}

	@Override
	public int updateByPrimaryKey(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKeySelective(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKeySelective(record);
	}

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public long countBySelectivePageable(PageParam<Object> pageParam) {
		return dao.countBySelectivePageable(pageParam);
	}

	public long countBySelective(T t) {
		return dao.countBySelective(t);
	}

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public List<Object> selectBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectBySelectivePageable(pageParam);
	}

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param record
	 * @return
	 */
	public List<T> selectBySelective(T record) {
		return dao.selectBySelective(record);
	}

}
