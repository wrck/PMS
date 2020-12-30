package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.impl.UserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.dao.ProjectManageUserMapper;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;

@Service("projectManageUserService")
public class ProjectManageUserService extends UserService implements IProjectManageUserService {
	
	@Autowired
	private ProjectManageUserMapper projectManageUserMapper;
	
	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IdentityService identityService;
	
	@Override
	public long countBySelectivePageable(PageParam<UserDetail> pageParam) {
		Principal currentPrincipal = UserContext.getCurrentPrincipal();
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN)) {
			return 0;
		}
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN) && UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
			if (pageParam != null && pageParam.getModel() != null) {
				UserDetail model = pageParam.getModel();
				model.setRoleCodes(StringUtils.join(currentPrincipal.getRoles(), ","));
				model.setCustom4(currentPrincipal.getUserInfo().getCustom4());
				model.setCustom5(currentPrincipal.getUserInfo().getCustom5());
			}
		}
		return projectManageUserMapper.countBySelectivePageable(pageParam);
	}

	@Override
	public long countBySelective(UserDetail userDetail) {
		return projectManageUserMapper.countBySelective(userDetail);
	}

	@Override
	public List<UserDetail> selectBySelectivePageable(PageParam<UserDetail> pageParam) {
		return projectManageUserMapper.selectBySelectivePageable(pageParam);
	}

	@Override
	public List<UserDetail> selectBySelective(UserDetail userDetail) {
		return projectManageUserMapper.selectBySelective(userDetail);
	}
	
	@Override
	public List<UserInfo> selectBySelective(UserInfo userInfo) {
		return userInfoService.selectBySelective(userInfo);
	}
	
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
