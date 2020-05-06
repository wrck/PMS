package com.dp.plat.pms.springmvc.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;

public abstract class AbstractController<Service extends IAbstractBaseService<T>, T, V> extends BaseController {

	private final ThreadLocal<Map<String, Object>> localVariables = new ThreadLocal<Map<String, Object>>();

	private static final String TEMPLATE_NAMESPACE = "template/";
	private String MODEL;
	protected String URL_NAMESPACE;
	protected String VIEW_NAMESPACE;
	protected String DATANAME_FORM;
	protected String DATANAME_TABLE;
	protected String DATANAME_NAVTAB;

	private Boolean useTemplate = true;
	private String viewModel;
	private String keyword;

	@Autowired
	protected ICommonRelatedDataService commonRelatedDataService;

	@PostConstruct
	private void init() {
		String namespace = getTargetName(getTClass());
		MODEL = namespace;
		keyword = StringUtils.defaultIfBlank(this.keyword, "id");
		this.setViewModel(MODEL);
	}

	@Autowired
	protected Service service;

	@ModelAttribute
	public void initModelAttr(Integer id, V v, HttpServletRequest httpRequest, Model model) {
		model.addAttribute("urlNamespace", URL_NAMESPACE);
		model.addAttribute("model", getViewModel());
		model.addAttribute("keyword", getKeyword());

		String servletPath = httpRequest.getServletPath();
		model.addAttribute("isModals", servletPath.contains("/modals/"));
		
		model.addAttribute("permissions", UserContext.getCurrentPrincipal().getPermissions());
	}

	@RequestMapping
	public String home(Model model) {
		if (!checkPermission(null, model, getDataName() + ":list")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		model.addAttribute("urlNamespace", URL_NAMESPACE);
		model.addAttribute("model", getViewModel());
		model.addAttribute("keyword", getKeyword());
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		List<Object> list = Collections.emptyList();
		try {
			// Principal user = UserContext.getCurrentPrincipal();
			// v.setCompId(user.getCompId());
			PageParam<Object> tempParam = new PageParam<>();
			V temp = getVClass().newInstance();
			// temp.setCompID(user.getCompId());
			tempParam.setModel(temp);
			pageParam.setModel(v);

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

		List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
		pageParam.setColumns(columns);
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(newInstance(getVClass(), keyword, id), model,
				getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			T v = service.selectByPrimaryKey(id);
			if (v != null) {
				model.addAttribute("targetValue", v);

				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(getDataNameNavTab());
				model.addAttribute("tabList", navTavList);
			}
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = { "/detail", "/modals/detail" })
	public String detail(V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			model.addAttribute("targetValue", v);

			List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
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
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":update")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
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
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(newInstance(getVClass(), keyword, id), model, getDataName() + ":update")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
		}
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

	/**
	 * 导入入口页面
	 * 
	 * @return
	 */
	@GetMapping("/modals/import")
	public String toImport(V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":import")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		return TEMPLATE_NAMESPACE + "/import";
	}

	/**
	 * 报告数据调整
	 * 
	 * @return
	 */
	@PostMapping("/import/preview")
	public String importPreview(V v, String excelPath, Model model) {
		if (!checkPermission(v, model, getDataName() + ":import")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		Result result = null;
		Map<String, Object> params = new HashMap<String, Object>();
		List<DataTableColumn> columnList = findColumnList(getDataNameTable());
		params.put("columns", columnList);
		params.put("targetValue", v);
		
		boolean useTempTable = !Boolean.FALSE.equals(Boolean.parseBoolean(HttpContext.getCurrentRequest().getParameter("useTempTable")));
		params.put("useTempTable", useTempTable);
		try {
			Method method = service.getClass().getMethod("importPreview", Map.class, String.class);
			result = (Result) method.invoke(service, params, excelPath);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			result = new Result(false, "不支持导入功能");
		}
		model.mergeAttributes(result.getMap());
		return getRealViewNameSpace() + "import";
	}

	/**
	 * 预览临时表数据
	 * 
	 * @return
	 */
	@RequestMapping("/previewTempTable")
	public String previewTempTable(String tempTableName, PageParam<Object> pageParam, V v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":import")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		List<DataTableColumn> columnList = findColumnList(getDataNameTable());
		pageParam.setColumns(columnList);
		pageParam.setModel(v);
		List<?> data;
		Result result;
		try {
			Method method = service.getClass().getMethod("selectTempImportData", String.class, PageParam.class);
			data = (List<?>) method.invoke(service, tempTableName, pageParam);
			result = new Result(true, data);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			result = new Result(false, "不支持导入功能");
		}
		model.mergeAttributes(result.getMap());
		return getRealViewNameSpace() + "import";
	}

	/**
	 * 删除临时表
	 * 
	 * @return
	 */
	@RequestMapping("/dropTempTable")
	public String dropTempTable(String tempTableName, Model model) {
		try {
			Method method = service.getClass().getMethod("dropTempTable", String.class);
			method.invoke(service, tempTableName);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
		}
		return getRealViewNameSpace() + "import";
	}

	/**
	 * 报告数据调整
	 * 
	 * @return
	 */
	@PostMapping("/import/submit")
	public String importSubmit(V v, String excelPath, Model model) {
		if (!checkPermission(v, model, getDataName() + ":import")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		Result result = null;
		Map<String, Object> params = new HashMap<String, Object>();
		List<DataTableColumn> columnList = findColumnList(getDataNameTable());
		params.put("columns", columnList);
		params.put("targetValue", v);
		try {
			Method method = service.getClass().getMethod("importSubmit", Map.class, String.class);
			result = (Result) method.invoke(service, params, excelPath);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			result = new Result(Boolean.FALSE, "不支持导入功能");
			ExceptionHandler.insertException(e);
		}
		model.mergeAttributes(result.getMap());
		return getRealViewNameSpace() + "import";
	}

	/**
	 * 报告数据调整
	 * 
	 * @return
	 */
	@PostMapping("/import/submitTempTable")
	public String submitTempTable(V v, String tempTableName, String columns, Model model) {
		if (!checkPermission(v, model, getDataName() + ":import")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		Result result;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Method method = service.getClass().getMethod("submitTempTable", Map.class, String.class, Collection.class);
			result = (Result) method.invoke(service, params, tempTableName, JSON.parseArray(columns, String.class));
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			result = new Result(false, "不支持导入功能");
		}
		model.mergeAttributes(result.getMap());
		return getRealViewNameSpace() + "import";
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

	protected void setUrlNameSpace(String urlNameSpace) {
		if (StringUtils.isNotBlank(urlNameSpace)) {
			this.URL_NAMESPACE = urlNameSpace;
		}
	}

	protected void setViewNameSpace(String viewNameSpace) {
		if (StringUtils.isNotBlank(viewNameSpace)) {
			this.VIEW_NAMESPACE = viewNameSpace;
		}
	}

	protected String getViewNameSpace() {
		return VIEW_NAMESPACE;
	}

	protected static String getTemplateNamespace() {
		return TEMPLATE_NAMESPACE;
	}

	/**
	 * useTemplate:false 并且设置了VIEW_NAMESPACE则返回VIEW_NAMESPACE，否则返回TEMPLATE_NAMESPACE
	 * 
	 * @return
	 */
	protected String getRealViewNameSpace() {
		if (Boolean.TRUE.equals(useTemplate) || StringUtils.isBlank(VIEW_NAMESPACE)) {
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
		VIEW_NAMESPACE = viewModel + "/";
		DATANAME_FORM = viewModel + "Form";
		DATANAME_TABLE = viewModel + "List";
		DATANAME_NAVTAB = viewModel + "Tab";
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDataName() {
		String dataPrefix = (String) this.getLocalVariables("dataPrefix");
		if (StringUtils.isNotBlank(dataPrefix)) {
			return dataPrefix;
		}
		return viewModel;
	}
	
	public String getDataNameForm() {
		String dataPrefix = (String) this.getLocalVariables("dataPrefix");
		if (StringUtils.isNotBlank(dataPrefix)) {
			return dataPrefix + "_" + DATANAME_FORM;
		}
		return DATANAME_FORM;
	}

	public String getDataNameTable() {
		String dataPrefix = (String) this.getLocalVariables("dataPrefix");
		if (StringUtils.isNotBlank(dataPrefix)) {
			return dataPrefix + "_"  + DATANAME_TABLE;
		}
		return DATANAME_TABLE;
	}

	public String getDataNameNavTab() {
		String dataPrefix = (String) this.getLocalVariables("dataPrefix");
		if (StringUtils.isNotBlank(dataPrefix)) {
			return dataPrefix + "_" + DATANAME_NAVTAB;
		}
		return DATANAME_NAVTAB;
	}

	/**
	 * @Description: 设置线程参数 @param key @param value @return void @throws
	 */
	public void setLocalVariables(String key, Object value) {
		Map<String, Object> map = localVariables.get();
		if (map == null) {
			map = new ConcurrentHashMap<>();
		}
		map.put(key, value);
		localVariables.set(map);
	}

	/**
	 * @Description: 设置线程参数 @param map @return void @throws
	 */
	public void setLocalVariables(Map<String, Object> map) {
		localVariables.set(map);
	}

	/**
	 * @Description: 获取线程参数 @param @return String @throws
	 */
	public Object getLocalVariables() {
		return localVariables.get();
	}

	/**
	 * @Description: 获取线程参数 @param @return String @throws
	 */
	public Object getLocalVariables(String key) {
		Map<String, Object> map = localVariables.get();
		Object var = null;
		if (map != null) {
			var = map.get(key);
		}
		return var;
	}

	/**
	 * @Description: 清空线程参数 @param @return void @throws
	 */
	public void clearLocalVariables() {
		localVariables.remove();
	}

	public boolean checkPermission(V v, Model model, String... permissions) {
		if (!UserContext.checkPermission(permissions)) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return false;
		}
		model.addAttribute("permissionType", "all");
		return true;
	}

	protected V newInstance(Class<?> clazz, Object... kvs) {
		if (clazz == null) {
			return null;
		}
		Object obj = null;
		try {
			obj = clazz.newInstance();
			if (kvs != null) {
				for (int i = 0; i < kvs.length; i += 2) {
					try {
						String key = (String) kvs[i];
						Object value = kvs[i + 1];
						if (StringUtils.isBlank(key) || value == null) {
							continue;
						}
						key = key.substring(0, 1).toUpperCase() + key.substring(1);
						Method method = clazz.getMethod("set" + key, value.getClass());
						method.invoke(obj, value);
					} catch (NoSuchMethodException e) {
					}
				}
			}
		} catch (Exception e) {
		}
		return (V) obj;
	}
}