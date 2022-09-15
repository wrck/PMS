package com.dp.plat.service;

import java.util.List;

import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.WorkflowCommonParam;

/**
 * 回访流程业务逻辑处理
 * @author admin
 *
 */
public interface CallBackService {
	/**
	 * 启动回访流程，并发起申请
	 */
	void startCallBackFlow(CallBack callBack);
	/**
	 * 查询callBack 根据ID
	 * @param callBackId
	 * @return
	 */
	CallBack queryCallBackById(int callBackId);
	/**
	 * 保存或提交回访问卷信息
	 * @param callBack
	 * @param pmClQuesnaireResultHeader
	 * @param pmClQuesnaireResultLineList
	 * @return
	 */
	void insertCallBackQuesnaire(CallBack callBack,
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList);
	/**
	 * 根据ID获取回访的问卷信息
	 * @param quesnaireId
	 * @return
	 */
	CallBackQuesnaire queryCbQuesnaire(int quesnaireId);
	/**
	 * 根据已经填写的问卷获取模板ID
	 * @param quesnaireId
	 * @return
	 */
	int queryQuesnaireTemplateId(int quesnaireId);
	/**
	 * 对回访流程进行审批
	 * @param param
	 */
	void submitCallBackFlow(WorkflowCommonParam param, CallBack callBack);
	/**
	 *  更新审批状态
	 * @param callBackId
	 * @param applyState
	 */
	void updateCallBackApplyState(int callBackId, int applyState);
	/**
	 * 查询回访申请审批意见列表
	 * @param callBackId
	 * @return
	 */
	List<CallBackComment> queryCallBackComment(int callBackId);
	/**
	 * 重新提交申请
	 * @param param
	 * @param callBack
	 */
	void reSubmitCallBackFlow(WorkflowCommonParam param, CallBack callBack);
}
