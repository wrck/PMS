package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.DeptDTO;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/list")
    public R<List<SysDepartment>> list() {
        List<SysDepartment> list = sysDeptService.queryDeptTree();
        return R.ok(list);
    }

    @PostMapping
    public R<Void> add(@RequestBody DeptDTO dto) {
        sysDeptService.addDept(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody DeptDTO dto) {
        sysDeptService.updateDept(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysDeptService.deleteDept(id);
        return R.ok();
    }
}
