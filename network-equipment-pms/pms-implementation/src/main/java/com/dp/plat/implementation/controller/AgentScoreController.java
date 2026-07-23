package com.dp.plat.implementation.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.service.IAgentScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Agent quality evaluation controller.
 */
@Tag(name = "代理商评价", description = "Agent quality evaluation APIs")
@RestController
@RequestMapping("/api/impl/agent/score")
@RequiredArgsConstructor
public class AgentScoreController {

    private final IAgentScoreService agentScoreService;

    @Operation(summary = "Evaluate an agent")
    @PostMapping("/evaluate")
    @PreAuthorize("@ss.hasPermission('implementation:agentScore:add')")
    @OperLog(title = "代理商评价", businessType = 1)
    public Result<AgentScore> evaluate(@Valid @RequestBody AgentScore score) {
        return Result.ok(agentScoreService.evaluate(score));
    }

    @Operation(summary = "List evaluations by agent id")
    @GetMapping("/agent/{agentId}")
    public Result<List<AgentScore>> listByAgent(@PathVariable Long agentId) {
        return Result.ok(agentScoreService.listByAgentId(agentId));
    }
}
