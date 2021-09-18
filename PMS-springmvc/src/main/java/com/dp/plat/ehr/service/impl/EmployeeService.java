package com.dp.plat.ehr.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.ehr.dao.EmployeeMapper;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.service.IEhrEmpPowerService;
import com.dp.plat.ehr.service.IEmployeeService;
import com.dp.plat.ehr.vo.EmployeeAppraiserVO;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.ehr.vo.Select2Data;

/**
 *
 * Created by CodeGenerator
 */
@Service("employeeService")
public class EmployeeService extends AbstractBaseService<EmployeeMapper, Employee> implements IEmployeeService {

	@Autowired
	private IUserService userService;

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IUserRoleService userRoleService;

	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IEhrEmpPowerService ehrEmpPowerService;

	@Autowired
	private IdentityService identityService;

	@Override
	public long countBySelectivePageableVO(PageParam<EmployeeVO> pageParam) {
		return dao.countBySelectivePageableVO(pageParam);
	}

	@Override
	public List<EmployeeVO> selectBySelectivePageableVO(PageParam<EmployeeVO> pageParam) {
		return dao.selectBySelectivePageableVO(pageParam);
	}

	@Override
	public EmployeeVO selectVOByPrimaryKey(Integer id) {
		return dao.selectVOByPrimaryKey(id);
	}

	@Override
	public List<EmployeeVO> selectEmployeeVOByIds(String ids) {
		return dao.selectEmployeeVOByIds(ids);
	}

	@Override
	public List<Select2Data> selectEmployeeSelect2Data(Select2Data select2Data) {
		return dao.selectEmployeeSelect2Data(select2Data);
	}

	@Override
	public List<EmployeeVO> selectEmployeeWithAccount(EmployeeVO employee) {
		return dao.selectEmployeeWithAccount(employee);
	}

	@Override
	@Transactional
	public void initUser(List<EmployeeVO> employeeList) {
		if (employeeList == null || employeeList.isEmpty()) {
			
			String empParams = SystemConfig.systemVariables.get("pm.sync.user.empParams");
			if (StringUtils.isNotBlank(empParams)) {
				EmployeeVO employee = JSON.parseObject(empParams, EmployeeVO.class);
				employee.setEmpStatus(1);
				employee.setEmpType(1);
				
				employeeList = this.selectEmployeeWithAccount(employee);
			} else {
				String depCodes = SystemConfig.systemVariables.get("pm.sync.user.officeCodes");
				String depIDs = SystemConfig.systemVariables.get("pm.sync.user.depIDs");
				String jobCodes = SystemConfig.systemVariables.get("pm.sync.user.jobCodes");
				String jobIDs = SystemConfig.systemVariables.get("pm.sync.user.jobIDs");
				employeeList = new ArrayList<EmployeeVO>();
				
				EmployeeVO temp = new EmployeeVO();
				temp.setEmpStatus(1);
				temp.setEmpType(1);
				
				EmployeeVO employee = new EmployeeVO();
				if (StringUtils.isNoneBlank(depCodes, depIDs)) {
					BeanUtils.copyProperties(temp, employee);
					employee.setDepCodes(depCodes);
					employee.setDepIDs(depIDs);
					List<EmployeeVO> list = this.selectEmployeeWithAccount(employee);
					employeeList.addAll(list);
				} else if (StringUtils.isNoneBlank(jobCodes, jobIDs)){
					BeanUtils.copyProperties(temp, employee);
					employee.setJobCodes(jobCodes);
					employee.setJobIDs(jobIDs);
					List<EmployeeVO> list = this.selectEmployeeWithAccount(employee);
					employeeList.addAll(list);
				}
			}
		}
		String roleName = SystemConfig.systemVariables.getOrDefault("pm.default.role", "user");
		String projectTypes = SystemConfig.systemVariables.getOrDefault("pm.default.projectTypes", "");
		Boolean ignoreCompany = Boolean.parseBoolean(SystemConfig.systemVariables.getOrDefault("pm.ignoreCompany", "false"));
		String defaultCompId = SystemConfig.systemVariables.getOrDefault("pm.default.compId", "1");
		List<Role> roleList = roleService.selectRolesByRoleNames(roleName);
		for (EmployeeVO employee : employeeList) {
			boolean isNew = true;
			String workNo = employee.getWorkNo();
			// 插入用户表
			User user = new User();
			user.setUserId(employee.getEmpID());
			user.setUserName(employee.getAccount());
			user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), PasswordUtil.createRandomPassword(8)));
			user.setNeedChangePwd(Boolean.FALSE);
			user.setUserCustom3(workNo);
			user.setUserCustom4(employee.getEmpID());
			try {
				userService.insertSelective(user);
				user.setUserId(employee.getEmpID());
			} catch (Exception e) {
				// User temp = userService.selectByUserName(user.getUserName());
				// //
				// 主键存在或者用户名存在进行此处，比较当前员工id时候已经初始化账号，如果没有继续，如果有则忽略（同个账号多个公司的员工信息时出现）
				// if (!employee.getEmpID().equals(temp.getUserCustom4())) {
				// user.setUserId(temp.getUserId());
				// } else {
				// continue;
				// }
				UserInfo userInfo = userInfoService.selectByPrimaryKey(employee.getEmpID());
				if (userInfo == null) {
					User temp = userService.selectByUserName(user.getUserName());
					user.setUserId(temp.getUserId());
				} else {
					isNew = false;
					//continue;
				}
			}

			// 插入用户信息表
			UserInfo userInfo = new UserInfo();
			BeanUtils.copyProperties(employee, userInfo);
			userInfo.setId(employee.getEmpID());
			userInfo.setUserId(user.getUserId());
			userInfo.setRealName(employee.getName());
			userInfo.setTelphone(employee.getOfficePhone());
			if (ignoreCompany) {
				userInfo.setCompID(Integer.valueOf(defaultCompId));
			}
			// 新增用户，初始化部门、区域和项目权限
			if (isNew) {
				userInfo.setCustom3(employee.getDepLV2Code());// 保存用户二级部门编码
				userInfo.setCustom4(projectTypes);// 保存项目权限
				userInfo.setCustom5(employee.getDepLV2Code());// 区域权限，默认为用户二级部门编码
			}
			// gender： 1-男，2-女，sex：1-男，0-女
			String sex = String.valueOf(employee.getGender() != null ? (employee.getGender() % 2) : null);
			userInfo.setSex(Short.valueOf(sex));
			if (isNew)  {
				userInfoService.insertSelective(userInfo);
				
				// 添加用户角色
				for (Role role : roleList) {
					UserRole userRole = new UserRole();
					userRole.setRoleId(role.getRoleId());
					userRole.setUserId(user.getUserId());
					userRole.setCompId(userInfo.getCompID());
					userRoleService.insertSelective(userRole);
				}
			} else {
				userInfoService.updateByPrimaryKeySelective(userInfo);
			}

			// 插入activiti用户表
			UserEntity userEntity = new UserEntity();
			userEntity.setId(employee.getEmpID().toString());
			userEntity.setFirstName(employee.getName());
			userEntity.setLastName(workNo);
			userEntity.setEmail(employee.getEmail());
//			identityService.saveUser(userEntity);
			insertOrUpdateActivitiUser(userEntity);
		}
		disableInvalidUser();
	}

	@Override
	public EmployeeVO selectByWorkNo(String workNo) {
		return dao.selectByWorkNo(workNo);
	}

	@Override
	public List<EmployeeAppraiserVO> selectEmployeeAppraiserBySelectivePageableVO(PageParam<EmployeeVO> pageParam) {
		return dao.selectEmployeeAppraiserBySelectivePageableVO(pageParam);
	}

	@Override
	@Transactional
	public void insertOrUpdateActivitiUser(UserEntity userEntity) {
		// 添加用户
		identityService.createNativeUserQuery()
				.sql("INSERT INTO ACT_ID_USER (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_) "
						+ "VALUES ( #{user.id}, 1, #{user.firstName}, #{user.lastName}, #{user.email}, #{user.password}) "
						+ "ON DUPLICATE KEY UPDATE FIRST_ = VALUES(FIRST_), LAST_ = VALUES(LAST_), EMAIL_ = VALUES(EMAIL_), PWD_ = VALUES(PWD_)")
				.parameter("user", userEntity).singleResult();
		
		// 添加分组
		identityService.createNativeGroupQuery().sql("INSERT `act_id_group` (ID_, REV_, NAME_, TYPE_) " + 
				"SELECT " + 
				"    role_name , 1, role_name_zn, role_id " + 
				"FROM " + 
				"    `t_role` " + 
				"ON DUPLICATE KEY UPDATE NAME_ = VALUES(NAME_), TYPE_ = VALUES(TYPE_)").singleResult();
		
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
		disableInvalidUser();
		
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
	
	private void disableInvalidUser() {
		// 失效已经离职的用户
		identityService.createNativeUserQuery()
				.sql("update t_user_info ui left join t_user u on ui.`user_id` = u.`user_id` left join ehr_employee e on e.`workNo` = ui.`workNo` and e.`compID` = ui.`compID` set ui.`state` = e.`empStatus`, u.`status` = 0 where e.`empStatus` = 2")
				.singleResult();
	}
}
