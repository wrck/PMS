package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * PurchaseRequest
 */

public class PurchaseRequest extends Request<Response> implements Serializable {
	private static final long serialVersionUID = 1L;

	public PurchaseRequest request(PurchaseRequestBody requestBody) {
		this.request = requestBody;
		return this;
	}

	/**
	 * Get request
	 * 
	 * @return request
	 */
	public PurchaseRequestBody getRequest() {
		return (PurchaseRequestBody) request;
	}

	public void setRequest(PurchaseRequestBody requestBody) {
		this.request = requestBody;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PurchaseRequest purchaseRequest = (PurchaseRequest) o;
		return Objects.equals(this.request, purchaseRequest.request);
	}

	@Override
	public int hashCode() {
		return Objects.hash(request);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PurchaseRequest {\n");
		sb.append("    request: ").append(toIndentedString(request)).append("\n");
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
