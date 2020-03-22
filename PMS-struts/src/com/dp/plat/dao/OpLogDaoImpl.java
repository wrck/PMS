package com.dp.plat.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.OperateLog;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.LoginAddParam;
import com.dp.plat.type.DateTime;
import com.ibatis.sqlmap.client.SqlMapExecutor;

public class OpLogDaoImpl implements OpLogDao
{
	public static int SUCCESS = 1;
	public static int FAIL = 2;
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public SqlMapClientTemplate getSqlMapClientTemplate() {
		return sqlMapClientTemplate;
	}
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}
	public void insertLog()
	{
	    try {
	        LoginAddParam param = new LoginAddParam();
    		UserContext userContext = (UserContext) SpringContext.getBean("userContext");
    		param.setUsername(userContext.getUser().getUsername());
    		param.setManagerid(userContext.getUser().getId());
    		param.setIp(userContext.getIp());
    		param.setInfo(userContext.getOption());
    		param.setTime(new DateTime(new Date().getTime() / 1000).longValue());
    		sqlMapClientTemplate.insert("insert-Operation-Log", param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@SuppressWarnings("unchecked")
	public List<OperateLog> queryLogList(DisplayParam displayParam)
	{
		displayParam.setPagesize(50);
		displayParam.setOffset((displayParam.getCurrentpage() - 1)
				* displayParam.getPagesize());
		Integer totalcount = (Integer) getSqlMapClientTemplate()
				.queryForObject("select-Operation-Log-Sum");
		displayParam.setTotalcount(totalcount);
		return (List<OperateLog>) getSqlMapClientTemplate().queryForList(
				"select-Operation-Log", displayParam);
	}
	
	@Override
	public void delete(final ArrayList<String> selected) throws DataAccessException
	{
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback<Object>()
		{
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException
			{
				executor.startBatch();
				int batch = 0;
				for (String value : selected)
				{
					getSqlMapClientTemplate()
							.delete("delete-Log-List", value);
					batch++;
					if (batch == 500)
					{
						executor.executeBatch();
						batch = 0;
					}
				}
				executor.executeBatch();
				return null;
			}
		});
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<com.dp.plat.data.OperateLog> queryLogAllList(DisplayParam displayParam) {
		displayParam.setPagesize(50);
		displayParam.setOffset((displayParam.getCurrentpage() - 1)
				* displayParam.getPagesize());
		Integer totalcount = (Integer) getSqlMapClientTemplate()
				.queryForObject("select-Operation-Log-Sum");
		displayParam.setTotalcount(totalcount);
		return getSqlMapClientTemplate().queryForList("select-all-log",displayParam);
	}
}
