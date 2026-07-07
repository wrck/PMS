package com.dp.plat.lowcode.engine.microflow;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Groovy 沙箱执行器（借鉴 Mendix/OutSystems 沙箱机制）。
 *
 * <p>使用 {@link SecureASTCustomizer} 配置白名单与黑名单，阻止用户表达式执行危险操作：
 * <ul>
 *   <li>禁用 receivers：System、Runtime、ProcessBuilder、Thread、ClassLoader、File</li>
 *   <li>imports 白名单：java.lang、java.util、java.math</li>
 *   <li>禁止显式 import 与静态 import</li>
 * </ul>
 * 保留基本运算（+ - * / &gt; &lt; == != &amp;&amp; || !）、List/Map 字面量、字符串/数字字面量等。</p>
 *
 * <p>注意：SecureASTCustomizer 并非绝对安全的沙箱，但能拦截绝大多数常见的命令注入与文件/反射访问。</p>
 */
@Component
public class GroovySandboxExecutor {

    private final CompilerConfiguration compilerConfiguration;

    public GroovySandboxExecutor() {
        SecureASTCustomizer customizer = new SecureASTCustomizer();
        // 不开启间接 import 检查 —— 该选项配合空 importsWhitelist 会拦截所有方法调用（包括 .size()），
        // 过度限制基本运算。改用 receiversBlackList 在 AST 层拦截危险类的方法调用。
        customizer.setIndirectImportCheckEnabled(false);
        // 禁止任何显式 import 语句（空列表 = 无白名单条目）
        customizer.setImportsWhitelist(Collections.emptyList());
        // 允许的 star imports：java.lang / java.util / java.math + groovy.lang（闭包 Closure 为 Groovy 基础语法）
        customizer.setStarImportsWhitelist(Arrays.asList("java.lang", "java.util", "java.math", "groovy.lang"));
        // 禁止静态 import
        customizer.setStaticImportsWhitelist(Collections.emptyList());
        // 禁用危险类的 receivers（方法调用与构造），并阻止通过 GroovyShell/GroovyClassLoader 等绕过沙箱
        customizer.setReceiversBlackList(Arrays.asList(
                System.class.getName(),
                Runtime.class.getName(),
                ProcessBuilder.class.getName(),
                Thread.class.getName(),
                ClassLoader.class.getName(),
                File.class.getName(),
                groovy.lang.GroovyShell.class.getName(),
                groovy.lang.GroovyClassLoader.class.getName(),
                groovy.lang.GroovySystem.class.getName()
        ));
        this.compilerConfiguration = new CompilerConfiguration();
        this.compilerConfiguration.addCompilationCustomizers(customizer);
    }

    /**
     * 在沙箱约束下执行 Groovy 表达式。
     *
     * @param binding    变量绑定
     * @param expression Groovy 表达式
     * @return 表达式求值结果
     */
    public Object evaluate(Binding binding, String expression) {
        GroovyShell shell = new GroovyShell(binding, compilerConfiguration);
        return shell.evaluate(expression);
    }
}
