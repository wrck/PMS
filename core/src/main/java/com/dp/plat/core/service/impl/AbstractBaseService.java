/**
 * 
 */
package com.dp.plat.core.service.impl;

import java.lang.reflect.Method;
import java.util.Date;
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
		if (record == null) {
			return 0;
		}
		Class<?> objClass = record.getClass();
		try {
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		try {
			Method method = objClass.getMethod("setCreateTime", Date.class);
			method.invoke(record, new Date());
		} catch (Exception e) {
		}
		try {
            Method method = objClass.getMethod("getCompId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setCompId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
            Method method = objClass.getMethod("getOrgId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setOrgId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
			Method method = objClass.getMethod("getEffectiveFrom");
			Object effectiveFrom = method.invoke(record);
			if (effectiveFrom == null) {
				method = objClass.getMethod("setEffectiveFrom", Date.class);
				method.invoke(record, new Date());
			}
		} catch (Exception e) {
		}
		return dao.insert(record);
	}

	@Override
	public int insertSelective(T record) {
		if (record == null) {
			return 0;
		}
		Class<?> objClass = record.getClass();
		try {
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		try {
			Method method = objClass.getMethod("setCreateTime", Date.class);
			method.invoke(record, new Date());
		} catch (Exception e) {
		}
		try {
            Method method = objClass.getMethod("getCompId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setCompId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
            Method method = objClass.getMethod("getOrgId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setOrgId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
			Method method = objClass.getMethod("getEffectiveFrom");
			Object effectiveFrom = method.invoke(record);
			if (effectiveFrom == null) {
				method = objClass.getMethod("setEffectiveFrom", Date.class);
				method.invoke(record, new Date());
			}
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
		if (record == null) {
			return 0;
		}
		Class<?> objClass = record.getClass();
		try {
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		try {
			Method method = objClass.getMethod("setUpdateTime", Date.class);
			method.invoke(record, new Date());
		} catch (Exception e) {
		}
		try {
            Method method = objClass.getMethod("getCompId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setCompId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
            Method method = objClass.getMethod("getOrgId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setOrgId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		return dao.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKeySelective(T record) {
		if (record == null) {
			return 0;
		}
		Class<?> objClass = record.getClass();
		try {
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(record, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		try {
			Method method = objClass.getMethod("setUpdateTime", Date.class);
			method.invoke(record, new Date());
		} catch (Exception e) {
		}
		try {
            Method method = objClass.getMethod("getCompId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setCompId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
        } catch (Exception e) {
        }
		try {
            Method method = objClass.getMethod("getOrgId");
            Object compId = method.invoke(record);
            if (compId == null) {
                method = objClass.getMethod("setOrgId", Integer.class);
                method.invoke(record, UserContext.getOrgId());
            }
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
	public long countBySelectivePageable(PageParam<?> pageParam) {
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
	public List<Object> selectBySelectivePageable(PageParam<?> pageParam) {
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
