package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.service.IProjectTemplateService;
import com.dp.plat.project.service.ProjectConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project/config")
@RequiredArgsConstructor
public class ProjectConfigController {

    private final ProjectConfigService configService;
    private final IProjectTemplateService templateService;

    @GetMapping("/{projectId}")
    public Result<Map<String, String>> getAllForProject(@PathVariable Long projectId) {
        Project project = templateService.getById(null) == null ? null : null; // 简化：实际从 ProjectService 获取
        // 实际实现需要查询 Project 获取 templateId
        Long templateId = null; // TODO: 通过 ProjectService 获取 project.getTemplateId()
        return Result.ok(configService.getAllForProject(projectId, templateId));
    }

    @PutMapping("/{projectId}")
    @RequiresPermissions("workflow:approval:config")
    public Result<Void> update(@PathVariable Long projectId, @RequestBody Map<String, String> configs) {
        // 批量更新项目级配置
        // 实现略：删除旧项目级配置 + 插入新配置
        return Result.ok();
    }
}
