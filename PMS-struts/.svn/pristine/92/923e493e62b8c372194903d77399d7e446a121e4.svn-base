package com.dp.plat.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.QualityParam;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ReportQueryParam;
import com.dp.plat.data.bean.StatisticsSummarize;

/**
 * 数据报表service
 * @author admin
 *
 */
public interface ReportService {
	/**
	 * 查询项目指派率
	 * @param queryParam
	 * @return
	 */
	Map<String, Double> queryAssignedRate(ReportQueryParam queryParam);
	/**
	 * 查询项目经理跟踪率
	 * @param queryParam
	 * @return
	 */
	Map<String, Double> queryTraceRate(ReportQueryParam queryParam);
	/**
	 * 查询项目质量管理数据 ，闭环平均分、闭环项目数量
	 * @param queryParam
	 * @return
	 */
	List<QualityParam> queryQualityList(ReportQueryParam queryParam);
	/**
	 * 查询全国项目质量管理数据 ，闭环平均分、闭环项目数量
	 * @param queryParam
	 * @return
	 */
	QualityParam queryTotalQuality(ReportQueryParam queryParam);
	/**
	 * 查询当月闭环比
	 * @param queryParam
	 * @return
	 */
	Map<String, Double> queryCloseRate(ReportQueryParam queryParam);
	/**
	 * 查询实施方式占比
	 * @param queryParam
	 * @param implWay
	 * @return
	 */
	Map<String, Long> queryImplWayMap(ReportQueryParam queryParam, int implWay);
	/**
	 * 查询趋势图加载的某部分数据
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	Map<Date, String> queryLineData(String officeCode, String dataTypeCode);
	/**
	 * 查询项目情况综述
	 * @return
	 */
	StatisticsSummarize queryStatisticsSummarize();
	
	/**
	 * 查询项目指派率数据
	 */
	List<ReportLineData> queryReportLineAssignedData(ReportQueryParam queryParam);
	/**
	 * 插入报表统计数据 参数为list
	 * @param assignedData
	 * @param reportAssignedRate
	 */
	void insertReportLineDataByList(List<ReportLineData> datas,
			String dataTypeCode);
	/**
	 * 根据各个办事处值，统计全国数据
	 * @param assignedDatas
	 * @return
	 */
	ReportLineData statisticsTotalData(List<ReportLineData> assignedDatas);
	/**
	 * 查询项目经理跟踪率数据
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineTraceData(ReportQueryParam queryParam);
	/**
	 * 查询闭环新增比数据
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineClosedData(ReportQueryParam queryParam);
	/**
	 * 查询闭环项目数量，分数
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineQualityData(ReportQueryParam queryParam);
	/**
	 * 查询闭环项目数量
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	Map<Date, String> queryLineQualityData(String officeCode,
			String dataTypeCode);
	/**
	 * 查询企业网项目实施方式占比数据
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineImplData(ReportQueryParam queryParam);
	/**
	 * 保存报表所有趋势图的数据
	 */
	void keepReportLineData();
	/**
	 * 查询企业网实现方式趋势图数据
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	Map<String, String> queryReportLineImplWayData(String officeCode,
			String dataTypeCode);
	/**
	 * 
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	String queryReportSettingTimes(String officeCode, String dataTypeCode);
	/**
	 * 查询透视表数据--项目经理指派率
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportTableAssignedData(ReportQueryParam queryParam);
	/**
	 * 查询透视表数据--项目经理跟踪率
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportTableTraceData(ReportQueryParam queryParam);
	/**
	 * 查询透视表数据--质量管理
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportTableQualityData(ReportQueryParam queryParam);
	/**
	 * 查询透视表数据--新增闭环比
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportTableClosedData(ReportQueryParam queryParam);
	/**
	 * 查询所有办事处的闭环平均分、数量以及除去非直签督导项目的闭环平均分和数量
	 * @param queryParam
	 * @return map 包含 total - List<QualityParam> ,remainder -  List<QualityParam>
	 */
	Map<String, List<QualityParam>> queryTotalAndRemainderList(ReportQueryParam queryParam);
	/**
	 * 查询各办事处所有闭环项目数量，以及除去非直签督导项目的闭环分数
	 * @param queryParam
	 * @return List 0：<code>List<ReportLineData></code> reportLineQualityData ，1：<code>Map<String,String> officeCode - totalSize</code>
	 */
	List<Object> queryReportLineRemainderQualityDataAndTotalsize(ReportQueryParam queryParam);
}
