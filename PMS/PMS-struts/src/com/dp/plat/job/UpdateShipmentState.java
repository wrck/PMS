package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class UpdateShipmentState{


	public static void work() throws Exception {
	    try {
    	    Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
    	    SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
    	    Map<String, Object> logParams = new HashMap<String, Object>();
    	    //开始同步，增加日志
    	    logParams.put("refreshTaskName", UpdateShipmentState.class.toString());
    	    logParams.put("handleUser", "system");
    	    logParams.put("dataFrom", "Local");
    	    logParams.put("dataTo", "Local");
    	    logParams.put("dataName", "UpdateShipmentState");
    	    logParams.put("refreshFrom", new Date());
    	    Object obj = sqlMap.insert("insert_fnd_data_refresh_log", logParams);
    	    logParams.put("id", obj);
    	    try {
        		//创建临时表，得出项目发货状态
        		sqlMap.update("create_shipment_state_tmp");
        		//表链接更新发货状态
        		sqlMap.update("update_shipment_state");
        		
        		logParams.put("refreshState", 1);
    	    } catch (Exception e) {
    	        //更新失败日志
    	        logParams.put("refreshException", ExceptionUtils.getStackTrace(e));
            }
    	    sqlMap.update("update_fnd_data_refresh_log", logParams);
	    } catch (Exception e) {
            e.printStackTrace();
        }
		
		ProjectSoftVersionInitJob softVersionInitJob = new ProjectSoftVersionInitJob();
		softVersionInitJob.work();
		
		GainDataFromLicense dataFromLicense = new GainDataFromLicense();
        dataFromLicense.work();
	}

	public static void main(String[] args) throws IOException, SQLException {
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap=SqlMapClientBuilder.buildSqlMapClient(reader);
		
		//创建临时表，得出项目发货状态
		sqlMap.update("create_shipment_state_tmp");
		//表链接更新发货状态
		sqlMap.update("update_shipment_state");
	}	
}
