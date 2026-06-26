package com.dp.plat.common.constants;

public class CommonConstant {

    public static final Integer STATUS_ENABLE = 1;
    public static final Integer STATUS_DISABLE = 0;

    public static final Integer NOT_DELETED = 0;
    public static final Integer DELETED = 1;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PARAM = "token";

    public static final Integer SUPER_ADMIN_ID = 1;

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String LOGIN_URL = "/api/auth/login";
    public static final String LOGOUT_URL = "/api/auth/logout";

    private CommonConstant() {
    }
}
