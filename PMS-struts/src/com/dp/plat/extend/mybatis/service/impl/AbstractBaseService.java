package com.dp.plat.extend.mybatis.service.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.plat.pms.extend.d365.dao.AbstractBaseMapper;
import com.dp.plat.pms.extend.d365.service.IAbstractBaseService;

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
			method.invoke(record, getCurrentUsername());
		} catch (Exception e) {
		}
		return dao.insert(record);
	}

	@Override
	public int insertSelective(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, getCurrentUsername());
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
			method.invoke(record, getCurrentUsername());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKeySelective(T record) {
		try {
			Class<?> objClass = record.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, getCurrentUsername());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKeySelective(record);
	}

	public long countBySelective(T t) {
		return dao.countBySelective(t);
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

	/**
	 * 反射获取用户上下文
	 * @return
	 */
	protected String getCurrentUsername() {
		try {
			Class<?> UserContext = Class.forName("com.dp.plat.core.context.UserContext");
			try {
    			Method getCurrentUsername = UserContext.getMethod("getCurrentUsername");
    			return (String) getCurrentUsername.invoke(null);
			} catch (Exception e) {
			    Method getCurrentUsername = UserContext.getMethod("getUsername");
                return (String) getCurrentUsername.invoke(null);
            }
		} catch (Throwable e) {
		}
		return null;
	}
}
