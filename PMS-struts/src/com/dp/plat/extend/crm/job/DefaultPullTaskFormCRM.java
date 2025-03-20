package com.dp.plat.extend.crm.job;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.quartz.Job;

import com.dp.plat.crm.model.ApiMap;
import com.dp.plat.crm.model.Response;
import com.dp.plat.crm.util.CrmApi;

/**
 * CRM默认数据拉取任务
 * @author w02611
 *
 */
public class DefaultPullTaskFormCRM<T> extends DefaultSyncTaskFormCRM<T> implements Job {

    public DefaultPullTaskFormCRM(String appXmlPath, String sqlMapConfig) {
        super(appXmlPath, sqlMapConfig);
    }

    @Override
    protected List<T> syncDataQuery(String dataName, String dbName, Map<String, Object> params) {
        log.info("{}-查询CRM数据开始", getTag(dataName));
        if (params.containsKey("directReturnList")) {
            List<T> directReturnList = (List<T>) params.remove("directReturnList");
//            return (List<T>) params.getOrDefault("directReturnList", Collections.emptyList());
            return directReturnList != null ? directReturnList : Collections.emptyList();
        }
        Response<T> response = CrmApi.queryRecordList(getRouterName(dataName), new ApiMap(params));
        List<T> data = response.getData();
        if (response.isSuccess()) {
            log.error("{}-查询CRM数据成功{}条", getTag(dataName), data.size());
        } else {
            log.error("{}-查询CRM数据-发生异常：{}", getTag(dataName), response.getMessage());
        }
        return data;
    }
    
    @Override
    protected void syncDataClear(String dataName, String dbName, Map<String, Object> params) {
        Map<String, Object> router = CrmApi.getRouter(getRouterName(dataName));
        String deleteSql = (String) router.get("deleteSql");
        syncDataClearBySql(dataName, dbName, deleteSql, params);
    }
    
    @Override
    protected void syncDataInsert(String dataName, String dbName, List<T> list, Map<String, Object> params) {
        Map<String, Object> router = CrmApi.getRouter(getRouterName(dataName));
        String insertSql = (String) router.get("insertSql");
        syncDataInsertBySql(dataName, dbName, insertSql, list, params);
    }
    
    
}
