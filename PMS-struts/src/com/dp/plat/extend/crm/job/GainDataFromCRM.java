package com.dp.plat.extend.crm.job;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.param.LendInfoParam;
import com.dp.plat.param.LendProductParam;
import com.dp.plat.param.PrjProperty;

/**
 * 同步CRM订单信息
 * @author w02611
 */
public class GainDataFromCRM extends DefaultSyncTaskFormCRM<Map<String, Object>> implements Job {

    public GainDataFromCRM() {
		super("applicationContext.xml", "sqlMapConfig.xml");
	}
	
	public void work() {
		Map<String, Object> params = new HashMap<String, Object>();
		
        // 刷新同步CRM项目执行单信息
//		syncProjectSalesInfo(params);
//		// 刷新同步CRM项目总代借货信息
//		syncProjectSoleagentLend(params);
		// 刷新同步CRM合同回款计划
//		syncContractCollectionPlan(params);
		// 刷新同步CRM项目售前测试
		syncProjectLendInfo(params);
	}
	
	public boolean syncProjectSalesInfo(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        String dataName = "SalesInfo";
        String tag = "同步CRM项目执行单信息";
        try {
            log.info("{}-开始", tag);
            syncDataBefore(dataName, sourceDbName, params);
            
            // 刷新同步CRM项目执行单信息
            syncProjectProperty(params);
            // 刷新同步CRM项目总代借货信息
            syncProjectSoleagentLend(params);

            // //项目责任人转移后更新新的销售
            // // 存在问题，进行调整
            // sqlMap.insert("update_pm_salesmember_info");
//          sqlMap.startTransaction();
//          sqlMap.insert("create_temp_project_sales_change");
//          sqlMap.update("invalid_project_invalid_sales");
//          sqlMap.update("insert_changed_project_sales");
//          sqlMap.insert("drop_temp_project_sales_change");
//          sqlMap.commitTransaction();
//          sqlMap.endTransaction();

            // SMS改单后实施方式、渠道信息发生变化进行更新
            sqlMap.insert("create_temp_max_ppfsId");
            sqlMap.insert("create_temp_max_prpId");
            sqlMap.insert("create_temp_max_ppfs");
            sqlMap.insert("create_temp_not_ppfs");
            sqlMap.insert("create_temporary_serviceType_and_channelName_table");

            sqlMap.startTransaction();
            sqlMap.update("update_project_serviceType");
            sqlMap.update("update_project_channelName");
            sqlMap.update("update_project_compId");
            sqlMap.update("update_project_customProjectName");
            sqlMap.commitTransaction();
            sqlMap.endTransaction();

            // SMS项目责任人转移后更新新的销售
            sqlMap.insert("create_temp_project_sales_change");
            sqlMap.update("invalid_project_invalid_sales");
            sqlMap.update("insert_changed_project_sales");
            sqlMap.insert("drop_temp_project_sales_change");

            sqlMap.delete("drop_temp_max_ppfsId");
            sqlMap.delete("drop_temp_max_prpId");
            sqlMap.delete("drop_temp_max_ppfs");
            sqlMap.delete("drop_temp_not_ppfs");
            sqlMap.delete("drop_temporary_serviceType_and_channelName_table");
            
            // 更新成功日志
            syncDataSuccess(dataName, sourceDbName, localDBName, params);
            return true;
        } catch (Exception e) {
            if (sqlMap != null) {
                try {
                    sqlMap.getCurrentConnection().rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            // 更新失败日志
            syncDataFail(dataName, sourceDbName, localDBName, params, e);
        } finally {
            try {
                sqlMap.endTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            syncDataAfter(dataName, sourceDbName, params);
        }
        return false;
    }
	
	/**
     * 刷新同步CRM项目执行单信息
     */
    public List<Map<String, Object>> syncProjectProperty(Map<String, Object> params) {
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
            return syncData("ProjectPropertyFormCRM", "CRM", params);
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return Collections.emptyList();
    }
    
    /**
     * 刷新同步CRM售前测试信息
     */
    public boolean syncProjectLendInfo(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String dataName = "LendInfo";
        String tag = "同步CRM售前测试执行单信息";
        try {
            log.info("{}-开始", tag);
            syncDataBefore(dataName, sourceDbName, params);
            
            // 同步CRM的借货数据
            List<LendInfoParam> lendinfoList = (List<LendInfoParam>) syncProjectLendHeader(params);
//            List<LendInfoParam> lendinfoList = CrmApi.transferToObject(lendinfoApiMapList, LendInfoParam.class);
            
            // 去除已存在的借货信息
            List<String> lendInfoIds = sqlMap.queryForList("query_lend_info_ids", "CRM");
            Set<String> newLendInfoIds = new HashSet<>(lendinfoList.size());
            for (Iterator<LendInfoParam> iterator = lendinfoList.iterator(); iterator.hasNext();) {
                LendInfoParam lendInfo = iterator.next();
                String lendInfoId = lendInfo.getLendInfoId();
                if (lendInfoIds.contains(lendInfoId) || newLendInfoIds.contains(lendInfoId)) {
                    iterator.remove();
                } else {
                    newLendInfoIds.add(lendInfoId);
                }
            }
//            
//            // 插入新的售前测试项目
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("dataSource", sourceDbName);
            paramMap.put("list", lendinfoList);
            if (lendinfoList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.insert("insert_pm_presales_header", paramMap);
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
            }
            paramMap.clear();
            lendinfoList = null;

            // 同步借货产品配置
            List<LendProductParam> lendProductList = (List<LendProductParam>) syncProjectLendProduct(params);
//            List<LendProductParam> lendProductList = CrmApi.transferToObject(syncLendInfoProductFromCRM(params), LendProductParam.class);

            // 除去已存在的产品配置
            for (Iterator<LendProductParam> iterator = lendProductList.iterator(); iterator.hasNext();) {
                LendProductParam lendProduct = iterator.next();
                if (lendInfoIds.contains(lendProduct.getLendInfoId())) {
                    iterator.remove();
                }
            }
            
            // 插入新的产品配置
            paramMap.put("dataSource", sourceDbName);
            paramMap.put("plist", lendProductList);
            if (lendProductList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.insert("insert_pm_presales_product", paramMap);
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
            }
            paramMap.clear();
            lendProductList = null;

            //
//            // 同步项目借转销数据
//            List<Map<String, Object>> lend2SaleList = sqlMapSms.queryForList("query_lend_2_sale_list");
//            if (lend2SaleList.size() > 0) {
//                sqlMap.startTransaction();
//                sqlMap.delete("delete_pm_presales_lend_2_sale_from_sms");
//                int sumCount = lend2SaleList.size();
//                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
//                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.insert("insert_pm_presales_lend_2_sale_from_sms", lend2SaleList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
//                }
//                sqlMap.commitTransaction();
//                sqlMap.endTransaction();
//                lend2SaleList = null;
//            }
//            
//            // 同步项目核销数据
//            List<Map<String, Object>> lend2RmaList = sqlMapSms.queryForList("query_lend_2_rma_list");
//            if (lend2RmaList.size() > 0) {
//                sqlMap.startTransaction();
//                sqlMap.delete("delete_pm_presales_lend_2_rma_from_sms");
//                int sumCount = lend2RmaList.size();
//                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
//                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.insert("insert_pm_presales_lend_2_rma_from_sms", lend2RmaList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
//                }
//                sqlMap.commitTransaction();
//                sqlMap.endTransaction();
//                lend2RmaList = null;
//            }
//            
//            // 同步项目核销时间
//            List<Map<String, Object>> lendDeliverAndBackDateList = sqlMapSAP.queryForList("query_lend_delivery_off_list");
//            if (lendDeliverAndBackDateList.size() > 0) {
//                sqlMap.startTransaction();
//                sqlMap.delete("delete_pm_presales_lend_2_delivery_off_from_sap");
//                int sumCount = lendDeliverAndBackDateList.size();
//                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
//                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.insert("insert_pm_presales_lend_2_delivery_off_from_sap", lendDeliverAndBackDateList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
//                }
//                sqlMap.commitTransaction();
//                sqlMap.endTransaction();
//                lendDeliverAndBackDateList = null;
//            }
//            
//            // 创建项目核销汇总信息表
//            sqlMap.startTransaction();
//            sqlMap.delete("drop_pm_presales_project_rma_info");
//            sqlMap.insert("create_pm_presales_project_rma_info");
//            sqlMap.commitTransaction();
//            sqlMap.endTransaction();
//            
//            // 更新售前测试借转销标记
//            sqlMap.startTransaction();
//            sqlMap.update("updatePresalesTransferState");
//            sqlMap.commitTransaction();
//            sqlMap.endTransaction();
//            
//            // 更新售前测试未核销标记
//            sqlMap.startTransaction();
//            sqlMap.update("updatePresalesRMAState");
//            sqlMap.commitTransaction();
//            sqlMap.endTransaction();
//            
//            //更新成功日志
//            logMap.put("id", Integer.parseInt(obj.toString()));
//            logMap.put("refreshTo", new Date());
//            logMap.put("refreshState", 1);
//            sqlMap.update("update_fnd_data_refresh_log_success", logMap);
            syncDataSuccess(dataName, sourceDbName, localDBName, params);
            return true;
        } catch (Exception e) {
            syncDataFail(dataName, sourceDbName, localDBName, params, e);
            
            //更新失败日志
//            logMap.put("refreshException", ExceptionUtils.getStackTrace(e));
//            logMap.put("id", Integer.parseInt(obj.toString()));
//            sqlMap.update("update_fnd_data_refresh_log_fail", logMap);
        } finally {
            syncDataAfter(dataName, sourceDbName, params);
        }
        return false;
    }
    
    /**
     * 刷新同步CRM售前测试信息
     */
    public List<?> syncProjectLendHeader(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String tag = "同步CRM售前测试信息";
        try {
            log.info("{}-开始", tag);
            // 刷新CRM项目执行单信息
            params.put("querySql", "query_pm_presales_lend_info_from_crm");
            params.put("deleteSql", "delete_pm_presales_lend_info_from_crm");
            params.put("insertSql", "insert_pm_presales_lend_info_from_crm");
            params.put("targetTable", "pm_presales_lend_info_from_sms");
            return syncData("LendInfoFormCRM", "CRM", params);
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return Collections.emptyList();
    }
    
    /**
     * 刷新同步CRM售前测试产品配置
     */
    public List<?> syncProjectLendProduct(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        String tag = "同步CRM售前测试产品配置";
        try {
            log.info("{}-开始", tag);
            // 刷新CRM项目执行单信息
            params.put("querySql", "query_pm_presales_lend_product_from_crm");
            params.put("deleteSql", "delete_pm_presales_lend_product_from_crm");
            params.put("insertSql", "insert_pm_presales_lend_product_from_crm");
            params.put("targetTable", "pm_presales_lend_product_from_sms");
            return syncData("LendProductFormCRM", "CRM", params);
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
        } finally {
            log.info("{}-结束", tag);
        }
        return Collections.emptyList();
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
        
        
        String tag = getTag();
        log.info("{}-Insert前置操作", tag);
        try {
            if ("ProjectPropertyFormCRM".equals(params.get("syncDataName"))) {
                for (Object obj : list) {
                    if (obj instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        map.put("orderExecNumber", StringUtils.replace((String) map.getOrDefault("orderCodeReal", ""), "J", "X"));
                    } else if (obj instanceof PrjProperty) {
                        PrjProperty prj = (PrjProperty) obj;
                        prj.setOrderExecNumber(StringUtils.replace(prj.getOrderExecNumber(), "J", "X"));
                    }
                }
            }
            
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
        } catch (Exception e) {
            log.error("{}-Insert前置操作发生错误", tag, e);
        }
    }

    public static void main(String[] args) {
		try {
			new GainDataFromCRM().syncProjectLendProduct(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
