package com.dp.plat.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.dao.UserInfoMapper;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.vo.UserInfoVO;

@Service("userInfoService")
public class UserInfoService extends AbstractBaseService<UserInfoMapper, UserInfo> implements IUserInfoService {

	@Resource
	private UserInfoMapper userInfoDao;

	@Override
	public int updateByPrimaryKeySelective(UserInfo userInfo) {
		return userInfoDao.updateByPrimaryKeySelective(userInfo);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return userInfoDao.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(UserInfo record) {
		return userInfoDao.insert(record);
	}

	@Override
	public int insertSelective(UserInfo record) {
		return userInfoDao.insertSelective(record);
	}

	@Override
	public UserInfo selectByPrimaryKey(Integer id) {
		return userInfoDao.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKey(UserInfo record) {
		return userInfoDao.updateByPrimaryKey(record);
	}

	@Override
	public UserInfo selectByUserId(Integer userId) {
		return userInfoDao.selectByUserId(userId);
	}

	@Override
	public UserInfoVO selectOneByUserId(Integer id) {
		return userInfoDao.selectOneByUserId(id);
	}

	@Override
	public void updateByUserId(UserInfo userInfo) {
		userInfoDao.updateByUserId(userInfo);
	}

	@Override
	public void deleteByUserId(Integer userId) {
		userInfoDao.deleteByUserId(userId);
	}

	@Override
	public List<UserInfoVO> selectVOsByUserId(Integer id) {
		return userInfoDao.selectVOsByUserId(id);
	}

	@Override
	public List<UserInfo> selectBySelective(UserInfo userInfo) {
		return userInfoDao.selectBySelective(userInfo);
	}

	@Override
	public UserInfoVO selectOneByUserIdAndCompId(UserInfo userInfo) {
		return userInfoDao.selectOneByUserIdAndCompId(userInfo);
	}

	@Override
	public UserInfoVO selectOneByUserNameAndCompId(String userName) {
		return this.selectOneByUserNameAndCompId(userName, UserContext.getOrgId());
	}

	@Override
	public UserInfoVO selectOneByUserNameAndCompId(String userName, Integer compID) {
		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setUserName(userName);
		userInfo.setCompID(compID);
		return userInfoDao.selectOneByUserNameAndCompId(userInfo);
	}
	
}
