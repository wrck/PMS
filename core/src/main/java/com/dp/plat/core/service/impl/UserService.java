package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.dao.UserMapper;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

@Service("userService")
public class UserService implements IUserService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());;

	@Resource
	private UserMapper userDao;

	@Override
	public long countBySelective(PageParam<UserDetail> record) {
		return userDao.countBySelective(record);
	}

	@Override
	public List<User> selectAllUser() {
		logger.info("查询系统所有用户");
		return userDao.selectAllUser();
	}

	@Override
	public void updateByPrimaryKeySelective(User user) {
		userDao.updateByPrimaryKeySelective(user);
	}

	@Override
	public void updateLoginInfoByUserName(User user) {
		userDao.updateLoginInfoByUserName(user);
	}

	@Override
	public List<UserDetail> selectBySelective(PageParam<UserDetail> pageParam) {
		return userDao.selectBySelective(pageParam);
	}

	@Override
	public int deleteByPrimaryKey(Integer userId) {
		return userDao.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(User record) {
		return userDao.insert(record);
	}

	@Override
	public int insertSelective(User record) {
		return userDao.insertSelective(record);
	}

	@Override
	public User selectByPrimaryKey(Integer userId) {
		return userDao.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKey(User record) {
		return userDao.updateByPrimaryKey(record);
	}

	@Override
	public void updateByUsername(User user) {
		userDao.updateByUsername(user);
	}

	@Override
	public User selectByUserName(String username) {
		return userDao.selectByUserName(username);
	}

	@Override
	public void updateUserErrorCount(String username) {
		userDao.updateUserErrorCount(username);
	}

	@Override
	public boolean checkUniqueUserName(String userName) {
		return userDao.checkUniqueUserName(userName);
	}

	@Override
	public void insertOrUpdateSelective(User user) {
		userDao.insertOrUpdateSelective(user);
	}

	@Override
	public String queryMaxRoleHomePageByUserId(Integer userId) {
		return userDao.queryMaxRoleHomePageByUserId(userId);
	}

	@Override
	public String queryMaxRoleHomePageByUserIdAndCompId(UserInfo userInfo) {
		return userDao.queryMaxRoleHomePageByUserIdAndCompId(userInfo);
	}

	@Override
	public List<UserDetail> findUserByParam(Map<String, String[]> parameterMap) {
		HashMap<String, String> param = new HashMap<>(parameterMap.size());
		for (Entry<String, String[]> entity : parameterMap.entrySet()) {
			String key = entity.getKey();
			String[] value = entity.getValue();
			if (value != null) {
				param.put(key, value[0]);
			}
		}
		if (!param.containsKey("compID")) {
			param.put("compID", String.valueOf(UserContext.getOrgId()));
		}
		return userDao.findUserByParam(param);
	}

}
