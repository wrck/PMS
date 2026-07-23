package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.system.entity.LoginLog;
import com.dp.plat.system.mapper.PmsLoginLogMapper;
import com.dp.plat.system.service.ILoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录日志服务实现。
 *
 * <p>显式指定 Bean 名 {@code pmsLoginLogServiceImpl}，避免与 yudao-module-system
 * 的 {@code loginLogServiceImpl} 同名冲突（两者类名相同，默认 Bean 名均为
 * {@code loginLogServiceImpl}，启动时 {@code @ComponentScan} 会抛出
 * BeanDefinitionConflictException）。</p>
 */
@Service("pmsLoginLogServiceImpl")
@RequiredArgsConstructor
public class LoginLogServiceImpl implements ILoginLogService {

    private final PmsLoginLogMapper loginLogMapper;

    @Override
    public boolean record(LoginLog loginLog) {
        return loginLogMapper.insert(loginLog) > 0;
    }

    @Override
    public IPage<LoginLog> page(int page, int size, LoginLog filter) {
        Page<LoginLog> p = new Page<>(page, size);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (filter != null) {
            wrapper.like(StringUtils.hasText(filter.getUsername()), LoginLog::getUsername, filter.getUsername());
            wrapper.eq(StringUtils.hasText(filter.getStatus()), LoginLog::getStatus, filter.getStatus());
            wrapper.eq(filter.getUserId() != null, LoginLog::getUserId, filter.getUserId());
        }
        wrapper.orderByDesc(LoginLog::getLoginTime);
        return loginLogMapper.selectPage(p, wrapper);
    }
}
