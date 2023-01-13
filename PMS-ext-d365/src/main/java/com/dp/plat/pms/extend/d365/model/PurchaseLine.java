package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * PurchaseLine
 */
public class PurchaseLine extends com.dp.plat.pms.extend.d365.entity.PurchaseLine implements Serializable {
    private static final long serialVersionUID = 1L;

//	@JSONField(name = "purchId")
//	private String purchId;
//
//	@JSONField(name = "lineNum")
//	private String lineNum;
//
//	@JSONField(name = "inventTransId")
//	private String inventTransId;
//
//	@JSONField(name = "itemId")
//	private String itemId;
//
//	@JSONField(name = "purchQty")
//	private BigDecimal purchQty;
//
//	@JSONField(name = "purchPrice")
//	private BigDecimal purchPrice;
//
//	@JSONField(name = "inventLocationId")
//	private String inventLocationId;
//
//	@JSONField(name = "taxItemGroup")
//	private String taxItemGroup;
//
//	@JSONField(name = "inventSerialId")
//	private String inventSerialId;
//
//	@JSONField(name = "officeCode")
//	private String officeCode;
//
//	@JSONField(name = "deliveryDate")
//	private String deliveryDate;
//
//	@JSONField(name = "remark")
//	private String remark;
//
//	@JSONField(name = "multiDimID")
//	private String multiDimID;
//
//	@JSONField(name = "investmentProject")
//	private String investmentProject;
//
//	@JSONField(name = "dimBankAccount")
//	private String dimBankAccount;
//
//	@JSONField(name = "dimCustomer")
//	private String dimCustomer;
//
//	@JSONField(name = "dimVendor")
//	private String dimVendor;
//
//	@JSONField(name = "dimEmployee")
//	private String dimEmployee;
//
//	@JSONField(name = "dimContract")
//	private String dimContract;
//
//	@JSONField(name = "dimDepartment")
//	private String dimDepartment;
//
//	@JSONField(name = "dimBU")
//	private String dimBU;
//
//	@JSONField(name = "dimProductLine")
//	private String dimProductLine;
//
//	@JSONField(name = "dimTerritory")
//	private String dimTerritory;
//
//	@JSONField(name = "dimIndustry")
//	private String dimIndustry;
//
//	@JSONField(name = "dimMultiDimID")
//	private String dimMultiDimID;

    public PurchaseLine purchId(String purchId) {
        this.setPurchId(purchId);
        return this;
    }

    public PurchaseLine inventTransId(String inventTransId) {
        this.setInventTransId(inventTransId);
        return this;
    }

    public PurchaseLine lineNum(String lineNum) {
        this.setLineNum(lineNum);
        return this;
    }

    public PurchaseLine itemId(String itemId) {
        this.setItemId(itemId);
        return this;
    }

    public PurchaseLine purchQty(BigDecimal purchQty) {
        this.setPurchQty(purchQty);
        return this;
    }

    public PurchaseLine purchPrice(BigDecimal purchPrice) {
        this.setPurchPrice(purchPrice);
        return this;
    }

    public PurchaseLine inventLocationId(String inventLocationId) {
        this.setInventLocationId(inventLocationId);
        return this;
    }

    public PurchaseLine taxItemGroup(String taxItemGroup) {
        this.setTaxItemGroup(taxItemGroup);
        return this;
    }

    public PurchaseLine inventSerialId(String inventSerialId) {
        this.setInventSerialId(inventSerialId);
        return this;
    }

    public PurchaseLine officeCode(String officeCode) {
        this.setOfficeCode(officeCode);
        return this;
    }

    public PurchaseLine deliveryDate(String deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public PurchaseLine remark(String remark) {
        this.setRemark(remark);
        return this;
    }

    public PurchaseLine multiDimID(String multiDimID) {
        this.setMultiDimID(multiDimID);
        return this;
    }

    public PurchaseLine investmentProject(String investmentProject) {
        this.setInvestmentProject(investmentProject);
        return this;
    }

    public PurchaseLine dimBankAccount(String dimBankAccount) {
        this.setDimBankAccount(dimBankAccount);
        return this;
    }

    public PurchaseLine dimCustomer(String dimCustomer) {
        this.setDimCustomer(dimCustomer);
        return this;
    }

    public PurchaseLine dimVendor(String dimVendor) {
        this.setDimVendor(dimVendor);
        return this;
    }

    public PurchaseLine dimEmployee(String dimEmployee) {
        this.setDimEmployee(dimEmployee);
        return this;
    }

    public PurchaseLine dimContract(String dimContract) {
        this.setDimContract(dimContract);
        return this;
    }

    public PurchaseLine dimDepartment(String dimDepartment) {
        this.setDimDepartment(dimDepartment);
        return this;
    }

    public PurchaseLine dimBU(String dimBU) {
        this.setDimBU(dimBU);
        return this;
    }

    public PurchaseLine dimProductLine(String dimProductLine) {
        this.setDimProductLine(dimProductLine);
        return this;
    }

    public PurchaseLine dimTerritory(String dimTerritory) {
        this.setDimTerritory(dimTerritory);
        return this;
    }

    public PurchaseLine dimIndustry(String dimIndustry) {
        this.setDimIndustry(dimIndustry);
        return this;
    }

    public PurchaseLine dimMultiDimID(String dimMultiDimID) {
        this.setDimMultiDimID(dimMultiDimID);
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
        PurchaseLine purchaseLine = (PurchaseLine) o;
        return Objects.equals(this.getPurchId(), purchaseLine.getPurchId())
                && Objects.equals(this.getInventTransId(), purchaseLine.getInventTransId())
                && Objects.equals(this.getLineNum(), purchaseLine.getLineNum())
                && Objects.equals(this.getItemId(), purchaseLine.getItemId())
                && Objects.equals(this.getPurchQty(), purchaseLine.getPurchQty())
                && Objects.equals(this.getPurchPrice(), purchaseLine.getPurchPrice())
                && Objects.equals(this.getInventLocationId(), purchaseLine.getInventLocationId())
                && Objects.equals(this.getTaxItemGroup(), purchaseLine.getTaxItemGroup())
                && Objects.equals(this.getInventSerialId(), purchaseLine.getInventSerialId())
                && Objects.equals(this.getOfficeCode(), purchaseLine.getOfficeCode())
                && Objects.equals(this.getDeliveryDate(), purchaseLine.getDeliveryDate())
                && Objects.equals(this.getRemark(), purchaseLine.getRemark())
                && Objects.equals(this.getMultiDimID(), purchaseLine.getMultiDimID())
                && Objects.equals(this.getInvestmentProject(), purchaseLine.getInvestmentProject())
                && Objects.equals(this.getDimBankAccount(), purchaseLine.getDimBankAccount())
                && Objects.equals(this.getDimCustomer(), purchaseLine.getDimCustomer())
                && Objects.equals(this.getDimVendor(), purchaseLine.getDimVendor())
                && Objects.equals(this.getDimEmployee(), purchaseLine.getDimEmployee())
                && Objects.equals(this.getDimContract(), purchaseLine.getDimContract())
                && Objects.equals(this.getDimDepartment(), purchaseLine.getDimDepartment())
                && Objects.equals(this.getDimBU(), purchaseLine.getDimBU())
                && Objects.equals(this.getDimProductLine(), purchaseLine.getDimProductLine())
                && Objects.equals(this.getDimTerritory(), purchaseLine.getDimTerritory())
                && Objects.equals(this.getDimIndustry(), purchaseLine.getDimIndustry())
                && Objects.equals(this.getDimMultiDimID(), purchaseLine.getDimMultiDimID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPurchId(), this.getInventTransId(), this.getLineNum(), this.getItemId(),
                this.getPurchQty(), this.getPurchPrice(), this.getInventLocationId(), this.getTaxItemGroup(),
                this.getInventSerialId(), this.getOfficeCode(), this.getDeliveryDate(), this.getRemark(),
                this.getMultiDimID(), this.getInvestmentProject(), this.getDimBankAccount(), this.getDimCustomer(),
                this.getDimVendor(), this.getDimEmployee(), this.getDimContract(), this.getDimDepartment(),
                this.getDimBU(), this.getDimProductLine(), this.getDimTerritory(), this.getDimIndustry(),
                this.getDimMultiDimID());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PurchaseLine {\n");
        sb.append("    purchId: ").append(this.toIndentedString(this.getPurchId())).append("\n");
        sb.append("    inventTransId: ").append(this.toIndentedString(this.getInventTransId())).append("\n");
        sb.append("    lineNum: ").append(this.toIndentedString(this.getLineNum())).append("\n");
        sb.append("    itemId: ").append(this.toIndentedString(this.getItemId())).append("\n");
        sb.append("    purchQty: ").append(this.toIndentedString(this.getPurchQty())).append("\n");
        sb.append("    purchPrice: ").append(this.toIndentedString(this.getPurchPrice())).append("\n");
        sb.append("    inventLocationId: ").append(this.toIndentedString(this.getInventLocationId())).append("\n");
        sb.append("    taxItemGroup: ").append(this.toIndentedString(this.getTaxItemGroup())).append("\n");
        sb.append("    inventSerialId: ").append(this.toIndentedString(this.getInventSerialId())).append("\n");
        sb.append("    officeCode: ").append(this.toIndentedString(this.getOfficeCode())).append("\n");
        sb.append("    deliveryDate: ").append(this.toIndentedString(this.getDeliveryDate())).append("\n");
        sb.append("    remark: ").append(this.toIndentedString(this.getRemark())).append("\n");
        sb.append("    multiDimID: ").append(this.toIndentedString(this.getMultiDimID())).append("\n");
        sb.append("    investmentProject: ").append(this.toIndentedString(this.getInvestmentProject())).append("\n");
        sb.append("    dimBankAccount: ").append(this.toIndentedString(this.getDimBankAccount())).append("\n");
        sb.append("    dimCustomer: ").append(this.toIndentedString(this.getDimCustomer())).append("\n");
        sb.append("    dimVendor: ").append(this.toIndentedString(this.getDimVendor())).append("\n");
        sb.append("    dimEmployee: ").append(this.toIndentedString(this.getDimEmployee())).append("\n");
        sb.append("    dimContract: ").append(this.toIndentedString(this.getDimContract())).append("\n");
        sb.append("    dimDepartment: ").append(this.toIndentedString(this.getDimDepartment())).append("\n");
        sb.append("    dimBU: ").append(this.toIndentedString(this.getDimBU())).append("\n");
        sb.append("    dimProductLine: ").append(this.toIndentedString(this.getDimProductLine())).append("\n");
        sb.append("    dimTerritory: ").append(this.toIndentedString(this.getDimTerritory())).append("\n");
        sb.append("    dimIndustry: ").append(this.toIndentedString(this.getDimIndustry())).append("\n");
        sb.append("    dimMultiDimID: ").append(this.toIndentedString(this.getDimMultiDimID())).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
