package com.dp.plat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.system.entity.LoginLog;

/**
 * 登录日志服务接口。
 */
public interface ILoginLogService {

    /**
     * 记录登录日志。
     *
     * @param loginLog 登录日志
     * @return 是否记录成功
     */
    boolean record(LoginLog loginLog);

    /**
     * 分页查询登录日志。
     *
     * @param page   当前页码
     * @param size   每页条数
     * @param filter 过滤条件
     * @return 分页结果
     */
    IPage<LoginLog> page(int page, int size, LoginLog filter);
}
