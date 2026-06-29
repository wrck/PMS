package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import com.dp.plat.context.SpringContext;
import com.dp.plat.exception.CustomRuntimeException;

import cn.hutool.core.date.DateUtil;

/**
 * 同步数据进行虚拟方法
 * 
 * @author user
 *
 */
public abstract class AbstractSynchronizeTask implements Job {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final String localDBName = "Local";
    protected Integer batchSize = 2000;

    protected ApplicationContext ctx;
    protected SqlMapClient sqlMap;
    protected ConcurrentHashMap<String, SqlMapClient> cachedSqlMapClientMap = new ConcurrentHashMap<String, SqlMapClient>(12);

    private String appXmlPath;
    private String sqlMapConfig;
    private String cacheDataName;

    public AbstractSynchronizeTask(String appXmlPath, String sqlMapConfig) {
        super();
        this.appXmlPath = appXmlPath;
        this.sqlMapConfig = sqlMapConfig;
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(sqlMapConfig);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            reader.close();
        } catch (IOException e) {
            log.error("初始化{}发生异常：{}", sqlMapConfig, e);
        }
		ctx = getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(appXmlPath);
        }
        cachedSqlMapClientMap.put(localDBName, sqlMap);
    }

	public ApplicationContext getApplicationContext() {
	    return SpringContext.getApplicationContext();
	}
	
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            this.work();
        } catch (Exception e) {
            log.error("发生错误：{}", e);
        }
    }

    public void work() throws IOException, SQLException {
        ctx = SpringContext.getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(this.appXmlPath);
        }
    }
    
    /**
     * 数据同步统一方法，遵循以下条件
     * <ol>
     * <li>数据库连接和语句使用sqlMapConfig命名遵循sqlMapConfig{{dbName}}.xml</li>
     * <li>select语句遵循select{{dataName}}，参数为map</li>
     * <li>delete语句遵循delete{{dataName}}，带参数选择性删除使用delete{{dataName}}ByParams</li>
     * <li>insert语句遵循insert{{dataName}}，参数为list</li>
     * </ol>
     * 
     * @param dataName 数据对象名
     * @param sourceDbName   数据来源名
     * @param params   同步参数
	 * @return 
     * @throws SQLException
     * @throws IOException
     */
    protected List<Map<String, Object>> syncData(String dataName, String sourceDbName, Map<String, Object> params) throws SQLException, IOException {
        return syncData(dataName, sourceDbName, localDBName, params, true, true);
    }
    
    /**
     * 数据同步统一方法，遵循以下条件
     * <ol>
     * <li>数据库连接和语句使用sqlMapConfig命名遵循sqlMapConfig{{dbName}}.xml</li>
     * <li>select语句遵循select{{dataName}}，参数为map</li>
     * <li>delete语句遵循delete{{dataName}}，带参数选择性删除使用delete{{dataName}}ByParams</li>
     * <li>insert语句遵循insert{{dataName}}，参数为list</li>
     * </ol>
     * 
     * @param dataName 数据对象名
     * @param sourceDbName   数据来源名
     * @param params   同步参数
     * @return 
     * @throws SQLException
     * @throws IOException
     */
    protected List<Map<String, Object>> syncData(String dataName, String sourceDbName, Map<String, Object> params, boolean clear) throws SQLException, IOException {
        return syncData(dataName, sourceDbName, localDBName, params, clear, clear ? true : false);
    }
    
    /**
     * 数据同步统一方法，遵循以下条件
     * <ol>
     * <li>数据库连接和语句使用sqlMapConfig命名遵循sqlMapConfig{{dbName}}.xml</li>
     * <li>select语句遵循select{{dataName}}，参数为map</li>
     * <li>delete语句遵循delete{{dataName}}，带参数选择性删除使用delete{{dataName}}ByParams</li>
     * <li>insert语句遵循insert{{dataName}}，参数为list</li>
     * </ol>
     * 
     * @param dataName 数据对象名
     * @param sourceDbName   数据来源名
     * @param targetDbName   数据目标名
     * @param params   同步参数
     * @param transaction 是否清理数据
     * @param transaction 是否开启事务
	 * @return 
     * @throws SQLException
     * @throws IOException
     */
	protected List<Map<String, Object>> syncData(String dataName, String sourceDbName, Map<String, Object> params, boolean clear, boolean transaction) throws SQLException, IOException {
	    return syncData(dataName, sourceDbName, localDBName, params, clear, transaction);
    }
    
    /**
     * 数据同步统一方法，遵循以下条件
     * <ol>
     * <li>数据库连接和语句使用sqlMapConfig命名遵循sqlMapConfig{{dbName}}.xml</li>
     * <li>select语句遵循select{{dataName}}，参数为map</li>
     * <li>delete语句遵循delete{{dataName}}，带参数选择性删除使用delete{{dataName}}ByParams</li>
     * <li>insert语句遵循insert{{dataName}}，参数为list</li>
     * </ol>
     * 
     * @param dataName 数据对象名
     * @param sourceDbName   数据来源名
     * @param targetDbName   数据目标名
     * @param params   同步参数
     * @throws SQLException
     * @throws IOException
     */
	protected List<Map<String, Object>> syncData(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params, boolean clear, boolean transaction) throws SQLException, IOException {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("sourceDbName", sourceDbName);
        params.put("targetDbName", targetDbName);
	    params.put("syncDataName", dataName);
        
        String tag = getTag(dataName);
        log.info("{}-开始", tag);
        
        SqlMapClient sqlMapTarget = getSqlMapClient(targetDbName);
        
        // 同步前的一些处理
        syncDataBefore(dataName, sourceDbName, targetDbName, params);
		List<Map<String, Object>> dataList = Collections.emptyList();
        try {
            // 刷新同步信息
            log.info("{}-查询数据开始", tag);
			dataList = syncDataQuery(dataName, sourceDbName, params);
            log.info("{}-查询数据结束", tag);

            if (transaction) {
                log.info("{}-开始事务", tag);
                sqlMapTarget.startTransaction();
            }
            
            if (clear) {
                // 清理目标数据库的数据
                log.info("{}-清理数据开始", tag);
                syncDataClear(dataName, targetDbName, params);
                log.info("{}-清理数据结束", tag);
            }
            
            log.info("{}-插入数据开始", tag);
            // 数据同步插入时的前置操作
            log.info("{}-插入数据前置操作", tag);
            syncDataInsertBefore(dataList, params);
            
            // 定义SQL语句
            log.info("{}-插入数据", tag);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            int i = 0;
			for (Map<String, Object> data : dataList) {
                if (i < batchSize) {
                    i++;
                    list.add(data);
                } else {
                    syncDataInsert(dataName, targetDbName, list, params);
					list.clear();
					i = 1;
                    list.add(data);
                }
            }

            if (!list.isEmpty()) {
                syncDataInsert(dataName, targetDbName, list, params);
            }
            
            // 数据同步插入时的后置操作
            log.info("{}-插入数据后置操作", tag);
            syncDataInsertAfter(dataList, params);
            
            log.info("{}-插入数据结束", tag);
            
            if (transaction) {
                log.info("{}-提交事务", tag);
                sqlMapTarget.commitTransaction();
            }

            // 数据同步成功
            syncDataSuccess(dataName, sourceDbName, targetDbName, params);
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
            if (transaction) {
                try {
                    log.error("{}-回滚事务", tag);
                    if (sqlMapTarget.getCurrentConnection() != null) {
                        sqlMapTarget.getCurrentConnection().rollback();
                    }
                } catch (Exception ex) {
                    log.error("{}-回滚事务失败：{}", tag, ex);
                }
            }
            // 数据同步成功
            syncDataFail(dataName, sourceDbName, targetDbName, params, e);
        } finally {
            if (transaction) {
                log.info("{}-结束事务", tag);
                sqlMapTarget.endTransaction();
            }
        }
        
        // 同步后的一些处理
        log.info("{}-同步数据后置操作", tag);
        syncDataAfter(dataName, sourceDbName, targetDbName, params);
        
        log.info("{}-结束", tag);
		return (List<Map<String, Object>>) dataList;
    }
    
    /**
     * 在数据同步之前的前置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 默认Local
     * @param params
     */
    protected void syncDataBefore(String dataName, String sourceDbName, Map<String, Object> params) {
        syncDataBefore(dataName, sourceDbName, localDBName, params);
    }
    
    /**
     * 在数据同步之前的前置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName
     * @param params
     */
    protected void syncDataBefore(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        Map<String, Object> logParams = new HashMap<String, Object>();
        //开始同步，增加日志
        logParams.put("refreshTaskName", this.getClass().toString() + "." + dataName);
        logParams.put("handleUser", "system");
        logParams.put("dataFrom", sourceDbName);
        logParams.put("dataTo", targetDbName);
        logParams.put("dataName", dataName);
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
     * @param targetDbName 默认Local
     * @param params
     */
    protected void syncDataSuccess(String dataName, String sourceDbName, Map<String, Object> params) {
        syncDataSuccess(dataName, sourceDbName, localDBName, params);
    }

    /**
     * 在数据同步成功的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 
     * @param params
     */
    protected void syncDataSuccess(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新成功日志
        logParams.put("refreshTo", new Date());
        logParams.put("refreshState", 1);
    }
    
    /**
     * 在数据同步失败的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 默认Local
     * @param params
     */
    protected void syncDataFail(String dataName, String sourceDbName, Map<String, Object> params, Throwable e) {
        syncDataFail(dataName, sourceDbName, localDBName, params, e);
    }
    
    /**
     * 在数据同步失败的后置操作
     * 
     * @param dataName
     * @param dbName
     * @param params
     * @param e 
     */
    protected void syncDataFail(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params, Throwable e) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新失败日志
        logParams.put("refreshException", ExceptionUtils.getStackTrace(e));
    }
    
    /**
     * 在数据同步之后的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 默认Local
     * @param params
     */
    protected void syncDataAfter(String dataName, String sourceDbName, Map<String, Object> params) {
        syncDataAfter(dataName, sourceDbName, localDBName, params);
    }
        
    /**
     * 在数据同步之后的后置操作
     * 
     * @param dataName
     * @param sourceDbName
     * @param targetDbName 
     * @param params
     */
    protected void syncDataAfter(String dataName, String sourceDbName, String targetDbName, Map<String, Object> params) {
    	Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新日志
        try {
            getSqlMapClient(targetDbName).update("update_fnd_data_refresh_log", logParams);
        } catch (SQLException e) {
        }
    }
    
    /**
     * 清理数据
     * @param dataName
     * @param dbName
     * @param params
     * @return
     */
    protected List<Map<String, Object>> syncDataQuery(String dataName, String dbName, Map<String, Object> params) {
        return syncDataQueryBySql(dataName, dbName, (String) params.get("querySql"), params);
    }
    
    /**
     * 查询数据
     * @param dataName
     * @param dbName
     * @param querySql
     * @param params
     * @return
     */
    protected List<Map<String, Object>> syncDataQueryBySql(String dataName, String dbName, String querySql, Map<String, Object> params) {
        String tag = getTag(dataName);
        try {
            SqlMapClient sqlMapOuter = getSqlMapClient(dbName);

            // 定义查询语句
            String select = StringUtils.defaultIfBlank(querySql, "select" + dataName);
            
            log.info("{}-查询所有数据{}", tag, select, params);
            // 刷新同步信息
            List<Map<String, Object>> dataList = sqlMapOuter.queryForList(select, params);
            log.info("{}-获取到数据{}条", tag, dataList.size());
            return dataList;
        } catch (Exception e) {
            log.error("{}-发生异常：{}", tag, e);
            throw new CustomRuntimeException(e);
        }
    }
    
    /**
     * 清理数据
     * @param dataName
     * @param dbName
     * @param params
     */
    protected void syncDataClear(String dataName, String dbName, Map<String, Object> params) {
        syncDataClearBySql(dataName, dbName, (String) params.get("deleteSql"), params);
    }
    
    /**
     * 清理数据
     * @param dataName
     * @param dbName
     * @param params
     */
    protected void syncDataClearBySql(String dataName, String dbName, String deleteSql, Map<String, Object> params) {
        String tag = getTag(dataName);
        try {
            SqlMapClient sqlMapClient = getSqlMapClient(dbName);
            
            // 定义SQL语句
            if (StringUtils.isNotBlank(deleteSql)) {
                log.info("{}-数据清理{}：{}", new Object[] { tag, deleteSql, params });
                sqlMapClient.delete(deleteSql, params);
            } else {
                String delete = "delete" + dataName;
                String deleteByParams = delete + "ByParams";
                
                if (params != null && !(params.isEmpty() || (params.size() == 1 && params.containsKey("logParams")))) {
                    log.info("{}-数据清理{}：{}", new Object[] { tag, deleteByParams, params });
                    try {
                        sqlMapClient.delete(deleteByParams, params);
                    } catch (Exception e) {
                        sqlMapClient.delete(delete, params);
                    }
                } else {
                    log.info("{}-数据清理{}", tag, delete);
                    sqlMapClient.delete(delete, params);
                }
            }
        } catch (Exception e) {
            log.error("{}-清理发生异常：{}", tag, e);
            throw new CustomRuntimeException(e);
        }
    }
    
    /**
     * 在数据同步Insert之前的前置操作
     * 
     * @param dataName
     * @param dbName
     * @param params
     */
    protected void syncDataInsertBefore(List<Map<String, Object>> list, Map<String, Object> params) {
    }
    
    /**
     * 数据Insert操作
     * @param list
     * @param params
     */
    protected void syncDataInsert(String dataName, List<Map<String, Object>> list, Map<String, Object> params) {
        syncDataInsert(dataName, localDBName, list, params);
    }
    
    /**
     * 数据Insert操作
     * @param list
     * @param params
     */
    protected void syncDataInsert(String dataName, String dbName, List<Map<String, Object>> list, Map<String, Object> params) {
        syncDataInsertBySql(dataName, dbName, (String) params.get("insertSql"), list, params);
    }
    
    /**
     * 数据Insert操作
     * @param list
     * @param params
     */
    protected void syncDataInsertBySql(String dataName, String dbName, String insertSql, List<Map<String, Object>> list, Map<String, Object> params) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String tag = getTag(dataName);
        try {
            SqlMapClient sqlMapClient = getSqlMapClient(dbName);
            // 定义SQL语句
            String insert = StringUtils.defaultIfBlank(insertSql, "insert" + dataName);

//            // 数据同步插入时的前置操作
//            log.info("{}-插入数据前置操作", tag);
//            syncDataInsertBefore(list, params);
            
            log.info("{}-插入数据{}，数量：{}", tag, insert, list.size());
            Map<String, Object> paramMap = new HashMap<String, Object>(12);
            paramMap.putAll(params);
            paramMap.put("list", list);
            // 数据插入
            try {
                sqlMapClient.insert(insert, paramMap);
            } catch (SQLException e) {
                if (e.getMessage() != null) {
                    // 创建 Pattern 对象，使用 DOTALL 标志
                    Pattern pattern = Pattern.compile(".*Expected '.*List' but found '.*Map'.*", Pattern.DOTALL);
                    
                    // 创建 Matcher 对象
                    Matcher matcher = pattern.matcher(e.getMessage());
                    if (matcher.matches()) {
                        sqlMapClient.insert(insert, list);
                    } else {
                        throw e;
                    }
                }
            }
            
//            // 数据同步插入时的后置操作
//            log.info("{}-插入数据后置操作", tag);
//            syncDataInsertAfter(list, params);
        } catch (Exception e) {
            log.error("{}-插入发生异常：{}", tag, e);
            throw new CustomRuntimeException(e);
        }
    }
    
    /**
     * 在数据同步Insert之后的后置操作
     * 
     * @param dataName
     * @param dbName
     * @param params
     */
    protected void syncDataInsertAfter(List<Map<String, Object>> list, Map<String, Object> params) {
    }
    
    /**
     * 获取dbName对应的SqlMapClient
     * 
     * @param dbName 数据源名
     * @return SqlMapClient
     */
    public SqlMapClient getSqlMapClient(String dbName) {
        String tag = getTag();
        try {
            log.info("{}-获取{}对应SalMapClient", tag, dbName);
            SqlMapClient sqlMapOuter = cachedSqlMapClientMap.get(dbName);
            if (null == sqlMapOuter) {
                sqlMapOuter = initSqlMapClient(dbName);
                cachedSqlMapClientMap.put(dbName, sqlMapOuter);
            }
            return sqlMapOuter;
        } catch (Exception e) {
            log.error("{}-获取{}对应SalMapClient失败：{}", new Object[] { tag, dbName, e });
        }
        return null;
    }

    /**
     * 获取打印日志标签
     * 
     * @return
     */
    protected String getTag() {
        return getTag(null);
    }
    
    /**
     * 获取打印日志标签
     * 
     * @return
     */
    protected String getTag(String dataName) {
        String tagName = this.getClass().getSimpleName();
        if (StringUtils.isNotBlank(dataName)) {
            cacheDataName = dataName;
            tagName = dataName;
        } else if (StringUtils.isNotBlank(cacheDataName)) {
            tagName = cacheDataName;
        }
        String tag = "执行[" + tagName + "]同步数据";
        return tag;
    }

    /**
     * 初始化SqlMapClient
     * 
     * @param dbName
     * @return
     * @throws IOException
     */
    private SqlMapClient initSqlMapClient(String dbName) throws IOException {
	    String tag = getTag();
        Reader readerOuter = null;
        try {
		    log.info("{}-初始化{}对应SalMapClient", tag, dbName);
		    try {
		        readerOuter = Resources.getResourceAsReader("sqlMapConfig" + dbName + ".xml");
		    } catch (Exception e) {
		        readerOuter = Resources.getResourceAsReader("sqlMapConfig" + dbName.toUpperCase() + ".xml");
            }
            return SqlMapClientBuilder.buildSqlMapClient(readerOuter);
        } finally {
            if (readerOuter != null) {
                readerOuter.close();
            }
        }
    }
    
    /**
     * 填充查询参数的时间
     * @param dateType
     * @return
     */
    public static Map<String, Object> initQueryDateRange(Integer dayRange, Integer dayOffset) {
        Date currentDate = new Date();
        // 当前时间往前偏移多少天，确定为同步结束日期
        Date offsetEndDate = DateUtil.endOfDay(DateUtil.offsetDay(currentDate, -dayOffset));
        offsetEndDate = offsetEndDate.after(currentDate) ? currentDate : offsetEndDate;
        // 根据同步日期范围，已经前面得出的结束日期，确定同步的开始日期
        Date offsetStartDate = DateUtil.beginOfDay(DateUtil.offsetDay(offsetEndDate, -dayRange + 1));
        String startTime = DateUtil.formatDateTime(offsetStartDate);
        String endTime = DateUtil.formatDateTime(offsetEndDate);
        Map<String, Object> queryDate = new HashMap<>();
        queryDate.put("startTime", startTime);
        queryDate.put("beginTime", startTime);
        queryDate.put("endTime", endTime);
        queryDate.put("startDate", startTime);
        queryDate.put("beginDate", startTime);
        queryDate.put("endDate", endTime);
        return queryDate;
    }

}
