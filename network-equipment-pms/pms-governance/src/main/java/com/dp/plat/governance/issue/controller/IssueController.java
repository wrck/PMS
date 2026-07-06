package com.dp.plat.governance.issue.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.issue.entity.Issue;
import com.dp.plat.governance.issue.service.IIssueService;
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

import java.util.List;

/**
 * Issue log controller (governance three-books: issue log).
 */
@Tag(name = "问题管理", description = "Issue log governance APIs")
@RestController
@RequestMapping("/api/governance/issue")
@RequiredArgsConstructor
public class IssueController {

    private final IIssueService issueService;

    @Operation(summary = "创建问题")
    @PostMapping
    @PreAuthorize("hasAuthority('governance:issue:add')")
    @OperLog(title = "问题管理", businessType = 1)
    public Result<Issue> create(@Valid @RequestBody Issue issue) {
        return issueService.create(issue);
    }

    @Operation(summary = "更新问题")
    @PutMapping
    @PreAuthorize("hasAuthority('governance:issue:edit')")
    @OperLog(title = "问题管理", businessType = 2)
    public Result<?> update(@Valid @RequestBody Issue issue) {
        return issueService.update(issue);
    }

    @Operation(summary = "删除问题")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('governance:issue:remove')")
    @OperLog(title = "问题管理", businessType = 3)
    public Result<?> delete(@PathVariable Long id) {
        return issueService.delete(id);
    }

    @Operation(summary = "查询全部问题")
    @GetMapping
    public Result<List<Issue>> list() {
        return issueService.listAll();
    }

    @Operation(summary = "根据ID查询问题")
    @GetMapping("/{id}")
    public Result<Issue> getById(@PathVariable Long id) {
        return issueService.getById(id);
    }

    @Operation(summary = "根据项目ID查询问题列表")
    @GetMapping("/project/{projectId}")
    public Result<List<Issue>> listByProject(@PathVariable Long projectId) {
        return issueService.listByProject(projectId);
    }

    @Operation(summary = "分配问题处理人")
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('governance:issue:process')")
    @OperLog(title = "问题管理", businessType = 2)
    public Result<Issue> assign(@PathVariable Long id,
                                @RequestParam Long assigneeId,
                                @RequestParam(required = false) String assigneeName) {
        return issueService.assign(id, assigneeId, assigneeName);
    }

    @Operation(summary = "解决问题")
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('governance:issue:process')")
    @OperLog(title = "问题管理", businessType = 2)
    public Result<Issue> resolve(@PathVariable Long id,
                                 @RequestParam(required = false) String resolution) {
        return issueService.resolve(id, resolution);
    }

    @Operation(summary = "关闭问题")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('governance:issue:process')")
    @OperLog(title = "问题管理", businessType = 2)
    public Result<Issue> close(@PathVariable Long id) {
        return issueService.close(id);
    }

    @Operation(summary = "升级问题为变更请求")
    @PostMapping("/{id}/escalate")
    @PreAuthorize("hasAuthority('governance:issue:process')")
    @OperLog(title = "问题管理", businessType = 2)
    public Result<?> escalate(@PathVariable Long id) {
        return issueService.escalate(id);
    }
}
