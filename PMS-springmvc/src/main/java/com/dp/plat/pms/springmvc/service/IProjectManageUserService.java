package com.dp.plat.pms.springmvc.service;

import java.util.List;

import org.activiti.engine.impl.persistence.entity.UserEntity;

import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;

public interface IProjectManageUserService extends IUserService {

	void insertOrUpdateActivitiUser(UserEntity userEntity);

	void initActivitiUser();

	long countBySelectivePageable(PageParam<UserDetail> pageParam);

	long countBySelective(UserDetail t);

	List<UserDetail> selectBySelectivePageable(PageParam<UserDetail> pageParam);

	List<UserDetail> selectBySelective(UserDetail record);

	List<UserInfo> selectBySelective(UserInfo ui);

}
