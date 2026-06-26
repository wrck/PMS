package com.dp.plat.service;

import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.model.vo.UserVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    void logout();

    UserVO getCurrentUserInfo();
}
