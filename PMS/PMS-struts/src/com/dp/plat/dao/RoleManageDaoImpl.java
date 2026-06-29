package com.dp.plat.dao;

import java.sql.SQLException;
import java.util.List;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.RoleParam;

public class RoleManageDaoImpl extends BaseDao implements RoleManageDao{
	@SuppressWarnings("unchecked")
	public List<Role> queryRoleList(DisplayParam displayParam,Role role) { 
		displayParam.setPagesize(50);
		displayParam.setOffset((displayParam.getCurrentpage() - 1)
				* displayParam.getPagesize());
		Integer totalcount = (Integer) getSqlMapClientTemplate().
				queryForObject("query-role-count",role);
		displayParam.setTotalcount(totalcount);
		RoleParam roleParam  = new RoleParam();
		roleParam.setDisplayParam(displayParam);
		roleParam.setRole(role);
		return (List<Role>) getSqlMapClientTemplate().queryForList(
				"query-rolelist", roleParam);
	}
	
	@SuppressWarnings("unchecked")
	public List<RoleMenuPower>queryRoleMenuPowerList(Role role){
		return (List<RoleMenuPower>)getSqlMapClientTemplate().queryForList("query-roleMenu-list",role);
	}
	
	public int addRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList){
		int roleId=0;
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();
			
		roleId= (Integer)getSqlMapClientTemplate().insert("insert-roleObject",role);
		for(RoleMenuPower roleMenuPower:rolemenuidList){
			if(roleMenuPower==null||roleMenuPower.getMenuId()==0||roleMenuPower.getMenuPower()==null||roleMenuPower.getMenuPower().equals("")){
				throw new RuntimeException("角色菜单权限信息错误");
			}
			roleMenuPower.setRoleId(roleId);
			getSqlMapClientTemplate().insert("insert-roleMenuPower-object",roleMenuPower);
		}
		
		getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return roleId;
	}
	
	public int updateRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList){
		int updateRow=0;
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();
			
			updateRow= (Integer)getSqlMapClientTemplate().update("update-roleObject",role);
			getSqlMapClientTemplate().delete("delete-roleMenuPower-byRoleId",role);
			for(RoleMenuPower roleMenuPower:rolemenuidList){
				if(roleMenuPower==null||roleMenuPower.getMenuId()==0||roleMenuPower.getMenuPower()==null||roleMenuPower.getMenuPower().equals("")){
					throw new RuntimeException("角色菜单权限信息错误");
				}
				roleMenuPower.setRoleId(role.getId());
				getSqlMapClientTemplate().insert("insert-roleMenuPower-object",roleMenuPower);
			}
		
		getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return updateRow;
	}
	
}
