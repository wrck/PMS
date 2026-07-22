package com.dp.plat.common.util;

import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.framework.security.core.LoginUser;
import com.dp.plat.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Objects;

/**
 * Utility to retrieve the current logged-in user information from the security context.
 *
 * <p>适配 yudao 框架后，principal 为 {@link LoginUser}。本工具类委托
 * {@link SecurityFrameworkUtils} 获取用户编号/昵称，同时保留 {@link #getCurrentUsername()}
 * 兼容历史调用（从 authentication details 或 LoginUser.info 中读取用户名）。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the current authentication object, or {@code null} if unauthenticated.
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get the current username, or "system" if no authenticated user is present.
     *
     * <p>优先从 yudao {@link LoginUser#getInfo()} 的 nickname 读取；
     * 兼容历史从 authentication details (key "username") 读取；
     * 最后回退到 principal 的字符串形式。</p>
     */
    public static String getCurrentUsername() {
        // 1. 优先走 yudao LoginUser.info.nickname
        String nickname = SecurityFrameworkUtils.getLoginUserNickname();
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        // 2. 兼容历史：从 authentication details 读取 username
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map) {
            Object username = map.get("username");
            if (username instanceof String s && !s.isEmpty()) {
                return s;
            }
        }
        // 3. 回退到 principal 的 username（历史 UserDetails 场景）
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal != null) {
            return principal.toString();
        }
        return "system";
    }

    /**
     * Get the current user id, or {@code null}.
     *
     * <p>委托 yudao {@link SecurityFrameworkUtils#getLoginUserId()}，principal 为
     * {@link LoginUser} 时直接返回其 id。</p>
     */
    public static Long getCurrentUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    /**
     * Whether the current user is authenticated.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !Objects.equals(authentication.getName(), CommonConstants.TOKEN_PREFIX.trim());
    }
}
