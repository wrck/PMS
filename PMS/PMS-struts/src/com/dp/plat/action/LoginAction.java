package com.dp.plat.action;


import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

import com.dp.plat.context.HttpContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.param.LoginParam;
import com.dp.plat.service.LoginService;
import com.dp.plat.service.UserManageService;

public class LoginAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	
	LoginParam user;
	
	LoginService loginService;
	
	UserManageService  userManageService;
	
	String redirecturl;
	

	@Override
    public String start() {
	    UserContext userContext = UserContext.getUserContext();
	    if (userContext.isLogin() && StringUtils.isNotBlank(userContext.getDefaultPage())) {
	        this.setRedirecturl(userContext.getDefaultPage());
	        return SUCCESS;
	    }
        return super.start();
    }

    @Override
	public String execute()throws Exception{
		if(UserContext.getUserContext().isCas()){
			return this.casLogin();
		}else {
			return this.noCasLogin();
		}
	}
	
	private String casLogin(){
		String ip = new String();
		try
		{
			ip = HttpContext.getRemoteAddr();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//cas获取用户名
		Assertion casAssertion= AssertionHolder.getAssertion();
		if(casAssertion==null){
			loginService.logout();
			return "errorCas";
		}
		
		String userName = casAssertion.getPrincipal().getName();
		if(userName==null||userName.equals("")){
			loginService.logout();
			return "errorCas";
		}
		
		user=new LoginParam();
		user.setUsername(userName);
		
		// 保存cas登录前的页面
		String defaultPage = loginService.getUserContext().getDefaultPage();
		if (!loginService.loginCas(user,ip))
		{
			loginService.logout();
			setErrmsg(loginService);
			return "errorCas";
		}
		System.out.println("-------------success------");
		
//		this.setRedirecturl(loginService.getUserContext().getDefaultPage());
		if (StringUtils.isBlank(defaultPage)) {
			defaultPage = loginService.getUserContext().getDefaultPage();
		}
		this.setRedirecturl(defaultPage);
		
		return SUCCESS;
	}
	
	private String noCasLogin(){
		String ip = new String();
		try
		{
			ip = HttpContext.getRemoteAddr();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// 保存登录之前的页面
		String defaultPage = loginService.getUserContext().getDefaultPage();
		if (!loginService.login(user,ip))
		{
			setErrmsg(loginService);
			return INPUT;
		}
		System.out.println("-------------success------");

//		this.setRedirecturl(loginService.getUserContext().getDefaultPage());
		if (StringUtils.isBlank(defaultPage)) {
			defaultPage = loginService.getUserContext().getDefaultPage();
		}
		this.setRedirecturl(defaultPage);
		
		return SUCCESS;
	}
	
	public String logout()throws Exception{
	    
		if(UserContext.getUserContext().isCas()){ 
		    
//		    AuthenticationFilter casFilter = SpringContext.getBean(AuthenticationFilter.class);
//		    String casServerLoginUrl = String.valueOf(ReflectionUtils.getField(ReflectionUtils.findField(casFilter.getClass(), "casServerLoginUrl"), casFilter));
//		    casServerLoginUrl = StringUtils.defaultIfBlank(casServerLoginUrl, "https://cas.dptech.com:8443/");
//		    URL uri = new URI(casServerLoginUrl).toURL();
            String url = UserContext.getUserContext().getUrl();
			String urlString="https://cas.dptech.com:8443/logout?service="+url.substring(0, url.lastIndexOf("/") + 1) + "Login.action";
			this.setRedirecturl(urlString);
		}else{
			this.setRedirecturl("index.jsp");
		}
		loginService.logout();
		return SUCCESS;
	}
	
	public String error404() {
	    return SUCCESS;
	}
	
	public String getRedirecturl() {
		return redirecturl;
	}

	public void setRedirecturl(String redirecturl) {
		this.redirecturl = redirecturl;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public LoginParam getUser() {
		return user;
	}
	public void setUser(LoginParam user) {
		this.user = user;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}
	
}
