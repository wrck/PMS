package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
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
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.filter.CasFilter;
import com.dp.plat.core.param.Consts;
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
import com.dp.plat.core.vo.Result;
import com.dp.plat.core.vo.RoleParam;
import com.dp.plat.ehr.job.EhrDataJob;
import com.dp.plat.ehr.service.IEmployeeService;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;
import com.dp.plat.pms.springmvc.vo.UserDetail;
import com.dp.plat.pms.springmvc.vo.UserInfoVO;
import com.dp.plat.support.mail.MailUtil;

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
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("user");
		this.setKeyword("userId");
		this.setUseTemplate(false);
	}
	
	@RequestMapping
	public String home(Model model) {
		if (!checkPermission(null, model)) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		RoleParam rolePage = new RoleParam();
		rolePage.setModel(new Role());
		rolePage.setOrderBy("priority desc");
		rolePage.setPageSize(9999L);
		List<Role> roles = roleService.selectBySelective(rolePage);
		this.setUseTemplate(false);
		model.addAttribute("roles", roles);
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, UserInfoVO userInfo, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		userInfo.setCompID(user.getCompId());
		if (!checkPermission(userInfo, model)) {
			return Consts.VIEW_UNAUTHORIZED;
		} else if (!HttpContext.isHTML()) {
			PageParam<UserDetail> tempParam = new PageParam<>();
			UserDetail temp = new UserDetail();
			temp.setCompID(user.getCompId());
			tempParam.setModel(temp);
			pageParam.setTotal(projectManageUserService.countBySelectivePageable(tempParam));
			
			List<UserDetail> userList = new ArrayList<>();
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
			UserDetail userDatil = new UserDetail();
			BeanUtils.copyProperties(userInfo, userDatil);
			tempParam.clone(pageParam);
			tempParam.setModel(userDatil);
			pageParam.setFiltered(projectManageUserService.countBySelectivePageable(tempParam));
			userList = projectManageUserService.selectBySelectivePageable(tempParam);
			model.addAttribute("data", userList);
			
			pageParam.setModel(userDatil);
			pageParam.setColumns(findColumnList(DATANAME_TABLE));
			pageParam.setRowId(getKeyword());
			model.addAttribute("permissionType", "all");
			model.addAttribute("permissions", Collections.singleton("user:*"));
		}
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		UserInfoVO userInfoVO = new UserInfoVO();
		userInfoVO.setId(id);
		boolean isAdmin = checkPermission(userInfoVO, model);
		Principal principal = UserContext.getCurrentPrincipal();
		Integer currentUserId = principal.getUserId();
		boolean isCurrentUser = false;
		if (id.equals(currentUserId)) {
			isCurrentUser = true;
		} else if (!isAdmin) {
			return "redirect:/unauthorized.html";
		}
		model.addAttribute("maxRole", principal.getMaxRole());
		model.addAttribute("isCurrentUser", isCurrentUser);
		User user = projectManageUserService.selectByPrimaryKey(id);
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
		
		RoleParam rolePage = new RoleParam();
		rolePage.setModel(new Role());
		rolePage.setOrderBy("priority desc");
		rolePage.setPageSize(9999L);
		List<Role> roles = roleService.selectBySelective(rolePage);
		// 允许添加的角色
		Role maxRole = principal.getMaxRole();
		int priority = maxRole != null ? maxRole.getPriority() : 0;
		for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			if(role.getPriority() > priority) {
				iterator.remove();
			}
		}
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("roles", roles);
		model.addAttribute("user", user);
		model.addAttribute("userInfo", userInfo);
		model.addAttribute("roleIds", roleIds);
		String isCas = SystemConfig.systemVariables.getOrDefault("sys.cas", "0");
		try {
			CasFilter casFilter = SpringContext.getBean(CasFilter.class);
		} catch (Exception e) {
			isCas = "0";
		}
		model.addAttribute("isCas", isCas);
		return getViewNameSpace() +  "detail";
	}

	@RequestMapping("/detail")
	public String detail(UserInfoVO userInfo, Model model) {
		boolean isAdmin = checkPermission(userInfo, model);
		Principal currentPrincipal = UserContext.getCurrentPrincipal();
		List<Role> roles = roleService.selectBySelective(null);
		// 允许添加的角色
		Role maxRole = currentPrincipal.getMaxRole();
		int priority = maxRole != null ? maxRole.getPriority() : 0;
		for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			if(role.getPriority() > priority) {
				iterator.remove();
			}
		}
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("roles", roles);
		model.addAttribute("maxRole", currentPrincipal.getMaxRole());
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "创建用户")
	public String create(@RequestBody UserInfoVO userInfo, Model model) {
		boolean isAdmin = checkPermission(userInfo, model);
		if (!isAdmin) {
			return "redirect:/unauthorized.html";
		}
		Principal currentUser = UserContext.getCurrentPrincipal();
		User user = userInfo.getUser();
		
		Boolean isInnerUser = false;
		Boolean isNew = true;
		String userName = user.getUserName();
		if (StringUtils.isNotBlank(userName)) {
			EmployeeVO employee = new EmployeeVO();
			employee.setAccount(user.getUserName());
			List<EmployeeVO> employeeWithAccount = employeeService.selectEmployeeWithAccount(employee);
			if (!employeeWithAccount.isEmpty()) {
				isInnerUser = true;
				employeeService.initUser(employeeWithAccount);
			} else {
				// 补充员工工号
				user.setUserCustom3(userName);
				userInfo.setWorkNo(userName);
			}
		}
		
		String randomPassword = PasswordUtil.createRandomPassword(8);
		user.setCreateTime(new Date());
		user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), randomPassword));
		try {
			projectManageUserService.insertSelective(user);
			// 补充员工ID
			if (user.getUserCustom4() == null) {
				user.setUserCustom4(user.getUserId());
				User temp = new User(user.getUserId());
				temp.setUserCustom4(user.getUserId());
				projectManageUserService.updateByPrimaryKeySelective(temp);
			}
		} catch (DuplicateKeyException e) {
			user = projectManageUserService.selectByUserName(user.getUserName());
		}
		
		Integer compID = UserContext.getCurrentPrincipal().getCompId();
		userInfo.setUserId(user.getUserId());
		userInfo.setCompID(compID);
		
		// 允许分配的项目类型
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN)) {
			List<String> allowProjectTypes = new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.stripToEmpty(currentUser.getUserInfo().getCustom4()), ",")));
			List<String> projectTypes = new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.stripToEmpty(userInfo.getCustom4()), ",")));
			projectTypes.retainAll(allowProjectTypes);
			userInfo.setCustom4(StringUtils.join(projectTypes, ","));
		}
		try {
			userInfoService.insertSelective(userInfo);
		} catch (DuplicateKeyException e) {
			isNew = false;
			UserInfo userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
			userInfo.setId(userInfoVO.getId());
			userInfo.setRealName(userInfoVO.getRealName());
			userInfo.setTelphone(userInfoVO.getTelphone());
			userInfo.setMobile(userInfoVO.getMobile());
			userInfo.setEmail(userInfoVO.getEmail());
		}
		
		// 允许添加的角色
		Role maxRole = currentUser.getMaxRole();
		int priority = maxRole != null ? maxRole.getPriority() : 0;
		List<Role> allRoles = roleService.selectBySelective(null);
		Set<Integer> validRoleIds = new HashSet<Integer>(allRoles.size());
		for (Role role : allRoles) {
			if(role.getPriority() <= priority) {
				validRoleIds.add(role.getRoleId());
			}
		}

		String roleIds = userInfo.getRoleIds();
		if (StringUtils.isNotBlank(roleIds)) {
			String[] newRoles = roleIds.split(",");
			List<UserRole> add = new ArrayList<>();
			for (String newRole : newRoles) {
				int roleId = Integer.parseInt(newRole);
				UserRole userRole = new UserRole();
				userRole.setUserId(user.getUserId());
				userRole.setRoleId(roleId);
				userRole.setCompId(compID);
				if (validRoleIds.contains(roleId)) {
					add.add(userRole);
				}
			}
			if (!add.isEmpty()) {
				userRoleService.batchInsertUserRole(add);
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

		// return "redirect:"+ Consts.URLPath.SYSTEM_MANAGER + "user/" +
		// user.getUserId() + ".json";
		model.addAttribute("userId", user.getUserId());
		
		// 外部用户，发送邮件通知账户已开通，并发送密码
		if (isNew && !isInnerUser) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", "sys.user.created.mail");
			context.put("userName", user.getUserName());
			context.put("realName", userInfo.getRealName());
			context.put("bccs", userInfo.getEmail());
			context.put("randomPassword", randomPassword);
			context.put("beforeSplit", "${");
			context.put("afterSplit", "}");
			context.put("dataSource", new Object[] {userInfo});
			MailUtil.keepMailWithTemplate(context, true);
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{userId}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "修改用户信息")
	public String update(@PathVariable("userId") Integer userId, @RequestBody UserInfoVO userInfo, Model model) {
		boolean isAdmin = checkPermission(userInfo, model);
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
		projectManageUserService.updateByPrimaryKeySelective(user);
//		UserInfo info = userInfoService.selectByUserId(userId);
		
		Integer compId = currentUser.getCompId();
		if (currentUser.getIsSysUser() != 0 && isCurrentUser) {
			compId = -1;
		}
		
		UserInfo info = new UserInfo();
		info.setUserId(userId);
		info.setCompID(compId);
		info = userInfoService.selectOneByUserIdAndCompId(info);
		
		// 允许分配的项目类型
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN)) {
			List<String> allowProjectTypes = new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.stripToEmpty(currentUser.getUserInfo().getCustom4()), ",")));
			List<String> projectTypes = new ArrayList<String>(Arrays.asList(StringUtils.split(StringUtils.stripToEmpty(userInfo.getCustom4()), ",")));
			projectTypes.retainAll(allowProjectTypes);
			userInfo.setCustom4(StringUtils.join(projectTypes, ","));
		}
		if (info == null) {
			// 补充员工工号
			userInfo.setWorkNo(user.getUserName());
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
			// 允许添加的角色
			Role maxRole = currentUser.getMaxRole();
			int priority = maxRole != null ? maxRole.getPriority() : 0;
			List<Role> allRoles = roleService.selectBySelective(null);
			Set<Integer> validRoleIds = new HashSet<Integer>(allRoles.size());
			for (Role role : allRoles) {
				if(role.getPriority() <= priority) {
					validRoleIds.add(role.getRoleId());
				}
			}
			
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
					int roleId = Integer.parseInt(newRole);
					UserRole userRole = new UserRole();
					userRole.setUserId(user.getUserId());
					userRole.setRoleId(roleId);
					userRole.setCompId(compId);
					if (validRoleIds.contains(roleId)) {
						add.add(userRole);
					}
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
		UserInfoVO vo = new UserInfoVO();
		vo.setUserId(userId);
		boolean isAdmin = checkPermission(vo, model);
		Principal currentUser = UserContext.getCurrentPrincipal();
		Integer currentUserId = currentUser.getUserId();
		boolean isCurrentUser = false;
		if (userId.equals(currentUserId)) {
			isCurrentUser = true;
		} else if (!userId.equals(currentUserId) && !isAdmin) {
			model.addAllAttributes(new Result(false, "没有权限进行该操作！").getMap());
			return;
		}
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
			String userRoleIds = StringUtils.trimToEmpty(userRoleService.selectUserRolesByUserIdAndCompId(userRole));
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
			projectManageUserService.deleteByPrimaryKey(userId);
		}
		model.addAllAttributes(new Result(true, "删除成功").getMap());
	}

	@RequestMapping(value = "checkUnique", method = RequestMethod.POST)
	public void checkUnique(@RequestParam("userName") String userName, Model model) {
//		boolean isUnique = userService.checkUniqueUserName(userName);
//		model.addAttribute("valid", !isUnique);
		User user = projectManageUserService.selectByUserName(userName);
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
		List<com.dp.plat.core.vo.UserDetail> userList = projectManageUserService.findUserByParam(request.getParameterMap());
		model.addAttribute("data", userList);
	}
	
	@RequestMapping("/initActitityUser")
	public void initActitityUser(HttpServletRequest request, Model model) {
		EhrDataJob ehrDataJob = new EhrDataJob();
		ehrDataJob.execute();
		projectManageUserService.initActivitiUser();
	}
	
	@Override
	public boolean checkPermission(UserInfoVO v, Model model, String... permissions) {
//		return super.checkPermission(v, model, permissions);
		String extRolesVar = SystemConfig.systemVariables.getOrDefault("sys.user.manager.extRoles", "");
		String[] extRoles = StringUtils.split(extRolesVar, ",");
		String[] roles = new String[] {RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN};
		Set<String> rolesSet = new HashSet<String>(Arrays.asList(roles));
		rolesSet.addAll(Arrays.asList(extRoles));
		if (!UserContext.hasAnyRoles(rolesSet)) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return false;
		}
		return true;
	}

}
