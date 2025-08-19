package com.dp.plat.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * ScriptTag 类用于在 JSP 页面中插入 HTML <script> 标签。
 * 该标签支持内联脚本和外链脚本，并提供与原生 HTML script 标签一致的行为。
 */
public class ScriptTag extends AbstractHtmlElementBodyTag {

    // ===== 核心属性 =====

    /**
     * 脚本资源路径（本地或远程）。
     * 对应 HTML script 标签的 src 属性。
     */
    private String src;

    /**
     * 脚本类型，默认为 text/javascript。
     * 对应 HTML script 标签的 type 属性。
     */
    private String type;

    /**
     * 脚本语言。
     * 对应 HTML script 标签的 language 属性。
     */
    private String language;

    /**
     * 脚本字符集。
     * 对应 HTML script 标签的 charset 属性。
     */
    private String charset;

    // ===== 加载行为 =====

    /**
     * 是否异步加载脚本。
     * 对应 HTML script 标签的 async 属性。
     */
    private boolean async;

    /**
     * 是否延迟加载脚本。
     * 对应 HTML script 标签的 defer 属性。
     */
    private boolean defer;

    // ===== 安全与完整性 =====

    /**
     * Subresource Integrity (SRI) 值。
     * 对应 HTML script 标签的 integrity 属性。
     */
    private String integrity;

    /**
     * 跨域策略。
     * 对应 HTML script 标签的 crossorigin 属性。
     */
    private String crossorigin;

    /**
     * 引用策略。
     * 对应 HTML script 标签的 referrerpolicy 属性。
     */
    private String referrerpolicy;

    /**
     * 一次性使用令牌。
     * 对应 HTML script 标签的 nonce 属性。
     */
    private String nonce;

    // ===== 模块控制 =====

    /**
     * 是否忽略模块支持。
     * 对应 HTML script 标签的 nomodule 属性。
     */
    private boolean nomodule;

    // ===== 事件处理 =====

    /**
     * beforeload 事件处理程序。
     * 对应 HTML script 标签的 onbeforeload 属性。
     */
    private String onbeforeload;

    /**
     * load 事件处理程序。
     * 对应 HTML script 标签的 onload 属性。
     */
    private String onload;

    /**
     * error 事件处理程序。
     * 对应 HTML script 标签的 onerror 属性。
     */
    private String onerror;

    // ===== 性能优化 =====

    /**
     * fetchpriority 属性值，指定资源获取优先级。
     * 对应 HTML script 标签的 fetchpriority 属性。
     */
    private String fetchpriority;

    /**
     * 设置脚本资源路径（本地或远程）。
     * @param src 脚本资源路径
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * 设置脚本类型，默认为 text/javascript。
     * @param type 脚本类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置脚本语言。
     * @param language 脚本语言
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 设置脚本字符集。
     * @param charset 脚本字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 设置脚本是否异步加载。
     * @param async true 表示启用异步加载
     */
    public void setAsync(boolean async) {
        this.async = async;
    }

    /**
     * 设置脚本是否延迟加载。
     * @param defer true 表示启用延迟加载
     */
    public void setDefer(boolean defer) {
        this.defer = defer;
    }

    /**
     * 设置 Subresource Integrity (SRI) 值。
     * @param integrity SRI 值
     */
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    /**
     * 设置跨域策略。
     * @param crossorigin 跨域策略值
     */
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    /**
     * 设置引用策略。
     * @param referrerpolicy 引用策略值
     */
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    /**
     * 设置是否忽略模块支持。
     * @param nomodule true 表示忽略模块支持
     */
    public void setNomodule(boolean nomodule) {
        this.nomodule = nomodule;
    }

    /**
     * 设置一次性使用令牌。
     * @param nonce 一次性使用令牌
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * 设置 beforeload 事件处理程序。
     * @param onbeforeload beforeload 事件处理函数
     */
    public void setOnbeforeload(String onbeforeload) {
        this.onbeforeload = onbeforeload;
    }

    /**
     * 设置 load 事件处理程序。
     * @param onload load 事件处理函数
     */
    public void setOnload(String onload) {
        this.onload = onload;
    }

    /**
     * 设置 error 事件处理程序。
     * @param onerror error 事件处理函数
     */
    public void setOnerror(String onerror) {
        this.onerror = onerror;
    }

    /**
     * 设置 fetchpriority 属性值。
     * @param fetchpriority fetchpriority 属性值
     */
    public void setFetchpriority(String fetchpriority) {
        this.fetchpriority = fetchpriority;
    }

    @Override
    public int doStartTag() throws JspException {
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            BodyContent bodyContent = getBodyContent();

            StringBuilder sb = buildScriptTag();

            // 获取标签体内容并追加
            if (bodyContent != null) {
                sb.append(bodyContent.getString());
            }

            // 输出闭合标签
            sb.append("</script>");

            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException("Error processing script tag: " + e.getMessage(), e);
        }
        return EVAL_PAGE;
    }

    @Override
    public int doAfterBody() throws JspException {
        return super.doAfterBody();
    }

    /**
     * 构建 script 标签的起始部分（包含所有属性）。
     * @return 已构建完成的 script 标签起始字符串
     */
    private StringBuilder buildScriptTag() {
        StringBuilder sb = new StringBuilder();
        sb.append("<script");

        // 类型和语言
        if (type != null && !type.isEmpty()) sb.append(" type=\"").append(type).append("\"");
        if (language != null && !language.isEmpty()) sb.append(" language=\"").append(language).append("\"");
        if (charset != null && !charset.isEmpty()) sb.append(" charset=\"").append(charset).append("\"");

        // 异步与延迟加载
        if (async) sb.append(" async");
        if (defer) sb.append(" defer");

        // 完整性校验
        if (integrity != null && !integrity.isEmpty()) sb.append(" integrity=\"").append(integrity).append("\"");
        if (crossorigin != null && !crossorigin.isEmpty()) sb.append(" crossorigin=\"").append(crossorigin).append("\"");

        // 模块控制
        if (nomodule) sb.append(" nomodule");
        if (referrerpolicy != null && !referrerpolicy.isEmpty()) sb.append(" referrerpolicy=\"").append(referrerpolicy).append("\"");
        if (nonce != null && !nonce.isEmpty()) sb.append(" nonce=\"").append(nonce).append("\"");

        // 事件处理
        if (onbeforeload != null && !onbeforeload.isEmpty()) sb.append(" onbeforeload=\"").append(onbeforeload).append("\"");
        if (onload != null && !onload.isEmpty()) sb.append(" onload=\"").append(onload).append("\"");
        if (onerror != null && !onerror.isEmpty()) sb.append(" onerror=\"").append(onerror).append("\"");

        // 新增 fetchpriority 属性
        if (fetchpriority != null && !fetchpriority.isEmpty()) sb.append(" fetchpriority=\"").append(fetchpriority).append("\"");

        // 构建 src 属性
        if (src != null && !src.isEmpty()) {
            sb.append(" src=\"").append(addVersionToUrl(src)).append("\"");
        }

        // 添加自定义属性
        writeOptionalAttributes(sb);

        sb.append(">");
        return sb;
    }
}