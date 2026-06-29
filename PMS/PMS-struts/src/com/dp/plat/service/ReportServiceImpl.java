package com.dp.plat.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.dao.ReportDao;
import com.dp.plat.data.bean.QualityParam;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ReportQueryParam;
import com.dp.plat.data.bean.StatisticsSummarize;
import com.dp.plat.param.ReportDataTypeParam;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MessageUtil;

public class ReportServiceImpl extends BaseServiceImpl implements ReportService{
	private ReportDao reportDao;
	
	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	@Override
	public Map<String, Double> queryAssignedRate(ReportQueryParam queryParam) {
		queryParam.setIsALl(0);
		Map<String, Long> allMap = reportDao.queryAssignedRate(queryParam);
		queryParam.setIsALl(1);
		Map<String, Long > zdMap = reportDao.queryAssignedRate(queryParam);
		
		Map<String, Double> rateMap = new HashMap<String, Double>();
		DecimalFormat df  = new DecimalFormat("###.00");
		long all = 0;
		long zd = 0;
		for(String officeCode : allMap.keySet()){
			all += (allMap.get(officeCode) == null ? 0:allMap.get(officeCode));
			zd += (zdMap.get(officeCode) == null ? 0 : zdMap.get(officeCode)) ;
			double rate = ((double) (zdMap.get(officeCode) == null ? 0 : zdMap.get(officeCode))
					/ (double) (allMap.get(officeCode) == null ? 0 : allMap.get(officeCode))) * 100;
			
			if (Double.isInfinite(rate) || Double.isNaN(rate)) {
				rate = 0;
			}
			rateMap.put(officeCode, Double.parseDouble(df.format(rate)));
		}
		double total = (double)zd / (double)all*100;
		if (Double.isInfinite(total) || Double.isNaN(total) || Double.isNaN(total)) {
			total = 0;
		}
		rateMap.put("total",  Double.parseDouble(df.format(total)));
		return rateMap;
	}

	@Override
	public Map<String, Double> queryTraceRate(ReportQueryParam queryParam) {
		queryParam.setIsALl(0);
		Map<String, Long> allMap = reportDao.queryTraceRate(queryParam);
		queryParam.setIsALl(1);
		Map<String, Long > zdMap = reportDao.queryTraceRate(queryParam);
		
		Map<String, Double> rateMap = new HashMap<String, Double>();
		DecimalFormat df  = new DecimalFormat("###.00");
		long all = 0;
		long zd = 0;
		for(String officeCode : allMap.keySet()){
			all += (allMap.get(officeCode) == null ? 0:allMap.get(officeCode));
			zd += (zdMap.get(officeCode) == null ? 0 : zdMap.get(officeCode)) ;
			double rate = ((double) (zdMap.get(officeCode) == null ? 0 : zdMap.get(officeCode) ) /(double)(allMap.get(officeCode) == null ? 0:allMap.get(officeCode) ))*100;
			if (Double.isInfinite(rate) || Double.isNaN(rate)) {
				rate = 0;
			}
			rateMap.put(officeCode, Double.parseDouble(df.format(rate)));
		}
		double total = (double) zd / (double) all * 100;
		if (Double.isInfinite(total) || Double.isNaN(total)) {
			total = 0;
		}
		rateMap.put("total", Double.parseDouble(df.format(total)));
		return rateMap;
	}

	@Override
	public List<QualityParam> queryQualityList(ReportQueryParam queryParam) {
		try {
			//创建临时表
			reportDao.createQualityTmpTable();
			
			// 统计除去非直签督导的项目各办事处闭环平均分和闭环数量
			List<QualityParam> remainderOfficeParams = null;
			List<QualityParam> remainderOtherOfficeParams = null;
			//查询各办事处的闭环项目数量、平均得分
			remainderOfficeParams = reportDao.queryOfficeQuality(queryParam);
			//查询没有闭环项目的办事处
			remainderOtherOfficeParams = reportDao.queryOtherOfficeQuality(queryParam);
			// 查询全国项目质量管理数据 ，闭环平均分、闭环项目数量
			QualityParam remainderTotalQuality = reportDao.queryTotalQuality(queryParam);
			
			remainderOfficeParams.add(0, remainderTotalQuality);
			remainderOfficeParams.addAll(remainderOtherOfficeParams);
			return remainderOfficeParams;
		} finally {
			reportDao.deleteQualityTmpTable();
		}
	}

	@Override
	public Map<String,List<QualityParam>> queryTotalAndRemainderList(ReportQueryParam queryParam) {
		try{
			//创建临时表
			reportDao.createQualityTmpTable();
			
			// 统计所有项目各办事处闭环平均分和闭环数量
			List<QualityParam> totalOfficeParams = null;
			List<QualityParam> totalOtherOfficeParams = null;
			totalOfficeParams = reportDao.queryOfficeQuality(queryParam);
			//查询没有闭环项目的办事处
			totalOtherOfficeParams = reportDao.queryOtherOfficeQuality(queryParam);
			// 查询全国项目质量管理数据 ，闭环平均分、闭环项目数量
			QualityParam totalTotalQuality = reportDao.queryTotalQuality(queryParam);
			totalOfficeParams.add(0, totalTotalQuality);
			totalOfficeParams.addAll(totalOtherOfficeParams);
			
			// 统计除去非直签督导、代理商/用户自服的项目各办事处闭环平均分和闭环数量
			queryParam.setImplWay(MessageUtil.IMPL_WAY_0);// 原厂直服
			queryParam.setProjectCategory(MessageUtil.PROJECT_TYPE_ENGINEE);// 20：非直签，查询是去除非直签，考虑null影响。不考虑可直接传10：直签
			queryParam.setIsALl(MessageUtil.IMPL_WAY_3);// 附加传值，用来传代理商/用户自服
			List<QualityParam> remainderOfficeParams = null;
			List<QualityParam> remainderOtherOfficeParams = null;
			//查询各办事处的闭环项目数量、平均得分
			remainderOfficeParams = reportDao.queryOfficeQuality(queryParam);
			//查询没有闭环项目的办事处
			remainderOtherOfficeParams = reportDao.queryOtherOfficeQuality(queryParam);
			// 查询全国项目质量管理数据 ，闭环平均分、闭环项目数量
			QualityParam remainderTotalQuality = reportDao.queryTotalQuality(queryParam);
			remainderOfficeParams.add(0, remainderTotalQuality);
			remainderOfficeParams.addAll(remainderOtherOfficeParams);
			
			HashMap<String,List<QualityParam>> all =  new HashMap<>();
			all.put("remainder",remainderOfficeParams);
			all.put("total",totalOfficeParams);
			return all;
		} finally {
			reportDao.deleteQualityTmpTable();
		}
	}
	
	@Override
	public QualityParam queryTotalQuality(ReportQueryParam queryParam) {
		QualityParam quality = null;
		try {
			//创建临时表
			reportDao.createQualityTmpTable();
			quality = reportDao.queryTotalQuality(queryParam);
		} finally {
			reportDao.deleteQualityTmpTable();
		}
		return quality;
	}

	@Override
	public Map<String, Double> queryCloseRate(ReportQueryParam queryParam) {
		Map<String, Long> closeMap = reportDao.queryCloseMap(queryParam);
		Map<String, Long> newMap = reportDao.queryNewMap(queryParam);
		
		Map<String, Double> rateMap = new HashMap<String, Double>();
		DecimalFormat df  = new DecimalFormat("###.00");
		long all = 0;
		long zd = 0;
		for(String officeCode : closeMap.size()> newMap.size() ? closeMap.keySet():newMap.keySet()){
			all += (closeMap.get(officeCode) == null ? 0:closeMap.get(officeCode));
			zd += (newMap.get(officeCode) == null ? 0 : newMap.get(officeCode)) ;
			double rate = ((double) (closeMap.get(officeCode) == null ? 0 : closeMap.get(officeCode))
					/ (double) (newMap.get(officeCode) == null ? 0 : newMap.get(officeCode))) * 100;
			if (Double.isInfinite(rate) || Double.isNaN(rate)) {
				rate = 0;
			}
			rateMap.put(officeCode, Double.parseDouble(df.format(rate)));
		}
		double total = (double)all / (double)zd*100;
		if (Double.isInfinite(total) || Double.isNaN(total)) {
			total = 0;
		}
		rateMap.put("total",  Double.parseDouble(df.format(total)));
		return rateMap;
	}

	@Override
	public Map<String, Long> queryImplWayMap(ReportQueryParam queryParam,
			int implWay) {
		Map<String, Long> wayMap = null;
		try {
			queryParam.setImplWay(implWay);
			//查询各办事处实施方式项目数量
			wayMap = reportDao.queryImplWayMap(queryParam);
		} catch (Exception e) {
			//创建临时表
			/*reportDao.createImplTmptable(queryParam);
			wayMap = reportDao.queryImplWayMap(queryParam);*/
		}
		return wayMap;
	}

	@Override
	public Map<Date, String> queryLineData(String officeCode,
			String dataTypeCode) {
		return reportDao.queryLineData(officeCode , dataTypeCode);
	}

	@Override
	public StatisticsSummarize queryStatisticsSummarize() {
		int totalNum = reportDao.queryTotalNum();
		int engineeringTypeNum = reportDao.queryEngineeringTypeNum();
		int commonTypeNum = reportDao.queryCommonTypeNum();
		int assignedNum = reportDao.queryAssignedNum();
		int traceNum = reportDao.queryTraceNum();
		return new StatisticsSummarize(totalNum, engineeringTypeNum, commonTypeNum, assignedNum, traceNum);
	}

	@Override
	public List<ReportLineData> queryReportLineAssignedData(
			ReportQueryParam queryParam) {
		return reportDao.queryReportLineAssignedData(queryParam);
	}

	@Override
	public void insertReportLineDataByList(List<ReportLineData> datas,
			String dataTypeCode) {
		reportDao.insertReportLineDataByList(datas ,dataTypeCode);
	}

	@Override
	public ReportLineData statisticsTotalData(List<ReportLineData> assignedDatas) {
		DecimalFormat df  = new DecimalFormat("###.00");
		double conditionValue = 0;
		double totalValue = 0;
		for(ReportLineData data : assignedDatas){
			conditionValue += Double.parseDouble(data.getConditionValue());
			totalValue += Double.parseDouble(data.getTotalValue());
		}
		String specificValue ;
		if(totalValue == 0){
			specificValue = "0";
		}else{
			specificValue = df.format((double) conditionValue/totalValue *100);
		}
	
		return new ReportLineData("total" , "全国", String.valueOf(conditionValue).replaceAll("\\.0*$", ""), String.valueOf(totalValue).replaceAll("\\.0*$", ""), specificValue, new Date());
	}

	@Override
	public List<ReportLineData> queryReportLineTraceData(
			ReportQueryParam queryParam) {
		return reportDao.queryReportLineTraceData(queryParam);
	}

	@Override
	public List<ReportLineData> queryReportLineClosedData(
			ReportQueryParam queryParam) {
		return reportDao.queryReportLineClosedData(queryParam);
	}

	@Override
	public List<ReportLineData> queryReportLineQualityData(
			ReportQueryParam queryParam) {
		try{
			//创建临时表
			reportDao.createQualityTmpTable();
			List<ReportLineData> qualityData = reportDao.queryReportLineQualityData(queryParam);
			List<ReportLineData> noQualityData = reportDao.queryReportLineNoQualityData(queryParam);
			qualityData.addAll(noQualityData);
			return qualityData;
		} finally {
			reportDao.deleteQualityTmpTable();
		}
	}

	@Override
	public List<Object> queryReportLineRemainderQualityDataAndTotalsize(ReportQueryParam queryParam) {
		try {
			//创建临时表
			reportDao.createQualityTmpTable();
			// 查询所有项目闭环数量和闭环分数
			List<ReportLineData> totalQualityData = reportDao.queryReportLineQualityData(queryParam);
			List<ReportLineData> totalNoQualityData = reportDao.queryReportLineNoQualityData(queryParam);
			totalQualityData.addAll(totalNoQualityData);
			HashMap<String, String> officeTotalSize = new HashMap<>();
			int total = 0;
			for (ReportLineData temp : totalQualityData) {
				total += Integer.parseInt(temp.getTotalValue());
				officeTotalSize.put(temp.getOfficeCode(), temp.getTotalValue());
			}
			officeTotalSize.put("total", String.valueOf(total));
			// 查询除去非直签督导、代理商/用户自服的项目闭环数量和闭环分数
			queryParam.setImplWay(MessageUtil.IMPL_WAY_0);// 原厂直服
			queryParam.setProjectCategory(MessageUtil.PROJECT_TYPE_ENGINEE);// 20：非直签，查询是去除非直签，考虑null影响。不考虑可直接传10：直签
			queryParam.setIsALl(MessageUtil.IMPL_WAY_3);// 附加传值，用来传代理商/用户自服
			List<ReportLineData> remainderQualityData = reportDao.queryReportLineQualityData(queryParam);
			List<ReportLineData> remainderNoQualityData = reportDao.queryReportLineNoQualityData(queryParam);
			remainderQualityData.addAll(remainderNoQualityData);
			List<Object> list = new ArrayList<Object>();
			list.add(remainderQualityData);
			list.add(officeTotalSize);
			
			return list;
		} finally {
			reportDao.deleteQualityTmpTable();
		}
	}
	
	@Override
	public Map<Date, String> queryLineQualityData(String officeCode,
			String dataTypeCode) {
		return reportDao.queryLineQualityData(officeCode ,dataTypeCode);
	}

	@Override
	public List<ReportLineData> queryReportLineImplData(
			ReportQueryParam queryParam) {
		return reportDao.queryReportLineImplData(queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void keepReportLineData() {
		ReportQueryParam queryParam = new ReportQueryParam();
		queryParam.setStartTime(DateUtil.getFirstDay());
		// 趋势图都改为月度
//		queryParam.setQuarterStartTime(DateUtil.getQuarterFirstDay(new Date()));
		queryParam.setQuarterStartTime(queryParam.getStartTime());
		DecimalFormat df  = new DecimalFormat("###.00");
		
		//统计项目经理指派率
		List<ReportLineData> assignedDatas = this.queryReportLineAssignedData(queryParam);
		ReportLineData totalAssigendData = this.statisticsTotalData(assignedDatas);
		assignedDatas.add(totalAssigendData);
		this.insertReportLineDataByList(assignedDatas , ReportDataTypeParam.REPORT_ASSIGNED_RATE);
		
		//统计项目经理跟踪率
		List<ReportLineData> traceDatas = this.queryReportLineTraceData(queryParam);
		ReportLineData totalTraceData = this.statisticsTotalData(traceDatas);
		traceDatas.add(totalTraceData);
		this.insertReportLineDataByList(traceDatas, ReportDataTypeParam.REPORT_TRACE_RATE);
		
//		// 趋势图都改为月度
//		if(DateUtil.isQuarterLastMonth()){//如果是季度末就执行
			//闭环新增比
			List<ReportLineData> closedDatas = this.queryReportLineClosedData( queryParam );
			ReportLineData totalCloesdData = this.statisticsTotalData(closedDatas);
			closedDatas.add(totalCloesdData);
			this.insertReportLineDataByList(closedDatas, ReportDataTypeParam.REPORT_CLOSE_RATE);
			
			//企业网项目实施方式占比
			List<ReportLineData> implDatas = this.queryReportLineImplData( queryParam );
			this.insertReportLineDataByList(implDatas, null);
			
			//质量管理,除去非直签督导闭环项目
//			List<ReportLineData> qualityDatas = this.queryReportLineQualityData( queryParam );
			List<Object> datas = this.queryReportLineRemainderQualityDataAndTotalsize(queryParam);
			List<ReportLineData> qualityDatas = (List<ReportLineData>) datas.get(0);
			ReportLineData totalQualityData = this.statisticsTotalData(qualityDatas);
			totalQualityData.setSpecificValue(df.format(Double.parseDouble(totalQualityData.getSpecificValue())/100.0));
			qualityDatas.add(totalQualityData);
			this.updateQualityData(qualityDatas, (Map<String, String>) datas.get(1));
			this.insertReportLineDataByList(qualityDatas, ReportDataTypeParam.REPORT_QUALITY_RATE);
//		}
		
	}

//		//质量管理  修改为按月统计，为不影响其他程序部分的调用，在此将季度开始时间参数设置为月度开始时间参数
//		queryParam.setQuarterStartTime(queryParam.getStartTime());
//		List<ReportLineData> qualityDatas = this.queryReportLineQualityData( queryParam );
//		ReportLineData totalQualityData = this.statisticsTotalData(qualityDatas);
//		totalQualityData.setSpecificValue(df.format(Double.parseDouble(totalQualityData.getSpecificValue())/100.0));
//		qualityDatas.add(totalQualityData);
//		this.insertReportLineDataByList(qualityDatas, ReportDataTypeParam.REPORT_QUALITY_RATE);
//  }
	
	/**
	 * 更新各办事处的闭环总数，原totalValue为除去非直签督导的数据
	 * @param qualityDatas
	 * @param officeTotalSize
	 * @return
	 */
	private void updateQualityData(List<ReportLineData> qualityDatas, Map<String, String> officeTotalSize) {
		for (ReportLineData reportLineData : qualityDatas) {
			reportLineData.setTotalValue(officeTotalSize.get(reportLineData.getOfficeCode()));
		}
	}

	@Override
	public Map<String, String> queryReportLineImplWayData(String officeCode,
			String dataTypeCode) {
		List<ReportLineData> list = reportDao.queryReportLineImplWayData(officeCode ,dataTypeCode);
		if(list.size() > 0){
			Map<String, String> map = new HashMap<String  , String>();
			for(ReportLineData data : list){
				map.put(data.getSettings() + data.getDataTypeCode(), data.getSpecificValue());
			}
			return map;
		}
		return  null;
	}

	@Override
	public String queryReportSettingTimes(String officeCode, String dataTypeCode) {
		return reportDao.queryReportSettingTimes(officeCode ,dataTypeCode);
	}

	@Override
	public List<ReportLineData> queryReportTableAssignedData(
			ReportQueryParam queryParam) {
		List<ReportLineData> list = this.queryReportLineAssignedData(queryParam);
		ReportLineData totalAssigendData = this.statisticsTotalData(list);
		List<ReportLineData> allList = new ArrayList<ReportLineData>();
		allList.add(totalAssigendData);
		allList.addAll(list);
		return allList;
	}

	@Override
	public List<ReportLineData> queryReportTableTraceData(
			ReportQueryParam queryParam) {
		List<ReportLineData> traceDatas = this.queryReportLineTraceData(queryParam);
		ReportLineData totalTraceData = this.statisticsTotalData(traceDatas);
		List<ReportLineData> allList = new ArrayList<ReportLineData>();
		allList.add(totalTraceData);
		allList.addAll(traceDatas);
		return allList;
	}

	@Override
	public List<ReportLineData> queryReportTableQualityData(
			ReportQueryParam queryParam) {
		DecimalFormat df  = new DecimalFormat("###.00");
		List<ReportLineData> qualityDatas = this.queryReportLineQualityData( queryParam );
		ReportLineData totalQualityData = this.statisticsTotalData(qualityDatas);
		totalQualityData.setSpecificValue(df.format(Double.parseDouble(totalQualityData.getSpecificValue())/100.0));
		List<ReportLineData> allDatas = new ArrayList<ReportLineData>();
		allDatas.add(totalQualityData);
		allDatas.addAll(qualityDatas);
		return allDatas;
	}

	@Override
	public List<ReportLineData> queryReportTableClosedData(
			ReportQueryParam queryParam) {
		List<ReportLineData> closedDatas = this.queryReportLineClosedData( queryParam );
		ReportLineData totalCloesdData = this.statisticsTotalData(closedDatas);
		List<ReportLineData> allList = new ArrayList<ReportLineData>();
		allList.add(totalCloesdData);
		allList.addAll(closedDatas);
		return allList;
	}

	@Override
	public List<Map<String, Object>> queryProjectSummaryStatus(Map<String, Object> params) {
		return reportDao.queryProjectSummaryStatus(params);
	}

}
