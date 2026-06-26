package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsPresales;

public interface PmsPresalesService {
    IPage<PmsPresales> queryPresalesPage(Integer pageNum, Integer pageSize,
                                          String presalesCode, String projectName,
                                          Integer applyState, String officeCode);
    PmsPresales getPresalesDetail(Long id);
    void createPresales(PmsPresales presales);
    void updatePresales(PmsPresales presales);
    void deletePresales(Long id);
    void startFlow(Long id);
    void approve(Long id, String comment, boolean approved);
}
