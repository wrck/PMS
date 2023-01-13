package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * PurchaseLine
 */
public class PurchaseLine2 extends com.dp.plat.pms.extend.d365.entity.PurchaseLine implements Serializable {
	private static final long serialVersionUID = 1L;

	@JSONField(name = "purchId")
	private String purchId;

	@JSONField(name = "lineNum")
	private String lineNum;

	@JSONField(name = "inventTransId")
	private String inventTransId;

	@JSONField(name = "itemId")
	private String itemId;

	@JSONField(name = "purchQty")
	private BigDecimal purchQty;

	@JSONField(name = "purchPrice")
	private BigDecimal purchPrice;

	@JSONField(name = "inventLocationId")
	private String inventLocationId;

	@JSONField(name = "taxItemGroup")
	private String taxItemGroup;

	@JSONField(name = "inventSerialId")
	private String inventSerialId;

	@JSONField(name = "officeCode")
	private String officeCode;

	@JSONField(name = "deliveryDate")
	private String deliveryDate;

	@JSONField(name = "remark")
	private String remark;

	@JSONField(name = "multiDimID")
	private String multiDimID;

	@JSONField(name = "investmentProject")
	private String investmentProject;

	@JSONField(name = "dimBankAccount")
	private String dimBankAccount;

	@JSONField(name = "dimCustomer")
	private String dimCustomer;

	@JSONField(name = "dimVendor")
	private String dimVendor;

	@JSONField(name = "dimEmployee")
	private String dimEmployee;

	@JSONField(name = "dimContract")
	private String dimContract;

	@JSONField(name = "dimDepartment")
	private String dimDepartment;

	@JSONField(name = "dimBU")
	private String dimBU;

	@JSONField(name = "dimProductLine")
	private String dimProductLine;

	@JSONField(name = "dimTerritory")
	private String dimTerritory;

	@JSONField(name = "dimIndustry")
	private String dimIndustry;

	@JSONField(name = "dimMultiDimID")
	private String dimMultiDimID;

	public PurchaseLine2 purchId(String purchId) {
		this.purchId = purchId;
		return this;
	}

	public PurchaseLine2 inventTransId(String inventTransId) {
		this.inventTransId = inventTransId;
		return this;
	}

	public PurchaseLine2 lineNum(String lineNum) {
		this.lineNum = lineNum;
		return this;
	}

	public PurchaseLine2 itemId(String itemId) {
		this.itemId = itemId;
		return this;
	}

	public PurchaseLine2 purchQty(BigDecimal purchQty) {
		this.purchQty = purchQty;
		return this;
	}

	public PurchaseLine2 purchPrice(BigDecimal purchPrice) {
		this.purchPrice = purchPrice;
		return this;
	}

	public PurchaseLine2 inventLocationId(String inventLocationId) {
		this.inventLocationId = inventLocationId;
		return this;
	}

	public PurchaseLine2 taxItemGroup(String taxItemGroup) {
		this.taxItemGroup = taxItemGroup;
		return this;
	}

	public PurchaseLine2 inventSerialId(String inventSerialId) {
		this.inventSerialId = inventSerialId;
		return this;
	}

	public PurchaseLine2 officeCode(String officeCode) {
		this.officeCode = officeCode;
		return this;
	}

	public PurchaseLine2 deliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}

	public PurchaseLine2 remark(String remark) {
		this.remark = remark;
		return this;
	}

	public PurchaseLine2 multiDimID(String multiDimID) {
		this.multiDimID = multiDimID;
		return this;
	}

	public PurchaseLine2 investmentProject(String investmentProject) {
		this.investmentProject = investmentProject;
		return this;
	}

	public PurchaseLine2 dimBankAccount(String dimBankAccount) {
		this.dimBankAccount = dimBankAccount;
		return this;
	}

	public PurchaseLine2 dimCustomer(String dimCustomer) {
		this.dimCustomer = dimCustomer;
		return this;
	}

	public PurchaseLine2 dimVendor(String dimVendor) {
		this.dimVendor = dimVendor;
		return this;
	}

	public PurchaseLine2 dimEmployee(String dimEmployee) {
		this.dimEmployee = dimEmployee;
		return this;
	}

	public PurchaseLine2 dimContract(String dimContract) {
		this.dimContract = dimContract;
		return this;
	}

	public PurchaseLine2 dimDepartment(String dimDepartment) {
		this.dimDepartment = dimDepartment;
		return this;
	}

	public PurchaseLine2 dimBU(String dimBU) {
		this.dimBU = dimBU;
		return this;
	}

	public PurchaseLine2 dimProductLine(String dimProductLine) {
		this.dimProductLine = dimProductLine;
		return this;
	}

	public PurchaseLine2 dimTerritory(String dimTerritory) {
		this.dimTerritory = dimTerritory;
		return this;
	}

	public PurchaseLine2 dimIndustry(String dimIndustry) {
		this.dimIndustry = dimIndustry;
		return this;
	}

	public PurchaseLine2 dimMultiDimID(String dimMultiDimID) {
		this.dimMultiDimID = dimMultiDimID;
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
		PurchaseLine2 purchaseLine = (PurchaseLine2) o;
		return Objects.equals(purchId, purchaseLine.purchId)
				&& Objects.equals(inventTransId, purchaseLine.inventTransId)
				&& Objects.equals(lineNum, purchaseLine.lineNum) && Objects.equals(itemId, purchaseLine.itemId)
				&& Objects.equals(purchQty, purchaseLine.purchQty)
				&& Objects.equals(purchPrice, purchaseLine.purchPrice)
				&& Objects.equals(inventLocationId, purchaseLine.inventLocationId)
				&& Objects.equals(taxItemGroup, purchaseLine.taxItemGroup)
				&& Objects.equals(inventSerialId, purchaseLine.inventSerialId)
				&& Objects.equals(officeCode, purchaseLine.officeCode)
				&& Objects.equals(deliveryDate, purchaseLine.deliveryDate)
				&& Objects.equals(remark, purchaseLine.remark) && Objects.equals(multiDimID, purchaseLine.multiDimID)
				&& Objects.equals(investmentProject, purchaseLine.investmentProject)
				&& Objects.equals(dimBankAccount, purchaseLine.dimBankAccount)
				&& Objects.equals(dimCustomer, purchaseLine.dimCustomer)
				&& Objects.equals(dimVendor, purchaseLine.dimVendor)
				&& Objects.equals(dimEmployee, purchaseLine.dimEmployee)
				&& Objects.equals(dimContract, purchaseLine.dimContract)
				&& Objects.equals(dimDepartment, purchaseLine.dimDepartment)
				&& Objects.equals(dimBU, purchaseLine.dimBU)
				&& Objects.equals(dimProductLine, purchaseLine.dimProductLine)
				&& Objects.equals(dimTerritory, purchaseLine.dimTerritory)
				&& Objects.equals(dimIndustry, purchaseLine.dimIndustry)
				&& Objects.equals(dimMultiDimID, purchaseLine.dimMultiDimID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(purchId, inventTransId, lineNum, itemId, purchQty, purchPrice, inventLocationId,
				taxItemGroup, inventSerialId, officeCode, deliveryDate, remark, multiDimID, investmentProject,
				dimBankAccount, dimCustomer, dimVendor, dimEmployee, dimContract, dimDepartment, dimBU, dimProductLine,
				dimTerritory, dimIndustry, dimMultiDimID);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PurchaseLine {\n");
		sb.append("    purchId: ").append(toIndentedString(purchId)).append("\n");
		sb.append("    inventTransId: ").append(toIndentedString(inventTransId)).append("\n");
		sb.append("    lineNum: ").append(toIndentedString(lineNum)).append("\n");
		sb.append("    lineNum: ").append(toIndentedString(lineNum)).append("\n");
		sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
		sb.append("    purchQty: ").append(toIndentedString(purchQty)).append("\n");
		sb.append("    purchPrice: ").append(toIndentedString(purchPrice)).append("\n");
		sb.append("    inventLocationId: ").append(toIndentedString(inventLocationId)).append("\n");
		sb.append("    taxItemGroup: ").append(toIndentedString(taxItemGroup)).append("\n");
		sb.append("    inventSerialId: ").append(toIndentedString(inventSerialId)).append("\n");
		sb.append("    officeCode: ").append(toIndentedString(officeCode)).append("\n");
		sb.append("    deliveryDate: ").append(toIndentedString(deliveryDate)).append("\n");
		sb.append("    remark: ").append(toIndentedString(remark)).append("\n");
		sb.append("    multiDimID: ").append(toIndentedString(multiDimID)).append("\n");
		sb.append("    investmentProject: ").append(toIndentedString(investmentProject)).append("\n");
		sb.append("    dimBankAccount: ").append(toIndentedString(dimBankAccount)).append("\n");
		sb.append("    dimCustomer: ").append(toIndentedString(dimCustomer)).append("\n");
		sb.append("    dimVendor: ").append(toIndentedString(dimVendor)).append("\n");
		sb.append("    dimEmployee: ").append(toIndentedString(dimEmployee)).append("\n");
		sb.append("    dimContract: ").append(toIndentedString(dimContract)).append("\n");
		sb.append("    dimDepartment: ").append(toIndentedString(dimDepartment)).append("\n");
		sb.append("    dimBU: ").append(toIndentedString(dimBU)).append("\n");
		sb.append("    dimProductLine: ").append(toIndentedString(dimProductLine)).append("\n");
		sb.append("    dimTerritory: ").append(toIndentedString(dimTerritory)).append("\n");
		sb.append("    dimIndustry: ").append(toIndentedString(dimIndustry)).append("\n");
		sb.append("    dimMultiDimID: ").append(toIndentedString(dimMultiDimID)).append("\n");
		sb.append("}");
		return sb.toString();
	}

}
