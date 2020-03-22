package com.dp.plat.core.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

/**
 * 用户业务管理
 * 
 * @author j01441
 *
 */
public interface IUserService {

	int deleteByPrimaryKey(Integer userId);

	int insert(User record);

	int insertSelective(User record);

	User selectByPrimaryKey(Integer userId);

	int updateByPrimaryKey(User record);

	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	List<User> selectAllUser();

	/**
	 * 根据主键有选择的更新用户
	 * 
	 * @param user
	 */
	void updateByPrimaryKeySelective(User user);

	/**
	 * 根据用户名有选择的更新用户登陆信息
	 * 
	 * @param user
	 */
	void updateLoginInfoByUserName(User user);

	long countBySelective(PageParam<UserDetail> pageParam);

	/**
	 * @param pageParam
	 * @return
	 */
	List<UserDetail> selectBySelective(PageParam<UserDetail> pageParam);
	
	void updateByUsername(User user);
	
	User selectByUserName(String username);

	/**
	 * 用户密码错误，错误次数加一
	 * @param username
	 */
	void updateUserErrorCount(String username);

	/**
	 * 检查用户名唯一性
	 * @param userName
	 * @return
	 */
	boolean checkUniqueUserName(String userName);

	/**
	 * 插入新纪录或更新重复的记录
	 * @param user
	 */
	void insertOrUpdateSelective(User user);

	/**
	 * 根据userId 查询所属角色的主页
	 * @param userId
	 * @return homePage
	 */
	String queryMaxRoleHomePageByUserId(Integer userId);

	/**
	 *  根据userId ,compId查询所属角色的主页
	 * @param userInfo
	 * @return
	 */
	String queryMaxRoleHomePageByUserIdAndCompId(UserInfo userInfo);

	List<UserDetail> findUserByParam(Map<String, String[]> parameterMap);

}
