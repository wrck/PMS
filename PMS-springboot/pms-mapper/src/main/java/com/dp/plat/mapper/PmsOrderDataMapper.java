package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsOrderData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * SAP订单数据Mapper
 */
@Mapper
public interface PmsOrderDataMapper extends BaseMapper<PmsOrderData> {

    /**
     * 根据项目ID查询订单数据
     */
    @Select("SELECT * FROM pm_order_data_from_sap WHERE project_id = #{projectId}")
    List<PmsOrderData> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据合同号查询RMA订单数据
     */
    @Select("SELECT * FROM pm_order_data_from_sap WHERE rma_contract_no = #{contractNo}")
    List<PmsOrderData> selectRmaByContractNo(@Param("contractNo") String contractNo);
}
