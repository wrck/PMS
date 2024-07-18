package com.dp.plat.util;

import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.JavaMethodReflectionFunctionMissing;

public class AviatorUtils {

    private static int cacheSize = 100;

    private static class StaticHolder {
        private static AviatorEvaluatorInstance INSTANCE = newInstance();
        
        private static AviatorEvaluatorInstance newInstance() {
            AviatorEvaluatorInstance instance = AviatorEvaluator.getInstance();
            instance.useLRUExpressionCache(cacheSize);
            instance.setCachedExpressionByDefault(true);
            instance.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
            return instance;
        }
    }

    public static AviatorEvaluatorInstance getInstance() {
        return StaticHolder.INSTANCE;
    }   

    public static Object exceute(String script, Map<String, Object> env) {
        // 启用基于反射的方法查找和调用
        AviatorEvaluatorInstance evaluatorInstance = getInstance();
        // 缓存的Key
        String cacheKey = Md5Util.getMD5(script.getBytes());
        // 编译脚本
        Expression expression = evaluatorInstance.compile(cacheKey, script, true);
        // 执行脚本
        Object result = expression.execute(env);
        return result;
    }

    public static int getCacheSize() {
        return cacheSize;
    }

    /**
     * 设置缓存长度
     * @param cacheSize
     */
    public static void setCacheSize(int cacheSize) {
        AviatorUtils.cacheSize = cacheSize;
        getInstance().useLRUExpressionCache(cacheSize);
    }
    
    /**
     * 重置resetAviator
     */
    public static void resetAviator() {
        // 清空原来示例的缓存释放内存
        getInstance().clearExpressionCache();
        // 生成一个新的实例，赋给单例对象
        StaticHolder.INSTANCE = StaticHolder.newInstance();
    }
}
