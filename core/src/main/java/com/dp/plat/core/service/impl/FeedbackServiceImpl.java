package com.dp.plat.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.annotation.SystemServiceLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.dao.FeedbackMapper;
import com.dp.plat.core.pojo.Feedback;
import com.dp.plat.core.service.FeedbackService;

/**
 * 用户反馈服务实现
 *
 * @author trae
 *
 */
@Service("feedbackService")
public class FeedbackServiceImpl implements FeedbackService {

	@Resource
	private FeedbackMapper feedbackMapper;

	@Override
	@Transactional
	@SystemServiceLog(description = "提交用户反馈")
	public void submit(Feedback feedback) {
		if (feedback.getStatus() == null || feedback.getStatus().isEmpty()) {
			feedback.setStatus("open");
		}
		feedback.setCreateUser(UserContext.getUsername());
		feedback.setCreateTime(new Date());
		feedbackMapper.insert(feedback);
	}

	@Override
	@SystemServiceLog(description = "查询用户反馈列表")
	public List<Feedback> list(Feedback query) {
		return feedbackMapper.findList(query);
	}

	@Override
	public Feedback detail(Long id) {
		return feedbackMapper.findById(id);
	}

	@Override
	@Transactional
	@SystemServiceLog(description = "更新用户反馈状态")
	public int updateStatus(Long id, String status) {
		Feedback feedback = new Feedback();
		feedback.setId(id);
		feedback.setStatus(status);
		feedback.setUpdateUser(UserContext.getUsername());
		feedback.setUpdateTime(new Date());
		return feedbackMapper.update(feedback);
	}

}
