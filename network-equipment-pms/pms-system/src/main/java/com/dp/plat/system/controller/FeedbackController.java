package com.dp.plat.system.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.entity.Feedback;
import com.dp.plat.system.service.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Feedback / support ticket controller.
 *
 * <p>Any authenticated user can submit feedback (POST /); administrators with
 * {@code system:feedback:list} / {@code system:feedback:reply} permissions can
 * list all tickets and reply/close them. GET /{id} is accessible by the
 * ticket creator or administrators.</p>
 */
@Tag(name = "技术支持反馈", description = "User feedback / support ticket APIs")
@RestController
@RequestMapping("/api/system/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final IFeedbackService feedbackService;

    @Operation(summary = "Submit a new feedback ticket (any authenticated user)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(title = "技术支持反馈", businessType = 1)
    @RateLimit(capacity = 5, refillTokens = 5, refillPeriodSeconds = 60,
            message = "反馈提交过于频繁，请稍后再试")
    public Result<Boolean> create(@Valid @RequestBody Feedback feedback) {
        // 防止用户伪造提交人字段：清空由服务端覆盖
        feedback.setUsername(null);
        feedback.setUserId(null);
        if (!StringUtils.hasText(feedback.getStatus())) {
            feedback.setStatus("PENDING");
        }
        // 回复字段不允许在创建时填写
        feedback.setReply(null);
        feedback.setReplyBy(null);
        feedback.setReplyAt(null);
        return Result.ok(feedbackService.save(feedback));
    }

    @Operation(summary = "Get feedback by id (creator or admin)")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Feedback> get(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        String currentUser = SecurityUtils.getCurrentUsername();
        if (!currentUser.equals(feedback.getUsername()) && !isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return Result.ok(feedback);
    }

    @Operation(summary = "List feedback submitted by the current user")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<List<Feedback>> my() {
        String username = SecurityUtils.getCurrentUsername();
        return Result.ok(feedbackService.listByUser(username));
    }

    @Operation(summary = "List all feedback (admin)")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:feedback:list')")
    public Result<List<Feedback>> list(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) String category) {
        return Result.ok(feedbackService.listAll(status, category));
    }

    @Operation(summary = "Reply to a feedback ticket (admin)")
    @PutMapping("/{id}/reply")
    @PreAuthorize("@ss.hasPermission('system:feedback:reply')")
    @OperLog(title = "技术支持反馈", businessType = 2)
    @RateLimit(capacity = 30, refillTokens = 30, refillPeriodSeconds = 60)
    public Result<Boolean> reply(@PathVariable Long id,
                                 @Valid @RequestBody FeedbackReplyRequest request) {
        return Result.ok(feedbackService.reply(id, request.getReply(),
                SecurityUtils.getCurrentUsername()));
    }

    @Operation(summary = "Close a feedback ticket (admin)")
    @PutMapping("/{id}/close")
    @PreAuthorize("@ss.hasPermission('system:feedback:reply')")
    @OperLog(title = "技术支持反馈", businessType = 2)
    @RateLimit(capacity = 30, refillTokens = 30, refillPeriodSeconds = 60)
    public Result<Boolean> close(@PathVariable Long id) {
        return Result.ok(feedbackService.close(id));
    }

    @Operation(summary = "Count feedback grouped by status (current user)")
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public Result<Map<String, Long>> stats() {
        return Result.ok(feedbackService.countByStatus());
    }

    /**
     * Whether the current user has the admin role.
     */
    private boolean isAdmin() {
        Authentication auth = SecurityUtils.getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (CommonConstants.SUPER_ADMIN_ROLE.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Request body for the reply endpoint.
     */
    @Data
    public static class FeedbackReplyRequest {
        @NotBlank(message = "回复内容不能为空")
        @Size(max = 4000, message = "回复内容长度不能超过 4000 个字符")
        private String reply;
    }
}
