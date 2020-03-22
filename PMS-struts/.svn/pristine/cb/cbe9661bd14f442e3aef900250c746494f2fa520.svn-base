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

import com.dp.plat.param.RealProductLineBean;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 从销售管理系统同步项目真实设备清单
 * 
 * @author admin
 *
 */
public class GainPrjRealProjectLineBySMS {

	@SuppressWarnings("unchecked")
	public static synchronized void work() throws IOException, SQLException {
		Reader readerSms = Resources.getResourceAsReader("sqlMapConfigSMS.xml");
		SqlMapClient sqlMapSms = SqlMapClientBuilder.buildSqlMapClient(readerSms);

		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 开始同步，增加日志
		paramMap.put("refreshTaskName", GainPrjRealProjectLineBySMS.class.toString());
		paramMap.put("handleUser", "system");
		paramMap.put("dataFrom", "SMS");
		paramMap.put("dataTo", null);
		paramMap.put("refreshFrom", new Date());
		Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);
		try {
			List<RealProductLineBean> prjRealProducts = sqlMapSms.queryForList("query_view_refer_product");
			sqlMap.startTransaction();
			sqlMap.delete("delete_pm_project_real_product_line_from_sms");
			List<RealProductLineBean> list = new ArrayList<RealProductLineBean>();
			int i = 0;
			for (RealProductLineBean pp : prjRealProducts) {
				pp.setOrderExecNumber(pp.getOrderExecNumber().replace("J", "X"));
				if (i < 2000) {
					list.add(pp);
					i++;
				} else {
					paramMap.put("list", list);
					sqlMap.insert("insert_pm_project_real_product_line_from_sms", paramMap);
					i = 0;
					list = new ArrayList<RealProductLineBean>();
					list.add(pp);
				}
			}
			
			paramMap.put("list", list);
			sqlMap.insert("insert_pm_project_real_product_line_from_sms", paramMap);
			sqlMap.commitTransaction();
			sqlMap.endTransaction();
			
			sqlMap.startTransaction();
			sqlMap.delete("delete_pm_project_product_line_real");
			sqlMap.insert("insert_pm_project_product_line_real");
			sqlMap.commitTransaction();
			sqlMap.endTransaction();
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
