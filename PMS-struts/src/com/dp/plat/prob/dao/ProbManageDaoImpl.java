package com.dp.plat.prob.dao;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.BaseDao;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbFile;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.ProbStatistic;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.util.MessageUtil;

public class ProbManageDaoImpl extends BaseDao implements ProbManageDao {

	@Override
	public int saveProb(Prob prob) {
		prob.setCreateBy(getCurrUsername());
		prob.setTrackingUser(getCurrUsername());// 跟踪人默认为当前创建技术公告用户
		return (int) getSqlMapClientTemplate().insert("insert_into_prob", prob);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Prob> queryProbList(Prob prob, DisplayParam displayParam) {
		if (prob == null) {
			prob = new Prob();
		}
		UserContext userContext = UserContext.getUserContext();
		String username = getCurrUsername();
		if (userContext.isHasRole(MessageUtil.ROLE_ADMIN) || userContext.isHasRole(MessageUtil.ROLE_PROB_ADMIN)
				|| userContext.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)) {
			prob.setVisibleRange(-1);
		} else if (userContext.isHasRole(MessageUtil.ROLE_PROB_RD)){
			prob.setTrackingUser(username);
		}
		
		int total = (int) getSqlMapClientTemplate().queryForObject("query_prob_count", prob);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
		}
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prob", prob);
		paramMap.put("displayParam", displayParam);
		// 设置group_contract的最大长度，默认1024
		getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
		return getSqlMapClientTemplate().queryForList("query_prob_list", paramMap);
	}

	@Override
	public Prob queryOneProb(Prob prob) {
		return (Prob) getSqlMapClientTemplate().queryForObject("query_prob_one", prob);
	}

	@Override
	public void updateProb(Prob prob) {
		prob.setUpdateBy(getCurrUsername());
		getSqlMapClientTemplate().update("update_prob", prob);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SoftVersion> checkSoftVersionList(SoftVersion softVersion) {
		return getSqlMapClientTemplate().queryForList("check_soft_version_list", softVersion);
	}

	@Override
	public void updateInvalidSoftVersion(int probId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probId", probId);
		paramMap.put("updateBy", getCurrUsername());
		getSqlMapClientTemplate().update("update_invalid_softversion", paramMap);
	}

	@Override
	public void saveSoftVersion(List<SoftVersion> softVersionList, int probId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probId", probId);
		paramMap.put("list", softVersionList);
		paramMap.put("createBy", getCurrUsername());
		getSqlMapClientTemplate().update("insert_into_softversion", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SoftVersion> querySoftVersionList(int probId) {
		return getSqlMapClientTemplate().queryForList("query_prob_soft_version", probId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> queryProbFileMap(int probId) {
		return getSqlMapClientTemplate().queryForMap("query_prob_file_map", probId, "fileId", "fileName");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbRestore> queryProbRestoreList(ProbRestore probRestore, DisplayParam restoreDisplayParam) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probRestore", probRestore);
		paramMap.put("displayParam", restoreDisplayParam);
		return getSqlMapClientTemplate().queryForList("query_prob_restore_list", paramMap);
	}

	@Override
	public void insertBatchProbRestoreTask(ProbRestore probRestore, List<ProbRestore> probRestoreTaskList) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probRestore", probRestore);
		paramMap.put("list", probRestoreTaskList);
		paramMap.put("createBy", getCurrUsername());
		getSqlMapClientTemplate().insert("insert_batch_probRestore_task_list", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbRestore> queryProbRestoreTaskList(ProbRestore probRestore, DisplayParam displayParam)
			throws UnsupportedEncodingException {
		if (displayParam == null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("probRestore", probRestore);
			return getSqlMapClientTemplate().queryForList("query_list_probRestore_task", paramMap);
		}
		int total = (int) getSqlMapClientTemplate().queryForObject("query_count_probRestore_task", probRestore);
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
			displayParam.setOffset(0);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probRestore", probRestore);
		paramMap.put("displayParam", displayParam);
		return getSqlMapClientTemplate().queryForList("query_list_probRestore_task", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbRestore> queryProbRestoreTaskProjectList(ProbRestore probRestore, DisplayParam displayParam)
			throws UnsupportedEncodingException {
		if (displayParam == null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("probRestore", probRestore);
			return getSqlMapClientTemplate().queryForList("query_list_probRestore_task_project", paramMap);
		}
		System.out
				.println(getSqlMapClientTemplate().queryForObject("query_count_probRestore_task_project", probRestore));
		int total = (int) getSqlMapClientTemplate().queryForObject("query_count_probRestore_task_project", probRestore);
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
			displayParam.setOffset(0);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probRestore", probRestore);
		paramMap.put("displayParam", displayParam);
		return getSqlMapClientTemplate().queryForList("query_list_probRestore_task_project", paramMap);
	}

	@Override
	public int insertProbRestoreProcess(ProbRestore probRestore) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probRestore", probRestore);
		paramMap.put("createBy", getCurrUsername());
		return (int) getSqlMapClientTemplate().insert("insert_restore_process", paramMap);
	}

	@Override
	public void updateProbRestore(int processId, String restoreIds, String assignee) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("processId", processId);
		paramMap.put("restoreIds", restoreIds);
		paramMap.put("assignee", assignee);
		paramMap.put("updateBy", getCurrUsername());
		getSqlMapClientTemplate().update("update_prob_restore_processId", paramMap);
	}

	@Override
	public void updateProbRestoreAssignee(ProbRestore probRestore, String restoreIds) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("assignee", probRestore.getAssignee());
		paramMap.put("restoreIds", restoreIds);
		paramMap.put("updateBy", getCurrUsername());
		getSqlMapClientTemplate().update("update_prob_restore_assignee", paramMap);
	}

	@Override
	public int queryProbRestoreProcessSize(int probId) {
		return (int) getSqlMapClientTemplate().queryForObject("query_prob_restore_process_size", probId);
	}

	@Override
	public void deleteProbInfo(int probId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("updateBy", getCurrUsername());
		paramMap.put("probId", probId);
		getSqlMapClientTemplate().update("delete_prob_info", paramMap);
	}

	@Override
	public int queryNextVal() {
		return (int) getSqlMapClientTemplate().queryForObject("query_next_val");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbFile> queryProbFileList(String attachments) {
		return getSqlMapClientTemplate().queryForList("query_prob_file_list", attachments);
	}

	@Override
	public void insertProbTaskWeekly(int fileId, int probId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fileId", fileId);
		paramMap.put("probId", probId);
		paramMap.put("createBy", getCurrUsername());
		getSqlMapClientTemplate().insert("insert_prob_task_weekly", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbRestoreWeekly> queryProbWeekly(int probId, String username) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probId", probId);
		paramMap.put("uploaduser", username);
		return getSqlMapClientTemplate().queryForList("query_prob_task_weekly", paramMap);
	}

	@Override
	public String queryProbAssigneeEmails(String restoreIds) {
		return (String) getSqlMapClientTemplate().queryForObject("query_prob_assignee_emails", restoreIds);
	}

	@Override
	public void bacthDeleteProbRestores(String probRestoreIds) {
		getSqlMapClientTemplate().delete("batch_delete_probRestores", probRestoreIds);
	}

	@Override
	public void updateProbStatus(Prob prob) {
		getSqlMapClientTemplate().update("update_prob_status", prob);
	}

	@Override
	public String queryProjectIdsByProbRestoreIds(String restoreIds) {
		return (String) getSqlMapClientTemplate().queryForObject("query_projectIds_by_probrestoreIds", restoreIds);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ShipmentInfo> queryHistSoftVersionListByProbRestoreIds(String restoreIds) {
		return getSqlMapClientTemplate().queryForList("query_hist_soft_version_list_by_probRestoreIds", restoreIds);
	}

	@Override
	public String queryOfficeMailsByProbRestoreIds(String restoreIds) {
		return (String) getSqlMapClientTemplate().queryForObject("query_officeMails_by_probRestoreIds", restoreIds);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbParam> queryExportProbList(Map<Object, Object> params) {
		return getSqlMapClientTemplate().queryForList("query_exportProb_list", params);
	}

	@Override
	public void batchAddSoftVersion(List<Object> softVersions) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("list", softVersions);
		paramMap.put("createdBy", getCurrUsername());
		getSqlMapClientTemplate().insert("batch_add_softVersion", paramMap);
	}

	@Override
	public List<ProbStatistic> queryProbStatisticList(ProbStatistic probStatistic, DisplayParam displayParam) {
		return queryProbStatisticListWithReport(probStatistic, displayParam, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProbStatistic> queryProbStatisticListWithReport(ProbStatistic probStatistic, DisplayParam displayParam,
			List<ReportLineData> reportLineDatas) {
		// 设置group_contract的最大长度，默认1024
		getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
		// 创建临时表
		getSqlMapClientTemplate().insert("create_prob_softChangeLog_tempTable", probStatistic);
		getSqlMapClientTemplate().insert("create_prob_statistics_tempTable", probStatistic);

		Integer total = (int) getSqlMapClientTemplate().queryForObject("query_prob_statistics_count", probStatistic);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
		}
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probStatistic", probStatistic);
		paramMap.put("displayParam", displayParam);
		List<ProbStatistic> probStatistics = getSqlMapClientTemplate().queryForList("query_prob_statistics", paramMap);

		if (reportLineDatas != null) {
			// 创建临时表
			getSqlMapClientTemplate().insert("create_prob_statistics_projectTempTable", probStatistic);
			// 查询办事处的项目软件版本更新比
			List<ReportLineData> datas = getSqlMapClientTemplate().queryForList("query_prob_statistics_report");

			// 计算全国总的软件版本更新比
			
//			Integer totolAll = (int) getSqlMapClientTemplate().queryForObject("query_prob_statistics_project_Count", probStatistic);
//			String specificValue = "-";
//			if (totolAll > 0) {
//				NumberFormat numberFormat = NumberFormat.getPercentInstance();
//				numberFormat.setMinimumFractionDigits(2);
//				numberFormat.setMaximumFractionDigits(2);
//				specificValue = numberFormat.format((float) total / totolAll);
//			}
//			ReportLineData all = new ReportLineData("total", "全国", total.toString(), totolAll.toString(), specificValue, null);
			ReportLineData all = (ReportLineData) getSqlMapClientTemplate().queryForObject("query_prob_statistics_report_total");
			reportLineDatas.add(all);
			reportLineDatas.addAll(datas);
			// 删除临时表
			getSqlMapClientTemplate().delete("drop_prob_statistics_projectTempTable");
		}

		// 删除临时表
		getSqlMapClientTemplate().delete("drop_prob_statistics_tempTable");
		getSqlMapClientTemplate().delete("drop_prob_softChangeLog_tempTable");
		return probStatistics;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Project> queryProbStatisticProjectList(ProbStatistic probStatistic, DisplayParam displayParam) {
		// 创建临时表
		getSqlMapClientTemplate().insert("create_prob_statistics_projectTempTable", probStatistic);
		Integer total = (int) getSqlMapClientTemplate().queryForObject("query_prob_statistics_project_Count", probStatistic);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
		}
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probStatistic", probStatistic);
		paramMap.put("displayParam", displayParam);
		List<Project> projects = getSqlMapClientTemplate().queryForList("query_prob_statistics_project", paramMap);
		// 删除临时表
		getSqlMapClientTemplate().delete("drop_prob_statistics_projectTempTable");
		return projects;
	}

	@Override
	public void insertProbReadLog(ProbReadLog probReadLog) {
		getSqlMapClientTemplate().insert("insertProbReadLog", probReadLog);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProbReadLog> queryProbReadLogList(ProbReadLog probReadLog, DisplayParam displayParam) {
		Integer total = (int) getSqlMapClientTemplate().queryForObject("count_prob_read_log", probReadLog);
		if (displayParam.getExport()) {
			displayParam.setPagesize(total);
		}
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(total);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("probReadLog", probReadLog);
		paramMap.put("displayParam", displayParam);
		List<ProbReadLog> probReadLogs = getSqlMapClientTemplate().queryForList("query_prob_read_log", paramMap);
		return probReadLogs;
	}
	
}
