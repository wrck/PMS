package com.dp.plat.implementation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.dto.SettlementCreateRequest;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.service.ISettlementService;
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
 * Settlement management controller.
 */
@Tag(name = "结算管理", description = "Settlement management APIs")
@RestController
@RequestMapping("/api/impl/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final ISettlementService settlementService;

    @Operation(summary = "Create a settlement with line items")
    @PostMapping
    public Result<Settlement> create(@RequestBody SettlementCreateRequest request) {
        return Result.ok(settlementService.createSettlement(
                request.getSettlement(), request.getDetails()));
    }

    @Operation(summary = "Approve a settlement")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        settlementService.approve(id, opinion);
        return Result.ok();
    }

    @Operation(summary = "Reject a settlement")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        settlementService.reject(id, opinion);
        return Result.ok();
    }

    @Operation(summary = "Get settlement by id")
    @GetMapping("/{id}")
    public Result<Settlement> get(@PathVariable Long id) {
        return Result.ok(settlementService.getById(id));
    }

    @Operation(summary = "Paginated settlement query")
    @GetMapping("/list")
    public Result<Page<Settlement>> list(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         Settlement filters) {
        return Result.ok(settlementService.list(page, size, filters));
    }
}
