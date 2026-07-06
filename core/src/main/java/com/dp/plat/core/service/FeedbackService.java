package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.Feedback;

/**
 * 用户反馈服务
 *
 * @author trae
 *
 */
public interface FeedbackService {

	/**
	 * 提交反馈
	 *
	 * @param feedback
	 */
	void submit(Feedback feedback);

	/**
	 * 按条件查询反馈列表
	 *
	 * @param query
	 * @return
	 */
	List<Feedback> list(Feedback query);

	/**
	 * 查询反馈详情
	 *
	 * @param id
	 * @return
	 */
	Feedback detail(Long id);

	/**
	 * 更新反馈状态
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	int updateStatus(Long id, String status);
}
