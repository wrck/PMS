//package com.dp.plat.core.dao;
//
//import java.util.List;
//
//import com.dp.plat.core.pojo.MailInfo;
//import com.dp.plat.support.mail.MailSenderInfo;
//import com.dp.plat.support.mail.NotificationTemplate;
//
//public interface MailInfoMapper extends AbstractBaseMapper<MailInfo> {
//
//	int deleteByPrimaryKey(Integer id);
//
//	int insert(MailInfo record);
//
//	int insertSelective(MailInfo record);
//
//	MailInfo selectByPrimaryKey(Integer id);
//
//	int updateByPrimaryKeySelective(MailInfo record);
//
//	int updateByPrimaryKeyWithBLOBs(MailInfo record);
//
//	int updateByPrimaryKey(MailInfo record);
//
//	/**
//	 * 查询邮件模板
//	 * 
//	 * @param templateCode
//	 * @return NotificationTemplate
//	 */
//	NotificationTemplate queryNotificationTemplate(String templateCode);
//
//	/**
//	 * 查找未发送邮件,预期发送时间小于当前时间
//	 * 
//	 * @return
//	 */
//	List<MailSenderInfo> queryUnSendMails();
//
//	/**
//	 * 更新发送成功的邮件状态
//	 * 
//	 * @param mailIds
//	 */
//	void updateMailWhenSendSuccess(String mailIds);
//
//	/**
//	 * 更新邮件实际发送地址和发送状态
//	 * 
//	 * @param successMails
//	 */
//	void updateMailInfoWhenSendSuccess(List<MailInfo> successMails);
//	
//	/**
//	 * 更新邮件实际发送地址和发送状态
//	 * @param successMail
//	 */
//	void updateOneMailInfoWhenSendSuccess(MailInfo successMail);
//
//}