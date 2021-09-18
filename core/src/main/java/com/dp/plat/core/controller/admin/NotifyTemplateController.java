/**
 * 
 */
package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.INotifyTemplateService;
import com.dp.plat.core.util.JsoupUtil;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;

/**
 * @author w02611
 *
 */
@Controller
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate")
public class NotifyTemplateController {

	@Resource
	private INotifyTemplateService notifyTemplateService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate";
	}

	@RequestMapping("/list")
	public String findAll(PageParam<Object> pageParam, NotifyTemplate data, Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(notifyTemplateService.countBySelective(null));
		pageParam.setFiltered(notifyTemplateService.countBySelectivePageable(pageParam));
		List<Object> dataList = new ArrayList<Object>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = notifyTemplateService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", dataList);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("模板编码", "templateCode"));
		columns.add(new DataTableColumn("模板标题", "subject"));
		columns.add(new DataTableColumn("模板内容", "content"));
//		columns.add(new DataTableColumn("开始有效时间", "effectiveFrom"));
//		columns.add(new DataTableColumn("开始失效时间", "effectiveTo"));
		columns.add(new DataTableColumn("状态", "effectiveFrom", "templateStatusRender"));
		pageParam.setColumns(columns);
		pageParam.setRowId("id");
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		NotifyTemplate template = notifyTemplateService.selectByPrimaryKey(id);
		model.addAttribute("template", template);
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate_detail";
	}

	@RequestMapping("/detail")
	public String create() {
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(NotifyTemplate template, Model model) {
		template.setCreateTime(new Date());
//		HttpSession currentSession = HttpContext.getCurrentSession();
//		if (!(currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC")))) {
//			String content = template.getContent();
//			template.setContent(HtmlUtils.htmlEscape(content));
//		} else if (currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC"))) {
//			String content = template.getContent();
//			content = content.replaceAll("＆", "&");
//			template.setContent(HtmlUtils.htmlUnescape(content));
//		}
		String content = StringUtils.trimToEmpty(template.getContent());
		HttpSession currentSession = HttpContext.getCurrentSession();
		if (!(currentSession != null && Boolean.TRUE.equals(currentSession.getAttribute("isSC")))) {
			content = JsoupUtil.clean(content, HttpContext.baseUri(), Safelist.relaxed()
					.addAttributes(":all", "style", "title", "width", "height", "align", "valign")
					.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
					.preserveRelativeLinks(true));
		} else if (currentSession != null && Boolean.TRUE.equals(currentSession.getAttribute("isSC"))) {
			content = JsoupUtil.unescape(content);
		}
		template.setContent(content);
		notifyTemplateService.insertSelective(template);
		model.addAttribute("id", template.getId());
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, NotifyTemplate template) {
		template.setUpdateTime(new Date());
//		HttpSession currentSession = HttpContext.getCurrentSession();
//		if (!(currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC")))) {
//			String content = template.getContent();
//			template.setContent(HtmlUtils.htmlEscape(content));
//		} else if (currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC"))) {
//			String content = template.getContent();
//			content = content.replaceAll("＆", "&");
//			template.setContent(HtmlUtils.htmlUnescape(content));
//		}
		String content = StringUtils.trimToEmpty(template.getContent());
		HttpSession currentSession = HttpContext.getCurrentSession();
		if (!(currentSession != null && Boolean.TRUE.equals(currentSession.getAttribute("isSC")))) {
			content = JsoupUtil.clean(content, HttpContext.baseUri(), Safelist.relaxed()
					.addAttributes(":all", "style", "title", "width", "height", "align", "valign")
					.addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
					.preserveRelativeLinks(true));
		} else if (currentSession != null && Boolean.TRUE.equals(currentSession.getAttribute("isSC"))) {
			content = JsoupUtil.unescape(content);
		}
		template.setContent(content);
		notifyTemplateService.updateByPrimaryKeySelective(template);
		return Consts.URLPath.SYSTEM_MANAGER + "notifyTemplate_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		notifyTemplateService.deleteByPrimaryKey(id);
	}

}
