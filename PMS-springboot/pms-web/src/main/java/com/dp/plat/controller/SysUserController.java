package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.UserDTO;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.UserVO;
import com.dp.plat.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public R<IPage<UserVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) String username,
                                 @RequestParam(required = false) String realname,
                                 @RequestParam(required = false) Long deptId) {
        IPage<UserVO> page = sysUserService.queryUserPage(pageNum, pageSize, username, realname, deptId);
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<UserVO> detail(@PathVariable Long id) {
        return R.ok(sysUserService.getUserById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody UserDTO dto) {
        sysUserService.addUser(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody UserDTO dto) {
        sysUserService.updateUser(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return R.ok();
    }

    @PostMapping("/reset-password")
    public R<Void> resetPassword(@RequestParam Long userId) {
        sysUserService.resetPassword(userId);
        return R.ok();
    }

    @PostMapping("/{id}/change-password")
    public R<Void> changePassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        sysUserService.changePassword(id, params.get("oldPassword"), params.get("newPassword"));
        return R.ok();
    }
}
