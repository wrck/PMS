package com.dp.plat.support.mail.dao;

import java.util.List;

import com.dp.plat.support.mail.MailSenderInfo;
import com.dp.plat.support.mail.NotificationTemplate;
import com.dp.plat.support.mail.entity.MailInfo;
import com.dp.plat.support.mail.vo.PageParam;

public interface MailInfoMapper {

	int deleteByPrimaryKey(Object id);

	int insert(MailInfo record);

	int insertSelective(MailInfo record);

	MailInfo selectByPrimaryKey(Object id);

	int updateByPrimaryKeySelective(MailInfo record);

	int updateByPrimaryKey(MailInfo record);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	long countBySelectivePageable(PageParam<MailInfo> pageParam);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param t
	 * @return
	 */
	long countBySelective(MailInfo t);

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	List<MailInfo> selectBySelectivePageable(PageParam<MailInfo> pageParam);

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param t
	 * @return
	 */
	List<MailInfo> selectBySelective(MailInfo t);

	/**
	 * 查询邮件模板
	 * 
	 * @param templateCode
	 * @return NotificationTemplate
	 */
	NotificationTemplate queryNotificationTemplate(String templateCode);

	/**
	 * 查找未发送邮件,预期发送时间小于当前时间
	 * 
	 * @return
	 */
	List<MailSenderInfo> queryUnSendMails();

	/**
	 * 查找未发送邮件,预期发送时间小于当前时间，并且发送失败次数小于指定次数
	 * 
	 * @param failedCount
	 * @return
	 */
	List<MailSenderInfo> queryUnSendMails(Integer failedCount);

	/**
	 * 更新发送成功的邮件状态
	 * 
	 * @param mailIds
	 */
	void updateMailWhenSendSuccess(String mailIds);

	/**
	 * 更新邮件实际发送地址和发送状态
	 * 
	 * @param successMails
	 */
	void updateMailInfoWhenSendSuccess(List<MailInfo> successMails);

	/**
	 * 更新邮件实际发送地址和发送状态
	 * 
	 * @param successMail
	 */
	void updateOneMailInfoWhenSendSuccess(MailInfo successMail);

	/**
	 * 更新邮件发送失败次数
	 * 
	 * @param failedMailIds
	 */
	void updateMailFailedCount(String failedMailIds);

}