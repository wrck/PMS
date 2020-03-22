package com.dp.plat.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.QualityParam;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ReportQueryParam;

public class ReportDaoImpl extends BaseDao implements ReportDao {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> queryAssignedRate(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForMap("query_assigned_rate", queryParam, "officeCode", "num");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> queryTraceRate(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForMap("query_trace_rate", queryParam, "officeCode", "num");
	}

	@Override
	public void createQualityTmpTable() {
		getSqlMapClientTemplate().update("create_tmp_table_for_quality");
	}

	@Override
	public QualityParam queryTotalQuality(ReportQueryParam queryParam) {
		return (QualityParam) getSqlMapClientTemplate().queryForObject("query_total_quality", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QualityParam> queryOfficeQuality(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_office_quality", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QualityParam> queryOtherOfficeQuality(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_other_office_quality", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> queryCloseMap(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForMap("query_close_project_size", queryParam, "officeCode", "size");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> queryNewMap(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForMap("query_new_project_size", queryParam, "officeCode", "size");
	}

	@Override
	public void createImplTmptable(ReportQueryParam queryParam) {
		getSqlMapClientTemplate().update("create_implway_tmp_table", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> queryImplWayMap(ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForMap("query_implway_size", queryParam, "officeCode", "size");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Date, String> queryLineData(String officeCode,
			String dataTypeCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("officeCode", officeCode);
		params.put("dataTypeCode", dataTypeCode);
		return getSqlMapClientTemplate().queryForMap("query_line_data", params, "settingTime", "specificValue");
	}

	@Override
	public int queryTotalNum() {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_totalNum");
	}

	@Override
	public int queryEngineeringTypeNum() {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_engineeringTypeNum");
	}

	@Override
	public int queryCommonTypeNum() {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_commonTypeNum");
	}

	@Override
	public int queryAssignedNum() {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_assignedNum");
	}

	@Override
	public int queryTraceNum() {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_traceNum");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineAssignedData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_assigned_info", queryParam);
	}

	@Override
	public void insertReportLineDataByList(List<ReportLineData> datas,
			String dataTypeCode) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("list", datas);
		if(dataTypeCode == null){
			getSqlMapClientTemplate().insert("insert_reportline_data_bylist_self", param);
		}else{
			param.put("dataTypeCode", dataTypeCode);
			getSqlMapClientTemplate().insert("insert_reportline_data_bylist", param);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineTraceData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_trace_info", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineClosedData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_closed_info", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineQualityData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_quality_info", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineNoQualityData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_no_quality_info", queryParam);
	}

	@Override
	public void deleteQualityTmpTable() {
		getSqlMapClientTemplate().update("delete_quality_tmp_table");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Date, String> queryLineQualityData(String officeCode,
			String dataTypeCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("officeCode", officeCode);
		params.put("dataTypeCode", dataTypeCode);
		return getSqlMapClientTemplate().queryForMap("query_line_quality_data", params, "settingTime", "totalValue");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineImplData(
			ReportQueryParam queryParam) {
		return getSqlMapClientTemplate().queryForList("query_reportline_impl_info", queryParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportLineData> queryReportLineImplWayData(String officeCode,
			String dataTypeCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("officeCode", officeCode);
		params.put("dataTypeCode", dataTypeCode);
		return getSqlMapClientTemplate().queryForList("query_line_implway_data", params);
	}

	@Override
	public String queryReportSettingTimes(String officeCode, String dataTypeCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("officeCode", officeCode);
		params.put("dataTypeCode", dataTypeCode);
		return (String) getSqlMapClientTemplate().queryForObject("query_report_impl_settingTime", params);
	}

}
