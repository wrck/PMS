package com.dp.plat.aop.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;

import com.dp.plat.aop.annotation.AopParam;

/**
 * 切点类
 */
@Aspect
@Component
@SuppressWarnings("unchecked")
public class AspectUtil {

    // 本地异常日志记录对象
    private static final Logger logger = Logger.getLogger(AspectUtil.class);

    private static final String DEFAULT_BEFORE_SPLIT = "$";
    private static final String DEFAULT_AFTER_SPLIT = "$";

    private ThreadLocal<Map<String, Object>> params = new ThreadLocal<>();
    private ThreadLocal<Map<String, String>> paramRelations = new ThreadLocal<>();
    
    /**
     * 根据注解类型，获取注解中对方法的描述信息
     * 
     * @param joinPoint
     * @param specialType systemLog annotion type
     * @return description
     * @throws Exception
     */
    public static Annotation getAnnotation(JoinPoint joinPoint, Class<? extends Annotation>... specialType) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation annotation = null;
        // 根据特定的注解获取描述
        if (specialType.length > 0) {
            Class<? extends Annotation> annotationType = specialType[0];
            annotation = signature.getMethod().getAnnotation(annotationType);
            if (annotation == null) {
                annotation = targetClass.getAnnotation(annotationType);
            }
        }
        return annotation;
    }

    /**
     * 根据注解类型，获取注解中对方法指定名称的描述信息
     * 
     * @param joinPoint
     * @param fieldNames
     * @param specialType annotion type
     * @return fieldValue
     * @throws Exception
     */
    public static Map<String, Object> getAnnotationFieldValues(JoinPoint joinPoint, String[] fieldNames, Class<? extends Annotation>... specialType) throws Exception {
        if (fieldNames == null || fieldNames.length == 0) {
            return null;
        }
        Annotation annotation = getAnnotation(joinPoint, specialType);
        Map<String, Object> fieldValues = null;
        if (annotation != null) {
            fieldValues = new HashMap<String, Object>();
            for (String fieldName : fieldNames) {
                try {
                    Method method = annotation.getClass().getDeclaredMethod(fieldName);
                    Object description = (Object) method.invoke(annotation);
                    fieldValues.put(fieldName, description);
                } catch (NoSuchMethodException e) {
                }
            }
        }
        return fieldValues;
    }

    /**
     * 获取给 "方法参数" 进行注解的值
     *
     * @param joinPoint 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表
     */
    public static String[] getMethodParameterNamesByAnnotation(JoinPoint joinPoint, Map<String, String> paramRelations, Class<? extends Annotation>... specialType) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return null;
        }
        String[] parameterNames = signature.getParameterNames();
        ArrayList<Object> dataParams = new ArrayList<>(parameterAnnotations.length);
        Class<? extends Annotation> specialAnnotation = AopParam.class;
        if (specialType.length > 0) {
            specialAnnotation = specialType[0];
        }
        for (int j = 0; j < parameterAnnotations.length; j++) {
            Annotation[] annotations = parameterAnnotations[j];
            String argName = parameterNames != null ? parameterNames[j] : ("arg" + j);
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(specialAnnotation) || annotation.annotationType().isAnnotationPresent(specialAnnotation)) {
                    String param = argName;
                    String newParam = null;
                    try {
                        Method valueMethod = annotation.getClass().getDeclaredMethod("value");
                        newParam = (String) valueMethod.invoke(annotation);
                    } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    }
                    if (StringUtils.isBlank(newParam)) {
                        newParam = param;
                    }
                    if (StringUtils.isNotBlank(newParam)) {
                        dataParams.add(newParam);
                    }
                    if (!StringUtils.isAllBlank(param, newParam)) {
                        paramRelations.put(StringUtils.defaultIfBlank(param, newParam), newParam);
                    }
                }
            }
        }
        return (String[]) dataParams.toArray(new String[] {});
    }

    /**
     * 获取给 "方法参数" 进行注解的值
     *
     * @param joinPoint 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表
     */
    public static String[] getMethodParameterNameByAnnotation(JoinPoint joinPoint, String parameterName, Class<? extends Annotation>... specialType) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return null;
        }
        String[] parameterNames = signature.getParameterNames();
        ArrayList<Object> dataParams = new ArrayList<>(parameterAnnotations.length);
        Class<?> specialAnnotation = null;
        if (specialType.length > 0) {
            specialAnnotation = specialType[0];
        }
        for (int j = 0; j < parameterAnnotations.length; j++) {
            Annotation[] annotations = parameterAnnotations[j];
            String argName = parameterNames != null ? parameterNames[j] : ("arg" + j);
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(specialAnnotation)) {
                    String param = null;
                    try {
                        Method valueMethod = annotation.getClass().getDeclaredMethod("value");
                        param = (String) valueMethod.invoke(annotation);
                    } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    }
                    if (StringUtils.isBlank(param)) {
                        param = argName;
                    }
                    if (StringUtils.isNotBlank(param)) {
                        dataParams.add(param);
                    }
                }
            }
        }
        return (String[]) dataParams.toArray(new String[] {});
    }

    /**
     * 根据注解类型，获取注解中需要忽略的参数
     * 
     * @param joinPoint
     * @param specialType systemLog annotion type
     * @return description
     * @throws Exception
     */

    private static String[] getSystemLogIgnoreParams(JoinPoint joinPoint, Class<? extends Annotation>... specialType) {
        Annotation annotation = getAnnotation(joinPoint, specialType);
        String[] ignoreParams = {};
        if (annotation != null) {
            try {
                Method method = annotation.getClass().getDeclaredMethod("ignoreParams");
                ignoreParams = (String[]) method.invoke(annotation);
            } catch (Exception e) {
            }
        }
        return ignoreParams;
    }

    /**
     * 将请求参数封装成JSON字符串
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    public static String getParamsJson(JoinPoint joinPoint) throws Exception {
        Map<String, String> paramRelations = new HashMap<>();
        getMethodParameterNamesByAnnotation(joinPoint, paramRelations);
        return getParamsJson(joinPoint, paramRelations);
    }

    /**
     * 将请求参数封装成JSON字符串
     * 
     * @param joinPoint
     * @param paramRelations
     * @return
     * @throws Exception
     */
    public static String getParamsJson(JoinPoint joinPoint, Map<String, String> paramRelations) throws Exception {
        Map<String, Object> params = getParamsMap(joinPoint, paramRelations);
        return JSON.toJSONString(params);
    }

    /**
     * 将请求参数封装成Map
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getParamsMap(JoinPoint joinPoint) {
        Map<String, String> paramRelations = new HashMap<>();
        AspectUtil.getMethodParameterNamesByAnnotation(joinPoint, paramRelations);
        return getParamsMap(joinPoint, paramRelations);
    }

        /**
         * 将请求参数封装成Map
         *
         * @param joinPoint
         * @return
         * @throws Exception
         */
    public static Map<String, Object> getParamsMap(JoinPoint joinPoint, Map<String, String> paramRelations) {
        Map<String, Object> params = new HashMap<>();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramsName = methodSignature.getParameterNames();
        Object[] paramsValue = joinPoint.getArgs();
        String[] ignoreParams = getSystemLogIgnoreParams(joinPoint);
        for (int i = 0; i < paramsValue.length; i++) {
            Object value = paramsValue[i];
            if (paramsName == null || paramsName[i] == null || paramsName[i].toLowerCase().indexOf("request") >= 0 || paramsName[i].toLowerCase().indexOf("response") >= 0
                    || ArrayUtils.contains(ignoreParams, paramsName[i])) {
                continue;
            }
            if (paramsName[i].indexOf("model") >= 0 && value instanceof Model) {
                Map<String, Object> model = ((Model) value).asMap();
                Map<String, Object> result = new HashMap<String, Object>(model.size());
                Set<String> renderedAttributes = model.keySet();
                for (Entry<String, Object> entry : model.entrySet()) {
                    if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
                value = result;
            }
            // 判断变量是否有别名，如果有赋值别名变量
            if (paramRelations.containsKey(paramsName[i])) {
                params.put(paramRelations.get(paramsName[i]), value);
            } else {
                params.put(paramsName[i], value);
            }
        }
        if (ignoreParams != null && ignoreParams.length > 0) {
            params.put("ignoreParams", ignoreParams);
            Map<String, Object> newParams = new HashMap<>();
            newParams.putAll(params);
            boolean isIgnore = false;
            for (String ignoreKey : ignoreParams) {
                isIgnore = containsObjectKey(ignoreKey, newParams);
                if (isIgnore) {
                    removeIgnoreObjectValue(ignoreKey, newParams);
                }
            }
        }
        return params;
    }

    /**
     * 获取当前的HttpRequest请求，如果不是HttpRequest请求，则查询是否有webservice上下文
     * 
     * @param joinPoint
     * @return
     */
    private static HttpServletRequest getRequest(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes != null ? requestAttributes.getRequest() : null;
        // 无法获取Http请求时，查询是否为Webservice 请求
        if (request == null) {
            try {
                Object target = joinPoint.getTarget();
                Field field = target.getClass().getDeclaredField("wsContext");
                if (field != null) {
                    field.setAccessible(true);
                    WebServiceContext wsContext = (WebServiceContext) field.get(target);
                    request = (HttpServletRequest) wsContext.getMessageContext().get("HTTP.REQUEST");
                }
            } catch (Throwable ex) {
                // ex.printStackTrace();
            }
        }
        return request;
    }

    /**
     * 合并变量中的Map
     * 
     * @param params
     * @return
     */
    private static Map<String, Object> mergerSubMap(Map<String, Object> params) {
        Map<String, Object> mergeredMap = new HashMap<>();
        mergeredMap.putAll(params);
        for (Entry<String, Object> entity : params.entrySet()) {
            Object value = entity.getValue();
            if (value != null && value instanceof String) {
                try {
                    value = JSON.parseObject((String) value, HashMap.class);
                } catch (Exception e) {
                }
            }
            if (value != null && value instanceof Map) {
                mergeredMap.putAll((Map<? extends String, ? extends Object>) value);
            }
        }
        return mergeredMap;
    }

    /**
     * 解析field字段的值，例如，user.username,获取user对象的username属性值
     *
     * @param field
     * @param newParams
     * @return
     */
    public static Object parseObjectValue(String field, Map<String, Object> newParams) {
        return parseObjectValue(field, newParams, DEFAULT_BEFORE_SPLIT, DEFAULT_AFTER_SPLIT);
    }

        /**
         * 解析field字段的值，例如，user.username,获取user对象的username属性值
         *
         * @param field
         * @param newParams
         * @param beforeSplit
         * @param afterSplit
         * @return
         */
    public static Object parseObjectValue(String field, Map<String, Object> newParams, String beforeSplit, String afterSplit) {
        if (StringUtils.isBlank(field)) {
            return "";
        }
        String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
        Object value = "";
        String[] relations = null;
        if (key.contains(".") && !newParams.containsKey(key)) {
            relations = key.split("\\.");
            StringBuilder prevRelation = new StringBuilder();
            for (int i = 0; i < relations.length; i++) {
                String relation = relations[i];
                // value = newParams.getOrDefault(prevRelation + relation, "");
                value = newParams.get(prevRelation + relation);
                try {
                    if (value == null || value instanceof String || value instanceof Integer) {
                        break;
                    }

                    Map<String, Object> parseMap = new HashMap<>();
                    if (value instanceof Map) {
                        parseMap = (Map<String, Object>) value;
                    } else if (!(value instanceof List)) {
                        String objStr = JSON.toJSONString(value);
                        parseMap = JSON.parseObject(objStr, HashMap.class);
                    }
                    for (Entry<String, Object> entry : parseMap.entrySet()) {
                        Object tempValue = entry.getValue();
                        if (tempValue != null) {
                            newParams.put(prevRelation + relation + "." + entry.getKey(), tempValue);
                        }
                    }
                    // value = parseMap.getOrDefault(relation, "");
                    value = newParams.get(relation);
                    prevRelation.append(relation).append(".");
                } catch (Exception e) {
                    // ExceptionHandler.insertException(e);
                }
            }
        } else {
            // value = newParams.getOrDefault(key, "");
            value = newParams.get(key);
        }
        value = value == null ? "" : value;
        return value;
    }

    /**
     * 解析field字段的值，例如，user.username,获取user对象的username属性值
     * 
     * @param field
     * @param newParams 已将所有map中的object转换为map后得到的新Map
     * @param beforeSplit
     * @param afterSplit
     * @return
     */
    public static Object parseMapValue(String field, Map<String, Object> newParams, String beforeSplit, String afterSplit) {
        if (StringUtils.isBlank(field)) {
            return "";
        }
        String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
        Object value = "";
        String[] relations = null;
        if (key.contains(".") && !newParams.containsKey(key)) {
            relations = key.split("\\.");
            Map<String, Object> tempParams = newParams;
            for (String tempKey : relations) {
                Object tempValue = tempParams.getOrDefault(tempKey, "");
                if (tempValue instanceof Map) {
                    tempParams = (Map<String, Object>) tempValue;
                    continue;
                }
                value = tempValue;
                break;
            }
        } else {
            value = newParams.getOrDefault(key, "");
        }
        return value;
    }

    /**
     * 判断是否存在某个属性
     * 
     * @param field
     * @param newParams
     * @return
     */
    public static boolean containsObjectKey(String field, Map<String, Object> newParams) {
        if (StringUtils.isBlank(field)) {
            return false;
        }
        String key = field.replaceAll("\\" + DEFAULT_BEFORE_SPLIT + "|\\" + DEFAULT_AFTER_SPLIT, "");
        Object value = "";
        boolean flag = false;
        String[] relations = null;
        if (key.contains(".") && !newParams.containsKey(key)) {
            relations = key.split("\\.");
            StringBuilder prevRelation = new StringBuilder();
            for (int i = 0; i < relations.length; i++) {
                if (newParams.containsKey(key)) {
                    flag = true;
                    break;
                }
                String relation = relations[i];
                value = newParams.getOrDefault(prevRelation + relation, "");
                try {
                    if (value instanceof String || value instanceof Integer) {
                        break;
                    }

                    Map<String, Object> parseMap = new HashMap<>();
                    if (value instanceof Map) {
                        parseMap = (Map<String, Object>) value;
                    } else if (!(value instanceof List)) {
                        String objStr = JSON.toJSONString(value);
                        parseMap = JSON.parseObject(objStr, HashMap.class);
                    }
                    for (Entry<String, Object> entry : parseMap.entrySet()) {
                        Object tempValue = entry.getValue();
                        if (tempValue != null) {
                            newParams.put(prevRelation + relation + "." + entry.getKey(), tempValue);
                        }
                    }
                    if (i < relations.length - 1) {
                        prevRelation.append(relation).append(".");
                    }
                } catch (Exception e) {
                    // ExceptionHandler.insertException(e);
                }
            }
        } else if (newParams.containsKey(key)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 移除需要忽略的属性
     * 
     * @param ignoreKey
     * @param params
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean removeIgnoreObject(String ignoreKey, Map<String, Object> params) {
        if (StringUtils.isBlank(ignoreKey)) {
            return false;
        }
        String key = ignoreKey.replaceAll("\\" + DEFAULT_BEFORE_SPLIT + "|\\" + DEFAULT_AFTER_SPLIT, "");
        Object value = "";
        boolean flag = false;
        String[] relations = null;
        if (key.contains(".") && !params.containsKey(key)) {
            relations = key.split("\\.");
            StringBuilder prevRelation = new StringBuilder();
            int i = 0;
            for (; i < relations.length; i++) {
                String relation = relations[i];
                value = params.getOrDefault(prevRelation + relation, "");
                try {
                    if (value instanceof String || value instanceof Integer) {
                        i++;
                        break;
                    }

                    Map<String, Object> parseMap = new HashMap<>();
                    if (value instanceof Map) {
                        parseMap = (Map<String, Object>) value;
                    } else if (!(value instanceof List)) {
                        String objStr = JSON.toJSONString(value);
                        parseMap = JSON.parseObject(objStr, HashMap.class);
                    }
                    for (Entry<String, Object> entry : parseMap.entrySet()) {
                        Object tempValue = entry.getValue();
                        if (tempValue != null) {
                            params.put(prevRelation + relation + "." + entry.getKey(), tempValue);
                        }
                    }
                    if (i < relations.length - 1) {
                        prevRelation.append(relation).append(".");
                    }
                } catch (Exception e) {
                    // ExceptionHandler.insertException(e);
                }
            }
            removeIgnoreObjectValue(ignoreKey, params);
        } else if (key.contains(".")) {
            relations = key.split("\\.");
            String prevRelation = StringUtils.join(ArrayUtils.subarray(relations, 0, relations.length - 1), ".");
            Object prevValue = params.getOrDefault(prevRelation, "");
            value = params.getOrDefault(key, "");
            if (relations.length > 1 && params.containsKey(key)) {
                if (!(prevValue == null || prevValue instanceof String || prevValue instanceof Integer)) {
                    String field = relations[relations.length - 1];
                    if (prevValue instanceof Map) {
                        ((Map<?, ?>) prevValue).remove(field);
                    } else if (prevValue instanceof Object) {
                        try {
                            field = field.substring(0, 1).toUpperCase() + field.substring(1);
                            Method method = prevValue.getClass().getMethod("set" + field, value.getClass());
                            method.setAccessible(true);
                            method.invoke(prevValue, new Object[] { null });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 移除需要忽略的属性值
     * 
     * @param ignoreKey
     * @param params
     * @return
     */
    private static Object removeIgnoreObjectValue(String ignoreKey, Map<String, Object> params) {
        String[] keys = StringUtils.split(ignoreKey, "\\.");
        if (keys.length > 1 && params.containsKey(ignoreKey)) {
            String prevRelation = StringUtils.join(ArrayUtils.subarray(keys, 0, keys.length - 1), ".");
            Object prevValue = params.getOrDefault(prevRelation, "");
            if (!(prevValue == null || prevValue instanceof String || prevValue instanceof Integer)) {
                String field = keys[keys.length - 1];
                if (prevValue instanceof Map) {
                    ((Map<?, ?>) prevValue).remove(field);
                } else if (prevValue instanceof Set) {
                    ((Set<?>) prevValue).remove(field);
                } else if (prevValue instanceof Object) {
                    try {
                        Object value = params.get(ignoreKey);
                        if (value != null) {
                            field = field.substring(0, 1).toUpperCase() + field.substring(1);
                            Method method = prevValue.getClass().getMethod("set" + field, value.getClass());
                            method.setAccessible(true);
                            method.invoke(prevValue, new Object[] { null });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return params;
    }
}