package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProjectDeliver;
import com.dp.plat.service.ProjectDeliverService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project/deliver")
public class ProjectDeliverController {

    @Autowired
    private ProjectDeliverService deliverService;

    @GetMapping("/list")
    public R<List<PmsProjectDeliver>> list(@RequestParam Long projectId) {
        return R.ok(deliverService.queryDeliversByProject(projectId));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProjectDeliver deliver, HttpServletRequest request) {
        deliver.setCreateBy((String) request.getAttribute("currentUsername"));
        deliverService.addDeliver(deliver);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProjectDeliver deliver) {
        deliverService.updateDeliver(deliver);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        deliverService.deleteDeliver(id);
        return R.ok();
    }
}
