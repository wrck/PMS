package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/list")
    public R<IPage<SysBasicData>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dictionaryService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysBasicData> detail(@PathVariable Long id) {
        return R.ok(dictionaryService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysBasicData entity) {
        dictionaryService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysBasicData entity) {
        dictionaryService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dictionaryService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysBasicData>> listAll() {
        return R.ok(dictionaryService.listAll());
    }
}
