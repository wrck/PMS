package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsMaintenance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsMaintenanceMapper extends BaseMapper<PmsMaintenance> {

    /** 查询服务交付记录 */
    List<Map<String, Object>> selectServiceDelivery(@Param("maintenanceId") Long maintenanceId);
}
