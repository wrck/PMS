package com.dp.plat.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.dao.UserManageDao;
import com.dp.plat.data.bean.MenuForUser;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.util.UserUtil;

public class UserManageServiceImpl extends BaseServiceImpl implements UserManageService {
	private UserManageDao userManageDao;

	@Override
	public List<User> queryUserList(DisplayParam displayParam, User user) {
		log("查看用户信息");
		return userManageDao.queryUserList(displayParam, user);
	}

	public UserManageDao getUserManageDao() {
		return userManageDao;
	}

	public void setUserManageDao(UserManageDao userManageDao) {
		this.userManageDao = userManageDao;
	}

	@Override
	public User queryUserByUserName(String username) {
		return userManageDao.queryUserByUserName(username);
	}

	@Override
	public List<User> queryUsersByUserNames(String usernames) {
		return userManageDao.queryUsersByUserNames(usernames);
	}

	@Override
	public void updatepwdbyusername(String md5pwd, String username) {
		userManageDao.updatepwdbyusername(md5pwd, username);
	}

	@Override
	public void updatepwdbyuser(User user) {
		userManageDao.updatepwdbyuser(user);
	}

	@Override
	public List<Role> queryRolelist() {
		return userManageDao.queryRolelist();
	}

	@Override
	public List<UserMenu> queryUserMenuList() {
		return userManageDao.queryUserMenuList();
	}

	@Override
	public User queryUserByUserId(int id) {
		return userManageDao.queryUserByUserId(id);
	}

	@Override
	public String queryUserMenuidsByUserid(int id) {
		return userManageDao.queryUserMenuidsByUserid(id);
	}

	@Override
	public void addUserInfo(User user, String usermenuids) {
		log("增加用户信息");
		if (user.getRoleids() != null && !"".equals(user.getRoleids())) {
			user.setRoleids(dealWith(user.getRoleids()));
		}
		userManageDao.addUserInfo(user, usermenuids);
	}

	@Override
	public void updateUserInfo(User user, String usermenuids) {
		log("修改用户信息");
		UserMenu userMenuForDefaultPage = userManageDao.queryUserMenu(Integer.parseInt(user.getDefaultPage()));
		if (userMenuForDefaultPage == null || userMenuForDefaultPage.getPath() == null
				|| userMenuForDefaultPage.getPath().equals("")) {
			throw new RuntimeException("获取默认登录页面出错");
		} else {
			user.setDefaultPage(userMenuForDefaultPage.getPath());
		}
		String roleids = user.getRoleids();
		if (roleids != null && !"".equals(roleids) && !roleids.startsWith(";")) {
			user.setRoleids(dealWith(roleids));
		}
		userManageDao.updateUser(user);
		String usermenuidArr[] = usermenuids.split(",");
		int userId = user.getId();
		userManageDao.deleteUsermenu(userId);
		for (int i = 0; i < usermenuidArr.length; i++) {
			MenuForUser menuForUser = new MenuForUser();
			menuForUser.setSys_user_id(userId);
			menuForUser.setUsername(user.getUsername());
			UserMenu userMenu = userManageDao.queryUserMenu(Integer.parseInt(usermenuidArr[i]));
			if (userMenu != null) {
				menuForUser.setMenuCode(userMenu.getMenuCode());
			}
			userManageDao.insertUsermenu(menuForUser);
		}
		User u = userManageDao.queryUserByUserId(userId);
		String areapower = StringUtils.trimToEmpty(user.getAreapower());
		areapower += "," + u.getDpNo();
		areapower = UserUtil.processAreaPower(areapower);
		if (u.getAreapower() != null) {
			userManageDao.updateUserPower(userId, areapower, user.getUsername());
		} else {
			userManageDao.insertUserpower(userId, areapower, user.getUsername());
		}

	}

	private String dealWith(String roleids) {
		String[] roles = roleids.split(",");
		StringBuilder sb = new StringBuilder();
		for (String role : roles) {
			sb.append(";");
			sb.append(role);
			sb.append(";");
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public List<User> queryAllUser() {
		return userManageDao.queryAllUser();
	}

	@Override
	public List<User> queryAllUserList(User user) {
		return userManageDao.queryAllUserList(user);
	}

	@Override
	public Map<String, User> queryAllUserMap() {
		return userManageDao.queryAllUserMap();
	}

	@Override
	public int queryUserSizeByUserName(String username) {
		return userManageDao.queryUserSizeByUserName(username);
	}

	@Override
	public List<User> queryUserWithRoleId(int roleid) {
		return userManageDao.queryUserWithRoleId(roleid);
	}

	@Override
	public String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb) {
		return userManageDao.updateServiceAndProgramMember(batchCgMb);
	}

	@Override
	public List<User> queryUserWithRoleIdAndDpNo(Map<String, String> map) {
		return userManageDao.queryUserWithRoleIdAndDpNo(map);
	}

	@Override
	public List<User> queryUserWithRoleIdAndDpNoOrInAreaPower(Map<String, String> params) {
		return userManageDao.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
	}

	@Override
	public String queryMailsByRoleAndOfficeCodes(String officeCodes, Integer roleId) {
		return userManageDao.queryMailsByRoleAndOfficeCodes(officeCodes, roleId);
	}

}
