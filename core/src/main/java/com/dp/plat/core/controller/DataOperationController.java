package com.dp.plat.core.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.druid.DbType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dp.plat.core.config.RoutingDataSource;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.entity.DataOperation;
import com.dp.plat.core.exception.ExcelImportException;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IDataOperationService;
import com.dp.plat.core.util.ExportUtils;
import com.dp.plat.core.util.JsoupUtil;
import com.dp.plat.core.util.PropertyUtil;
import com.dp.plat.core.util.SQLParser;
import com.dp.plat.core.util.SQLParser.SqlParserResult;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

/**
 * 数据操作控制器，主要用于数据导入导出的操作
 * 
 * @author w02611
 *
 */
@Controller
@RequestMapping("data")
public class DataOperationController {
    private static final String IMPORT_URL = "/import";
    private static final String EXPORT_URL = "/export";
    
    @Autowired
    private IDataOperationService dataOperationService;

    @RequestMapping
    public String listView() {
        return "/data/data_operation_list";
    }

    @RequestMapping("/list")
    public String list(PageParam<Object> pageParam , DataOperation dataOperation, Model model) {
        Principal user = UserContext.getCurrentPrincipal();
        List<Object> dataOperations = new ArrayList<>();
        PageParam<Object> tempPageParam = new PageParam<>();
        String empPower = null;
        if (!UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
            empPower = user.getUserInfoId().toString();
        }
        DataOperation temp = new DataOperation();
        temp.setEmpPower(empPower);
        tempPageParam.setModel(temp);
        pageParam.setTotal(dataOperationService.countBySelectivePageable(tempPageParam));
        
        dataOperation.setEmpPower(empPower);
        pageParam.setModel(dataOperation);
        
        if (pageParam.getPageSize() == -1L) {
            pageParam.setPageSize(pageParam.getTotal());
        } else {
            pageParam.setFiltered(dataOperationService.countBySelectivePageable(pageParam));
        }
        dataOperations = dataOperationService.selectBySelectivePageable(pageParam);
        model.addAttribute("data", dataOperations);
        return "/data/data_operation_list";
    }

    @RequestMapping("{id}")
    public String findOne(@PathVariable("id") Integer id, Model model) {
    	if(!checkPermission(null)) {
        	return Consts.VIEW_UNAUTHORIZED;
        }
        DataOperation dataOperation = dataOperationService.selectByPrimaryKey(id);
        model.addAttribute("data", dataOperation);
        return "/data/data_operation_detail";
    }

    @RequestMapping("/detail")
    public String create(Model model) {
        DataOperation dataOperation = new DataOperation();
        if(!checkPermission(dataOperation)) {
        	return Consts.VIEW_UNAUTHORIZED;
        } 
        return "/data/data_operation_detail";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public String create(DataOperation dataOperation, Model model, RedirectAttributes redirectAttributes) {
    	if(!checkPermission(dataOperation)) {
    		model.addAttribute("status", false);
    		model.addAttribute("error", "没有权限进行该操作！");
        	return Consts.VIEW_UNAUTHORIZED;
        } 
    	boolean status = true;
    	String error = "";
    	if (dataOperation.getType() == 0) {
    		String sql = StringUtils.trimToEmpty(dataOperation.getScript());
////    		sql = sql.replaceAll("＆", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
//    		sql = JsoupUtil.unescape(JsoupUtil.clean(sql, Safelist.none()));
//    		String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
//    		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
//    		Matcher matcher = pattern.matcher(sql);
//    		if (matcher.find()) {
//    			String fromUrl = HttpContext.getCurrentRequest().getServletPath();
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", "参数不合法！");
//    			redirectAttributes.addFlashAttribute("status", false);
//    			redirectAttributes.addFlashAttribute("error", "参数不合法！");
//    			return "redirect:/illegal.json?illegal=SQLInject&fromUrl=" + fromUrl;
//    		}
//    		String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
//    		SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_operation_detail";
//    		}
//    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
//    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_operation_detail";
//    		}
    		Result result = checkSql(sql);
    		if (!result.isSuccess()) {
    			model.addAttribute("status", result.isSuccess());
    			model.addAttribute("error", result.getMessage());
    			redirectAttributes.addFlashAttribute("status", result.isSuccess());
    			redirectAttributes.addFlashAttribute("error", result.getMessage());
    			return (String) result.getData();// 返回view
    		}
    		dataOperation.setScript((String) result.getData());
    	}
    	dataOperation.setFormHtml(JsoupUtil.clean(dataOperation.getFormHtml(), JsoupUtil.getFormSafelist()));
        dataOperationService.insertSelective(dataOperation);
        model.addAttribute("id", dataOperation.getId());
        model.addAttribute("status", status);
		model.addAttribute("error", error);
        return "/data/data_operation_detail";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public String update(@PathVariable("id") Integer id, DataOperation dataOperation, Model model, RedirectAttributes redirectAttributes) {
    	if(!checkPermission(null)) {
        	return Consts.VIEW_UNAUTHORIZED;
        }
    	boolean status = true;
    	String error = "";
    	if (dataOperation.getType() == 0) {
    		String sql = StringUtils.trimToEmpty(dataOperation.getScript());
////    		sql = sql.replaceAll("＆", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
//    		sql = JsoupUtil.unescape(sql);
//    		String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
//    		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
//    		Matcher matcher = pattern.matcher(sql);
//    		if (matcher.find()) {
//    			String fromUrl = HttpContext.getCurrentRequest().getServletPath();
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", "参数不合法！");
//    			redirectAttributes.addFlashAttribute("status", false);
//    			redirectAttributes.addFlashAttribute("error", "参数不合法！");
//    			return "redirect:/illegal.json?illegal=SQLInject&fromUrl=" + fromUrl;
//    		}
//    		String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
//    		SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_operation_detail";
//    		}
//    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
//    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_operation_detail";
//    		}
    		Result result = checkSql(sql);
    		if (!result.isSuccess()) {
    			model.addAttribute("status", result.isSuccess());
    			model.addAttribute("error", result.getMessage());
    			redirectAttributes.addFlashAttribute("status", result.isSuccess());
    			redirectAttributes.addFlashAttribute("error", result.getMessage());
    			return (String) result.getData();// 返回view
    		}
    		dataOperation.setScript((String) result.getData());
    	}
		dataOperation.setFormHtml(JsoupUtil.clean(dataOperation.getFormHtml(), JsoupUtil.getFormSafelist()));
        dataOperationService.updateByPrimaryKeySelective(dataOperation);
        model.addAttribute("status", status);
		model.addAttribute("error", error);
        return "/data/data_operation_detail";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
    	if(!checkPermission(null)) {
        	return;
        }
        dataOperationService.deleteByPrimaryKey(id);
    }
    
    /**
     * 导入用服一评得分
     * @param appraiserExcel
     * @param response
     * @param map
     * @throws IOException
     */
    @RequestMapping(IMPORT_URL)
    public String importForm(String operationName, Model model) {
        model.addAttribute("operationName", operationName);
        DataOperation dataOperation = dataOperationService.selectByOperationName(operationName);
        if(!checkPermission(dataOperation)) {
        	model.addAttribute("error", "没有权限进行该操作！");
        } else if (dataOperation != null) {
            String formHtml = StringUtils.trimToEmpty(dataOperation.getFormHtml());
            String script = StringUtils.trimToEmpty(dataOperation.getScript());
//            formHtml = formHtml.replaceAll("<[^>]+>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//            script = script.replaceAll("<[^>]+>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//            formHtml = JsoupUtil.clean(formHtml);
            script = JsoupUtil.clean(script);
            model.addAttribute("formHtml", formHtml);
            model.addAttribute("script", script);
        } else {
            model.addAttribute("error", "该数据操作不存在！");
        }
        return "/data/data_import_operation";
    }
    
    /**
     * 导入用服一评得分
     * @param appraiserExcel
     * @param response
     * @param map
     * @throws IOException
     */
    @RequestMapping(IMPORT_URL + "/{operationName}")
    public void importObjectives(@PathVariable("operationName") String operationName, @RequestParam(value = "fileExcel") MultipartFile appraiserExcel, HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String errorMessage = "";
        if (!appraiserExcel.isEmpty()) {
            String fileType = appraiserExcel.getOriginalFilename()
                    .substring(appraiserExcel.getOriginalFilename().lastIndexOf("."));
            if (fileType.equals(".xlsx")) {
                try {
                    DataOperation dataOperation = dataOperationService.selectByOperationName(operationName);
                    if(!checkPermission(dataOperation)) {
                    	throw new RuntimeException("没有权限进行该操作！");
                    } 
                    String clazzStr = dataOperation.getClazz();
                    String methodStr = dataOperation.getMethod();
                    
                    String parameterTypesStr = StringUtils.trimToEmpty(dataOperation.getParameterTypes());
                    String[] parameterTypesArr = StringUtils.split(parameterTypesStr);
                    Class<?>[] parameterTypes = new Class<?>[parameterTypesArr.length + 1];
                    parameterTypes[0] = MultipartFile.class;
                    for (int i = 0; i < parameterTypesArr.length; i++) {
                        String parameterType = parameterTypesArr[i];
                        parameterTypes[i + 1] = Class.forName(parameterType);
                    }
                    
                    Object service = SpringContext.getBean(clazzStr);
                    Method method = service.getClass().getDeclaredMethod(methodStr, parameterTypes);
                    Object errorResult = null;
                    if (parameterTypes.length != 1) {
                        errorResult = method.invoke(service, appraiserExcel, request.getParameterMap());
                    } else {
                        errorResult = method.invoke(service, appraiserExcel);
                    }
                    if (errorResult != null) {
                        if (errorResult instanceof List) {
                            //errorMessage = JSON.toJSONString(errorResult);
                            JSONArray jsonArray =  (JSONArray) JSON.toJSON(errorResult);
                            for (Object jsonObject : jsonArray) {
                                ((JSONObject)jsonObject).remove("stackTrace");
                                ((JSONObject)jsonObject).remove("@type");
                                ((JSONObject)jsonObject).remove("localizedMessage");
                                ((JSONObject)jsonObject).remove("suppressed");
                                ((JSONObject)jsonObject).remove("cause");
                            }
                            errorMessage = JSONArray.toJSONString(jsonArray, SerializerFeature.WriteMapNullValue);
                        }
                    }
                    //appraiserRelationshipService.importAppraisersExcel(appraiserExcel);
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    if (e instanceof InvocationTargetException) {
                        errorMessage = ((InvocationTargetException) e).getTargetException().getMessage();
                        if (((InvocationTargetException) e).getTargetException() instanceof ExcelImportException) {
                            map.put("progress", ((ExcelImportException) ((InvocationTargetException) e).getTargetException()).getProgress());
                        }
                    }
                    if (e instanceof ExcelImportException) {
                        map.put("progress", ((ExcelImportException) e).getProgress());
                    }
                }
            } else {
                errorMessage = "文件格式错误";
            }
        } else {
            errorMessage = "文件为空";
        }
        map.put("errorMessage", errorMessage);
        out.write(JSON.toJSONString(map));
    }
    
    /**
     * 导入用服一评得分
     * @param appraiserExcel
     * @param response
     * @param map
     * @throws IOException
     */
    @RequestMapping(EXPORT_URL)
    public String exportForm(String operationName, Model model) {
        model.addAttribute("operationName", operationName);
        DataOperation dataOperation = dataOperationService.selectByOperationName(operationName);
        if(!checkPermission(dataOperation)) {
        	model.addAttribute("error","没有权限进行该操作！");
        } else if (dataOperation != null) {
            String formHtml = StringUtils.trimToEmpty(dataOperation.getFormHtml());
            String script = StringUtils.trimToEmpty(dataOperation.getScript());
            String columns = StringUtils.trimToEmpty(dataOperation.getColumns());
            
//            formHtml = formHtml.replaceAll("<[^>]+>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//            formHtml = JsoupUtil.clean(formHtml);
//            script = script.replaceAll("<[^>]+>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
//            script = JsoupUtil.unescape(JsoupUtil.clean(script, Safelist.none()));
            script = JsoupUtil.unescape(JsoupUtil.clean(script));
            HashMap<String, String> columnsMap = new LinkedHashMap<>();
            HashMap<String, String> allColumnsMap = new LinkedHashMap<>();
            for (String columnKV : StringUtils.split(columns, ";")) {
                String[] kv = StringUtils.split(columnKV, "=");
				if (kv.length == 2 && !kv[0].equals(kv[1])) {
                    columnsMap.put(kv[0], kv[1]);
                }
				allColumnsMap.put(kv[0], kv[1]);
            }
            
            model.addAttribute("formHtml", formHtml);
            model.addAttribute("script", script);
            model.addAttribute("columns", columnsMap.isEmpty() ? allColumnsMap : columnsMap);
            model.addAttribute("objectName", HashMap.class.getName());
            model.addAttribute("id", dataOperation.getId());
        } else {
            model.addAttribute("error", "该数据操作不存在！");
        }
        return "/data/data_export_operation";
    }
    
    /**
     * 查询sql包含的所有列名
     * 
     * @param sql
     * @param model
     * @return
     */
    @RequestMapping(EXPORT_URL + "/queryExportColumns")
    public String queryExportColumns(String sql, Model model, RedirectAttributes redirectAttributes) {
        List<Object>  columns = new ArrayList<Object>();
        if (StringUtils.isNotBlank(sql)) {
//        	sql = JsoupUtil.unescape(JsoupUtil.clean(sql, Safelist.none()));// 放开Mysql JSON的语法
//        	String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
//        	Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
//        	Matcher matcher = pattern.matcher(sql);
//        	if (matcher.find()) {
//        		String fromUrl = HttpContext.getCurrentRequest().getServletPath();
//        		model.addAttribute("status", false);
//        		model.addAttribute("error", "参数不合法！");
//        		return "redirect:/illegal.json?illegal=SQLInject&fromUrl=" + fromUrl;
//        	}
//        	String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
//    		SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/base/showExportColumns";
//    		}
//    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
//    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/base/showExportColumns";
//    		}
        	Result result = checkSql(sql);
    		if (!result.isSuccess()) {
    			model.addAttribute("status", result.isSuccess());
    			model.addAttribute("error", result.getMessage());
    			redirectAttributes.addFlashAttribute("status", result.isSuccess());
    			redirectAttributes.addFlashAttribute("error", result.getMessage());
    			return (String) result.getData();// 返回view
    		}
    		sql = (String) result.getData();
    		
            Map<String, Object> exportColumns = dataOperationService.queryExportColumns(sql);
            columns = Arrays.asList(exportColumns.keySet().toArray());
        }
        model.addAttribute("columns", columns);
        return "/base/showExportColumns";
    }
    
    /**
     * 导入用服一评得分
     * @param appraiserExcel
     * @param response
     * @param map
     * @throws IOException
     */
    @RequestMapping("/operation/{id}")
    public void operation(@PathVariable("id") Integer id, @RequestParam(required = false, value = "fileExcel") MultipartFile appraiserExcel, HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws IOException {
        String errorMessage = "";
        DataOperation dataOperation = dataOperationService.selectByPrimaryKey(id);
        
        response.setContentType("text/plain; charset=UTF-8");
        if (dataOperation == null) {
            errorMessage = "请求的数据操作不存在，请重试！";
        } else if(!checkPermission(dataOperation)) {
        	errorMessage = "没有权限进行该操作！";
        } else {
            Integer type = dataOperation.getType();
            if (type == null) {
                errorMessage = "请求的数据操作类型不正确，请联系管理员！";
            } else if (type == 1) {
                importOperation(dataOperation, appraiserExcel, request, response);
            } else if (type == 0) {
                exportOperation(dataOperation, request, response);
            }
        }
        if (StringUtils.isNotBlank(errorMessage)) {
            PrintWriter out = response.getWriter();
            map.put("errorMessage", errorMessage);
            out.write(JSON.toJSONString(map));
        }
    }
    
    public void exportOperation(DataOperation dataOperation, HttpServletRequest request, HttpServletResponse response) {
        String objectName = request.getParameter("objectName");
        String objectKV = request.getParameter("objectKV");
        String pageParamKV = request.getParameter("pageParamKV");
        String columns = request.getParameter("columns");
        HttpSession session = request.getSession();
        String operationName = dataOperation.getName();
        try {
            session.setAttribute(operationName, "0.00%");
            PageParam<?> pageParam = ExportUtils.getPageParam(objectName, objectKV, pageParamKV);
            String sql = dataOperation.getScript();
//            sql = sql.replaceAll("<[^>]+>", "").replaceAll("＆", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
//            sql = JsoupUtil.unescape(JsoupUtil.clean(sql, Safelist.none()));
//            String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
//    		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
//    		Matcher matcher = pattern.matcher(sql);
//			if (matcher.find()) {
//				throw new RuntimeException(new RuntimeException("【违规操作】SQL注入"));
//    		}
//			String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
//    		SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			throw new RuntimeException(String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    		}
//    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
//    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			throw new RuntimeException(String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    		}
            Result result = checkSql(sql, true);
    		if (!result.isSuccess()) {
    			throw new RuntimeException(result.getMessage());
    		}
    		sql = (String) result.getData();

            pageParam.setCustomField(sql);
            if (StringUtils.isBlank(columns)) {
                columns = dataOperation.getColumns();
            }
            pageParam.setColumns(parseColumns(columns));
            
            long total = dataOperationService.countExportData(pageParam);
            int rowAccessWindowSize = 100;
            SXSSFWorkbook workbook = null;
            ExportUtils exportUtils = new ExportUtils(rowAccessWindowSize);
            exportUtils.setOneSheetOnly(true);
            exportUtils.setNeedParseTitle(false);
            
            Map<String, Object> model = new HashMap<>();
            pageParam.setPageSize(rowAccessWindowSize);
            model.put("exportFileName", operationName);
            model.put("columns", StringUtils.split(columns, ";"));
            
            for (int statrRow = 0; statrRow < total; statrRow += rowAccessWindowSize) {
                session.setAttribute(operationName, String.format("%.2f%%", (double) (statrRow) /  (total) * 100));
                pageParam.setStart(statrRow);
                List<Map<String, Object>> exportData = dataOperationService.queryExportData(pageParam);
                model.put("data", exportData);
                
                exportUtils.setStartRow(statrRow + 1);
                workbook = exportUtils.renderExcelDocument(model, request, response, workbook);
            }
            session.setAttribute(operationName, "100%");
            exportUtils.writeToResponse(response, workbook);
        } catch (Exception e) {
            Integer logId = ExceptionHandler.insertException(e);
            throw new RuntimeException("导出数据失败！<br><br>错误信息：" + e.getMessage() + "<br>错误ID:" +logId);
        } finally {
            if (session != null) {
                session.removeAttribute(operationName);
            }
        }
    }

    public void importOperation(DataOperation dataOperation, MultipartFile appraiserExcel, ServletRequest request, ServletResponse response) throws IOException {
        String errorMessage = "";
        HashMap<String, Object> map = new HashMap<>();
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (!appraiserExcel.isEmpty()) {
            String fileType = appraiserExcel.getOriginalFilename()
                    .substring(appraiserExcel.getOriginalFilename().lastIndexOf("."));
            if (fileType.equals(".xlsx")) {
                try {
                    String clazzStr = dataOperation.getClazz();
                    String methodStr = dataOperation.getMethod();
                    
                    String parameterTypesStr = StringUtils.trimToEmpty(dataOperation.getParameterTypes());
                    String[] parameterTypesArr = StringUtils.split(parameterTypesStr);
                    Class<?>[] parameterTypes = new Class<?>[parameterTypesArr.length + 1];
                    parameterTypes[0] = MultipartFile.class;
                    for (int i = 0; i < parameterTypesArr.length; i++) {
                        String parameterType = parameterTypesArr[i];
                        parameterTypes[i + 1] = Class.forName(parameterType);
                    }
                    
                    Object service = SpringContext.getBean(clazzStr);
                    Method method = service.getClass().getDeclaredMethod(methodStr, parameterTypes);
                    Object errorResult = null;
                    if (parameterTypes.length != 1) {
                        errorResult = method.invoke(service, appraiserExcel, request.getParameterMap());
                    } else {
                        errorResult = method.invoke(service, appraiserExcel);
                    }
                    if (errorResult != null) {
                        if (errorResult instanceof List && !((List) errorResult).isEmpty()) {
                            //errorMessage = JSON.toJSONString(errorResult);
                            JSONArray jsonArray =  (JSONArray) JSON.toJSON(errorResult);
                            for (Object jsonObject : jsonArray) {
                                ((JSONObject)jsonObject).remove("stackTrace");
                                ((JSONObject)jsonObject).remove("@type");
                                ((JSONObject)jsonObject).remove("localizedMessage");
                                ((JSONObject)jsonObject).remove("suppressed");
                                ((JSONObject)jsonObject).remove("cause");
                            }
                            errorMessage = JSONArray.toJSONString(jsonArray, SerializerFeature.WriteMapNullValue);
                        }
                    }
                } catch (ExcelImportException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                    errorMessage = e.getMessage();
                    if (e instanceof InvocationTargetException) {
                        errorMessage = ((InvocationTargetException) e).getTargetException().getMessage();
                        if (((InvocationTargetException) e).getTargetException() instanceof ExcelImportException) {
                            map.put("progress", ((ExcelImportException) ((InvocationTargetException) e).getTargetException()).getProgress());
                        }
                    }
                    if (e instanceof ExcelImportException) {
                        map.put("progress", ((ExcelImportException) e).getProgress());
                    }
                }
            } else {
                errorMessage = "文件格式错误";
            }
        } else {
            errorMessage = "文件为空";
        }
        map.put("errorMessage", errorMessage);
        out.write(JSON.toJSONString(map));
    }
    
    /**
     * 导入用服一评得分
     * @param appraiserExcel
     * @param response
     * @param map
     * @throws IOException
     */
    @RequestMapping(EXPORT_URL + "/preview/{id}")
    public String exportPreview(@PathVariable("id") Integer id, PageParam<Map<String, String>> pageParam, String objectKV, Model model) {
        DataOperation dataOperation = dataOperationService.selectByPrimaryKey(id);
        if(!checkPermission(dataOperation)) {
        	return Consts.VIEW_UNAUTHORIZED;
        } 
        if (HttpContext.isJSON()) {
            pageParam.setModel(ExportUtils.str2KVMap(objectKV, false));
            
            String sql = StringUtils.trimToEmpty(dataOperation.getScript());
////            sql = sql.replaceAll("<[^>]+>", "").replaceAll("＆", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
//            sql = JsoupUtil.unescape(JsoupUtil.clean(sql, Safelist.none()));
//            String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
//    		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
//    		Matcher matcher = pattern.matcher(sql);
//			if (matcher.find()) {
//    			String fromUrl = HttpContext.getCurrentRequest().getServletPath();
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", "参数不合法！");
//    			return "redirect:/illegal.json?illegal=SQLInject&fromUrl=" + fromUrl;
//    		}
//			String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
//    		SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_export_preview";
//    		}
//    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
//    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex);
//    		if (!matcherSqlTables.isValid()) {
//    			model.addAttribute("status", false);
//    			model.addAttribute("error", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
//    			return "/data/data_export_preview";
//    		}
            Result result = checkSql(sql, true);
    		if (!result.isSuccess()) {
    			model.addAttribute("status", result.isSuccess());
    			model.addAttribute("error", result.getMessage());
    			return (String) result.getData();// 返回view
    		}
    		sql = (String) result.getData();
            
            pageParam.setCustomField(sql);
            String columns = HttpContext.getCurrentRequest().getParameter("columnsStr");
            if (StringUtils.isBlank(columns)) {
                columns = dataOperation.getColumns();
            }
            pageParam.setColumns(parseColumns(columns));
            
            long total = dataOperationService.countExportData(pageParam);
            List<Map<String, Object>> exportData = dataOperationService.queryExportData(pageParam);
            pageParam.setTotal(total);
            model.addAttribute("data", exportData);
        }
        model.addAttribute("operationName", dataOperation.getName());
        return "/data/data_export_preview";
    }
    
    private List<DataTableColumn> parseColumns(String columns) {
        List<DataTableColumn> tableColumns = new ArrayList<>();
        List<DataTableColumn> allTableColumns = new ArrayList<>();
        if (StringUtils.isNotBlank(columns)) {
            String[] columnArr = StringUtils.split(columns, ";");
            for (String column : columnArr) {
                String[] kv = StringUtils.split(column, "=");
                String title = kv[0];
                String data = kv[0];
                if (kv.length == 2) {
                    title = kv[1];
                }
                
                if (!data.equals(title)) {
                    DataTableColumn tableColumn = new DataTableColumn(title, data);
                    tableColumns.add(tableColumn);
                }
                DataTableColumn tableColumn = new DataTableColumn(title, data);
                allTableColumns.add(tableColumn);
            }
        }
        return tableColumns.isEmpty() ? allTableColumns : tableColumns;
    }
    
    private Result checkSql(String sql) {
    	return checkSql(sql, false);
    }
    
    private Result checkSql(String sql, Map<String, Object> params) {
    	return checkSql(sql, true, params);
    }
    
    private Result checkSql(String sql, boolean fillParams) {
    	return checkSql(sql, fillParams, null);
    }
    
    private Result checkSql(String sql, boolean fillParams, Map<String, Object> params) {
//    	sql = JsoupUtil.unescape(JsoupUtil.clean(sql, Safelist.none()));// 放开Mysql JSON的语法
    	sql = JsoupUtil.unescape(JsoupUtil.clean(sql));
        if (StringUtils.isNotBlank(sql)) {
        	String regex = SystemConfig.systemVariables.getOrDefault("sys.sql.inject.filter", PropertyUtil.getProperty("sys.sql.inject.filter"));
        	Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.UNICODE_CASE);
        	Matcher matcher = pattern.matcher(sql);
        	if (matcher.find()) {
        		String fromUrl = HttpContext.getCurrentRequest().getServletPath();
        		return new Result(false, "redirect:/illegal.json?illegal=SQLInject&fromUrl=" + fromUrl, "参数不合法！");
        	}
        	
        	if (fillParams) {
            	// 替换当前用户参数
            	Principal user = UserContext.getCurrentPrincipal();
            	Map<String, Object> variables = new HashMap<String, Object>();
            	if (params != null) {
            		variables.putAll(params);
            	}
            	variables.put("user", user);
            	sql = SQLParser.fillSqlParams(sql, variables);
        	}
        	
        	// 获取当前连接数据库的类型
        	DbType dbType = getCurrentDbType();
        	
        	String tableWhitelistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.whitelist.regex", PropertyUtil.getProperty("sys.sql.table.whitelist.regex"));
        	SqlParserResult matcherSqlTables = SQLParser.matcherSqlTables(sql, tableWhitelistRegex, dbType);
    		if (!matcherSqlTables.isValid()) {
    			return new Result(false, "/base/showExportColumns", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
    		}
    		String tableBlacklistRegex = SystemConfig.systemVariables.getOrDefault("sys.sql.table.blacklist.regex", PropertyUtil.getProperty("sys.sql.table.blacklist.regex"));
    		matcherSqlTables = SQLParser.unMatcherSqlTables(sql, tableBlacklistRegex, dbType);
    		if (!matcherSqlTables.isValid()) {
    			return new Result(false, "/base/showExportColumns", String.format("没有权限访问以下表%s！", matcherSqlTables.getMatchTables()));
    		}
        } else {
        	return new Result(false, "/base/showExportColumns", String.format("不允许为空！"));
        }
		return new Result(true, (Object) sql);
    }
    
    private boolean checkPermission(DataOperation dataOperation) {
    	Boolean isPermit = false;
    	String empId = null;
    	Principal user = UserContext.getCurrentPrincipal();
        if (!UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
        	empId = user.getUserInfoId().toString();
        } else {
        	isPermit = true;
        }
        if (dataOperation != null) {
        	String empPower = StringUtils.trimToEmpty(dataOperation.getEmpPower());
//        	String depPower = StringUtils.trimToEmpty(dataOperation.getDepPower());
        	
        	if (empPower.matches("(.*)\\b" + empId + "\\b(.*)")) {
        		isPermit = true;
        	}
        }
        return isPermit;
    }
    
	/**
	 * 获取当前链接数据库的数据库类型
	 * 
	 * @return
	 */
	private DbType getCurrentDbType() {
		RoutingDataSource dataSource = SpringContext.getBean("dataSource", RoutingDataSource.class);
		return SQLParser.getCurrentDbType(dataSource);
	}
}