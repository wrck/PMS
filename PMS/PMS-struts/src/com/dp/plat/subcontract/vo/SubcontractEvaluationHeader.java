package com.dp.plat.subcontract.vo;

import com.dp.plat.data.bean.PmClEvaluationHeader;

public class SubcontractEvaluationHeader extends PmClEvaluationHeader {
	
	private Integer subcontractId;
	private String subcontractName;
	private String subcontractNo;
	private String procInstId;

	public Integer getSubcontractId() {
		return subcontractId;
	}

	public void setSubcontractId(Integer subcontractId) {
		this.subcontractId = subcontractId;
	}

	public String getSubcontractName() {
		return subcontractName;
	}

	public void setSubcontractName(String subcontractName) {
		this.subcontractName = subcontractName;
	}

	public String getSubcontractNo() {
		return subcontractNo;
	}

	public void setSubcontractNo(String subcontractNo) {
		this.subcontractNo = subcontractNo;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

}
