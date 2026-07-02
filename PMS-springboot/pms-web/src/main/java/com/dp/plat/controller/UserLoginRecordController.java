package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.UserLogin;
import com.dp.plat.service.UserLoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录记录 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/login-record")
public class UserLoginRecordController {

    @Autowired
    private UserLoginRecordService userLoginRecordService;

    @GetMapping("/list")
    public R<IPage<UserLogin>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(userLoginRecordService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<UserLogin> detail(@PathVariable Long id) {
        return R.ok(userLoginRecordService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody UserLogin entity) {
        userLoginRecordService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody UserLogin entity) {
        userLoginRecordService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userLoginRecordService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<UserLogin>> listAll() {
        return R.ok(userLoginRecordService.listAll());
    }
}
