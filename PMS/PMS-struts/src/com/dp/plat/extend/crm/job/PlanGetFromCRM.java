package com.dp.plat.extend.crm.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.dp.plat.crm.job.AbstractSynchronizeTask;
import com.dp.plat.job.GainDataFromITR;

/**
 * 同步CRM订单信息
 * @author w02611
 */
public class PlanGetFromCRM extends DefaultSyncTaskFormCRM<Map<String, Object>> implements Job {

    public PlanGetFromCRM() {
		super("applicationContext.xml", "sqlMapConfig.xml");
	}
	
	public void work() {
		Map<String, Object> params = new HashMap<String, Object>();
		
//        // 刷新同步CRM项目执行单信息
//		syncProjectProperty(params);
//		// 刷新同步CRM项目总代借货信息
//		syncProjectSoleagentLend(params);
		// 刷新同步CRM合同回款计划
		syncContractCollectionPlan(params);
	}
	
	/**
     * 刷新同步CRM项目执行单信息
     */
    public boolean syncProjectProperty(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String tag = "同步CRM项目执行单信息";
        try {
            log.info("{}-开始", tag);
            // 刷新CRM项目执行单信息
//            params.put("querySql", "query_pm_project_property_from_crm");
//            params.put("deleteSql", "delete_pm_project_property_from_crm");
//            params.put("insertSql", "insert_pm_project_property_from_crm");
            params.put("targetTable", "pm_project_property_from_sms");
            syncData("ProjectPropertyFormCRM", "CRM", params);
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
    }
    
    /**
     * 刷新同步CRM项目总代借货信息
     */
    public boolean syncProjectSoleagentLend(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String tag = "同步CRM项目总代借货信息";
        try {
            log.info("{}-开始", tag);
            // 刷新CRM项目总代借货信息
//            params.put("querySql", "query_pm_project_soleagent_lend_from_crm");
//            params.put("deleteSql", "delete_pm_project_soleagent_lend_from_crm");
//            params.put("insertSql", "insert_pm_project_soleagent_lend_from_crm");
            params.put("targetTable", "pm_project_soleagent_lend_from_sms");
            syncData("ProjectSoleagentLendFormCRM", "CRM", params);
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
    }
    
    /**
     * 刷新同步CRM合同回款计划
     */
    public boolean syncContractCollectionPlan(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String tag = "同步CRM合同回款计划";
        try {
            log.info("{}-开始", tag);
            // 刷新CRM项目执行单信息
            params.put("targetTable", "pm_pb_plan_from_sms");
            syncData("ContractCollectionPlanFromCRM", "CRM", params);
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
        try {
            // 重置表的自增ID
            if (params.containsKey("targetTable")) {
                sqlMap.insert("resetTableAutoId", params);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
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
			new PlanGetFromCRM().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
