package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.ResultCode;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.mapper.SysUserMapper;
import com.dp.plat.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link ISysUserService}.
 *
 * <p>密码保护：创建用户与更新密码时使用 {@link PasswordEncoder}（BCrypt）加密，
 * 不存储明文密码。已加密的密码（BCrypt 哈希以 {@code $2} 开头）不会重复加密。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    /** BCrypt 哈希前缀，用于判断密码是否已加密。 */
    private static final String BCRYPT_PREFIX = "$2";

    /** Spring Security 提供的密码编码器（BCrypt）。 */
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户：对明文密码进行 BCrypt 加密后存储。
     *
     * @param entity 用户实体
     * @return 是否保存成功
     */
    @Override
    public boolean save(SysUser entity) {
        encodePasswordIfNeeded(entity);
        return super.save(entity);
    }

    /**
     * 更新用户：若请求携带新密码则 BCrypt 加密后更新；未携带密码则不更新密码字段。
     *
     * @param entity 用户实体
     * @return 是否更新成功
     */
    @Override
    public boolean updateById(SysUser entity) {
        encodePasswordIfNeeded(entity);
        return super.updateById(entity);
    }

    /**
     * 按用户名查询用户。
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    /**
     * 修改用户密码：对新密码进行 BCrypt 加密后更新。
     *
     * @param userId      用户 id
     * @param newPassword 新明文密码
     * @return 是否更新成功
     */
    public boolean changePassword(Long userId, String newPassword) {
        if (userId == null || !StringUtils.hasText(newPassword)) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return super.updateById(user);
    }

    /**
     * 若密码字段非空且未被 BCrypt 加密，则执行 BCrypt 加密。
     *
     * <p>已加密的密码（以 {@code $2} 开头）跳过，避免重复加密。</p>
     *
     * @param user 用户实体
     */
    private void encodePasswordIfNeeded(SysUser user) {
        if (user == null) {
            return;
        }
        String password = user.getPassword();
        if (StringUtils.hasText(password) && !isBcryptEncoded(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
    }

    /**
     * 判断密码是否已被 BCrypt 加密。
     *
     * @param password 密码字符串
     * @return true 表示已是 BCrypt 哈希
     */
    private boolean isBcryptEncoded(String password) {
        return password != null && password.startsWith(BCRYPT_PREFIX);
    }
}
