package com.dp.plat.extend.crm.job;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionException;

import com.dp.plat.crm.model.Response;
import com.dp.plat.crm.util.CrmApi;

import cn.hutool.core.map.MapUtil;

/**
 * 推送发票信息给CRM
 * 
 * @author w02611
 *
 */
public class PushJobToCRM extends DefaultPushTaskFormCRM<Map<String, Object>> implements Job {

    public PushJobToCRM() {
		super("applicationContext.xml", "sqlMapConfig.xml");
		batchSize = 500;
	}

	public void work() throws IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		// 推送合同发货数据
//		pushData("ContractShimpentBarcodeToCRM", "合同发货数据", params);
		// 推送合同发货数据
        pushData("ContractCollectionPlanToCRM", "合同回款计划完成时间", params);
	}

	/**
	 * 刷新推送合同发货数据
	 */
	public boolean pushData(String dataName, String tag, Map<String, Object> params) {
	    if (params == null) {
            params = new HashMap<String, Object>();
        } else {
            params = new HashMap<String, Object>(params);
        }
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
//	    // FIXME TEST
//	    List<Map<String, Object>> collect = list.stream().limit(batchSize).collect(Collectors.toList());
//	    list.clear();
//	    list.addAll(collect);
	    
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
	    Long offset = MapUtil.getLong(params, "offset", 0L);
	    params.put("offset", offset + list.size());
	    log.info("{}-推送CRM数据开始{}条，位移量{}", getTag(dataName), list.size(), offset);
        Response<Map<String, Object>> response = CrmApi.pushRecord(getRouterName(dataName), list);
        if (response.isSuccess()) {
            log.info("{}-推送CRM数据成功{}条，位移量{}", getTag(dataName), list.size(), offset);
        } else {
            log.error("{}-推送CRM数据，位移量{}-发生异常：{}", getTag(dataName), offset, response.getMessage());
        }
    }

    public static void main(String[] args) {
		try {
			new PushJobToCRM().execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
