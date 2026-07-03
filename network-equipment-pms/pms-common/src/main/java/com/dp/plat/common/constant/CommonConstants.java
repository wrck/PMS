package com.dp.plat.common.constant;

/**
 * Common constants used across the application.
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /** Authorization header name. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Bearer token prefix. */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** Default current page number. */
    public static final int DEFAULT_PAGE_NUM = 1;

    /** Default page size. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** UTF-8 encoding. */
    public static final String UTF_8 = "UTF-8";

    /** Status: enabled / normal. */
    public static final String STATUS_NORMAL = "0";

    /** Status: disabled / abnormal. */
    public static final String STATUS_DISABLE = "1";

    /** Super admin role code. */
    public static final String SUPER_ADMIN_ROLE = "admin";

    /** Cache key prefix for login user. */
    public static final String LOGIN_USER_KEY = "login_user:";

    /** Menu type: directory. */
    public static final String MENU_TYPE_DIR = "M";

    /** Menu type: menu. */
    public static final String MENU_TYPE_MENU = "C";

    /** Menu type: button. */
    public static final String MENU_TYPE_BUTTON = "F";
}
