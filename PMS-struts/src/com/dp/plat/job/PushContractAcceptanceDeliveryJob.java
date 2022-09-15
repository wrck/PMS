package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dp.plat.context.SpringContext;
import com.dp.plat.pms.extend.d365.util.D365Api;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.subcontract.service.SubcontractService;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 从销售管理系统同步市场部维度基础数据
 * 
 * @author admin
 *
 */
public class PushContractAcceptanceDeliveryJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            work();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

	@SuppressWarnings("unchecked")
	public static synchronized void work() throws IOException, SQLException {
	    ApplicationContext ctx = SpringContext.getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        BasicDataService basicDataService = ctx.getBean("basicDataService", BasicDataService.class);
        // 获取项目转包推采购订单的配置项
        String configStr = basicDataService.querySysArg("pm.project.subcontract.pushPurchaseOrder.config");
        configStr = StringUtils.defaultIfBlank(configStr, "{}");
        Map<String, Object> config = JSON.parseObject(configStr, new TypeReference<HashMap<String, Object>>() {});
        boolean enablePushPurchaseOrder = Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(config.get("enablePushPurchaseOrder"))));
        if (!enablePushPurchaseOrder) {
            return;
        }
        
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 开始同步，增加日志
		paramMap.put("refreshTaskName", PushContractAcceptanceDeliveryJob.class.toString());
		paramMap.put("handleUser", "system");
		paramMap.put("dataFrom", "PMS");
		paramMap.put("dataTo", "D365");
		paramMap.put("refreshFrom", new Date());
		Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);
		try {
		    // 查询7天内发生变化的交付件信息
		    paramMap.put("dateDiff", 90);
		    ProjectService projectService = SpringContext.getBean("projectService", ProjectService.class);
			List<Map<String, Object>> list = projectService.selectContractAcceptanceDeliveryInfo(paramMap);
			// 按账套，合同分组
			Map<String, Map<String, List<Map<String, Object>>>> dataContractMap = new HashMap<String, Map<String, List<Map<String,Object>>>>();
			for (Map<String, Object> line : list) {
			    String contractNo = (String) line.get("contractNo");
			    String dataAreaId = (String) line.get("dataAreaId");
			    Map<String, List<Map<String, Object>>> contractMap = dataContractMap.getOrDefault(dataAreaId, new HashMap<String, List<Map<String,Object>>>());
			    List<Map<String, Object>> lines = contractMap.getOrDefault(line.get("contractNo"), new ArrayList<Map<String,Object>>());
			    lines.add(line);
			    contractMap.put(contractNo, lines);
			    dataContractMap.put(dataAreaId, contractMap);
            }
			
			for (Entry<String, Map<String, List<Map<String, Object>>>> dataMap : dataContractMap.entrySet()) {
			    String dataAreaId = dataMap.getKey();
			    Map<String, List<Map<String, Object>>> contractMap = dataMap.getValue();
			    for (Entry<String, List<Map<String, Object>>> linesMap : contractMap.entrySet()) {
			        String contractNo = linesMap.getKey();
			        List<Map<String, Object>> lines = linesMap.getValue();
			        D365Api.pushContractAcceptanceDeliveryInfo(dataAreaId, contractNo, lines, config);
                }
            }
			
			// 更新成功日志
			paramMap.put("id", Integer.parseInt(obj.toString()));
			paramMap.put("refreshTo", new Date());
			paramMap.put("refreshState", 1);
			sqlMap.update("update_fnd_data_refresh_log_success", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
            if (sqlMap != null) {
                try {
                    sqlMap.getCurrentConnection().rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
			// 更新失败日志
			paramMap.put("refreshException", ExceptionUtils.getStackTrace(e));
			paramMap.put("id", Integer.parseInt(obj.toString()));
			sqlMap.update("update_fnd_data_refresh_log_fail", paramMap);
		} finally {
			sqlMap.endTransaction();
		}
	}

	/**
	 * test
	 * 
	 * @param arg
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void main(String[] arg) throws IOException, SQLException {
		work();
	}

}
