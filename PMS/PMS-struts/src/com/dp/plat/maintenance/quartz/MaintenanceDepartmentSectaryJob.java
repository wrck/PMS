package com.dp.plat.maintenance.quartz;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * SSE部门秘书同步
 * 
 * @author admin
 */
public class MaintenanceDepartmentSectaryJob implements Job {

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
        	System.out.println("###############项目维护同步办事处秘书开始################");
            Reader readerSSE = Resources.getResourceAsReader("sqlMapConfigSSE.xml");
            SqlMapClient sqlMapSSE = SqlMapClientBuilder.buildSqlMapClient(readerSSE);
            List<Map<String, Object>> list = sqlMapSSE.queryForList("querySSEDepartmentSectaryList");
            readerSSE.close();
            
            Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
            SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            
            sqlMap.delete("deleteSSEDepartmentSectary");
            sqlMap.insert("insertSSEDepartmentSectary", list);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
		} finally {
			System.out.println("###############项目维护同步办事处秘书结束################");
		}
    }
    
    public static void main(String[] args) throws JobExecutionException {
        new MaintenanceDepartmentSectaryJob().execute(null);
    }
}
