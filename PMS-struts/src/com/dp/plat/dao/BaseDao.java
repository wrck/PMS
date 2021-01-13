package com.dp.plat.dao;

import java.text.MessageFormat;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.dp.plat.context.UserContext;

public class BaseDao {
	public static int OP_SUCCESS = OpLogDaoImpl.SUCCESS;
	public static int OP_FAIL = OpLogDaoImpl.FAIL;

	private String errmsg;
	private OpLogDao logger;
	private SqlMapClientTemplate sqlMapClientTemplate;
	private SqlMapClientTemplate sqlMapClientTemplateSAP;

	public SqlMapClientTemplate getSqlMapClientTemplateSAP() {
		return sqlMapClientTemplateSAP;
	}

	public void setSqlMapClientTemplateSAP(SqlMapClientTemplate sqlMapClientTemplateSAP) {
		this.sqlMapClientTemplateSAP = sqlMapClientTemplateSAP;
	}

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	public SqlMapClientTemplate getSqlMapClientTemplate() {
		return sqlMapClientTemplate;
	}

	public OpLogDao getOpLoggerDao() {
		return logger;
	}

	public void setOpLoggerDao(OpLogDao logger) {
		this.logger = logger;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	protected void setErrmsg(String errmsg, String... errparams) {
		MessageFormat.format(errmsg.substring(1), (Object[]) errparams);
	}

	public String getCurrUsername() {
		try {
			return UserContext.getUserContext().getUsername();
		} catch (Exception e) {
			return null;
		}
	}
}
