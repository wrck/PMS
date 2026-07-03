package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;

/**
 * Service for {@link Project}.
 */
public interface IProjectService extends IService<Project> {

    /**
     * Create a project with status PENDING and generate the project code.
     *
     * @param project project to create
     * @return operation result
     */
    Result createProject(Project project);

    /**
     * Get a project by id.
     *
     * @param id project id
     * @return operation result containing the project
     */
    Result<Project> getProjectById(Long id);

    /**
     * Paginated project query with optional filters.
     *
     * @param page        page number (1-based)
     * @param size        page size
     * @param projectName project name filter (fuzzy)
     * @param status      status filter
     * @return operation result containing the page
     */
    Result<Page<Project>> listProjects(int page, int size, String projectName, String status);

    /**
     * Update a project.
     *
     * @param project project to update
     * @return operation result
     */
    Result updateProject(Project project);

    /**
     * Delete a project by id.
     *
     * @param id project id
     * @return operation result
     */
    Result deleteProject(Long id);

    /**
     * Approve a project, setting status to APPROVED and ensuring the project code exists.
     *
     * @param projectId project id
     * @return operation result
     */
    Result approveProject(Long projectId);

    /**
     * Get dashboard data: projects grouped/filtered by status.
     *
     * @param status status filter (null for all)
     * @return operation result containing the page
     */
    Result<Page<Project>> dashboard(String status);

    /**
     * Generate the project code in format PMS-YYYY-XXXX.
     *
     * @return generated project code
     */
    String generateProjectCode();
}
