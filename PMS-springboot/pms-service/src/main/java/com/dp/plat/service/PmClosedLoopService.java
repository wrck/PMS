package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmClosedLoop;

public interface PmClosedLoopService {

    IPage<PmClosedLoop> queryClosedLoopPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState);

    PmClosedLoop getDetail(Long id);

    void apply(PmClosedLoop closedLoop);

    void approve(Long id, String comment, boolean approved, String role);
}
