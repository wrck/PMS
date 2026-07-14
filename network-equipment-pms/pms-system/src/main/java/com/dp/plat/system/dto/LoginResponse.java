package com.dp.plat.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Login response payload containing the JWT token and user info.
 *
 * <p>Structure aligns with the frontend {@code LoginResult} type:
 * {@code { token, userInfo: { id, username, nickname, roles, permissions } }}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    /** User info expected by the frontend (id, username, nickname, roles, permissions, ...). */
    private Map<String, Object> userInfo;
}
