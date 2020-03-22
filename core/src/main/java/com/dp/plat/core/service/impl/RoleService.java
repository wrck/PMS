package com.dp.plat.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.RoleMapper;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.vo.RoleParam;

@Service("roleService")
public class RoleService implements IRoleService {

	@Resource
	private RoleMapper roleMapper;

	@Override
	public int updateByPrimaryKeySelective(Role role) {
		return roleMapper.updateByPrimaryKeySelective(role);
	}

	@Override
	public int deleteByPrimaryKey(Integer userId) {
		return roleMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(Role record) {
		return roleMapper.insert(record);
	}

	@Override
	public int insertSelective(Role record) {
		return roleMapper.insertSelective(record);
	}

	public Role selectByPrimaryKey(Integer userId) {
		return roleMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKey(Role record) {
		return roleMapper.updateByPrimaryKey(record);
	}

	@Override
	public long countBySelective(RoleParam pageParam) {
		return roleMapper.countBySelective(pageParam);
	}

	@Override
	public List<Role> selectAllRole() {
		return roleMapper.selectAllRole();
	}

	@Override
	@Deprecated
	public List<Role> selectBySelective(Role role, RoleParam pageParam) {
		return roleMapper.selectBySelective(role, pageParam);
	}

	@Override
	public List<Role> selectBySelective(RoleParam pageParam) {
		return roleMapper.selectBySelective(pageParam);
	}

	@Override
	public List<Role> selectRolesByRoleNames(String roleName) {
		return roleMapper.selectRolesByRoleNames(roleName);
	}

}
