package com.dp.plat.common.config;

import com.dp.plat.common.aspect.IdempotentKeyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置：注册 {@link IdempotentKeyInterceptor} 拦截器。
 *
 * <p>拦截所有 {@code /**} 路径的请求，从 {@code X-Idempotent-Key} 头读取
 * 幂等键并写入 request attribute，为 {@code IdempotentAspect} 提供备用访问方式。</p>
 *
 * <p>注意：本配置仅注册拦截器，{@code IdempotentAspect} 切面的实际生效依赖
 * AspectJ 自动代理（已在 {@link AspectConfig} 通过 {@code @EnableAspectJAutoProxy} 启用）。</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final IdempotentKeyInterceptor idempotentKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempotentKeyInterceptor)
                .addPathPatterns("/**");
    }
}
