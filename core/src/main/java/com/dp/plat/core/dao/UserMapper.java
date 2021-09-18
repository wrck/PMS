package com.dp.plat.core.dao;

import java.util.HashMap;
import java.util.List;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

public interface UserMapper {

    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /** 自定义方法 */
    /**
	 * 根据用户名称查询用户信息
	 * 
	 * @param username
	 * @return
	 */
    User selectByUserName(String username);

    /**
	 * 查询系统所有可用用户
	 * 
	 * @return
	 */
    List<User> selectAllUser();

    /**
	 * 根据用户名称查询菜单权限
	 * 
	 * @param username
	 * @return
	 */
    List<Menu> queryUserMenuByUsername(String username);

    /**
	 * 根据用户名有选择的更新用户登陆信息
	 * 
	 * @param user
	 */
    void updateLoginInfoByUserName(User user);

    long countBySelective(PageParam<UserDetail> record);

    /**
	 * @param pageParam
	 * @return
	 */
    List<UserDetail> selectBySelective(PageParam<UserDetail> pageParam);

    void updateByUsername(User user);

    /**
	 * 更新密码错误次数
	 * 
	 * @param username
	 */
    void updateUserErrorCount(String username);

    /**
	 * 检查用户名唯一性
	 * 
	 * @param userName
	 * @return true/false
	 */
    boolean checkUniqueUserName(String userName);

	/**
	 * 插入新纪录或更新重复记录
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
	 * 根据用户名称,compId查询用户菜单
	 * @param params
	 * @return
	 */
	List<Menu> queryUserMenuByUserIdAndCompId(UserInfo userInfo);

	/**
	 * 根据userId,compId 查询所属角色的主页
	 * @param userInfo
	 * @return
	 */
	String queryMaxRoleHomePageByUserIdAndCompId(UserInfo userInfo);
	
	List<UserDetail> findUserByParam(HashMap<String, String> param);
}
