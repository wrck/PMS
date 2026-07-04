package com.dp.plat.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.system.entity.ExceptionLog;

/**
 * 异常日志服务接口。
 */
public interface IExceptionLogService {

    /**
     * 记录异常日志。
     *
     * @param exceptionLog 异常日志
     * @return 是否记录成功
     */
    boolean record(ExceptionLog exceptionLog);

    /**
     * 分页查询异常日志。
     *
     * @param page   当前页码
     * @param size   每页条数
     * @param filter 过滤条件
     * @return 分页结果
     */
    IPage<ExceptionLog> page(int page, int size, ExceptionLog filter);
}
