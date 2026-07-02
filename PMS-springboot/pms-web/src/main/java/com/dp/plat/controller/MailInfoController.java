package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.MailContent;
import com.dp.plat.service.MailInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮件 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/mail")
public class MailInfoController {

    @Autowired
    private MailInfoService mailInfoService;

    @GetMapping("/list")
    public R<IPage<MailContent>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(mailInfoService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<MailContent> detail(@PathVariable Long id) {
        return R.ok(mailInfoService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody MailContent entity) {
        mailInfoService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody MailContent entity) {
        mailInfoService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        mailInfoService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<MailContent>> listAll() {
        return R.ok(mailInfoService.listAll());
    }
}
