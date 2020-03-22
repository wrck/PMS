package com.dp.plat.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.support.mail.MailSenderInfo;
import com.dp.plat.support.mail.MailUtil;
import com.dp.plat.support.mail.entity.MailInfo;
import com.dp.plat.support.mail.service.IMailInfoService;

@Controller
public class PasswordController {
	@Resource
	private IUserService userService;
	@Resource
	private IUserInfoService userInfoService;
	@Resource
	private IMailInfoService mailInfoService;

	@RequestMapping(value = "/password", method = RequestMethod.GET)
	public String forwardToPage() {
		return "/modifyPassword";
	}

	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
	@SystemControllerLog(description = "用户修改密码")
	public ModelAndView modifyPassword(HttpServletRequest request,
			@RequestParam(value = "newPassword", required = true) String newPassword,
			@RequestParam(value = "oldPassword", required = true) String oldPassword) {
		ModelAndView mav = new ModelAndView();
//		HttpSession session = request.getSession();
		// XXX 用UserContext替换
		User user = UserContext.getCurrentUser();
//		User user = (User) session.getAttribute("user");
		User userInDB = userService.selectByUserName(user.getUserName());
		// 判断用户输入的旧密码是否正确
		if (userInDB != null) {
			if (userInDB.getPassword().equals(PasswordUtil.encryptPassword(user.getUserName(), oldPassword))) {
				user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), newPassword));
				user.setUpdateBy(user.getUserName());
				user.setUpdateTime(new Date());
				user.setNeedChangePwd(false);
				userService.updateByUsername(user);
				mav.addObject("successMsg", "修改密码成功");
			} else {
				mav.addObject("errorMsg", "原密码不正确");
				return mav;
			}
		}
		mav.setViewName("/modifyPassword");
		return mav;
	}

	@RequestMapping(value = "/admin/resetPassword", method = RequestMethod.POST)
	@SystemControllerLog(description = "管理员重置密码")
	// @RequiresRoles("admin")
	public String resetPassword(@RequestParam(value = "userId", required = true) Integer userId,
			@RequestParam(value = "userName", required = true) String userName, String email, Model model) {
		if (!UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
			return "redirect:/unauthorized.html";
		}
		if (StringUtils.isBlank(email)) {
//			UserInfo userInfo = userInfoService.selectByUserId(userId);
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(userId);
			userInfo.setCompID(UserContext.getCurrentPrincipal().getCompId());
			userInfo = userInfoService.selectOneByUserIdAndCompId(userInfo);
			email = userInfo.getEmail();
		}
		if (StringUtils.isBlank(email)) {
			model.addAttribute("errorMsg", "邮箱地址为空，无法重置密码！");
			return null;
		}
		String randPassword = PasswordUtil.createRandomPassword(8);
		User user = new User(userId);
		user.setPassword(PasswordUtil.encryptPassword(userName, randPassword));
		user.setNeedChangePwd(true);
		userService.updateByPrimaryKeySelective(user);
		MailInfo mailInfo = new MailSenderInfo();
		mailInfo.setSubject("OSS重置密码");
		StringBuilder content = new StringBuilder(userName);
		content.append("你好！<br>&nbsp;&nbsp;&nbsp;&nbsp;您重置后的随机密码为：").append(randPassword)
				.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;请尽快登录<a href='");
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String hostUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
		+ request.getContextPath();
		content.append(hostUrl);
		content.append("'>OSS系统</a>进行修改！");
		mailInfo.setContent(content.toString());
		mailInfo.setBccs(email);
		MailUtil.keepMail(mailInfo);
		boolean success = MailUtil.sendHtmlMail((MailSenderInfo) mailInfo);
		if (success) {
			//mailInfoService.updateMailWhenSendSuccess(mailInfo.getId().toString());
			List<MailInfo> successMails = new ArrayList<MailInfo>();
			successMails.add(mailInfo);
			mailInfoService.updateMailInfoWhenSendSuccess(successMails);
		}
		model.addAttribute("successMsg", "密码重置成功!<br>随机密码已发送至[" + email + "]");
		return null;
	}

}
