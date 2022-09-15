package com.dp.plat.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.QualityParam;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ReportQueryParam;

public interface ReportDao {
	/**
	 * 查询项目指派率
	 * @param queryParam
	 * @return
	 */
	Map<String, Long> queryAssignedRate(ReportQueryParam queryParam);
	/**
	 * 查询项目跟踪率
	 * @param queryParam
	 * @return
	 */
	Map<String, Long> queryTraceRate(ReportQueryParam queryParam);
	/**
	 * 创建临时表
	 */
	void createQualityTmpTable();
	/**
	 * 查询全国的闭环项目数量、平均得分
	 * @param queryParam
	 * @return
	 */
	QualityParam queryTotalQuality(ReportQueryParam queryParam);
	/**
	 * 查询各办事处的闭环项目数量、平均得分
	 * @param queryParam
	 * @return
	 */
	List<QualityParam> queryOfficeQuality(ReportQueryParam queryParam);
	/**
	 * 查询没有闭环项目的办事处
	 * @param queryParam
	 * @return
	 */
	List<QualityParam> queryOtherOfficeQuality(ReportQueryParam queryParam);
	/**
	 * 查询 每个办事处当月闭环项目数量
	 * @param queryParam
	 * @return
	 */
	Map<String, Long> queryCloseMap(ReportQueryParam queryParam);
	/**
	 * 查询 每个办事处当月新增项目数量
	 * @param queryParam
	 * @return
	 */
	Map<String, Long> queryNewMap(ReportQueryParam queryParam);
	/**
	 * 创建实施方式临时表
	 * @param queryParam
	 */
	void createImplTmptable(ReportQueryParam queryParam);
	/**
	 * 查询实施方式占比
	 * @param queryParam
	 * @return
	 */
	Map<String, Long> queryImplWayMap(ReportQueryParam queryParam);
	/**
	 * 
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	Map<Date, String> queryLineData(String officeCode, String dataTypeCode);
	/**
	 * 查询全国项目数量
	 * @return
	 */
	int queryTotalNum();
	/**
	 * 查询工程类项目数量
	 * @return
	 */
	int queryEngineeringTypeNum();
	/**
	 * 普通类项目数量
	 * @return
	 */
	int queryCommonTypeNum();
	/**
	 * 指派项目经理数量
	 * @return
	 */
	int queryAssignedNum();
	/**
	 * 在跟踪项目数量
	 * @return
	 */
	int queryTraceNum();
	/**
	 * 统计项目指派率 ，没有时间限制 ，按降序排列
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineAssignedData(ReportQueryParam queryParam);
	/**
	 * batch insert pm_report_line_data
	 * @param datas
	 * @param dataTypeCode
	 */
	void insertReportLineDataByList(List<ReportLineData> datas,
			String dataTypeCode);
	/**
	 * 统计项目经理跟踪率 ，没有时间限制 ，按降序排列
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineTraceData(ReportQueryParam queryParam);
	/**
	 * 统计闭环新增比数据，按月统计 ， 按降序排列
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineClosedData(ReportQueryParam queryParam);
	/**
	 * 统计闭环项目数量，分数，按季度统计 ，按降序排列
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineQualityData(ReportQueryParam queryParam);
	/**
	 * 统计没有闭环项目的办事处
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineNoQualityData(ReportQueryParam queryParam);
	/**
	 * 删除项目质量管理临时表
	 */
	void deleteQualityTmpTable();
	
	Map<Date, String> queryLineQualityData(String officeCode,
			String dataTypeCode);
	/**
	 * 统计企业网项目实施方式占比 ，按月统计，按降序排列
	 * @param queryParam
	 * @return
	 */
	List<ReportLineData> queryReportLineImplData(ReportQueryParam queryParam);
	/**
	 * 查询企业网实现方式趋势图数据
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	List<ReportLineData> queryReportLineImplWayData(String officeCode,
			String dataTypeCode);
	/**
	 * 
	 * @param officeCode
	 * @param dataTypeCode
	 * @return
	 */
	String queryReportSettingTimes(String officeCode, String dataTypeCode);

}
