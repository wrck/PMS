package com.dp.plat.common.utils;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.ResultCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class SecurityUtil {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Long getCurrentUserId() {
        Subject subject = getSubject();
        if (subject == null || !subject.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String userId = (String) subject.getPrincipal();
        return Long.parseLong(userId);
    }

    public static String getCurrentUsername() {
        Subject subject = getSubject();
        if (subject == null || !subject.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return (String) subject.getSession().getAttribute("username");
    }

    public static void checkPermission(String permission) {
        Subject subject = getSubject();
        if (subject != null) {
            subject.checkPermission(permission);
        }
    }

    public static void checkRole(String role) {
        Subject subject = getSubject();
        if (subject != null) {
            subject.checkRole(role);
        }
    }
}
