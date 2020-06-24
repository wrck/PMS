package com.dp.plat.pms.springmvc.service.impl;

import org.activiti.engine.IdentityService;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.dao.UserInfoMapper;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;

@Service("projectManageUserService")
public class ProjectManageUserService extends AbstractBaseService<UserInfoMapper, UserInfo>
		implements IProjectManageUserService {

	@Autowired
	private IdentityService identityService;

	@Override
	@Transactional
	public void insertOrUpdateActivitiUser(UserEntity userEntity) {
		// 添加用户
		identityService.createNativeUserQuery().sql("INSERT INTO ACT_ID_USER (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_) "
				+ "VALUES ( #{user.id}, 1, #{user.firstName}, #{user.lastName}, #{user.email}, #{user.password}) "
				+ "ON DUPLICATE KEY UPDATE FIRST_ = VALUES(FIRST_), LAST_ = VALUES(LAST_), EMAIL_ = VALUES(EMAIL_), PWD_ = VALUES(PWD_)")
				.parameter("user", userEntity).singleResult();
		
		// 清空分组对应关系
		identityService.createNativeGroupQuery().sql("DELETE FROM act_id_membership where USER_ID_ = #{user.id}")
			.parameter("user", userEntity).singleResult();
		
		// 添加分组对应关系
		identityService.createNativeGroupQuery().sql("INSERT `act_id_membership` (USER_ID_, GROUP_ID_) " + 
				"SELECT DISTINCT " + 
				"    ui.`id`, g.ID_ " + 
				"FROM " + 
				"    `act_id_group` g " + 
				"    INNER JOIN t_role r " + 
				"    ON r.role_name = g.ID_ " + 
				"    INNER JOIN `t_user_role` ur " + 
				"        ON ur.role_id = r.role_id " + 
				"    INNER JOIN t_user_info ui " + 
				"        ON ui.user_id = ur.user_id " + 
				"        AND ui.compID = ur.comp_id " + 
				"WHERE ui.id = #{user.id} " + 
				"ON DUPLICATE KEY UPDATE USER_ID_ = VALUES(USER_ID_), GROUP_ID_ = VALUES(GROUP_ID_)")
				.parameter("user", userEntity).singleResult();
	}

	@Override
	@Transactional
	public void initActivitiUser() {
		// 添加用户
		identityService.createNativeUserQuery().sql("INSERT INTO ACT_ID_USER (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_) "
				+ "select id, 1, realName, u.`user_name`, email, null "
				+ "from t_user_info ui left join `t_user` u on ui.`user_id` = u.`user_id` "
				+ "ON DUPLICATE KEY UPDATE FIRST_ = VALUES(FIRST_), LAST_ = VALUES(LAST_), EMAIL_ = VALUES(EMAIL_), PWD_ = VALUES(PWD_)")
				.singleResult();
		
		// 添加分组
		identityService.createNativeGroupQuery().sql("INSERT `act_id_group` (ID_, REV_, NAME_, TYPE_) " + 
				"SELECT " + 
				"    role_name , 1, role_name_zn, role_id " + 
				"FROM " + 
				"    `t_role` " + 
				"ON DUPLICATE KEY UPDATE NAME_ = VALUES(NAME_), TYPE_ = VALUES(TYPE_)").singleResult();
		
		// 清空分组对应关系
		identityService.createNativeGroupQuery().sql("TRUNCATE act_id_membership").singleResult();
		
		// 添加分组对应关系
		identityService.createNativeGroupQuery().sql("INSERT `act_id_membership` (USER_ID_, GROUP_ID_) " + 
				"SELECT DISTINCT " + 
				"    ui.`id`, g.ID_ " + 
				"FROM " + 
				"    `act_id_group` g " + 
				"    INNER JOIN t_role r " + 
				"    ON r.role_name = g.ID_ " + 
				"    INNER JOIN `t_user_role` ur " + 
				"        ON ur.role_id = r.role_id " + 
				"    INNER JOIN t_user_info ui " + 
				"        ON ui.user_id = ur.user_id " + 
				"        AND ui.compID = ur.comp_id " + 
				"ON DUPLICATE KEY UPDATE USER_ID_ = VALUES(USER_ID_), GROUP_ID_ = VALUES(GROUP_ID_)")
				.singleResult();
		
	}
}
