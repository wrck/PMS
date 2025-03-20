package com.dp.plat.pms.springmvc.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import com.dp.plat.context.SystemContext;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.schedule.SyncType;
import com.dp.plat.core.service.ISyncLogService;
import com.dp.plat.core.service.IUploaderService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.erms.api.ErmsApi;
import com.dp.plat.erms.util.ErmsUtil;
import com.dp.plat.erms.vo.DateRange;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.vo.SettlementVO;
import com.dp.plat.support.mail.MailUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;

/**
 * 推送项目转包的发票给FP系统邮箱
 * @author w02611
 *
 */
public class DispatchSettlementInvoiceToFPJob implements Job {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private IDispatchProjectService dispatchProjectService;
	
	@Resource
	private IDispatchSettlementService dispatchSettlementService;
	
	@Resource
    private IUploaderService uploaderService;
	
	@Resource
    private IUserService userService;
	
	@Resource
    private ISyncLogService syncLogService;

	@Resource
    private SecurityManager securityManager;

    private SyncType syncType;
	
	public void execute() {
	    // 初始化参数
	    Map<String, Object> config = JSON.parseObject(MapUtil.getStr(SystemConfig.systemVariables, "sys.erms.api.config", "{}"), Map.class);
	    if (!MapUtil.getBool(config, "enable", false)) {
	        return;
	    }
	    
	    // 获取配置的时间间隔
        Integer dayRange = MapUtil.getInt(config, "dateRange", 7);
        Integer dayOffset = MapUtil.getInt(config, "dateOffset", 0);
        String archiveUser = MapUtil.getStr(config, "user", "erms");
        DateRange dateRange = ErmsUtil.initDateRange(dayRange, dayOffset);

        // 查询创建和更新时间在时间范围内的所有外派结算
        PageParam<SettlementVO> pageParam = new PageParam<SettlementVO>();
        pageParam.setPageSize(-1L);
        SettlementVO model = new SettlementVO();
        model.setDateType("confirmTime");
        model.setDateStartTime(dateRange.getStartTime());
        model.setDateEndTime(dateRange.getEndTime());
        model.setDisabled(false);
        model.setDispatched(true);
        pageParam.setModel(model);
        
        syncType = SyncType.INCREM_SYNC;
        log.info("执行{}更新定时程序开始：{}", syncType, DateUtil.formatDateTime(new Date()));
        String className = this.getClass().getName();
        SyncLog syncLog = new SyncLog(className + ".execute", syncType.getCode(), syncType.getType());
        syncLog.setDataFrom("Local");
        syncLog.setDataTo("FP");
        syncLog.setSyncParams(ErmsApi.toJSONString(model));
        
        try {
            // 获取业务发票的票夹
            List<DispatchSettlement> list = dispatchSettlementService.selectSettlementWidthDispatchPageable((PageParam)pageParam);
//            dispatchSettlementService.selectBySelective(model);
//            dispatchSettlementService.countBySelective(model);
//            dispatchSettlementService.selectBySelectivePageable(pageParam);
//            dispatchSettlementService.countBySelectivePageable(pageParam);
//            dispatchSettlementService.selectSettlementWidthDispatchPageable((PageParam)pageParam);
//            dispatchSettlementService.countSettlementWidthDispatchPageable((PageParam)pageParam);
            SyncLog folderSyncLog = pushFolderInvoice(list, config);
            syncLog.setDataCount(folderSyncLog.getDataCount());
            syncLog.setException(folderSyncLog.getException());
            syncLog.setIsSuccess(true);
        } catch (Exception e) {
            syncLog.setException(ExceptionUtils.getStackTrace(e));
        } finally {
            syncLogService.insertSelective(syncLog);
        }
	}

	public SyncLog pushFolderInvoice(List<DispatchSettlement> list, Map<String, Object> config) {
	    list = list != null ? list : Collections.emptyList();
	    String className = this.getClass().getName();
        SyncLog syncLog = new SyncLog(className + ".execute", "安服发票", syncType.getType());
        syncLog.setDataFrom("Local");
        syncLog.setDataTo("FP");
        if (!MapUtil.getBool(config, "enable", false)) {
            return syncLog;
        }
        
        AtomicInteger successCount = new AtomicInteger();
        try {
            log.info("{}-发送安服发票数据至FP邮箱开始{}条", className, list.size());
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setTos(MapUtil.getStr(config, "fpMailAddress", "ssetest.dptech.com"));
            
            Map<String, Object> defaultContext = new HashMap<String, Object>();
            defaultContext.put("templateCode", MapUtil.getStr(config, "mailTemplateCode", "pm.dispatch.settlement.invoice.to.fp.mail"));
            defaultContext.put("tos", MapUtil.getStr(config, "fpMailAddress", "ssetest.dptech.com"));
            defaultContext.put("beforeSplit", MapUtil.getStr(config, "beforeSplit", "${"));
            defaultContext.put("afterSplit", MapUtil.getStr(config, "afterSplit", "}"));
            
            Integer invoiceFileType = MapUtil.getInt(config, "invoiceFileType", 9);
            List<String> errorList = new ArrayList<String>();
            list.parallelStream().forEachOrdered(settlement-> {
                try {
                    String invoiceFileIds = (String) settlement.getCustomInfoByKey("invoiceFileIds", "");
                    if (StringUtils.isBlank(invoiceFileIds)) {
                        return;
                    }
                    List<FileInfo> deliverList = uploaderService.selectFileInfoByIdsAndType(Arrays.asList(StringUtils.split(invoiceFileIds, ",")), invoiceFileType);
                    List<String> attachFiles = new ArrayList<>(deliverList.size());
                    for (FileInfo deliver : deliverList) {
                        attachFiles.add(StringUtils.join(new Object[] {deliver.getPath(), deliver.getName() }, ","));
                    }
                    if (!attachFiles.isEmpty()) {
                        Map<String, Object> context = new HashMap<String, Object>(defaultContext);
                        context.put("attachFiles", StringUtils.join(attachFiles, "&&"));
                        context.put("dataSource", new Object[] { settlement });
                        MailUtil.keepMailWithTemplate(context, true);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorList.add(String.format("结算ID：%s，编号：%s，返回错误信息：%s", settlement.getId(), settlement.getSettleSeq(), ExceptionUtils.getStackTrace(e)));
                }
            });
            
            log.info("{}-发送安服发票数据至FP邮箱总共{}条，成功{}条，失败{}条", className, list.size(), successCount.intValue(), errorList.size());
            syncLog.setException(StringUtils.join(errorList, "\r\n"));
            syncLog.setDataCount(successCount.intValue());
            syncLog.setIsSuccess(true);
        } catch (Exception e) {
            log.error("{}-发送安服发票数据至FP邮箱-发生异常：{}", className, e);
            syncLog.setException(ExceptionUtils.getStackTrace(e));
        } finally {
//            syncLogService.insertSelective(syncLog);
        }
        return syncLog;
	}

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        execute();
    }
	
}
