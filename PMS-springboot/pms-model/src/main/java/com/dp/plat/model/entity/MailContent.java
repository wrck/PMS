package com.dp.plat.model.entity;

import lombok.Data;

/** 邮件内容 - 对应老系统 MailContent (5字段) */
@Data
public class MailContent {
    private String title;
    private String content;
    private String[] toAddresses;
    private String[] ccAddresses;
    private String[] attachments;
}
