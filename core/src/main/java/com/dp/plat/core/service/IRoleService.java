/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.vo.RoleParam;

/**
 * @author w02611
 *
 */
public interface IRoleService {
	int deleteByPrimaryKey(Integer roleId);

	int insert(Role role);

	int insertSelective(Role role);

	Role selectByPrimaryKey(Integer roleId);

	int updateByPrimaryKeySelective(Role role);

	int updateByPrimaryKey(Role role);

	// 以下自定义查询方法
	long countBySelective(RoleParam pageParam);

	List<Role> selectAllRole();

	/**
	 * 根据roleMenu参数，以及pageParam参数，有选择的查询所有角色信息
	 * 
	 * @param role
	 * @param pageParam
	 * @return roleList
	 */
	List<Role> selectBySelective(Role role, RoleParam pageParam);

	/**
	 * @param pageParam
	 * @return roleList
	 */
	List<Role> selectBySelective(RoleParam pageParam);

	/**
	 * @param roleNames
	 * @return roleList
	 */
	List<Role> selectRolesByRoleNames(String roleName);

}
