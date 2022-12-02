package com.dp.plat.pms.springmvc.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.schedule.SyncType;
import com.dp.plat.core.schedule.SynchronizeJob;
import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.PurchaseReceiptSettlement;

/**
 * 全量更新Job
 * 
 * @author w02611
 *
 */
public class D365DataJob extends SynchronizeJob {
    
    @Autowired
    private IPmSynchronizeService pmSynchronizeService;

    @Override
	@Autowired
	@Qualifier("pmSynchronizeService")
	public void setSynchronizeService(ISynchronizeService synchronizeService) {
	    this.synchronizeService = synchronizeService;
	}

	public D365DataJob() {
        super(SyncType.FULL_SYNC, new Class<?>[] {
            Facilitator.class,
            PurchaseReceiptSettlement.class
        }, "D365", "PMS");
    }
	
	

    @Override
    public void execute() {
        this.initApplicationContext("spring.xml");
        
        // 部分同步需要的同步参数
        List<String> purchPoolIds = JSON.parseArray(SystemConfig.systemVariables.getOrDefault("pm.sync.purch.settlement.purchPoolIds", "[]"), String.class);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("purchPoolIds", purchPoolIds);
        
        super.execute(params);
        
        // 插入/更新供应商信息
        pmSynchronizeService.insertOrUpdateFacilitatorFromD365();
        // 更新项目转包的付款信息
        pmSynchronizeService.updateDispatchAndSubcontractPaymentFromD365(params);
    }

    public static void main(String[] args) {
        D365DataJob job = new D365DataJob();
        job.execute();
	}
}
