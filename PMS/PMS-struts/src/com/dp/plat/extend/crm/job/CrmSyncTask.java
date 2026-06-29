package com.dp.plat.extend.crm.job;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;

import com.ibatis.sqlmap.client.SqlMapClient;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.SystemContext;

/**
 * 默认同步任务
 * @author w02611
 *
 */
public interface CrmSyncTask {
    
    public static final Supplier<ConcurrentHashMap<String, Object>> getCrmConfig = new Supplier<ConcurrentHashMap<String, Object>>() {

        @Override
        public ConcurrentHashMap<String, Object> get() {
            return SystemContext.getCrmConfig();
        }
    };

    public default Supplier<ConcurrentHashMap<String, Object>> getCrmConfig() {
        return getCrmConfig;
    }

    public default ApplicationContext getApplicationContext() {
        return SpringContext.getApplicationContext();
    }
    
    /**
     * 在数据同步之前的前置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName
     * @param params
     */
    public default void syncDataBefore(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        Map<String, Object> logParams = new HashMap<String, Object>();
        //开始同步，增加日志
        logParams.put("refreshTaskName", this.getClass().toString() + "." + dataName);
        logParams.put("handleUser", "system");
        logParams.put("dataFrom", sourceDbName);
        logParams.put("dataTo", targetDbName);
        logParams.put("refreshFrom", new Date());
        try {
            Object obj = getSqlMapClient(targetDbName).insert("insert_fnd_data_refresh_log", logParams);
            logParams.put("id", obj);
        } catch (SQLException e) {
        }
        params.put("logParams", logParams);
    }
    
    /**
     * 在数据同步成功的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 
     * @param params
     */
    public default void syncDataSuccess(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新成功日志
        logParams.put("refreshTo", new Date());
        logParams.put("refreshState", 1);
    }
    
    /**
     * 在数据同步失败的后置操作
     * 
     * @param dataName
     * @param dbName
     * @param params
     * @param e 
     */
    public default void syncDataFail(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params, Throwable e) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新失败日志
        logParams.put("refreshException", ExceptionUtils.getStackTrace(e));
    }
    
    /**
     * 在数据同步之后的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 
     * @param params
     */
    public default void syncDataAfter(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新日志
        try {
            getSqlMapClient(targetDbName).update("update_fnd_data_refresh_log", logParams);
        } catch (SQLException e) {
        }
    }
    

    public default String getRouterName(String dataName) {
        if (dataName == null) {
            return dataName;
        }
        dataName = dataName.replaceAll("(ToCRM|FromCRM)", "");
        return dataName;
    }
    
    SqlMapClient getSqlMapClient(String targetDbName);
}
