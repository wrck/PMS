package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 同步数据进行虚拟方法
 * 
 * @author user
 *
 */
public abstract class AbstractSynchronizeTask implements Job {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected Integer batchSize = 2000;

	protected ApplicationContext ctx;
	protected SqlMapClient sqlMap;
	protected ConcurrentHashMap<String, SqlMapClient> cachedSqlMapClientMap = new ConcurrentHashMap<String, SqlMapClient>(12);

	private String appXmlPath;
	private String sqlMapConfig;

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
		ctx = SpringContext.getApplicationContext();
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext(appXmlPath);
		}
		cachedSqlMapClientMap.put("Local", sqlMap);
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
	 * @param dbName   数据来源名
	 * @param params   同步参数
	 * @throws SQLException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void syncData(String dataName, String dbName, Map<String, Object> params)
			throws SQLException, IOException {
		String tag = getTag();
		log.info("{}-开始", tag);
		// 同步前的一些处理
		syncDataBefore(dataName, dbName, params);
		try {
			log.info("{}-初始化SalMapClient", tag);
			SqlMapClient sqlMapOuter = getSqlMapClient(dbName);

			// 定义查询语句
			String select = "select" + dataName;
			String delete = "delete" + dataName;
			String deleteByParams = delete + "ByParams";
			String insert = "insert" + dataName;

			log.info("{}-查询所有数据{}", tag, select);
			// 刷新同步SMS项目信息
			List<Map<String, Object>> dataList = sqlMapOuter.queryForList(select, params);
			log.info("{}-获取到数据{}条", tag, dataList.size());

			log.info("{}-开始事务", tag);
			sqlMap.startTransaction();
			if (params != null && !(params.isEmpty() || (params.size() == 1 && params.containsKey("logParams")))) {
				log.info("{}-数据清理{}：{}", new Object[] { tag, deleteByParams, params });
				try {
				    sqlMap.delete(deleteByParams, params);
				} catch (Exception e) {
				    sqlMap.delete(delete);
                }
			} else {
				log.info("{}-数据清理{}", tag, delete);
				sqlMap.delete(delete);
			}
			log.info("{}-插入数据{}", tag, insert);
			
			// 数据同步插入时的前置操作
		    syncDataInsertBefore(dataList, params);
			
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			int i = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>(12);
			for (Map<String, Object> data : dataList) {
				if (i < batchSize) {
					i++;
					list.add(data);
				} else {
					paramMap.put("list", list);
					sqlMap.insert(insert, paramMap);
					i = 0;
					list = new ArrayList<Map<String, Object>>();
					list.add(data);
				}
			}

			paramMap.put("list", list);
			if (!list.isEmpty()) {
				sqlMap.insert(insert, paramMap);
			}
			
			// 数据同步插入时的后置操作
			syncDataInsertAfter(dataList, params);
			
			// 数据同步成功
			syncDataSuccess(dataName, dbName, params);
			
			log.info("{}-提交事务", tag);
			sqlMap.commitTransaction();
		} catch (Exception e) {
			log.error("{}-发生异常：{}", tag, e);
			try {
				log.error("{}-回滚事务", tag);
				if (sqlMap.getCurrentConnection() != null) {
					sqlMap.getCurrentConnection().rollback();
				}
			} catch (Exception ex) {
				log.error("{}-回滚事务失败：{}", tag, ex);
			}
			// 数据同步成功
            syncDataFail(dataName, dbName, params, e);
		} finally {
			log.info("{}-结束事务", tag);
			sqlMap.endTransaction();
		}
		// 同步后的一些处理
		syncDataAfter(dataName, dbName, params);
	}
	
    /**
	 * 在数据同步之前的前置操作
	 * 
	 * @param dataName
	 * @param dbName
	 * @param params
	 */
	protected void syncDataBefore(String dataName, String dbName, Map<String, Object> params) {
	    Map<String, Object> logParams = new HashMap<String, Object>();
        //开始同步，增加日志
        logParams.put("refreshTaskName", this.getClass().toString() + "." + dataName);
        logParams.put("handleUser", "system");
        logParams.put("dataFrom", dbName);
        logParams.put("dataTo", null);
        logParams.put("refreshFrom", new Date());
        try {
            Object obj = sqlMap.insert("insert_fnd_data_refresh_log", logParams);
            logParams.put("id", obj);
        } catch (SQLException e) {
        }
        params.put("logParams", logParams);
	}
	
	/**
     * 在数据同步成功的后置操作
     * 
     * @param dataName
     * @param dbName
     * @param params
     */
	protected void syncDataSuccess(String dataName, String dbName, Map<String, Object> params) {
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
    protected void syncDataFail(String dataName, String dbName, Map<String, Object> params, Throwable e) {
        Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新失败日志
        logParams.put("refreshException", ExceptionUtils.getStackTrace(e));
    }
	
	/**
     * 在数据同步之后的后置操作
     * 
	 * @param dataName
	 * @param dbName
	 * @param params
	 */
	protected void syncDataAfter(String dataName, String dbName, Map<String, Object> params) {
	    Map<String, Object> logParams = (Map<String, Object>) params.getOrDefault("logParams", new HashMap());
        //更新日志
        try {
            sqlMap.update("update_fnd_data_refresh_log_success", logParams);
        } catch (SQLException e) {
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
	protected SqlMapClient getSqlMapClient(String dbName) {
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
		String className = this.getClass().getSimpleName();
		String tag = "执行[" + className + "]同步数据";
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
		Reader readerOuter = null;
		try {
			readerOuter = Resources.getResourceAsReader("sqlMapConfig" + dbName.toUpperCase() + ".xml");
			return SqlMapClientBuilder.buildSqlMapClient(readerOuter);
		} finally {
			if (readerOuter != null) {
				readerOuter.close();
			}
		}
	}
}
