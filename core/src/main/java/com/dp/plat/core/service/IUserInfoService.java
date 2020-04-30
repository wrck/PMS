/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.vo.UserInfoVO;

/**
 * @author w02611
 *
 */
public interface IUserInfoService extends IAbstractBaseService<UserInfo> {
	int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(UserInfo record);
    
	/**
	 * @param userInfo
	 * @return 
	 */
	int updateByPrimaryKeySelective(UserInfo userInfo);

	/**
	 * @param id
	 * @return
	 */
	UserInfo selectByUserId(Integer id);

	/**
	 * @param id
	 * @return
	 */
	UserInfoVO selectOneByUserId(Integer id);
	
	/**
	 * @param id
	 * @return
	 */
	List<UserInfoVO> selectVOsByUserId(Integer id);
	
	/**
	 * @param userInfo
	 */
	void updateByUserId(UserInfo userInfo);

	/**
	 * @param id
	 */
	void deleteByUserId(Integer id);

	/**
	 * @param userInfo
	 * @return 
	 */
	List<UserInfo> selectBySelective(UserInfo userInfo);

	/**
	 * @param userInfo
	 * @return
	 */
	UserInfoVO selectOneByUserIdAndCompId(UserInfo userInfo);

	UserInfoVO selectOneByUserNameAndCompId(String userName);

	UserInfoVO selectOneByUserNameAndCompId(String userName, Integer orgId);
}
