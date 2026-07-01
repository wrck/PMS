package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.JwtUtil;
import com.dp.plat.common.utils.PasswordUtil;
import com.dp.plat.mapper.SysBasicDataMapper;
import com.dp.plat.mapper.SysUserMapper;
import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 登录服务 - 迁移自老系统 LoginServiceImpl
 *
 * 核心逻辑：
 * 1. 根据用户名查询 fnd_user_info
 * 2. 校验密码
 * 3. 检查用户状态和密码过期时间
 * 4. 生成 JWT Token
 * 5. 构建区域权限（processAreaPower: 16↔31互换）
 * 6. 构建角色菜单权限映射（CRUD: 8→insert, 1→delete, 4→select, 2→update）
 * 7. 返回用户信息和菜单权限
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysBasicDataMapper basicDataMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        // 3. 校验密码
        String inputPwd = PasswordUtil.encrypt(dto.getPassword());
        if (!inputPwd.equalsIgnoreCase(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 检查密码是否过期
        if (user.getPwdOverdue() != null && LocalDateTime.now().isAfter(user.getPwdOverdue())) {
            throw new BusinessException("密码已过期，请联系管理员重置");
        }

        // 5. 构建返回值
        return buildLoginVO(user);
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
        return buildLoginVO(user);
    }

    // ===== 迁移自老系统 LoginServiceImpl =====

    @Override
    public LoginVO loginWithCaptcha(LoginDTO dto, String ip, String captchaCode, String sessionCaptcha) {
        // 迁移自: LoginServiceImpl.login(LoginParam, String ip)
        // 1. 验证码校验
        String envirment = querySysArg("sys.envirment.argu");
        if ("1".equals(envirment)) {
            if (!StringUtils.hasText(captchaCode) || !captchaCode.equals(sessionCaptcha)) {
                throw new BusinessException("验证码错误");
            }
        }

        // 2. 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 测试环境忽略密码
        if (!"1".equals(envirment)) {
            // 测试环境：直接通过密码校验
        } else {
            // 生产环境：校验密码
            String inputPwd = PasswordUtil.encrypt(dto.getPassword());
            if (!inputPwd.equalsIgnoreCase(user.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
        }

        // 4. 检查用户状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 5. 构建返回值
        LoginVO vo = buildLoginVO(user);
        vo.setAreaPower(processAreaPower(user.getAreaPower()));
        return vo;
    }

    @Override
    public LoginVO loginCas(String username, String ip) {
        // 迁移自: LoginServiceImpl.loginCas()
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        LoginVO vo = buildLoginVO(user);
        vo.setAreaPower(processAreaPower(user.getAreaPower()));
        return vo;
    }

    @Override
    public String querySysArg(String code) {
        // 迁移自: LoginServiceImpl.querySysArg()
        return basicDataMapper.selectSysArg(code);
    }

    @Override
    public Map<Integer, Map<String, Integer>> getUserRoleMenuPower(Long userId) {
        // 迁移自: LoginServiceImpl 中的 roleMenuPowerMap 构建逻辑
        // 查询用户角色
        SysUser user = userMapper.selectById(userId);
        if (user == null || !StringUtils.hasText(user.getRoleIds())) {
            return Collections.emptyMap();
        }

        Map<Integer, Map<String, Integer>> roleMenuPowerMap = new HashMap<>();
        String[] roleIds = user.getRoleIds().split(";");

        for (String roleIdStr : roleIds) {
            if (!StringUtils.hasText(roleIdStr)) continue;
            try {
                int roleId = Integer.parseInt(roleIdStr.trim());
                // 查询角色菜单权限
                List<Map<String, Object>> roleMenuPowers = queryRoleMenuPowerList(roleId);
                for (Map<String, Object> roleMenuPower : roleMenuPowers) {
                    int menuId = toInt(roleMenuPower.get("menuId"));
                    String menuPower = toString(roleMenuPower.get("menuPower"));
                    Map<String, Integer> menuPowerMap = new HashMap<>();
                    // 初始化CRUD权限
                    menuPowerMap.put("insert", 0);
                    menuPowerMap.put("delete", 0);
                    menuPowerMap.put("select", 0);
                    menuPowerMap.put("update", 0);
                    // 解析权限值: 8=增加, 1=删除, 4=查找, 2=更新
                    if (StringUtils.hasText(menuPower)) {
                        for (String str : menuPower.split(",")) {
                            str = str.trim();
                            if ("8".equals(str)) menuPowerMap.put("insert", 1);
                            if ("1".equals(str)) menuPowerMap.put("delete", 1);
                            if ("4".equals(str)) menuPowerMap.put("select", 1);
                            if ("2".equals(str)) menuPowerMap.put("update", 1);
                        }
                    }
                    roleMenuPowerMap.put(menuId, menuPowerMap);
                }
            } catch (NumberFormatException e) {
                // 忽略无效的角色ID
            }
        }
        return roleMenuPowerMap;
    }

    @Override
    public Map<String, List<String>> getUserMenuNameMap(Long userId) {
        // 迁移自: LoginServiceImpl 中的 permissionNameMap
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT menuCode, menuName FROM fnd_user_menus WHERE fnd_user_id = ?", userId);
            Map<String, List<String>> map = new HashMap<>();
            for (Map<String, Object> row : rows) {
                String menuCode = toString(row.get("menuCode"));
                String menuName = toString(row.get("menuName"));
                if (StringUtils.hasText(menuCode)) {
                    map.computeIfAbsent(menuCode, k -> new ArrayList<>()).add(menuName);
                }
            }
            return map;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    // ===== 内部辅助方法 =====

    /**
     * 构建LoginVO
     */
    private LoginVO buildLoginVO(SysUser user) {
        // 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.generateToken(user.getUsername(), claims);

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

    /**
     * 市场和用服相同办事处的权限进行补充
     * 迁移自: LoginServiceImpl.processAreaPower() / UserUtil.processAreaPower()
     * 逻辑: 16开头的办事处编码补充31开头的对应编码，反之亦然
     */
    private String processAreaPower(String areaPower) {
        if (!StringUtils.hasText(areaPower)) return areaPower;
        Set<String> newAreaList = new LinkedHashSet<>();
        String[] areaList = areaPower.split(",");
        Collections.addAll(newAreaList, areaList);
        for (String area : areaList) {
            String newArea = null;
            String trimmedArea = area.length() > 6 ? area.substring(0, 6) : area;
            if (trimmedArea.startsWith("16")) {
                newArea = trimmedArea.replaceFirst("16", "31");
            } else if (trimmedArea.startsWith("31")) {
                newArea = trimmedArea.replaceFirst("31", "16");
            }
            if (StringUtils.hasText(newArea) && !newAreaList.contains(newArea)) {
                newAreaList.add(newArea);
            }
        }
        return String.join(",", newAreaList);
    }

    /**
     * 查询角色菜单权限列表
     * 迁移自: LoginDao.queryRoleMenuPowerList()
     */
    private List<Map<String, Object>> queryRoleMenuPowerList(int roleId) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT menuId, menuPower FROM fnd_role_menu_power WHERE roleId = ?",
                    roleId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return 0; }
    }

    private String toString(Object val) {
        return val != null ? val.toString() : null;
    }
}
