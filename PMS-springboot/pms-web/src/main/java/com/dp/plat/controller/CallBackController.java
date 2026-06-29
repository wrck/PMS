package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsCallBack;
import com.dp.plat.service.CallBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/callback")
public class CallBackController {
    @Autowired
    private CallBackService callBackService;

    @GetMapping("/list")
    public R<IPage<PmsCallBack>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Long projectId,
                                       @RequestParam(required = false) Integer applyState) {
        return R.ok(callBackService.queryCallBackPage(pageNum, pageSize, projectId, applyState));
    }

    @GetMapping("/{id}")
    public R<PmsCallBack> detail(@PathVariable Long id) {
        return R.ok(callBackService.getCallBackDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsCallBack callBack) {
        callBackService.createCallBack(callBack);
        return R.ok();
    }

    @PostMapping("/{id}/start-flow")
    public R<Void> startFlow(@PathVariable Long id) {
        callBackService.startFlow(id);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id, @RequestParam String comment, @RequestParam boolean approved) {
        callBackService.approve(id, comment, approved);
        return R.ok();
    }
}
