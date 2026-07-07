package com.dp.plat.lowcode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.entity.LowCodeMicroflowExecutionLog;
import com.dp.plat.lowcode.mapper.LowCodeMicroflowExecutionLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 低代码微流执行轨迹 Controller（借鉴 Joget APM）。
 *
 * <p>提供按 executionId 查询某次执行的所有节点轨迹，以及按 microflowId 查询最近执行的接口。</p>
 */
@Tag(name = "低代码微流执行轨迹", description = "LowCode microflow execution log APIs")
@RestController
@RequestMapping("/api/lowcode/microflow-execution-log")
@RequiredArgsConstructor
public class LowCodeMicroflowExecutionLogController {

    private final LowCodeMicroflowExecutionLogMapper executionLogMapper;

    @Operation(summary = "按执行ID查询节点轨迹")
    @GetMapping("/{executionId}")
    @PreAuthorize("hasAuthority('lowcode:microflow:exec')")
    public Result<List<LowCodeMicroflowExecutionLog>> getByExecutionId(@PathVariable String executionId) {
        List<LowCodeMicroflowExecutionLog> logs = executionLogMapper.selectList(
                new LambdaQueryWrapper<LowCodeMicroflowExecutionLog>()
                        .eq(LowCodeMicroflowExecutionLog::getExecutionId, executionId)
                        .orderByAsc(LowCodeMicroflowExecutionLog::getId));
        return Result.ok(logs);
    }

    @Operation(summary = "查询微流最近执行轨迹")
    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('lowcode:microflow:exec')")
    public Result<List<LowCodeMicroflowExecutionLog>> getRecent(@RequestParam Long microflowId,
                                                                @RequestParam(defaultValue = "50") Integer limit) {
        int safeLimit = limit == null || limit <= 0 || limit > 500 ? 50 : limit;
        List<LowCodeMicroflowExecutionLog> logs = executionLogMapper.selectList(
                new LambdaQueryWrapper<LowCodeMicroflowExecutionLog>()
                        .eq(LowCodeMicroflowExecutionLog::getMicroflowId, microflowId)
                        .orderByDesc(LowCodeMicroflowExecutionLog::getId)
                        .last("LIMIT " + safeLimit));
        return Result.ok(logs);
    }
}
