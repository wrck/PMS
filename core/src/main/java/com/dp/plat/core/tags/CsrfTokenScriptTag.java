package com.dp.plat.core.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.shiro.SecurityUtils;

import com.dp.plat.security.csrf.CSRFTokenManager;

public class CsrfTokenScriptTag extends BodyTagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			// 输出value值
			StringBuilder text = new StringBuilder();
			text.append("<script type=\"text/javascript\">");
			text.append(" var ");		
			text.append(CSRFTokenManager.CSRF_PARAM_NAME);
			text.append(" = '");
			text.append(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME));	
			text.append( "'</script>");
			pageContext.getOut().println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}
 
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

}
