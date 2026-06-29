package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsShipmentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 发货信息Mapper
 */
@Mapper
public interface PmsShipmentInfoMapper extends BaseMapper<PmsShipmentInfo> {

    /** 根据合同号和项目ID查询发货信息 */
    @Select("SELECT * FROM pm_shipment_info WHERE contract_no = #{contractNo} AND project_id = #{projectId}")
    List<PmsShipmentInfo> selectByContractNoAndProjectId(@Param("contractNo") String contractNo, @Param("projectId") Long projectId);

    /** 根据项目ID查询发货信息 */
    @Select("SELECT * FROM pm_shipment_info WHERE project_id = #{projectId}")
    List<PmsShipmentInfo> selectByProjectId(@Param("projectId") Long projectId);

    /** 删除项目的安装信息 */
    @Update("UPDATE pm_shipment_info SET install_address = NULL WHERE project_id = #{projectId}")
    int clearInstallAddressByProjectId(@Param("projectId") Long projectId);

    /** 根据条码查询发货信息 */
    @Select("SELECT * FROM pm_shipment_info WHERE bar_code = #{barCode} AND project_id = #{projectId} LIMIT 1")
    PmsShipmentInfo selectByBarcodeAndProjectId(@Param("barCode") String barCode, @Param("projectId") Long projectId);

    /** 查询项目的发货数量 */
    @Select("SELECT COUNT(*) FROM pm_shipment_info WHERE project_id = #{projectId}")
    int countByProjectId(@Param("projectId") Long projectId);

    /** 查询已安装地址的发货数量 */
    @Select("SELECT COUNT(*) FROM pm_shipment_info WHERE project_id = #{projectId} AND install_address IS NOT NULL AND install_address != ''")
    int countInstalledByProjectId(@Param("projectId") Long projectId);
}
