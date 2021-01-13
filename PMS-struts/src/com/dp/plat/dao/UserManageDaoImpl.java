package com.dp.plat.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.MenuForUser;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.Md5Param;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.param.UserParam;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.UserUtil;

public class UserManageDaoImpl extends BaseDao implements UserManageDao {

	@SuppressWarnings("unchecked")
	public List<User> queryUserList(DisplayParam displayParam, User user) {
		if (user.getRoleids() != null && !"".equals(user.getRoleids())) {
			user.setRoleids(";" + user.getRoleids() + ";");
		}
		displayParam.setPagesize(50);
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("query-user-count", user);
		displayParam.setTotalcount(totalcount);
		UserParam userParam = new UserParam();
		userParam.setDisplayParam(displayParam);
		userParam.setUser(user);

		return (List<User>) getSqlMapClientTemplate().queryForList("query-userlist", userParam);
	}

	@Override
	public void updateuser(User user) {
		getSqlMapClientTemplate().update("update-user", user);
	}

	@Override
	public User queryUserByUserName(String username) {
		return (User) getSqlMapClientTemplate().queryForObject("select-user-byusername", username);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> queryUsersByUserNames(String usernames) {
		return getSqlMapClientTemplate().queryForList("select-users-byusernames", usernames);
	}
	
	@Override
	public void updatepwdbyusername(String md5pwd, String username) {
		Md5Param mp = new Md5Param();
		mp.setMd5pwd(md5pwd);
		mp.setUsername(username);
		getSqlMapClientTemplate().insert("update-md5pwd-byusername", mp);
	}

	@Override
	public void updatepwdbyuser(User user) {
		getSqlMapClientTemplate().update("update-pwd-byusername", user.getUsername());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Role> queryRolelist() {
		List<Role> roleList = getSqlMapClientTemplate().queryForList("query_sys_roles");
		for (Iterator<Role> iterator = roleList.iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			if (!UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN)
					&& (role.getId() == MessageUtil.ROLE_ADMIN || role.getId() == MessageUtil.ROLE_PROB_ADMIN)) {
				iterator.remove();
			}
		}
		return roleList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserMenu> queryUserMenuList() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("superId", 0);
		List<UserMenu> list = getSqlMapClientTemplate().queryForList("query_menu_modules", map);
		/*
		 * if(list != null ){ for(UserMenu userMenu : list){ map.put("superId",
		 * userMenu.getId());
		 * List<UserMenu>subList=getSqlMapClientTemplate().queryForList(
		 * "query_menu_modules", map); userMenu.setUserMenuList(subList); } }
		 */
		queryUserMenuList2(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public void queryUserMenuList2(List<UserMenu> list) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (list != null && list.size() > 0) {
			for (Iterator<UserMenu> iterator = list.iterator(); iterator.hasNext();) {
				UserMenu userMenu = (UserMenu) iterator.next();
				if (!UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN)
						&& userMenu.getSuperId() == 0 && !"sys.leftmenu.powermanage".equals(userMenu.getMenuCode())) {
					iterator.remove();
					continue;
				}
				map.put("superId", userMenu.getId());
				List<UserMenu> subList = getSqlMapClientTemplate().queryForList("query_menu_modules", map);
				userMenu.setUserMenuList(subList);
				queryUserMenuList2(subList);
			}
		}
		return;
	}

	@Override
	public User queryUserByUserId(int id) {
		return (User) getSqlMapClientTemplate().queryForObject("query_user_by_id", id);
	}

	@Override
	public String queryUserMenuidsByUserid(int id) {
		return (String) getSqlMapClientTemplate().queryForObject("query_usermenuids_by_id", id);
	}

	@Override
	public void addUserInfo(User user, String usermenuids) {
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();

			UserMenu userMenuForDefaultPage = (UserMenu) getSqlMapClientTemplate().queryForObject("query-menu-byId",
					Integer.parseInt(user.getDefaultPage()));
			if (userMenuForDefaultPage == null || userMenuForDefaultPage.getPath() == null
					|| userMenuForDefaultPage.getPath().equals("")) {
				throw new RuntimeException("获取默认登录页面出错");
			} else {
				user.setDefaultPage(userMenuForDefaultPage.getPath());
			}
			user.setCreateBy(UserContext.getUserContext().getUsername());
			int userId = (Integer) getSqlMapClientTemplate().insert("insert-user-object", user);
			String usermenuidArr[] = usermenuids.split(",");
			for (int i = 0; i < usermenuidArr.length; i++) {
				MenuForUser menuForUser = new MenuForUser();
				menuForUser.setSys_user_id(userId);
				menuForUser.setUsername(user.getUsername());
				UserMenu userMenu = (UserMenu) getSqlMapClientTemplate().queryForObject("query-menu-byId",
						Integer.parseInt(usermenuidArr[i]));
				menuForUser.setMenuCode(userMenu.getMenuCode());
				getSqlMapClientTemplate().insert("insert-menuForUser-object", menuForUser);
			}
			String areapower = StringUtils.trimToEmpty(user.getAreapower());
			areapower += "," + user.getDpNo();
			areapower = UserUtil.processAreaPower(areapower);
			this.insertUserpower(userId, areapower, user.getUsername());

			getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void updateUserInfo(User user, String usermenuids) {
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();

			UserMenu userMenuForDefaultPage = (UserMenu) getSqlMapClientTemplate().queryForObject("query-menu-byId",
					Integer.parseInt(user.getDefaultPage()));
			if (userMenuForDefaultPage == null || userMenuForDefaultPage.getPath() == null
					|| userMenuForDefaultPage.getPath().equals("")) {
				throw new RuntimeException("获取默认登录页面出错");
			} else {
				user.setDefaultPage(userMenuForDefaultPage.getPath());
			}

			getSqlMapClientTemplate().update("update-user-object", user);
			String usermenuidArr[] = usermenuids.split(",");
			int userId = user.getId();
			getSqlMapClientTemplate().delete("delete-menuForUser-byUserId", userId);
			for (int i = 0; i < usermenuidArr.length; i++) {
				MenuForUser menuForUser = new MenuForUser();
				menuForUser.setSys_user_id(userId);
				menuForUser.setUsername(user.getUsername());
				UserMenu userMenu = (UserMenu) getSqlMapClientTemplate().queryForObject("query-menu-byId",
						Integer.parseInt(usermenuidArr[i]));
				menuForUser.setMenuCode(userMenu.getMenuCode());
				getSqlMapClientTemplate().insert("insert-menuForUser-object", menuForUser);
			}

			getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public UserMenu queryUserMenu(int menuId) {
		return (UserMenu) getSqlMapClientTemplate().queryForObject("query-menu-byId", menuId);
	}

	@Override
	public void updateUser(User user) {
		try {
			user.setUpdateBy(getCurrUsername());
		} catch (Exception e) {
		}
		getSqlMapClientTemplate().update("update-user-object", user);
	}

	@Override
	public void deleteUsermenu(int userId) {
		getSqlMapClientTemplate().delete("delete-menuForUser-byUserId", userId);
	}

	@Override
	public void insertUsermenu(MenuForUser menuForUser) {
		getSqlMapClientTemplate().insert("insert-menuForUser-object", menuForUser);
	}

	@Override
	public void updateUserPower(int userId, String areapower, String username) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("areapower", areapower);
		paramMap.put("username", username);
		paramMap.put("updateBy", UserContext.getUserContext().getUsername());
		getSqlMapClientTemplate().update("update_user_power", paramMap);
	}

	@Override
	public void insertUserpower(int userId, String areapower, String username) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("areapower", areapower);
		paramMap.put("username", username);
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		getSqlMapClientTemplate().insert("insert_user_power", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> queryAllUser() {
		return getSqlMapClientTemplate().queryForList("query-all-user");
	}

	@SuppressWarnings("unchecked")
	public List<User> queryAllUserList(User user) {
		/*
		 * if(user.getRoleids() != null){
		 * user.setRoleids(";"+user.getRoleids()+";"); }
		 */
		return (List<User>) getSqlMapClientTemplate().queryForList("query-userlist-all", user);
	}

	@SuppressWarnings("unchecked")
	public Map<String, User> queryAllUserMap() {
		return (Map<String, User>) getSqlMapClientTemplate().queryForMap("query_user_allMap", null, "username");
	}

	@Override
	public int queryUserSizeByUserName(String username) {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_username_size", username);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> queryUserWithRoleId(int roleid) {
		return getSqlMapClientTemplate().queryForList("query_user_with_role", roleid);
	}

	@Override
	public String queryServiceMails(int roleId) {
		return (String) getSqlMapClientTemplate().queryForObject("query_mails_with_role", roleId);
	}

	@Override
	public String queryServiceMails(String officeCodes, Integer roleId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("officeCodes", officeCodes);
		paramMap.put("roleId", roleId);
		return (String) getSqlMapClientTemplate().queryForObject("query_mails_with_role_and_office", paramMap);
	}
	
	@Override
	public String queryMailsByRoleAndOfficeCodes(String officeCodes, Integer roleId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("officeCodes", officeCodes);
		paramMap.put("roleId", roleId);
		return (String) getSqlMapClientTemplate().queryForObject("query_mails_with_role_and_office", paramMap);
	}

	@Override
	public String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb) {
		HashMap<String, String> params = new HashMap<>();
		params.put("vMemberCode", batchCgMb.getOldMemberCode());
		params.put("vNewMemberCode", batchCgMb.getNewMemberCode());
		params.put("vNewMemberName", batchCgMb.getNewMemberName());
		params.put("vDepartmentName", batchCgMb.getDpName());
		params.put("operateUser", getCurrUsername());
		params.put("changeType", batchCgMb.getChangeType());
		return (String) getSqlMapClientTemplate().queryForObject("UpdateServiceAndProgramMember", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> queryUserWithRoleIdAndDpNo(Map<String, String> params) {
		return getSqlMapClientTemplate().queryForList("query_user_with_dpNo_role", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> queryUserWithRoleIdAndDpNoOrInAreaPower(Map<String, String> params) {
		return getSqlMapClientTemplate().queryForList("query_user_with_dpNo_role_orin_areaPower", params);
	}
}
