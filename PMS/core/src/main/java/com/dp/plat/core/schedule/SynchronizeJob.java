package com.dp.plat.core.schedule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.dp.plat.core.config.DataSourceHolder;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.pojo.SyncState;
import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.core.util.DateUtil;

/**
 * 全量更新Job
 * 
 * @author w02611
 *
 */
public class SynchronizeJob {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
	/**
	 * 批量插入数量，默认值
	 */
    protected final static int BATCH_INSERT_NUMBER = 1000;
    
    protected final static ConcurrentHashMap<String, SyncType> syncStateMap = new ConcurrentHashMap<String, SyncType>(12);
	
	/**
	 * 批量插入数量
	 */
    protected int batchSize = BATCH_INSERT_NUMBER;
    
    protected ApplicationContext ctx;
    
	/**
	 * 全量同步，同步类型：1
	 */
    private SyncType syncType;
	
	/**
     * 标识增量同步是否正在进行，避免增量同步与全量同时进行发生冲突
     */
    private boolean isSyncing = false;

    
    private Class<?>[] dataClassArrs;
    private String[] dataSourceFromKeys;
    private String[] dataSourceToKeys;

    protected ISynchronizeService synchronizeService;
	
    @Resource
    public void setSynchronizeService(ISynchronizeService synchronizeService) {
        this.synchronizeService = synchronizeService;
    }
    
    public SynchronizeJob(SyncType syncType, Class<?>[] dataClassArrs, String dataSourceFromKey, String dataSourceToKey) {
        this(syncType, dataClassArrs, new String[] { dataSourceFromKey }, new String[] { dataSourceToKey });
    }
    
	public SynchronizeJob(SyncType syncType, Class<?>[] dataClassArrs, String[] dataSourceFromKeys, String[] dataSourceToKeys) {
        super();
        this.syncType = syncType;
        this.dataClassArrs = dataClassArrs != null ? dataClassArrs : new Class<?>[0];

        // 自动补齐数据源
        String[] formSources = new String[dataClassArrs.length];
        String[] toSources = new String[dataClassArrs.length];
        int fromSize = dataSourceFromKeys.length;
        int toSize = dataSourceToKeys.length;
        for (int i = 0; i < dataClassArrs.length; i++) {
            formSources[i] = dataSourceFromKeys[fromSize > i ? i : (fromSize - 1)];
            toSources[i] = dataSourceToKeys[toSize > i ? i : (toSize - 1)];
        }
        this.dataSourceFromKeys = formSources;
        this.dataSourceToKeys = toSources;
    }
	
	public void initApplicationContext(String configLocation) {
	    ctx = SpringContext.getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(configLocation);
        }
	}
	
	public void execute() {
	    this.execute(new HashMap<String, Object>());
	}

    public void execute(Map<String, Object> params) {
//        if (isSyncing) {
//            log.info("正在执行" + syncType.getName());
//            return;
//        }
//        isSyncing = true;
		log.info("执行{}更新定时程序开始：{}", syncType.getName(), DateUtil.getTodayDateTime());
		String className = this.getClass().getName();
		SyncLog syncLog = new SyncLog(className + ".execute", syncType.getCode(), syncType.getType());
		syncLog.setDataFrom("OuterDataSource");
		syncLog.setDataTo("Local");
		try {
//		    // 判断是否正在同步
//            SyncType syncingType = syncStateMap.get(className);
//            if (syncingType != null) {
//                syncLog.setException("正在执行" + syncingType.getName());
//                return;
//            } else {
//                syncStateMap.put(className, syncType);
//            }
		    
//			pmSynchronizeService.clearSyncState();
			Integer threadPoolSize = 3;
			try {
				threadPoolSize = Integer.valueOf(SystemConfig.systemVariables.getOrDefault("sys.sync.threadPool.size", "3"));
				threadPoolSize = Math.max(threadPoolSize > dataClassArrs.length ? dataClassArrs.length : threadPoolSize, 1);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			long a = System.currentTimeMillis();

			ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
			for (int i = 0; i < dataClassArrs.length; i++) {
				final Class<?> clazz = dataClassArrs[i];
				final String dataSourceTo = dataSourceToKeys != null ? dataSourceToKeys[i] : "Local";
				final String[] dataSource = new String[] { dataSourceFromKeys[i], dataSourceTo };
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
						    if (SyncType.FULL_SYNC.equals(syncType)) {
						        insert(clazz, params, dataSource);
						    } else {
						        insertIncrement(clazz, params, dataSource);
						    }
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			threadPool.shutdown();
			while (!threadPool.isTerminated()) {
			}
			
			syncLog.setIsSuccess(true);
			long b = System.currentTimeMillis();
			log.info("执行{}定时程序结束，共耗时{}s", syncType.getName(), (b - a) / 1000);
		} catch (Exception e) {
			syncLog.setException(ExceptionUtils.getStackTrace(e));
		} finally {
			try {
				HashSet<String> hashSet = new HashSet<>();
				hashSet.addAll(Arrays.asList(dataSourceFromKeys));
				for (String key : hashSet) {
					Object dataSource = ctx.getBean("dataSource" + key);
					if (dataSource != null) {
						if (dataSource instanceof DruidDataSource) {
//							((DruidDataSource) dataSource).restart();
						} else if(dataSource instanceof DriverManagerDataSource) {
							((DriverManagerDataSource) dataSource).getConnection().close();
						}
					}
				}
			} catch (Exception e) {
				syncLog.setException(syncLog.getException() + "\r\n" + ExceptionUtils.getStackTrace(e));
			}
			synchronizeService.insertSyncLog(syncLog);
		}
		
//		isSyncing = false;
//		syncStateMap.remove(className, syncType);
	}

	public void insert(Class<?> objectClass, Map<String, Object> syncParams, String... dataSource) {
		long a = System.currentTimeMillis();
		String className = this.getClass().getName();
		SyncLog syncLog = new SyncLog(className + ".insert", objectClass.getName(), syncType.getType());
		SyncState syncState = null;
		String syncName = objectClass.getName();
		try {
		    // 判断是否正在同步
		    SyncType syncingType = syncStateMap.get(syncName);
		    if (syncingType != null) {
		        syncLog.setException("正在执行" + syncingType.getName());
		        return;
		    } else {
		        syncStateMap.put(syncName, syncType);
		    }
		    
			String methodName = objectClass.getSimpleName();
			List<?> objects = null;
			Class<?> clazz = synchronizeService.getClass();
			Method method;
			if (dataSource.length > 0) {
				DataSourceHolder.setDataSourceType(dataSource[0]);
				syncLog.setDataFrom(dataSource[0]);
			}
			// 查询所有的源数据
			try {
    			method = clazz.getMethod("selectAll" + methodName, Map.class);
    			objects = (List<?>) method.invoke(synchronizeService, syncParams);
			} catch (Exception e) {
			    method = clazz.getMethod("selectAll" + methodName);
                objects = (List<?>) method.invoke(synchronizeService);
            }

			// method = clazz.getMethod("count" + methodName);
			// Integer offset = (int) method.invoke(synchronizeService);
			Integer offset = objects.size();
			// 全量更新时需要将增量更新的syncState重置
			String lastId = null;
			if (!objects.isEmpty()) {
				Object obj = objects.get(objects.size() - 1);
				Class<?> objClazz = obj.getClass();
				Field[] fields = objClazz.getDeclaredFields();
				for (Field field : fields) {
					// 获取字段的@Id注解,即表的主键
					Boolean hasId = field.isAnnotationPresent(Primary.class);
					if (hasId) {
						String pkName = field.getName();
						method = objClazz.getMethod("get" + pkName.substring(0, 1).toUpperCase() + pkName.substring(1));
						if (method != null) {
							Object id = method.invoke(obj);
							lastId = String.valueOf(id == null ? "0" : id);
						}
						break;
					}
				}
			}
			syncState = new SyncState(methodName, lastId, offset);

			if (dataSource.length > 1) {
				DataSourceHolder.setDataSourceType(dataSource[1]);
				syncLog.setDataTo(dataSource[1]);
			} else {
				DataSourceHolder.setDataSourceType("Local");
				syncLog.setDataTo("Local");
			}

			// 清空临时表
			try {
				method = clazz.getMethod("clearAll" + methodName, Map.class);
				method.invoke(synchronizeService, syncParams);
			} catch (NoSuchMethodException e) {
			    try {
	                method = clazz.getMethod("clearAll" + methodName);
	                method.invoke(synchronizeService);
	            } catch (NoSuchMethodException e2) {
	            }
			}
			List<List<?>> lists = new ArrayList<List<?>>();
			int totalSize = objects.size();
			syncLog.setDataCount(totalSize);
			int count = (int) Math.ceil((double) totalSize / batchSize);// 需要拆分成几个大小为number的list
			for (int i = 0; i < count; i++) {
				lists.add(objects.subList(i * batchSize,
						(i + 1) * batchSize > totalSize ? totalSize : (i + 1) * batchSize));
			}
			// 释放数组内存
			objects = null;
			for (List<?> list : lists) {
				method = clazz.getMethod("insert" + methodName, List.class);
				method.invoke(synchronizeService, list);
			}
			
			syncLog.setIsSuccess(true);
		} catch (Exception e) {
//			e.printStackTrace();
			syncLog.setException(ExceptionUtils.getStackTrace(e));
		} finally {
			DataSourceHolder.clearDataSourceType();
			synchronizeService.insertSyncState(syncState);
			synchronizeService.insertSyncLog(syncLog);
		}
		
		syncStateMap.remove(syncName, syncType);
		long b = System.currentTimeMillis();
		log.info("耗时" + (b - a) / 1000 + " s");
	}
	
	private void insertIncrement(Class<?> objectClass, Map<String, Object> syncParams, String... dataSource) {
        long a = System.currentTimeMillis();
        String className = this.getClass().getName();
        String syncName = objectClass.getName();
        SyncLog syncLog = new SyncLog(className + ".insertIncrement", objectClass.getName(), syncType.getType());
        try {
            // 判断是否正在同步
            SyncType syncingType = syncStateMap.get(syncName);
            if (syncingType != null) {
                syncLog.setException("正在执行" + syncingType.getName());
                return;
            } else {
                syncStateMap.put(syncName, syncType);
            }
            
            Map<String, Object> params = null;
            String tableObject = objectClass.getSimpleName();
            params = synchronizeService.selectSyncState(tableObject);
            if (params == null) {
                params = new HashMap<String, Object>();
                params.put("lastId", "");
                params.put("offset", 0);
                params.put("lastSyncTime", new Date(0));
            } else if (params.get("lastSyncTime") == null) {
                params.put("lastId", "");
                params.put("offset", 0);
                params.put("lastSyncTime", new Date(0));
            }
            params.putAll(syncParams);
            try {
                syncLog.setSyncParams(JSON.toJSONStringWithDateFormat(params, "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                syncLog.setSyncParams(params.toString());
            }
            if (dataSource.length > 0) {
                DataSourceHolder.setDataSourceType(dataSource[0]);
                syncLog.setDataFrom(dataSource[0]);
            }
            Class<?> clazz = synchronizeService.getClass();
            List<?> list = null;
            Method method = clazz.getMethod("selectIncrement" + tableObject, Map.class);
            list = (List<?>) method.invoke(synchronizeService, params);

            method = clazz.getMethod("count" + tableObject);
            Integer offset = (int) method.invoke(synchronizeService);

            String lastId = null;
            if (!list.isEmpty()) {
                Object obj = list.get(list.size() - 1);
                Class<?> objClazz = obj.getClass();
                Field[] fields = objClazz.getDeclaredFields();
                for (Field field : fields) {
                    // 获取字段的@Id注解,即表的主键
                    Boolean hasId = field.isAnnotationPresent(Id.class);
                    if (hasId) {
                        String pkName = field.getName();
                        Class<?> pkType = field.getType();
                        method = objClazz.getMethod("get" + pkName.substring(0, 1).toUpperCase() + pkName.substring(1));
                        if (method != null) {
                            lastId = String.valueOf(method.invoke(obj));
                            if (pkType.equals(Integer.class)) {
                                String paramsLastId = (String) params.get("lastId");
                                paramsLastId = StringUtils.isBlank(paramsLastId) ? "0" : paramsLastId;
                                try {
                                    lastId = Integer.valueOf(lastId) > Integer.valueOf(paramsLastId) ? lastId : null;
                                } catch (NumberFormatException e) {
                                    lastId = lastId.compareTo((String) params.get("lastId")) > 0 ? lastId : null;
                                }
                            } else {
                                lastId = lastId.compareTo((String) params.get("lastId")) > 0 ? lastId : null;
                            }
                        }
                        break;
                    }
                }
            }
            SyncState syncState = new SyncState(tableObject, lastId, offset);

            if (dataSource.length > 1) {
                DataSourceHolder.setDataSourceType(dataSource[1]);
                syncLog.setDataTo(dataSource[1]);
            } else {
                DataSourceHolder.setDataSourceType("Local");
                syncLog.setDataTo("Local");
            }
            List<List<?>> lists = new ArrayList<List<?>>();
            int totalSize = list.size();
            syncLog.setDataCount(totalSize);
            int count = (int) Math.ceil((double) totalSize / batchSize);// 需要拆分成几个大小为5000的list
            for (int i = 0; i < count; i++) {
                lists.add(list.subList(i * batchSize,
                        (i + 1) * batchSize > totalSize ? totalSize : (i + 1) * batchSize));
            }

            // 释放数组内存
            list = null;

            for (List<?> tempList : lists) {
                method = clazz.getMethod("insert" + tableObject, List.class);
                method.invoke(synchronizeService, tempList);
            }

            // 插入执行完成后，更新增量同步状态表
            synchronizeService.insertSyncState(syncState);
            syncLog.setIsSuccess(true);
        } catch (Exception e) {
//            e.printStackTrace();
            syncLog.setException(ExceptionUtils.getStackTrace(e));
        } finally {
            DataSourceHolder.clearDataSourceType();
            synchronizeService.insertSyncLog(syncLog);
        }
        
        syncStateMap.remove(syncName, syncType);
        long b = System.currentTimeMillis();
        log.info("耗时" + (b - a) + " ms");
    }

    public boolean isSyncing() {
        return isSyncing;
    }
    
	public SyncType getSyncType() {
        return syncType;
    }

    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }

    public Class<?>[] getDataClassArrs() {
        return dataClassArrs;
    }

    public void setDataClassArrs(Class<?>[] dataClassArrs) {
        this.dataClassArrs = dataClassArrs;
    }

    public String[] getDataSourceFromKeys() {
        return dataSourceFromKeys;
    }

    public void setDataSourceFromKeys(String[] dataSourceFromKeys) {
        this.dataSourceFromKeys = dataSourceFromKeys;
    }

    public String[] getDataSourceToKeys() {
        return dataSourceToKeys;
    }

    public void setDataSourceToKeys(String[] dataSourceToKeys) {
        this.dataSourceToKeys = dataSourceToKeys;
    }

    public static void main(String[] args) {
	    SynchronizeJob synchronizeJob = new SynchronizeJob(SyncType.FULL_SYNC, null, "Local", "Local");
	    synchronizeJob.initApplicationContext("spring.xml");
	    synchronizeJob.execute();
	}
	
}
