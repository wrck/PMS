package com.dp.plat.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.service.IDataExportService;
import com.dp.plat.core.util.ExportUtils;
import com.dp.plat.core.util.LinkedHashMapSort;
import com.dp.plat.core.vo.PageParam;

/**
 * 数据导出控制器
 * 
 * @author w02611
 *
 */
@RequestMapping("/export")
@Controller
public class DataExportController {

	@Resource
	private IDataExportService dataExportService;
	
	@RequestMapping("/showExportColumns")
	public String showExportColumns(String objectName, String objectKV, String pageParamKV,
			String fullServiceName, Model model) throws ClassNotFoundException {
		model.addAttribute("objectName", objectName);
		model.addAttribute("objectKV", objectKV);
		model.addAttribute("pageParamKV", pageParamKV);
		model.addAttribute("fullServiceName", fullServiceName);
		if (StringUtils.isNotBlank(objectName)) {
			//增加查询导出有无动态列的问题，先从数据库查询一次动态列数据，根据有无增加进入不同的方法解析
			Map<String,String> dynamicColumn = dataExportService.queryDynamicColumn(objectName);
			Map<String, String> columns = ExportUtils.getExportColumns(objectName,dynamicColumn);
			//查询字段排序
			String columnSort = dataExportService.queryDynamicColumnSort(objectName);
			
			model.addAttribute("columns", LinkedHashMapSort.sort(columnSort, columns));
		}
		return "/base/showExportColumns";
	}

	@RequestMapping("/selectExportColumns")
	public String selectExportColumns(String objectName, Model model) throws ClassNotFoundException {
		//增加查询导出有无动态列的问题，先从数据库查询一次动态列数据，根据有无增加进入不同的方法解析
		Map<String,String> dynamicColumn = dataExportService.queryDynamicColumn(objectName);
		Map<String, String> columns = ExportUtils.getExportColumns(objectName,dynamicColumn);
		model.addAttribute("columns", columns);
		return "/base/showExportColumns";
	}
	
	@RequestMapping(value = "/dataExport", method = RequestMethod.POST)
	public String dataExport(String objectName, String objectKV, String pageParamKV, String columns, 
			String fullServiceName,Model model) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException {
		PageParam<?> pageParam = ExportUtils.getPageParam(objectName, objectKV, pageParamKV);
//		String classz = "com.dp.plat.core.service.impl.DataExportService";
		List<?> dataInfos = Collections.emptyList();
		try {
		Class<?> t_class =  Class.forName(fullServiceName);
		Class<?> o_class = ExportUtils.getClass(objectName);
		Method m = t_class.getMethod("export" + o_class.getSimpleName(), PageParam.class);
		
			dataInfos = (List<?>) m.invoke(SpringContext.getBean(t_class), pageParam);
		} catch (Exception e) {
			Method method = dataExportService.getClass().getMethod("export" + objectName, PageParam.class);
		
			dataInfos = (List<?>) method.invoke(dataExportService, pageParam);
		}
		model.addAttribute("data", dataInfos);
		if(StringUtils.isNotBlank(columns)){
			model.addAttribute("columns", columns.split(";"));
		}
		//增加查询导出有无动态列的问题，先从数据库查询一次动态列数据，根据有无增加进入不同的方法解析
		Map<String,String> dynamicColumn = dataExportService.queryDynamicColumn(objectName);
		model.addAttribute("dynamicColumns", dynamicColumn);
		return "orderDataExport";
	}
}
