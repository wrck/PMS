package com.dp.plat.pms.springmvc.service;

import org.activiti.engine.impl.persistence.entity.UserEntity;

import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IAbstractBaseService;

public interface IProjectManageUserService extends IAbstractBaseService<UserInfo> {

	void insertOrUpdateActivitiUser(UserEntity userEntity);

	void initActivitiUser();

}
