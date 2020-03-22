package com.dp.plat.tags;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.dp.plat.context.VContext;
import com.opensymphony.xwork2.util.ValueStack;

public class TimeSheetTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8557045952790625840L;

	private String name;
	private String value;

	public TimeSheetTag() {
		;
	}

	public int doStartTag() throws JspException {
		return TagSupport.SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			ValueStack vs = (ValueStack) request
					.getAttribute("struts.valueStack");

			String timesheet_name = name;
			Object timesheet_value = "";
			if (value != null && value.equals("") == false) {
				timesheet_value = (Object) vs.findValue(value);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("timesheet_name", timesheet_name);
			map.put("timesheet_value", timesheet_value);
			map.put("vs", vs);
			VContext.getVM(out, "com/dp/plat/vmpage/timesheet.vm", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TagSupport.EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
