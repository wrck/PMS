package com.dp.plat.data.bean;

import com.dp.plat.data.activity.ActComment;
/**
 * 回访流程审批意见列表相关字段
 * @author admin
 *
 */
public class CallBackComment extends ActComment {
	private int quesnaireId;//问卷ID

	public int getQuesnaireId() {
		return quesnaireId;
	}

	public void setQuesnaireId(int quesnaireId) {
		this.quesnaireId = quesnaireId;
	}
}
