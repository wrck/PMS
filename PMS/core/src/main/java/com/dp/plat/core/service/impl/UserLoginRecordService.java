package com.dp.plat.core.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.UserLoginRecordMapper;
import com.dp.plat.core.pojo.UserLoginRecord;
import com.dp.plat.core.service.IUserLoginRecordService;

@Service("userLoginRecordService")
public class UserLoginRecordService extends AbstractBaseService<UserLoginRecordMapper, UserLoginRecord>
		implements IUserLoginRecordService {

}
