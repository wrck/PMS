package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.JwtUtil;
import com.dp.plat.common.utils.PasswordUtil;
import com.dp.plat.mapper.SysUserMapper;
import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录服务 - 迁移自老系统 LoginServiceImpl
 * 
 * 核心逻辑：
 * 1. 根据用户名查询 fnd_user_info
 * 2. MD5 校验密码（老系统用 MD5，保持兼容）
 * 3. 检查用户状态和密码过期时间
 * 4. 生成 JWT Token
 * 5. 返回用户信息和菜单权限
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查用户状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 3. 校验密码（MD5，兼容老系统）
        String inputPwd = PasswordUtil.md5(dto.getPassword());
        if (!inputPwd.equalsIgnoreCase(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 检查密码是否过期
        if (user.getPwdOverdue() != null && LocalDateTime.now().isAfter(user.getPwdOverdue())) {
            throw new BusinessException("密码已过期，请联系管理员重置");
        }

        // 5. 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.generateToken(user.getUsername(), claims);

        // 6. 构建返回值
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealname(user.getRealname());
        vo.setEmail(user.getEmail());
        vo.setDeptCode(user.getDeptCode());
        vo.setRoleIds(user.getRoleIds());
        vo.setDefaultPage(user.getDefaultPage());

        return vo;
    }

    @Override
    public void logout(String username) {
        // JWT 无状态，客户端清除 Token 即可
        // 如需服务端注销，可维护一个 Token 黑名单（Redis）
    }

    @Override
    public LoginVO getUserInfo(String username) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealname(user.getRealname());
        vo.setEmail(user.getEmail());
        vo.setDeptCode(user.getDeptCode());
        vo.setRoleIds(user.getRoleIds());
        vo.setDefaultPage(user.getDefaultPage());
        return vo;
    }
}
