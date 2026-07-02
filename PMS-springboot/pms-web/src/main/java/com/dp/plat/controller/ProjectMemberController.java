package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProjectMember;
import com.dp.plat.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目成员 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/project/member")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService projectMemberService;

    @GetMapping("/list")
    public R<IPage<PmsProjectMember>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(projectMemberService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PmsProjectMember> detail(@PathVariable Long id) {
        return R.ok(projectMemberService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProjectMember entity) {
        projectMemberService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProjectMember entity) {
        projectMemberService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        projectMemberService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PmsProjectMember>> listAll() {
        return R.ok(projectMemberService.listAll());
    }
}
