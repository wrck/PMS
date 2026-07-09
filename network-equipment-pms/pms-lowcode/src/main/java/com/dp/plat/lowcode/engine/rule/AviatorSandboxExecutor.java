package com.dp.plat.lowcode.engine.rule;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Feature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aviator 沙箱执行器（借鉴 {@code GroovySandboxExecutor} 白名单思路）。
 *
 * <p>针对低代码规则引擎执行用户表达式的场景，使用独立的
 * {@link AviatorEvaluatorInstance}（非全局单例 {@link AviatorEvaluator}），
 * 避免污染平台其他模块对 Aviator 的依赖。</p>
 *
 * <p><b>禁用的危险特性</b>：
 * <ul>
 *   <li>{@link Feature#NewInstance} — 禁止 {@code new} 关键字与 Java 类加载，
 *       阻断 {@code Runtime.getRuntime()}、{@code new ProcessBuilder(...)} 等反射调用链</li>
 *   <li>{@link Feature#Module} — 禁止 Java 9+ 模块加载</li>
 *   <li>{@link Feature#InternalVars} — 禁用 {@code __env__} 等内部变量，
 *       避免泄漏运行时上下文</li>
 * </ul>
 * 保留业务表达式所需的基础特性：算术/逻辑运算、三元、字符串拼接、List/Map 字面量、
 * 闭包（Lambda）、条件分支（If）等。</p>
 *
 * <p><b>移除的系统函数</b>（保留业务常用函数如 {@code string.contains / seq.list}）：
 * {@code sysdate}、{@code now}、{@code rand}、{@code rand_long}、{@code date_to_string}、
 * {@code get_sys_prop}、{@code get_sys_env}、{@code load}、{@code require}。
 * 移除这些函数可防止用户表达式读取系统时间/属性/环境变量并据此绕过沙箱。</p>
 *
 * <p><b>安全等级</b>：Aviator 沙箱并非绝对安全，但禁用 NewInstance/Module/InternalVars
 * 可拦截绝大多数常见的命令注入、反射调用与系统信息读取。表达式来源应在调用方做权限校验。</p>
 */
@Slf4j
@Component
public class AviatorSandboxExecutor {

    /** 独立的 Aviator 求值器实例（非全局单例） */
    private final AviatorEvaluatorInstance instance;

    public AviatorSandboxExecutor() {
        // newInstance() 返回独立实例，其配置变更不影响 AviatorEvaluator 全局单例
        this.instance = AviatorEvaluator.newInstance();
        // 禁用危险特性：NewInstance（new 关键字 + 类加载）/ Module（模块加载）/ InternalVars（内部变量）
        this.instance.disableFeature(Feature.NewInstance);
        this.instance.disableFeature(Feature.Module);
        this.instance.disableFeature(Feature.InternalVars);
        // 移除危险系统函数（函数不存在时 ignore）
        removeDangerousFunctions();
        log.info("[AviatorSandbox] 初始化完成: 已禁用 NewInstance/Module/InternalVars，移除 {} 个系统函数",
                DANGEROUS_FUNCTIONS.length);
    }

    /** 危险系统函数名单（按 Aviator 5.4.3 内置函数表选取） */
    private static final String[] DANGEROUS_FUNCTIONS = {
            "sysdate", "now", "rand", "rand_long", "date_to_string",
            "get_sys_prop", "get_sys_env", "load", "require"
    };

    private void removeDangerousFunctions() {
        for (String name : DANGEROUS_FUNCTIONS) {
            try {
                this.instance.removeFunction(name);
            } catch (Exception e) {
                // 函数未注册或已被移除，忽略
            }
        }
    }

    /**
     * 在沙箱约束下执行 Aviator 表达式。
     *
     * @param expression Aviator 表达式
     * @param env        上下文变量（不可为 null，可为空 Map）
     * @return 表达式求值结果
     * @throws com.googlecode.aviator.exception.ExpressionSyntaxErrorException
     *         表达式语法错误或命中禁用特性（如使用 {@code new}）
     * @throws com.googlecode.aviator.exception.FunctionNotFoundException
     *         调用了被移除的系统函数（如 {@code sysdate()}）
     */
    public Object execute(String expression, Map<String, Object> env) {
        return this.instance.execute(expression, env);
    }
}
