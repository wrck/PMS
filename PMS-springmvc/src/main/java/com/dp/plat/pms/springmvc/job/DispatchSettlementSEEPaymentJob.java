package com.dp.plat.pms.springmvc.job;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

/**
 * 派单结算SSE付款信息Job（数据同步在PMS-struts中已经同步过），这里只做更新
 * 
 * @author w02611
 * @see com.dp.plat.subcontract.quartz.SubcontractPaymentAutoComplete
 */
public class DispatchSettlementSEEPaymentJob {
	/**
	 * 批量插入数量
	 */
	private final static int BATCH_INSERT_NUMBER = 1000;
	/**
	 * 全量同步，同步类型：1
	 */
	private final static short SYNC_TYPE = 1;

	@Resource
	private IDispatchSettlementService dispatchSettlementService;
	
	@Resource
	private IPmSynchronizeService pmSynchronizeService;

	public void execute() {
		if (pmSynchronizeService == null) {
			pmSynchronizeService = SpringContext.getBean("pmSynchronizeService", IPmSynchronizeService.class);
		}
		if (dispatchSettlementService == null) {
			dispatchSettlementService = SpringContext.getBean("dispatchSettlementService", IDispatchSettlementService.class);
		}
		
		System.out.println("执行派单结算SSE付款信息更新定时程序开始：" + DateUtil.getTodayDateTime());
		SyncLog syncLog = new SyncLog(this.getClass().getName() + ".execute", "full_sync", SYNC_TYPE);
		syncLog.setDataFrom("SSE");
		syncLog.setDataTo("PMS");
		Class<?>[] clazzArrs = new Class[] { DispatchSettlement.class};
		String[] dataSourceFromKeys = new String[] {};
		String[] dataSourceToKeys = new String[] {};
		Boolean insertNew = Boolean.valueOf(SystemConfig.systemVariables.getOrDefault("pm.sync.settlement.payment.insertNew", "false"));
		try {
			long a = System.currentTimeMillis();
			List<SettlementVO> paymentList = dispatchSettlementService.querySSEDispatchSettlementPaymentList();
	        Set<Integer> dispatchIds = new HashSet<>();
	        for (Iterator<SettlementVO> iterator = paymentList.iterator(); iterator.hasNext();) {
	            try {
	            	SettlementVO settlement = iterator.next();
	            	// 已经提报销但是没有结算的单据是否自动添加
	            	if (!Boolean.TRUE.equals(insertNew) && settlement.getId() == null) {
	            		iterator.remove();
	            		continue;
	            	}
	            	if (settlement.getId() != null) {
		            	settlement.setAmount(null);
		            	settlement.setRatio(null);
	            	}
//	                Integer dispatchId = settlement.getDispatchId();
//	                if (!dispatchIds.contains(dispatchId)) {
//	                    dispatchIds.add(dispatchId);
//	                }
	            } catch(Exception e) {
	                iterator.remove();
	            }
	        }
	        if (!paymentList.isEmpty()) {
	        	dispatchSettlementService.saveSettlementPayment(paymentList);
	        }
//	        if (!dispatchIds.isEmpty()) {
//	        	dispatchSettlementService.deleteEmptySubcontractPayment(StringUtils.join(dispatchIds, ","));
//	        }
//	        // 更新同步SSE付款信息后未付款的付款时间和备注
//	        dispatchSettlementService.updateSSESubcontractPaymentTime();
			
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
					Object dataSource = SpringContext.getBean("dataSource" + key);
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

}
