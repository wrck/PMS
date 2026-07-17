package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.ProjectTreeNode;
import com.dp.plat.project.entity.Project;

import java.util.List;
import java.util.Map;

/**
 * Service for {@link Project}.
 */
public interface IProjectService extends IService<Project> {

    /**
     * Create a project with status PENDING and trigger the approval workflow.
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
     * Approve a project, setting status to APPROVED, generating the project code
     * and completing the corresponding approval workflow task.
     *
     * @param projectId project id
     * @return operation result
     */
    Result approveProject(Long projectId);

    /**
     * Get dashboard data: projects grouped by status.
     *
     * @param status status filter (null for all)
     * @return operation result containing the status-to-projects map
     */
    Result<Map<String, List<Project>>> dashboard(String status);

    /**
     * Generate the project code in format PMS-YYYY-XXXX.
     *
     * @return generated project code
     */
    String generateProjectCode();

    // ============ Phase 3：主子项目与生命周期（Story 2） ============

    /**
     * 递归查询主子项目树。
     *
     * <p>关联设计文档：§5.3 GET /api/project/{id}/tree。
     *
     * @param id 根项目 ID
     * @return 树形结构（含递归子节点）
     */
    Result<ProjectTreeNode> getProjectTree(Long id);

    /**
     * 创建子项目（设置 parentProjectId、projectPath、depth）。
     *
     * <p>关联设计文档：§5.3 POST /api/project/{id}/subproject。
     *
     * @param parentId    父项目 ID
     * @param subproject  子项目信息
     * @return 创建结果
     */
    Result<Project> createSubproject(Long parentId, Project subproject);

    /**
     * 关闭主项目（含子项目校验）。
     *
     * <p>关联设计文档：§5.3 POST /api/project/{id}/close、§3.2 Story 2 验收 2。
     * 完整子项目校验逻辑在 Phase 3 Task 3 实现（抛出 SubprojectNotClosedException）。
     *
     * @param id 项目 ID
     * @return 关闭结果
     */
    Result<Project> closeProject(Long id);

    /**
     * 取消项目。
     *
     * <p>关联设计文档：§5.3 POST /api/project/{id}/cancel。
     *
     * @param id 项目 ID
     * @return 取消结果
     */
    Result<Project> cancelProject(Long id);

    /**
     * 项目进度汇总（含子项目加权平均进度）。
     *
     * <p>关联设计文档：§5.3 GET /api/project/{id}/progress、§2.5 递归 CTE。
     * 完整 CTE 汇总在 Phase 3 Task 4 实现。
     *
     * @param id 项目 ID
     * @return 进度汇总结果
     */
    Result<Map<String, Object>> getProjectProgress(Long id);
}
