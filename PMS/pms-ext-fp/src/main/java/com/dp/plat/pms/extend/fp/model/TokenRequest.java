package com.dp.plat.pms.extend.fp.model;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * TokenRequest
 */
public class TokenRequest extends Request<TokenResponse> implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "oauthType")
    private String oauthType;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "nickName")
    private String nickName;

    public TokenRequest oauthType(String oauthType) {
        this.oauthType = oauthType;
        return this;
    }

    /**
     * Get oauthType
     * 
     * @return oauthType
     */
    public String getOauthType() {
        return oauthType;
    }

    public void setOauthType(String oauthType) {
        this.oauthType = oauthType;
    }

    public TokenRequest code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     * 
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TokenRequest nickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    /**
     * Get nickName
     * 
     * @return nickName
     */
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TokenRequest inlineObject = (TokenRequest) o;
        return Objects.equals(this.oauthType, inlineObject.oauthType) && Objects.equals(this.code, inlineObject.code)
                && Objects.equals(this.nickName, inlineObject.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oauthType, code, nickName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineObject {\n");

        sb.append("    oauthType: ").append(toIndentedString(oauthType)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    nickName: ").append(toIndentedString(nickName)).append("\n");
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