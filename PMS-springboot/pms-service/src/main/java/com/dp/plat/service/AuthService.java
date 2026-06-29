package com.dp.plat.service;

import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.model.vo.UserVO;

public interface AuthService {

    /** 登录 */
    LoginVO login(LoginDTO loginDTO);

    /** 登出 */
    void logout(String username);

    /** 获取当前用户信息 */
    LoginVO getUserInfo(String username);
}
