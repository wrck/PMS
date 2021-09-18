///**
// * 
// */
//package com.dp.plat.core.service.impl;
//
//import java.util.Date;
//import java.util.List;
//
//import org.apache.shiro.SecurityUtils;
//import org.springframework.stereotype.Service;
//
//import com.dp.plat.core.dao.MailInfoMapper;
//import com.dp.plat.core.pojo.MailInfo;
//import com.dp.plat.core.realms.Principal;
//import com.dp.plat.core.service.IMailInfoService;
//import com.dp.plat.support.mail.MailSenderInfo;
//import com.dp.plat.support.mail.NotificationTemplate;
//
///**
// * @author w02611
// *
// */
//@Service("mailInfoService")
//public class MailInfoService extends AbstractBaseService<MailInfoMapper, MailInfo> implements IMailInfoService {
//
//	@Override
//	public int insertSelective(MailInfo record) {
//		if (record.getExpectSendTime() == null) {
//			record.setExpectSendTime(new Date());
//		}
//		try {
//			Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
//			if (principal != null) {
//				record.setCreateBy(principal.getUserName());
//			}
//		} catch (Exception e) {
//			record.setCreateBy("system");
//		}
//		record.setCreateTime(new Date());
//		return super.insertSelective(record);
//	}
//
//	@Override
//	public NotificationTemplate queryNotificationTemplate(String templateCode) {
//		return dao.queryNotificationTemplate(templateCode);
//	}
//
//	@Override
//	public List<MailSenderInfo> queryUnSendMails() {
//		return dao.queryUnSendMails();
//	}
//
//	@Override
//	public void updateMailWhenSendSuccess(String mailIds) {
//		dao.updateMailWhenSendSuccess(mailIds);
//	}
//
//	@Override
//	public void updateMailInfoWhenSendSuccess(List<MailInfo> successMails) {
//		dao.updateMailInfoWhenSendSuccess(successMails);
//	}
//
//	@Override
//	public void updateOneMailInfoWhenSendSuccess(MailInfo successMail) {
//		dao.updateOneMailInfoWhenSendSuccess(successMail);
//	}
//}
