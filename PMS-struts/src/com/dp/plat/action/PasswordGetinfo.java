package com.dp.plat.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.HttpContext;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.LoginDao;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.PasswordEditParam;
import com.dp.plat.service.PasswordService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.MailUtil;
import com.dp.plat.util.PasswordUtil;
/**
 * 作废的action
 * @author j01441
 *
 */
public class PasswordGetinfo extends BaseAction{
	private static final long serialVersionUID = 1L;
	private PasswordEditParam passwordEditParam;
	private PasswordService passwordService;
	private UserManageService userManageService;
	
	private Map<String, Object> result;
	
	public String executepwd() throws Exception {
		return SUCCESS;
	}
	public String editlogin(){
		if(passwordService.changelogin(passwordEditParam)){
		    HttpServletRequest request = getServletRequest();
		    request.getSession().setAttribute("Pwdoverdue", "0");
		    UserContext userContext = UserContext.getUserContext();
		    String defaultPage = userContext.getDefaultPage();
		    if (!userContext.isLogin()) {
                defaultPage = "Login.action";
            } else if (StringUtils.isBlank(defaultPage)) {
		        LoginDao loginDao = SpringContext.getBean("loginDao", LoginDao.class);
		        defaultPage = loginDao.queryUserDefaultPage(userContext.getUser().getId());
		    }
			try {
                getServletResponse().sendRedirect(request.getContextPath() + "/" + defaultPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
			setErrmsg(HttpContext.getMessage("sys.changepass.success"));
			return SUCCESS;
		} else {
			setErrmsg(HttpContext.getMessage("sys.changepass.error"));
			return SUCCESS;
		}
	}
	
	/**
     * 用户密码重置
     * 
     * @return
     */
    public String resetPassword() {
        // 密码重置
        String randomPassword = PasswordUtil.generatePass();
        User user = userManageService.queryUserByUserId(passwordEditParam.getId());
        user.setPassword(PasswordUtil.encryptMD5Password(randomPassword, user.getUsername()));
        user.setPwdoverdue(new Date());
        userManageService.updatepwdbyuser(user);
        
        // 邮件通知用户
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("templateCode", "userAddOrRestPwdMailInfo");
        context.put("title", "帐号密码已重置");
        context.put("realName", user.getRealName());
        context.put("userName", user.getUsername());
        context.put("randomPassword", randomPassword);
        context.put("content", context.get("title"));
        context.put("tos", user.getEmail());
        context.put("beforeSplit", "${");
        context.put("afterSplit", "}");
        MailUtil.keepMailWithTemplate(context, true);
        
        
        PasswordService passwordService = SpringContext.getBean("passwordService", PasswordService.class);
        passwordService.forcedOffline(user.getUsername());
        
        result = new HashMap<String, Object>();
        result.put("success", true);
        return SUCCESS;
    }
	
	public PasswordEditParam getPasswordEditParam() {
		return passwordEditParam;
	}
	public void setPasswordEditParam(PasswordEditParam passwordEditParam) {
		this.passwordEditParam = passwordEditParam;
	}
	public PasswordService getPasswordService() {
		return passwordService;
	}
	public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}
    public UserManageService getUserManageService() {
        return userManageService;
    }
    public void setUserManageService(UserManageService userManageService) {
        this.userManageService = userManageService;
    }
    public Map<String, Object> getResult() {
        return result;
    }
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
	
}
