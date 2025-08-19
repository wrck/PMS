package com.dp.plat.core.tags;

import com.dp.plat.core.config.SystemConfig;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * LinkTag 类用于在 JSP 页面中插入 HTML <link> 标签。
 * 该标签支持外部资源链接，并提供与原生 HTML link 标签一致的行为。
 * <p>
 * 注意：由于 HTML 的 <link> 标签是空元素（self-closing tag），因此本类不支持标签体内容。
 * 同时支持对外部资源 URL 自动追加版本号参数（?version=x.x.x）以提高缓存控制能力。
 */
public class LinkTag extends AbstractHtmlElementBodyTag {

    // ===== 核心属性 =====

    /**
     * 链接的 URL。
     * 对应 HTML link 标签的 href 属性。
     */
    private String href;

    /**
     * 链接资源的 MIME 类型。
     * 对应 HTML link 标签的 type 属性。
     */
    private String type;

    /**
     * 链接关系类型（如：stylesheet、icon 等）。
     * 对应 HTML link 标签的 rel 属性。
     */
    private String rel;

    /**
     * 媒体类型或媒介查询。
     * 对应 HTML link 标签的 media 属性。
     */
    private String media;

    /**
     * 链接的标题。
     * 对应 HTML link 标签的 title 属性。
     */
    private String title;

    // ===== 其他常用属性 =====

    /**
     * 链接的目标框架或窗口。
     * 对应 HTML link 标签的 target 属性。
     */
    private String target;

    /**
     * 链接的引用来源策略。
     * 对应 HTML link 标签的 referrerpolicy 属性。
     */
    private String referrerpolicy;

    /**
     * 跨域请求策略。
     * 对应 HTML link 标签的 crossorigin 属性。
     */
    private String crossorigin;

    /**
     * Subresource Integrity (SRI) 值。
     * 对应 HTML link 标签的 integrity 属性。
     */
    private String integrity;

    /**
     * 一次性使用令牌。
     * 对应 HTML link 标签的 nonce 属性。
     */
    private String nonce;

    /**
     * 是否启用替代样式表。
     * 对应 HTML link 标签的 alternate 属性。
     */
    private boolean alternate;

    /**
     * 设置链接的 URL。
     * @param href 链接的 URL
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * 设置链接资源的 MIME 类型。
     * @param type MIME 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置链接关系类型（如：stylesheet、icon 等）。
     * @param rel 链接关系类型
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * 设置链接的媒体类型或媒介查询。
     * @param media 媒体类型或媒介查询
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * 设置链接的标题。
     * @param title 链接的标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置链接的目标框架或窗口。
     * @param target 目标框架或窗口
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 设置链接的引用来源策略。
     * @param referrerpolicy 引用来源策略
     */
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    /**
     * 设置跨域请求策略。
     * @param crossorigin 跨域请求策略
     */
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    /**
     * 设置 Subresource Integrity (SRI) 值。
     * @param integrity SRI 值
     */
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    /**
     * 设置一次性使用令牌。
     * @param nonce 一次性使用令牌
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * 设置是否启用替代样式表。
     * @param alternate true 表示启用替代样式表
     */
    public void setAlternate(boolean alternate) {
        this.alternate = alternate;
    }

    /**
     * 输出最终的 <link> 标签。
     * 因为 <link> 是一个空元素（self-closing tag），所以不支持也不需要标签体内容。
     * 
     * 此外，对于外链资源会自动追加版本号参数（?version=x.x.x）以提高缓存控制能力。
     * 版本号取自 application.properties 文件中的 application.version 属性。
     * 
     * @throws JspException 如果处理过程中发生错误
     * @return
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<link");

            // type
            if (type != null && !type.isEmpty()) sb.append(" type=\"").append(type).append("\"");
            // rel
            if (rel != null && !rel.isEmpty()) sb.append(" rel=\"").append(rel).append("\"");
            // media
            if (media != null && !media.isEmpty()) sb.append(" media=\"").append(media).append("\"");
            // title
            if (title != null && !title.isEmpty()) sb.append(" title=\"").append(title).append("\"");
            // target
            if (target != null && !target.isEmpty()) sb.append(" target=\"").append(target).append("\"");
            // referrerpolicy
            if (referrerpolicy != null && !referrerpolicy.isEmpty()) sb.append(" referrerpolicy=\"").append(referrerpolicy).append("\"");
            // crossorigin
            if (crossorigin != null && !crossorigin.isEmpty()) sb.append(" crossorigin=\"").append(crossorigin).append("\"");
            // integrity
            if (integrity != null && !integrity.isEmpty()) sb.append(" integrity=\"").append(integrity).append("\"");
            // nonce
            if (nonce != null && !nonce.isEmpty()) sb.append(" nonce=\"").append(nonce).append("\"");
            // alternate
            if (alternate) sb.append(" alternate");

            // href
            if (href != null && !href.isEmpty()) {
                sb.append(" href=\"").append(addVersionToUrl(href)).append("\"");
            }

            writeOptionalAttributes(sb);

            sb.append(">");

            // 输出最终结果
            JspWriter out = pageContext.getOut();
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException("Error processing link tag: " + e.getMessage(), e);
        }
        return EVAL_PAGE;
    }
}