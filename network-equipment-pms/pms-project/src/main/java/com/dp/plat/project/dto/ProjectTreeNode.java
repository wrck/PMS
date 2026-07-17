package com.dp.plat.project.dto;

import com.dp.plat.project.entity.Project;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 主子项目树节点 DTO。
 *
 * <p>关联设计文档：§5.3 GET /api/project/{id}/tree — 递归展示主子项目层级。
 * 仅暴露树展示所需字段，避免直接序列化整个 Project 实体（含敏感字段）。
 */
@Data
public class ProjectTreeNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String projectCode;
    private String projectName;
    private String status;
    private Long parentProjectId;
    private String projectPath;
    private Integer depth;
    private Integer progress;
    private Long currentPhaseId;
    private List<ProjectTreeNode> children = new ArrayList<>();

    public ProjectTreeNode() {
    }

    public ProjectTreeNode(Project project) {
        this.id = project.getId();
        this.projectCode = project.getProjectCode();
        this.projectName = project.getProjectName();
        this.status = project.getStatus();
        this.parentProjectId = project.getParentProjectId();
        this.projectPath = project.getProjectPath();
        this.depth = project.getDepth();
        this.progress = project.getProgress();
        this.currentPhaseId = project.getCurrentPhaseId();
    }
}
