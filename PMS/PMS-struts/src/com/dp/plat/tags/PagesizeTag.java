package com.dp.plat.tags;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.opensymphony.xwork2.util.ValueStack;

public class PagesizeTag extends TagSupport
{

	private String displayParam;
	private String formid;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8149121927665835421L;

	public PagesizeTag()
	{
		;
	}

	public int doStartTag() throws JspException
	{
		return TagSupport.SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		try
		{

            HttpServletRequest request = (HttpServletRequest) pageContext .getRequest();
            JspWriter out = pageContext.getOut();

        	ValueStack vs = (ValueStack)request.getAttribute("struts.valueStack");
        	int pagesize=(Integer)vs.findValue(displayParam+".pagesize");

			out.print("<a href=\"javascript: document.getElementById(\'"+displayParam+
					".pagesize\').value=10;document.getElementById(\'"+formid+
					"\').submit();\">"+(pagesize==10?"10":"[10]")+"</a> ");
			out.print("<a href=\"javascript: document.getElementById(\'"+displayParam+
					".pagesize\').value=50;document.getElementById(\'"+formid+
					"\').submit();\">"+(pagesize==50?"50":"[50]")+"</a> ");
			out.print("<a href=\"javascript: document.getElementById(\'"+displayParam+
					".pagesize\').value=100;document.getElementById(\'"+formid+
					"\').submit();\">"+(pagesize==100?"100":"[100]")+"</a> ");
			out.print("<a href=\"javascript: document.getElementById(\'"+displayParam+
					".pagesize\').value=500;document.getElementById(\'"+formid+
					"\').submit();\">"+(pagesize==500?"500":"[500]")+"</a> ");
			out.print(" <input type=\"hidden\" name=\""+displayParam+".pagesize\" id=\"pagesize\" value=\""+pagesize+"\"> ");
//			out.flush();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return TagSupport.EVAL_PAGE;
	}

	public void release()
	{
		super.release();
	}

	public String getDisplayParam()
	{
		return displayParam;
	}

	public void setDisplayParam(String displayParam)
	{
		this.displayParam = displayParam;
	}

	public String getFormid()
	{
		return formid;
	}

	public void setFormid(String formid)
	{
		this.formid = formid;
	}

}
