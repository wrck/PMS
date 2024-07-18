package com.dp.plat.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionException;

/**
 * 同步ERP订单信息
 * @author w02611
 */
public class GainDataFromITR extends AbstractSynchronizeTask implements Job {

    public GainDataFromITR() {
		super("applicationContext.xml", "sqlMapConfig.xml");
	}
	
	public void work() {
		Map<String, Object> params = new HashMap<String, Object>();
		
        // 刷新同步ITR问题单数据
		syncProblemTicketFormITR(params);
        
	}
	
	/**
     * 刷新同步ITR问题单数据
     */
    public boolean syncProblemTicketFormITR(Map<String, Object> params) {
        String tag = "同步ERP订单数据";
        try {
            log.info("{}-开始", tag);
            // 刷新ERP同步合同基础数据
            syncData("ProblemTicketFormITR", "ITR", params);
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
			new GainDataFromITR().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
