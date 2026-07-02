package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.Holiday;
import com.dp.plat.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 假期 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/list")
    public R<IPage<Holiday>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(holidayService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Holiday> detail(@PathVariable Long id) {
        return R.ok(holidayService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Holiday entity) {
        holidayService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Holiday entity) {
        holidayService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        holidayService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<Holiday>> listAll() {
        return R.ok(holidayService.listAll());
    }
}
