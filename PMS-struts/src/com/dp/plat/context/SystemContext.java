/**
 * 
 */
package com.dp.plat.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;

import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.data.bean.Arg;

/**
 * @author w02611
 */
public class SystemContext {

    private Map<String, Arg> args;

    public Integer getIntegerValue(String argName, int defaultValue) {
        Integer longValue = getIntegerValue(argName);
        return longValue == null ? defaultValue : longValue;
    }

    public Integer getIntegerValue(String argName) {
        Arg arg = this.args.get(argName);
        return arg != null ? arg.getIntegerValue() : null;
    }

    public Long getLongValue(String argName) {
        Arg arg = this.args.get(argName);
        return arg != null ? arg.getLongValue() : null;
    }

    public Long getLongValue(String argName, int defaultValue) {
        Long longValue = getLongValue(argName);
        return longValue == null ? defaultValue : longValue;
    }

    public Double getDoubleValue(String argName) {
        Arg arg = this.args.get(argName);
        return arg != null ? arg.getDoubleValue() : null;
    }

    public Double getDoubleValue(String argName, double defaultValue) {
        Double doubleValue = getDoubleValue(argName);
        return doubleValue == null ? defaultValue : doubleValue;
    }

    public String getTextValue(String argName) {
        Arg arg = this.args.get(argName);
        return arg != null ? arg.getTextValue() : null;
    }

    public String getTextValue(String argName, String defaultValue) {
        String textValue = getTextValue(argName);
        return StringUtils.isEmpty(textValue) ? defaultValue : textValue;
    }

    public String getBigTextValue(String argName) {
        Arg arg = this.args.get(argName);
        return arg != null ? arg.getBigTextValue() : null;
    }

    public String getBigTextValue(String argName, String defaultValue) {
        String textValue = getBigTextValue(argName);
        return StringUtils.isEmpty(textValue) ? defaultValue : textValue;
    }

    public Arg getArg(String argName) {
        return this.args.get(argName);
    }
    
    public <T> T getCacheJsonValue(String argName) {
        Arg arg = this.getArg(argName);
        if (arg != null) {
            Object cache = arg.getCache();
            if (cache == null) {
                cache = JSON.parseObject(StringUtils.defaultIfEmpty(arg.getBigTextValue(), arg.getTextValue()));
                arg.setCache(cache);
            }
            return (T) cache;
        }
        return arg != null ? (T) arg.getCache() : null;
    }

    public <T> T getCacheJsonValue(String argName, Class<T> type) {
        Arg arg = this.getArg(argName);
        if (arg != null) {
            Object cache = arg.getCache();
            if (cache == null) {
                cache = JSON.parseObject(StringUtils.defaultIfEmpty(arg.getBigTextValue(), arg.getTextValue()), type);
                arg.setCache(cache);
            }
            return (T) cache;
        }
        return arg != null ? (T) arg.getCache() : null;
    }

    /**
     * 初始化 服务启动时执行
     * 
     * @param args
     */
    public void init() {
        query();
        autoIncrement();
    }

    private void query() {
        BasicDataDao argsDao = (BasicDataDao) SpringContext.getBean("basicDataDao");
        List<Arg> list = argsDao.querySysArgList(new Arg());
        Map<String, Arg> map = new HashMap<String, Arg>();
        for (Arg arg : list) {
            map.put(arg.getCode(), arg);
        }
        this.args = map;
    }

    /**
     * TODO 脚本版本号自增长 以清除缓存
     */
    private void autoIncrement() {
//	    BasicDataDao argsDao = (BasicDataDao) SpringContext.getBean("basicDataDao");
//		//将脚本版本号加1，以清除缓存
//		argsDao.autoIncrementScriptVersion();
    }

    /**
     * 刷新
     */
    public void refresh() {
        query();
    }

    public static SystemContext getSystemContext() {
        // systemContent
        return (SystemContext) SpringContext.getBean("systemContext");
    }
    
    /**
     * 获取CRM系统的配置
     * @return
     */
    public static ConcurrentHashMap<String, Object> getCrmConfig() {
        return getCrmConfig("sys.crm.api.config");
    }
    
    /**
     * 获取CRM系统的配置
     * @return
     */
    public static ConcurrentHashMap<String, Object> getCrmConfig(String key) {
        SystemContext systemContext = getSystemContext();
        if (systemContext != null) {
            ConcurrentHashMap<String, Object> config = systemContext.getCacheJsonValue(key, ConcurrentHashMap.class);
            if (config == null) {
                systemContext.refresh();
                config = systemContext.getCacheJsonValue(key, ConcurrentHashMap.class);
            }
            return config;
        }
        return null;
    }
}
