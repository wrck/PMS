package com.dp.plat.extend.erms.job;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.SystemContext;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.erms.api.ErmsApi;
import com.dp.plat.erms.util.ErmsUtil;
import com.dp.plat.erms.vo.DateRange;
import com.dp.plat.job.AbstractSynchronizeTask;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.subcontract.vo.SubcontractPaymentVO;
import com.dp.plat.util.MailUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;

/**
 * 推送项目转包的发票给FP系统邮箱
 * 
 * @author w02611
 *
 */
public class SubcontractInvoiceToFP extends AbstractSynchronizeTask implements Job {

    public SubcontractInvoiceToFP() {
		super("applicationContext.xml", "sqlMapConfig.xml");
		batchSize = 1000;
		
		// 初始化参数
        ErmsApi.initConfig(SystemContext.getConfig("sys.erms.api.config"));
	}

	public void work() throws IOException, SQLException {
	    Map<String, Object> config = ErmsApi.getConfig();
        if (!MapUtil.getBool(config, "enable", false)) {
            return;
        }
        
        // 获取配置的时间间隔
        Integer dayRange = MapUtil.getInt(config, "dateRange", 7);
        Integer dayOffset = MapUtil.getInt(config, "dateOffset", 0);
        String archiveUser = MapUtil.getStr(config, "user", "erms");
        DateRange dateRange = ErmsUtil.initDateRange(dayRange, dayOffset);
        
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchTimeType", "confirmTime");
		params.put("searchStartTime", dateRange.getStartTimeFormated());
		params.put("searchEndTime", dateRange.getEndTimeFormated());
		
		// 推送产品开票软件名称信息
		pushData("SubcontractPaymentToFP", "用服转包付款数据", params);
	}

	/**
	 * 刷新推送合同开票数据
	 */
	public boolean pushData(String dataName, String tag, Map<String, Object> params) {
        try {
			log.info("推送{}-开始", tag);
			// 推送合同开票数据
			syncData(dataName, "Local", params, false);
			log.info("同步{}-成功", tag);
			return true;
		} catch (Exception e) {
			log.error("同步{}-发生异常", tag, e);
		} finally {
			log.info("同步{}-结束", tag);
		}
		return false;
	}
	
	@Override
	protected void syncDataInsertBefore(List<Map<String, Object>> list, Map<String, Object> params) {
	    // FIXME TEST
	    List<Map<String, Object>> collect = list.stream().limit(batchSize).collect(Collectors.toList());
	    list.clear();
	    list.addAll(collect);
	    
//	    for (Map<String, Object> map : list) {
//	        if (map.containsKey("DATE")) {
//	            map.put("new_invoice_create_date", new Date(Long.valueOf(map.get("DATE").toString()) * 1000));
//	        }
//	        if (map.containsKey("STATUS") || map.containsKey("status")) {
//	            String status = String.valueOf(map.getOrDefault("STATUS", map.getOrDefault("status", -1)));
//	            if (!NumberUtils.isCreatable(status)) {
//	                map.put("new_invoice_status", "正常".equals(status) ? 1 : 2);
//	            }
//            }
//        }
	}

	@Override
    protected void syncDataInsert(String dataName, String dbName, List<Map<String, Object>> list, Map<String, Object> params) {
	    list = list != null ? list : Collections.emptyList();
	    log.info("{}-发送PMS发票数据至FP邮箱开始{}条", getTag(dataName), list.size());
	    
	    Map<String, Object> config = ErmsApi.getConfig();
        if (!MapUtil.getBool(config, "enable", false)) {
            return;
        }
        
        AtomicInteger successCount = new AtomicInteger();
        try {
            log.info("{}-发送PMS发票数据至FP邮箱开始{}条", getTag(dataName), list.size());
            
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setTos(MapUtil.getStr(config, "fpMailAddress", "ssetest.dptech.com"));
            
            Map<String, Object> defaultContext = new HashMap<String, Object>();
            defaultContext.put("templateCode", MapUtil.getStr(config, "mailTemplateCode", "subcontractMailToFP"));
            defaultContext.put("tos", MapUtil.getStr(config, "fpMailAddress", "ssetest.dptech.com"));
            defaultContext.put("beforeSplit", MapUtil.getStr(config, "beforeSplit", "${"));
            defaultContext.put("afterSplit", MapUtil.getStr(config, "afterSplit", "}"));
            
            SubcontractService subcontractService = SpringContext.getBean("subcontractService", SubcontractService.class);
            
            String invoiceFileType = MapUtil.getStr(config, "invoiceFileType", "发票原件");
            List<String> errorList = new ArrayList<String>();
            list.parallelStream().forEachOrdered(p-> {
                SubcontractPaymentVO paymentVO = new SubcontractPaymentVO();
                try {
                    BeanUtil.copyProperties(p, paymentVO);
                    SubcontractDeliver temp = new SubcontractDeliver();
                    temp.setType(invoiceFileType);
                    temp.setSubcontractId(paymentVO.getSubcontractId());
                    temp.setPaymentId(paymentVO.getId());
                    temp.setEffectiveTo(new Date());
                    List<SubcontractDeliver> deliverList = subcontractService.selectSubcontractDeliverList(temp);
                    List<String> attachFiles = new ArrayList<>(deliverList.size());
                    for (SubcontractDeliver deliver : deliverList) {
                        attachFiles.add(StringUtils.join(new Object[] {deliver.getFilePath(), deliver.getFileName() }, ","));
                    }
                    if (!attachFiles.isEmpty()) {
                        Map<String, Object> context = new HashMap<String, Object>(defaultContext);
                        context.put("attachFiles", StringUtils.join(attachFiles, "&&"));
                        context.put("dataSource", new Object[] { p });
                        MailUtil.keepMailWithTemplate(context, true);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorList.add(String.format("结算ID：%s，返回错误信息：%s", paymentVO.getId(), ExceptionUtils.getStackTrace(e)));
                }
            });
            
            log.info("{}-发送PMS发票数据至FP邮箱成功{}条，成功{}条，失败{}条", getTag(dataName), list.size(), successCount.intValue(), errorList.size());
        } catch (Exception e) {
            log.error("{}-发送PMS发票数据至FP邮箱-发生异常：{}", getTag(dataName), e);
        } finally {
        }
    }

    public static void main(String[] args) {
		try {
			new SubcontractInvoiceToFP().execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
