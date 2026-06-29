package com.dp.plat.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PasswordDao;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.LoginParam;
import com.dp.plat.param.PasswordEditParam;
import com.dp.plat.security.context.HttpContext;
import com.dp.plat.security.util.CaptchaUtil;

public class PasswordServiceImpl extends BaseServiceImpl implements PasswordService{
	private PasswordDao passwordDao;
	@Override
	public boolean changelogin(PasswordEditParam passwordEditParam) {
	    UserContext userContext = getUserContext();
	    User user = userContext.getUser();
		passwordEditParam.setId(user.getId());
		if(!passwordEditParam.getOldPassword().equals(user.getPassword())){
			return false;
		}
		passwordDao.usChangelogin(passwordEditParam);
		//记录日志
		log("更新密码");
		getUserContext().getUser().setPassword(passwordEditParam.getNewPassword());
		
        // 当前会话在进行重新登录
        HttpServletRequest currentRequest = HttpContext.getCurrentRequest();
        userContext.logout();
        LoginService loginService = SpringContext.getBean("loginService", LoginService.class);
        loginService.logout();
        
        // 获取验证码
        HttpSession session = currentRequest.getSession();
        String captcha = new CaptchaUtil().genRandomCode();
        LoginParam loginParam = new LoginParam();
        loginParam.setUsername(user.getUsername());
        loginParam.setPassword(passwordEditParam.getNewPassword());
        loginParam.setValidation(captcha);
        session.setAttribute("rand", captcha);
        loginService.login(loginParam, HttpContext.getCurrentIp());
        
        // 强制下线
        forcedOffline(user.getUsername());
		return true;
	}
	
	/**
	 * 踢指定用户下线
	 * @param username
	 */
	@Override
	public void forcedOffline(String username) {
        // 获取当前用户登录的Session，全部踢下线
        List<UserContext> onlineList = new ArrayList<UserContext>(UserContext.getOnlineList());
        for (Iterator<UserContext> iterator = onlineList.iterator(); iterator.hasNext();) {
            UserContext activeSession = (UserContext) iterator.next();
            // 当前用户
            if (!username.equals(activeSession.getUsername())) {
                continue;
            }
            // 当前Session保留
            if (getUserContext().toString().equals(activeSession.toString())) {
                continue;
            }
            activeSession.logout();
        }
	}
	
	public PasswordDao getPasswordDao() {
		return passwordDao;
	}
	public void setPasswordDao(PasswordDao passwordDao) {
		this.passwordDao = passwordDao;
	}
	
	
}
