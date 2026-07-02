package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.CommonRelatedData;
import com.dp.plat.service.CommonRelatedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共关联数据 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/common/related-data")
public class CommonRelatedDataController {

    @Autowired
    private CommonRelatedDataService commonRelatedDataService;

    @GetMapping("/list")
    public R<IPage<CommonRelatedData>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(commonRelatedDataService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<CommonRelatedData> detail(@PathVariable Long id) {
        return R.ok(commonRelatedDataService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody CommonRelatedData entity) {
        commonRelatedDataService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody CommonRelatedData entity) {
        commonRelatedDataService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        commonRelatedDataService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<CommonRelatedData>> listAll() {
        return R.ok(commonRelatedDataService.listAll());
    }
}
