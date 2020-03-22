package com.dp.plat.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.dp.plat.context.SpringContext;
import com.dp.plat.support.LeftMenu;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;

public class LeftMenuTag extends AbstractTag {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public LeftMenuTag() {
		;
	}

	public int doEndTag() throws JspException {
		try {
			LeftMenu menu = (LeftMenu) SpringContext.getBean("SysLeftMenu");
			menu.drow(pageContext);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return TagSupport.EVAL_PAGE;
	}

	public void release() {
		super.release();
	}
}
