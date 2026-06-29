package com.dp.plat.core.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.vo.UserDetail;
import com.dp.plat.core.vo.UserInfoVO;

public interface UserInfoMapper extends AbstractBaseMapper<UserInfo> {

    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    /**
	 * @param id
	 * @return
	 */
    UserInfo selectByUserId(Integer userId);

    /**
	 * @param userInfo
	 * @return
	 */
    int updateByUserId(UserInfo userInfo);

    /**
	 * @param userId
	 */
    void deleteByUserId(Integer userId);

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
	 * @return
	 */
	UserInfoVO selectOneByUserIdAndCompId(UserInfo userInfo);
	
	List<UserDetail> findUserInfoWithParam(Map<String, String[]> parameterMap);

	UserInfoVO selectOneByUserNameAndCompId(UserInfoVO userInfo);
}
