package com.dp.plat.project.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectMember;
import com.dp.plat.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project/member")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final IProjectMemberService memberService;

    @GetMapping("/project/{projectId}")
    public Result<List<ProjectMember>> listByProjectId(@PathVariable Long projectId) {
        return Result.ok(memberService.listByProjectId(projectId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('project:subproject:manage')")
    public Result<ProjectMember> create(@RequestBody ProjectMember member) {
        return Result.ok(memberService.create(member));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('project:subproject:manage')")
    public Result<ProjectMember> update(@RequestBody ProjectMember member) {
        return Result.ok(memberService.update(member));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:subproject:manage')")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Result.ok();
    }
}
