package com.dp.plat.service;

import java.util.Date;

import com.dp.plat.dao.SendMailDao;
import com.dp.plat.data.bean.MailSenderInfo;

/**
 * 提供发送邮件服务
 * @author admin
 *
 */
public class SendMailServiceImpl extends BaseServiceImpl implements SendMailService{
	
	private SendMailDao sendMailDao;
	public SendMailDao getSendMailDao() {
		return sendMailDao;
	}
	public void setSendMailDao(SendMailDao sendMailDao) {
		this.sendMailDao = sendMailDao;
	}
	
	/**
	 *  调用接口，将邮件内容写入数据表
	 */
	@Override
	public void keepMailInfo(MailSenderInfo info) {
		if(info.getMailExpectSendTime() == null){
			info.setMailExpectSendTime(new Date());
		}
		if(info.getMailServerPort() == null){
			info.setMailServerPort("25");
		}
		if(info.getMailServerHost() == null){
			info.setMailServerHost("172.153.254.12");
		}
		if(info.getUserName() == null){
			info.setUserName("pms@dptech.com");
		}
		if(info.getPassword() == null){
			info.setPassword("2Bk29UamZr");
		}
		if(info.getFromAddress() == null){
			info.setFromAddress("pms@dptech.com");
		}
		sendMailDao.keepMailInfo(info);
	}
}
