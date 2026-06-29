package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.ProjectDTO;
import com.dp.plat.model.dto.ProjectMemberDTO;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.model.entity.PmsProjectMember;
import com.dp.plat.model.vo.ProjectVO;
import com.dp.plat.service.PmsProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class PmsProjectController {

    @Autowired
    private PmsProjectService pmsProjectService;

    @GetMapping("/list")
    public R<IPage<ProjectVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String projectCode,
                                    @RequestParam(required = false) String projectName,
                                    @RequestParam(required = false) String contractNo,
                                    @RequestParam(required = false) String officeCode,
                                    @RequestParam(required = false) Integer projectState) {
        IPage<ProjectVO> page = pmsProjectService.queryProjectPage(
                pageNum, pageSize, projectCode, projectName, contractNo, officeCode, projectState);
        return R.ok(page);
    }

    @PostMapping
    public R<Void> add(@RequestBody ProjectDTO dto) {
        pmsProjectService.addProject(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody ProjectDTO dto) {
        pmsProjectService.updateProject(dto);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<ProjectVO> detail(@PathVariable Long id) {
        ProjectVO vo = pmsProjectService.getProjectDetail(id);
        return R.ok(vo);
    }

    @GetMapping("/{id}/members")
    public R<List<PmsProjectMember>> members(@PathVariable Long id) {
        List<PmsProjectMember> list = pmsProjectService.getProjectMembers(id);
        return R.ok(list);
    }

    @PostMapping("/member")
    public R<Void> addMember(@RequestBody ProjectMemberDTO dto) {
        pmsProjectService.addProjectMember(dto);
        return R.ok();
    }

    @DeleteMapping("/member/{id}")
    public R<Void> deleteMember(@PathVariable Long id) {
        pmsProjectService.deleteProjectMember(id);
        return R.ok();
    }
}
