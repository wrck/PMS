package com.dp.plat.security.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import com.dp.plat.security.context.HttpContext;

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
		html = html.replaceAll("&", "＆");
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
	
	public static String xssEncode(String s) {
	    if (s == null || s.isEmpty()) {
            return s;
        }
	    StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '>':
                    sb.append("&gt;");
//                    sb.append('＞');// 全角大于号
                    break;
                case '<':
                    sb.append("&lt;");
//                    sb.append('＜');// 全角小于号
                break;
//                case '\'':
//                    sb.append('‘');// 全角单引号
//                    break;
//                case '\"':
//                    sb.append('“');// 全角双引号
//                    break;
//                case '&':
//                    sb.append('＆');// 全角
//                    break;
//                case '\\':
//                    sb.append('＼');// 全角斜线
//                    break;
//                case '#':
//                    sb.append('＃');// 全角井号
//                    break;
                case '%': // < 字符的 URL 编码形式表示的 ASCII 字符（十六进制格式） 是: %3c
                    processUrlEncoder(sb, s, i);
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
	}
	
	public static void processUrlEncoder(StringBuilder sb, String s, int index) {
        if (s.length() >= index + 2) {
            if (s.charAt(index + 1) == '3' && (s.charAt(index + 2) == 'c' || s.charAt(index + 2) == 'C')) { // %3c, %3C
//                sb.append('＜');
                sb.append("&gt;");
                return;
            }
            if (s.charAt(index + 1) == '6' && s.charAt(index + 2) == '0') { // %3c (0x3c=60)
//                sb.append('＜');
                sb.append("&gt;");
                return;
            }
            if (s.charAt(index + 1) == '3' && (s.charAt(index + 2) == 'e' || s.charAt(index + 2) == 'E')) { // %3e, %3E
//                sb.append('＞');
                sb.append("&lt;");
                return;
            }
            if (s.charAt(index + 1) == '6' && s.charAt(index + 2) == '2') { // %3e (0x3e=62)
//                sb.append('＞');
                sb.append("&lt;");
                return;
            }
        }
        sb.append(s.charAt(index));
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
