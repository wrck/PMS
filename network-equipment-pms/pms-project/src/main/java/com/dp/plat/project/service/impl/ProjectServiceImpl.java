package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Implementation of {@link IProjectService}.
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    /** Default status for a newly created project. */
    private static final String STATUS_PENDING = "PENDING";
    /** Status after the project is approved. */
    private static final String STATUS_APPROVED = "APPROVED";
    /** Default priority. */
    private static final String PRIORITY_NORMAL = "NORMAL";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createProject(Project project) {
        if (project == null) {
            throw new BusinessException("项目信息不能为空");
        }
        if (!StringUtils.hasText(project.getProjectName())) {
            throw new BusinessException("项目名称不能为空");
        }
        // New project starts in PENDING status.
        project.setStatus(STATUS_PENDING);
        if (!StringUtils.hasText(project.getPriority())) {
            project.setPriority(PRIORITY_NORMAL);
        }
        if (project.getProgress() == null) {
            project.setProgress(0);
        }
        // Generate project code on creation.
        project.setProjectCode(generateProjectCode());
        project.setId(null);
        this.save(project);
        return Result.ok(project);
    }

    @Override
    public Result<Project> getProjectById(Long id) {
        Project project = this.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return Result.ok(project);
    }

    @Override
    public Result<Page<Project>> listProjects(int page, int size, String projectName, String status) {
        Page<Project> pageObj = new Page<>(page <= 0 ? 1 : page, size <= 0 ? 10 : size);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .like(StringUtils.hasText(projectName), Project::getProjectName, projectName)
                .eq(StringUtils.hasText(status), Project::getStatus, status)
                .orderByDesc(Project::getCreateTime);
        Page<Project> result = this.page(pageObj, wrapper);
        return Result.ok(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProject(Project project) {
        if (project == null || project.getId() == null) {
            throw new BusinessException("项目信息或ID不能为空");
        }
        Project existing = this.getById(project.getId());
        if (existing == null) {
            throw new BusinessException("项目不存在");
        }
        this.updateById(project);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteProject(Long id) {
        Project existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("项目不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result approveProject(Long projectId) {
        Project project = this.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (!STATUS_PENDING.equals(project.getStatus())) {
            throw new BusinessException("当前项目状态不允许审批");
        }
        project.setStatus(STATUS_APPROVED);
        // Ensure the project code exists, generate one if missing.
        if (!StringUtils.hasText(project.getProjectCode())) {
            project.setProjectCode(generateProjectCode());
        }
        // TODO: integrate workflow module to start the project approval workflow once pms-workflow is ready.
        this.updateById(project);
        return Result.ok(project);
    }

    @Override
    public Result<Page<Project>> dashboard(String status) {
        // Return projects filtered by status for dashboard display.
        Page<Project> pageObj = new Page<>(1, 1000);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(StringUtils.hasText(status), Project::getStatus, status)
                .orderByDesc(Project::getCreateTime);
        Page<Project> result = this.page(pageObj, wrapper);
        return Result.ok(result);
    }

    @Override
    public String generateProjectCode() {
        int year = LocalDate.now().getYear();
        String prefix = "PMS-" + year + "-";
        // Count existing projects whose code starts with the year prefix, then add 1.
        long count = this.count(new LambdaQueryWrapper<Project>()
                .likeRight(Project::getProjectCode, prefix));
        long sequence = count + 1;
        return prefix + String.format("%04d", sequence);
    }
}
