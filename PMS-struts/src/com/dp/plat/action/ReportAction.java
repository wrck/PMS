package com.dp.plat.action;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.QualityParam;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ReportQueryParam;
import com.dp.plat.data.bean.StatisticsSummarize;
import com.dp.plat.data.report.EchartsUtil;
import com.dp.plat.echarts.Echarts;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ReportDataTypeParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ReportService;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MessageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opensymphony.xwork2.Preparable;

/**
 * 报表功能
 * 
 * @author admin
 * 
 */
public class ReportAction extends BaseAction implements Preparable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicDataService basicDataService;
	private DepartmentManageService departmentManageService;
	private ReportService reportService;

	private List<Department> officeList;
	private List<BasicDataBean> navTabList;// 项目维护页面选项卡集合
	private ReportQueryParam queryParam;
	private Map<String, Object> queryParamMap;
	private List<Map<String, Object>> dataList;
	
	private String data;
	private String dataJson;
	private String dataTypeCode;//取趋势图数据类型
	private String officeCode;
	private StatisticsSummarize summarize;//综述
	private String BASIC_DATA_TARGET_CODE = "28";
	
	//数据透视表数据HTML
	private String assignedTableHtml;
	private String traceTableHtml;
	private String qualityTableHtml;
	private String closeTableHtml;
	private String impl0TableHtml;
	private String impl1TableHtml;
	private String impl3TableHtml;

	@Override
	public void prepare() throws Exception {
		// 	// 数据查询
		if (queryParam == null) {
			queryParam = new ReportQueryParam();
			queryParam.setStartTime(DateUtil.getFirstDay());
			queryParam.setQuarterStartTime(DateUtil.getQuarterFirstDay(new Date()));
		}
	}

	/**
	 * 报表展示页面
	 * @return
	 */
	public String show() {
		
		// 选项卡
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_DATA_TAB);
		
		List<Department> offices = departmentManageService.queryDepartments();
		if(officeList == null){
			officeList = new ArrayList<Department>();
		}
		officeList.add(new Department("total" , "全国"));
		officeList.addAll(offices);
		//全国项目统计综述 
		summarize = reportService.queryStatisticsSummarize();
		//项目经理指派率
		List<ReportLineData> assignedDatas = reportService.queryReportTableAssignedData(queryParam);
		assignedTableHtml = EchartsUtil.packagingTableHtml(assignedDatas);
		//项目经理跟踪率
		List<ReportLineData> traceDatas = reportService.queryReportTableTraceData(queryParam);
		traceTableHtml = EchartsUtil.packagingTableHtml(traceDatas);
		
		//项目实施方式占比
		
		//闭环新增比
		List<ReportLineData> closedDatas = reportService.queryReportTableClosedData( queryParam );
		closeTableHtml = EchartsUtil.packagingTableHtml(closedDatas);
		
		//质量管理
		// TODO
//		queryParam.setImplWay(MessageUtil.IMPL_WAY_1);
//		queryParam.setProjectCategory(MessageUtil.PROJECT_TYPE_ENGINEE);
//		List<ReportLineData> qualityDatas = reportService.queryReportTableQualityData(queryParam);
//		qualityTableHtml = EchartsUtil.packagingQualityTableHtml(qualityDatas);
		return "show";
	}
	/**
	 * 获取办事处code和name
	 * @return
	 */
	private Map<String, String> getOfficeMap(){
		Map<String, String> officeMap = departmentManageService.queryDepartmentMap();
		officeMap.put("total", "全国");
		return officeMap;
	}
	/**
	 * 加载趋势图(折线图)
	 * @return
	 */
	public String loadLineData(){
		
		Map<String, String> officeMap = getOfficeMap();
		
		Map<String, String> targetMap =  basicDataService.queryBasicDataBeanMap(BASIC_DATA_TARGET_CODE);
		
		Map<Date, String> map = reportService.queryLineData(officeCode , dataTypeCode);
		String[] xAsis = new String[map.size()];
		Date[] dates = new Date[map.size()];
		Double[] rates = new Double[map.size()];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		int i = 0;
		int max = 100;
		for(Date time : map.keySet()){
			xAsis[i] = sdf.format(time);
			rates[i] = Double.parseDouble(map.get(time));
			if(rates[i] > max)
				max = (int) Math.ceil(rates[i]);
			dates[i] = time;
			i++;
		}
		try {
			arraySort(dates,xAsis,rates);
			List<Double[]> rateList = new ArrayList<Double[]>();
			rateList.add(rates);
			Echarts echarts = EchartsUtil.packagingLineEcharts(officeMap.get(officeCode) + targetMap.get(dataTypeCode), "",
					"center", xAsis, rateList, new String[]{targetMap.get(dataTypeCode)} ,"" , new String[]{EchartsUtil.getColor(0)});
			StringBuffer json = new StringBuffer(EchartsUtil.jsonToString(echarts));
			String seach = "\"yAxis\":[{\"type\":\"value\",";
			int index = json.lastIndexOf(seach);
			json.insert(index+seach.length(), "\"min\":0,\"max\":"+ max +",\"scale\":true,");
			data = json.toString();
//			data = EchartsUtil.jsonToString(echarts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 查询项目闭环数量的趋势图
	 * @return
	 */
	public String loadLine_qualityData(){
		Map<String, String> officeMap = departmentManageService
				.queryDepartmentMap();
		officeMap.put("total", "全国");
		
		Map<Date, String> map = reportService.queryLineQualityData(officeCode , dataTypeCode);
		String[] times = new String[map.size()];
		Date[] dates = new Date[map.size()];
		Double[] rates = new Double[map.size()];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		int i = 0;
		for(Date time : map.keySet()){
			times[i] = sdf.format(time);
			rates[i] = Double.parseDouble(map.get(time));
			dates[i] = time;
			i++;
		}
		try {
			arraySort(dates,times,rates);
			List<Double[]> rateList = new ArrayList<Double[]>();
			rateList.add(rates);
			Echarts echarts = EchartsUtil.packagingLineEcharts(officeMap.get(officeCode) + "闭环项目数量", "",
					"center", times, rateList, new String[]{"闭环项目数量"} ,"" , new String[]{EchartsUtil.getColor(1)});
			data = EchartsUtil.jsonToString(echarts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 加载企业网项目实施占比趋势图
	 * @return
	 */
	public String loadLine_implData(){
		try {
			Map<String, String> officeMap = departmentManageService
					.queryDepartmentMap();
			officeMap.put("total", "全国");
			
			Map<String, String> implMap = reportService.queryReportLineImplWayData(officeCode , dataTypeCode);
			
			String settingTimes = reportService.queryReportSettingTimes(officeCode , dataTypeCode);
			String[] settingArr = StringUtils.isBlank(settingTimes) ? new String[] {} : settingTimes.split(",");
			sortSettingTime(settingArr);
			Double[] impl0 = new Double[settingArr.length];
			Double[] impl1 = new Double[settingArr.length];
			Double[] impl3 = new Double[settingArr.length];
			int i = 0;
			for(String time : settingArr){
				impl0[i] = Double.parseDouble(implMap.get(time + "impl0") == null ? "0":implMap.get(time + "impl0"));
				impl1[i] = Double.parseDouble(implMap.get(time + "impl1") == null ? "0":implMap.get(time + "impl1"));
				impl3[i] = Double.parseDouble(implMap.get(time + "impl3") == null ? "0":implMap.get(time + "impl3"));
				i ++;
			}
			List<Double[]> rateList = new ArrayList<Double[]>();
			rateList.add(impl0);
			rateList.add(impl1);
			rateList.add(impl3);
			Echarts echarts = EchartsUtil.packagingLineEcharts(officeMap.get(officeCode) + "企业网项目实施占比", "",
					"center", settingArr, rateList, new String[]{"原厂直服" , "原厂督导" , "代理商自服"} ,"%" ,
					new String[]{EchartsUtil.getColor(1) ,EchartsUtil.getColor(4) ,EchartsUtil.getColor(5)});
			data = EchartsUtil.jsonToString(echarts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	private void sortSettingTime(String[] settingArr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		int n = settingArr.length;
		if(n > 1){
			for(int i = 0 ;i < n - 1 ; i ++){
				for(int j = 0 ; j < n - 1;j ++){
					if(sdf.parse(settingArr[j]).after(sdf.parse(settingArr[j + 1]))){
						String tmp = settingArr[j];
						settingArr[j] = settingArr[ j + 1];
						settingArr[j + 1] = tmp;
					}
				}
			}
		}
	}

	/**
	 * 项目指派率查询
	 * 
	 * @return
	 */
	public String assignedRate() {
		try {
			//查询数据项目指派率数据
			List<ReportLineData> assignedDatas = reportService.queryReportLineAssignedData(queryParam);
			ReportLineData totalData = reportService.statisticsTotalData(assignedDatas);
			BasicDataBean assigne = basicDataService.queryBasicDataBeanByDataId(ReportDataTypeParam.REPORT_ASSIGNED_RATE);
			
			String[] officeNames = new String[assignedDatas.size()+1];
			Double[] rates = new Double[assignedDatas.size()+1];
			int i  = 1;
			for(ReportLineData data : assignedDatas){
				officeNames[i] = StringUtils.trimToEmpty(data.getOfficeName());
				rates[i] = Double.parseDouble(data.getSpecificValue());
				i ++ ;
			}
			officeNames[0] = totalData.getOfficeName();
			rates[0] = Double.parseDouble(totalData.getSpecificValue());
			data = EchartsUtil.packagingOneBarEcharts(assigne.getBasicDataName(), officeNames, rates, assigne.getBasicDataName(), assigne.getBasicDataAttri1(), "#8FBC8F");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}



	/**
	 * 项目经理跟踪率
	 * 
	 * @return
	 */
	public String traceRate() {
		try {
			List<ReportLineData> traceDatas = reportService.queryReportLineTraceData(queryParam);
			ReportLineData totalTraceData = reportService.statisticsTotalData(traceDatas);
			BasicDataBean assigne = basicDataService.queryBasicDataBeanByDataId(ReportDataTypeParam.REPORT_TRACE_RATE);
			
			int size = traceDatas.size();
			String[] officeNames = new String[size+1];
			Double[] rates = new Double[size+1];
			int i  = 1;
			for(ReportLineData data : traceDatas){
				officeNames[i] = StringUtils.trimToEmpty(data.getOfficeName());
				rates[i] = Double.parseDouble(data.getSpecificValue());
				i ++ ;
			}
			officeNames[0] = totalTraceData.getOfficeName();
			rates[0] = Double.parseDouble(totalTraceData.getSpecificValue());
			data = EchartsUtil.packagingOneBarEcharts(assigne.getBasicDataName(), officeNames, rates, assigne.getBasicDataName(), assigne.getBasicDataAttri1(), "#8FBC8F");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 季度新增闭环比
	 * @return
	 */
	public String closeRate(){
		if(queryParam == null){
			queryParam = new ReportQueryParam();
			queryParam.setStartTime(DateUtil.getFirstDay());
			queryParam.setQuarterStartTime(DateUtil.getQuarterFirstDay(new Date()));
		}
		
		Map<String, Double> rateMap = reportService.queryCloseRate(queryParam);
		officeList = departmentManageService.queryDepartments();
		String[] officeNames = new String[officeList.size()+1];
		Double[] rates = new Double[officeList.size()+1];
		officeNames[0] = "全国";
		rates[0] = 10000.0;
		int i = 1;
		for (Department office : officeList) {
			if (rateMap.get(office.getDepartmentNum()) != null) {
				officeNames[i] = office.getDepartmentName();
				rates[i] = rateMap.get(office.getDepartmentNum());
				i++;
			} else {
				officeNames[i] = office.getDepartmentName();
				rates[i] = 0.0;
				i++;
			}
		}
		arraySort(rates, officeNames);
		rates[0] = rateMap.get("total") == null ? 0d : rateMap.get("total");
		List<Double[]> rateList = new ArrayList<Double[]>();
		rateList.add(rates);
		try {
			Echarts echarts = EchartsUtil.packagingBarEcharts("闭环新增比", "",
					"center", officeNames, rateList, new String[]{"闭环新增比"} , "%",new String[]{EchartsUtil.getColor(2)});
			data = EchartsUtil.jsonToString(echarts);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 项目实施方式占比
	 * @return
	 */
	public String implRate(){
		if(queryParam == null){
			queryParam = new ReportQueryParam();
			queryParam.setStartTime(DateUtil.getFirstDay());
			queryParam.setQuarterStartTime(DateUtil.getQuarterFirstDay(new Date()));
		}
		//所有
		Map<String, Long> implMap = reportService.queryImplWayMap(queryParam, MessageUtil.IMPL_WAY_ALL);
		//原厂直服
		Map<String, Long> implMap1 = reportService.queryImplWayMap(queryParam , MessageUtil.IMPL_WAY_0);
		//原厂督导
		Map<String, Long> implMap2 = reportService.queryImplWayMap(queryParam, MessageUtil.IMPL_WAY_1);
		//代理商自服
		Map<String, Long> implMap3 = reportService.queryImplWayMap(queryParam, MessageUtil.IMPL_WAY_3);
		//查询办事处
		Map<String, String> officeMap = departmentManageService.queryDepartmentMap();
		//组织柱状图参数
		Double[] implArr1 = new Double[officeMap.size() + 1];
		Double[] implArr2 = new Double[officeMap.size() + 1];
		Double[] implArr3 = new Double[officeMap.size() + 1];
		String[] officeNames = new String[officeMap.size() + 1];
		officeNames[0] = "全国";
		implArr1[0] = 0d;
		implArr2[0] = 0d;
		implArr3[0] = 0d;
		int i = 1;
		DecimalFormat df = new DecimalFormat("###.00");
		for(String officeCode : officeMap.keySet()){
			officeNames[i] = StringUtils.trimToEmpty(officeMap.get(officeCode));
			implArr1[i] = 0d;
			implArr2[i] = 0d;
			implArr3[i] = 0d;
			
			if(implMap.get(officeCode) != null && implMap.get(officeCode)!=0){
				implArr1[i] =  Double.parseDouble(df.format(((double)(implMap1.get(officeCode) == null ? 0 : implMap1.get(officeCode) )/ (double)(implMap.get(officeCode)))*100));
				implArr2[i] =  Double.parseDouble(df.format(((double)(implMap2.get(officeCode) == null ? 0 : implMap2.get(officeCode))/ (double)(implMap.get(officeCode)))*100));
				implArr3[i] =  Double.parseDouble(df.format(((double)(implMap3.get(officeCode) == null ? 0 : implMap3.get(officeCode)) / ( (double)implMap.get(officeCode)))*100));
//				officeNames[i] = officeMap.get(officeCode);
				
				// 全国数据
				implArr1[0] += implArr1[i];
				implArr2[0] += implArr2[i];
				implArr3[0] += implArr3[i];
			}
			i++;
		}
		double total = implArr1[0] + implArr2[0] + implArr3[0];
		if (total != 0d) {
			implArr1[0] = Double.parseDouble(df.format(implArr1[0] / total * 100));
			implArr2[0] = Double.parseDouble(df.format(implArr2[0] / total * 100));
			implArr3[0] = Double.parseDouble(df.format(implArr3[0] / total * 100));
		}
		try {
			List<Double[]> rateList = new ArrayList<Double[]>();
			rateList.add(implArr1);
			rateList.add(implArr2);
			rateList.add(implArr3);
			Echarts echarts = EchartsUtil.packagingBarEcharts("企业网项目各类实施占比", "",
					"center", officeNames, rateList, new String[]{"原厂直服","原厂督导", "代理商自服"} ,
					 "%" ,new String[]{EchartsUtil.getColor(3),EchartsUtil.getColor(4),EchartsUtil.getColor(5)});
			data = EchartsUtil.jsonToString(echarts);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	
	/**
	 * 项目质量
	 * @return
	 */
	public String quality() {
		if (queryParam == null) {
			queryParam = new ReportQueryParam();
			queryParam.setStartTime(DateUtil.getQuarterFirstDay(new Date()));
			queryParam.setQuarterStartTime(DateUtil.getQuarterFirstDay(new Date()));
		}
		Map<String, String> officeMap = departmentManageService.queryDepartmentMap();
		officeMap.put("total", "全国");
		//List<QualityParam> qualityParams = reportService.queryQualityList(queryParam);
		Map<String,List<QualityParam>> allQualityParams = reportService.queryTotalAndRemainderList(queryParam);
		List<QualityParam> remainderQualityParams =  allQualityParams.get("remainder");
		List<QualityParam> totalQualityParams =  allQualityParams.get("total");
		//查询全国的闭环数、平均得分
		//QualityParam totalQualityParam = reportService.queryTotalQuality(queryParam);
		int size = remainderQualityParams.size();
		String[] officeNames = new String[size];
		//String[] officeNames2 = new String[size];
		Double[] avgScore = new Double[size];
		Double[] remainderProjectSize = new Double[size];
		Double[] totalProjectSize = new Double[size];
		DecimalFormat df = new DecimalFormat("###.00");
		int i = 0;
		for (QualityParam param : remainderQualityParams) {
			officeNames[i] = StringUtils.trimToEmpty(officeMap.get(param.getOfficeCode()));
			avgScore[i] = Double.parseDouble(df.format(param.getAvgCloseScore()));
			remainderProjectSize[i] = (double) param.getProjectSize();

			// 找到各办事处对应的全部闭环项目数量，以及非直签督导项目闭环数量
			QualityParam temp = null;
			if (i < totalQualityParams.size()) {
				temp = totalQualityParams.get(i);
			}
			if (temp != null && param.getOfficeCode().equals(temp.getOfficeCode())) {
				totalProjectSize[i] = (double) temp.getProjectSize();
			} else {
				for (QualityParam loopTemp : totalQualityParams) {
					if (loopTemp != null && param.getOfficeCode().equals(loopTemp.getOfficeCode())) {
						totalProjectSize[i] = (double) loopTemp.getProjectSize();
						break;
					}
				}
			}
			i++;
		}
		//加上全国的
//		officeNames[0] = officeMap.get(totalQualityParam.getOfficeCode());
//		officeNames2[0] = officeMap.get(totalQualityParam.getOfficeCode());
//		avgScore[0] = Double.parseDouble(df.format(totalQualityParam.getAvgCloseScore()));
//		projectSize[0] = (double) totalQualityParam.getProjectSize();
		try {
			List<Double[]> rateList = new ArrayList<Double[]>();
			rateList.add(avgScore);
			Echarts echarts = EchartsUtil.packagingBarEcharts("闭环平均得分", "",
					"center", officeNames, rateList, new String[]{"平均分"} ,"",new String[]{EchartsUtil.getColor(6)});
			// 为y坐标添加最小值和最大值
			StringBuffer json = new StringBuffer(EchartsUtil.jsonToString(echarts));
			String seach = "\"yAxis\":[{\"type\":\"value\",";
			int index = json.lastIndexOf(seach);
			json.insert(index+seach.length(), "\"min\":69,\"max\":100,\"scale\":true,");
//			data = EchartsUtil.jsonToString(echarts);
			data = json.toString();
			rateList = new ArrayList<Double[]>();
			rateList.add(Arrays.copyOfRange(remainderProjectSize, 1, remainderProjectSize.length));
			Echarts echarts2 = EchartsUtil.packagingBarEcharts("闭环项目数量", "",
					"center", Arrays.copyOfRange(officeNames, 1, officeNames.length), rateList, new String[]{"项目数量"} , "",new String[]{EchartsUtil.getColor(7)});
			dataJson = EchartsUtil.jsonToString(echarts2);
			qualityTableHtml = EchartsUtil.packagingQualityTableHtml(avgScore,remainderProjectSize,totalProjectSize,officeNames);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String projectSummaryStatus() {
		boolean isSub = getServletRequest().getRequestURI().contains("/sub/");
		// 选项卡
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_DATA_TAB);
		if (StringUtils.isNotBlank(dataJson)) {
			queryParamMap = JSON.parseObject(dataJson, Map.class);
		}
		if (queryParamMap == null) {
			queryParamMap = new HashMap<String, Object>();
		}
		// 已创建项目
		queryParamMap.put("projectState", "30,31,32");
		// 已发货项目
		queryParamMap.put("shipmentState", "-1");
		
		List<Map<String, Object>> list = reportService.queryProjectSummaryStatus(queryParamMap);
		// 明细信息，这直接返回列表
		if ("info".equals(data)) {
			dataList = list;
		} else { // 否则，则显示统计页面
			// 工程计划状态
			Collection<String> planStateKeys = Arrays.asList(/*"47"*/);
			Map<String, String> projectPlanStateMap = converter2Map(basicDataService.queryBasicDataBeans("22"), planStateKeys);
			// 实施状态
			Collection<String> executionStateKeys = Arrays.asList();
			Map<String, String> executionStateMap = converter2Map(basicDataService.queryBasicDataBeans("projectExecutionState"), executionStateKeys);
			// 值和名称的映射关系
			String executionStateRelationStr = "{" + 
					"	['',null]: '未填写'," + 
					"	['5','10']: '未实施'," + 
					"	['20','30','40']: '实施中'," + 
					"	['50','60','70','80','90']: '实施完成'" + 
					"}";
			Map<Collection<String>, String> executionStateRelation = JSON.parseObject(executionStateRelationStr, LinkedHashMap.class);
			// 流程状态
			Collection<String> closeProcessStateKeys = Arrays.asList(/*"10","15","20","30","40","50"*/);
			Map<String, String> closeProcessStateMap = converter2Map(basicDataService.queryBasicDataBeans("projectCloseProcessState"), closeProcessStateKeys);
			
			Map<String, Object> config = new HashMap<String, Object>();
			config.put("projectPlanStateMap", projectPlanStateMap);
			config.put("executionStateMap", executionStateMap);
			config.put("executionStateRelation", executionStateRelation);
			config.put("closeProcessStateMap", closeProcessStateMap);
			
			//String summaryStr = "[{'title':'项目实施状态（直签+非直签）','params':['executionState'],'dimension':'executionState','allCondition':'总计','emptyCondition':'未填写'},{'title':'项目实施状态（直签）','params':['column004','column011'],'expression':'column004=${column004}&&column011=${column011}','condition':[{'regex':'column004=运营商市场部&&column011=10','result':true}],'dimension':'executionState','allCondition':'总计','emptyCondition':'未填写'},{'title':'项目流程状态（直签+非直签）','params':['closeProcessState'],'dimension':'closeProcessState','allCondition':'总计','emptyCondition':'未填写'},{'title':'可闭环项目数','dimensions':[{'params':['column004','column011','projectPlanState'],'expression':'column004=${column004}&&column011=${column011}&&projectPlanState=${projectPlanState}','condition':[{'regex':'column004=运营商市场部&&column011=10&&projectPlanState=47','result':true}],'dimension':'直签可闭环'},{'params':['column004','column011','executionState'],'expression':'column004=${column004}&&column011=${column011}&&executionState=${executionState}','condition':[{'regex':'column004=运营商市场部&&column011=10','result':false},{'regex':'executionState=(50|60|70|80|90)','result':true}],'dimension':'非直签可闭环'}]}]";
//			String summaryStr = "[{'title':'项目实施状态（直签+非直签）','params':['executionState'],'dimension':'executionState','allCondition':'总计','emptyCondition':'未填写'},{'title':'项目实施状态（直签）','params':['column004','column011'],'expression':'column004=${column004}&&column011=${column011}','condition':[{'regex':'column004=运营商市场部&&column011=10','result':true}],'dimension':'executionState','allCondition':'总计','emptyCondition':'未填写'},{'title':'项目流程状态（直签+非直签）','params':['closeProcessState'],'dimension':'closeProcessState','allCondition':'总计','emptyCondition':'未填写'},{'title':'可闭环项目数','dimensions':[{'params':['column004','column011','projectPlanState'],'expression':'column004=${column004}&&column011=${column011}&&projectPlanState=${projectPlanState}','condition':[{'regex':'column004=运营商市场部&&column011=10&&projectPlanState=47','result':true}],'dimension':'直签可闭环'},{'params':['column004','column011','executionState'],'expression':'column004=${column004}&&column011=${column011}&&executionState=${executionState}','condition':[{'regex':'column004=运营商市场部&&column011=10','result':false,'queryResultName':'exceptZQYY','isReverse':true},{'regex':'executionState=(50|60|70|80|90)','result':true}],'dimension':'非直签可闭环'}],'allCondition':'总计','isQueryUnion':true}]";
			String summaryStr = basicDataService.querySysArg("pm.report.project.summary.status");
			List<LinkedHashMap> summaryOptions = JSON.parseArray(summaryStr, LinkedHashMap.class);
			Map<String, Object> summary = new LinkedHashMap<String, Object>();
			Map<String, Object> totalSummary = (Map<String, Object>) summary.getOrDefault("全国", new LinkedHashMap<>());
			for (Map<String, Object> map : list) {
				String column001 = (String) map.get("column001");
				String officeName = (String) map.get("officeName");
//				String column004 = (String) map.get("column004");
//				String column011 = (String) map.get("column011");
//				String projectPlanState = (String) map.get("projectPlanState");
//				String executionState = (String) map.get("executionState");
//				String closeProcessState = (String) map.get("closeProcessState");
				
				Map<String, Object> officeSummary = (Map<String, Object>) summary.getOrDefault(officeName, new LinkedHashMap<>());
				HashMap<Object, Object> queryOffice = new HashMap<>();
				queryOffice.put("column001", column001);
				officeSummary.put("query", queryOffice);
				for (Map options : summaryOptions) {
					summaryDimensionStatus(options, map, config, officeSummary);
					summaryDimensionStatus(options, map, config, totalSummary);
				}
				summary.put(officeName, officeSummary);
			}
			summary.put("全国", totalSummary);
//			System.out.println(JSON.toJSONString(summary));
			
			// 转化为List
			dataList = new ArrayList<Map<String,Object>>(summary.size());
			StringBuilder titleHeader = new StringBuilder();
			List<String> subTitleHeader = new ArrayList<String>();
			boolean isFirst = true;
			String[] tdClassArr = new String[] {"bg-info", "bg-warning", "bg-success", "bg-danger"};
			for (Entry<String, Object> officeSummary : summary.entrySet()) {
				Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
				Map<String, Object> summaryInfo = (Map<String, Object>) officeSummary.getValue();
				Map<String, Object> summaryStatus = (Map<String, Object>) summaryInfo.getOrDefault("summary", Collections.emptyMap());
				Map<String, Object> officeQuery = (Map<String, Object>) summaryInfo.getOrDefault("query", Collections.emptyMap());
				dataMap.put("officeName", officeSummary.getKey());
				dataMap.put("summaryStatus", summaryStatus);
				
				List<Integer> valueTd = new ArrayList<Integer>();
				List<Object> valueRenderedTd = new ArrayList<Object>();
				int summaryIndex = 0;
				for (Entry<String, Object> status : summaryStatus.entrySet()) {
					String tdClass = tdClassArr[summaryIndex++];
					String title = status.getKey();
					Map<String, Object> dimensionSummary = (Map<String, Object>) status.getValue();
					// 初始化标题
					if (isFirst) {
						String thTagPrefixHtml = "<th class='" + tdClass;
						String thTagHtml = thTagPrefixHtml + "'>";
						titleHeader.append("</th>").append(thTagPrefixHtml).append("' colspan='").append(dimensionSummary.size()).append("'>").append(title);
						subTitleHeader.add(thTagHtml + StringUtils.join(dimensionSummary.keySet(), "</th>" + thTagHtml));
					}
//					valueTd.addAll(dimensionSummary.values());
					
					// 渲染单元格
					for (Entry<String, Object> summaryCount : dimensionSummary.entrySet()) {
						String key = summaryCount.getKey();
						Map<String, Object> value = (Map<String, Object>) summaryCount.getValue();
						Integer count = (Integer) value.getOrDefault("count", 0);
						Map<String, Object> query = (Map<String, Object>) value.get("query");
						Collection<Map<String, Object>> querys = (Collection<Map<String, Object>>) value.get("querys");
						if (query == null) {
							query = new HashMap<String, Object>();
						}
						query.putAll(officeQuery);
						if (querys != null) {
							query.put("querys", querys);
						}
						valueTd.add(count);
						StringBuilder renderedTd = new StringBuilder("<td class='").append(tdClass).append("'>");
						if (count > 0) {
//							valueRenderedTd.add("<a href='javascript:void(0);' onclick='summaryInfo(this)' data-query='" + JSON.toJSONString(query) + "'>" + count + "</a>");
							renderedTd.append("<a href='javascript:void(0);' onclick='summaryInfo(this)' data-query='" + JSON.toJSONString(query) + "'>" + count + "</a>");
						} else {
//							valueRenderedTd.add(count);
							renderedTd.append(count);
						}
						valueRenderedTd.add(renderedTd);
					}
				}
				isFirst = false;
//				valueTd.add(0, null);
				valueRenderedTd.add(0, null);
				dataMap.put("expendSummaryStatus", StringUtils.join(valueTd, "</td><td>"));
				dataMap.put("expendSummaryStatusHtml", StringUtils.join(valueRenderedTd, "</td>"));
				
				dataList.add(dataMap);
			}
			subTitleHeader.add(0, "");
			dataJson = StringUtils.join(subTitleHeader, "</th>");
			if (!getServletRequest().getParameterMap().containsKey("6578706f7274")) {
				dataJson = titleHeader.toString() + "</tr><tr><th>" + dataJson;
			}
		}
		if (isSub) {
			return SUCCESS;
		}
		return "projectSummaryStatus";
	}
	
	/**
	 * 将基础数据类型转换为map，筛选有用的Key
	 * @param beans
	 * @param keys
	 * @return
	 */
	private Map<String, String> converter2Map(List<BasicDataBean> beans, Collection<String> filterKeys) {
		if (beans == null || beans.isEmpty()) {
			return Collections.emptyMap();
		}
		boolean needFilter = filterKeys != null && !filterKeys.isEmpty();
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (BasicDataBean basicDataBean : beans) {
			String key = basicDataBean.getBasicDataId();
			if (needFilter && !filterKeys.contains(key)) {
				continue;
			}
			map.put(key, basicDataBean.getBasicDataName());
		}
		return map;
	}
	
	private String fillExpressionParams(String expression, Collection<String> params, Map<String, Object> values) {
		for (String param : params) {
			expression = expression.replaceAll("\\Q${" + param + "}\\E", String.valueOf(values.get(param)));
		}
		return expression;
	}
	
	private Map summaryDimensionStatus(Map<String, Object> options, Map<String, Object> map, Map<String, Object> config, Map<String, Object> officeSummary) {
		String title = (String) options.get("title");
		List<Map> dimensions = (List) options.get("dimensions");
		if (dimensions == null) {
			dimensions = Arrays.asList(options);
		}
		Boolean isQueryUnion = (Boolean) options.getOrDefault("isQueryUnion", false);
		for (Map dimensionMap : dimensions) {
			String dimension = (String) dimensionMap.get("dimension");
			String dimensionValue = (String) map.getOrDefault(dimension, dimension);
			List<String> params = (List<String>) dimensionMap.getOrDefault("params", Collections.emptyList());
			String expression = (String) dimensionMap.get("expression");
			String allCondition = (String) dimensionMap.getOrDefault("allCondition", options.get("allCondition"));
			boolean isMatch = true;
//			Map<String, Object> officeQuery = (Map<String, Object>) officeSummary.getOrDefault("query", Collections.emptyMap());
			Map<String, Object> query = (Map<String, Object>) dimensionMap.get("query");
			if (query == null) {
				query = new HashMap<String, Object>(/* officeQuery */);
			}
			if (StringUtils.isNoneBlank(expression)) {
				String expressionV = fillExpressionParams(expression, params, map);
				List<Map> conditions = (List<Map>) dimensionMap.get("condition");
				// 是否将conditions解析为查询参数
				Boolean isQueryParsed = (Boolean) dimensionMap.getOrDefault("isQueryParsed", false);
				for (Map condition : conditions) {
					String regex = (String) condition.get("regex");
					String queryResultName =  (String) condition.get("queryResultName");
					Boolean isReverse =  (Boolean) condition.getOrDefault("isReverse", false);
					Boolean result =  (Boolean) condition.getOrDefault("result", true);
					
					// 如果没有解析查询参数，则进行解析，将regex解析成查询参数
					if (!Boolean.TRUE.equals(isQueryParsed) ) {
						String queryStr = "{'" + regex.replaceAll("=", "':'")
								.replaceAll("&&", "','")
								.replaceAll("\\(", "")
								.replaceAll("\\)", "")
								.replaceAll("\\|", ",") + "'}";
						Map<String, Object> queryInner = JSON.parseObject(queryStr, Map.class);
						if (query == null || query.isEmpty()) {
							query = queryInner;
						} else {
							query.putAll(queryInner);
						}
						if (StringUtils.isNotBlank(queryResultName)) {
							query.put(queryResultName, isReverse ^ result);
						}
					}
					
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(expressionV);
					if (!result.equals(matcher.find())) {
						isMatch = false;
						break;
					}
				}
				dimensionMap.put("isQueryParsed", true); 
			}
			dimensionMap.put("query", query);
			
			Map<String, String> dimensionValueMap = (Map) config.getOrDefault(dimension + "Map", Collections.emptyMap());
			Map<Collection<String>, String> dimensionValueRelation = (Map) config.getOrDefault(dimension + "Relation", Collections.emptyMap());
			String dimensionKey = dimensionValue;
			String dimensionSourceKey = dimensionValue;
			Map<String, Object> summaryInfo = (Map<String, Object>) officeSummary.getOrDefault("summary", new LinkedHashMap<>());
			Map<String, Object> dimensionSummary = (Map<String, Object>) summaryInfo.get(title);
			boolean isFirst = dimensionSummary == null;
			if (isFirst) {
				dimensionSummary = new LinkedHashMap<String, Object>();
			}
			// 初始化状态值和状态Map
			if (!dimensionValueRelation.isEmpty()) {
				// 如果存在值对应关系，则根据值对应关系进行统计
				for (Entry<Collection<String>, String> entry : dimensionValueRelation.entrySet()) {
					if (isFirst) {
						LinkedHashMap value = new LinkedHashMap();
						value.put("count", 0);
						HashMap<String, Object> valueQuery = new HashMap<>(query);
						valueQuery.put(dimension, StringUtils.join(entry.getKey(), ","));
						value.put("query", valueQuery);
						dimensionSummary.put(entry.getValue(), value);
					}
					if (entry.getKey().contains(dimensionValue)) {
						dimensionSourceKey = StringUtils.join(entry.getKey(), ",");
						dimensionKey = entry.getValue();
					}
				}
			} else if (!dimensionValueMap.isEmpty()) {
				// 如果存在值，则根据值进行统计
				if (isFirst) {
					for (Entry<String, String> entry : dimensionValueMap.entrySet()) {
						LinkedHashMap value = new LinkedHashMap();
						value.put("count", 0);
						HashMap<String, Object> valueQuery = new HashMap<>(query);
						valueQuery.put(dimension, entry.getKey());
						value.put("query", valueQuery);
						dimensionSummary.put(entry.getValue(), value);
					}
				}
				dimensionKey = dimensionValueMap.getOrDefault(dimensionValue, dimensionValue);
			} else {
				// 如果都不存在，则根据dimensions来统计
				if (isFirst) {
					for (Map entry : dimensions) {
						String tdimension = (String) entry.get("dimension");
						String tdimensionValue = (String) map.getOrDefault(tdimension, tdimension);
						LinkedHashMap value = new LinkedHashMap();
						value.put("count", 0);
						if (dimension.equals(tdimension)) {
							value.put("query", new HashMap<>(query));
						}
						dimensionSummary.put(tdimensionValue, value);
					}
				} 
			}
			// 进行统计值累加
			if (isMatch) {
				Map<String, Object> value = (Map<String, Object>) dimensionSummary.get(dimensionKey);
				if (value == null) {
					value = new LinkedHashMap<String, Object>();
				}
				Integer count = (Integer) value.getOrDefault("count", 0);
				value.put("count", count + 1);
				
				if (value.get("query") == null) {
					HashMap<String, Object> valueQuery = new HashMap<>(query);
					valueQuery.put(dimension, dimensionSourceKey);
					value.put("query", valueQuery);
				}
				dimensionSummary.put(dimensionKey, value);
			}
			// 是否有总计，如果有进行求和
			if (StringUtils.isNotBlank(allCondition)) {
				Map<String, Object> allValue = (Map<String, Object>) dimensionSummary.get(allCondition);
				if (allValue == null) {
					allValue = new LinkedHashMap<String, Object>();
				}
				// 初始化总计的查询条件
				if (isQueryUnion) {
					Collection<Map<String, Object>> querys = (Collection<Map<String, Object>>) allValue.get("querys");
					if (querys == null) {
						querys = new HashSet<Map<String, Object>>(dimensions.size());
					}
					querys.add(query);
					allValue.put("querys", querys);
				} else {
					if (allValue.get("query") == null) {
						HashMap<String, Object> valueQuery = new HashMap<>(query);
						allValue.put("query", valueQuery);
					}
				}
				Integer count = (Integer) allValue.getOrDefault("count", 0);
				allValue.put("count", isMatch ? (count + 1) : count);
				dimensionSummary.put(allCondition, allValue);
			}
			summaryInfo.put(title, dimensionSummary);
			officeSummary.put("summary", summaryInfo);
		}
		return officeSummary;
	}

	public List<Department> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Department> officeList) {
		this.officeList = officeList;
	}

	public List<BasicDataBean> getNavTabList() {
		return navTabList;
	}

	public void setNavTabList(List<BasicDataBean> navTabList) {
		this.navTabList = navTabList;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setDepartmentManageService(
			DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDataJson() {
		return dataJson;
	}

	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}

	public String getDataTypeCode() {
		return dataTypeCode;
	}

	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public StatisticsSummarize getSummarize() {
		return summarize;
	}

	public void setSummarize(StatisticsSummarize summarize) {
		this.summarize = summarize;
	}
	public String getAssignedTableHtml() {
		return assignedTableHtml;
	}
	public void setAssignedTableHtml(String assignedTableHtml) {
		this.assignedTableHtml = assignedTableHtml;
	}
	public String getTraceTableHtml() {
		return traceTableHtml;
	}
	public void setTraceTableHtml(String traceTableHtml) {
		this.traceTableHtml = traceTableHtml;
	}
	public String getQualityTableHtml() {
		return qualityTableHtml;
	}
	public void setQualityTableHtml(String qualityTableHtml) {
		this.qualityTableHtml = qualityTableHtml;
	}
	public String getImpl0TableHtml() {
		return impl0TableHtml;
	}
	public void setImpl0TableHtml(String impl0TableHtml) {
		this.impl0TableHtml = impl0TableHtml;
	}
	public String getImpl1TableHtml() {
		return impl1TableHtml;
	}
	public void setImpl1TableHtml(String impl1TableHtml) {
		this.impl1TableHtml = impl1TableHtml;
	}
	public String getImpl3TableHtml() {
		return impl3TableHtml;
	}
	public void setImpl3TableHtml(String impl3TableHtml) {
		this.impl3TableHtml = impl3TableHtml;
	}
	public String getCloseTableHtml() {
		return closeTableHtml;
	}
	public void setCloseTableHtml(String closeTableHtml) {
		this.closeTableHtml = closeTableHtml;
	}
	public Map<String, Object> getQueryParamMap() {
		return queryParamMap;
	}
	public void setQueryParamMap(Map<String, Object> queryParamMap) {
		this.queryParamMap = queryParamMap;
	}
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	/**
	 * 按时间降序排序
	 * @param a
	 * @param b
	 * @param c
	 */
	private void arraySort(Date[] a , String[] b , Double[] c){
		int n = a.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - 1; j++) {
				if (a[j].after(a[j + 1])) {
					Date temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
					
					String tmp = b[j];
					b[j] = b[j + 1];
					b[j + 1] = tmp;
					
					double tp = c[j];
					c[j] = c[j + 1];
					c[j + 1] = tp;
				}
			}
		}
	}
	
	/**
	 * 按分值降序排列
	 * @param a
	 * @param b
	 */
	private void arraySort(Double[] a, String[] b) {
		int n = a.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - 1; j++) {
				if (a[j] < a[j + 1]) {
					double temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
					
					String tmp = b[j];
					b[j] = b[j + 1];
					b[j + 1] = tmp;
				}
			}
		}
	}
	@Deprecated
	public String input() {
		// 办事处
		officeList = departmentManageService.queryDepartments();
		
		// 选项卡
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_DATA_TAB);

		return "input";
	}
}
