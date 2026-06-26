package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmClosedLoop;
import com.dp.plat.service.PmClosedLoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/closed-loop")
public class PmClosedLoopController {

    @Autowired
    private PmClosedLoopService closedLoopService;

    @GetMapping("/list")
    public R<IPage<PmClosedLoop>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) Long projectId,
                                        @RequestParam(required = false) Integer applyState) {
        return R.ok(closedLoopService.queryClosedLoopPage(pageNum, pageSize, projectId, applyState));
    }

    @GetMapping("/{id}")
    public R<PmClosedLoop> detail(@PathVariable Long id) {
        return R.ok(closedLoopService.getDetail(id));
    }

    @PostMapping
    public R<Void> apply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.apply(closedLoop);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id, @RequestParam String comment,
                            @RequestParam boolean approved, @RequestParam(defaultValue = "sm") String role) {
        closedLoopService.approve(id, comment, approved, role);
        return R.ok();
    }
}
