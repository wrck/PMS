package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.filter.CasFilter;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserDetail;
import com.dp.plat.core.vo.UserInfoVO;
import com.dp.plat.support.mail.MailUtil;

/**
 * 用户管理
 * 
 * @author j01441
 *
 */

@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "user")
@Controller
public class UserController {

	@Autowired
	private IShiroService shiroService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IUserRoleService userRoleService;
	@Autowired
	private IRoleService roleService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "user_list";
	}

	@RequestMapping("/list")
//	@SystemControllerLog(description = "查看用户列表")
	public String list(PageParam<UserDetail> pageParam, UserDetail userDetail, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		userDetail.setCompID(user.getCompId());
		PageParam<UserDetail> tempParam = new PageParam<>();
		UserDetail temp = new UserDetail();
		temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		
		pageParam.setModel(userDetail);
		pageParam.setTotal(userService.countBySelective(tempParam));
		pageParam.setFiltered(userService.countBySelective(pageParam));
		List<UserDetail> userList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		userList = userService.selectBySelective(pageParam);
		model.addAttribute("data", userList);
		return Consts.URLPath.SYSTEM_MANAGER + "user_list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		boolean isAdmin = SecurityUtils.getSecurityManager().hasRole(principalCollection, "admin");
		Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
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
		String isCas = SystemConfig.systemVariables.getOrDefault("sys.cas", "0");
		try {
			CasFilter casFilter = SpringContext.getBean(CasFilter.class);
		} catch (Exception e) {
			isCas = "0";
		}
		model.addAttribute("isCas", isCas);
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping("/detail")
	public String create(Model model) {
		List<Role> roles = roleService.selectBySelective(null);
		model.addAttribute("roles", roles);
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "创建用户")
	public String create(User user, UserInfo userInfo, String roleIds, Model model) {
		Boolean isNew = true;
		String randomPassword = PasswordUtil.createRandomPassword(8);
		user.setCreateTime(new Date());
		user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), randomPassword));
		try {
			userService.insertSelective(user);
		} catch (DuplicateKeyException e) {
			isNew = false;
			user = userService.selectByUserName(user.getUserName());
		}
		
		Integer compID = UserContext.getCurrentPrincipal().getCompId();
		userInfo.setUserId(user.getUserId());
		userInfo.setCompID(compID);
		userInfoService.insertSelective(userInfo);

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

		// return "redirect:"+ Consts.URLPath.SYSTEM_MANAGER + "user/" +
		// user.getUserId() + ".json";
		model.addAttribute("userId", user.getUserId());
		
		// 新用户，发送邮件通知账户已开通，并发送密码
		if (isNew) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", "sys.user.created.mail");
			context.put("userName", user.getUserName());
			context.put("realName", userInfo.getRealName());
			context.put("bccs", userInfo.getEmail());
			context.put("randomPassword", randomPassword);
			context.put("dataSource", new Object[] {userInfo});
			MailUtil.keepMailWithTemplate(context, true);
		}
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping(value = "{userId}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "修改用户信息")
	public String update(@PathVariable("userId") Integer userId, User user, UserInfo userInfo, String roleIds) {
		boolean isAdmin = UserContext.hasRole(RoleConstant.ROLE_ADMIN);
		Principal currentUser = UserContext.getCurrentPrincipal();
		Integer currentUserId = currentUser.getUserId();
		boolean isCurrentUser = false;
		if (userId.equals(currentUserId)) {
			isCurrentUser = true;
		} else if (!userId.equals(currentUserId) && !isAdmin) {
			return "redirect:/unauthorized.html";
		}
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
//			userInfo.setUserId(info.getUserId());
			//userInfoService.updateByUserId(userInfo);
			userInfoService.updateByPrimaryKeySelective(userInfo);
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
			if (StringUtils.isNotBlank(roleIds)) {
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
		// return "redirect:" + Consts.URLPath.SYSTEM_MANAGER + "user/" + userId
		// + ".json";
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@SystemControllerLog(description = "删除用户")
	public void delete(@PathVariable("id") Integer id) {
		Integer compId = UserContext.getCurrentPrincipal().getCompId();
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(id);
		userInfo.setCompID(compId);
		UserInfoVO userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
		if (userInfoVO != null) {
			userInfoService.deleteByPrimaryKey(userInfoVO.getId());
			UserRole userRole = new UserRole();
			userRole.setCompId(compId);
			userRole.setUserId(id);
			String userRoleIds = StringUtils.trimToEmpty(userRoleService.selectUserRolesByUserIdAndCompId(userRole));
			List<String> roleIdList = Arrays.asList(StringUtils.split(userRoleIds, ","));
			List<UserRole> del = new ArrayList<>(roleIdList.size());
			for (String oldRole : roleIdList) {
				userRole = new UserRole();
				userRole.setCompId(compId);
				userRole.setUserId(id);
				userRole.setRoleId(Integer.parseInt(oldRole));
				del.add(userRole);
			}
			if (!del.isEmpty()) {
				userRoleService.batchDeleteUserRoleByUserRole(del);
			}
		}
		List<UserInfoVO> userInfos = userInfoService.selectVOsByUserId(id);
		if (userInfos.isEmpty()) {
			userService.deleteByPrimaryKey(id);
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
			UserInfoVO userInfoVO = userInfoService.selectOneByUserIdAndCompId(userInfo);
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
}
