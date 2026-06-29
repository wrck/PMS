package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProb;
import com.dp.plat.service.ProbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prob")
public class ProbController {
    @Autowired
    private ProbService probService;

    @GetMapping("/list")
    public R<IPage<PmsProb>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String probTitle,
                                   @RequestParam(required = false) Integer probState,
                                   @RequestParam(required = false) Integer probType) {
        return R.ok(probService.queryPage(pageNum, pageSize, probTitle, probState, probType));
    }

    @GetMapping("/{id}")
    public R<PmsProb> detail(@PathVariable Long id) {
        return R.ok(probService.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProb prob) {
        probService.create(prob);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProb prob) {
        probService.update(prob);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        probService.delete(id);
        return R.ok();
    }
}
