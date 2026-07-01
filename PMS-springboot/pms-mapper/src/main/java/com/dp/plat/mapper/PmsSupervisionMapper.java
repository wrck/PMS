package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsSupervision;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsSupervisionMapper extends BaseMapper<PmsSupervision> {

    /** 查询有督查权限的用户 */
    List<Map<String, Object>> selectPowerUsers();
}
