package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsProb;

public interface ProbService {
    IPage<PmsProb> queryPage(Integer pageNum, Integer pageSize, String probTitle, Integer probState, Integer probType);
    PmsProb getDetail(Long id);
    void create(PmsProb prob);
    void update(PmsProb prob);
    void delete(Long id);
}
