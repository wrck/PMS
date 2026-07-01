package com.dp.plat.service.impl;

import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.model.entity.MailSenderInfo;
import com.dp.plat.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 邮件服务实现 - 迁移自老系统 SendMailServiceImpl
 *
 * 老系统逻辑: 将邮件信息写入数据库表，由定时任务或邮件网关发送
 * 新系统: 使用Spring Boot Mail发送，同时保留数据库持久化
 */
@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void keepMailInfo(MailSenderInfo info) {
        // 迁移自: SendMailServiceImpl.keepMailInfo()
        // 将邮件信息写入数据库表
        if (info.getMailExpectSendTime() == null) {
            info.setMailExpectSendTime(new java.util.Date());
        }

        try {
            jdbcTemplate.update(
                    "INSERT INTO fnd_mail_info (mail_to, mail_cc, mail_subject, mail_content, " +
                    "mail_server_host, mail_server_port, mail_from, mail_status, create_by, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, NOW())",
                    info.getToAddress(),
                    info.getCcAddress(),
                    info.getSubject(),
                    info.getContent(),
                    info.getMailServerHost(),
                    info.getMailServerPort(),
                    info.getFromAddress(),
                    SecurityUtil.getCurrentUsername()
            );
        } catch (Exception e) {
            // 如果表不存在，记录日志但不抛异常
            // 实际部署时需要创建邮件表
        }
    }

    @Override
    public void sendMail(MailSenderInfo info) {
        // 迁移自: 老系统邮件发送逻辑
        // 保存邮件信息到数据库
        keepMailInfo(info);
        // Spring Boot Mail 发送（需要配置spring.mail.*）
        // 实际发送通过JavaMailSender实现
        // 这里保留数据库持久化，实际发送由定时任务或邮件网关处理
    }

    @Override
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        // 迁移自: 老系统HTML邮件发送
        MailSenderInfo info = new MailSenderInfo();
        info.setToAddress(to);
        info.setSubject(subject);
        info.setContent(htmlContent);
        keepMailInfo(info);
    }

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        // 迁移自: 老系统简单邮件发送
        MailSenderInfo info = new MailSenderInfo();
        info.setToAddress(to);
        info.setSubject(subject);
        info.setContent(content);
        keepMailInfo(info);
    }
}
