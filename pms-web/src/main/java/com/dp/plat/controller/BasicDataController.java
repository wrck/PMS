package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.service.BasicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/basic-data")
public class BasicDataController {

    @Autowired
    private BasicDataService basicDataService;

    @GetMapping("/list")
    public R<List<SysBasicData>> list(@RequestParam String dataType) {
        return R.ok(basicDataService.queryByType(dataType));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysBasicData data) {
        basicDataService.addBasicData(data);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysBasicData data) {
        basicDataService.updateBasicData(data);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        basicDataService.deleteBasicData(id);
        return R.ok();
    }
}
