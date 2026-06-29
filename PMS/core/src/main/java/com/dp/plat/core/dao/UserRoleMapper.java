package com.dp.plat.core.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserRoleInfo;

public interface UserRoleMapper extends AbstractBaseMapper<UserRole> {

    int deleteByPrimaryKey(Integer id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    /** 自定义方法 */
    /**
	 * 查询用户角色集合
	 * 
	 * @param principal
	 * @return
	 */
    List<String> queryUserRoleByName(String principal);

    /**
	 * 查询用户权限集合
	 * 
	 * @param principal
	 * @return
	 */
    List<String> queryPermissionByUsername(String principal);

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
	 * 批量解绑用户和角色关系
	 * @param del
	 */
    void batchDeleteUserRoleByUserRole(List<UserRole> del);

	/**
	 * /**
	 * 根据UserId,compId查用户角色
	 * @param userRole
	 * @return
	 */
	String selectUserRolesByUserIdAndCompId(UserRole userRole);

	/**
	 * @param params
	 * @return
	 */
	List<String> queryUserRoleByNameAndCompId(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	List<String> queryPermissionByUsernameAndCompId(Map<String, Object> params);
}
