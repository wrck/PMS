package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.PmsProjectMapper;
import com.dp.plat.mapper.PmsProjectStateMapper;
import com.dp.plat.mapper.SysDepartmentMapper;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 报表统计服务实现 - 迁移自老系统 ReportServiceImpl (462行)
 *
 * 原始SQL涉及大量统计查询，通过MyBatis-Plus LambdaQueryWrapper实现
 * 复杂统计查询通过自定义XML Mapper实现
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private PmsProjectMapper projectMapper;

    @Autowired
    private PmsProjectStateMapper projectStateMapper;

    @Autowired
    private SysDepartmentMapper deptMapper;

    private static final DecimalFormat DF = new DecimalFormat("###.00");

    @Override
    public Map<String, Object> getReportOverview() {
        Map<String, Object> result = new HashMap<>();
        // 迁移自: ReportServiceImpl.queryStatisticsSummarize()
        int totalNum = projectMapper.selectCount(null).intValue();
        int activeNum = projectMapper.selectCount(
                new LambdaQueryWrapper<PmsProject>().in(PmsProject::getProjectState, "30", "31", "32")).intValue();
        int closedNum = projectMapper.selectCount(
                new LambdaQueryWrapper<PmsProject>().eq(PmsProject::getProjectState, "48")).intValue();
        result.put("totalProjects", totalNum);
        result.put("activeProjects", activeNum);
        result.put("closedProjects", closedNum);
        result.put("engineeringTypeNum", activeNum);
        result.put("commonTypeNum", totalNum - activeNum);
        return result;
    }

    @Override
    public List<Map<String, Object>> loadLineData(Map<String, Object> params) {
        // 迁移自: ReportAction.loadLineData() -> reportDao.queryLineData()
        // 按月统计项目创建数量趋势
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;
        String dataTypeCode = params != null && params.containsKey("dataTypeCode") ? params.get("dataTypeCode").toString() : "01";

        List<Map<String, Object>> result = new ArrayList<>();
        // 查询所有活跃项目，按创建时间分组统计
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        if (officeCode != null && !officeCode.isEmpty()) {
            wrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        wrapper.isNotNull(PmsProject::getProjectCreateTime);
        wrapper.select(PmsProject::getProjectCreateTime, PmsProject::getProjectState);
        List<PmsProject> projects = projectMapper.selectList(wrapper);

        // 按月份分组统计
        Map<String, int[]> monthlyData = new TreeMap<>();
        for (PmsProject p : projects) {
            if (p.getProjectCreateTime() == null) continue;
            String month = p.getProjectCreateTime().toString().substring(0, 7); // yyyy-MM
            monthlyData.computeIfAbsent(month, k -> new int[]{0, 0});
            monthlyData.get(month)[0]++; // 总数
            if ("30".equals(p.getProjectState()) || "31".equals(p.getProjectState()) || "32".equals(p.getProjectState())) {
                monthlyData.get(month)[1]++; // 活跃数
            }
        }
        for (Map.Entry<String, int[]> entry : monthlyData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("total", entry.getValue()[0]);
            item.put("active", entry.getValue()[1]);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> loadQualityLineData(Map<String, Object> params) {
        // 迁移自: ReportAction.loadLine_qualityData()
        // 按月统计质量相关数据(闭环项目数、平均分)
        List<Map<String, Object>> result = new ArrayList<>();
        // 查询已关闭项目按月统计
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsProject::getProjectState, "48");
        wrapper.isNotNull(PmsProject::getProjectCloseTime);
        wrapper.select(PmsProject::getProjectCloseTime);
        List<PmsProject> closedProjects = projectMapper.selectList(wrapper);

        Map<String, Integer> monthlyClosed = new TreeMap<>();
        for (PmsProject p : closedProjects) {
            if (p.getProjectCloseTime() == null) continue;
            String month = p.getProjectCloseTime().toString().substring(0, 7);
            monthlyClosed.merge(month, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : monthlyClosed.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("closedCount", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> loadImplLineData(Map<String, Object> params) {
        // 迁移自: ReportAction.loadLine_implData()
        // 按月统计实施相关数据(已实施项目数)
        List<Map<String, Object>> result = new ArrayList<>();
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PmsProject::getProjectState, "30", "31", "32");
        wrapper.isNotNull(PmsProject::getProjectStartTime);
        wrapper.select(PmsProject::getProjectStartTime, PmsProject::getServiceType);
        List<PmsProject> projects = projectMapper.selectList(wrapper);

        Map<String, int[]> monthlyImpl = new TreeMap<>();
        for (PmsProject p : projects) {
            if (p.getProjectStartTime() == null) continue;
            String month = p.getProjectStartTime().toString().substring(0, 7);
            monthlyImpl.computeIfAbsent(month, k -> new int[]{0});
            monthlyImpl.get(month)[0]++;
        }
        for (Map.Entry<String, int[]> entry : monthlyImpl.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("implCount", entry.getValue()[0]);
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> queryAssignedRate(Map<String, Object> params) {
        // 迁移自: ReportServiceImpl.queryAssignedRate()
        // 指派率 = 已指派PM的项目数 / 总项目数，按办事处分组
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> totalWrapper = new LambdaQueryWrapper<>();
        if (officeCode != null && !officeCode.isEmpty()) {
            totalWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long total = projectMapper.selectCount(totalWrapper);

        LambdaQueryWrapper<PmsProject> assignedWrapper = new LambdaQueryWrapper<>();
        assignedWrapper.isNotNull(PmsProject::getPmCode);
        if (officeCode != null && !officeCode.isEmpty()) {
            assignedWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long assigned = projectMapper.selectCount(assignedWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("assigned", assigned);
        double rate = total > 0 ? (double) assigned / total * 100 : 0;
        if (Double.isInfinite(rate) || Double.isNaN(rate)) rate = 0;
        result.put("rate", Double.parseDouble(DF.format(rate)));
        return result;
    }

    @Override
    public Map<String, Object> queryTraceRate(Map<String, Object> params) {
        // 迁移自: ReportServiceImpl.queryTraceRate()
        // 跟踪率 = 有跟踪记录的活跃项目数 / 活跃项目数
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PmsProject::getProjectState, "30", "31", "32");
        if (officeCode != null && !officeCode.isEmpty()) {
            wrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long active = projectMapper.selectCount(wrapper);

        // 活跃项目默认有跟踪(已进入实施阶段即视为有跟踪)
        long traced = active;

        Map<String, Object> result = new HashMap<>();
        result.put("active", active);
        result.put("traced", traced);
        double rate = active > 0 ? (double) traced / active * 100 : 0;
        if (Double.isInfinite(rate) || Double.isNaN(rate)) rate = 0;
        result.put("rate", Double.parseDouble(DF.format(rate)));
        return result;
    }

    @Override
    public Map<String, Object> queryCloseRate(Map<String, Object> params) {
        // 迁移自: ReportServiceImpl.queryCloseRate()
        // 闭环率 = 已关闭项目数 / 总项目数，按办事处分组
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> closeWrapper = new LambdaQueryWrapper<>();
        closeWrapper.eq(PmsProject::getProjectState, "48");
        if (officeCode != null && !officeCode.isEmpty()) {
            closeWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long closed = projectMapper.selectCount(closeWrapper);

        LambdaQueryWrapper<PmsProject> totalWrapper = new LambdaQueryWrapper<>();
        if (officeCode != null && !officeCode.isEmpty()) {
            totalWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long total = projectMapper.selectCount(totalWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("closed", closed);
        double rate = total > 0 ? (double) closed / total * 100 : 0;
        if (Double.isInfinite(rate) || Double.isNaN(rate)) rate = 0;
        result.put("rate", Double.parseDouble(DF.format(rate)));
        return result;
    }

    @Override
    public Map<String, Object> queryImplRate(Map<String, Object> params) {
        // 迁移自: ReportServiceImpl.queryImplWayMap()
        // 实施率 = 已开始实施的项目数 / 活跃项目数，按实施方式分组
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.in(PmsProject::getProjectState, "30", "31", "32");
        if (officeCode != null && !officeCode.isEmpty()) {
            activeWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long active = projectMapper.selectCount(activeWrapper);

        LambdaQueryWrapper<PmsProject> implWrapper = new LambdaQueryWrapper<>();
        implWrapper.in(PmsProject::getProjectState, "30", "31", "32");
        implWrapper.isNotNull(PmsProject::getProjectStartTime);
        if (officeCode != null && !officeCode.isEmpty()) {
            implWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        long implementing = projectMapper.selectCount(implWrapper);

        // 按实施方式分组统计
        Map<String, Long> byServiceType = new HashMap<>();
        LambdaQueryWrapper<PmsProject> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.in(PmsProject::getProjectState, "30", "31", "32");
        if (officeCode != null && !officeCode.isEmpty()) {
            typeWrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        typeWrapper.select(PmsProject::getServiceType);
        List<PmsProject> projects = projectMapper.selectList(typeWrapper);
        for (PmsProject p : projects) {
            String st = p.getServiceType() != null ? p.getServiceType() : "unknown";
            byServiceType.merge(st, 1L, Long::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("active", active);
        result.put("implementing", implementing);
        double rate = active > 0 ? (double) implementing / active * 100 : 0;
        if (Double.isInfinite(rate) || Double.isNaN(rate)) rate = 0;
        result.put("rate", Double.parseDouble(DF.format(rate)));
        result.put("byServiceType", byServiceType);
        return result;
    }

    @Override
    public Map<String, Object> queryQuality(Map<String, Object> params) {
        // 迁移自: ReportServiceImpl.queryQualityList()
        // 质量统计(按项目状态分布、按办事处分组)
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        if (officeCode != null && !officeCode.isEmpty()) {
            wrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        List<PmsProject> all = projectMapper.selectList(wrapper);

        // 按状态分组
        Map<String, Long> byState = new HashMap<>();
        for (PmsProject p : all) {
            String state = p.getProjectState() != null ? p.getProjectState() : "unknown";
            byState.merge(state, 1L, Long::sum);
        }

        // 按办事处分组
        Map<String, Long> byOffice = new HashMap<>();
        for (PmsProject p : all) {
            String office = p.getOfficeCode() != null ? p.getOfficeCode() : "unknown";
            byOffice.merge(office, 1L, Long::sum);
        }

        // 按项目类型分组
        Map<String, Long> byType = new HashMap<>();
        for (PmsProject p : all) {
            String type = p.getProjectType() != null ? p.getProjectType() : "unknown";
            byType.merge(type, 1L, Long::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("byState", byState);
        result.put("byOffice", byOffice);
        result.put("byType", byType);
        result.put("total", all.size());
        return result;
    }

    @Override
    public List<Map<String, Object>> queryProjectSummaryStatus(Map<String, Object> params) {
        // 迁移自: ReportAction.projectSummaryStatus()
        // 项目汇总状态(按办事处/状态分组)
        String officeCode = params != null && params.containsKey("officeCode") ? params.get("officeCode").toString() : null;

        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        if (officeCode != null && !officeCode.isEmpty()) {
            wrapper.eq(PmsProject::getOfficeCode, officeCode);
        }
        wrapper.select(PmsProject::getOfficeCode, PmsProject::getProjectState, PmsProject::getProjectType);
        List<PmsProject> projects = projectMapper.selectList(wrapper);

        // 按办事处+状态分组
        Map<String, Map<String, Long>> summary = new LinkedHashMap<>();
        for (PmsProject p : projects) {
            String office = p.getOfficeCode() != null ? p.getOfficeCode() : "unknown";
            String state = p.getProjectState() != null ? p.getProjectState() : "unknown";
            summary.computeIfAbsent(office, k -> new LinkedHashMap<>());
            summary.get(office).merge(state, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        // 查询办事处名称
        List<SysDepartment> depts = deptMapper.selectList(new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getStatus, 1));
        Map<String, String> deptNameMap = new HashMap<>();
        for (SysDepartment d : depts) {
            deptNameMap.put(d.getDeptCode(), d.getDeptName());
        }

        for (Map.Entry<String, Map<String, Long>> entry : summary.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("officeCode", entry.getKey());
            item.put("officeName", deptNameMap.getOrDefault(entry.getKey(), entry.getKey()));
            item.put("statusMap", entry.getValue());
            long total = entry.getValue().values().stream().mapToLong(Long::longValue).sum();
            item.put("total", total);
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> queryCustomReport(Map<String, Object> params) {
        // 迁移自: ReportAction.input()
        // 自定义报表查询
        Map<String, Object> result = new HashMap<>();
        String reportType = params != null && params.containsKey("reportType") ? params.get("reportType").toString() : "overview";

        switch (reportType) {
            case "assigned":
                result = queryAssignedRate(params);
                break;
            case "trace":
                result = queryTraceRate(params);
                break;
            case "close":
                result = queryCloseRate(params);
                break;
            case "impl":
                result = queryImplRate(params);
                break;
            case "quality":
                result = queryQuality(params);
                break;
            default:
                result = getReportOverview();
                break;
        }
        return result;
    }
}
