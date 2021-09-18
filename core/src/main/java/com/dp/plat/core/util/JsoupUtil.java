package com.dp.plat.core.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import com.dp.plat.core.context.HttpContext;

public class JsoupUtil {

	public static Safelist getFormSafelist() {
		return Safelist.relaxed()
			.addTags("input", "select", "label")
			.addAttributes("input", "type", "name", "placeholder", "autocomplete", "data-flag", "data-src", "value", "checked")
			.addAttributes("select", "type", "name", "placeholder", "autocomplete", "data-flag", "data-src", "value", "selected")
			.addAttributes(":all", "id", "class", "style", "title", "width", "height", "align", "valign")
			.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
			.preserveRelativeLinks(true);
	}
	
	public static String escape(String html) {
		if (html == null) {
			return html;
		}
		html = HtmlUtils.htmlEscape(html, "UTF-8");
		return html;
	}

	public static String unescape(String html) {
		if (html == null) {
			return html;
		}
		html = html.replaceAll("＆", "&");
		html = HtmlUtils.htmlUnescape(html);
		return html;
	}

	/**
	 * 使用默认规则清理html
	 * @param html
	 * @defaultValue
	 *  <pre>
	 *      Safelist.relaxed().addAttributes(":all", "style", "title", "width", "height", "align", "valign")
	 *      		.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
	 *      		.preserveRelativeLinks(true)
	 *  </pre>
	 * @return
	 */
	public static String clean(String html) {
		return clean(html, HttpContext.baseUri());
	}

	/**
	 * 使用默认规则，清理html，传入baseUri，生效相对路径
	 * 
	 * @param html
	 * @param baseUri
	 * @defaultValue
	 *  <pre>
	 *      Safelist.relaxed().addAttributes(":all", "style", "title", "width", "height", "align", "valign")
	 *      		.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
	 *      		.preserveRelativeLinks(true)
	 *  </pre>
	 * @return
	 */
	public static String clean(String html, String baseUri) {
		return clean(html, baseUri,
				Safelist.relaxed().addAttributes(":all", "style", "title", "width", "height", "align", "valign")
						.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
						.preserveRelativeLinks(true));
	}

	public static String clean(String html, Safelist safelist) {
		return clean(html, HttpContext.baseUri(), safelist);
	}

	public static String clean(String html, String baseUri, Safelist safelist) {
		if (html == null) {
			return html;
		}
		html = unescape(html);
		html = Jsoup.clean(html, baseUri, safelist);
		return html;
	}
}
