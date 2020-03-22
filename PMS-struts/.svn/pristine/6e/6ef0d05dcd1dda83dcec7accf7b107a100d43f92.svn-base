package com.dp.plat.dao;

import java.util.List;

import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.CallBackQuesnaire;

/**
 * 回访数据数据库管理
 * @author admin
 *
 */
public interface CallBackDao {
	/**
	 * 保存回访申请信息，返回主键
	 * @param callBack
	 * @return
	 */
	int insertCallBack(CallBack callBack);
	/**
	 * 将activity 流程写入业务表，并更新申请状态为审批中
	 * @param callBackId
	 * @param instId
	 */
	void updateCallBackInstId(int callBackId, String instId);
	/**
	 * 查询pm_cl_callback By Id
	 * @param callBackId
	 * @return
	 */
	CallBack queryCallBackById(int callBackId);
	/**
	 * 查询回访的问卷版本号
	 * @param callBackId
	 * @return
	 */
	int queryCallBackQuesnaireVersion(int callBackId);
	/**
	 * 插入 pm_cl_callback_quesnaire
	 * @param cbq
	 */
	void insertCallBackQuesnaire(CallBackQuesnaire cbq);
	/**
	 * 根据ID查询pm_cl_callback_quesnaire
	 * @param quesnaireId
	 * @return
	 */
	CallBackQuesnaire queryCbQuesnaire(int quesnaireId);
	/**
	 * 查询pm_cl_quesnaire_result_header根据ID获取quesnaireTemplateHeaderId
	 * @param quesnaireId
	 * @return
	 */
	int queryQuesnaireTemplateID(int quesnaireId);
	/**
	 *  查询pm_cl_callback_quesnaire表ID 根据callbackId /taskId
	 * @param callBack
	 * @return
	 */
	int queryCallBackQuesnaireId(CallBack callBack);
	/**
	 * 更新pm_cl_callback_quesnaire表quesnaireId、quesnaireState根据id
	 * @param callbackQuesnaireId
	 * @param pmClQuesnaireResultHeaderId
	 * @param status
	 */
	void updateCallBackQuesnaire(int callbackQuesnaireId,
			int pmClQuesnaireResultHeaderId, int status);
	/**
	 * 更新pm_cl_callback applyState by id
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
	 * 更新pm_cl_callback
	 * @param callBack
	 */
	void updateCallBack(CallBack callBack);

}
