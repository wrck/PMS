package com.dp.plat.tags;

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import com.dp.plat.context.VContext;
import com.opensymphony.xwork2.util.ValueStack;

public class BarPercentTag extends TagSupport
{
	private String value;
	private static final long serialVersionUID = 1L;

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

        	Double val = (Double)vs.findValue(value);
			DecimalFormat df = new DecimalFormat("0.###");
			Long v = (long) (val * 3 / 4);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("perwidth", v);
			map.put("percent", df.format(val));
			map.put("floor", val >= Double.MAX_VALUE);

			VContext
					.getVM(out, "com/dp/plat/vmpage/BarPercent.vm", map);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return TagSupport.EVAL_PAGE;
	}
    
	public static void outHtml(Double val, Writer out)

	{

		DecimalFormat df = new DecimalFormat("0.###");

		Long v = (long) (val * 3 / 4);

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("perwidth", v);

		map.put("percent", df.format(val));

		map.put("floor", val >= Double.MAX_VALUE);

		VContext.getVM(out, "com/dp/plat/vmpage/BarPercent.vm", map);

	}
	
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

}
