package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.Feedback;

import java.util.List;
import java.util.Map;

/**
 * Service for {@link Feedback}.
 */
public interface IFeedbackService extends IService<Feedback> {

    /**
     * Create a new feedback ticket. {@code userId} / {@code username} / default
     * {@code status=PENDING} are populated by the service.
     *
     * @param feedback 反馈实体
     * @return 是否保存成功
     */
    @Override
    boolean save(Feedback feedback);

    /**
     * List feedback submitted by the given username.
     *
     * @param username 用户名
     * @return 反馈列表（按创建时间倒序）
     */
    List<Feedback> listByUser(String username);

    /**
     * List all feedback (admin).
     *
     * @param status 状态过滤（可空）
     * @param category 分类过滤（可空）
     * @return 反馈列表（按创建时间倒序）
     */
    List<Feedback> listAll(String status, String category);

    /**
     * Reply to a feedback ticket. Sets {@code reply}, {@code replyBy},
     * {@code replyAt} and transitions status to PROCESSING (if currently PENDING).
     *
     * @param id      反馈 id
     * @param reply   回复内容
     * @param replyBy 回复人用户名
     * @return 是否更新成功
     */
    boolean reply(Long id, String reply, String replyBy);

    /**
     * Close a feedback ticket. Transitions status to CLOSED.
     *
     * @param id 反馈 id
     * @return 是否更新成功
     */
    boolean close(Long id);

    /**
     * Count feedback grouped by status.
     *
     * @return map of status → count
     */
    Map<String, Long> countByStatus();
}
