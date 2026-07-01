package com.dp.plat.model.entity;

import lombok.Data;

/** 通知模板 - 对应老系统 NotificationTemplate (3字段) */
@Data
public class NotificationTemplate {
    private String templateCode;
    private String templateName;
    private String templateContent;
}
