package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.entity.LowCodeProcessBinding;
import com.dp.plat.lowcode.service.LowCodeProcessBindingService;
import com.dp.plat.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 低代码流程 Controller。
 *
 * <p>提供流程绑定 CRUD 与 Flowable 流程定义查询，复用 pms-workflow 的 WorkflowService。</p>
 */
@Tag(name = "低代码流程", description = "LowCode process binding & integration")
@RestController
@RequestMapping("/api/lowcode/process")
@RequiredArgsConstructor
public class LowCodeProcessController {

    private final LowCodeProcessBindingService bindingService;
    private final WorkflowService workflowService;

    @Operation(summary = "查询流程绑定列表")
    @GetMapping("/bindings")
    @PreAuthorize("hasAuthority('lowcode:process:list')")
    public Result<List<LowCodeProcessBinding>> listBindings() {
        return Result.ok(bindingService.list());
    }

    @Operation(summary = "保存流程绑定")
    @PostMapping("/bindings")
    @PreAuthorize("hasAuthority('lowcode:process:edit')")
    @OperLog(title = "低代码流程绑定", businessType = 1)
    public Result<LowCodeProcessBinding> saveBinding(@RequestBody LowCodeProcessBinding binding) {
        bindingService.saveOrUpdate(binding);
        return Result.ok(binding);
    }

    @Operation(summary = "查询 Flowable 流程定义列表")
    @GetMapping("/definitions")
    @PreAuthorize("hasAuthority('lowcode:process:list')")
    public Result<Map<String, Object>> listDefinitions(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return workflowService.listProcessDefinitions(page, size);
    }

    @Operation(summary = "根据 task 获取绑定的表单 code")
    @GetMapping("/task-form")
    @PreAuthorize("hasAuthority('lowcode:process:list')")
    public Result<String> getTaskForm(@RequestParam String processDefinitionKey,
                                      @RequestParam String nodeId) {
        return Result.ok(bindingService.getFormCodeForNode(processDefinitionKey, nodeId));
    }
}
