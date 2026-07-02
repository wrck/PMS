package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.IndustryAsset;
import com.dp.plat.service.IndustryAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业资产 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/industry/asset")
public class IndustryAssetController {

    @Autowired
    private IndustryAssetService industryAssetService;

    @GetMapping("/list")
    public R<IPage<IndustryAsset>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(industryAssetService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<IndustryAsset> detail(@PathVariable Long id) {
        return R.ok(industryAssetService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody IndustryAsset entity) {
        industryAssetService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody IndustryAsset entity) {
        industryAssetService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        industryAssetService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<IndustryAsset>> listAll() {
        return R.ok(industryAssetService.listAll());
    }
}
