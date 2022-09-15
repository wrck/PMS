package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * PurchaseRequestBody
 */
public class PurchaseRequestBody extends RequestBody implements Serializable {
	private static final long serialVersionUID = 1L;

	@JSONField(name = "purchTable")
	private PurchaseHeader purchTable;

	@JSONField(name = "purchLine")
	private List<PurchaseLine> purchLine = new ArrayList<>();

	public PurchaseRequestBody dataAreaId(String dataAreaId) {
		this.dataAreaId = dataAreaId;
		return this;
	}

	public PurchaseRequestBody purchTable(PurchaseHeader purchTable) {
		this.purchTable = purchTable;
		return this;
	}

	/**
	 * Get purchTable
	 * 
	 * @return purchTable
	 */
	public PurchaseHeader getPurchTable() {
		return purchTable;
	}

	public void setPurchTable(PurchaseHeader purchTable) {
		this.purchTable = purchTable;
	}

	public PurchaseRequestBody purchLine(java.util.List<PurchaseLine> purchLine) {
		this.purchLine = purchLine;
		return this;
	}

	public PurchaseRequestBody addPurchLineItem(PurchaseLine purchLineItem) {
		this.purchLine.add(purchLineItem);
		return this;
	}

	/**
	 * Get purchLine
	 * 
	 * @return purchLine
	 */
	public java.util.List<PurchaseLine> getPurchLine() {
		return purchLine;
	}

	public void setPurchLine(java.util.List<PurchaseLine> purchLine) {
		this.purchLine = purchLine;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PurchaseRequestBody purchaseRequestBody = (PurchaseRequestBody) o;
		return Objects.equals(this.dataAreaId, purchaseRequestBody.dataAreaId)
				&& Objects.equals(this.purchTable, purchaseRequestBody.purchTable)
				&& Objects.equals(this.purchLine, purchaseRequestBody.purchLine);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataAreaId, purchTable, purchLine);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PurchaseRequestBody {\n");
		sb.append("    dataAreaId: ").append(toIndentedString(dataAreaId)).append("\n");
		sb.append("    purchTable: ").append(toIndentedString(purchTable)).append("\n");
		sb.append("    purchLine: ").append(toIndentedString(purchLine)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
