package com.dp.plat.service;

import com.dp.plat.model.dto.DataQueryParam;
import com.dp.plat.model.vo.CbDataVO;

import java.util.List;
import java.util.Map;

/**
 * 数据分析服务接口 - 迁移自老系统 DataAnalysisService
 */
public interface DataAnalysisService {

    /**
     * 查询回访数据列表
     * 迁移自: DataAnalysisServiceImpl.quesyCbDataList()
     */
    List<CbDataVO> queryCbDataList(DataQueryParam queryParam);

    /**
     * 分析概览数据
     * 迁移自: DataAnalysisAction.overview()
     */
    Map<String, Object> overview(DataQueryParam queryParam);

    /**
     * 项目状态分布分析
     * 迁移自: DataAnalysisAction.projectStatus()
     */
    List<Map<String, Object>> projectStatus(DataQueryParam queryParam);

    /**
     * 办事处维度分析
     * 迁移自: DataAnalysisAction.byOffice()
     */
    List<Map<String, Object>> byOffice(DataQueryParam queryParam);

    /**
     * 时间维度分析
     * 迁移自: DataAnalysisAction.byTime()
     */
    List<Map<String, Object>> byTime(DataQueryParam queryParam);

    /**
     * 自定义查询
     * 迁移自: DataAnalysisAction.customQuery()
     */
    List<Map<String, Object>> customQuery(Map<String, Object> queryParams);
}
