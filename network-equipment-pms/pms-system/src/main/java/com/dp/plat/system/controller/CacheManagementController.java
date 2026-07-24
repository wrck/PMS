package com.dp.plat.system.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p><b>@Deprecated</b>：Redis 缓存监控已由 yudao 底座提供，替代接口为 {@code /admin-api/infra/redis/*}（yudao RedisController）。</p>
 *
 * <p>缓存管理面板：查看缓存名称、按名称清空缓存、一键清空全部缓存。</p>
 *
 * <p>当前缓存命名空间由 {@link com.dp.plat.system.config.RedisConfig} 预置：
 * {@code sysDict}、{@code sysMenu}、{@code sysConfig}、{@code sysRole}。</p>
 */
@Deprecated
@Slf4j
@Tag(name = "缓存管理（已弃用）", description = "Cache management APIs")
@RestController
@RequestMapping("/api/system/cache")
@RequiredArgsConstructor
public class CacheManagementController {

    private final CacheManager cacheManager;

    @Operation(summary = "获取所有缓存名称")
    @GetMapping("/names")
    public Result<List<String>> names() {
        Collection<String> nameSet = cacheManager.getCacheNames();
        return Result.ok(new ArrayList<>(nameSet));
    }

    @Operation(summary = "清空全部缓存")
    @PostMapping("/clearAll")
    @PreAuthorize("@ss.hasPermission('system:cache:clear')")
    @OperLog(title = "缓存管理", businessType = 2)
    public Result<Void> clearAll() {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
        log.info("已清空全部缓存: {}", cacheManager.getCacheNames());
        return Result.ok();
    }

    @Operation(summary = "按名称清空指定缓存")
    @PostMapping("/clear/{cacheName}")
    @PreAuthorize("@ss.hasPermission('system:cache:clear')")
    @OperLog(title = "缓存管理", businessType = 2)
    public Result<Void> clearByName(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return Result.fail("缓存不存在: " + cacheName);
        }
        cache.clear();
        log.info("已清空缓存: {}", cacheName);
        return Result.ok();
    }
}
