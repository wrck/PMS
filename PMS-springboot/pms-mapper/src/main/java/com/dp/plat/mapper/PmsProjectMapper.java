package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsProjectMapper extends BaseMapper<PmsProject> {

    /** 查询租赁配置清单 (from CRM) */
    @Select("SELECT c.* FROM pm_project_product_lease_line_from_crm c " +
            "INNER JOIN (SELECT projectCode, MAX(orderExecNumber) AS max_order " +
            "FROM pm_project_product_lease_line_from_crm WHERE projectCode = #{projectCode} GROUP BY projectCode) m " +
            "ON c.projectCode = m.projectCode AND c.orderExecNumber = m.max_order " +
            "WHERE c.projectCode = #{projectCode}")
    List<Map<String, Object>> selectLeaseLineByProjectCode(@Param("projectCode") String projectCode);

    /** 查询配置关系清单 (from CRM) */
    @Select("SELECT c.*, SUBSTRING_INDEX(bomPaths, ',', level -1) AS parentBomPaths, " +
            "CONCAT(c.itemGroup, '_', SUBSTRING_INDEX(bomPaths, ',', level -1)) AS parentGroupPaths, " +
            "CONCAT(c.itemGroup, '_', bomPaths) AS groupPaths " +
            "FROM pm_project_product_config_level_info_from_crm c " +
            "INNER JOIN (SELECT projectCode, MAX(orderExecNumber) AS max_order " +
            "FROM pm_project_product_config_level_info_from_crm WHERE projectCode = #{projectCode} GROUP BY projectCode) m " +
            "ON c.projectCode = m.projectCode AND c.orderExecNumber = m.max_order " +
            "WHERE c.projectCode = #{projectCode} ORDER BY c.projectCode, itemGroup, bomPaths")
    List<Map<String, Object>> selectConfigLevelInfoByProjectCode(@Param("projectCode") String projectCode);

    /** 查询项目计划状态 */
    @Select("SELECT projectPlanState FROM pm_project_state WHERE projectId = #{projectId} LIMIT 1")
    String selectPlanState(@Param("projectId") Long projectId);

    /** 查询当前工程计划阶段 */
    @Select("SELECT taskTypeCode FROM pm_project_task WHERE projectId = #{projectId} " +
            "AND effectiveFrom <= NOW() AND (effectiveTo IS NULL OR effectiveTo > NOW()) " +
            "ORDER BY eventPlanHappenDate ASC LIMIT 1")
    String selectCurrentPlan(@Param("projectId") Long projectId);

    /** 更新项目最后刷新时间 */
    @Update("UPDATE pm_project SET projectRefreshTime = NOW() WHERE projectId = #{projectId}")
    int updateRefreshTime(@Param("projectId") Long projectId);

    /** 查询合同号对应的项目数量 */
    @Select("SELECT COUNT(*) FROM pm_project_contract WHERE contractNo = #{contractNo}")
    int countByContractNo(@Param("contractNo") String contractNo);

    /** 查询项目组编码数量 */
    @Select("SELECT COUNT(*) FROM pm_project_group_relationship WHERE projectCode LIKE CONCAT(#{codePrefix}, '%')")
    int countByProjectCodePrefix(@Param("codePrefix") String codePrefix);
}
