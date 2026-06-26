package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/operate-log")
public class OperateLogController {

    @Autowired
    private OperateLogService logService;

    @GetMapping("/list")
    public R<IPage<SysOperateLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String module) {
        return R.ok(logService.queryLogPage(pageNum, pageSize, username, module));
    }
}
