package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.PmsCallBackMapper;
import com.dp.plat.mapper.PmsProjectMapper;
import com.dp.plat.mapper.PmsProjectMemberMapper;
import com.dp.plat.mapper.SysDepartmentMapper;
import com.dp.plat.model.dto.DataQueryParam;
import com.dp.plat.model.entity.PmsCallBack;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.model.vo.CbDataVO;
import com.dp.plat.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现 - 迁移自老系统 DataAnalysisServiceImpl
 *
 * 核心逻辑:
 * 1. 回访数据列表查询（关联项目、办事处、回访结果）
 * 2. 多维度数据分析（按办事处、时间、状态等）
 */
@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private PmsProjectMapper projectMapper;
    @Autowired
    private PmsCallBackMapper callBackMapper;
    @Autowired
    private SysDepartmentMapper deptMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<CbDataVO> queryCbDataList(DataQueryParam queryParam) {
        // 迁移自: DataAnalysisDao.quesyCbDataList()
        // 使用原生SQL查询回访数据（关联项目、办事处、回访结果）
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT cb.id, p.projectName, p.projectCode, ");
        sql.append("cb.applyBy AS pmUserName, cb.applyBy AS pmRealName, ");
        sql.append("cb.officeCode, d.departmentName AS officeName, ");
        sql.append("cb.applyTime AS cbTime, 1 AS times, ");
        sql.append("0.0 AS projectScore, 0.0 AS otherScore, 0.0 AS equScore, ");
        sql.append("0.0 AS engScore, 0.0 AS totalScore, ");
        sql.append("cb.applyState AS cbResult, ");
        sql.append("'' AS approveRemark, '' AS opinion, '' AS equExplain, ");
        sql.append("0 AS evaResult, cb.projectId, ");
        sql.append("'' AS serviceType, '' AS compId, '' AS companyName, '' AS companyAbbr ");
        sql.append("FROM pm_callback_header cb ");
        sql.append("LEFT JOIN pm_project p ON cb.projectId = p.id ");
        sql.append("LEFT JOIN fnd_department d ON cb.officeCode = d.departmentNum ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (queryParam != null) {
            if (StringUtils.hasText(queryParam.getOfficeCode())) {
                sql.append("AND cb.officeCode = ? ");
                params.add(queryParam.getOfficeCode());
            }
            if (queryParam.getOfficeCodes() != null) {
                String[] codes = queryParam.getOfficeCodes().split(",");
                sql.append("AND cb.officeCode IN (");
                for (int i = 0; i < codes.length; i++) {
                    sql.append(i > 0 ? ",?" : "?");
                    params.add(codes[i].trim());
                }
                sql.append(") ");
            }
            if (queryParam.getCbStartTime() != null) {
                sql.append("AND cb.applyTime >= ? ");
                params.add(java.sql.Timestamp.valueOf(queryParam.getCbStartTime()));
            }
            if (queryParam.getCbEndTime() != null) {
                sql.append("AND cb.applyTime <= ? ");
                params.add(java.sql.Timestamp.valueOf(queryParam.getCbEndTime()));
            }
            if (queryParam.getProjectType() != null) {
                sql.append("AND p.projectType = ? ");
                params.add(queryParam.getProjectType());
            }
        }
        sql.append("ORDER BY cb.applyTime DESC");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return rows.stream().map(this::mapToCbDataVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> overview(DataQueryParam queryParam) {
        // 迁移自: DataAnalysisAction.overview()
        Map<String, Object> result = new HashMap<>();
        List<CbDataVO> dataList = queryCbDataList(queryParam);

        result.put("totalCount", dataList.size());
        result.put("approvedCount", dataList.stream().filter(d -> d.getCbResult() != null && d.getCbResult() == 1).count());
        result.put("rejectedCount", dataList.stream().filter(d -> d.getCbResult() != null && d.getCbResult() == 2).count());
        result.put("pendingCount", dataList.stream().filter(d -> d.getCbResult() != null && d.getCbResult() == 0).count());

        double avgScore = dataList.stream()
                .filter(d -> d.getTotalScore() != null && d.getTotalScore() > 0)
                .mapToDouble(CbDataVO::getTotalScore)
                .average().orElse(0.0);
        result.put("avgScore", Math.round(avgScore * 100.0) / 100.0);

        return result;
    }

    @Override
    public List<Map<String, Object>> projectStatus(DataQueryParam queryParam) {
        // 迁移自: DataAnalysisAction.projectStatus()
        List<CbDataVO> dataList = queryCbDataList(queryParam);
        Map<Integer, Long> statusCount = dataList.stream()
                .filter(d -> d.getCbResult() != null)
                .collect(Collectors.groupingBy(CbDataVO::getCbResult, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        statusCount.forEach((status, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("status", status);
            item.put("statusName", getStatusName(status));
            item.put("count", count);
            result.add(item);
        });
        return result;
    }

    @Override
    public List<Map<String, Object>> byOffice(DataQueryParam queryParam) {
        // 迁移自: DataAnalysisAction.byOffice()
        List<CbDataVO> dataList = queryCbDataList(queryParam);
        Map<String, List<CbDataVO>> grouped = dataList.stream()
                .filter(d -> StringUtils.hasText(d.getOfficeName()))
                .collect(Collectors.groupingBy(CbDataVO::getOfficeName));

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((officeName, list) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("officeName", officeName);
            item.put("officeCode", list.get(0).getOfficeCode());
            item.put("totalCount", list.size());
            item.put("approvedCount", list.stream().filter(d -> d.getCbResult() != null && d.getCbResult() == 1).count());
            double avgScore = list.stream()
                    .filter(d -> d.getTotalScore() != null && d.getTotalScore() > 0)
                    .mapToDouble(CbDataVO::getTotalScore)
                    .average().orElse(0.0);
            item.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
            result.add(item);
        });
        return result;
    }

    @Override
    public List<Map<String, Object>> byTime(DataQueryParam queryParam) {
        // 迁移自: DataAnalysisAction.byTime()
        List<CbDataVO> dataList = queryCbDataList(queryParam);
        Map<String, Long> grouped = dataList.stream()
                .filter(d -> d.getCbTime() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getCbTime().toLocalDate().toString().substring(0, 7),
                        Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((month, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("count", count);
            result.add(item);
        });
        result.sort(Comparator.comparing(m -> (String) m.get("month")));
        return result;
    }

    @Override
    public List<Map<String, Object>> customQuery(Map<String, Object> queryParams) {
        // 迁移自: DataAnalysisAction.customQuery()
        DataQueryParam param = new DataQueryParam();
        if (queryParams.containsKey("officeCode")) {
            param.setOfficeCode((String) queryParams.get("officeCode"));
        }
        if (queryParams.containsKey("startTime")) {
            param.setCbStartTime(java.time.LocalDateTime.parse((String) queryParams.get("startTime")));
        }
        if (queryParams.containsKey("endTime")) {
            param.setCbEndTime(java.time.LocalDateTime.parse((String) queryParams.get("endTime")));
        }
        List<CbDataVO> dataList = queryCbDataList(param);
        return dataList.stream().map(vo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", vo.getId());
            map.put("projectName", vo.getProjectName());
            map.put("projectCode", vo.getProjectCode());
            map.put("officeName", vo.getOfficeName());
            map.put("cbTime", vo.getCbTime());
            map.put("totalScore", vo.getTotalScore());
            map.put("cbResult", vo.getCbResult());
            return map;
        }).collect(Collectors.toList());
    }

    // ===== 内部辅助方法 =====

    private CbDataVO mapToCbDataVO(Map<String, Object> row) {
        CbDataVO vo = new CbDataVO();
        vo.setId(toLong(row.get("id")));
        vo.setProjectName(toString(row.get("projectName")));
        vo.setProjectCode(toString(row.get("projectCode")));
        vo.setPmUserName(toString(row.get("pmUserName")));
        vo.setPmRealName(toString(row.get("pmRealName")));
        vo.setOfficeCode(toString(row.get("officeCode")));
        vo.setOfficeName(toString(row.get("officeName")));
        if (row.get("cbTime") instanceof java.sql.Timestamp) {
            vo.setCbTime(((java.sql.Timestamp) row.get("cbTime")).toLocalDateTime());
        }
        vo.setTimes(toInt(row.get("times")));
        vo.setProjectScore(toDouble(row.get("projectScore")));
        vo.setOtherScore(toDouble(row.get("otherScore")));
        vo.setEquScore(toDouble(row.get("equScore")));
        vo.setEngScore(toDouble(row.get("engScore")));
        vo.setTotalScore(toDouble(row.get("totalScore")));
        vo.setCbResult(toInt(row.get("cbResult")));
        vo.setApproveRemark(toString(row.get("approveRemark")));
        vo.setOpinion(toString(row.get("opinion")));
        vo.setEquExplain(toString(row.get("equExplain")));
        vo.setEvaResult(toInt(row.get("evaResult")));
        vo.setProjectId(toLong(row.get("projectId")));
        vo.setServiceType(toString(row.get("serviceType")));
        vo.setCompId(toString(row.get("compId")));
        vo.setCompanyName(toString(row.get("companyName")));
        vo.setCompanyAbbr(toString(row.get("companyAbbr")));
        return vo;
    }

    private String getStatusName(int status) {
        switch (status) {
            case -1: return "草稿";
            case 0: return "待审批";
            case 1: return "已通过";
            case 2: return "已驳回";
            default: return "未知";
        }
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.parseLong(val.toString()); } catch (Exception e) { return null; }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return null; }
    }

    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return null; }
    }

    private String toString(Object val) {
        return val != null ? val.toString() : null;
    }
}
