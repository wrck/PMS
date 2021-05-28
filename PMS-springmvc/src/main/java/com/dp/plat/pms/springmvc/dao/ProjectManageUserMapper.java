package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.vo.UserDetail;

public interface ProjectManageUserMapper {
	
	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	long countBySelectivePageable(PageParam<UserDetail> pageParam);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param t
	 * @return
	 */
	long countBySelective(UserDetail t);

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	List<UserDetail> selectBySelectivePageable(PageParam<UserDetail> pageParam);

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param t
	 * @return
	 */
	List<UserDetail> selectBySelective(UserDetail t);
}
