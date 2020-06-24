package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;
import com.dp.plat.ehr.job.EhrDataJob;
import com.dp.plat.ehr.service.IEmployeeService;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.job.DispatchSettlementSEEPaymentJob;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;
import com.dp.plat.pms.springmvc.vo.UserInfoVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "user")
public class ProjectManageUserController extends AbstractController<IUserInfoService, UserInfo, UserInfoVO> {

	@Autowired
	private IUserService userService;
	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IUserRoleService userRoleService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IProjectManageUserService projectManageUserService;
	@Autowired
	private IEmployeeService employeeService;
	
	@PostConstruct
	public void init() {
		this.setViewModel("user");
		this.setKeyword("userId");
		this.setUseTemplate(true);
	}
	
	@RequestMapping
	public String home(Model model) {
		return getTemplateNamespace() + "list";
	}

	@RequestMapping("/list")
	@SystemControllerLog(description = "查看用户列表")
	public String list(PageParam<Object> pageParam, UserInfoVO userInfo, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		userInfo.setCompID(user.getCompId());
		PageParam<UserDetail> tempParam = new PageParam<>();
		UserDetail temp = new UserDetail();
		temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		pageParam.setTotal(userService.countBySelective(tempParam));
		
		UserDetail userDatil = new UserDetail();
		BeanUtils.copyProperties(userInfo, userDatil);
		tempParam.clone(pageParam);
		tempParam.setModel(userDatil);
		pageParam.setFiltered(userService.countBySelective(tempParam));
		List<UserDetail> userList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		userList = userService.selectBySelective(tempParam);
		model.addAttribute("data", userList);
		
		pageParam.setColumns(findColumnList(DATANAME_TABLE));
		pageParam.setRowId(getKeyword());
		return getTemplateNamespace() + "list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		boolean isAdmin = UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN);
		Principal principal = UserContext.getCurrentPrincipal();
		Integer currentUserId = principal.getUserId();
		boolean isCurrentUser = false;
		if (id.equals(currentUserId)) {
			isCurrentUser = true;
		} else if (!isAdmin) {
			return "redirect:/unauthorized.html";
		}
		model.addAttribute("isCurrentUser", isCurrentUser);
		User user = userService.selectByPrimaryKey(id);
		Integer compId = principal.getCompId();
		if (principal.getIsSysUser() != 0 && isCurrentUser) {
			compId = -1;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(id);
		userInfo.setCompID(compId);
		userInfo = userInfoService.selectOneByUserIdAndCompId(userInfo);
//		String roleIds = userRoleService.selectUserRolesByUserId(id);
		UserRole userRole = new UserRole();
		userRole.setUserId(id);
		userRole.setCompId(compId);
		String roleIds = userRoleService.selectUserRolesByUserIdAndCompId(userRole);
		List<Role> roles = roleService.selectBySelective(null);
		model.addAttribute("roles", roles);
		model.addAttribute("user", user);
		model.addAttribute("userInfo", userInfo);
		model.addAttribute("roleIds", roleIds);
		model.addAttribute("isCas", SystemConfig.systemVariables.getOrDefault("sys.cas", "0"));
		return getViewNameSpace() +  "detail";
	}

	@RequestMapping("/detail")
	public String detail(UserInfoVO userInfo, Model model) {
		List<Role> roles = roleService.selectBySelective(null);
		model.addAttribute("roles", roles);
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "创建用户")
	public String create(@RequestBody UserInfoVO userInfo, Model model) {
		boolean isAdmin = UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN);
		if (!isAdmin) {
			return "redirect:/unauthorized.html";
		}
		
		User user = userInfo.getUser();
		
		String userName = user.getUserName();
		if (StringUtils.isNotBlank(userName)) {
			EmployeeVO employee = new EmployeeVO();
			employee.setAccount(user.getUserName());
			List<EmployeeVO> employeeWithAccount = employeeService.selectEmployeeWithAccount(employee);
			if (!employeeWithAccount.isEmpty()) {
				employeeService.initUser(employeeWithAccount);
			}
		}
		
		user.setCreateTime(new Date());
		user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), "123456"));
		try {
			userService.insertSelective(user);
		} catch (DuplicateKeyException e) {
			user = userService.selectByUserName(user.getUserName());
		}
		
		Integer compID = UserContext.getCurrentPrincipal().getCompId();
		userInfo.setUserId(user.getUserId());
		userInfo.setCompID(compID);
		try {
			userInfoService.insertSelective(userInfo);
		} catch (DuplicateKeyException e) {
			UserInfo userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
			userInfo.setId(userInfoVO.getId());
			userInfo.setRealName(userInfoVO.getRealName());
			userInfo.setTelphone(userInfoVO.getTelphone());
			userInfo.setMobile(userInfoVO.getMobile());
			userInfo.setEmail(userInfoVO.getEmail());
		}

		String roleIds = userInfo.getRoleIds();
		if (StringUtils.isNotBlank(roleIds)) {
			String[] newRoles = roleIds.split(",");
			List<UserRole> add = new ArrayList<>();
			for (String newRole : newRoles) {
				UserRole userRole = new UserRole();
				userRole.setUserId(user.getUserId());
				userRole.setRoleId(Integer.parseInt(newRole));
				userRole.setCompId(compID);
				add.add(userRole);
			}
			userRoleService.batchInsertUserRole(add);
		}
		
		// 插入activiti用户表
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userInfo.getId().toString());
		userEntity.setFirstName(userInfo.getRealName());
		userEntity.setLastName(user.getUserName());
		userEntity.setEmail(userInfo.getEmail());
//					identityService.saveUser(userEntity);
		projectManageUserService.insertOrUpdateActivitiUser(userEntity);

		// return "redirect:"+ Consts.URLPath.SYSTEM_MANAGER + "user/" +
		// user.getUserId() + ".json";
		model.addAttribute("userId", user.getUserId());
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{userId}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "修改用户信息")
	public String update(@PathVariable("userId") Integer userId, @RequestBody UserInfoVO userInfo, Model model) {
		boolean isAdmin = UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN);
		Principal currentUser = UserContext.getCurrentPrincipal();
		Integer currentUserId = currentUser.getUserId();
		boolean isCurrentUser = false;
		if (userId.equals(currentUserId)) {
			isCurrentUser = true;
		} else if (!userId.equals(currentUserId) && !isAdmin) {
			return "redirect:/unauthorized.html";
		}
		User user = userInfo.getUser();
		user.setUserId(userId);
		user.setUpdateTime(new Date());
		userService.updateByPrimaryKeySelective(user);
//		UserInfo info = userInfoService.selectByUserId(userId);
		
		Integer compId = currentUser.getCompId();
		if (currentUser.getIsSysUser() != 0 && isCurrentUser) {
			compId = -1;
		}
		
		UserInfo info = new UserInfo();
		info.setUserId(userId);
		info.setCompID(compId);
		info = userInfoService.selectOneByUserIdAndCompId(info);
		if (info == null) {
			userInfoService.insertSelective(userInfo);
		} else {
			userInfo.setId(info.getId());
			// 非管理员忽略自定义字段
			String[] ignoreProperties = null;
			if (!isAdmin) {
				ignoreProperties = new String[]{"custom1", "custom2", "custom3", "custom4", "custom5"};
			}
			BeanUtils.copyProperties(userInfo, info, ignoreProperties);
//			userInfo.setUserId(info.getUserId());
			//userInfoService.updateByUserId(userInfo);
			userInfoService.updateByPrimaryKeySelective(info);
		}
		
		if (isAdmin) {
			UserRole temp = new UserRole();
			temp.setUserId(userId);
			temp.setCompId(compId);
			//		String oldRoleIds = userRoleService.selectUserRolesByUserId(user.getUserId());
			String oldRoleIds = userRoleService.selectUserRolesByUserIdAndCompId(temp);
			List<String> oldRoles = new ArrayList<>();
			List<String> newRoles = new ArrayList<>();
			if (StringUtils.isNotBlank(oldRoleIds)) {
				oldRoles = Arrays.asList(oldRoleIds.split(","));
			}
			String roleIds = userInfo.getRoleIds();
			if (StringUtils.isNotBlank(roleIds )) {
				newRoles = Arrays.asList(roleIds.split(","));
			}
	
			List<UserRole> add = new ArrayList<>();
			for (String newRole : newRoles) {
				if (StringUtils.isBlank(oldRoleIds) || !oldRoleIds.contains(newRole)) {
					UserRole userRole = new UserRole();
					userRole.setUserId(user.getUserId());
					userRole.setRoleId(Integer.parseInt(newRole));
					userRole.setCompId(compId);
					add.add(userRole);
				}
			}
	
			List<UserRole> del = new ArrayList<>();
			for (String oldRole : oldRoles) {
				if (StringUtils.isBlank(roleIds) || !roleIds.contains(oldRole)) {
					UserRole userRole = new UserRole();
					userRole.setUserId(user.getUserId());
					userRole.setRoleId(Integer.parseInt(oldRole));
					userRole.setCompId(compId);
					del.add(userRole);
				}
			}
	
			if (!add.isEmpty()) {
				userRoleService.batchInsertUserRole(add);
			}
			if (!del.isEmpty()) {
				userRoleService.batchDeleteUserRoleByUserRole(del);
			}
		}
		
		// 插入activiti用户表
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userInfo.getId().toString());
		userEntity.setFirstName(userInfo.getRealName());
		userEntity.setLastName(user.getUserName());
		userEntity.setEmail(userInfo.getEmail());
//					identityService.saveUser(userEntity);
		projectManageUserService.insertOrUpdateActivitiUser(userEntity);
		// return "redirect:" + Consts.URLPath.SYSTEM_MANAGER + "user/" + userId
		// + ".json";
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
	@SystemControllerLog(description = "删除用户")
	public void delete(@PathVariable("userId") Integer userId, Model model) {
		Integer compId = UserContext.getCurrentPrincipal().getCompId();
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setCompID(compId);
		UserInfo userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
		if (userInfoVO != null) {
			userInfoService.deleteByPrimaryKey(userInfoVO.getId());
			UserRole userRole = new UserRole();
			userRole.setCompId(compId);
			userRole.setUserId(userId);
			String userRoleIds = userRoleService.selectUserRolesByUserIdAndCompId(userRole );
			List<String> roleIdList = Arrays.asList(StringUtils.split(userRoleIds, ","));
			List<UserRole> del = new ArrayList<>(roleIdList.size());
			for (String oldRole : roleIdList) {
				userRole = new UserRole();
				userRole.setCompId(compId);
				userRole.setUserId(userId);
				userRole.setRoleId(Integer.parseInt(oldRole));
				del.add(userRole);
			}
			if (!del.isEmpty()) {
				userRoleService.batchDeleteUserRoleByUserRole(del);
			}
		}
		List<?> userInfos = userInfoService.selectVOsByUserId(userId);
		if (userInfos.isEmpty()) {
			userService.deleteByPrimaryKey(userId);
		}
	}

	@RequestMapping(value = "checkUnique", method = RequestMethod.POST)
	public void checkUnique(@RequestParam("userName") String userName, Model model) {
//		boolean isUnique = userService.checkUniqueUserName(userName);
//		model.addAttribute("valid", !isUnique);
		User user = userService.selectByUserName(userName);
		boolean isUnique = false;
		if (user == null) {
			isUnique = true;
		} else {
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(user.getUserId());
			userInfo.setCompID(UserContext.getCurrentPrincipal().getCompId());
			UserInfo userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
			if (userInfoVO == null) {
				isUnique = true;
			}
		}
		model.addAttribute("valid", isUnique);
	}
	
	@RequestMapping("/param")
	public void findUserInfoWithParam(HttpServletRequest request, Model model) {
		List<UserDetail> userList = userService.findUserByParam(request.getParameterMap());
		model.addAttribute("data", userList);
	}
	
	@RequestMapping("/initActitityUser")
	public void initActitityUser(HttpServletRequest request, Model model) {
		EhrDataJob ehrDataJob = new EhrDataJob();
		ehrDataJob.execute();
		projectManageUserService.initActivitiUser();
	}
}
