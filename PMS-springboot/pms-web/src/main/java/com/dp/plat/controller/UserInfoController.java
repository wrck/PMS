package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/user-info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/list")
    public R<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(userInfoService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        return R.ok(userInfoService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysUser entity) {
        userInfoService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysUser entity) {
        userInfoService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userInfoService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysUser>> listAll() {
        return R.ok(userInfoService.listAll());
    }
}
