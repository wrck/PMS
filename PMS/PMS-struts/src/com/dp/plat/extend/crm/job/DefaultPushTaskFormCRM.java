package com.dp.plat.extend.crm.job;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.quartz.Job;

import com.dp.plat.crm.model.Response;
import com.dp.plat.crm.util.CrmApi;

import cn.hutool.core.map.MapUtil;

/**
 * CRM默认数据推送
 * @author w02611
 *
 */
public class DefaultPushTaskFormCRM<T> extends DefaultSyncTaskFormCRM<T> implements Job {
    
    public DefaultPushTaskFormCRM(String appXmlPath, String sqlMapConfig) {
        super(appXmlPath, sqlMapConfig);
    }
    
    

    @Override
    protected void syncDataBefore(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        super.syncDataBefore(dataName, sourceDbName, targetDbName, params);
        
        Map<String, Object> router = CrmApi.getRouter(getRouterName(dataName));
        int pageSize = MapUtil.getInt(router, "pageSize", batchSize);
        params.put("batchSize", pageSize);
        String syncType = (String) router.getOrDefault("syncType", "All");
        if ("dateRange".equalsIgnoreCase(syncType)) {
            Integer dayRange = (Integer) router.getOrDefault("dayRange", 7);
            Integer dayOffset = (Integer) router.getOrDefault("dayOffset", 0);
            Map<String, Object> dateRange = initQueryDateRange(dayRange, dayOffset);
            params.putAll(dateRange);
        }
    }



    @Override
    protected List<T> syncDataQuery(String dataName, String dbName, Map<String, Object> params) {
        Map<String, Object> router = CrmApi.getRouter(getRouterName(dataName));
        String querySql = (String) router.get("querySql");
        return syncDataQueryBySql(dataName, dbName, querySql, params);
    }
    
    @Override
    protected void syncDataClear(String dataName, String dbName, Map<String, Object> params) {
    }
    
    @Override
    protected void syncDataInsert(String dataName, String dbName, List<T> list, Map<String, Object> params) {
        list = list != null ? list : Collections.emptyList();
        log.info("{}-推送CRM数据开始{}条", getTag(dataName), list.size());
        Response<T> response = CrmApi.pushRecord(getRouterName(dataName), list);
        if (response.isSuccess()) {
            log.info("{}-推送CRM数据成功{}条", getTag(dataName), list.size());
        } else {
            log.error("{}-推送CRM数据-发生异常：{}", getTag(dataName), response.getMessage());
        }
    }
}
