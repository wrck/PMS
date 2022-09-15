package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class UpdateShipmentState{


	public static void work() throws Exception {
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap=SqlMapClientBuilder.buildSqlMapClient(reader);
		//创建临时表，得出项目发货状态
		sqlMap.update("create_shipment_state_tmp");
		//表链接更新发货状态
		sqlMap.update("update_shipment_state");
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
