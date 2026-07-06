package com.dp.plat.implementation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.Idempotent;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.excel.ExcelUtils;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.dto.SettlementCreateRequest;
import com.dp.plat.implementation.dto.SettlementExportDTO;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.service.ISettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PreAuthorize("hasAuthority('implementation:settlement:add')")
    @OperLog(title = "结算管理", businessType = 1)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    @Idempotent
    public Result<Settlement> create(@Valid @RequestBody SettlementCreateRequest request) {
        return Result.ok(settlementService.createSettlement(
                request.getSettlement(), request.getDetails()));
    }

    @Operation(summary = "Approve a settlement")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('implementation:settlement:approve')")
    @OperLog(title = "结算管理", businessType = 2)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
    @Idempotent
    public Result<Void> approve(@PathVariable Long id, @RequestParam(required = false) String opinion) {
        settlementService.approve(id, opinion);
        return Result.ok();
    }

    @Operation(summary = "Reject a settlement")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('implementation:settlement:approve')")
    @OperLog(title = "结算管理", businessType = 2)
    @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
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

    @Operation(summary = "Export settlement list to Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('implementation:settlement:export')")
    @OperLog(title = "结算管理", businessType = 4)
    public void export(HttpServletResponse response, Settlement filters) {
        LambdaQueryWrapper<Settlement> wrapper = new LambdaQueryWrapper<>();
        if (filters != null) {
            wrapper.eq(filters.getTaskId() != null, Settlement::getTaskId, filters.getTaskId())
                    .eq(filters.getAgentId() != null, Settlement::getAgentId, filters.getAgentId())
                    .eq(filters.getProjectId() != null, Settlement::getProjectId, filters.getProjectId())
                    .eq(filters.getStatus() != null, Settlement::getStatus, filters.getStatus())
                    .like(filters.getSettlementNo() != null, Settlement::getSettlementNo, filters.getSettlementNo());
        }
        wrapper.orderByDesc(Settlement::getCreateTime);
        List<Settlement> rows = settlementService.list(wrapper);
        List<SettlementExportDTO> data = rows.stream().map(s -> {
            SettlementExportDTO dto = new SettlementExportDTO();
            BeanUtils.copyProperties(s, dto);
            return dto;
        }).toList();
        ExcelUtils.export(response, "settlement-list", "结算单清单", SettlementExportDTO.class, data);
    }
}
