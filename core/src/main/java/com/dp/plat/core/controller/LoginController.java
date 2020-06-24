package com.dp.plat.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.CaptchaException;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UsernamePasswordCaptchaToken;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.MenuUtil;
import com.dp.plat.core.vo.UserInfoVO;

/**
 * 登录控制器
 * 
 * @author j01441
 *
 */

@Controller
@RequestMapping(path={"/", Consts.URLPath.SYSTEM_MANAGER})
public class LoginController {

	@Resource
	private IUserService userService;
	@Resource
	private IUserInfoService userInfoService;
	@Resource
	private IShiroService shiroService;
	
	/**
	 * 登录页面，已登录用户不需要再进行登录直接跳转到成功页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {// 未认证
			return "redirect:/sys/success.html";
		}
		return "login";
	}

	/**
	 * 登录请求
	 * 
	 * @param username
	 * @param password
	 * @param captcha
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@SystemControllerLog(description = "登录")
	public String login(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "captcha") String captcha, HttpServletRequest request, Model model) {
		Subject currentUser = SecurityUtils.getSubject();
		// if (!currentUser.isAuthenticated()) {// 未认证
		// 把用户名和密码封装为 UsernamePasswordToken 对象
		UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(username, password, captcha);
		// rememberme
		token.setRememberMe(false);
		try {
			User user = new User();
			user.setUserName(username);
//			HttpSession session = request.getSession();
//			session.setAttribute("user", user);
//			session.setAttribute("contextPath", session.getServletContext().getContextPath());
			// 执行登录.
			currentUser.login(token);
			
			String loginUrl = "/login";
			String servletPath = request.getServletPath();
			int lastIndexOf = servletPath.lastIndexOf(loginUrl);
			String suffix = servletPath.substring(lastIndexOf + loginUrl.length());
			return "redirect:/sys/success" + suffix;
//			String suffix = request.getServletPath().replace("/login", "");
//			return "redirect:/success" + suffix;
		} catch (CaptchaException e) {
			request.setAttribute("error", e.getMessage());
			model.addAttribute("error", e.getMessage());
			e.printStackTrace();
		} catch (IncorrectCredentialsException e) {
			userService.updateUserErrorCount(username);
			User user = userService.selectByUserName(username);
			String error = "用户名或密码错误！";
			if (user.getLoginErrorCount() > 2) {
				error += "连续错误" + user.getLoginErrorCount() + "次";
			}
			request.setAttribute("error", error);
			model.addAttribute("error", error);
			e.printStackTrace();
		} catch (AuthenticationException e) {
			request.setAttribute("error", e.getMessage());
			model.addAttribute("error", e.getMessage());
			e.printStackTrace();
		}
		// }
		return "login";
	}

	@RequestMapping("success")
	public String loginSuccess(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		String htmlSuffix = ".html";
//		String suffix = request.getServletPath().replace("/sys/success", "");
//		if (".html".equals(suffix)) {
		if (servletPath.endsWith(htmlSuffix)) {
			// 判断用户进入的首页
			Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
			if (principal.getNeedChangePwd()/* && ".html".equals(suffix)*/) {
				return "redirect:" + Consts.URLPath.SYSTEM_MANAGER + "user/" + principal.getUserId()
						+ ".html?needChangePwd=true";
			}
			
			if (StringUtils.isNotBlank(principal.getHomePage()) && !principal.getHomePage().replace(htmlSuffix, "").matches("(/sys)?/success")) {
				return "redirect:" + principal.getHomePage();
			}
		}
		return "success";
	}

	@RequestMapping(value = "changeCompany", method = RequestMethod.POST)
	public void changeCompany(@RequestParam("compId") Integer compId, Model model) {
		Principal principal = UserContext.getCurrentPrincipal();
		if (principal.getIsSysUser() != 0) {
			List<UserInfoVO> userInfoVoList = principal.getUserInfoList();
			for (UserInfoVO userInfoVO : userInfoVoList) {
				if (compId.equals(userInfoVO.getCompID())) {
					principal.setUserInfo(userInfoVO);
					break;
				}
			}
			model.addAttribute("status", true);
			model.addAttribute("message", "切换成功！");
			return;
		}
		
		Integer userId = principal.getUserId();
		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setUserId(userId);
		userInfo.setCompID(compId);
		
		userInfo = userInfoService.selectOneByUserIdAndCompId(userInfo);
		if (userInfo == null) {
			model.addAttribute("status", false);
			model.addAttribute("message", "切换失败，并未找到该公司的用户信息");
			return;
		}
		principal.setUserInfo(userInfo);
		
		String homePage = userService.queryMaxRoleHomePageByUserIdAndCompId(principal.getUserInfo());
		principal.setHomePage(homePage);
		
		List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(principal.getUserInfo());
		HttpSession session = HttpContext.getCurrentRequest().getSession();
		principal.setMenus(MenuUtil.drow(nodes, (String) session.getServletContext().getContextPath()));
		
		List<UserInfoVO> userInfoVOs = userInfoService.selectVOsByUserId(userId);
		principal.setUserInfoList(userInfoVOs);
		
		RealmSecurityManager realmSecurityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		Realm shiroRealm = realmSecurityManager.getRealms().iterator().next();
		try {
			Method method = shiroRealm.getClass().getMethod("doClearCache", PrincipalCollection.class);
			if (method != null) {
				method.invoke(shiroRealm, SecurityUtils.getSubject().getPrincipals());
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
//		shiroRealm.doClearCache(SecurityUtils.getSubject().getPrincipals());
		model.addAttribute("status", true);
		model.addAttribute("message", "切换成功！");
		return;
	}

}
