package com.dp.plat.core.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.io.IOException;

/**
 * StyleTag 类用于在 JSP 页面中插入 HTML <style> 标签。
 * 该标签支持内联 CSS 样式，并提供与原生 HTML style 标签一致的行为。
 */
public class StyleTag extends AbstractHtmlElementBodyTag {

    // ===== 核心属性 =====

    /**
     * 样式表类型，默认为 text/css。
     * 对应 HTML style 标签的 type 属性。
     */
    private String type;

    /**
     * 样式表媒体类型。
     * 对应 HTML style 标签的 media 属性。
     */
    private String media;

    /**
     * 样式表标题。
     * 对应 HTML style 标签的 title 属性。
     */
    private String title;

    /**
     * 是否启用替代样式表。
     * 对应 HTML style 标签的 disabled 属性。
     */
    private boolean alternate;

    /**
     * 是否禁用样式表。
     * 对应 HTML style 标签的 disabled 属性。
     */
    private boolean disabled;

    // ===== 其他属性 =====

    /**
     * 样式表的完整性校验值。
     * 对应 HTML style 标签的 integrity 属性。
     */
    private String integrity;

    /**
     * 跨域策略。
     * 对应 HTML style 标签的 crossorigin 属性。
     */
    private String crossorigin;

    /**
     * 一次性使用令牌。
     * 对应 HTML style 标签的 nonce 属性。
     */
    private String nonce;

    /**
     * 设置样式表类型，默认为 text/css。
     * @param type 样式表类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置样式表媒体类型。
     * @param media 媒体类型
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * 设置样式表标题。
     * @param title 样式表标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置是否启用替代样式表。
     * @param alternate true 表示启用替代样式表
     */
    public void setAlternate(boolean alternate) {
        this.alternate = alternate;
    }

    /**
     * 设置是否禁用样式表。
     * @param disabled true 表示禁用样式表
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
     * 设置一次性使用令牌。
     * @param nonce 一次性使用令牌
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * 处理标签体内容并在页面上输出最终的 <style> 标签。
     * @return int 表示后续处理方式
     * @throws JspException 如果处理过程中发生错误
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<style");

            // 类型
            if (type != null && !type.isEmpty()) sb.append(" type=\"").append(type).append("\"");
            // 媒体类型
            if (media != null && !media.isEmpty()) sb.append(" media=\"").append(media).append("\"");
            // 标题
            if (title != null && !title.isEmpty()) sb.append(" title=\"").append(title).append("\"");
            // 替代样式表
            if (alternate) sb.append(" alternate");
            // 禁用状态
            if (disabled) sb.append(" disabled");
            // 完整性校验
            if (integrity != null && !integrity.isEmpty()) sb.append(" integrity=\"").append(integrity).append("\"");
            // 跨域策略
            if (crossorigin != null && !crossorigin.isEmpty()) sb.append(" crossorigin=\"").append(crossorigin).append("\"");
            // 一次性使用令牌
            if (nonce != null && !nonce.isEmpty()) sb.append(" nonce=\"").append(nonce).append("\"");

            writeOptionalAttributes(sb);

            sb.append(">");


            // 直接获取标签体内容
            String content = getBodyContent().getString();
            if (content != null && !content.isEmpty()) {
                sb.append(content);
            }

            sb.append("</style>");

            // 输出最终结果
            JspWriter out = pageContext.getOut();
            out.print(sb.toString());

            // 继续处理标签后的内容
            return EVAL_PAGE;
        } catch (IOException e) {
            throw new JspException("Error processing style tag: " + e.getMessage(), e);
        }
    }
}