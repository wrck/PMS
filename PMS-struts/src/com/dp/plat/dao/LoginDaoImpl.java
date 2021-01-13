package com.dp.plat.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.data.bean.User;

public class LoginDaoImpl extends BaseDao implements LoginDao{

	@Override
	public User querUser(String username) {
		return (User) this.getSqlMapClientTemplate().
				queryForObject("query-user-by-name",username);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> queryUserMenuMap(int userId ) {
		return getSqlMapClientTemplate().queryForMap("query_permissions_by_name", userId, "menuCode" ,"menuValue");
	}

	@Override
	public String queryUserDefaultPage(int userId) {
		Object obj = getSqlMapClientTemplate().queryForObject("query_defaultpage_by_username_1", userId);
		if(obj == null){
			obj = getSqlMapClientTemplate().queryForObject("query_defaultpage_by_username_2", userId);
		}
		return (String)obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<RoleMenuPower>queryRoleMenuPowerList(Role role){
		return (List<RoleMenuPower>)getSqlMapClientTemplate().queryForList("query-roleMenu-list",role);
	}
	
	@Override
	public String querySysArg(String code){
		return (String)getSqlMapClientTemplate().queryForObject("query_sys_arg",code);
	}

}
