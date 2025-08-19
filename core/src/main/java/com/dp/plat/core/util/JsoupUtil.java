package com.dp.plat.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

public class JsoupUtil {

	public static Safelist getFormSafelist() {
		Safelist safelist = Safelist.relaxed()
				.addTags("input", "select", "label", "option")
			.addAttributes("input", "type", "name", "placeholder", "autocomplete", "data-flag", "data-src", "value", "checked")
			.addAttributes("select", "type", "name", "placeholder", "autocomplete", "data-flag", "data-src", "value", "selected")
			.addAttributes(":all", "id", "class", "style", "title", "width", "height", "align", "valign")
			.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
			.preserveRelativeLinks(true);

		return extraConfigSafelist(safelist, "form");
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
		safelist = extraConfigSafelist(safelist, "clean");
		html = Jsoup.clean(html, baseUri, safelist);
		return html;
	}

	/**
	 * 从配置添加额外的配置性
	 * @param safelist
	 * @param type
	 * @return
	 */
	public static Safelist extraConfigSafelist(Safelist safelist, String type) {
		// 解析配置并应用额外配置
		try {
			JSONObject config = JSON.parseObject(SystemConfig.systemVariables.getOrDefault("sys.jsoup.safelist.config", "{}"));
			JSONObject json = config.getJSONObject(type);
			if (json.containsKey("addTags")) {
				JSONArray addTags = json.getJSONArray("addTags");
				String[] tags = addTags.toArray(new String[0]);
				safelist.addTags(tags);
			}
			if (json.containsKey("addAttributes")) {
				JSONObject attrs = json.getJSONObject("addAttributes");
				for (String key : attrs.keySet()) {
					JSONArray addAttributes = attrs.getJSONArray(key);
					String[] attributes = addAttributes.toArray(new String[0]);
					safelist.addAttributes(key, attributes);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		return safelist;
	}
}
