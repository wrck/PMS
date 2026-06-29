package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目合同Mapper
 */
@Mapper
public interface PmsProjectContractMapper extends BaseMapper<PmsProjectContract> {

    /**
     * 根据项目编码查询合同列表
     */
    @Select("SELECT * FROM pm_project_contract WHERE project_code = #{projectCode}")
    List<PmsProjectContract> selectByProjectCode(@Param("projectCode") String projectCode);

    /**
     * 根据合同号查询合同数量
     */
    @Select("SELECT COUNT(*) FROM pm_project_contract WHERE contract_no = #{contractNo}")
    int countByContractNo(@Param("contractNo") String contractNo);
}
