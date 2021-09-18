package com.dp.plat.action;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.ServletContextAware;

import com.dp.plat.service.BaseService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 本项目所有Action的基类1
 * 
 * @author admin
 * 
 */
public class BaseAction extends ActionSupport implements ServletContextAware,
		ServletRequestAware, ServletResponseAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errmsg = "";
	private ServletContext servletContext;
	private HttpServletResponse servletResponse;
	private HttpServletRequest servletRequest;
	
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * 默认显示方法
	 * 
	 * @return 默认返回INPUT
	 */
	public String start() {
		return INPUT;
	}

	public void setErrmsg(String errmsg) {
		if (null != errmsg && errmsg.trim().equals("") == false) {
			this.addFieldError("errmsg", errmsg);
			this.errmsg = errmsg;
		}
	}

	protected void setErrmsg(BaseService service) {
		for (String msg : service.getErrmsg()) {
			this.addFieldError("errmsg", msg);
		}
		for (String warnMsg : service.getWarnmsg()) {
			this.addFieldError("warnmsg", warnMsg);
		}
	}

	protected void setWarnMessage(BaseService service) {
		for (String msg : service.getWarnmsg()) {
			this.addFieldError("warnmsg", msg);
		}
	}

	public List<String> getErrmsg(BaseService service) {
		return service.getErrmsg();
	}

	public String getErrmsg() {
		return errmsg;
	}

	@Override
	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}

	public ServletContext setServletContext() {
		return this.servletContext;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.servletRequest = request;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.servletResponse = response;
	}

/*	public String getUploadExcelPath() {
		//return getServletContext().getRealPath("/upload");
		return getServletContext().getRealPath("/" + UploadFileUtil.UPLOAD_PARH);
	}*/

	@Override
	public void addActionError(String anErrorMessage) {
		super.addActionError(anErrorMessage);
	}

	@Override
	public void addFieldError(String fieldName, String errorMessage) {
		super.addFieldError(fieldName, errorMessage);
	}

	@Override
	public void addActionMessage(String aMessage) {
		super.addActionMessage(aMessage);
	}

}
