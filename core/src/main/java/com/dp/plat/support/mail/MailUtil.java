package com.dp.plat.support.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.support.mail.config.MailConfig;
import com.dp.plat.support.mail.entity.MailInfo;
import com.dp.plat.support.mail.service.IMailInfoService;

/**
 * 邮件发送器
 */
@Component
public class MailUtil {

	private static final String DEFAULT_AFTER_SPLIT = "$";
	private static final String DEFAULT_BEFORE_SPLIT = "$";
	private static final String DEFAULT_REGEX = ";";
	// 外部邮箱地址域名
	// private static final String OUTMAIL_DOMAIN = "@dptech.com";
	// 内部邮箱地址域名
	private static final String INNERMAIL_DOMAIN = "@dp.com";
	// 获取img标签正则
	private static final String IMGURL_REG = "<img[^>]*src\\s*=\\s*([\\'\"]?)([^\\'\">]*)\\1(?=\\s|\\/|>)";
	// 获取src路径的正则
	private static final String IMGSRC_REG = "(https|http):\"?(.*?)(>|\"|\\s+)";

	@Autowired
	private IMailInfoService mailInfoService;

	private static MailUtil mailUtil;

	@PostConstruct
	public void init() {
		mailUtil = this;
		mailUtil.mailInfoService = this.mailInfoService;
	}

	public void setMailInfoService(IMailInfoService mailInfoService) {
		this.mailInfoService = mailInfoService;
	}

	/**
	 * 保存邮件信息无邮件模板
	 * 
	 * @param context
	 *            Map<String,Object>(默认值[v]类型为String)<br>
	 *            <b>重要键[k]如下:</b><br>
	 *            <b>标题：subject</b><br>
	 *            <b>内容：content</b><br>
	 *            <b>主送人员：tos</b>，以 ; 拼接邮件地址<br>
	 *            <b>抄送人员：ccs</b><br>
	 *            <b>密送人员：bccs</b><br>
	 *            <b>期望发送时间：expectSendTime</b>，数据类型Date<br>
	 *            <b>附件：attachFiles</b><br>
	 * 
	 */
	public static void keepMail(Map<String, Object> context) {
		// MailInfo info = new MailInfo();
		// info.setTos((String) context.get("tos"));
		// info.setCcs((String) context.get("ccs"));
		// info.setBccs((String) context.get("bccs"));
		// info.setSubject((String) context.get("subject"));
		// String content = (String) context.get("content");
		// if (StringUtils.isBlank(content)) {
		// content = "";
		// }
		// info.setContent(content);
		// info.setAttachFiles((String) context.get("attachFiles"));
		// info.setExpectSendTime((Date) context.get("expectSendTime"));
		// info.setCreateBy((String) context.get("createBy"));
		// keepMail(info);
		keepMail(context, false);
	}

	/**
	 * 保存邮件有邮件模板
	 * 
	 * @param context
	 *            Map<String,Object>(默认值[v]类型为String)<br>
	 *            <b>重要键[k]如下:</b><br>
	 *            <b>模板编码：templateCode</b><br>
	 *            <b>模板参数替代值实体实例：dataSource</b>
	 *            ，数据类型Object[]，可选。若有该属性，则替换参数值以实体类中属性值为准，且对象数组中同名属性值以较后者实例为准
	 *            <br>
	 *            <b>参数前后缀：beforeSplit、afterSplit</b>，默认都为$ <br>
	 *            <b>模板不存在缺省标题：subject</b><br>
	 *            <b>模板不存在缺省内容：content</b><br>
	 *            <b>主送人员：tos</b>，以 ; 拼接邮件地址<br>
	 *            <b>抄送人员：ccs</b><br>
	 *            <b>密送人员：bccs</b><br>
	 *            <b>期望发送时间：expectSendTime</b>，数据类型Date<br>
	 *            <b>附件：attachFiles</b><br>
	 *            <b>附属参数</b>，额外的模板替代参数
	 */
	public static void keepMailWithTemplate(Map<String, Object> context) {
		// String[] template = findTemplate(context);
		// if (template != null) {
		// context.put("subject", template[0]);
		// context.put("content", template[1]);
		// }
		// keepMail(context);
		keepMailWithTemplate(context, false);
	}

	/**
	 * 保存邮件实体
	 * 
	 * @param mailInfo
	 */
	public static void keepMail(MailInfo mailInfo) {
		// List<MailInfo> mailInfos = sortMailType(mailInfo);
		// for (MailInfo info : mailInfos) {
		// mailUtil.mailInfoService.insertSelective(info);
		// }
		keepMail(mailInfo, false);
	}

	/**
	 * 保存并发送邮件信息无邮件模板
	 * 
	 * @param context
	 *            Map<String,Object>(默认值[v]类型为String)<br>
	 *            <b>重要键[k]如下:</b><br>
	 *            <b>标题：subject</b><br>
	 *            <b>内容：content</b><br>
	 *            <b>主送人员：tos</b>，以 ; 拼接邮件地址<br>
	 *            <b>抄送人员：ccs</b><br>
	 *            <b>密送人员：bccs</b><br>
	 *            <b>期望发送时间：expectSendTime</b>，数据类型Date<br>
	 *            <b>附件：attachFiles</b><br>
	 * @param isSendNow
	 *            是否立即发送
	 * 
	 */
	public static void keepMail(Map<String, Object> context, boolean isSendNow) {
		MailInfo info = new MailSenderInfo();
		info.setTos((String) context.get("tos"));
		info.setCcs((String) context.get("ccs"));
		info.setBccs((String) context.get("bccs"));
		info.setSubject((String) context.get("subject"));
		String content = (String) context.get("content");
		if (StringUtils.isBlank(content)) {
			content = "";
		}
		info.setContent(content);
		info.setAttachFiles((String) context.get("attachFiles"));
		info.setExpectSendTime((Date) context.get("expectSendTime"));
		info.setCreateBy((String) context.get("createBy"));

		keepMail(info, isSendNow);
	}

	/**
	 * 保存并发送邮件邮件有邮件模板
	 * 
	 * @param context
	 *            Map<String,Object>(默认值[v]类型为String)<br>
	 *            <b>重要键[k]如下:</b><br>
	 *            <b>模板编码：templateCode</b><br>
	 *            <b>模板参数替代值实体实例：dataSource</b>
	 *            ，数据类型Object[]，可选。若有该属性，则替换参数值以实体类中属性值为准，且对象数组中同名属性值以较后者实例为准
	 *            <br>
	 *            <b>参数前后缀：beforeSplit、afterSplit</b>，默认都为$ <br>
	 *            <b>模板不存在缺省标题：subject</b><br>
	 *            <b>模板不存在缺省内容：content</b><br>
	 *            <b>主送人员：tos</b>，以 ; 拼接邮件地址<br>
	 *            <b>抄送人员：ccs</b><br>
	 *            <b>密送人员：bccs</b><br>
	 *            <b>期望发送时间：expectSendTime</b>，数据类型Date<br>
	 *            <b>附件：attachFiles</b><br>
	 *            <b>附属参数</b>，额外的模板替代参数
	 * @param isSendNow
	 *            是否立即发送
	 */
	public static void keepMailWithTemplate(Map<String, Object> context, boolean isSendNow) {
		String[] template = findTemplate(context);
		if (template != null) {
			context.put("subject", template[0]);
			context.put("content", template[1]);
		}
		keepMail(context, isSendNow);
	}

	/**
	 * 保存并发送邮件
	 * 
	 * @param mailInfo
	 * @param isSendNow
	 *            是否立即发送
	 */
	public static void keepMail(MailInfo mailInfo, boolean isSendNow) {
		List<MailInfo> mailInfos = sortMailType(mailInfo);
		for (MailInfo info : mailInfos) {
			if (isSendNow) {
				MailSenderInfo senderInfo = new MailSenderInfo();
				BeanUtils.copyProperties(info, senderInfo);
				sendMailWithAttachments(senderInfo);
				info = senderInfo;
			}
			mailUtil.mailInfoService.insertSelective(info);
		}
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 * @return 发送是否成功
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		try {
			Message mailMessage = createMimeMessage(mailInfo);
			// // 设置邮件消息发送的时间
			// mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			return sendMail(mailMessage, mailInfo);
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		return false;
	}

	/**
	 * 带附件邮件发送方法
	 * 
	 * @return 发送是否成功
	 */
	public static boolean sendMailWithAttachments(MailSenderInfo mailInfo) {
		try {
			MimeMessage mimeMsg = createMimeMessage(mailInfo);
			// Transport transport = mimeMsg.getSession().getTransport("smtp");
			Multipart multipart = new MimeMultipart("mixed");// 附件传输格式
			MimeBodyPart content = new MimeBodyPart();
			// content.setContent(mailInfo.getContent(),
			// "text/html;charset=utf-8");
			multipart.addBodyPart(content);

			multipart = addAttachments(mailInfo, multipart);

			insertPictureIntoContent(mailInfo, content);

			mimeMsg.setContent(multipart);
			mimeMsg.saveChanges();
			// 发送邮件
			return sendMail(mimeMsg, mailInfo);
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
			// e.printStackTrace();
		}
		return false;
	}

	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 * @return 发送是否成功
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		try {
			Message mailMessage = createMimeMessage(mailInfo);
			// // 设置邮件消息发送的时间
			// mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			return sendMail(mailMessage, mailInfo);
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
			// ex.printStackTrace();
		}
		return false;
	}

	private static boolean sendMail(Message mailMessage, MailSenderInfo mailInfo) {
		try {
			List<Message> mailMessageList = sortMailType(mailMessage, mailInfo.getIsInner());
			for (Message message : mailMessageList) {
				if (message != null) {
					Transport.send(message);
				}
			}
			mailInfo.setSendTime(new Date());
			mailInfo.setSendFlag(true);
			return true;
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		int faliedCount = mailInfo.getFailedCount() != null ? mailInfo.getFailedCount() : 0;
		mailInfo.setFailedCount(faliedCount + 1);
		return false;
	}

	/**
	 * 设置邮件发送服务器参数，包括邮件服务器地址、端口，认证用户名、密码，发件地址等
	 * 
	 * @param mailSenderInfo
	 */
	private static void completeMailServerVariables(MailSenderInfo mailSenderInfo) {
		HashMap<String, String> systemVariables = MailConfig.getMailVariables();
		mailSenderInfo.setFromNick(systemVariables.get("sys.mail.defaultNick"));
		if (Boolean.TRUE.equals(mailSenderInfo.getIsInner())) {
			mailSenderInfo.setMailServerHost(systemVariables.get("sys.innerMail.server.host"));
			mailSenderInfo.setMailServerPort(systemVariables.get("sys.innerMail.server.port"));
			mailSenderInfo.setValidate(true);
			mailSenderInfo.setUserName(systemVariables.get("sys.innerMail.server.username"));
			mailSenderInfo.setPassword(systemVariables.get("sys.innerMail.server.password"));
			mailSenderInfo.setFromAddress(systemVariables.get("sys.innerMail.server.fromAddress"));
		} else {
			mailSenderInfo.setMailServerHost(systemVariables.get("sys.mail.server.host"));
			mailSenderInfo.setMailServerPort(systemVariables.get("sys.mail.server.port"));
			mailSenderInfo.setValidate(true);
			mailSenderInfo.setUserName(systemVariables.get("sys.mail.server.username"));
			mailSenderInfo.setPassword(systemVariables.get("sys.mail.server.password"));
			mailSenderInfo.setFromAddress(systemVariables.get("sys.mail.server.fromAddress"));
		}
	}

	/**
	 * 根据邮件实体组织MimeMessage消息头， 包括邮件会话属性、认证、收件地址等
	 * 
	 * @param mailInfo
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	private static MimeMessage createMimeMessage(MailSenderInfo mailInfo) throws MessagingException {
		// 判断是否需要身份认证
		Authenticator authenticator = null;
		// 补充邮件服务器相关参数
		completeMailServerVariables(mailInfo);
		// 获取会话信息
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new Authenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		MimeMessage mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		// Address from = new InternetAddress(mailInfo.getFromAddress());
		Address from = addNickForAddr(mailInfo.getFromNick(), mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		setAllRecipients(mailInfo, mailMessage);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject(), "utf-8");
		// 设置信件头的发送日期
		mailMessage.setSentDate(new Date());
		return mailMessage;
	}

	/**
	 * 根据内外网邮箱类型，组织一个MimeMessage消息头， 包括邮件会话属性、认证等，不包括收件地址
	 * 
	 * @param isInner
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	private static MimeMessage createMimeMessage(Message message, boolean isInner) throws MessagingException {
		// 判断是否需要身份认证
		Authenticator authenticator = null;
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setIsInner(isInner);
		// 补充邮件服务器相关参数
		completeMailServerVariables(mailInfo);
		// 获取会话信息
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new Authenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		MimeMessage mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		// Address from = new InternetAddress(mailInfo.getFromAddress());
		Address from = addNickForAddr(mailInfo.getFromNick(), mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// // 创建邮件的接收者地址，并设置到邮件消息中
		// setAllRecipients(mailInfo, mailMessage);
		// 设置邮件消息的主题
		mailMessage.setSubject(message.getSubject());
		// 设置信件头的发送日期
		mailMessage.setSentDate(new Date());
		return mailMessage;
	}

	/**
	 * 处理邮件地址，过滤掉不合法的邮件地址
	 * 
	 * @param str
	 *            原始邮件地址拼接字符串
	 * @param regex
	 *            分割标识符
	 * @return InternetAddress[] 处理后的邮件地址
	 * @throws AddressException
	 */
	private static InternetAddress[] filterEmailAddress(String str, String regex) {
		if (StringUtils.isBlank(str) || StringUtils.isBlank(regex))
			return null;
		String[] s = str.split(regex);
		List<InternetAddress> addressList = new ArrayList<InternetAddress>();
		for (String string : s) {
			if (StringUtils.isNotBlank(string)) {
				try {
					InternetAddress address = new InternetAddress(string);
					addressList.add(address);
				} catch (Exception e) {
					ExceptionHandler.insertException(e);
				}
			}
		}
		InternetAddress[] internetAddresses = new InternetAddress[addressList.size()];
		addressList.toArray(internetAddresses);
		return internetAddresses;
	}

	/**
	 * 根据传入的上下文参数，查找相应模板，返回处理后的邮件标题和正文
	 * 
	 * @param context
	 * @return [0]标题 [1]正文
	 */
	private static String[] findTemplate(Map<String, Object> context) {
		String templateCode = (String) context.get("templateCode");
		NotificationTemplate template = null;
		if (StringUtils.isNotBlank(templateCode)) {
			template = getTemplate(templateCode);
		}
		// if (template == null) {
		// return null;
		// }
		template = processTemplate(template, context);
		return new String[] { template.getSubject(), template.getContent() };
	}

	/**
	 * 从数据库加载邮件模板
	 * 
	 * @param templateCode
	 * @return
	 */
	private static NotificationTemplate getTemplate(String templateCode) {
		return mailUtil.mailInfoService.queryNotificationTemplate(templateCode);
	}

	/**
	 * 传入数据模型类解析需要的替换参数，默认前后缀$field$
	 * 
	 * @param objects
	 * @param context
	 * @return paramNames
	 * @throws ClassNotFoundException
	 */
	private static Set<String> getTemplateParams(Object[] objects, Map<String, Object> context) {
		return getTemplateParams(DEFAULT_BEFORE_SPLIT, DEFAULT_AFTER_SPLIT, objects, context);
	}

	/**
	 * 传入数据模型类解析需要的替换参数，以及替代参数的前后缀
	 * 
	 * @param beforeSplit
	 * @param afterSplit
	 * @param objects
	 * @param context
	 * @return paramNames
	 */
	private static Set<String> getTemplateParams(String beforeSplit, String afterSplit, Object[] objects,
			Map<String, Object> context) {
		Set<String> paramNames = new HashSet<String>();
		for (Object obj : objects) {
			try {
				if (obj == null) {
					continue;
				}
				Class<?> c = obj.getClass();
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					paramNames.add(beforeSplit + field.getName() + afterSplit);
					Object value = field.get(obj);
					if (value != null) {
						context.put(field.getName(), value);
					}
				}
			} catch (Exception e) {
				ExceptionHandler.insertException(e);
				// e.printStackTrace();
			}
		}
		return paramNames;
	}

	/**
	 * 将模板中的参数，使用真实值替换
	 * 
	 * @param templete
	 * @param context
	 * @return NotificationTemplate 处理后的邮件模板
	 * @throws ClassNotFoundException
	 */
	private static NotificationTemplate processTemplate(NotificationTemplate templete, Map<String, Object> context) {
		Set<String> paramNames = null;
		String templateCode = StringUtils.trimToEmpty((String) context.get("templateCode"));
		String subject = StringUtils.trimToEmpty((String) context.get("subject"));
		String content = StringUtils.trimToEmpty((String) context.get("content"));
		if (templete != null) {
			subject = templete.getSubject();
			content = templete.getContent();
		}
		// 实体数据源，模板值以实体数据源的值为准，若无实体数据源，则以context中的对应值为准
		Object[] objects = (Object[]) context.get("dataSource");
		if (objects != null) {
			if (context.get("beforeSplit") != null && context.get("afterSplit") != null) {
				paramNames = getTemplateParams((String) context.get("beforeSplit"), (String) context.get("afterSplit"),
						objects, context);
			} else {
				paramNames = getTemplateParams(objects, context);
			}
			for (String name : paramNames) {
				Object value = context.get(name.substring(1, name.length() - 1));
				value = value == null ? "" : value;
				String regex = "\\Q" + name + "\\E";
				subject = subject.replaceAll(regex, value.toString());
				content = content.replaceAll(regex, value.toString());
			}
		}
		// 处理实体数据源以外的其他模板值
		String beforeSplit = DEFAULT_BEFORE_SPLIT;
		String afterSplit = DEFAULT_AFTER_SPLIT;
		if (context.get("beforeSplit") != null) {
			beforeSplit = (String) context.get("beforeSplit");
		}
		if (context.get("afterSplit") != null) {
			afterSplit = (String) context.get("afterSplit");
		}
		for (Entry<String, Object> entry : context.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			value = (value == null) ? "" : value;
			String regex = "\\Q" + key + "\\E";
			if (key.contains(beforeSplit) && key.contains(afterSplit)) {
				regex = "\\Q" + key + "\\E";
			} else if (key.contains(beforeSplit) && !key.contains(afterSplit)) {
				regex = "\\Q" + key + afterSplit + "\\E";
			} else if (!key.contains(beforeSplit) && key.contains(afterSplit)) {
				regex = "\\Q" + beforeSplit + key + "\\E";
			} else {
				regex = "\\Q" + beforeSplit + key + afterSplit + "\\E";
			}
			subject = subject.replaceAll(regex, value.toString());
			content = content.replaceAll(regex, value.toString());
		}
		subject = subject.replaceAll("\\" + beforeSplit + "(\\w+)\\" + afterSplit + "", "");
		content = content.replaceAll("\\" + beforeSplit + "(\\w+)\\" + afterSplit + "", "");
		NotificationTemplate processedTemplate = new NotificationTemplate();
		processedTemplate.setTemplateCode(templateCode);
		processedTemplate.setSubject(subject);
		processedTemplate.setContent(content);
		return processedTemplate;
	}

	/**
	 * 设置邮件接收者。<br>
	 * 正式环境发送给邮件设定的主送、抄送、密送人员；<br>
	 * 测试环境发送给维护人员；<br>
	 * 当邮件无接收者时，发送给维护人员<br>
	 * 
	 * @param mailInfo
	 * @param mimeMessage
	 * @throws MessagingException
	 */
	private static void setAllRecipients(MailSenderInfo mailInfo, MimeMessage mimeMessage) throws MessagingException {
		// 创建邮件的接收者地址，并设置到邮件消息中
		HashMap<String, String> systemVariables = MailConfig.getMailVariables();
		String envirmentArgu = systemVariables.get("sys.envirment.argu");
		// 1:正式环境，2：正式试运行，0：测试环境
		if ("1".equals(envirmentArgu) || "2".equals(envirmentArgu)) {
			// 主送
			InternetAddress[] tos = filterEmailAddress(mailInfo.getTos(), ";");
			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, tos);
			// 抄送
			InternetAddress[] ccs = filterEmailAddress(mailInfo.getCcs(), ";");
			mimeMessage.setRecipients(MimeMessage.RecipientType.CC, ccs);
			// 密送
			InternetAddress[] bccs = filterEmailAddress(mailInfo.getBccs(), ";");
			mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, bccs);

			// 正式试运行，密送邮件给特定邮箱
			if ("2".equals(envirmentArgu)) {
				String devTos = systemVariables.get("sys.mail.develop.receiveAddress");
				InternetAddress[] devBccs = filterEmailAddress(devTos, ";");
				mimeMessage.addRecipients(MimeMessage.RecipientType.BCC, devBccs);
			}
		} else {
			String devTos = systemVariables.get("sys.mail.develop.receiveAddress");
			InternetAddress[] tos = filterEmailAddress(devTos, ";");
			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, tos);
		}
		// 邮件没有接收人，发送给系统维护人员
		if (mimeMessage.getAllRecipients() == null || mimeMessage.getAllRecipients().length == 0) {
			String devTos = systemVariables.get("sys.mail.develop.receiveAddress");
			InternetAddress[] tos = filterEmailAddress(devTos, ";");
			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, tos);
			mimeMessage.setSubject(mailInfo.getSubject() + "----邮件无接收人提醒", "utf-8");
		}
		// String actualSendAddress =
		// ArrayUtils.toString(mimeMessage.getAllRecipients(), null);
		String actualSendAddress = StringUtils.join(mimeMessage.getAllRecipients(), ";");

		mailInfo.setActualSendAddress(actualSendAddress);
	}

	/**
	 * 根据邮件正文信息。匹配出内置的img提取出src的url地址
	 * 
	 * @param mailInfo
	 * @return
	 */
	private static List<String> matchImgSrc(MailInfo mailInfo) {
		List<String> srcList = new ArrayList<String>();
		Matcher matcherImg = Pattern.compile(IMGURL_REG).matcher(mailInfo.getContent());
		while (matcherImg.find()) {
			String imgTag = matcherImg.group() + ">";
			Matcher matcherSrc = Pattern.compile(IMGSRC_REG).matcher(imgTag);
			while (matcherSrc.find()) {
				String src = matcherSrc.group();
				src = src.replace("\"", "");
				srcList.add(src);
			}
		}
		return srcList;
	}

	/**
	 * 根据图片img的src属性，获取服务器绝对地址
	 * 
	 * @param src
	 * @return
	 */
	private static String getImgRealPath(String src) {
		ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
		int idxEnd = src.lastIndexOf(":");
		int idxStart = src.indexOf(":");
		String uri = null;
		if (idxEnd != idxStart && idxEnd > 0) {
			uri = src.substring(idxEnd);
			String contextPath = servletContext.getContextPath();
			uri = uri.replace(contextPath, "");
			int idxTemp = uri.indexOf("/");
			uri = uri.substring(idxTemp);
		} else {
			int indexOf = src.indexOf("static");
			uri = src.substring(indexOf);
		}
		String filePath = servletContext.getRealPath(uri);
		return filePath;
	}

	/**
	 * 向邮件中添加附件
	 * 
	 * @param mailInfo
	 * @param multipart
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private static Multipart addAttachments(MailInfo mailInfo, Multipart multipart)
			throws MessagingException, UnsupportedEncodingException {
		String[] fileNames = new String[] {};
		if (mailInfo.getAttachFiles() != null) {
			fileNames = mailInfo.getAttachFiles().split("&&");
		}
		ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
		if (fileNames != null && fileNames.length > 0) {
			for (int i = 0; i < fileNames.length; i++) {
				MimeBodyPart fileAttaches = new MimeBodyPart();
				// 选择出每一个附件名
				String filePath = fileNames[i].split(",")[0];
				File file = new File(filePath);
				if (!file.exists()) {
					filePath = servletContext.getRealPath(filePath);
				}
				String displayname = fileNames[i].split(",")[1];
				// 得到数据源
				FileDataSource fds = new FileDataSource(filePath);
				// 得到附件本身并至入BodyPart
				fileAttaches.setDataHandler(new DataHandler(fds));
				// 得到文件名同样至入BodyPart
				fileAttaches.setFileName(MimeUtility.encodeText(displayname));
				multipart.addBodyPart(fileAttaches);
			}
		}
		return multipart;
	}

	/**
	 * 向正文中添加图片
	 * 
	 * @param mailInfo
	 * @param content
	 * @throws MessagingException
	 */
	private static void insertPictureIntoContent(MailInfo mailInfo, MimeBodyPart content) throws MessagingException {
		MimeMultipart bodyMultipart = new MimeMultipart("related");
		content.setContent(bodyMultipart);
		MimeBodyPart htmlPart = new MimeBodyPart();
		bodyMultipart.addBodyPart(htmlPart);
		List<String> srcList = matchImgSrc(mailInfo);
		for (String src : srcList) {
			MimeBodyPart gifPart = new MimeBodyPart();
			bodyMultipart.addBodyPart(gifPart);
			String filePath = getImgRealPath(src);
			DataSource gifds = new FileDataSource(filePath);
			DataHandler gifdh = new DataHandler(gifds);
			gifPart.setDataHandler(gifdh);
			gifPart.setHeader("Content-Location", src);
		}

		htmlPart.setContent(mailInfo.getContent(), "text/html;charset=utf-8");
	}

	/**
	 * 内外网邮箱需要分开发送，所以需要对收件地址进行分拣
	 * 
	 * @param info
	 * @return
	 */
	private static List<MailInfo> sortMailType(MailInfo info) {
		int mailType = checkMailType(info);
		List<MailInfo> mailInfos = new ArrayList<>();
		if (mailType == 1) {
			info.setIsInner(true);
		}
		if (mailType != 2) {
			mailInfos.add(info);
			return mailInfos;
		}

		String[] tos = splitMailStr(info.getTos(), ";");
		String[] ccs = splitMailStr(info.getCcs(), ";");
		String[] bccs = splitMailStr(info.getBccs(), ";");

		StringBuilder innerTos = new StringBuilder();
		StringBuilder innerCcs = new StringBuilder();
		StringBuilder innerBccs = new StringBuilder();
		StringBuilder outerTos = new StringBuilder();
		StringBuilder outerCcs = new StringBuilder();
		StringBuilder outerBccs = new StringBuilder();

		sortMailStr(tos, innerTos, outerTos);
		sortMailStr(ccs, innerCcs, outerCcs);
		sortMailStr(bccs, innerBccs, outerBccs);

		mailInfos.add(cloneMainInfo(info, outerTos, outerCcs, outerBccs, false));
		mailInfos.add(cloneMainInfo(info, innerTos, innerCcs, innerBccs, true));
		return mailInfos;
	}

	/**
	 * 内外网邮箱需要分开发送，所以需要对收件地址进行分拣
	 * 
	 * @param mimeMessage
	 * @return
	 */
	private static List<Message> sortMailType(Message mimeMessage, Boolean isInner) {
		int mailType = checkMailType(mimeMessage);
		List<Message> mimeMessageList = new ArrayList<>();
		if (mailType != 2) {
			// 原来的邮件类型和最终的一致，则不重新构建消息体
			if ((mailType == 1 && Boolean.TRUE.equals(isInner)) || (mailType == 0 && !Boolean.TRUE.equals(isInner))) {
				mimeMessageList.add(mimeMessage);
				return mimeMessageList;
			}
		}

		String[] tos = new String[0];
		String[] ccs = new String[0];
		String[] bccs = new String[0];
		try {
			tos = splitMailStr(StringUtils.join(mimeMessage.getRecipients(RecipientType.TO), ";"), ";");
			ccs = splitMailStr(StringUtils.join(mimeMessage.getRecipients(RecipientType.CC), ";"), ";");
			bccs = splitMailStr(StringUtils.join(mimeMessage.getRecipients(RecipientType.BCC), ";"), ";");
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		StringBuilder innerTos = new StringBuilder();
		StringBuilder innerCcs = new StringBuilder();
		StringBuilder innerBccs = new StringBuilder();
		StringBuilder outerTos = new StringBuilder();
		StringBuilder outerCcs = new StringBuilder();
		StringBuilder outerBccs = new StringBuilder();

		sortMailStr(tos, innerTos, outerTos);
		sortMailStr(ccs, innerCcs, outerCcs);
		sortMailStr(bccs, innerBccs, outerBccs);

		mimeMessageList.add(cloneMessage(mimeMessage, outerTos, outerCcs, outerBccs, false));
		mimeMessageList.add(cloneMessage(mimeMessage, innerTos, innerCcs, innerBccs, true));
		return mimeMessageList;
	}

	/**
	 * 检查邮箱所有收件地址是否有2种类别的邮箱，返回存在的邮箱域名类型
	 * 
	 * @param info
	 * @return 0:outer, 1:inner, 2:both, -1:none
	 */
	private static int checkMailType(MailInfo info) {
		String[] mailStrs = new String[] { info.getTos(), info.getCcs(), info.getBccs() };
		return filterMailType(mailStrs);
	}

	/**
	 * 检查邮箱所有收件地址是否有2种类别的邮箱，返回存在的邮箱域名类型
	 * 
	 * @param info
	 * @return 0:outer, 1:inner, 2:both, -1:none
	 */
	private static int checkMailType(Message info) {
		Address[][] mailAddrs = new Address[0][];
		try {
			mailAddrs = new Address[][] { info.getRecipients(RecipientType.TO), info.getRecipients(RecipientType.CC),
					info.getRecipients(RecipientType.BCC) };
		} catch (MessagingException e) {
			ExceptionHandler.insertException(e);
			// e.printStackTrace();
		}
		// 重构
		List<String> mailStrList = new ArrayList<>();
		for (Address[] mailStrAddr : mailAddrs) {
			String mailStr = StringUtils.join(mailStrAddr, ";");
			if (StringUtils.isNotBlank(mailStr)) {
				mailStrList.add(mailStr);
			}
		}
		return filterMailType(mailStrList.toArray(new String[0]));
	}

	/**
	 * 过滤邮箱所有收件地址是否有内网邮箱，返回存在的邮箱域名类型
	 * 
	 * @param mailStrs
	 * @return 0:outer, 1:inner, 2:both, -1:none
	 */
	private static int filterMailType(String[] mailStrs) {
		HashSet<String> mailDomains = new HashSet<>();
		Pattern p = Pattern.compile("@(\\w+)(\\.)(\\w+)(\\.\\w+)*");
		for (String mailStr : mailStrs) {
			if (StringUtils.isBlank(mailStr)) {
				continue;
			}
			Matcher m = p.matcher(mailStr);
			while (m.find()) {
				String domain = m.group();
				if (StringUtils.isNotBlank(domain)) {
					mailDomains.add(domain.toLowerCase());
				}
			}
		}
		// 区分邮件域名，不区分大小写
		if (!mailDomains.isEmpty()) {
			if (mailDomains.size() == 1 && mailDomains.contains(INNERMAIL_DOMAIN)) {// 仅有@dp.com邮箱后缀
				return 1;
			} else if (mailDomains.size() > 1 && mailDomains.contains(INNERMAIL_DOMAIN)) {// 多种邮件域名，并且含有@dp.com邮箱后缀
				return 2;
			} else {// 没有@dp.com邮箱后缀，都可由外网邮箱服务器发送
				return 0;
			}
		}
		return -1;
	}

	/**
	 * 切割邮箱地址字符串，返回邮箱地址数组
	 * 
	 * @param mailStr
	 * @param regex
	 * @return
	 */
	private static String[] splitMailStr(String mailStr, String regex) {
		if (StringUtils.isBlank(mailStr)) {
			return new String[0];
		}
		if (StringUtils.isBlank(regex)) {
			regex = DEFAULT_REGEX;
		}
		return mailStr.split(regex);
	}

	/**
	 * 将传入的邮箱地址数组，分成添加到inner，和outer
	 * 
	 * @param mailStrs
	 * @param inner
	 * @param outer
	 */
	private static void sortMailStr(String[] mailStrs, StringBuilder inner, StringBuilder outer) {
		for (String mailStr : mailStrs) {
			if (StringUtils.isBlank(mailStr)) {
				continue;
			}
			if (mailStr.toLowerCase().contains(INNERMAIL_DOMAIN)) {
				inner.append(mailStr).append(";");
			} else {
				outer.append(mailStr).append(";");
			}
			// TODO 其他外网邮箱，非公司邮箱
			// if (mailStr.contains(OUTMAIL_DOMAIN)) {
			// outer.append(mailStr).append(";");
			// }
		}
	}

	/**
	 * 克隆mainInfo， 更新收件地址以及isInner
	 * 
	 * @param info
	 * @param tos
	 * @param ccs
	 * @param bccs
	 * @param isInner
	 * @return mainInfo
	 */
	private static MailInfo cloneMainInfo(MailInfo info, StringBuilder tos, StringBuilder ccs, StringBuilder bccs,
			boolean isInner) {
		MailInfo mailInfo = new MailInfo();
		BeanUtils.copyProperties(info, mailInfo);
		mailInfo.setTos(tos.toString());
		mailInfo.setCcs(ccs.toString());
		mailInfo.setBccs(bccs.toString());
		if (isInner) {
			mailInfo.setIsInner(isInner);
		}
		return mailInfo;
	}

	/**
	 * 克隆mainInfo， 更新收件地址以及isInner
	 * 
	 * @param info
	 * @param tos
	 * @param ccs
	 * @param bccs
	 * @param isInner
	 * @return mainInfo
	 */
	private static Message cloneMessage(Message info, StringBuilder tos, StringBuilder ccs, StringBuilder bccs,
			boolean isInner) {
		Message mimeMessage = null;
		try {
			String tosStr = tos.toString();
			String ccsStr = ccs.toString();
			String bccsStr = bccs.toString();
			if (StringUtils.isNotBlank(tosStr) || StringUtils.isNotBlank(ccsStr) || StringUtils.isNotBlank(bccsStr)) {
				InternetAddress[] tosAddr = filterEmailAddress(tos.toString(), ";");
				InternetAddress[] ccsAddr = filterEmailAddress(ccs.toString(), ";");
				InternetAddress[] bccsAddr = filterEmailAddress(bccs.toString(), ";");
				mimeMessage = createMimeMessage(info, isInner);
				mimeMessage.setRecipients(RecipientType.TO, tosAddr);
				mimeMessage.setRecipients(RecipientType.CC, ccsAddr);
				mimeMessage.setRecipients(RecipientType.BCC, bccsAddr);
				mimeMessage.setContent((Multipart) info.getContent());
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
			//e.printStackTrace();
		}
		return mimeMessage;
	}

	// 邮件地址添加nick
	private static InternetAddress addNickForAddr(String nick, String address) throws AddressException {
		if (StringUtils.isBlank(nick)) {
			return new InternetAddress(address);
		}
		String nickEncodeText = "";
		try {
			nickEncodeText = MimeUtility.encodeText(nick);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new InternetAddress(nickEncodeText + "<" + address + ">");
	}
}
