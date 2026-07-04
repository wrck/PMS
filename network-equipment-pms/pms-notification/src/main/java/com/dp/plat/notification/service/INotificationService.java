package com.dp.plat.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.notification.entity.Notification;

import java.util.Map;
import java.util.Set;

/**
 * 通知中心服务。
 *
 * <p>提供站内信的创建、已读管理、未读统计、分页查询，以及多通道并发投递与模板化发送。
 * 支持的通道：{@code IN_APP}（站内信落库）、{@code WS}（WebSocket 实时推送）、
 * {@code EMAIL}（邮件，占位）、{@code OA}（OA 待办，占位）。</p>
 */
public interface INotificationService {

    /**
     * 创建一条站内信（落库，状态默认 UNREAD）。
     *
     * @param notification 通知内容（userId/title/content 必填）
     * @return 落库后的通知（含生成的 id）
     */
    Notification create(Notification notification);

    /**
     * 标记单条通知为已读。
     *
     * @param id 通知 id
     * @return 是否更新成功
     */
    boolean markAsRead(Long id);

    /**
     * 标记指定用户的全部未读通知为已读。
     *
     * @param userId 用户 id
     * @return 是否更新成功
     */
    boolean markAllRead(Long userId);

    /**
     * 统计指定用户的未读通知数。
     *
     * @param userId 用户 id
     * @return 未读条数
     */
    int unreadCount(Long userId);

    /**
     * 分页查询通知。filter 中的 userId、category、readStatus、bizType、bizId 作为过滤条件。
     *
     * @param page   页码（1-based）
     * @param size   每页条数
     * @param filter 过滤条件
     * @return 分页结果
     */
    IPage<Notification> list(int page, int size, Notification filter);

    /**
     * 多通道并发发送通知。任一通道失败仅记录日志，不会阻塞其他通道。
     *
     * <p>{@code IN_APP} 通道负责落库（生成 id 供 {@code WS} 使用）；
     * {@code WS} 通道通过 Redis Pub/Sub 广播，由各实例的订阅器推送到用户 WebSocket；
     * {@code EMAIL} / {@code OA} 通道为占位实现，生产环境可接入 EmailService/OaIntegrationService。</p>
     *
     * @param notification 通知内容
     * @param channels     投递通道集合（IN_APP/WS/EMAIL/OA）
     */
    void multiChannelSend(Notification notification, Set<String> channels);

    /**
     * 按模板渲染并发送通知。先使用 variables 渲染指定模板，再按 channels 多通道投递。
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @param userId       接收人用户 id
     * @param channels     投递通道集合
     */
    void sendByTemplate(String templateCode, Map<String, Object> variables, Long userId, Set<String> channels);
}
