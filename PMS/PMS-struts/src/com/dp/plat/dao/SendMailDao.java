package com.dp.plat.dao;

import java.util.List;

import com.dp.plat.data.bean.MailSenderInfo;

public interface SendMailDao {
	/**
	 * 保存邮件内容
	 * @param mailSenderInfo
	 */
	void keepMailInfo(MailSenderInfo mailSenderInfo);
	/**
	 * 检查数据表中是否有尚待发送的邮件	 
	 * @return
	 */
	List<MailSenderInfo> gainMailInfoList();
	/**
	 * 更新邮件发送状态及时间
	 * @param mailSenderInfo
	 */
	void updateMailInfo(MailSenderInfo mailSenderInfo);
}
