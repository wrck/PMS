package com.dp.plat.service;

import com.dp.plat.model.entity.MailSenderInfo;

/**
 * 邮件服务接口 - 迁移自老系统 SendMailService
 */
public interface SendMailService {

    /**
     * 保存邮件信息到数据库
     * 迁移自: SendMailServiceImpl.keepMailInfo()
     */
    void keepMailInfo(MailSenderInfo info);

    /**
     * 发送邮件
     * 迁移自: 老系统邮件发送逻辑
     */
    void sendMail(MailSenderInfo info);

    /**
     * 发送HTML邮件
     */
    void sendHtmlMail(String to, String subject, String htmlContent);

    /**
     * 发送简单文本邮件
     */
    void sendSimpleMail(String to, String subject, String content);
}
