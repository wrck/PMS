package com.dp.plat.pms.springmvc.job;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.dp.plat.core.config.DataSourceHolder;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.pojo.SyncState;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.entity.OfstContractHeadSAP;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
//github.com/wrck/PMS
import com.dp.plat.pms.springmvc.vo.ProjectProduct;

/**
 * 全量更新Job
 * 
 * @author w02611
 *
 */
public class SapFacilitatorJob {
	/**
	 * 批量插入数量
	 */
	private final static int BATCH_INSERT_NUMBER = 1000;
	/**
	 * 全量同步，同步类型：1
	 */
	private final static short SYNC_TYPE = 1;

	@Resource
	private IPmSynchronizeService pmSynchronizeService;

	public void execute() {
		ApplicationContext ctx = SpringContext.getApplicationContext();
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("spring.xml");
		}
		if (pmSynchronizeService == null) {
			pmSynchronizeService = ctx.getBean("pmSynchronizeService", IPmSynchronizeService.class);
		}
		System.out.println("执行全量更新定时程序开始：" + DateUtil.getTodayDateTime());
		SyncLog syncLog = new SyncLog(this.getClass().getName() + ".execute", "full_sync", SYNC_TYPE);
		syncLog.setDataFrom("OuterDataSource");
		syncLog.setDataTo("PMS");
		Class<?>[] clazzArrs = new Class[] {Facilitator.class};
		String[] dataSourceFromKeys = new String[] { "SAP" };
		String[] dataSourceToKeys = new String[] { "PMS" };
		try {
			pmSynchronizeService.clearSyncState();
			Integer threadPoolSize = 3;
			try {
				threadPoolSize = Integer
						.valueOf(SystemConfig.systemVariables.getOrDefault("sys.sync.threadPool.size", "3"));
				threadPoolSize = Math.max(threadPoolSize > clazzArrs.length ? clazzArrs.length : threadPoolSize, 1);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			long a = System.currentTimeMillis();

			ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
			for (int i = 0; i < clazzArrs.length; i++) {
				final Class<?> clazz = clazzArrs[i];
				final String dataSourceTo = dataSourceToKeys != null ? dataSourceToKeys[i] : "Local";
				final String[] dataSource = new String[] { dataSourceFromKeys[i], dataSourceTo };
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							insert(clazz, dataSource);
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
			System.out.println("执行全量更新定时程序结束，共耗时" + (b - a) / 1000 + " s");
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
							((DruidDataSource) dataSource).restart();
						} else if(dataSource instanceof DriverManagerDataSource) {
							((DriverManagerDataSource) dataSource).getConnection().close();
						}
					}
				}
			} catch (Exception e) {
				syncLog.setException(syncLog.getException() + "\r\n" + ExceptionUtils.getStackTrace(e));
			}
			pmSynchronizeService.insertSyncLog(syncLog);
		}
	}

	public void insert(Class<?> objectClass, String... dataSource) {
		long a = System.currentTimeMillis();
		SyncLog syncLog = new SyncLog(this.getClass().getName() + ".insert", objectClass.getName(), SYNC_TYPE);
		SyncState syncState = null;
		try {
			String methodName = objectClass.getSimpleName();
			List<?> objects = null;
			Class<?> clazz = pmSynchronizeService.getClass();
			Method method;
			if (dataSource.length > 0) {
				DataSourceHolder.setDataSourceType(dataSource[0]);
				syncLog.setDataFrom(dataSource[0]);
			}
			// 查询所有的源数据
			method = clazz.getMethod("selectAll" + methodName);
			objects = (List<?>) method.invoke(pmSynchronizeService);

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
				method = clazz.getMethod("clearAll" + methodName);
				method.invoke(pmSynchronizeService);
			} catch (NoSuchMethodException e) {
			}
			List<List<?>> lists = new ArrayList<List<?>>();
			int totalSize = objects.size();
			syncLog.setDataCount(totalSize);
			int count = (int) Math.ceil((double) totalSize / BATCH_INSERT_NUMBER);// 需要拆分成几个大小为number的list
			for (int i = 0; i < count; i++) {
				lists.add(objects.subList(i * BATCH_INSERT_NUMBER,
						(i + 1) * BATCH_INSERT_NUMBER > totalSize ? totalSize : (i + 1) * BATCH_INSERT_NUMBER));
			}
			// 释放数组内存
			objects = null;
			for (List<?> list : lists) {
				method = clazz.getMethod("insert" + methodName, List.class);
				method.invoke(pmSynchronizeService, list);
			}
			
			syncLog.setIsSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			syncLog.setException(ExceptionUtils.getStackTrace(e));
		} finally {
			DataSourceHolder.clearDataSourceType();
			pmSynchronizeService.insertSyncState(syncState);
			pmSynchronizeService.insertSyncLog(syncLog);
		}
		long b = System.currentTimeMillis();
		System.out.println("耗时" + (b - a) / 1000 + " s");
	}

	
	public static void main(String[] args) {
		new SapFacilitatorJob().execute();
	}
}
