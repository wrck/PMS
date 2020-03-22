package com.dp.plat.action;

import com.dp.plat.context.HttpContext;
import com.dp.plat.param.PasswordEditParam;
import com.dp.plat.service.PasswordService;
/**
 * 作废的action
 * @author j01441
 *
 */
@Deprecated
public class PasswordGetinfo extends BaseAction{
	private static final long serialVersionUID = 1L;
	private PasswordEditParam passwordEditParam;
	private PasswordService passwordService;
	
	public String executepwd() throws Exception {
		return SUCCESS;
	}
	public String editlogin(){
		if(passwordService.changelogin(passwordEditParam)){
			getServletRequest().getSession().setAttribute("Pwdoverdue", "0");
			setErrmsg(HttpContext.getMessage("sys.changepass.success"));
			return SUCCESS;
		} else {
			setErrmsg(HttpContext.getMessage("sys.changepass.error"));
			return SUCCESS;
		}
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
	
	
	
}
