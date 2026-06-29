package com.dp.plat.core.tags;

import com.dp.plat.core.config.SystemConfig;
import org.springframework.util.CollectionUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHtmlElementBodyTag extends BodyTagSupport implements DynamicAttributes {

    /**
     * 动态属性集合，用于存储动态属性。
     */
    protected Map<String, Object> dynamicAttributes;

    /**
     * 输出动态属性。
     *
     * @param sb
     * @throws JspException
     */
    protected void writeOptionalAttributes(StringBuilder sb) {
        if (!CollectionUtils.isEmpty(this.dynamicAttributes)) {
            for (String attr : this.dynamicAttributes.keySet()) {
                sb.append(" ").append(attr);
                Object value = this.dynamicAttributes.get(attr);
                if (value != null) {
                    sb.append("=\"").append(value).append("\"");
                }
            }
        }
    }

    /**
     * 在url后添加版本号
     * @param url 需要添加版本号的URL
     * @return 添加版本号后的URL
     */
    protected String addVersionToUrl(String url) {
        return addVersionToUrl(url, null, null);
    }

    /**
     * 在url后添加版本号
     * @param url 需要添加版本号的URL
     * @param version 版本号
     * @return 添加版本号后的URL
     */
    protected String addVersionToUrl(String url, Object version) {
        return addVersionToUrl(url, null, version);
    }

    /**
     * 在url后添加版本号
     * @param url 需要添加版本号的URL
     * @param versionKey 版本号键
     * @param version 版本号
     * @return 添加版本号后的URL
     */
    protected String addVersionToUrl(String url, String versionKey, Object version) {
        if (url == null || url.isEmpty()) return url;

        boolean isExternal = url.startsWith("http://") || url.startsWith("https://");
        boolean isAbsolutePath = url.startsWith("/");
        StringBuilder sb = new StringBuilder(url);

        // 只有是绝对路径且不是外部 URL 时才加 contextPath
        if (!isExternal && isAbsolutePath) {
            String contextPath = getContextPath();
            if (!url.startsWith(contextPath) && !"/".equals(contextPath)) {
                sb.insert(0, contextPath);
            }
        }

        if (url.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        versionKey = versionKey == null ? "version" : versionKey;
        if (version == null) {
            version = SystemConfig.systemVariables.getOrDefault("sys.script.version", "1");
        }
        
        sb.append(versionKey).append("=").append(version);
        return sb.toString();
    }

    /**
     * Called when a tag declared to accept dynamic attributes is passed
     * an attribute that is not declared in the Tag Library Descriptor.
     *
     * @param uri       the namespace of the attribute, or null if in the default
     *                  namespace.
     * @param localName the name of the attribute being set.
     * @param value     the value of the attribute
     *
     * @throws JspException if the tag handler wishes to
     *                      signal that it does not accept the given attribute.  The
     *                      container must not call doStartTag() or doTag() for this tag.
     */
    @Override
    public void setDynamicAttribute(String uri, String localName, Object value ) throws JspException {
        if (this.dynamicAttributes == null) {
            this.dynamicAttributes = new HashMap<String, Object>();
        }
        if (!isValidDynamicAttribute(localName, value)) {
            return;
        }
        dynamicAttributes.put(localName, value);
    }

    /**
     * Whether the given name-value pair is a valid dynamic attribute.
     */
    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return true;
    }

    /**
     * Associate a value with a String key.
     *
     * @param key The key String.
     * @param value The value to associate.
     */
    @Override
    public void setValue(String key, Object value) {
        super.setValue(key, value);
        if (this.dynamicAttributes == null) {
            this.dynamicAttributes = new HashMap<String, Object>();
        }
        this.dynamicAttributes.put(key, value);
    }

    /**
     * 获取当前请求的上下文路径。
     * @return 当前请求的上下文路径
     */
    protected String getContextPath() {
        return ((javax.servlet.http.HttpServletRequest) pageContext.getRequest()).getContextPath();
    }
}
