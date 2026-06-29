package com.dp.plat.security.csrf;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 * A manager for the CSRF token for a given session. The
 * {@link #getTokenForSession(HttpSession)} should used to obtain the token
 * value for the current session (and this should be the only way to obtain the
 * token value).
 ***/
public final class CSRFTokenManager {

    /**
     * The token parameter name
     */
    public static final String CSRF_PARAM_NAME_DEFAULT = "__RequestVerificationToken";
    
    /**
     * The location on the session which stores the token
     */
    public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class.getName() + ".tokenval";
    
    public static final String CSRF_TOKEN_PARAM_NAME = "CSRF_TOKEN";
    
    private static String csrfTokenName = CSRF_PARAM_NAME_DEFAULT;
    
    private CSRFTokenManager() {
    }
    
    private CSRFTokenManager(String csrfTokenName) {
        CSRFTokenManager.setCsrfTokenName(csrfTokenName);
    }
    
    public static String generateToken() {
       return UUID.randomUUID().toString();
    }

    public static String getTokenForSession(HttpSession session) {
        String token = null;

        // I cannot allow more than one token on a session - in the case of two
        // requests trying to
        // init the token concurrently
        synchronized (session) {
            token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
            if (null == token) {
                token = generateToken();
                session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
            }
        }
        return token;
    }

    /**
     * Extracts the token value from the session
     * 
     * @param request
     * @return
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        String csrfToken = null;
        csrfToken = request.getParameter(getTokenName());
        if (StringUtils.isEmpty(csrfToken)) {
            csrfToken = request.getHeader(getTokenName());
        }
        if (StringUtils.isEmpty(csrfToken)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (getTokenName().equals(name)) {
                        csrfToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return csrfToken;
    }

    public static String getTokenName() {
        return csrfTokenName;
    }

    public static void setCsrfTokenName(String csrfTokenName) {
        CSRFTokenManager.csrfTokenName = csrfTokenName;
    }

}
