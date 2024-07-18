package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dp.plat.param.LendInfoParam;
import com.dp.plat.param.LendProductParam;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 同步刷新SMS审批通过之后的
 * 
 * @author admin
 */
public class GainPresalesInfoBySMS {
    private final static int BATCH_SIZE = 1000;
    
    @SuppressWarnings("unchecked")
    public static void work() throws IOException, SQLException {
        // SMS数据库连接
        Reader readerSms = Resources.getResourceAsReader("sqlMapConfigSMS.xml");
        SqlMapClient sqlMapSms = SqlMapClientBuilder.buildSqlMapClient(readerSms);
        // SAP数据库连接
        Reader readerSAP = Resources.getResourceAsReader("sqlMapConfigSAP.xml");
        SqlMapClient sqlMapSAP = SqlMapClientBuilder.buildSqlMapClient(readerSAP);
        // 本地数据库连接
        Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
        SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);

        Map<String, Object> logMap = new HashMap<String, Object>();
        //开始同步，增加日志
        logMap.put("refreshTaskName", GainPresalesInfoBySMS.class.toString());
        logMap.put("handleUser", "system");
        logMap.put("dataFrom", "SMS");
        logMap.put("dataTo", null);
        logMap.put("refreshFrom", new Date());
        Object obj = sqlMap.insert("insert_fnd_data_refresh_log", logMap);
        try {
            // 借货订单信息
//            List<Map<String, Object>> lendOrderList = sqlMapSms.queryForList("query_lend_order_list");
//            if (lendOrderList.size() > 0) {
//                sqlMap.startTransaction();
//                sqlMap.delete("delete_pm_presales_lend_order_from_sms");
//                int sumCount = lendOrderList.size();
//                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
//                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.insert("insert_pm_presales_lend_order_from_sms", lendOrderList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.commitTransaction();
//                }
////                sqlMap.insert("insert_pm_presales_lend_order_from_sms", lendOrderList);
////                sqlMap.commitTransaction();
//                sqlMap.endTransaction();
//                lendOrderList = null;
//            }
            
            // 同步借货信息
            List<LendInfoParam> lendinfoList = sqlMapSms.queryForList("query_lend_info_list");
            if (lendinfoList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.delete("delete_pm_presales_lend_info_from_sms");
                int sumCount = lendinfoList.size();
                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
                    sqlMap.insert("insert_pm_presales_lend_info_from_sms", lendinfoList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
                }
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
            }
            
            // 去除已存在的借货信息
            List<String> lendInfoIds = sqlMap.queryForList("query_lend_info_ids", "SMS");
            Iterator<LendInfoParam> iterator = lendinfoList.iterator();
            while (iterator.hasNext()) {
                LendInfoParam lendInfo = iterator.next();
                if (lendInfoIds.contains(lendInfo.getLendInfoId())) {
                    iterator.remove();
                }
            }
            iterator = null;
            
            // 插入新的售前测试项目
            Map<String, Object> paramMap = new HashMap<String, Object>();
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
            List<LendProductParam> lendProductList = sqlMapSms.queryForList("query_lend_product_list");
            if (lendProductList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.delete("delete_pm_presales_lend_product_from_sms");
                int sumCount = lendProductList.size();
                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
                    sqlMap.insert("insert_pm_presales_lend_product_from_sms", lendProductList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
//                    sqlMap.commitTransaction();
                }
//                sqlMap.insert("insert_pm_presales_lend_product_from_sms", lendProductList);
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
            }

            // 除去已存在的产品配置
            Iterator<LendProductParam> iterator2 = lendProductList.iterator();
            while (iterator2.hasNext()) {
                LendProductParam lendProduct = iterator2.next();
                if (lendInfoIds.contains(lendProduct.getLendInfoId())) {
                    iterator2.remove();
                }
            }
            iterator2 = null;
            
            // 插入新的产品配置
            paramMap.put("plist", lendProductList);
            if (lendProductList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.insert("insert_pm_presales_product", paramMap);
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
            }
            paramMap.clear();
            lendProductList = null;

            // 同步项目借转销数据
            List<Map<String, Object>> lend2SaleList = sqlMapSms.queryForList("query_lend_2_sale_list");
            if (lend2SaleList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.delete("delete_pm_presales_lend_2_sale_from_sms");
                int sumCount = lend2SaleList.size();
                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
                    sqlMap.insert("insert_pm_presales_lend_2_sale_from_sms", lend2SaleList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
                }
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
                lend2SaleList = null;
            }
            
            // 同步项目核销数据
            List<Map<String, Object>> lend2RmaList = sqlMapSms.queryForList("query_lend_2_rma_list");
            if (lend2RmaList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.delete("delete_pm_presales_lend_2_rma_from_sms");
                int sumCount = lend2RmaList.size();
                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
                    sqlMap.insert("insert_pm_presales_lend_2_rma_from_sms", lend2RmaList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
                }
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
                lend2RmaList = null;
            }
            
            // 同步项目核销时间
            List<Map<String, Object>> lendDeliverAndBackDateList = sqlMapSAP.queryForList("query_lend_delivery_off_list");
            if (lendDeliverAndBackDateList.size() > 0) {
                sqlMap.startTransaction();
                sqlMap.delete("delete_pm_presales_lend_2_delivery_off_from_sap");
                int sumCount = lendDeliverAndBackDateList.size();
                for(int i = 0; i < sumCount / BATCH_SIZE + 1; i++) {
                    System.out.println(sumCount + ":" + (i * BATCH_SIZE) + "~" + (Math.min((i+1) * BATCH_SIZE, sumCount)));
                    sqlMap.insert("insert_pm_presales_lend_2_delivery_off_from_sap", lendDeliverAndBackDateList.subList(i * BATCH_SIZE, Math.min((i+1) * BATCH_SIZE, sumCount)));
                }
                sqlMap.commitTransaction();
                sqlMap.endTransaction();
                lendDeliverAndBackDateList = null;
            }
            
            // 创建项目核销汇总信息表
            sqlMap.startTransaction();
            sqlMap.delete("drop_pm_presales_project_rma_info");
            sqlMap.insert("create_pm_presales_project_rma_info");
            sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
            // 更新售前测试借转销标记
            sqlMap.startTransaction();
            sqlMap.update("updatePresalesTransferState");
            sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
            // 更新售前测试未核销标记
            sqlMap.startTransaction();
            sqlMap.update("updatePresalesRMAState");
            sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
            //更新成功日志
            logMap.put("id", Integer.parseInt(obj.toString()));
            logMap.put("refreshTo", new Date());
            logMap.put("refreshState", 1);
            sqlMap.update("update_fnd_data_refresh_log_success", logMap);
        } catch (Exception e) {
            e.printStackTrace();
            if (sqlMap != null) {
                try {
                    sqlMap.getCurrentConnection().rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            
            //更新失败日志
            logMap.put("refreshException", ExceptionUtils.getStackTrace(e));
            logMap.put("id", Integer.parseInt(obj.toString()));
            sqlMap.update("update_fnd_data_refresh_log_fail", logMap);
        }
    }

    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    @Deprecated
    public static void main(String[] args) throws Exception {
        GainPresalesInfoBySMS.work();
    }

}
