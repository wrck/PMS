package com.dp.plat.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务日志工具类
 * 为未继承 AbstractSynchronizeTask 的 Job 提供统一的日志记录
 * 
 * @author PMS Team
 */
public class JobLogger {
    
    private final Logger log;
    private final String jobName;
    
    /**
     * 创建 JobLogger
     * @param clazz Job 类
     */
    public JobLogger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
        this.jobName = clazz.getSimpleName();
    }
    
    /**
     * 记录任务开始
     */
    public void logStart() {
        log.info("[{}] - 任务开始", jobName);
    }
    
    /**
     * 记录任务开始（带参数）
     * @param param 参数描述
     */
    public void logStart(String param) {
        log.info("[{}] - 任务开始，参数: {}", jobName, param);
    }
    
    /**
     * 记录任务完成
     */
    public void logComplete() {
        log.info("[{}] - 任务完成", jobName);
    }
    
    /**
     * 记录任务完成（带结果）
     * @param result 结果描述
     */
    public void logComplete(String result) {
        log.info("[{}] - 任务完成，结果: {}", jobName, result);
    }
    
    /**
     * 记录任务失败
     * @param e 异常
     */
    public void logError(Exception e) {
        log.error("[{}] - 任务执行异常: {}", jobName, e.getMessage(), e);
    }
    
    /**
     * 记录任务失败（带自定义消息）
     * @param message 错误消息
     * @param e 异常
     */
    public void logError(String message, Exception e) {
        log.error("[{}] - {}: {}", jobName, message, e.getMessage(), e);
    }
    
    /**
     * 记录信息日志
     * @param message 消息
     * @param args 参数
     */
    public void logInfo(String message, Object... args) {
        log.info("[{}] - " + message, prepend(jobName, args));
    }
    
    /**
     * 记录警告日志
     * @param message 消息
     * @param args 参数
     */
    public void logWarn(String message, Object... args) {
        log.warn("[{}] - " + message, prepend(jobName, args));
    }
    
    /**
     * 记录调试日志
     * @param message 消息
     * @param args 参数
     */
    public void logDebug(String message, Object... args) {
        log.debug("[{}] - " + message, prepend(jobName, args));
    }
    
    /**
     * 在参数数组前插入 jobName
     */
    private Object[] prepend(String prefix, Object[] args) {
        if (args == null || args.length == 0) {
            return new Object[]{prefix};
        }
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = prefix;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }
}
