package com.dp.plat.governance.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.issue.entity.Issue;

import java.util.List;

/**
 * Service for {@link Issue}.
 *
 * <p>Implements the issue log lifecycle: create → assign → resolve → close.
 * Supports three-book linkage: an issue requiring formal change can be
 * escalated to a change request ({@link #escalate}).</p>
 */
public interface IIssueService extends IService<Issue> {

    /**
     * Create an issue.
     *
     * @param issue issue to create
     * @return operation result containing the created issue
     */
    Result<Issue> create(Issue issue);

    /**
     * Update an issue.
     *
     * @param issue issue to update
     * @return operation result
     */
    Result<?> update(Issue issue);

    /**
     * Delete an issue by id.
     *
     * @param id issue id
     * @return operation result
     */
    Result<?> delete(Long id);

    /**
     * List all issues.
     *
     * @return operation result containing the list
     */
    Result<List<Issue>> listAll();

    /**
     * Get an issue by id.
     *
     * @param id issue id
     * @return operation result containing the issue
     */
    Result<Issue> getById(Long id);

    /**
     * List issues by project id.
     *
     * @param projectId project id
     * @return operation result containing the list
     */
    Result<List<Issue>> listByProject(Long projectId);

    /**
     * Assign an issue to a user.
     *
     * @param id           issue id
     * @param assigneeId   assignee user id
     * @param assigneeName assignee user name
     * @return operation result containing the updated issue
     */
    Result<Issue> assign(Long id, Long assigneeId, String assigneeName);

    /**
     * Resolve an issue with a resolution description.
     *
     * @param id         issue id
     * @param resolution resolution description
     * @return operation result containing the updated issue
     */
    Result<Issue> resolve(Long id, String resolution);

    /**
     * Close an issue.
     *
     * @param id issue id
     * @return operation result containing the updated issue
     */
    Result<Issue> close(Long id);

    /**
     * Escalate an issue: create a change request.
     *
     * @param id issue id
     * @return operation result containing the created change request
     */
    Result<?> escalate(Long id);

    /**
     * Generate the issue number in format ISSUE-YYYY-XXXX.
     *
     * @return generated issue number
     */
    String generateIssueNo();
}
