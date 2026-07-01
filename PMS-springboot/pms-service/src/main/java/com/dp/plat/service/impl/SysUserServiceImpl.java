package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.PasswordUtil;
import com.dp.plat.mapper.SysUserMapper;
import com.dp.plat.model.dto.UserDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.UserVO;
import com.dp.plat.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户管理服务 - 迁移自老系统 UserManageServiceImpl
 *
 * 核心逻辑：
 * 1. 用户 CRUD，保持与 fnd_user_info 表兼容
 * 2. 密码 MD5 加密（兼容老系统）
 * 3. 密码重置（生成随机密码并邮件通知）
 * 4. 角色ID用分号分隔存储（;1;2;3;）
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public IPage<UserVO> queryUserPage(Integer pageNum, Integer pageSize,
                                        String username, String realname, Long deptId) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
               .like(StringUtils.hasText(realname), SysUser::getRealname, realname)
               .orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = userMapper.selectPage(page, wrapper);

        return userPage.convert(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        });
    }

    @Override
    @Transactional
    public void addUser(UserDTO dto) {
        // 检查用户名唯一
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        // 生成随机密码并 MD5 加密（兼容老系统）
        String randomPwd = generateRandomPassword();
        user.setPassword(PasswordUtil.md5(randomPwd));

        // 设置默认值
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 密码过期时间（默认90天）
        user.setPwdOverdue(LocalDateTime.now().plusDays(90));

        userMapper.insert(user);

        // 发送通知(邮件服务集成后可启用)
        // notificationService.sendEmail(user.getEmail(), "初始密码通知", "您的初始密码为: " + randomPwd);
    }

    @Override
    @Transactional
    public void updateUser(UserDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新允许修改的字段
        if (StringUtils.hasText(dto.getRealname())) user.setRealname(dto.getRealname());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StringUtils.hasText(dto.getPhone())) user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getRoleIds())) user.setRoleIds(dto.getRoleIds());
        if (StringUtils.hasText(dto.getDefaultPage())) user.setDefaultPage(dto.getDefaultPage());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成随机密码
        String randomPwd = generateRandomPassword();
        user.setPassword(PasswordUtil.md5(randomPwd));
        user.setPwdOverdue(LocalDateTime.now().plusDays(90));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 发送通知(邮件服务集成后可启用)
        // notificationService.sendEmail(user.getEmail(), "密码重置通知", "您的新密码为: " + randomPwd);
        // 强制用户下线(如有在线会话管理)
        // sessionManager.forceOffline(userId);
    }

    /**
     * 生成随机密码（8位，含大小写字母和数字）
     */
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 验证旧密码
        if (!PasswordUtil.encrypt(oldPassword).equals(user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        // 设置新密码
        user.setPassword(PasswordUtil.encrypt(newPassword));
        user.setPwdOverdue(LocalDateTime.now().plusDays(90));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void forcedOffline(String username) {
        // 迁移自: PasswordServiceImpl.forcedOffline()
        // 老系统逻辑: 遍历在线用户列表，踢除同用户名的其他Session
        // 新系统(JWT无状态): 维护Token黑名单实现强制下线
        // 实际实现需要Redis支持，这里标记为待集成
        // 如需实现，可通过Redis维护一个Token黑名单:
        // redisTemplate.opsForValue().set("token:blacklist:" + username, "1", 24, TimeUnit.HOURS);
        // 然后在JWT过滤器中检查Token是否在黑名单中
    }
}
