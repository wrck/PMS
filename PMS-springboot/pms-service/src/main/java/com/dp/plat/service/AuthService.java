package com.dp.plat.service;

import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.model.vo.UserVO;

import java.util.List;
import java.util.Map;

public interface AuthService {

    /** 登录 */
    LoginVO login(LoginDTO loginDTO);

    /** 登出 */
    void logout(String username);

    /** 获取当前用户信息 */
    LoginVO getUserInfo(String username);

    /**
     * 登录（含验证码校验和IP记录）
     * 迁移自: LoginServiceImpl.login(LoginParam, String ip)
     */
    LoginVO loginWithCaptcha(LoginDTO loginDTO, String ip, String captchaCode, String sessionCaptcha);

    /**
     * CAS单点登录
     * 迁移自: LoginServiceImpl.loginCas()
     */
    LoginVO loginCas(String username, String ip);

    /**
     * 查询系统参数
     * 迁移自: LoginServiceImpl.querySysArg()
     */
    String querySysArg(String code);

    /**
     * 获取用户角色菜单权限映射
     * 迁移自: LoginServiceImpl 中的 roleMenuPowerMap 构建逻辑
     */
    Map<Integer, Map<String, Integer>> getUserRoleMenuPower(Long userId);

    /**
     * 获取用户菜单名称映射
     * 迁移自: LoginServiceImpl 中的 permissionNameMap
     */
    Map<String, List<String>> getUserMenuNameMap(Long userId);
}
