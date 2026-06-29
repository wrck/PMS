package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsSubcontractFacilitator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsSubcontractFacilitatorMapper extends BaseMapper<PmsSubcontractFacilitator> {

    @Select("SELECT * FROM pm_subcontract_facilitator WHERE state = 1 ORDER BY name")
    List<PmsSubcontractFacilitator> selectAllActive();
}
