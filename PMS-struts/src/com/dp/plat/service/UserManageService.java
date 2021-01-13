package com.dp.plat.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectBatchCgMbParam;

public interface UserManageService {
	/**
	 * 查询用户列表数据
	 * @param displayParam
	 * @param user user.username user.roleName
	 * @return
	 */
	List<User> queryUserList(DisplayParam displayParam,User user);
	/**
	 * 根据用户名查询用户信息
	 * @param username
	 * @return
	 */
	User queryUserByUserName(String username);
	/**
	 * 根据用户名查询用户信息
	 * @param username
	 * @return
	 */
	List<User> queryUsersByUserNames(String username);
	/**
	 * 根据用户名更新用户密码
	 * @param md5pwd
	 * @param username
	 */
	void updatepwdbyusername(String md5pwd,String username);
	/**
	 * 初始化用户密码为1q2w3e4r
	 * @param user
	 */
	void updatepwdbyuser(User user);
	/**
	 * 查询全部用户角色
	 * @return
	 */
	List<Role> queryRolelist();
	/**
	 * 查询系统菜单
	 * @return
	 */
	List<UserMenu> queryUserMenuList();
	/**
	 * 根据用户id获取用户信息
	 * @param id
	 * @return
	 */
	User queryUserByUserId(int id);
	/**
	 * 根据用户id查询具有的菜单id
	 * @param id
	 * @return
	 */
	String queryUserMenuidsByUserid(int id);
	/**
	 * 增加用户信息
	 * @param user
	 * @param usermenuids
	 */
	void addUserInfo(User user, String usermenuids);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @param usermenuids
	 */
	void updateUserInfo(User user, String usermenuids);
	
	/**
	 * 查询所有系统用户 
	 * @return
	 */
	List<User> queryAllUser();
	
	/**
	 *查询所有用户集合
	 * @param user
	 * @return
	 */
	List<User> queryAllUserList(User user);
	
	/**
	 * 查询所有用户，并封装为Map，key为用户名
	 * @return
	 */
	Map<String,User> queryAllUserMap();
	/**
	 * 查询用户名是否存在
	 * @param username
	 * @return
	 */
	int queryUserSizeByUserName(String username);
	/**
	 * 查询有这个角色的用户
	 * @param roleid
	 * @return
	 */
	List<User> queryUserWithRoleId(int roleid);
	/**
	 * 更新项目服务经理、项目经理或者两者都更新
	 * @param user
	 * @param changeType 
	 * @param newMemberCode 
	 */
	String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb);
	/**
	 * 查询部门中有这个角色的用户
	 * @param params
	 * @return
	 */
	List<User> queryUserWithRoleIdAndDpNo(Map<String, String> params);
	/**
	 * 查询某部门有某个角色的用户,或者某角色并且有某部门的权限的用户
	 * @param params
	 * @return list<user>
	 */
	List<User> queryUserWithRoleIdAndDpNoOrInAreaPower(Map<String, String> params); 
	
	/**
	 * 查询办事处某类角色的邮件地址
	 * @param officeCodes
	 * @param roleId
	 * @return
	 */
	String queryMailsByRoleAndOfficeCodes(String officeCodes, Integer roleId);
}
