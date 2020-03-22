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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dp.plat.param.Person;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class GainPersonByOA implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			this.work();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void work() throws IOException, SQLException{
		Reader readerSap = Resources.getResourceAsReader("sqlMapConfigOA.xml");
		SqlMapClient sqlMapSap=SqlMapClientBuilder.buildSqlMapClient(readerSap);
		
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap=SqlMapClientBuilder.buildSqlMapClient(reader);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//开始同步，增加日志
		paramMap.put("refreshTaskName", GainPersonByOA.class.toString());
		paramMap.put("handleUser", "system");
		paramMap.put("dataFrom", "OA");
		paramMap.put("dataTo", null);
		paramMap.put("refreshFrom", new Date());
		Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);
		try {
			sqlMap.startTransaction();
			List<Person> persons = sqlMapSap.queryForList("query_view_person_info_4_pms" ,1);//有效员工
			sqlMap.delete("delete_pm_person_from_oa");
			
			paramMap.put("list", persons);
			sqlMap.insert("insert_pm_person_from_oa", paramMap);
			sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
//			//由于之前柯善辉的PMS账号申请的时候输入有误，导致，PMS账号与工号不一致，在此修改一下
//			sqlMap.update("update_keshanhui_from_oa");
			List<Person> invalidPersons = sqlMapSap.queryForList("query_view_person_info_4_pms" , 0);//失效员工
//			// 逐条更新太慢，改为临时表批量关联更新
//			sqlMap.startTransaction();
//			sqlMap.startBatch();
//			for(Person person : invalidPersons){
//				sqlMap.update("update_project_member", person.getSalesmanCode());
//			}
//			sqlMap.executeBatch();
//			sqlMap.commitTransaction();
//			sqlMap.endTransaction();
			
            sqlMap.insert("createInvalidPersonsTempTable");
            List<Person> tempList = new ArrayList<Person>();
            int m = 0;
            sqlMap.startTransaction();
            for (Person person : invalidPersons) {
                m++;
                tempList.add(person);
                if (m % 1000 == 0) {
                    paramMap.put("list", tempList);
                    sqlMap.insert("insert_pm_person_from_oa_temp", paramMap);
                    tempList.clear();
                }
            }
            if (tempList.isEmpty()) {
                paramMap.put("list", tempList);
                sqlMap.insert("insert_pm_person_from_oa_temp", paramMap);
                tempList.clear();
            }
            sqlMap.commitTransaction();
            sqlMap.endTransaction();
            // 失效项目关联的离职销售
            sqlMap.update("invalidQuitProjectQuitSalesMan");
            
            sqlMap.delete("dropInvalidPersonsTempTable");
			
			//更新成功日志
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
			//更新失败日志
			paramMap.put("refreshException", ExceptionUtils.getStackTrace(e));
			paramMap.put("id", Integer.parseInt(obj.toString()));
			sqlMap.update("update_fnd_data_refresh_log_fail", paramMap);
		}finally{
			sqlMap.endTransaction();
		}
	}
	
	public static void main(String[] arg) throws IOException, SQLException {
		new GainPersonByOA().work();
	} 
}
