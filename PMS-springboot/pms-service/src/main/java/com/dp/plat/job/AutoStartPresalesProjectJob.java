package com.dp.plat.job;

import com.dp.plat.service.PmsPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 自动开始售前项目 - 迁移自 AutoStartPresalesProjectJob, 每天08:30和13:30 */
@Component
public class AutoStartPresalesProjectJob {
    @Autowired private PmsPresalesService presalesService;
    @Scheduled(cron = "0 30 8,13 * * ?")
    public void execute() { presalesService.autoStartPresalesProjects(); }
}
