package com.dp.plat.pms.extend.d365.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * TokenResponse
 */
public class TokenResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@JSONField(name = "token_type")
	private String tokenType;

	@JSONField(name = "expires_in")
	private String expiresIn;

	@JSONField(name = "ext_expires_in")
	private String extExpiresIn;

	@JSONField(name = "expires_on")
	private String expiresOn;

	@JSONField(name = "not_before")
	private String notBefore;

	@JSONField(name = "resource")
	private String resource;

	@JSONField(name = "access_token")
	private String accessToken;

	@JSONField(name = "error")
	private String error;

	@JSONField(name = "error_description")
	private String errorDescription;

	@JSONField(name = "error_codes")
	private List<Object> errorCodes;

	@JSONField(name = "timestamp")
	private String timestamp;

	@JSONField(name = "trace_id")
	private String traceId;

	@JSONField(name = "correlation_id")
	private String correlationId;

	@JSONField(name = "error_uri")
	private String errorUri;

	public TokenResponse tokenType(String tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	/**
	 * Get tokenType
	 * 
	 * @return tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public TokenResponse expiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
		return this;
	}

	/**
	 * Get expiresIn
	 * 
	 * @return expiresIn
	 */
	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public TokenResponse extExpiresIn(String extExpiresIn) {
		this.extExpiresIn = extExpiresIn;
		return this;
	}

	/**
	 * Get extExpiresIn
	 * 
	 * @return extExpiresIn
	 */
	public String getExtExpiresIn() {
		return extExpiresIn;
	}

	public void setExtExpiresIn(String extExpiresIn) {
		this.extExpiresIn = extExpiresIn;
	}

	public TokenResponse expiresOn(String expiresOn) {
		this.expiresOn = expiresOn;
		return this;
	}

	/**
	 * Get expiresOn
	 * 
	 * @return expiresOn
	 */
	public String getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(String expiresOn) {
		this.expiresOn = expiresOn;
	}

	public TokenResponse notBefore(String notBefore) {
		this.notBefore = notBefore;
		return this;
	}

	/**
	 * Get notBefore
	 * 
	 * @return notBefore
	 */
	public String getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}

	public TokenResponse resource(String resource) {
		this.resource = resource;
		return this;
	}

	/**
	 * Get resource
	 * 
	 * @return resource
	 */
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public TokenResponse accessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	/**
	 * Get accessToken
	 * 
	 * @return accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public List<Object> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<Object> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getErrorUri() {
		return errorUri;
	}

	public void setErrorUri(String errorUri) {
		this.errorUri = errorUri;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TokenResponse response = (TokenResponse) o;
		return Objects.equals(this.tokenType, response.tokenType) && Objects.equals(this.expiresIn, response.expiresIn)
				&& Objects.equals(this.extExpiresIn, response.extExpiresIn)
				&& Objects.equals(this.expiresOn, response.expiresOn)
				&& Objects.equals(this.notBefore, response.notBefore)
				&& Objects.equals(this.resource, response.resource)
				&& Objects.equals(this.accessToken, response.accessToken)
				&& Objects.equals(this.accessToken, response.error)
				&& Objects.equals(this.accessToken, response.errorDescription)
				&& Objects.equals(this.accessToken, response.errorCodes)
				&& Objects.equals(this.accessToken, response.timestamp)
				&& Objects.equals(this.accessToken, response.traceId)
				&& Objects.equals(this.accessToken, response.correlationId)
				&& Objects.equals(this.accessToken, response.errorUri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokenType, expiresIn, extExpiresIn, expiresOn, notBefore, resource, accessToken, error,
				errorDescription, errorCodes, timestamp, traceId, correlationId, errorUri);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class TokenResponse {\n");
		if (tokenType != null)
			sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
		if (expiresIn != null)
			sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
		if (extExpiresIn != null)
			sb.append("    extExpiresIn: ").append(toIndentedString(extExpiresIn)).append("\n");
		if (expiresOn != null)
			sb.append("    expiresOn: ").append(toIndentedString(expiresOn)).append("\n");
		if (notBefore != null)
			sb.append("    notBefore: ").append(toIndentedString(notBefore)).append("\n");
		if (resource != null)
			sb.append("    resource: ").append(toIndentedString(resource)).append("\n");
		if (accessToken != null)
			sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
		if (error != null)
			sb.append("    error: ").append(toIndentedString(error)).append("\n");
		if (errorDescription != null)
			sb.append("    errorDescription: ").append(toIndentedString(errorDescription)).append("\n");
		if (errorCodes != null)
			sb.append("    errorCodes: ").append(toIndentedString(errorCodes)).append("\n");
		if (timestamp != null)
			sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
		if (traceId != null)
			sb.append("    traceId: ").append(toIndentedString(traceId)).append("\n");
		if (correlationId != null)
			sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
		if (errorUri != null)
			sb.append("    errorUri: ").append(toIndentedString(errorUri)).append("\n");
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
