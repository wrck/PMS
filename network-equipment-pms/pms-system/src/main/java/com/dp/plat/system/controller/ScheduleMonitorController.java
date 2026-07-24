package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.ScheduleLog;
import com.dp.plat.system.mapper.ScheduleLogMapper;
import com.dp.plat.system.service.IScheduleLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p><b>@Deprecated</b>：定时任务管理已由 yudao 底座提供，替代接口为 {@code /admin-api/infra/job/*} + {@code /admin-api/infra/job-log/*}。</p>
 *
 * <p>定时任务监控控制器：最近执行记录、失败记录、按任务名分组统计、手动重试占位。</p>
 *
 * <p>统计与 24h 失败查询通过 {@link LambdaQueryWrapper} + Java Stream 实现，
 * 不改写 mapper XML。手动重试仅记录一条 {@code MANUAL_TRIGGER} 日志，
 * 实际重试需任务自身支持（本接口不真正触发任务执行）。</p>
 */
@Deprecated
@Slf4j
@Tag(name = "定时任务监控（已弃用）", description = "Schedule task monitor APIs")
@RestController
@RequestMapping("/api/system/schedule")
@RequiredArgsConstructor
public class ScheduleMonitorController {

    private static final String STATUS_FAIL = "FAIL";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_MANUAL_TRIGGER = "MANUAL_TRIGGER";
    private static final String TRIGGER_MANUAL = "MANUAL";
    private static final int RECENT_LIMIT = 100;

    private final IScheduleLogService scheduleLogService;
    private final ScheduleLogMapper scheduleLogMapper;

    @Operation(summary = "最近 100 条定时任务日志")
    @GetMapping("/recent")
    public Result<List<ScheduleLog>> recent() {
        // 通过 service 分页取第一页 100 条（按 start_time 倒序，由 service 内部排序保证）
        List<ScheduleLog> records = scheduleLogService.page(1, RECENT_LIMIT, null).getRecords();
        return Result.ok(records);
    }

    @Operation(summary = "最近 24h 失败的定时任务")
    @GetMapping("/failed")
    public Result<List<ScheduleLog>> failedRecent24h() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<ScheduleLog> failed = scheduleLogMapper.selectList(new LambdaQueryWrapper<ScheduleLog>()
                .eq(ScheduleLog::getStatus, STATUS_FAIL)
                .ge(ScheduleLog::getStartTime, since)
                .orderByDesc(ScheduleLog::getStartTime));
        return Result.ok(failed);
    }

    @Operation(summary = "按任务名分组的成功/失败次数统计")
    @GetMapping("/statistic")
    public Result<List<Map<String, Object>>> statistic() {
        // 取最近 1000 条日志按 task_name 分组统计成功/失败次数，避免全表扫描。
        List<ScheduleLog> logs = scheduleLogMapper.selectList(new LambdaQueryWrapper<ScheduleLog>()
                .orderByDesc(ScheduleLog::getStartTime)
                .last("LIMIT 1000"));
        Map<String, long[]> agg = new LinkedHashMap<>();
        for (ScheduleLog log : logs) {
            String name = log.getTaskName() == null ? "" : log.getTaskName();
            long[] counts = agg.computeIfAbsent(name, k -> new long[2]);
            if (STATUS_SUCCESS.equals(log.getStatus())) {
                counts[0]++;
            } else if (STATUS_FAIL.equals(log.getStatus())) {
                counts[1]++;
            }
        }
        List<Map<String, Object>> result = agg.entrySet().stream()
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("taskName", e.getKey());
                    row.put("successCount", e.getValue()[0]);
                    row.put("failCount", e.getValue()[1]);
                    return row;
                })
                .collect(Collectors.toList());
        return Result.ok(result);
    }

    @Operation(summary = "手动重试占位接口（仅记录日志，不真正触发任务）")
    @PostMapping("/retry/{id}")
    @PreAuthorize("@ss.hasPermission('system:schedule:retry')")
    @OperLog(title = "定时任务监控", businessType = 2)
    public Result<String> retry(@PathVariable Long id) {
        ScheduleLog original = scheduleLogMapper.selectById(id);
        if (original == null) {
            return Result.fail("定时任务日志不存在: " + id);
        }
        // 不真正重试任务，仅记录一条 MANUAL_TRIGGER 日志用于审计。
        ScheduleLog triggerLog = ScheduleLog.builder()
                .taskName(original.getTaskName())
                .taskGroup(original.getTaskGroup())
                .cronExpression(original.getCronExpression())
                .startTime(LocalDateTime.now())
                .status(STATUS_MANUAL_TRIGGER)
                .triggerType(TRIGGER_MANUAL)
                .errorMessage("手动重试占位：实际重试需任务自身支持")
                .build();
        scheduleLogService.record(triggerLog);
        log.info("记录手动重试占位日志 taskId={} taskName={}", id, original.getTaskName());
        return Result.ok("手动重试需任务自身支持，已记录触发日志");
    }
}
