package com.dp.plat.pms.springmvc.controller;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;

public class AbstractController<Service extends IAbstractBaseService<T>, T, V> extends BaseController {

	private static final String TEMPLATE_NAMESPACE = "template/";
	private static String MODEL;
	private static String VIEW_NAMESPACE;
	private static String DATANAME_FORM;
	private static String DATANAME_TABLE;
	private static String DATANAME_NAVTAB;

	private Boolean useTemplate = true;
	private String viewModel;

	@PostConstruct
	private void init() {
		String namespace = getTargetName(getTClass());
		MODEL = namespace;
		this.setViewModel(MODEL);
		VIEW_NAMESPACE = namespace + "/";
		DATANAME_FORM = namespace + "Form";
		DATANAME_TABLE = namespace + "List";
		DATANAME_NAVTAB = namespace + "Tab";
	}

	@Autowired
	protected Service service;

	@RequestMapping
	public String home(Model model) {
		model.addAttribute("model", getViewModel());
		return getViewNameSpace() + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, V facilitator, Model model) {
		List<Object> list = Collections.emptyList();
		try {
			// Principal user = UserContext.getCurrentPrincipal();
			// facilitator.setCompId(user.getCompId());
			PageParam<Object> tempParam = new PageParam<>();
			V temp = getVClass().newInstance();
			// temp.setCompID(user.getCompId());
			tempParam.setModel(temp);
			pageParam.setModel(facilitator);
	
			pageParam.setTotal(service.countBySelectivePageable(tempParam));
			pageParam.setFiltered(service.countBySelectivePageable(pageParam));
			list = service.selectBySelectivePageable(pageParam);
	
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(DATANAME_TABLE);
		pageParam.setColumns(columns);
		return getViewNameSpace() + "list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			T facilitator = service.selectByPrimaryKey(id);
			if (facilitator != null) {
				model.addAttribute("targetValue", facilitator);

				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(DATANAME_NAVTAB);
				model.addAttribute("tabList", navTavList);
			}
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = { "/detail", "/modals/detail" })
	public String detail(T t, Model model) {
		if (HttpContext.isJSON()) {
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
			model.addAttribute("model", getViewModel());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(V v, Model model) {
		Boolean status = true;
		String message = null;
		try {
			service.insertSelective((T) v);
			model.addAttribute("targetName", this.getTargetName(v.getClass()));
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, V v, Model model) {
		Boolean status = true;
		String message = null;
		try {
			service.updateByPrimaryKeySelective((T) v);
			model.addAttribute("targetName", this.getTargetName(v.getClass()));
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			service.deleteByPrimaryKey(id);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	protected String getTargetName(Class<?> cls) {
		String targetName = cls.getSimpleName();
		String temp = new String(new char[] { targetName.charAt(0) });
		targetName = targetName.replaceFirst(temp, temp.toLowerCase());
		return targetName;
	}

	public Class<T> getTClass() {
		Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		return tClass;
	}

	public Class<V> getVClass() {
		Class<V> tClass = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
		return tClass;
	}

	private String getViewNameSpace() {
		if (Boolean.TRUE.equals(useTemplate)) {
			return TEMPLATE_NAMESPACE;
		} else {
			return VIEW_NAMESPACE;
		}
	}

	public void setUseTemplate(Boolean useTemplate) {
		this.useTemplate = useTemplate;
	}

	public String getViewModel() {
		return viewModel;
	}

	public void setViewModel(String viewModel) {
		this.viewModel = viewModel;
	}
	
}