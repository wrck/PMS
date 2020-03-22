package com.dp.plat.core.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 绘制左侧菜单
 * @author j01441
 *
 */
public class LeftMenuTag extends TagSupport{
	
	private String node;
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@SuppressWarnings("static-access")
	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.print(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}
}
