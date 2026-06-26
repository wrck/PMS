package com.dp.plat.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.ProjectSoftVersion;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.exception.NoMatchedSoftVersionStrategyExecption;
import com.dp.plat.prob.util.SoftVersionUtil;
import com.dp.plat.prob.version.SoftVersionParser;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.Util;

/**
 * 软件版本初始化解析及数据状态同步任务
 * @author w02611
 */
public class ProjectSoftVersionInitJob extends AbstractSynchronizeTask implements Job {

    public ProjectSoftVersionInitJob() {
		super("applicationContext.xml", "sqlMapConfig.xml");
	}
	
	public void work() {
		Map<String, Object> params = new HashMap<String, Object>();
		
        // 解析项目设备软件版本
		parseProjectSoftVersion(params);
		
		// 更新失效的发货记录的项目软件版本信息
		updateInvalidShipmentProjectSoftVersion(params);
        
	}
	
	/**
     * 解析项目设备软件版本
     */
    public boolean parseProjectSoftVersion(Map<String, Object> params) {
        String tag = "解析项目设备软件版本";
        try {
            log.info("{}-开始", tag);
            
            ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
            ProjectDao projectDao = ctx.getBean("projectDao", ProjectDao.class);
            
            ProjectSoftVersion projectSoftVersion = new ProjectSoftVersion();
            projectSoftVersion.setProjectId(null);
            projectSoftVersion.setDatastate(1);
            // 检查有软件版本的数据
            projectSoftVersion.setCheckHasConp(true);
            // 检查conpMark没有格式化的数据
            projectSoftVersion.setCheckNullConpMark(true);
            // 匹配发货记录，更新发货contract、itemCode、datastate
            projectSoftVersion.setCheckShipment(true);
            DisplayParam displayParam = new DisplayParam();
            displayParam.setExport(true);
            List<ProjectSoftVersion> softList = projectService.selectProjectSoftVersionList(projectSoftVersion, displayParam);
            
            softList.parallelStream().forEach(softVersion -> {
                try {
                    String conp = softVersion.getConp();
                    softVersion.setItemCode(StringUtils.defaultIfBlank(StringUtils.trimToEmpty(softVersion.getItemCode()), StringUtils.left(softVersion.getBarCode(), 8)));
                    softVersion.setConpType("");
                    softVersion.setConpSeries("");
                    softVersion.setConpMark("");
                    if (StringUtils.isNotBlank(conp)) {
                        List<SoftVersionParser> parserList = SoftVersionUtil.createSoftVersionParser(conp);
                        if (parserList == null || parserList.isEmpty()) {
                            return;
                        }
                        SoftVersionParser versionParser = parserList.get(0);
                        softVersion.setConpType(versionParser.getType());
                        softVersion.setConpSeries(versionParser.getSeries());
                        softVersion.setConpMark(versionParser.getMark());
                        softVersion.setCustomInfoByKey("parser", parserList);
                    }
                } catch (Exception e) {
                    if (!(e instanceof NoMatchedSoftVersionStrategyExecption)) {
                        e.printStackTrace();
                        softVersion.setCustomInfoByKey("error", StringUtils.joinWith(":", e.getClass(), e.getMessage()));
                    }
                }
            });
            List<List<ProjectSoftVersion>> partitionList = Util.partition(softList, 1000);
//            for (List<ProjectSoftVersion> list : partitionList) {
            partitionList.parallelStream().forEach(list -> {
                projectDao.insertSoftVersionList(list, 0);
            });
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
    }
    
    /**
     * 更新失效的发货记录的项目软件版本信息
     */
    public boolean updateInvalidShipmentProjectSoftVersion(Map<String, Object> params) {
        String tag = "更新失效的发货记录的项目软件版本信息";
        try {
            log.info("{}-开始", tag);
            
            ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
            ProjectDao projectDao = ctx.getBean("projectDao", ProjectDao.class);
            
            ProjectSoftVersion projectSoftVersion = new ProjectSoftVersion();
            projectSoftVersion.setProjectId(null);
            projectSoftVersion.setDatastate(1);
            // 匹配发货记录，更新发货contract、itemCode、datastate
            projectSoftVersion.setCheckShipment(true);
            // 更新发货记录匹配失效的数据
            projectSoftVersion.setUpdateInvalidShipment(true);
            DisplayParam displayParam = new DisplayParam();
            displayParam.setExport(true);
            List<ProjectSoftVersion> softList = projectService.selectProjectSoftVersionList(projectSoftVersion, displayParam);
            List<List<ProjectSoftVersion>> partitionList = Util.partition(softList, 1000);
//            for (List<ProjectSoftVersion> list : partitionList) {
            partitionList.parallelStream().forEach(list -> {
                projectDao.insertSoftVersionList(list, 0);
                projectDao.deleteShipmentInstallInfoByList(list);
            });
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
    }

	@Override
    protected void syncDataBefore(String dataName, String dbName, Map<String, Object> params) {
	    super.syncDataBefore(dataName, dbName, params);
//	    String tag = getTag();
//        log.info("{}-前置操作", tag);
//        try {
//            log.info("{}-填充OrgCode", tag);
//            if (params != null && !params.containsKey("orgCode") && (params.containsKey("orgId") || params.containsKey("org_id"))) {
//                Object orgId = params.getOrDefault("orgId", params.getOrDefault("org_id", 1));
//                String orgCode = (String) sqlMap.queryForObject("selectOrgCodeByOrgId", String.valueOf(orgId));
//                params.put("orgCode", orgCode);
//            }
//        } catch (Exception e) {
//            log.error("{}-前置操作发生错误", tag, e);
//        }
    }
	
    @Override
    protected void syncDataInsertBefore(List<Map<String, Object>> list, Map<String, Object> params) {
        super.syncDataInsertBefore(list, params);
//        String tag = getTag();
//        log.info("{}-Insert前置操作", tag);
//        try {
//            log.info("{}-填充OrgId", tag);
//            Map<String, Integer> orgCodeMap = new HashMap<>();
//            if (params != null) {
//                orgCodeMap = (Map<String, Integer>) params.getOrDefault("orgCodeMap", orgCodeMap);
//                params.put("orgCodeMap", orgCodeMap);
//            }
//            for (Map<String, Object> map : list) {
//                String orgCode = String.valueOf(map.getOrDefault("orgCode", map.get("org_code")));
//                Integer orgId = orgCodeMap.getOrDefault(orgCode, (Integer) map.getOrDefault("orgId", map.get("org_id")));
//                if (orgId == null) {
//                    orgId = (Integer) sqlMap.queryForObject("selectOrgIdByOrgCode", orgCode);
//                    orgCodeMap.put(orgCode, orgId);
//                }
//                map.put("orgId", orgId);
//                map.put("org_id", orgId);
//            }
//        } catch (Exception e) {
//            log.error("{}-Insert前置操作发生错误", tag, e);
//        }
    }

    public static void main(String[] args) {
		try {
			new ProjectSoftVersionInitJob().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
