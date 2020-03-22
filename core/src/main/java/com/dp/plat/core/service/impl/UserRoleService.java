package com.dp.plat.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.UserRoleMapper;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserRoleInfo;

@Service("userRoleService")
public class UserRoleService extends AbstractBaseService<UserRoleMapper, UserRole> implements IUserRoleService {

	@Override
	public long countUserRoleSelected(PageParam<UserRoleInfo> pageParam) {
		return dao.countUserRoleSelected(pageParam);
	}

	@Override
	public List<User> selectUserRoleSelected(PageParam<UserRoleInfo> pageParam) {
		return dao.selectUserRoleSelected(pageParam);
	}

	@Override
	public long countUserRoleUnselected(PageParam<UserRoleInfo> pageParam) {
		return dao.countUserRoleUnselected(pageParam);
	}

	@Override
	public List<User> selectUserRoleUnselected(PageParam<UserRoleInfo> pageParam) {
		return dao.selectUserRoleUnselected(pageParam);
	}

	@Override
	public void batchInsertUserRole(List<UserRole> userRoleList) {
		dao.batchInsertUserRole(userRoleList);
	}

	@Override
	public void batchDeleteUserRole(List<Integer> ids) {
		dao.batchDeleteUserRole(ids);
	}

	@Override
	public String selectUserRolesByUserId(Integer userId) {
		return dao.selectUserRolesByUserId(userId);
	}

	@Override
	public String selectUserRolesByUserIdAndCompId(UserRole userRole) {
		return dao.selectUserRolesByUserIdAndCompId(userRole);
	}

	@Override
	public void batchDeleteUserRoleByUserRole(List<UserRole> del) {
		dao.batchDeleteUserRoleByUserRole(del);
	}
}
