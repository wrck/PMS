package com.dp.plat.service;

import com.dp.plat.data.bean.MailSenderInfo;

public interface SendMailService extends BaseService{
	void keepMailInfo(MailSenderInfo mailSenderInfo);
}
