package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.system.entity.LoginLog;
import com.dp.plat.system.mapper.LoginLogMapper;
import com.dp.plat.system.service.ILoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements ILoginLogService {

    private final LoginLogMapper loginLogMapper;

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
