package com.dp.plat.governance.risk.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.governance.risk.dto.RiskMatrixDto;
import com.dp.plat.governance.risk.entity.Risk;
import com.dp.plat.governance.risk.service.IRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Risk register controller (governance three-books: risk register).
 */
@Tag(name = "风险管理", description = "Risk register governance APIs")
@RestController
@RequestMapping("/api/governance/risk")
@RequiredArgsConstructor
public class RiskController {

    private final IRiskService riskService;

    @Operation(summary = "创建风险")
    @PostMapping
    public Result<Risk> create(@RequestBody Risk risk) {
        return riskService.create(risk);
    }

    @Operation(summary = "更新风险")
    @PutMapping
    public Result<?> update(@RequestBody Risk risk) {
        return riskService.update(risk);
    }

    @Operation(summary = "删除风险")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return riskService.delete(id);
    }

    @Operation(summary = "查询全部风险")
    @GetMapping
    public Result<List<Risk>> list() {
        return riskService.listAll();
    }

    @Operation(summary = "根据ID查询风险")
    @GetMapping("/{id}")
    public Result<Risk> getById(@PathVariable Long id) {
        return riskService.getById(id);
    }

    @Operation(summary = "根据项目ID查询风险列表")
    @GetMapping("/project/{projectId}")
    public Result<List<Risk>> listByProject(@PathVariable Long projectId) {
        return riskService.listByProject(projectId);
    }

    @Operation(summary = "标记风险已发生并转化为问题")
    @PostMapping("/{id}/mark-occurred")
    public Result<?> markOccurred(@PathVariable Long id) {
        return riskService.markOccurred(id);
    }

    @Operation(summary = "升级风险为变更请求")
    @PostMapping("/{id}/escalate")
    public Result<?> escalate(@PathVariable Long id) {
        return riskService.escalate(id);
    }

    @Operation(summary = "获取项目风险矩阵(5x5)")
    @GetMapping("/matrix")
    public Result<RiskMatrixDto> riskMatrix(@RequestParam(required = false) Long projectId) {
        return riskService.riskMatrix(projectId);
    }
}
