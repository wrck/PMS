package com.dp.plat.subcontract.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.util.UserUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SubcontractProject {
	private Integer id;

	// 转包名称
	private String subcontractName;

	// 合同号
	private String contractNos;

	// 项目projectIds
	private String projectIds;

	// 转包类型
	private Integer type;

	// 转包状态
	private Integer state;
	
	// 回访状态
	private Integer callbackState;

	// 服务商表ID
	private Integer facilitatorId;

	// 服务商名
	private String facilitatorName;

	// 服务商开户地址
	private String bankInfo;

	// 服务商收款账户
	private String bankAccount;

	// 办事处部门
	private String officeCode;

	// 收益部门
	private String profitDepCode;

	// 转包合同号
	private String subcontractNo;

	// 是否计提
	private Boolean isAccrued;
	
	// 是否提供发票
	private Boolean isInvoiced;

	// 转包价
	private String subcontractAmount;
	
	// 转包原因
	private String reason;

	// 备注
	private String remark;

	// 有效开始时间
	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date effectiveFrom;

	// 有效结束时间
	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date effectiveTo;

	// 有效结束时间
	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date zrApproveTime;
		
	private String createBy;

	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date createTime;

	private String updateBy;

	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date updateTime;

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取转包名称
	 *
	 * @return subcontractName - 转包名称
	 */
	public String getSubcontractName() {
		return subcontractName;
	}

	/**
	 * 设置转包名称
	 *
	 * @param subcontractName
	 *            转包名称
	 */
	public void setSubcontractName(String subcontractName) {
		this.subcontractName = subcontractName;
	}

	/**
	 * 获取合同号
	 *
	 * @return contractNos - 合同号
	 */
	public String getContractNos() {
		return contractNos;
	}

	/**
	 * 设置合同号
	 *
	 * @param contractNos
	 *            合同号
	 */
	public void setContractNos(String contractNos) {
		this.contractNos = contractNos;
	}

	/**
	 * 设置项目projectIds
	 *
	 * @param projectIds
	 *            项目projectIds
	 */
	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}

	/**
	 * 获取项目projectIds
	 *
	 * @return projectIds - 项目projectIds
	 */
	public String getProjectIds() {
		return projectIds;
	}

	/**
	 * 获取转包类型
	 *
	 * @return type - 转包类型
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 设置转包类型
	 *
	 * @param type
	 *            转包类型
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 获取转包状态
	 *
	 * @return state - 转包状态
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * 设置转包状态
	 *
	 * @param state
	 *            转包状态
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * 获取回访状态
	 * 
	 * @return callbackState - 回访状态
	 */
	public Integer getCallbackState() {
		return callbackState;
	}

	/**
	 * 设置回访状态
	 *
	 * @param callbackState
	 *            回访状态
	 */
	public void setCallbackState(Integer callbackState) {
		this.callbackState = callbackState;
	}

	/**
	 * 获取服务商表ID
	 * 
	 * @return facilitatorId - 服务商表ID
	 */
	public Integer getFacilitatorId() {
		return facilitatorId;
	}

	/**
	 * 设置服务商表ID
	 *
	 * @param facilitatorId
	 *            服务商表ID
	 */
	public void setFacilitatorId(Integer facilitatorId) {
		this.facilitatorId = facilitatorId;
	}

	/**
	 * 获取服务商名
	 * 
	 * @return facilitatorName - 服务商名
	 */
	public String getFacilitatorName() {
		return facilitatorName;
	}

	/**
	 * 设置服务商名
	 * 
	 * @param facilitatorName
	 *            服务商名
	 */
	public void setFacilitatorName(String facilitatorName) {
		this.facilitatorName = facilitatorName;
	}

	/**
	 * 获取服务商开户地址
	 * 
	 * @return bankInfo - 服务商开户地址
	 */
	public String getBankInfo() {
		return bankInfo;
	}

	/**
	 * 设置服务商开户地址
	 * 
	 * @param bankInfo
	 *            服务商开户地址
	 */
	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}

	/**
	 * 获取服务商收款账户
	 * 
	 * @return bankAccount - 服务商收款账户
	 */
	public String getBankAccount() {
		return bankAccount;
	}

	/**
	 * 设置服务商收款账户
	 * 
	 * @param bankAccount
	 *            服务商收款账户
	 */
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	/**
	 * 获取办事处部门
	 *
	 * @return officeCode - 办事处部门
	 */
	public String getOfficeCode() {
		return officeCode;
	}

	/**
	 * 设置办事处部门
	 *
	 * @param officeCode
	 *            办事处部门
	 */
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	/**
	 * 获取收益部门
	 *
	 * @return profitDepCode - 收益部门
	 */
	public String getProfitDepCode() {
		return profitDepCode;
	}

	/**
	 * 获取收益部门，将用服的转换为市场的部门编码
	 *
	 * @return profitDepCode - 转换后的收益部门
	 */
	public String getProfitDepCode2Office() {
		// 用服部门转换为市场部门，查找对应的主任
		String profitDepCode = this.profitDepCode;
//		if (StringUtils.isNotBlank(profitDepCode) && profitDepCode.length() > 6) {
//			profitDepCode = profitDepCode.substring(0, 6);
//		}
//		if (StringUtils.isNotBlank(profitDepCode) && profitDepCode.startsWith("31")) {
//			profitDepCode = profitDepCode.replaceFirst("31", "16");
//		}
		profitDepCode = UserUtil.transferDepNo(profitDepCode, 1);
		return profitDepCode;
	}
	
	/**
	 * 设置收益部门
	 *
	 * @param profitDepCode
	 *            收益部门
	 */
	public void setProfitDepCode(String profitDepCode) {
		this.profitDepCode = profitDepCode;
	}

	/**
	 * 获取转包合同号
	 *
	 * @return subcontractNo - 转包合同号
	 */
	public String getSubcontractNo() {
		return subcontractNo;
	}

	/**
	 * 设置转包合同号
	 *
	 * @param subcontractNo
	 *            转包合同号
	 */
	public void setSubcontractNo(String subcontractNo) {
		this.subcontractNo = subcontractNo;
	}

	/**
	 * 获取是否计提
	 *
	 * @return isAccrued - 是否计提
	 */
	public Boolean getIsAccrued() {
		return isAccrued;
	}

	/**
	 * 设置是否计提
	 *
	 * @param isAccrued
	 *            是否计提
	 */
	public void setIsAccrued(Boolean isAccrued) {
		this.isAccrued = isAccrued;
	}

	/**
	 * 获取是否提供发票
	 *
	 * @return isInvoiced - 是否提供发票
	 */
	public Boolean getIsInvoiced() {
		return isInvoiced;
	}

	/**
	 * 设置是否提供发票
	 *
	 * @param isInvoiced
	 *            是否提供发票
	 */
	public void setIsInvoiced(Boolean isInvoiced) {
		this.isInvoiced = isInvoiced;
	}

	/**
	 * 获取转包金额
	 *
	 * @return reason - 转包金额
	 */
	public String getSubcontractAmount() {
		return subcontractAmount;
	}
	
	/**
	 * 设置转包金额
	 *
	 * @param reason
	 *            转包金额
	 */
	public void setSubcontractAmount(String subcontractAmount) {
		this.subcontractAmount = subcontractAmount;
	}
	
	/**
	 * 获取转包原因
	 *
	 * @return reason - 转包原因
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * 设置转包原因
	 *
	 * @param reason
	 *            转包原因
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * 获取备注
	 *
	 * @return remark - 备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置备注
	 *
	 * @param remark
	 *            备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取有效开始时间
	 *
	 * @return effectiveFrom - 有效开始时间
	 */
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	/**
	 * 设置有效开始时间
	 *
	 * @param effectiveFrom
	 *            有效开始时间
	 */
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	/**
	 * 获取有效结束时间
	 *
	 * @return effectiveTo - 有效结束时间
	 */
	public Date getEffectiveTo() {
		return effectiveTo;
	}

	/**
	 * 设置有效结束时间
	 *
	 * @param effectiveTo
	 *            有效结束时间
	 */
	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	/**
	 * 获取最新主任审批时间
	 *
	 * @return zrApproveTime - 最新主任审批时间
	 */
	public Date getZrApproveTime() {
		return zrApproveTime;
	}
	
	/**
	 * 设置最新主任审批时间
	 *
	 * @param zrApproveTime
	 *            最新主任审批时间
	 */
	public void setZrApproveTime(Date zrApproveTime) {
		this.zrApproveTime = zrApproveTime;
	}

	/**
	 * @return createBy
	 */
	public String getCreateBy() {
		return createBy;
	}

	/**
	 * @param createBy
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	/**
	 * @return createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return updateBy
	 */
	public String getUpdateBy() {
		return updateBy;
	}

	/**
	 * @param updateBy
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	/**
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
