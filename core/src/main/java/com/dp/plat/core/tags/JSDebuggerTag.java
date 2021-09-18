package com.dp.plat.core.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.RoleConstant;

/**
 * 禁用浏览器开发者工具
 * 
 * @author w02611
 *
 */
public class JSDebuggerTag extends TagSupport {

	private static final long serialVersionUID = -8877023339963565901L;

	private static final String REDIRECT = "redirect";

	private static final String REWRITE = "rewrite";

	private static final String CALLBACK = "callback";

	/**
	 * 禁用 console.clear 函数，防止脚本清屏阻断监测
	 */
	private boolean clear = true;

	/**
	 * 是否开启定时 debugger 反爬虫审查
	 */
	private boolean debug = true;

	/**
	 * 定时 debugger 时间间隔（毫秒）
	 */
	private long debugTime = 1000;

	/**
	 * 处理方式
	 */
	private String type;
	/**
	 * 处理方式的内容，redirect：传入 开启控制台后重定向地址，write：传入 开启控制台后重写 document.body 内容，支持传入节点或字符串，callback：传入开启控制台后的回调函数
	 */
	private String content;

	/**
	 * optionsJSON格式
	 */
	private String options;

	@SuppressWarnings("static-access")
	@Override
	public int doEndTag() throws JspException {
		boolean isAdmin = UserContext.hasAnyRoles(RoleConstant.ROLE_ADMIN);
		Boolean disableJSDebugger = Boolean.parseBoolean(SystemConfig.systemVariables.getOrDefault("sys.disable.js.debugger", "true"));
		if (!Boolean.TRUE.equals(disableJSDebugger) || isAdmin) {
			return super.EVAL_PAGE;
		}
		String ignoreUrl = SystemConfig.systemVariables.getOrDefault("sys.disable.js.debugger.url.ignore", ".*\\/(404|500|unauthorized)([.|?][^./?]*)?$");
		HttpServletRequest currentRequest = HttpContext.getCurrentRequest();
		String contextPath = currentRequest.getContextPath();
		String servletPath = currentRequest.getServletPath();
		if (servletPath.matches(ignoreUrl)) {
			return super.EVAL_PAGE;
		}
		StringBuilder script = new StringBuilder();
		if (REDIRECT.equals(type) && StringUtils.isNotBlank(content)) {
			if(content.contains("?")) {
				content += "&illegal=JSDebugger&fromUrl=" + servletPath; 
			} else {
				content += "?illegal=JSDebugger&fromUrl=" + servletPath; 
			}
		}
		script.append("<script src=\"").append(contextPath).append("/static/plugins/console-ban/console-ban.min.js\"></script>");
		script.append("<script>\r\n")
			.append("	ConsoleBan.init({\r\n")
			.append("	clear:").append(clear).append(",\r\n")
			.append("	debug:").append(debug).append(",\r\n")
			.append("	debugTime:").append(debugTime).append(",\r\n")
			.append("	").append(type).append(":'").append(content).append("',\r\n")
			.append("});")
			.append("</script>");
		JspWriter out = pageContext.getOut();
		try {
			out.print(script);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public long getDebugTime() {
		return debugTime;
	}

	public void setDebugTime(long debugTime) {
		this.debugTime = debugTime;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}
	
}
