package com.dp.plat.project.dto;

import com.dp.plat.project.entity.Project;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 未关闭子项目（用于关闭主项目被拒绝时的响应明细）。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 2 uncompletedSubProjects 数组项。
 */
@Data
public class UncompletedSubProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String projectName;
    private String status;

    public UncompletedSubProject() {
    }

    public UncompletedSubProject(Project p) {
        this.id = p.getId();
        this.projectName = p.getProjectName();
        this.status = p.getStatus();
    }
}
