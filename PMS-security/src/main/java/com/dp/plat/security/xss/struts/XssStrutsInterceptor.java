package com.dp.plat.security.xss.struts;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.dp.plat.security.util.JsoupUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;

public class XssStrutsInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 8642204240305659814L;

    private String excludes;
    private Set<String> excludeUrls;
    
    private String encodes;
    private Set<String> encodeUrls;
    
    private String cleans;
    private Set<String> cleanUrls;
    private String enable;
    private boolean enabled;

    @Override 
    public void init() {
        super.init();
        // 将不需要xss过滤的接口添加到列表中
        if (StringUtils.hasText(excludes)) {
            String[] urls = excludes.split(",");
            excludeUrls = excludeUrls != null ? excludeUrls : new LinkedHashSet<String>();
            for (String url : urls) {
                excludeUrls.add(url);
            }
        }
        // 将需要转义xss过滤的接口添加到列表中
        if (StringUtils.hasText(encodes)) {
            String[] urls = encodes.split(",");
            encodeUrls = encodeUrls != null ? encodeUrls : new LinkedHashSet<String>();
            for (String url : urls) {
                encodeUrls.add(url);
            }
        }
        // 将需要清理xss过滤的接口添加到列表中
        if (StringUtils.hasText(cleans)) {
            String[] urls = cleans.split(",");
            cleanUrls = cleanUrls != null ? cleanUrls : new LinkedHashSet<String>();
            for (String url : urls) {
                cleanUrls.add(url);
            }
        }
        if (StringUtils.hasText(enable)) {
            enabled = Boolean.valueOf(enable);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext actionContext = invocation.getInvocationContext();
        String servletPath = null;
        try {
            HttpServletRequest servletRequest = (HttpServletRequest) actionContext.get("com.opensymphony.xwork2.dispatcher.HttpServletRequest");
            servletPath = servletRequest.getServletPath();
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (isExcludeUrl(servletPath)) {
            return invocation.invoke();
        }
        // 是否清理
        boolean isClean = isMatch(servletPath, this.cleanUrls);
//        // 是否转义
//        boolean isEncode = isMatch(servletPath, this.encodeUrls) || !isClean;
        
//        // Struts 2.5
//        Object parameters = actionContext.getParameters();
//        if (parameters instanceof HttpParameters) {
//            HttpParameters httpParameters = (HttpParameters) parameters;
//            for (Entry<String, Parameter> entry : httpParameters.entrySet()) {
//                Object parameter = entry.getValue();
//                if (parameter instanceof Parameter) {
//                    Parameter param = (Parameter) parameter;
//                    if (!param.isMultiple() && param.isDefined()) {
//                        String value = param.getValue();
//                        String escape = JsoupUtil.escape(param.getValue());
//                        if (!value.equals(escape)) {
//                            entry.setValue(new Parameter.Request(param.getName(), escape));
//                        }
//                    }
//                }
//            }
//        }
        // Struts 2.3
        Map<String, Object> httpParameters = actionContext.getParameters();
        for (Entry<String, Object> entry : httpParameters.entrySet()) {
            Object parameter = entry.getValue();
            if (parameter instanceof String[]) {
                String[] strArr = (String[]) parameter;
                for (int i = 0; i < strArr.length; i++) {
                     String param = strArr[i];
                     strArr[i] = isClean ? JsoupUtil.clean(param) : JsoupUtil.xssEncode(param);
                }
                entry.setValue(strArr);
            } else if (parameter instanceof String){
                String param = parameter.toString();
                param = isClean ? JsoupUtil.clean(param, JsoupUtil.getFormSafelist()) : JsoupUtil.xssEncode(param);
                entry.setValue(param);
            } else {
                System.out.println(parameter);
                entry.setValue(parameter);
            }
        }
        return invocation.invoke();
    }

    private boolean isExcludeUrl(String urlPath) {
        return isMatch(urlPath, this.excludeUrls);
//        if (!enabled) {
//            return true;
//        }
//        if (!StringUtils.hasText(excludes) || !StringUtils.hasText(urlPath)) {
//            return false;
//        }
//        String url = urlPath;
//        for (String pattern : excludeUrls) {
//            Pattern p = Pattern.compile("^" + pattern);
//            Matcher m = p.matcher(url);
//            if (m.find()) {
//                return true;
//            }
//        }
//        return false;
    }
    
    private boolean isMatch(String urlPath, Set<String> paths) {
        if (!enabled) {
            return true;
        }
        if (paths == null || paths.isEmpty() || !StringUtils.hasText(urlPath)) {
            return false;
        }
        String url = urlPath;
        for (String pattern : paths) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public Set<String> getExcludeUrls() {
        return excludeUrls;
    }
    
    /**
     * 将不需要xss过滤的接口添加到列表
     * @param excludeUrls
     */
    public void setExcludeUrls(String excludeUrls) {
        // 将转义的接口添加到列表
        if (StringUtils.hasText(excludeUrls)) {
            this.excludeUrls = TextParseUtil.commaDelimitedStringToSet(excludeUrls);
//            String[] urls = excludeUrls.split(",");
//            this.excludeUrls = new LinkedHashSet<String>();
//            for (String url : urls) {
//                this.excludeUrls.add(url);
//            }
        }
    }

//    public void setExcludeUrls(Set<String> excludeUrls) {
//        this.excludeUrls = excludeUrls;
//    }
    
    public Set<String> getEncodeUrls() {
        return encodeUrls;
    }
    
    /**
     * 将需要转义的接口添加到列表
     * @param encodeUrls
     */
    public void setEncodeUrls(String encodeUrls) {
        // 将转义的接口添加到列表
        if (StringUtils.hasText(encodeUrls)) {
            this.encodeUrls = TextParseUtil.commaDelimitedStringToSet(encodeUrls);
//            String[] urls = encodeUrls.split(",");
//            this.encodeUrls = new LinkedHashSet<String>();
//            for (String url : urls) {
//                this.encodeUrls.add(url);
//            }
        }
    }

//    public void setEncodeUrls(Set<String> encodeUrls) {
//        this.encodeUrls = encodeUrls;
//    }

    public Set<String> getCleanUrls() {
        return cleanUrls;
    }

    /**
     * 将需要清理的接口添加到列表
     * @param cleanUrls
     */
    public void setCleanUrls(String cleanUrls) {
        // 将转义的接口添加到列表
        if (StringUtils.hasText(cleanUrls)) {
            this.cleanUrls = TextParseUtil.commaDelimitedStringToSet(cleanUrls);
//            String[] urls = cleanUrls.split(",");
//            this.cleanUrls = new LinkedHashSet<String>();
//            for (String url : urls) {
//                this.cleanUrls.add(url);
//            }
        }
    }
    
//    public void setCleanUrls(Set<String> cleanUrls) {
//        this.cleanUrls = cleanUrls;
//    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    
}
