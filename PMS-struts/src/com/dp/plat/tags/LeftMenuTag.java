package com.dp.plat.tags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.support.LeftMenu;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;

public class LeftMenuTag extends AbstractTag {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	
	private String menu;
	private String module; 
	private String group;
	private String function;

	public LeftMenuTag() {
		;
	}

	public int doEndTag() throws JspException {
	    // 获取装饰器当前页面的菜单项，进行权限校验
	    try {
	        String sitemeshPageId = "__sitemesh__page";
            Page sitemeshPage = (Page) this.pageContext.getAttribute(sitemeshPageId);
            if (sitemeshPage != null) {
                String menu = sitemeshPage.getProperty("meta.menu");
                String module = sitemeshPage.getProperty("meta.module");
                String group = sitemeshPage.getProperty("meta.group");
                String netmgroup = sitemeshPage.getProperty("meta.netmgroup");
                String supfunction = sitemeshPage.getProperty("meta.supfunction");
                String function = sitemeshPage.getProperty("meta.function");
                UserContext userContext = UserContext.getUserContext();
                if(!StringUtils.isAllBlank(group, netmgroup, supfunction, function) 
                        && !userContext.isHasPermission(group, function) 
                        && !userContext.isHasPermission(group, supfunction) 
                        && !userContext.isHasPermission(netmgroup, function) 
                        && !userContext.isHasPermission(netmgroup, supfunction)) {
                    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                    String servletPath = request.getServletPath();
                    String error404 = "/404.action";
                    if (!error404.equals(servletPath)) {
                        BasicDataService basicDataService = SpringContext.getBean("basicDataService", BasicDataService.class);
                        String paths = basicDataService.querySysArg("sys.menu.permission.exclude.urls");
                        // 是否是指定的例外链接
                        if (!isMatch(servletPath, paths)) {
                            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
                            String redirect = request.getContextPath() + error404;
                            response.sendRedirect(redirect);
                            JspWriter out = pageContext.getOut();
                            out.write("<script>window.location.href='" + redirect + "'</script>");
                            return TagSupport.SKIP_PAGE;
                        }
                    }
                }
            }
	    } catch (Exception e) {
	        e.printStackTrace();
        }
	    
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

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
	
    private boolean isMatch(String urlPath, String paths) {
        if (StringUtils.isBlank(paths) || StringUtils.isBlank(urlPath)) {
            return false;
        }
        LinkedHashSet<String> urls = new LinkedHashSet<String>(Arrays.asList(paths.split(",")));
        String url = urlPath;
        for (String pattern : urls) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
}
