package com.dp.plat.job;

import com.dp.plat.service.PmsProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 自动关闭不跟踪项目 - 迁移自 CloseNotTrackProject, 每天03:25和13:25 */
@Component
public class CloseNotTrackProjectJob {
    @Autowired private PmsProjectService pmsProjectService;
    @Scheduled(cron = "0 25 3,13 * * ?")
    public void execute() { pmsProjectService.closeNotTrackProjects(); }
}
