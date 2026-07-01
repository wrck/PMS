package com.dp.plat.model.entity;

import lombok.Data;
import java.util.Date;

/** 邮件发送信息 - 对应老系统 MailSenderInfo (21字段) */
@Data
public class MailSenderInfo {
    private String mailServerHost;
    private String mailServerPort;
    private String fromAddress;
    private String toAddress;
    private String ccAddress;
    private String bccAddress;
    private String userName;
    private String password;
    private boolean validate;
    private String subject;
    private String content;
    private String[] attachFileNames;
    private String charset;
    private String protocol;
    /** 预期发送时间 */
    private Date mailExpectSendTime;
}
