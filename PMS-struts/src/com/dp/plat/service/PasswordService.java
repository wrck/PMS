package com.dp.plat.service;

import com.dp.plat.param.PasswordEditParam;

public interface PasswordService {
	boolean changelogin(PasswordEditParam passwordEditParam);

    /**
     * 踢指定用户下线
     * @param username
     */
    void forcedOffline(String username);
}
