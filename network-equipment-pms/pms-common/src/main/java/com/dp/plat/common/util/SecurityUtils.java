package com.dp.plat.common.util;

import com.dp.plat.common.constant.CommonConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

/**
 * Utility to retrieve the current logged-in user information from the security context.
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
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
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
     * Get the current user id parsed from the authentication name, or {@code null}.
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            try {
                return Long.valueOf(userDetails.getUsername());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (principal != null) {
            try {
                return Long.valueOf(principal.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
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
