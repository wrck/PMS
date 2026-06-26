package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.SysBasicData;

import java.util.List;

public interface BasicDataService {
    List<SysBasicData> queryByType(String dataType);
    void addBasicData(SysBasicData data);
    void updateBasicData(SysBasicData data);
    void deleteBasicData(Long id);
    String querySysArg(String code);
}
