package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.RoleDTO;
import com.dp.plat.model.entity.SysRole;
import com.dp.plat.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public R<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String roleName) {
        IPage<SysRole> page = sysRoleService.queryRolePage(pageNum, pageSize, roleName);
        return R.ok(page);
    }

    @GetMapping("/all")
    public R<List<SysRole>> all() {
        return R.ok(sysRoleService.listAllRoles());
    }

    @PostMapping
    public R<Void> add(@RequestBody RoleDTO dto) {
        sysRoleService.addRole(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody RoleDTO dto) {
        sysRoleService.updateRole(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return R.ok();
    }
}
