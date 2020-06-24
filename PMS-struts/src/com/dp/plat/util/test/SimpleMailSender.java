package com.dp.plat.util.test;

import javax.mail.Address;
import org.apache.commons.lang3.StringUtils;

import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.util.MailUtil;

/**
 * 简单邮件（不带附件的邮件）发送器
 */
public class SimpleMailSender {
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) {
		return MailUtil.sendTextMail(mailInfo);
//		// 判断是否需要身份认证
//		MyAuthenticator authenticator = null;
//		Properties pro = mailInfo.getProperties();
//		if (mailInfo.isValidate()) {
//			// 如果需要身份认证，则创建一个密码验证器
//			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
//		}
//		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
//		Session sendMailSession = Session.getInstance(pro, authenticator);
//		try {
//			// 根据session创建一个邮件消息
//			Message mailMessage = new MimeMessage(sendMailSession);
//			// 创建邮件发送者地址
//			Address from = new InternetAddress(mailInfo.getFromAddress(),
//					MimeUtility.encodeText(StringEscUtil.getText("sys.title")));
//			// 设置邮件消息的发送者
//			mailMessage.setFrom(from);
//			// 创建邮件的接收者地址，并设置到邮件消息中
//			Address to = new InternetAddress(mailInfo.getToAddress());
//			mailMessage.setRecipient(Message.RecipientType.TO, to);
//			// 设置邮件消息的主题
//			mailMessage.setSubject(mailInfo.getSubject());
//			// 设置邮件消息发送的时间
//			mailMessage.setSentDate(new Date());
//			// 设置邮件消息的主要内容
//			String mailContent = mailInfo.getContent();
//			mailMessage.setText(mailContent);
//			// 发送邮件
//			Transport.send(mailMessage);
//			return true;
//		} catch (SendFailedException e) {
//			e.printStackTrace();
//			return removeInvalidAddressAndResend(mailInfo, e.getInvalidAddresses());
//		} catch (MessagingException | UnsupportedEncodingException ex) {
//			ex.printStackTrace();
//		}
//		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		return MailUtil.sendHtmlMail(mailInfo);
//		// 判断是否需要身份认证
//		MyAuthenticator authenticator = null;
//		Properties pro = mailInfo.getProperties();
//		// 如果需要身份认证，则创建一个密码验证器
//		if (mailInfo.isValidate()) {
//			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
//		}
//		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
//		Session sendMailSession = Session.getInstance(pro, authenticator);
//		try {
//			// 根据session创建一个邮件消息
//			Message mailMessage = new MimeMessage(sendMailSession);
//			// 创建邮件发送者地址
//			Address from = new InternetAddress(mailInfo.getFromAddress(),
//					MimeUtility.encodeText(StringEscUtil.getText("sys.title")));
//			// 设置邮件消息的发送者
//			mailMessage.setFrom(from);
//			// 创建邮件的接收者地址，并设置到邮件消息中
//			Address to = new InternetAddress(mailInfo.getToAddress());
//			// Message.RecipientType.TO属性表示接收者的类型为TO
//			mailMessage.setRecipient(Message.RecipientType.TO, to);
//			// 设置邮件消息的主题
//			mailMessage.setSubject(mailInfo.getSubject());
//			// 设置邮件消息发送的时间
//			mailMessage.setSentDate(new Date());
//			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
//			Multipart mainPart = new MimeMultipart();
//			// 创建一个包含HTML内容的MimeBodyPart
//			BodyPart html = new MimeBodyPart();
//			// 设置HTML内容
//			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
//			mainPart.addBodyPart(html);
//			// 将MiniMultipart对象设置为邮件内容
//			mailMessage.setContent(mainPart);
//			// 发送邮件
//			Transport.send(mailMessage);
//			return true;
//		} catch (SendFailedException e) {
//			e.printStackTrace();
//			return removeInvalidAddressAndResend(mailInfo, e.getInvalidAddresses());
//		} catch (MessagingException | UnsupportedEncodingException ex) {
//			ex.printStackTrace();
//		}
//		return false;
	}

	/**
	 * 带附件邮件发送方法
	 */
	@SuppressWarnings("static-access")
	public static boolean sendMail(MailSenderInfo mailInfo) {
		return MailUtil.sendMailWithAttachments(mailInfo);
//		Properties props = mailInfo.getProperties(mailInfo);
//		MyAuthenticator authenticator = null;
//		if (mailInfo.isValidate()) {
//			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
//		}
//		Session session = Session.getInstance(props, authenticator);
//
//		try {
//			MimeMessage mimeMsg = new MimeMessage(session);
//			Transport transport = session.getTransport("smtp");
//			Address from = new InternetAddress(mailInfo.getFromAddress(),
//					MimeUtility.encodeText(StringEscUtil.getText("sys.title")));
//			mimeMsg.setFrom(from);
//			// 主送
//			String[] sendtos = new String[] {};
//			if (mailInfo.getTos() != null) {
//				sendtos = splitStringByTrim(mailInfo.getTos(), ";");
//			}
//			if (sendtos.length > 0) {
//				InternetAddress[] tos = new InternetAddress[sendtos.length];
//				for (int i = 0; i < sendtos.length; i++) {
//					tos[i] = new InternetAddress(sendtos[i]);
//				}
//				mimeMsg.setRecipients(MimeMessage.RecipientType.TO, tos);
//			}
//			// 抄送
//			String[] cs = new String[] {};
//			if (mailInfo.getCcs() != null) {
//				cs = splitStringByTrim(mailInfo.getCcs(), ";");
//			}
//			if (cs.length > 0) {
//				InternetAddress[] ccs = new InternetAddress[cs.length];
//				for (int i = 0; i < cs.length; i++) {
//					ccs[i] = new InternetAddress(cs[i]);
//				}
//				mimeMsg.setRecipients(MimeMessage.RecipientType.CC, ccs);
//			}
//			// 增加密送
//			String[] bc = new String[] {};
//			if (mailInfo.getBcc() != null) {
//				bc = splitStringByTrim(mailInfo.getBcc(), ";");
//				// mailInfo.getBcc().split(";");
//			}
//			if (bc.length > 0) {
//				InternetAddress[] bcs = new InternetAddress[bc.length];
//				for (int i = 0; i < bc.length; i++) {
//					bcs[i] = new InternetAddress(bc[i]);
//				}
//				mimeMsg.setRecipients(MimeMessage.RecipientType.BCC, bcs);
//			}
//
//			mimeMsg.setSubject(mailInfo.getSubject(), "utf-8");
//			MimeBodyPart messageBodyPart1 = new MimeBodyPart();
//			messageBodyPart1.setContent(mailInfo.getContent(), "text/html;charset=utf-8");
//			Multipart multipart = new MimeMultipart();// 附件传输格式
//			multipart.addBodyPart(messageBodyPart1);
//			String[] fileNames = new String[] {};
//			if (StringUtils.isNotBlank(mailInfo.getAttachFileNames())) {
//				fileNames = mailInfo.getAttachFileNames().split("&&");
//			}
//			if (fileNames != null && fileNames.length > 0) {
//				for (int i = 0; i < fileNames.length; i++) {
//					MimeBodyPart messageBodyPart2 = new MimeBodyPart();
//					// 选择出每一个附件名
//					String filename = fileNames[i].split(",")[0];
//					System.out.println("附件名：" + filename);
//					String displayname = fileNames[i].split(",")[1];
//					// 得到数据源
//					FileDataSource fds = new FileDataSource(filename);
//					// 得到附件本身并至入BodyPart
//					messageBodyPart2.setDataHandler(new DataHandler(fds));
//					// 得到文件名同样至入BodyPart
//					messageBodyPart2.setFileName(MimeUtility.encodeText(displayname));
//					multipart.addBodyPart(messageBodyPart2);
//				}
//			}
//			mimeMsg.setContent(multipart);
//			// 设置信件头的发送日期
//			mimeMsg.setSentDate(new Date());
//
//			mimeMsg.saveChanges();
//			// 发送邮件
//			transport.send(mimeMsg);
//			transport.close();
//			return true;
//		} catch (SendFailedException e) {
//			e.printStackTrace();
//			return removeInvalidAddressAndResend(mailInfo, e.getInvalidAddresses());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//		return false;
	}

	/**
	 * 移除无效的地址并进行重发
	 * @param mailInfo
	 * @param invalidAddresses
	 * @return 重发时候成功
	 */
	private static boolean removeInvalidAddressAndResend(MailSenderInfo mailInfo, Address[] invalidAddresses) {
		boolean needResend = invalidAddresses != null && invalidAddresses.length > 0;
		if (!needResend) {
			return needResend;
		}
		if (StringUtils.isNotBlank(mailInfo.getTos())) {
			String tos = mailInfo.getTos();
			for (Address invalid : invalidAddresses) {
				tos = tos.replaceFirst(";?" + invalid.toString() + ";?", ";");
			}
			mailInfo.setTos(tos);
		}
		if (StringUtils.isNotBlank(mailInfo.getCcs())) {
			String ccs = mailInfo.getCcs();
			for (Address invalid : invalidAddresses) {
				ccs = ccs.replaceFirst(";?" + invalid.toString() + ";?", ";");
			}
			mailInfo.setCcs(ccs);
		}
		if (StringUtils.isNotBlank(mailInfo.getBcc())) {
			String bcc = mailInfo.getBcc();
			for (Address invalid : invalidAddresses) {
				bcc = bcc.replaceFirst(";?" + invalid.toString() + ";?", ";");
			}
			mailInfo.setBcc(bcc);
		}
		return sendMail(mailInfo);
	}

	/**
	 * 分割字符串为数组，只保留非空字符串
	 * 
	 * @param str
	 *            原始字符串
	 * @param regex
	 *            分割标识符
	 * @return 分割后的字符串数组
	 */
	public static String[] splitStringByTrim(String str, String regex) {
		if (StringUtils.isBlank(str) || StringUtils.isBlank(regex))
			return new String[] {};
		String[] s = str.split(regex);
		StringBuffer buffer = new StringBuffer();
		for (String string : s) {
			if (StringUtils.isNotBlank(string)) {
				buffer.append(string).append(regex);
			}
		}
		return StringUtils.split(buffer.toString(), regex);
	}
}
