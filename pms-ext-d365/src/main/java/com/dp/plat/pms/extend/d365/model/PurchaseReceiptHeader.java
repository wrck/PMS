package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;
import com.dp.plat.pms.extend.d365.entity.PurchaseReceipt;

/**
 * PurchaseReceiptHeader
 */
public class PurchaseReceiptHeader extends PurchaseReceipt implements Serializable {
    private static final long serialVersionUID = 1L;

//	@JSONField(name = "dataAreaId")
//	private String dataAreaId;
//
//	@JSONField(name = "deliveryDate")
//	private String deliveryDate;
//
//	@JSONField(name = "documentDate")
//	private String documentDate;
//
//	@JSONField(name = "packingSlipId")
//	private String packingSlipId;
//
//	@JSONField(name = "packingSlipRemark")
//	private String packingSlipRemark;
//
//	@JSONField(name = "projectProgress")
//	private String projectProgress;

    @JSONField(name = "lines")
    private List<PurchaseReceiptLine> lines = new ArrayList<>();

    public PurchaseReceiptHeader dataAreaId(String dataAreaId) {
        this.setDataAreaId(dataAreaId);
        return this;
    }

    public PurchaseReceiptHeader deliveryDate(String deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public PurchaseReceiptHeader documentDate(String documentDate) {
        this.setDocumentDate(documentDate);
        return this;
    }

    public PurchaseReceiptHeader packingSlipId(String packingSlipId) {
        this.setPackingSlipId(packingSlipId);
        return this;
    }

    public PurchaseReceiptHeader packingSlipRemark(String packingSlipRemark) {
        this.setPackingSlipRemark(packingSlipRemark);
        return this;
    }

    public PurchaseReceiptHeader projectProgress(String projectProgress) {
        this.setProjectProgress(projectProgress);
        return this;
    }

    public PurchaseReceiptHeader lines(List<PurchaseReceiptLine> lines) {
        this.lines = lines;
        return this;
    }

    public PurchaseReceiptHeader addLinesItem(PurchaseReceiptLine linesItem) {
        this.lines.add(linesItem);
        return this;
    }

    public List<PurchaseReceiptLine> getLines() {
        return this.lines;
    }

    public void setLines(List<PurchaseReceiptLine> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PurchaseReceiptHeader purchaseReceiptHeader = (PurchaseReceiptHeader) o;
        return Objects.equals(this.getDataAreaId(), purchaseReceiptHeader.getDataAreaId())
                && Objects.equals(this.getDeliveryDate(), purchaseReceiptHeader.getDeliveryDate())
                && Objects.equals(this.getDocumentDate(), purchaseReceiptHeader.getDocumentDate())
                && Objects.equals(this.getPackingSlipId(), purchaseReceiptHeader.getPackingSlipId())
                && Objects.equals(this.getPackingSlipRemark(), purchaseReceiptHeader.getPackingSlipRemark())
                && Objects.equals(this.getProjectProgress(), purchaseReceiptHeader.getProjectProgress())
                && Objects.equals(this.lines, purchaseReceiptHeader.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDataAreaId(), this.getDeliveryDate(), this.getDocumentDate(),
                this.getPackingSlipId(), this.getPackingSlipRemark(), this.getProjectProgress(), this.lines);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PurchaseReceiptHeader {\n");
        sb.append("    dataAreaId: ").append(this.toIndentedString(this.getDataAreaId())).append("\n");
        sb.append("    deliveryDate: ").append(this.toIndentedString(this.getDeliveryDate())).append("\n");
        sb.append("    documentDate: ").append(this.toIndentedString(this.getDocumentDate())).append("\n");
        sb.append("    packingSlipId: ").append(this.toIndentedString(this.getPackingSlipId())).append("\n");
        sb.append("    packingSlipRemark: ").append(this.toIndentedString(this.getPackingSlipRemark())).append("\n");
        sb.append("    projectProgress: ").append(this.toIndentedString(this.getProjectProgress())).append("\n");
        sb.append("    lines: ").append(this.toIndentedString(this.lines)).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
