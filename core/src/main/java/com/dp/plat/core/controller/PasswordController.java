package com.dp.plat.core.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.pojo.UsernamePasswordCaptchaToken;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.JsoupUtil;
import com.dp.plat.core.util.PasswordUtil;
import com.dp.plat.support.CaptchaServlet;
import com.dp.plat.support.CaptchaUtil;
import com.dp.plat.support.mail.MailUtil;
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
	@SystemControllerLog(description = "用户修改密码", ignoreParams = {"newPassword", "oldPassword"})
	public ModelAndView modifyPassword(HttpServletRequest request,	
			@RequestParam(value = "newPassword", required = true) String newPassword,
			@RequestParam(value = "oldPassword", required = true) String oldPassword) {
		ModelAndView mav = new ModelAndView("/modifyPassword");
//		HttpSession session = request.getSession();
		// XXX 用UserContext替换
		User user = UserContext.getCurrentUser();
//		User user = (User) session.getAttribute("user");
		User userInDB = userService.selectByUserName(user.getUserName());
		// 判断用户输入的旧密码是否正确
		if (userInDB != null) {
			oldPassword = JsoupUtil.unescape(oldPassword);
			newPassword = JsoupUtil.unescape(newPassword);
			if (userInDB.getPassword().equals(PasswordUtil.encryptPassword(user.getUserName(), oldPassword))) {
				user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), newPassword));
				user.setUpdateBy(user.getUserName());
				user.setUpdateTime(new Date());
				user.setNeedChangePwd(false);
				userService.updateByUsername(user);
				mav.addObject("successMsg", "修改密码成功");
				
				// 当前会话在进行重新登录
				Subject subject = SecurityUtils.getSubject();
				subject.logout();
				String captcha = new CaptchaUtil().genRandomCode();
				UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(user.getUserName(), PasswordUtil.encryptSHA1Password(newPassword, user.getUserName()), captcha);
				// rememberme
				token.setRememberMe(false);
				Session newSession = subject.getSession();
				newSession.setAttribute(CaptchaServlet.KEY_CAPTCHA, captcha);
				subject.login(token);
				
				// 获取当前用户登录的Session，全部踢下线
				Collection<Session> activeSessions = UserContext.getActiveSessions(user.getUserName());
				SessionDAO sessionDAO = UserContext.getSessionDAO();
				for (Session activeSession : activeSessions) {
					// 当前Session保留
					if (subject.getSession().getId().equals(activeSession.getId())) {
						continue;
					}
					sessionDAO.delete(activeSession);
				}
			} else {
				mav.addObject("errorMsg", "原密码不正确");
				return mav;
			}
		}
//		mav.setViewName("/modifyPassword");
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
		String nickName = "用户";
		if (StringUtils.isBlank(email)) {
//			UserInfo userInfo = userInfoService.selectByUserId(userId);
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(userId);
			userInfo.setCompID(UserContext.getCurrentPrincipal().getCompId());
			userInfo = userInfoService.selectOneByUserIdAndCompId(userInfo);
			email = userInfo.getEmail();
			nickName = userInfo.getRealName();
		}
		if (StringUtils.isBlank(email)) {
			model.addAttribute("errorMsg", "邮箱地址为空，无法重置密码！");
			return null;
		}
		String randomPassword = PasswordUtil.createRandomPassword(8);
		User user = new User(userId);
		user.setPassword(PasswordUtil.encryptPassword(userName, randomPassword));
		user.setNeedChangePwd(true);
		userService.updateByPrimaryKeySelective(user);
		
		// 获取重置用户登录的Session，全部踢下线
		Collection<Session> activeSessions = UserContext.getActiveSessions(userName);
		SessionDAO sessionDAO = UserContext.getSessionDAO();
		for (Session session : activeSessions) {
			sessionDAO.delete(session);
		}
		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("templateCode", "sys.password.reset.mail");
		context.put("bccs", email);
		context.put("randomPassword", randomPassword);
		context.put("beforeSplit", "${");
		context.put("afterSplit", "}");
		context.put("nickName", nickName);
//		context.put("dataSource", new Object[] {Collections.singletonMap("randomPassword", randomPassword)});
		MailUtil.keepMailWithTemplate(context, true);
		model.addAttribute("successMsg", "密码重置成功!<br>随机密码已发送至[" + email + "]");
		return null;
	}

}
