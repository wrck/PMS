package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsSubcontractLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsSubcontractLineMapper extends BaseMapper<PmsSubcontractLine> {

    @Select("SELECT * FROM pm_subcontract_line WHERE subcontract_id = #{subcontractId}")
    List<PmsSubcontractLine> selectBySubcontractId(@Param("subcontractId") Long subcontractId);
}
