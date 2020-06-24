package com.dp.plat.prob.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.ProbStatistic;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.service.BaseService;

public interface ProbManageService extends BaseService{
	/**
	 * 保存技术公告主信息
	 * @param prob
	 * @param softVersionList 
	 * @param root 
	 * @throws IOException 
	 */
	int saveProb(Prob prob, List<SoftVersion> softVersionList, String root) throws IOException;
	/**
	 * 查询技术公告列表
	 * @param prob
	 * @param displayParam
	 * @return
	 */
	List<Prob> queryProbList(Prob prob, DisplayParam displayParam);
	/**
	 * 查询单个的技术公告信息
	 * @param prob
	 * @return
	 */
	Prob queryOneProb(Prob prob);
	/**
	 *  更新技术公告信息
	 * @param prob
	 * @param softVersionList 
	 */
	void updateProb(Prob prob, List<SoftVersion> softVersionList);
	/**
	 * 检索符合条件的软件版本
	 * @param softVersion
	 * @return
	 */
	List<SoftVersion> checkSoftVersionList(SoftVersion softVersion);
	/**
	 * 查询受某个技术公告影响的软件版本
	 * @param probId
	 * @return
	 */
	List<SoftVersion> querySoftVersionList(int probId);
	/**
	 * 更新软件版本
	 * @param softVersionList
	 * @param probId
	 */
	void updateProbSoftVersion(List<SoftVersion> softVersionList, int probId);
	/**
	 *  查询技术公告的附件
	 * @param probId
	 * @return
	 */
	Map<Integer, String> queryProbFileMap(int probId);
	/**
	 * 查询设备修复情况的数据对象集合
	 * @param probRestore
	 * @param restoreDisplayParam
	 * @return
	 */
	List<ProbRestore> queryProbRestoreList(ProbRestore probRestore, DisplayParam restoreDisplayParam);
	/**
	 * 创建修复技术公告任务
	 * @param probRestore
	 * @param probRestoreTaskList
	 * @param root 根目录
	 * @throws IOException 
	 */
	void insertBatchProbRestoreTask(ProbRestore probRestore, List<ProbRestore> probRestoreTaskList, String root) throws IOException;
	/**
	 * 查询子任务列表
	 * @param probRestore
	 * @param restoreDisplayParam
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	List<ProbRestore> queryProbRestoreTaskList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException;
	/**
	 * 更新技术公告任务状态
	 * @param probRestore
	 * @param restoreIds
	 * @param isProbAdmin 
	 * @param root 
	 */
	void updateProbRestoreTask(ProbRestore probRestore, String restoreIds, int isProbAdmin) throws IOException;
	/**
	 * 删除技术公告
	 * @param probId
	 */
	void deleteProbInfo(int probId);
	/**
	 * 查询技术公告编号
	 * @return
	 */
	String queryNextProbNum();
	/**
	 * 保存发布邮件
	 * @param prob 技术公告主信息 必须要有主键
	 * @param softVersionList 可以为空
	 * @param remark 邮件主题
	 * @param root 根目录
	 * @param files 附件
	 * @param bccs 密送邮件地址
	 */
	void keepRelaseEmail(Prob prob, List<SoftVersion> softVersionList , String remark, String root ,String files , String  bccs) throws IOException;
	/**
	 * 保存进展周报
	 * @param fileId
	 * @param probId
	 * @param root 
	 * @param weeklyPath 
	 * @throws IOException 
	 */
	void insertProbTaskWeekly(int fileId, int probId, String root, String weeklyPath) throws IOException;
	/**
	 * 查询进展附件
	 * @param probId
	 * @param string
	 * @return
	 */
	List<ProbRestoreWeekly> queryProbWeekly(int probId, String username);
	/**
	 * 批量删除子任务
	 * @param probRestoreIds
	 */
	void bacthDeleteProbRestores(String probRestoreIds);
	/**
	 * 更新Prob status 主要用于技术公告原审批
	 * @param prob
	 */
	void updateProbStatus(Prob prob);
	/**
	 * 查询子任务设计的项目列表
	 * @param probRestore
	 * @param restoreDisplayParam
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	List<ProbRestore> queryProbRestoreTaskProjectList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException;
	/**
	 * 查询导出技术公告数据
	 * @param hashMap
	 */
	List<ProbParam> queryExportProbList(Map<Object, Object> hashMap);
	/**
	 * 批量导入软件版本信息
	 * @param softVersions
	 */
	void batchAddSoftVersion(List<Object> softVersions);

	/**
	 * 查询已维护的项目软件版本统计表
	 * @param probStatistic
	 * @param displayParam 
	 * @return
	 */
	List<ProbStatistic> queryProbStatisticList(ProbStatistic probStatistic, DisplayParam displayParam);
	/**
	 * 查询已维护的项目软件版本统计表,附带报表数据
	 * @param probStatistic
	 * @param displayParam
	 * @param reportLineDatas
	 * @return
	 */
	List<ProbStatistic> queryProbStatisticListWithReport(ProbStatistic probStatistic, DisplayParam displayParam,
			List<ReportLineData> reportLineDatas);
	/**
	 * 查询原厂直服的项目列表
	 * @param probStatistic
	 * @param displayParam
	 * @return
	 */
	List<Project> queryProbStatisticProjectList(ProbStatistic probStatistic, DisplayParam displayParam);
	/**
	 * @param probId
	 * @param status
	 */
	void readLog(int probId, int status);
	/**
	 * @param probReadLog
	 */
	void insertProbReadLog(ProbReadLog probReadLog);
	
	/**
	 * @param probReadLog
	 * @param displayParam
	 * @return
	 */
	List<ProbReadLog> queryProbReadLogList(ProbReadLog probReadLog, DisplayParam displayParam);
}
