package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;
import com.dp.plat.pms.extend.d365.entity.Purchase;

/**
 * PurchaseHeader
 */

public class PurchaseHeader extends Purchase implements Serializable {
    private static final long serialVersionUID = 1L;

//	@JSONField(name = "purchId", alternateNames = {"PurchId"})
//	private String purchId;
//
//	@JSONField(name = "vendAccount")
//	private String vendAccount;
//
//	@JSONField(name = "purchName")
//	private String purchName;
//
//	@JSONField(name = "purchPoolId")
//	private String purchPoolId;
//
//	@JSONField(name = "purContract")
//	private String purContract;
//
//	@JSONField(name = "salesContract")
//	private String salesContract;
//
//	@JSONField(name = "contractAmount")
//	private String contractAmount;
//
//	@JSONField(name = "workerPurchPlacer")
//	private String workerPurchPlacer;
//
//	@JSONField(name = "applicant")
//	private String applicant;
//
//	@JSONField(name = "inventLocationId")
//	private String inventLocationId;
//
//	@JSONField(name = "deliveryDate")
//	private String deliveryDate;
//
//	@JSONField(name = "dlvMode")
//	private String dlvMode;
//
//	@JSONField(name = "dlvTerm")
//	private String dlvTerm;
//
//	@JSONField(name = "payment")
//	private String payment;
//
//	@JSONField(name = "paymMode")
//	private String paymMode;
//
//	@JSONField(name = "remark")
//	private String remark;
//
//	@JSONField(name = "otherSysNum")
//	private String otherSysNum;
//
//	@JSONField(name = "projectName")
//	private String projectName;
//
//	@JSONField(name = "projectProgress")
//	private String projectProgress;
//
//	@JSONField(name = "subcontractType")
//	private String subcontractType;
//
//	@JSONField(name = "subcontStartDate")
//	private String subcontStartDate;
//
//	@JSONField(name = "subcontEndDate")
//	private String subcontEndDate;

    public PurchaseHeader purchId(String purchId) {
        this.setPurchId(purchId);
        return this;
    }
    
    @JSONField(name = "purchId", alternateNames = {"PurchId"})
    @Override
    public void setPurchId(String purchId) {
        super.setPurchId(purchId);
    }

    public PurchaseHeader vendAccount(String vendAccount) {
        this.setVendAccount(vendAccount);
        return this;
    }

    public PurchaseHeader purchName(String purchName) {
        this.setPurchName(purchName);
        return this;
    }

    public PurchaseHeader purchPoolId(String purchPoolId) {
        this.setPurchPoolId(purchPoolId);
        return this;
    }

    public PurchaseHeader purContract(String purContract) {
        this.setPurContract(purContract);
        return this;
    }

    public PurchaseHeader salesContract(String salesContract) {
        this.setSalesContract(salesContract);
        return this;
    }

    public PurchaseHeader contractAmount(String contractAmount) {
        this.setContractAmount(contractAmount);
        return this;
    }

    public PurchaseHeader workerPurchPlacer(String workerPurchPlacer) {
        this.setWorkerPurchPlacer(workerPurchPlacer);
        return this;
    }

    public PurchaseHeader applicant(String applicant) {
        this.setApplicant(applicant);
        return this;
    }

    public PurchaseHeader inventLocationId(String inventLocationId) {
        this.setInventLocationId(inventLocationId);
        return this;
    }

    public PurchaseHeader deliveryDate(String deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public PurchaseHeader dlvMode(String dlvMode) {
        this.setDlvMode(dlvMode);
        return this;
    }

    public PurchaseHeader dlvTerm(String dlvTerm) {
        this.setDlvTerm(dlvTerm);
        return this;
    }

    public PurchaseHeader payment(String payment) {
        this.setPayment(payment);
        return this;
    }

    public PurchaseHeader paymMode(String paymMode) {
        this.setPaymMode(paymMode);
        return this;
    }

    public PurchaseHeader remark(String remark) {
        this.setRemark(remark);
        return this;
    }

    public PurchaseHeader otherSysNum(String otherSysNum) {
        this.setOtherSysNum(otherSysNum);
        return this;
    }

    public PurchaseHeader projectName(String projectName) {
        this.setProjectName(projectName);
        return this;
    }

    public PurchaseHeader projectProgress(String projectProgress) {
        this.setProjectProgress(projectProgress);
        return this;
    }

    public PurchaseHeader subcontractType(String subcontractType) {
        this.setSubcontractType(subcontractType);
        return this;
    }

    public PurchaseHeader subcontStartDate(String subcontStartDate) {
        this.setSubcontStartDate(subcontStartDate);
        return this;
    }

    public PurchaseHeader subcontEndDate(String subcontEndDate) {
        this.setSubcontEndDate(subcontEndDate);
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PurchaseHeader purchaseHeader = (PurchaseHeader) o;
        return Objects.equals(this.getPurchId(), purchaseHeader.getPurchId())
                && Objects.equals(this.getVendAccount(), purchaseHeader.getVendAccount())
                && Objects.equals(this.getPurchName(), purchaseHeader.getProjectName())
                && Objects.equals(this.getPurchPoolId(), purchaseHeader.getPurchPoolId())
                && Objects.equals(this.getPurContract(), purchaseHeader.getPurContract())
                && Objects.equals(this.getSalesContract(), purchaseHeader.getSalesContract())
                && Objects.equals(this.getContractAmount(), purchaseHeader.getContractAmount())
                && Objects.equals(this.getWorkerPurchPlacer(), purchaseHeader.getWorkerPurchPlacer())
                && Objects.equals(this.getApplicant(), purchaseHeader.getApplicant())
                && Objects.equals(this.getInventLocationId(), purchaseHeader.getInventLocationId())
                && Objects.equals(this.getDeliveryDate(), purchaseHeader.getDeliveryDate())
                && Objects.equals(this.getDlvMode(), purchaseHeader.getDlvMode())
                && Objects.equals(this.getDlvTerm(), purchaseHeader.getDlvTerm())
                && Objects.equals(this.getPayment(), purchaseHeader.getPayment())
                && Objects.equals(this.getPaymMode(), purchaseHeader.getPaymMode())
                && Objects.equals(this.getRemark(), purchaseHeader.getRemark())
                && Objects.equals(this.getOtherSysNum(), purchaseHeader.getOtherSysNum())
                && Objects.equals(this.getProjectName(), purchaseHeader.getProjectName())
                && Objects.equals(this.getProjectProgress(), purchaseHeader.getProjectProgress())
                && Objects.equals(this.getSubcontractType(), purchaseHeader.getSubcontractType())
                && Objects.equals(this.getSubcontStartDate(), purchaseHeader.getSubcontStartDate())
                && Objects.equals(this.getSubcontEndDate(), purchaseHeader.getSubcontEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPurchId(), this.getVendAccount(), this.getPurchName(), this.getPurchPoolId(),
                this.getPurContract(), this.getSalesContract(), this.getContractAmount(), this.getWorkerPurchPlacer(),
                this.getApplicant(), this.getInventLocationId(), this.getDeliveryDate(), this.getDlvMode(),
                this.getDlvTerm(), this.getPayment(), this.getPaymMode(), this.getRemark(), this.getOtherSysNum(), this.getProjectName(),
                this.getProjectProgress(), this.getSubcontractType(), this.getSubcontStartDate(), this.getSubcontEndDate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PurchaseHeader {\n");
        sb.append("    purchId: ").append(this.toIndentedString(this.getPurchId())).append("\n");
        sb.append("    vendAccount: ").append(this.toIndentedString(this.getVendAccount())).append("\n");
        sb.append("    purchName: ").append(this.toIndentedString(this.getPurchName())).append("\n");
        sb.append("    purchPoolId: ").append(this.toIndentedString(this.getPurchPoolId())).append("\n");
        sb.append("    purContract: ").append(this.toIndentedString(this.getPurContract())).append("\n");
        sb.append("    salesContract: ").append(this.toIndentedString(this.getSalesContract())).append("\n");
        sb.append("    contractAmount: ").append(this.toIndentedString(this.getContractAmount())).append("\n");
        sb.append("    workerPurchPlacer: ").append(this.toIndentedString(this.getWorkerPurchPlacer())).append("\n");
        sb.append("    applicant: ").append(this.toIndentedString(this.getApplicant())).append("\n");
        sb.append("    inventLocationId: ").append(this.toIndentedString(this.getInventLocationId())).append("\n");
        sb.append("    deliveryDate: ").append(this.toIndentedString(this.getDeliveryDate())).append("\n");
        sb.append("    dlvMode: ").append(this.toIndentedString(this.getDlvMode())).append("\n");
        sb.append("    dlvTerm: ").append(this.toIndentedString(this.getDlvTerm())).append("\n");
        sb.append("    payment: ").append(this.toIndentedString(this.getPayment())).append("\n");
        sb.append("    paymMode: ").append(this.toIndentedString(this.getPaymMode())).append("\n");
        sb.append("    remark: ").append(this.toIndentedString(this.getRemark())).append("\n");
        sb.append("    otherSysNum: ").append(this.toIndentedString(this.getOtherSysNum())).append("\n");
        sb.append("    projectName: ").append(this.toIndentedString(this.getProjectName())).append("\n");
        sb.append("    projectProgress: ").append(this.toIndentedString(this.getProjectProgress())).append("\n");
        sb.append("    subcontractType: ").append(this.toIndentedString(this.getSubcontractType())).append("\n");
        sb.append("    subcontStartDate: ").append(this.toIndentedString(this.getSubcontStartDate())).append("\n");
        sb.append("    subcontEndDate: ").append(this.toIndentedString(this.getSubcontEndDate())).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
