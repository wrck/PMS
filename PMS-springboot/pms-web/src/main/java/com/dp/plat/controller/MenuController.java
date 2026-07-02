package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.UserMenu;
import com.dp.plat.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public R<IPage<UserMenu>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(menuService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<UserMenu> detail(@PathVariable Long id) {
        return R.ok(menuService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody UserMenu entity) {
        menuService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody UserMenu entity) {
        menuService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<UserMenu>> listAll() {
        return R.ok(menuService.listAll());
    }
}
