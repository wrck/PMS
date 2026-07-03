package com.dp.plat.integration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.d365.PurchaseReceiptHeader;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.D365IntegrationService;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.dp.plat.integration.service.RetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Integration management controller: query logs, manual retry and manual push.
 */
@Tag(name = "集成管理", description = "External system integration management APIs")
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final IIntegrationLogService integrationLogService;
    private final RetryService retryService;
    private final D365IntegrationService d365IntegrationService;
    private final FpIntegrationService fpIntegrationService;

    @Operation(summary = "Paginated integration log query")
    @GetMapping("/log/list")
    public Result<Page<IntegrationLog>> list(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             IntegrationLog filters) {
        return Result.ok(integrationLogService.list(page, size, filters));
    }

    @Operation(summary = "Get integration log detail by id")
    @GetMapping("/log/{id}")
    public Result<IntegrationLog> get(@PathVariable Long id) {
        return Result.ok(integrationLogService.getById(id));
    }

    @Operation(summary = "Manually retry a failed integration by log id")
    @PostMapping("/log/{id}/retry")
    public Result<IntegrationLog> retry(@PathVariable Long id) {
        return Result.ok(retryService.retryLog(id));
    }

    @Operation(summary = "Manually push a purchase receipt to D365")
    @PostMapping("/d365/push-receipt")
    public Result<String> pushReceipt(@RequestBody PurchaseReceiptHeader header) {
        return Result.ok(d365IntegrationService.pushPurchaseReceipt(header));
    }

    @Operation(summary = "Manually push a settlement to FP")
    @PostMapping("/fp/push-settlement")
    public Result<FpResponse<String>> pushSettlement(@RequestBody SettlementPushRequest request) {
        return Result.ok(fpIntegrationService.pushSettlement(request));
    }
}
