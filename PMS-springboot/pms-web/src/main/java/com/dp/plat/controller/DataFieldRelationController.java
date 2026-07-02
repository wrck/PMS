package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.DataFieldRelation;
import com.dp.plat.service.DataFieldRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字段关系 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/data-field")
public class DataFieldRelationController {

    @Autowired
    private DataFieldRelationService dataFieldRelationService;

    @GetMapping("/list")
    public R<IPage<DataFieldRelation>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dataFieldRelationService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<DataFieldRelation> detail(@PathVariable Long id) {
        return R.ok(dataFieldRelationService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody DataFieldRelation entity) {
        dataFieldRelationService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody DataFieldRelation entity) {
        dataFieldRelationService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dataFieldRelationService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<DataFieldRelation>> listAll() {
        return R.ok(dataFieldRelationService.listAll());
    }
}
