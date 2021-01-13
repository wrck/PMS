package com.dp.plat.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import com.dp.plat.context.VContext;

public class RefreshTag extends TagSupport
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4144487353121992818L;

	private int intv = 30;

    public String getInterval()
    {
        return Integer.toString(intv);
    }

    public void setInterval(String interval)
    {
        this.intv = Integer.parseInt(interval);
    }
    
	public RefreshTag()
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
        	String autoref = request.getParameter("autoref");
        	Integer interval = null;
        	try
        	{
        		interval = Integer.valueOf(request.getParameter("interval"));
        	}
        	catch(Exception e)
        	{
        		;
        	}
			
            if(null != interval)
            {
                intv = interval;
            }
            String page = VContext.getVM(
                    "com/dp/plat/vmpage/Refresh.vm", 
                    "interval", intv,
                    "autoref", "true".equals(autoref)==true
                    );
            out.println(page);
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
}
