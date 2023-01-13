package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * PurchaseRequestBody
 */
public class RequestBody implements Serializable {
  private static final long serialVersionUID = 1L;

  @JSONField(name = "dataAreaId")
  protected String dataAreaId;
  
  public RequestBody dataAreaId(String dataAreaId) {
    this.dataAreaId = dataAreaId;
    return this;
  }

  /**
   * Get dataAreaId
   * @return dataAreaId
  */
  public String getDataAreaId() {
    return dataAreaId;
  }

  public void setDataAreaId(String dataAreaId) {
    this.dataAreaId = dataAreaId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestBody purchaseRequestBody = (RequestBody) o;
    return Objects.equals(this.dataAreaId, purchaseRequestBody.dataAreaId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataAreaId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequestBody {\n");
    sb.append("    dataAreaId: ").append(toIndentedString(dataAreaId)).append("\n");
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

