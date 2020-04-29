package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dp.plat.param.PrjProperty;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 从销售管理系统同步项目相关信息和销售信息
 * 
 * @author admin
 *
 */
public class GainPrjPropertyBySMS {

	@SuppressWarnings("unchecked")
	public static synchronized void work() throws IOException, SQLException {
		Reader readerSap = Resources.getResourceAsReader("sqlMapConfigSMS.xml");
		SqlMapClient sqlMapSap = SqlMapClientBuilder.buildSqlMapClient(readerSap);

		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 判断表字段有无增加
		paramMap.put("dbname", "dpspms");
		paramMap.put("tablename", "pm_project_property_from_sms");
		paramMap.put("fieldname", "serviceTypeName");
		int fields = (Integer) sqlMap.queryForObject("describe_table_field", paramMap);
		if (fields == 0) {
			paramMap.put("fieldtype", "varchar(10)");
			sqlMap.update("add_field", paramMap);
		}
		paramMap.put("fieldname", "channelName");
		fields = (Integer) sqlMap.queryForObject("describe_table_field", paramMap);
		if (fields == 0) {
			paramMap.put("fieldtype", "varchar(255)");
			sqlMap.update("add_field", paramMap);
		}
		// 开始同步，增加日志
		paramMap.put("refreshTaskName", GainPrjPropertyBySMS.class.toString());
		paramMap.put("handleUser", "system");
		paramMap.put("dataFrom", "SMS");
		paramMap.put("dataTo", null);
		paramMap.put("refreshFrom", new Date());
		Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);
		try {
			List<PrjProperty> prjProperties = sqlMapSap.queryForList("query_v_prj_property_4_pm");
			sqlMap.startTransaction();
			sqlMap.delete("delete_pm_project_property_from_sms");
			List<PrjProperty> list = new ArrayList<PrjProperty>();
			int i = 0;
			for (PrjProperty pp : prjProperties) {
				pp.setOrderExecNumber(pp.getOrderExecNumber().replace("J", "X"));
				if (i < 2000) {
					list.add(pp);
					i++;
				} else {
					paramMap.put("list", list);
					sqlMap.insert("insert_pm_project_property_from_sms", paramMap);
					i = 0;
					list = new ArrayList<PrjProperty>();
					list.add(pp);
				}
			}
			paramMap.put("list", list);
			sqlMap.insert("insert_pm_project_property_from_sms", paramMap);

			sqlMap.commitTransaction();
			sqlMap.endTransaction();
			
			// 总代借货及利润中心
            List<Map<String, Object>> soleagentLends = sqlMapSap.queryForList("query_v_soleagent_lend_4_pms");
            sqlMap.startTransaction();
            sqlMap.delete("delete_pm_project_soleagent_lend_from_sms");
            List<Map<String, Object>> soleagentLendList = new ArrayList<Map<String, Object>>();
            int j = 0;
            for (Map<String, Object> pp : soleagentLends) {
                soleagentLendList.add(pp);
                j++;
                if (j >= 2000) {
                    paramMap.put("list", soleagentLendList);
                    sqlMap.insert("insert_pm_project_soleagent_lend_from_sms", paramMap);
                    j = 0;
                    soleagentLendList.clear();
                }
            }
            if (!soleagentLendList.isEmpty()) {
                paramMap.put("list", soleagentLendList);
                sqlMap.insert("insert_pm_project_soleagent_lend_from_sms", paramMap);
            }

            sqlMap.commitTransaction();
            sqlMap.endTransaction();

			// //SMS项目责任人转移后更新新的销售
			// // 存在问题，进行调整
			// sqlMap.insert("update_pm_salesmember_info");
//			sqlMap.startTransaction();
//			sqlMap.insert("create_temp_project_sales_change");
//			sqlMap.update("invalid_project_invalid_sales");
//			sqlMap.update("insert_changed_project_sales");
//			sqlMap.insert("drop_temp_project_sales_change");
//			sqlMap.commitTransaction();
//			sqlMap.endTransaction();

			// SMS改单后实施方式、渠道信息发生变化进行更新
			sqlMap.insert("create_temp_max_ppfsId");
			sqlMap.insert("create_temp_max_prpId");
			sqlMap.insert("create_temp_max_ppfs");
			sqlMap.insert("create_temp_not_ppfs");
			sqlMap.insert("create_temporary_serviceType_and_channelName_table");

			sqlMap.startTransaction();
			sqlMap.update("update_project_serviceType");
			sqlMap.update("update_project_channelName");
			sqlMap.update("update_project_compId");
			sqlMap.update("update_project_customProjectName");
			sqlMap.commitTransaction();
			sqlMap.endTransaction();

			// SMS项目责任人转移后更新新的销售
			sqlMap.insert("create_temp_project_sales_change");
			sqlMap.update("invalid_project_invalid_sales");
			sqlMap.update("insert_changed_project_sales");
			sqlMap.insert("drop_temp_project_sales_change");

			sqlMap.delete("drop_temp_max_ppfsId");
			sqlMap.delete("drop_temp_max_prpId");
			sqlMap.delete("drop_temp_max_ppfs");
			sqlMap.delete("drop_temp_not_ppfs");
			sqlMap.delete("drop_temporary_serviceType_and_channelName_table");
			
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
		GainPrjPropertyBySMS.work();
	}
}
