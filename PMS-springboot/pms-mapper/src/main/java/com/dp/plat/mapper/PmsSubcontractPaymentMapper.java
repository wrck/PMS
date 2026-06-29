package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsSubcontractPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsSubcontractPaymentMapper extends BaseMapper<PmsSubcontractPayment> {

    @Select("SELECT * FROM pm_subcontract_payment WHERE subcontract_id = #{subcontractId}")
    List<PmsSubcontractPayment> selectBySubcontractId(@Param("subcontractId") Long subcontractId);
}
