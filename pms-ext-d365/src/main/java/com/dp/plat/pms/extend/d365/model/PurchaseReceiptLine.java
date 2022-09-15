package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * PurchaseReceiptLine
 */
public class PurchaseReceiptLine extends com.dp.plat.pms.extend.d365.entity.PurchaseReceiptLine
        implements Serializable {
    private static final long serialVersionUID = 1L;
//
//	@JSONField(name = "purchId")
//	private String purchId;
//
//	@JSONField(name = "lineNum")
//	private String lineNum;
//
//	@JSONField(name = "inventTransId")
//	private String inventTransId;
//
//  @JSONField(name = "qty")
//	private BigDecimal qty;
//
//	@JSONField(name = "inventSiteId")
//	private String inventSiteId = "";
//
//	@JSONField(name = "inventLocationId")
//	private String inventLocationId;
//
//	@JSONField(name = "wmsLocationId")
//	private String wmsLocationId;
//

    public PurchaseReceiptLine purchId(String purchId) {
        this.setPurchId(purchId);
        return this;
    }

    public PurchaseReceiptLine lineNum(String lineNum) {
        this.setLineNum(lineNum);
        return this;
    }

    public PurchaseReceiptLine inventTransId(String inventTransId) {
        this.setInventTransId(inventTransId);
        return this;
    }

    public PurchaseReceiptLine qty(BigDecimal qty) {
        this.setQty(qty);
        return this;
    }

    public PurchaseReceiptLine inventSiteId(String inventSiteId) {
        this.setInventSiteId(inventSiteId);
        return this;
    }

    public PurchaseReceiptLine inventLocationId(String inventLocationId) {
        this.setInventLocationId(inventLocationId);
        return this;
    }

    public PurchaseReceiptLine wmsLocationId(String wmsLocationId) {
        this.setWmsLocationId(wmsLocationId);
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
        PurchaseReceiptLine purchaseReceiptLine = (PurchaseReceiptLine) o;
        return Objects.equals(this.getPurchId(), purchaseReceiptLine.getPurchId())
                && Objects.equals(this.getLineNum(), purchaseReceiptLine.getLineNum())
                && Objects.equals(this.getInventTransId(), purchaseReceiptLine.getInventTransId())
                && Objects.equals(this.getQty(), purchaseReceiptLine.getQty())
                && Objects.equals(this.getInventSiteId(), purchaseReceiptLine.getInventSiteId())
                && Objects.equals(this.getInventLocationId(), purchaseReceiptLine.getInventLocationId())
                && Objects.equals(this.getWmsLocationId(), purchaseReceiptLine.getWmsLocationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPurchId(), this.getLineNum(), this.getInventTransId(), this.getQty(),
                this.getInventSiteId(), this.getInventLocationId(), this.getWmsLocationId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PurchaseReceiptLine {\n");
        sb.append("    purchId: ").append(this.toIndentedString(this.getPurchId())).append("\n");
        sb.append("    lineNum: ").append(this.toIndentedString(this.getLineNum())).append("\n");
        sb.append("    inventTransId: ").append(this.toIndentedString(this.getInventTransId())).append("\n");
        sb.append("    qty: ").append(this.toIndentedString(this.getQty())).append("\n");
        sb.append("    inventSiteId: ").append(this.toIndentedString(this.getInventSiteId())).append("\n");
        sb.append("    inventLocationId: ").append(this.toIndentedString(this.getInventLocationId())).append("\n");
        sb.append("    wmsLocationId: ").append(this.toIndentedString(this.getWmsLocationId())).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
