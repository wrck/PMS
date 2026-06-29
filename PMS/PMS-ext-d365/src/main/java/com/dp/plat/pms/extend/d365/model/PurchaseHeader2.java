package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;
import com.dp.plat.pms.extend.d365.entity.Purchase;

/**
 * PurchaseHeader
 */

public class PurchaseHeader2 extends Purchase implements Serializable {
	private static final long serialVersionUID = 1L;

	@JSONField(name = "purchId", alternateNames = {"PurchId"})
	private String purchId;

	@JSONField(name = "vendAccount")
	private String vendAccount;

	@JSONField(name = "purchName")
	private String purchName;

	@JSONField(name = "purchPoolId")
	private String purchPoolId;

	@JSONField(name = "purContract")
	private String purContract;

	@JSONField(name = "salesContract")
	private String salesContract;

	@JSONField(name = "contractAmount")
	private String contractAmount;

	@JSONField(name = "workerPurchPlacer")
	private String workerPurchPlacer;

	@JSONField(name = "applicant")
	private String applicant;

	@JSONField(name = "inventLocationId")
	private String inventLocationId;

	@JSONField(name = "deliveryDate")
	private String deliveryDate;

	@JSONField(name = "dlvMode")
	private String dlvMode;

	@JSONField(name = "dlvTerm")
	private String dlvTerm;

	@JSONField(name = "payment")
	private String payment;

	@JSONField(name = "paymMode")
	private String paymMode;

	@JSONField(name = "remark")
	private String remark;

	@JSONField(name = "otherSysNum")
	private String otherSysNum;

	@JSONField(name = "projectName")
	private String projectName;

	@JSONField(name = "projectProgress")
	private String projectProgress;

	@JSONField(name = "subcontractType")
	private String subcontractType;

	@JSONField(name = "subcontStartDate")
	private String subcontStartDate;

	@JSONField(name = "subcontEndDate")
	private String subcontEndDate;

	public PurchaseHeader2 purchId(String purchId) {
		this.purchId = purchId;
		return this;
	}

	public PurchaseHeader2 vendAccount(String vendAccount) {
		this.vendAccount = vendAccount;
		return this;
	}

	public PurchaseHeader2 purchName(String purchName) {
		this.purchName = purchName;
		return this;
	}

	public PurchaseHeader2 purchPoolId(String purchPoolId) {
		this.purchPoolId = purchPoolId;
		return this;
	}

	public PurchaseHeader2 purContract(String purContract) {
		this.purContract = purContract;
		return this;
	}

	public PurchaseHeader2 salesContract(String salesContract) {
		this.salesContract = salesContract;
		return this;
	}

	public PurchaseHeader2 contractAmount(String contractAmount) {
		this.contractAmount = contractAmount;
		return this;
	}

	public PurchaseHeader2 workerPurchPlacer(String workerPurchPlacer) {
		this.workerPurchPlacer = workerPurchPlacer;
		return this;
	}

	public PurchaseHeader2 applicant(String applicant) {
		this.applicant = applicant;
		return this;
	}

	public PurchaseHeader2 inventLocationId(String inventLocationId) {
		this.inventLocationId = inventLocationId;
		return this;
	}

	public PurchaseHeader2 deliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}

	public PurchaseHeader2 dlvMode(String dlvMode) {
		this.dlvMode = dlvMode;
		return this;
	}

	public PurchaseHeader2 dlvTerm(String dlvTerm) {
		this.dlvTerm = dlvTerm;
		return this;
	}

	public PurchaseHeader2 payment(String payment) {
		this.payment = payment;
		return this;
	}

	public PurchaseHeader2 paymMode(String paymMode) {
		this.paymMode = paymMode;
		return this;
	}

	public PurchaseHeader2 remark(String remark) {
		this.remark = remark;
		return this;
	}

	public PurchaseHeader2 otherSysNum(String otherSysNum) {
		this.otherSysNum = otherSysNum;
		return this;
	}

	public PurchaseHeader2 projectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public PurchaseHeader2 projectProgress(String projectProgress) {
		this.projectProgress = projectProgress;
		return this;
	}

	public PurchaseHeader2 subcontractType(String subcontractType) {
		this.subcontractType = subcontractType;
		return this;
	}

	public PurchaseHeader2 subcontStartDate(String subcontStartDate) {
		this.subcontStartDate = subcontStartDate;
		return this;
	}

	public PurchaseHeader2 subcontEndDate(String subcontEndDate) {
		this.subcontEndDate = subcontEndDate;
		return this;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PurchaseHeader2 purchaseHeader = (PurchaseHeader2) o;
		return Objects.equals(purchId, purchaseHeader.purchId)
                && Objects.equals(vendAccount, purchaseHeader.vendAccount)
				&& Objects.equals(purchName, purchaseHeader.purchName)
				&& Objects.equals(purchPoolId, purchaseHeader.purchPoolId)
				&& Objects.equals(purContract, purchaseHeader.purContract)
				&& Objects.equals(salesContract, purchaseHeader.salesContract)
				&& Objects.equals(contractAmount, purchaseHeader.contractAmount)
				&& Objects.equals(workerPurchPlacer, purchaseHeader.workerPurchPlacer)
				&& Objects.equals(applicant, purchaseHeader.applicant)
				&& Objects.equals(inventLocationId, purchaseHeader.inventLocationId)
				&& Objects.equals(deliveryDate, purchaseHeader.deliveryDate)
				&& Objects.equals(dlvMode, purchaseHeader.dlvMode) && Objects.equals(dlvTerm, purchaseHeader.dlvTerm)
				&& Objects.equals(payment, purchaseHeader.payment) && Objects.equals(paymMode, purchaseHeader.paymMode)
				&& Objects.equals(remark, purchaseHeader.remark)
				&& Objects.equals(otherSysNum, purchaseHeader.otherSysNum)
				&& Objects.equals(projectName, purchaseHeader.projectName)
				&& Objects.equals(projectProgress, purchaseHeader.projectProgress)
				&& Objects.equals(subcontractType, purchaseHeader.subcontractType)
				&& Objects.equals(subcontStartDate, purchaseHeader.subcontStartDate)
				&& Objects.equals(subcontEndDate, purchaseHeader.subcontEndDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(purchId, vendAccount, purchName, purchPoolId, purContract, salesContract, contractAmount,
				workerPurchPlacer, applicant, inventLocationId, deliveryDate, dlvMode, dlvTerm, payment, paymMode,
				remark, otherSysNum, projectName, projectProgress, subcontractType, subcontStartDate, subcontEndDate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PurchaseHeader {\n");
		sb.append("    purchId: ").append(toIndentedString(purchId)).append("\n");
		sb.append("    vendAccount: ").append(toIndentedString(vendAccount)).append("\n");
		sb.append("    purchName: ").append(toIndentedString(purchName)).append("\n");
		sb.append("    purchPoolId: ").append(toIndentedString(purchPoolId)).append("\n");
		sb.append("    purContract: ").append(toIndentedString(purContract)).append("\n");
		sb.append("    salesContract: ").append(toIndentedString(salesContract)).append("\n");
		sb.append("    contractAmount: ").append(toIndentedString(contractAmount)).append("\n");
		sb.append("    workerPurchPlacer: ").append(toIndentedString(workerPurchPlacer)).append("\n");
		sb.append("    applicant: ").append(toIndentedString(applicant)).append("\n");
		sb.append("    inventLocationId: ").append(toIndentedString(inventLocationId)).append("\n");
		sb.append("    deliveryDate: ").append(toIndentedString(deliveryDate)).append("\n");
		sb.append("    dlvMode: ").append(toIndentedString(dlvMode)).append("\n");
		sb.append("    dlvTerm: ").append(toIndentedString(dlvTerm)).append("\n");
		sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
		sb.append("    paymMode: ").append(toIndentedString(paymMode)).append("\n");
		sb.append("    remark: ").append(toIndentedString(remark)).append("\n");
		sb.append("    otherSysNum: ").append(toIndentedString(otherSysNum)).append("\n");
		sb.append("    projectName: ").append(toIndentedString(projectName)).append("\n");
		sb.append("    projectProgress: ").append(toIndentedString(projectProgress)).append("\n");
		sb.append("    subcontractType: ").append(toIndentedString(subcontractType)).append("\n");
		sb.append("    subcontStartDate: ").append(toIndentedString(subcontStartDate)).append("\n");
		sb.append("    subcontEndDate: ").append(toIndentedString(subcontEndDate)).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
