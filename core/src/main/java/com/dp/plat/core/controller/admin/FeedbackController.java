package com.dp.plat.core.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.pojo.Feedback;
import com.dp.plat.core.service.FeedbackService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

/**
 * 用户反馈管理
 *
 * @author trae
 *
 */
@Controller
@RequestMapping("/admin/feedback")
public class FeedbackController {

	@Resource
	private FeedbackService feedbackService;

	/**
	 * 反馈列表页
	 *
	 * @param model
	 */
	@RequestMapping
	public void listView(Model model) {
	}

	/**
	 * 反馈列表数据
	 *
	 * @param pageParam
	 * @param data
	 * @param model
	 * @return
	 */
	@RequestMapping("/list")
	@SystemControllerLog(description = "查看用户反馈列表")
	public String list(PageParam<Feedback> pageParam, Feedback data, Model model) {
		pageParam.setModel(data);
		List<Feedback> dataList = feedbackService.list(data);
		model.addAttribute("data", dataList);
		return "admin/feedback";
	}

	/**
	 * 反馈详情
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		Feedback feedback = feedbackService.detail(id);
		model.addAttribute("feedback", feedback);
		return "admin/feedback_detail";
	}

	/**
	 * 提交反馈（供前端弹窗/顶栏按钮 AJAX 调用，返回 JSON）
	 *
	 * @param feedback
	 * @return
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	@SystemControllerLog(description = "提交用户反馈")
	public Result submit(Feedback feedback) {
		feedbackService.submit(feedback);
		return Result.success(feedback);
	}

	/**
	 * 更新反馈状态（返回 JSON）
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	@ResponseBody
	@SystemControllerLog(description = "更新用户反馈状态")
	public Result updateStatus(Long id, String status) {
		if (id == null) {
			return Result.fail("反馈ID不能为空");
		}
		if (status == null || status.isEmpty()) {
			return Result.fail("状态不能为空");
		}
		int rows = feedbackService.updateStatus(id, status);
		if (rows > 0) {
			return Result.success();
		}
		return Result.fail("更新失败，反馈不存在或状态未变更");
	}
}
