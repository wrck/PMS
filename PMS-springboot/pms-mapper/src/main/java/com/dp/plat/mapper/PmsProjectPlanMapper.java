package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工程计划Mapper
 */
@Mapper
public interface PmsProjectPlanMapper extends BaseMapper<PmsProjectPlan> {

    /**
     * 根据合同号查询工程计划
     */
    @Select("SELECT * FROM pm_project_plan WHERE contract_no = #{contractNo}")
    List<PmsProjectPlan> selectByContractNo(@Param("contractNo") String contractNo);
}
