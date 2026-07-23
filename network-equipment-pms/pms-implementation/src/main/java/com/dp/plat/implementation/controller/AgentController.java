package com.dp.plat.implementation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.service.IAgentService;
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

import java.util.Map;

/**
 * Agent management controller.
 */
@Tag(name = "代理商管理", description = "Agent management APIs")
@RestController
@RequestMapping("/api/impl/agent")
@RequiredArgsConstructor
public class AgentController {

    private final IAgentService agentService;

    @Operation(summary = "Paginated agent query")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('implementation:agent:list')")
    public Result<Page<Agent>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Agent filters) {
        return Result.ok(agentService.list(page, size, filters));
    }

    @Operation(summary = "Get agent by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('implementation:agent:list')")
    public Result<Agent> get(@PathVariable Long id) {
        return Result.ok(agentService.getById(id));
    }

    @Operation(summary = "Create agent")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('implementation:agent:add')")
    @OperLog(title = "代理商管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody Agent agent) {
        return Result.ok(agentService.save(agent));
    }

    @Operation(summary = "Update agent")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('implementation:agent:edit')")
    @OperLog(title = "代理商管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody Agent agent) {
        return Result.ok(agentService.updateById(agent));
    }

    @Operation(summary = "Delete agent")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('implementation:agent:remove')")
    @OperLog(title = "代理商管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(agentService.removeById(id));
    }

    @Operation(summary = "Get average scores for an agent")
    @GetMapping("/{id}/scores")
    @PreAuthorize("@ss.hasPermission('implementation:agent:list')")
    public Result<Map<String, Object>> scores(@PathVariable Long id) {
        return Result.ok(agentService.getScore(id));
    }
}
