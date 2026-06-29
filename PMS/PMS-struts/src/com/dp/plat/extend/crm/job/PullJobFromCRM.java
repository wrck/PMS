package com.dp.plat.extend.crm.job;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.crm.model.ApiMap;
import com.dp.plat.crm.model.ConditionType;
import com.dp.plat.crm.util.CrmApi;
import com.dp.plat.param.LendInfoParam;
import com.dp.plat.param.LendProductParam;

import cn.hutool.core.map.MapUtil;

/**
 * 从销售管理系统同步市场部维度基础数据
 * 
 * @author admin
 *
 */
public class PullJobFromCRM extends DefaultPullTaskFormCRM<Map<String, Object>> implements Job {
    
    public static String sourceDbName = "CRM";
    
    public PullJobFromCRM() {
        super("applicationContext.xml", "sqlMapConfig.xml");
        batchSize = 1000;
    }

    public void work() throws IOException, SQLException {
        Map<String, Object> params = new HashMap<String, Object>();
        // 同步行业信息数据
//        syncMarketRelations(params);
        params.put("dataSource", sourceDbName);
//        syncLendInfo(params);
//        syncSalesInfo(params);
    }
    
    /**
     * 同步行业信息数据
     */
    public List<Map<String, Object>> syncMarketRelations(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        params.put("deleteSql", "delete_pm_project_market_relations_from_sms");
        params.put("insertSql", "insert_pm_project_market_relations_from_sms");
        return sync("GetFndIndustryLevel4", "行业信息数据", params);
    }
    
    /**
     * 
     * @param params
     * @see GainDataFromCRM#syncProjectSalesInfo
     * @return
     */
    public boolean syncLendInfo(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        params.put("new_ack_type", 2);
        params.put("new_ack_status", CrmApi.createCondition("new_ack_status", 2, ConditionType.GREATER_EQUAL));
        params.put("new_fnd_trial_type_id", "测试类借货");
        params.put("createdOn", CrmApi.createCondition("createdOn", "2025-01-06", ConditionType.GREATER_EQUAL));
        
        String dataName = "LendInfo";
        String tag;
        try {
            syncDataBefore(dataName, sourceDbName, params);
            
            // 同步CRM的借货数据
            List<Map<String, Object>> lendinfoApiMapList = syncLendInfoFromCRM(params);
            List<LendInfoParam> lendinfoList = CrmApi.transferToObject(lendinfoApiMapList, LendInfoParam.class);
            
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
            params.put("directReturnList", lendinfoApiMapList);
            List<LendProductParam> lendProductList = CrmApi.transferToObject(syncLendInfoProductFromCRM(params), LendProductParam.class);

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
     * 同步借货信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> syncLendInfoFromCRM(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        params.put("deleteSql", "delete_pm_presales_lend_info_from_sms");
        params.put("insertSql", "insert_pm_presales_lend_info_from_sms");
        // LendInfoOrderHeaderToPMS:同步借货订单行信息
        // LendInfoAckHeaderToPMS:同步借货执行单行信息
        // LendInfoAckLineToPMS:同步借货执行单头/行信息
        String dataName = "LendInfoAckLineToPMS";
        // 同步借货信息
        return sync(dataName, "借货信息", params);
    }

    /**
     * 同步借货产品配置信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> syncLendInfoProductFromCRM(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        params.put("deleteSql", "delete_pm_presales_lend_product_from_sms");
        params.put("insertSql", "insert_pm_presales_lend_product_from_sms");
        
        // LendInfoOrderLineToPMS:同步借货订单行信息，orderLine
        // LendInfoAckLineToPMS:同步借货执行单行信息，order_ack_line
        String dataName = "LendInfoAckLineToPMS";
        String subDataKey = "order_ack_line";
        params.put("dataName", dataName);
        params.put("subDataKey", subDataKey);
        
        // 同步借货信息
        List<Map<String, Object>> list = sync(dataName, "借货产品配置信息", params);
        return list;
    }

    /**
     * 同步借货信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> syncLendInfoAndLineFromCRM(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        
        params.put("deleteSql", "delete_pm_presales_lend_info_from_sms");
        params.put("insertSql", "insert_pm_presales_lend_info_from_sms");
        // LendInfoOrderHeaderToPMS:同步借货订单行信息
        // LendInfoAckHeaderToPMS:同步借货执行单行信息
        String dataName = "LendInfoOrderHeaderToPMS";
        // 同步借货信息
        List<Map<String, Object>> list = sync(dataName, "借货执行单及产品配置信息", params);
        return list;
    }

    /**
     * 同步CRM的销售数据
     * @param params
     * @see GainDataFromCRM#syncProjectSalesInfo(params)
     * @return
     */
    public boolean syncSalesInfo(Map<String, Object> params) {

        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        params.put("new_ack_type", 1);
        params.put("new_ack_status", CrmApi.createCondition("new_ack_status", 2, ConditionType.GREATER_EQUAL));
        params.put("createdOn", CrmApi.createCondition("createdOn", "2025-01-06", ConditionType.GREATER_EQUAL));
        
        params.put("new_opp_number", "16030023021203N");

        String dataName = "SalesInfo";
        String tag;
        try {
            syncDataBefore(dataName, sourceDbName, params);
            
            // 同步CRM的销售数据
            List<Map<String, Object>> salesInfoApiMapList = syncSalesInfoFromCRM(params);
//                List<PrjProperty> prjProperties = CrmApi.transferToObject(salesInfoApiMapList, PrjProperty.class);
            
//                // 总代借货及利润中心 TODO
//                List<Map<String, Object>> soleagentLends = sqlMapSap.queryForList("query_v_soleagent_lend_4_pms");
//                sqlMap.startTransaction();
//                sqlMap.delete("delete_pm_project_soleagent_lend_from_sms");
//                List<Map<String, Object>> soleagentLendList = new ArrayList<Map<String, Object>>();
//                int j = 0;
//                for (Map<String, Object> pp : soleagentLends) {
//                    soleagentLendList.add(pp);
//                    j++;
//                    if (j >= 2000) {
//                        paramMap.put("list", soleagentLendList);
//                        sqlMap.insert("insert_pm_project_soleagent_lend_from_sms", paramMap);
//                        j = 0;
//                        soleagentLendList.clear();
//                    }
//                }
//                if (!soleagentLendList.isEmpty()) {
//                    paramMap.put("list", soleagentLendList);
//                    sqlMap.insert("insert_pm_project_soleagent_lend_from_sms", paramMap);
//                }
//    
//                sqlMap.commitTransaction();
//                sqlMap.endTransaction();

            // //SMS项目责任人转移后更新新的销售
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
     * 同步正常订单信息
     * @param params
     * @return
     */
    public List<Map<String, Object>> syncSalesInfoFromCRM(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
        params.put("deleteSql", "delete_pm_project_property_from_sms");
        params.put("insertSql", "insert_pm_project_property_from_sms");
     // SalesInfoOrderHeaderToPMS:同步借货订单行信息
        // SalesInfoAckHeaderToPMS:同步借货执行单行信息
        // SalesInfoAckLineToPMS:同步借货执行单头/行信息
        String dataName = "SalesInfoAckLineToPMS";
        // 同步订单信息
        return sync(dataName, "正常订单信息", params);
    }
    
    /**
     * 同步数据
     */
    public List<Map<String, Object>> sync(String dataName, String tag, Map<String, Object> params) {
        try {
            log.info("同步{}-开始", tag);
            // 同步数据
            List<Map<String, Object>> data = syncData(dataName, "CRM", params, true);
            log.info("同步{}-成功", tag);
            return data;
        } catch (Exception e) {
            log.error("同步{}-发生异常", tag, e);
        } finally {
            log.info("同步{}-结束", tag);
        }
        return Collections.emptyList();
    }
    
    @Override
    protected void syncDataClear(String dataName, String dbName, Map<String, Object> params) {
        syncDataClearBySql(dataName, dbName, (String) params.get("deleteSql"), params);
    }
    
    @Override
    protected void syncDataInsertBefore(List<Map<String, Object>> list, Map<String, Object> params) {
        super.syncDataInsertBefore(list, params);
        
        String subDataKey = (String) params.get("subDataKey") ;
        if (StringUtils.isNotBlank(subDataKey)) {
            List<Map<String, Object>> lineList = new ArrayList<>(list.size() * 3);
            for (Map<String, Object> header : list) {
                List<Map<String, Object>> lines = MapUtil.get(header, subDataKey, List.class);
                if (lines == null || lines.isEmpty()) {
                    continue;
                }
                // 将header头的信息合并到行上
                for (Map<String, Object> line : lines) {
                    ApiMap newline = new ApiMap(header);
                    newline.putAll(line);
                    lineList.add(newline);
                }
            }
            list.clear();
            list.addAll(lineList);
        }
        
//        // FIXME TEST
//        List<Map<String, Object>> collect = list.stream().limit(batchSize).collect(Collectors.toList());
//        list.clear();
//        list.addAll(collect);
        
        
        for (Map<String, Object> map : list) {
            // 销售订单的执行单号进行处理，将J转为X
            if (params.getOrDefault("syncDataName", "").equals("SalesInfo") && map.containsKey("orderExecNumber")) {
                map.put("orderExecNumber", MapUtil.getStr(map, "orderExecNumber", "").replace("J", "X"));
            } else if (params.getOrDefault("syncDataName", "").equals("LendInfoAckLineToPMS")) {
                map.put("dutyContactWay", MapUtil.getStr(map, "new_opp_owner_phone", ""));
            }
            
            map.put("dataSource", params.get("dataSource"));
        }
    }

    @Override
    protected void syncDataInsert(String dataName, String dbName, List<Map<String, Object>> list, Map<String, Object> params) {
        syncDataInsertBySql(dataName, dbName, (String) params.get("insertSql"), list, params);
    }

    public static void main(String[] args) {
        try {
            new PullJobFromCRM().execute(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

}
