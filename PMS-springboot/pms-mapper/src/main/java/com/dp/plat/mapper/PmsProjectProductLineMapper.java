package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectProductLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 项目产品线Mapper
 */
@Mapper
public interface PmsProjectProductLineMapper extends BaseMapper<PmsProjectProductLine> {

    /**
     * 根据合同号从SAP订单行复制产品线到项目
     */
    @Select("INSERT INTO pm_project_product_line (project_id, contract_no, item_code, item_name, order_quantity, deliver_quantity, open_quantity) " +
            "SELECT #{projectId}, #{contractNo}, a.item_code, a.item_desc, a.order_quantity, " +
            "(a.order_quantity - a.open_quantity), a.open_quantity " +
            "FROM pm_order_line_from_sap a " +
            "LEFT JOIN pm_order_data_from_sap b ON a.order_number = b.order_number AND a.comp_code = b.comp_code " +
            "WHERE b.contract_no = #{contractNo}")
    int insertFromSapOrder(@Param("projectId") Long projectId, @Param("contractNo") String contractNo);

    /**
     * 更新产品数量
     */
    @Update("UPDATE pm_project_product_line SET project_quantity = #{projectQuantity} WHERE id = #{id}")
    int updateProjectQuantity(@Param("id") Long id, @Param("projectQuantity") Integer projectQuantity);
}
