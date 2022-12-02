package com.dp.plat.job;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.service.ProjectService;

/**
 * 同步ERP订单信息
 * @author w02611
 */
public class GainOrderByERP extends AbstractSynchronizeTask implements Job {

    public GainOrderByERP() {
		super("applicationContext.xml", "sqlMapConfig.xml");
	}
	
	public void work() {
		Map<String, Object> params = new HashMap<String, Object>();
//		// 刷新同步SAP订单数据
//		syncOrderFormSAP(params);
		
        // 刷新同步D365订单数据
		syncOrderFormD365(params);
		
        // 刷新同步ERP订单数据
		syncOrderFormERP(params);
		
		// 拆分总代借货订单信息
		splitSoleAgentLendOrderInfo(params);
		
        // 更新项目设备清单
        updateProjectProductLine(params);
        
        // 更新项目发货状态
        try {
            log.debug("#更新项目发货状态开始");
            UpdateShipmentState.work();
            log.debug("#更新项目发货状态结束");
        } catch (Exception e) {
            log.error("{}-发生异常：{}", "更新项目发货状态", e);
        } finally {
            log.info("{}-结束", "更新项目发货状态");
        }
	}
	
	/**
     * 刷新同步ERP订单数据
     */
    public boolean syncOrderFormERP(Map<String, Object> params) {
        String tag = "同步ERP订单数据";
        try {
            log.info("{}-开始", tag);
            // 刷新ERP同步合同基础数据
            syncData("OrderInfoFromERP", "Local", params);
            // 刷新ERP同步合同基础数据
            syncData("OrderLineFromERP", "Local", params);
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
    }

	/**
	 * 刷新同步D365订单数据
	 */
	public boolean syncOrderFormD365(Map<String, Object> params) {
		String tag = "同步D365订单数据";
		try {
			log.info("{}-开始", tag);
			// 刷新D365同步合同基础数据
            syncData("OrderInfoFromD365", "D365", params);
			// 刷新D365同步合同基础数据
			syncData("OrderLineFromD365", "D365", params);
			return true;
		} catch (Exception e) {
			log.error("{}-发生异常：{}", tag, e);
		} finally {
			log.info("{}-结束", tag);
		}
		return false;
	}
	
	/**
	 * 刷新同步订单行明细数据
	 */
	public boolean syncOrderFormSAP(Map<String, Object> params) {
		String tag = "同步SAP订单数据";
		try {
			log.info("{}-开始", tag);
			// 刷新SAP同步订单行明细数据
            syncData("OrderInfoFromSAP", "SAP", params);
			// 刷新SAP同步订单行明细数据
			syncData("OrderLineFromSAP", "SAP", params);
			return true;
		} catch (Exception e) {
			log.error("{}-发生异常：{}", tag, e);
		} finally {
			log.info("{}-结束", tag);
		}
		return false;
	}
	
	/**
	 * 拆分总代借货订单信息
	 * @param params
	 * @return
	 */
	public boolean splitSoleAgentLendOrderInfo(Map<String, Object> params) {
	    String tag = "拆分总代借货项目订单信息";
        try {
            log.info("{}-开始", tag);
            // 拆分总代借货项目订单信息
            sqlMap.update("callSplitSoleAgentLendOrderInfo");
            ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
            projectService.updateSoleAgentLendProject();
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return false;
	}
	
	/**
     * 更新项目设备清单
     * @param params
     * @return
     */
    public boolean updateProjectProductLine(Map<String, Object> params) {
        String tag = "更新项目设备清单";
        try {
            log.info("{}-开始", tag);
            sqlMap.startTransaction();
            
            // 创建需要更新的项目合同号、projectId临时表
            sqlMap.insert("createTempNeedUpdateProject");
            // 删除需要更新的项目的产品信息
            sqlMap.delete("deleteOldProductLines");
            // 重新更新产品信息的自增序列，避免id不连续
            sqlMap.update("updateProductLineId");
            sqlMap.insert("resetProductLineAutoId");
            // 插入需要更新的项目的产品信息
            sqlMap.insert("insertNewProductLines");
            // 删除临时表
            sqlMap.delete("dropTempNeedUpdateProject");
            
            sqlMap.commitTransaction();
            return true;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
            if (sqlMap != null) {
                try {
                    log.error("{}-回滚事务", tag);
                    if (sqlMap.getCurrentConnection() != null) {
                        sqlMap.getCurrentConnection().rollback();
                    }
                } catch (Exception ex) {
                    log.error("{}-回滚事务失败：{}", tag, ex);
                }
            }
        } finally {
            log.info("{}-结束", tag);
            try {
                sqlMap.endTransaction();
            } catch (SQLException e) {
                log.error("{}-结束事务失败：{}", tag, e);
            }
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
			new GainOrderByERP().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
