package com.dp.plat.lowcode.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.dto.DeployBpmnRequest;
import com.dp.plat.lowcode.entity.LowCodeProcessBinding;
import com.dp.plat.lowcode.service.LowCodeProcessBindingService;
import com.dp.plat.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * 低代码流程 Controller。
 *
 * <p>提供流程绑定 CRUD、Flowable 流程定义查询、BPMN XML 部署与已部署流程
 * 定义的 BPMN XML 读取，复用 pms-workflow 的 WorkflowService。</p>
 */
@Tag(name = "低代码流程", description = "LowCode process binding & integration")
@RestController
@RequestMapping("/api/lowcode/process")
@RequiredArgsConstructor
public class LowCodeProcessController {

    private final LowCodeProcessBindingService bindingService;
    private final WorkflowService workflowService;
    /** Flowable RuntimeService，用于查询流程实例当前活动节点（预览高亮） */
    private final RuntimeService runtimeService;

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

    @Operation(summary = "部署 BPMN XML 到 Flowable")
    @PostMapping("/deploy")
    @PreAuthorize("hasAuthority('lowcode:process:edit')")
    @OperLog(title = "低代码流程部署", businessType = 1)
    public Result<Map<String, Object>> deployBpmnXml(@Valid @RequestBody DeployBpmnRequest request) {
        // 将 XML 字符串转为内存 MultipartFile，复用 WorkflowService.deployProcess
        String resourceName = request.getName() + ".bpmn20.xml";
        MultipartFile multipart = new InMemoryMultipartFile(
                "file", resourceName, "application/xml",
                request.getXml().getBytes(StandardCharsets.UTF_8));
        return workflowService.deployProcess(multipart);
    }

    @Operation(summary = "获取已部署流程定义的 BPMN XML")
    @GetMapping("/bpmn-xml")
    @PreAuthorize("hasAuthority('lowcode:process:list')")
    public Result<String> getBpmnXml(@RequestParam String processDefinitionKey) {
        return Result.ok(workflowService.getProcessDefinitionBpmnXml(processDefinitionKey));
    }

    @Operation(summary = "查询流程实例当前活动节点 ID 列表")
    @GetMapping("/instance/activity-ids")
    @PreAuthorize("hasAuthority('lowcode:process:list')")
    public Result<List<String>> getActivityIds(@RequestParam String processInstanceId) {
        return Result.ok(runtimeService.getActiveActivityIds(processInstanceId));
    }

    /**
     * 内存 MultipartFile 实现。
     *
     * <p>用于将前端提交的 BPMN XML 字符串包装为 {@link MultipartFile}，
     * 以便复用 {@code WorkflowService.deployProcess(MultipartFile)}。
     * MockMultipartFile 位于 spring-test（test 作用域），不能用于主代码，
     * 故在此提供一个等价的最小实现。</p>
     */
    private static final class InMemoryMultipartFile implements MultipartFile {

        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        InMemoryMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content == null ? new byte[0] : content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (OutputStream out = Files.newOutputStream(dest.toPath())) {
                out.write(content);
            }
        }
    }
}
