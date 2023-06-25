package com.dp.plat.tags;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.dp.plat.context.VContext;
import com.dp.plat.exception.CustomRuntimeException;
import com.opensymphony.xwork2.util.ValueStack;

public class ErrorMsgTag extends TagSupport
{
	private String accesskey;
	private Boolean onlyone = false;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ErrorMsgTag(){
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
            JspWriter out = pageContext.getOut();//输出到JSP页面，调用out.print()

        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("accesskey", accesskey);
        	map.put("onlyone", onlyone);
        	ValueStack vs = (ValueStack)request.getAttribute("struts.valueStack");
        	if (vs != null) {
        	    map.put("fieldErrors", vs.findValue("fieldErrors"));
        	    
        	    // 配置的全局异常处理捕获的异常，判断是否是自定义抛出或者常见的Runtime异常
        	    Object globalException = vs.findValue("exception");
        	    if (globalException != null) {
        	        Class[] customThrowExceptions = new Class[] {CustomRuntimeException.class, RuntimeException.class};
        	        Throwable throwable = (Throwable) globalException;
        	        for (Class type : customThrowExceptions) {
        	            Throwable exception = ExceptionUtils.throwableOfType(throwable, type);
        	            if (exception != null) {
        	                throwable = exception;
        	                break;
        	            }
                    }
        	        map.put("globalException", ExceptionUtils.getStackTrace(throwable));
        	    }
        	}
        	

        	VContext.getVM(out, "com/dp/plat/vmpage/ErrorMsgTag.vm", map);

//        	out.flush();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        //表示JSP页面继续运行
        return TagSupport.EVAL_PAGE;
    }

    public void release()
    {
        super.release();
    }

	public String getAccesskey()
	{
		return accesskey;
	}

	public void setAccesskey(String accesskey)
	{
		this.accesskey = accesskey;
	}

	public Boolean getOnlyone()
	{
		return onlyone;
	}

	public void setOnlyone(Boolean onlyone)
	{
		this.onlyone = onlyone;
	}
}
