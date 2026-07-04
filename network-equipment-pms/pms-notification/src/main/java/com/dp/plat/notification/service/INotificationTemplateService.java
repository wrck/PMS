package com.dp.plat.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.notification.entity.NotificationTemplate;

/**
 * 通知模板服务，提供模板 CRUD 与按编码查询。
 */
public interface INotificationTemplateService extends IService<NotificationTemplate> {

    /**
     * 按模板编码查询。
     *
     * @param templateCode 模板编码
     * @return 模板实体，不存在返回 {@code null}
     */
    NotificationTemplate getByCode(String templateCode);

    /**
     * 分页查询模板。
     *
     * @param page 页码（1-based）
     * @param size 每页条数
     * @return 分页结果
     */
    IPage<NotificationTemplate> list(int page, int size);
}
