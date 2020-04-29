package com.dp.plat.pms.springmvc.service.impl;

import org.activiti.engine.IdentityService;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.UserInfoMapper;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;

@Service("pmUserService")
public class ProjectManageUserService extends AbstractBaseService<UserInfoMapper, UserInfo>
		implements IProjectManageUserService {

	@Autowired
	private IdentityService identityService;

	@Override
	public void insertOrUpdateActivitiUser(UserEntity userEntity) {
		identityService.createNativeUserQuery().sql("INSERT INTO ACT_ID_USER (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_) "
				+ "VALUES ( #{user.id}, 1, #{user.firstName}, #{user.lastName}, #{user.email}, #{user.password}) "
				+ "ON DUPLICATE KEY UPDATE FIRST_ = VALUES(FIRST_), LAST_ = VALUES(LAST_), EMAIL_ = VALUES(EMAIL_), PWD_ = VALUES(PWD_)")
				.parameter("user", userEntity).singleResult();
	}

}
