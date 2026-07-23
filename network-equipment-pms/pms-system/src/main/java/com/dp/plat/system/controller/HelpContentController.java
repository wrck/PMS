package com.dp.plat.system.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.HelpContent;
import com.dp.plat.system.service.IHelpContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Help content controller for the user guide / help center.
 *
 * <p>Read endpoints ({@code /list}, {@code /{id}}, {@code /categories}) are public;
 * write endpoints require admin permissions ({@code system:help:create} /
 * {@code system:help:edit} / {@code system:help:remove}).</p>
 */
@Tag(name = "帮助中心", description = "Help content management APIs")
@RestController
@RequestMapping("/api/system/help-content")
@RequiredArgsConstructor
public class HelpContentController {

    private static final List<String> ALL_CATEGORIES =
            Arrays.asList("QUICK_START", "FAQ", "VIDEO", "ADVANCED");

    private final IHelpContentService helpContentService;

    @Operation(summary = "List enabled help contents, optionally filtered by category")
    @GetMapping("/list")
    public Result<List<HelpContent>> list(@RequestParam(required = false) String category) {
        return Result.ok(helpContentService.listByCategory(category));
    }

    @Operation(summary = "Get help content by id (public)")
    @GetMapping("/{id}")
    public Result<HelpContent> get(@PathVariable Long id) {
        HelpContent content = helpContentService.getById(id);
        // 异步累加浏览次数（同步执行亦可，量级较小）
        if (content != null) {
            helpContentService.incrementViewCount(id);
        }
        return Result.ok(content);
    }

    @Operation(summary = "List all help content categories")
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.ok(ALL_CATEGORIES);
    }

    @Operation(summary = "Create help content (admin)")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:help:create')")
    @OperLog(title = "帮助中心", businessType = 1)
    @RateLimit(capacity = 20, refillTokens = 20, refillPeriodSeconds = 60)
    public Result<Boolean> create(@Valid @RequestBody HelpContent content) {
        if (content.getSortOrder() == null) {
            content.setSortOrder(0);
        }
        if (content.getStatus() == null) {
            content.setStatus("0");
        }
        if (content.getViewCount() == null) {
            content.setViewCount(0);
        }
        return Result.ok(helpContentService.save(content));
    }

    @Operation(summary = "Update help content (admin)")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:help:edit')")
    @OperLog(title = "帮助中心", businessType = 2)
    @RateLimit(capacity = 30, refillTokens = 30, refillPeriodSeconds = 60)
    public Result<Boolean> update(@Valid @RequestBody HelpContent content) {
        return Result.ok(helpContentService.updateById(content));
    }

    @Operation(summary = "Delete help content (admin)")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:help:remove')")
    @OperLog(title = "帮助中心", businessType = 3)
    @RateLimit(capacity = 20, refillTokens = 20, refillPeriodSeconds = 60)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(helpContentService.removeById(id));
    }
}
