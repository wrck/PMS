package com.dp.plat.ehr.job;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.persistence.Id;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.dp.plat.core.config.DataSourceHolder;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.pojo.SyncState;
import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.ehr.entity.Company;
import com.dp.plat.ehr.entity.Department;
import com.dp.plat.ehr.entity.EHRLoginAccount;
import com.dp.plat.ehr.entity.Employee;
import com.dp.plat.ehr.entity.Holiday;
import com.dp.plat.ehr.entity.Job;
import com.dp.plat.ehr.service.IEmployeeService;

/**
 * 全量更新Job
 * 
 * @author w02611
 *
 */
public class EhrDataJob {
	/**
	 * 批量插入数量
	 */
	private final static int BATCH_INSERT_NUMBER = 500;
	/**
	 * 全量同步，同步类型：1
	 */
	private final static short SYNC_TYPE = 1;

	@Resource
	private ISynchronizeService ehrSynchronizeService;
	
	@Resource
	private IEmployeeService employeeService;

	public void execute() {
		if (ehrSynchronizeService == null ){
			ehrSynchronizeService = SpringContext.getBean("ehrSynchronizeService", ISynchronizeService.class);
		}
		System.out.println("执行全量更新定时程序开始：" + DateUtil.getTodayDateTime());
		SyncLog syncLog = new SyncLog(this.getClass().getName() + ".execute", "full_sync", SYNC_TYPE);
		syncLog.setDataFrom("OuterDataSource");
		syncLog.setDataTo("Local");
		Class<?>[] clazzArrs = new Class[] { Company.class, Department.class, Job.class, Employee.class,
				EHRLoginAccount.class/* , Holiday.class */ };
		String[] dataSourceKeys = new String[] { "EHR", "EHR", "EHR", "EHR", "EHR", "EHR" };
		try {
			ehrSynchronizeService.clearSyncState();
			Integer threadPoolSize = 3;
			try {
				threadPoolSize = Integer.valueOf(SystemConfig.systemVariables.getOrDefault("sys.sync.threadPool.size", "3"));
				threadPoolSize = threadPoolSize > clazzArrs.length ? clazzArrs.length : threadPoolSize;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			long a = System.currentTimeMillis();

			ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
			for (int i = 0; i < clazzArrs.length; i++) {
				final Class<?> clazz = clazzArrs[i];
				final String[] dataSource = new String[] { dataSourceKeys[i], "Local" };
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
				hashSet.addAll(Arrays.asList(dataSourceKeys));
				for (String key : hashSet) {
					DruidDataSource dataSource = SpringContext.getBean("dataSource" + key, DruidDataSource.class);
					if (dataSource != null) {
						dataSource.restart();
					}
				}
			} catch (Exception e) {
				syncLog.setException(syncLog.getException() + "\r\n" + ExceptionUtils.getStackTrace(e));
			}
			ehrSynchronizeService.insertSyncLog(syncLog);
		}
		
		if (employeeService == null ){
			employeeService = SpringContext.getBean("employeeService", IEmployeeService.class);
		}
		employeeService.initUser(null);
	}

	public void insert(Class<?> objectClass, String... dataSource) {
		long a = System.currentTimeMillis();
		SyncLog syncLog = new SyncLog(this.getClass().getName() + ".insert", objectClass.getName(), SYNC_TYPE);
		try {
			String methodName = objectClass.getSimpleName();
			List<?> objects = null;
			Class<?> clazz = ehrSynchronizeService.getClass();
			Method method;
			if (dataSource.length > 0) {
				DataSourceHolder.setDataSourceType(dataSource[0]);
				syncLog.setDataFrom(dataSource[0]);
			}
			// 查询所有的源数据
			method = clazz.getMethod("selectAll" + methodName);
			objects = (List<?>) method.invoke(ehrSynchronizeService);

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
					Boolean hasId = field.isAnnotationPresent(Id.class);
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
			SyncState syncState = new SyncState(methodName, lastId, offset);

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
				method.invoke(ehrSynchronizeService);
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
				method.invoke(ehrSynchronizeService, list);
			}
			ehrSynchronizeService.insertSyncState(syncState);
			syncLog.setIsSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			syncLog.setException(ExceptionUtils.getStackTrace(e));
		} finally {
			DataSourceHolder.clearDataSourceType();
			ehrSynchronizeService.insertSyncLog(syncLog);
		}
		long b = System.currentTimeMillis();
		System.out.println("耗时" + (b - a) / 1000 + " s");
	}

}
