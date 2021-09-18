package com.dp.plat.core.dao;

import java.util.List;

import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.vo.RoleParam;

public interface RoleMapper extends AbstractBaseMapper<Role>{

	int deleteByPrimaryKey(Integer roleId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

	/**
	 * @param pageParam
	 * @return
	 */
	long countBySelective(RoleParam pageParam);

	/**
	 * @return
	 */
	List<Role> selectAllRole();

	/**
	 * @param role
	 * @param pageParam
	 * @return
	 */
	List<Role> selectBySelective(Role role, RoleParam pageParam);

	/**
	 * @param pageParam
	 * @return
	 */
	List<Role> selectBySelective(RoleParam pageParam);

	/**
	 * @param roleNames
	 * @return roleList
	 */
	List<Role> selectRolesByRoleNames(String roleNames);
	
	/**
	 * @param roleName
	 * @return role
	 */
	Role selectRoleByRoleName(String roleName);
}