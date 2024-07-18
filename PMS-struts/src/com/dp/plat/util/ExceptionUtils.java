package com.dp.plat.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dp.plat.exception.CustomRuntimeException;

public class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {
    /**
     *  默认自定义抛出异常类集合
     */
    private final static Set<Class<? extends Throwable>> defaultCustomThrowExceptions = new HashSet<>(Arrays.asList(CustomRuntimeException.class));
    /**
     *  默认自定义抛出异常包集合
     */
    private final static Set<String> defaultCustomThrowPackages = new HashSet<>(Arrays.asList("com.dp.plat"));
    /**
     *  自定义抛出异常类集合
     */
    private static Set<Class<? extends Throwable>> customThrowExceptions = new HashSet<>(defaultCustomThrowExceptions);
    /**
     *  自定义抛出异常包集合
     */
    private static Set<String> customThrowPackages = new HashSet<>(defaultCustomThrowPackages);
    
    /**
     * 获取异常消息文本，可选择包括异常堆栈跟踪的文本
     * 
     * @param throwable 异常对象
     * @return 优先自定义异常消息文本 或者 异常消息文本
     */
    public static String getMessage(Throwable throwable) {
        return getMessage(throwable, false);
    }

    /**
     * 获取优先获取自定义异常消息文本，如果不存在自定义异常，则返回异常对象的消息文本，可选择包括异常堆栈跟踪的文本
     * 
     * @param throwable 异常对象
     * @param includeStackTrace 包括异常堆栈跟踪的文本
     * @return 优先自定义异常消息文本，如果不是自定义信息，则includeStackTrace ? 异常堆栈跟踪的文本 : 异常消息文本
     */
    public static String getMessage(Throwable throwable, boolean includeStackTrace) {
        if (throwable == null) {
            return null;
        }
        Throwable customException = getCustomException(throwable);
        if (customException != null) {
            return customException.getMessage();
        }

        return includeStackTrace ? ExceptionUtils.getStackTrace(throwable) : throwable.getMessage();
    }

    /**
     * 从异常堆栈中获取自定义异常
     *
     * @param throwable 异常对象
     * @return 自定义异常对象
     */
    public static Throwable getCustomException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return ExceptionUtils.getThrowableList(throwable)
                .stream()
                .filter(ExceptionUtils::isCustomException)
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断异常是否为自定义异常或指定包路径下的异常
     *
     * @param throwable 异常对象
     * @return 如果异常是自定义异常或指定包路径下的异常，返回true，否则返回false
     */
    public static boolean isCustomException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        return isInstanceOfCustomThrowExceptions(throwable) || isInCustomThrowPackages(throwable) || RuntimeException.class.equals(throwable.getClass());
    }

    /**
     * 判断异常是否属于自定义异常类集合中的某个异常
     *
     * @param throwable 异常对象
     * @return 如果异常属于自定义异常类集合中的某个异常，返回true，否则返回false
     */
    public static boolean isInstanceOfCustomThrowExceptions(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        
        return customThrowExceptions
                .stream()
                .anyMatch(exceptionClass -> ExceptionUtils.throwableOfType(throwable, exceptionClass) != null);
    }

    /**
     * 判断异常是否属于自定义异常包集合中的异常
     *
     * @param throwable 异常对象
     * @return 如果异常属于自定义异常包集合中的异常，返回true，否则返回false
     */
    public static boolean isInCustomThrowPackages(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        String packageName = throwable.getClass().getPackage().getName();
        for (String pack : customThrowPackages) {
            if (packageName.startsWith(pack)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 设置自定义抛出异常类集合
     *
     * @param exceptions 自定义抛出异常类
     */
    @SafeVarargs
    public static void setCustomThrowExceptions(Class<? extends Throwable>... exceptions) {
        customThrowExceptions.retainAll(defaultCustomThrowExceptions);
        addCustomThrowExceptions(exceptions);
    }

    /**
     * 设置自定义抛出异常类集合
     *
     * @param exceptions 自定义抛出异常类的类全名
     * @throws ClassNotFoundException 如果类全名对应的类不存在
     */
    public static void setCustomThrowExceptions(String... exceptions) throws ClassNotFoundException {
        customThrowExceptions.retainAll(defaultCustomThrowExceptions);
        addCustomThrowExceptions(exceptions);
    }

    /**
     * 设置自定义抛出异常包集合
     *
     * @param packages 自定义抛出异常包的包名
     */
    public static void setCustomThrowPackages(String... packages) {
        customThrowPackages.clear();
        for (String pack : packages) {
            customThrowPackages.add(pack);
        }
    }

    /**
     * 向自定义抛出异常类集合中添加异常类
     *
     * @param exceptions 自定义抛出异常类
     */
    @SafeVarargs
    public static void addCustomThrowExceptions(Class<? extends Throwable>... exceptions) {
        for (Class<? extends Throwable> exception : exceptions) {
            customThrowExceptions.add(exception);
        }
    }

    /**
     * 向自定义抛出异常类集合中添加异常类
     *
     * @param exceptions 自定义抛出异常类的类全名
     * @throws ClassNotFoundException 如果类全名对应的类不存在
     */
    @SuppressWarnings("unchecked")
    public static void addCustomThrowExceptions(String... exceptions) throws ClassNotFoundException {
        for (String exception : exceptions) {
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) Class.forName(exception);
            customThrowExceptions.add(exceptionClass);
        }
    }

    /**
     * 向自定义抛出异常包集合中添加包名
     *
     * @param packages 自定义抛出异常包的包名
     */
    public static void addCustomThrowPackages(String... packages) {
        for (String pack : packages) {
            customThrowPackages.add(pack);
        }
    }

}