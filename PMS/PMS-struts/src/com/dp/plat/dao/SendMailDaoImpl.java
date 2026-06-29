package com.dp.plat.dao;

import java.util.List;

import com.dp.plat.data.bean.MailSenderInfo;

public class SendMailDaoImpl extends BaseDao implements SendMailDao{

	@Override
	public void keepMailInfo(MailSenderInfo mailSenderInfo) {
		getSqlMapClientTemplate().insert("insert_into_sys_mails", mailSenderInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MailSenderInfo> gainMailInfoList() {
		return getSqlMapClientTemplate().queryForList("query_sys_mails");
	}

	@Override
	public void updateMailInfo(MailSenderInfo mailSenderInfo) {
		getSqlMapClientTemplate().update("update_sys_mails_state", mailSenderInfo);
	}

}
