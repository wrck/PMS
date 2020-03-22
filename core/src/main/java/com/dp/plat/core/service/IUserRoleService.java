/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserRoleInfo;

/**
 * @author w02611
 *
 */
public interface IUserRoleService extends IAbstractBaseService<UserRole> {

	/**
	 * 查询绑定某个角色的记录数
	 * 
	 * @param pageParam
	 * @return
	 */
	long countUserRoleSelected(PageParam<UserRoleInfo> pageParam);

	/**
	 * 查询绑定某个角色的用户列表
	 * 
	 * @param pageParam
	 * @return
	 */
	List<User> selectUserRoleSelected(PageParam<UserRoleInfo> pageParam);

	/**
	 * 查询没有绑定某个角色的记录数
	 * 
	 * @param pageParam
	 * @return
	 */
	long countUserRoleUnselected(PageParam<UserRoleInfo> pageParam);

	/**
	 * 查询没有绑定某个用户的用户列表
	 * 
	 * @param pageParam
	 * @return
	 */
	List<User> selectUserRoleUnselected(PageParam<UserRoleInfo> pageParam);

	/**
	 * 批量绑定用户和角色关系
	 * 
	 * @param userRoleList
	 */
	void batchInsertUserRole(List<UserRole> userRoleList);

	/**
	 * 批量解绑用户和角色关系
	 * 
	 * @param ids
	 */
	void batchDeleteUserRole(List<Integer> ids);

	/**
	 * 根据UserId查用户角色
	 * @param userId
	 * @return roleIds
	 */
	String selectUserRolesByUserId(Integer userId);
	/**
	 * 根据UserId，compId查用户角色
	 * @param userRole
	 * 
	 * @return roleIds
	 */
	String selectUserRolesByUserIdAndCompId(UserRole userRole);

	/**
	 * 批量解绑用户和角色关系
	 * @param del
	 */
	void batchDeleteUserRoleByUserRole(List<UserRole> del);

}
