package com.dp.plat.governance.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.service.IChangeRequestService;
import com.dp.plat.governance.issue.entity.Issue;
import com.dp.plat.governance.issue.service.IIssueService;
import com.dp.plat.governance.risk.dto.RiskMatrixDto;
import com.dp.plat.governance.risk.entity.Risk;
import com.dp.plat.governance.risk.mapper.RiskMapper;
import com.dp.plat.governance.risk.service.IRiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IRiskService}.
 *
 * <p>Supports three-book linkage:
 * <ul>
 *   <li>{@link #markOccurred} converts a materialized risk into a new issue and
 *       closes the risk.</li>
 *   <li>{@link #escalate} creates a change request from the risk and marks the
 *       risk as ESCALATED.</li>
 * </ul>
 * The {@link IIssueService} and {@link IChangeRequestService} are injected via
 * constructor; the dependency graph is acyclic (risk → issue → change request),
 * so no circular dependency arises.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskServiceImpl
        extends ServiceImpl<RiskMapper, Risk>
        implements IRiskService {

    /** Default status for a newly identified risk. */
    private static final String STATUS_OPEN = "OPEN";
    /** Status while the risk is being mitigated. */
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    /** Status after the risk is closed. */
    private static final String STATUS_CLOSED = "CLOSED";
    /** Status when the risk is escalated to a change request. */
    private static final String STATUS_ESCALATED = "ESCALATED";

    /** Priority band: score 1-6. */
    private static final String PRIORITY_LOW = "LOW";
    /** Priority band: score 7-12. */
    private static final String PRIORITY_MEDIUM = "MEDIUM";
    /** Priority band: score 13-25. */
    private static final String PRIORITY_HIGH = "HIGH";

    /** Minimum/maximum likelihood and impact scores. */
    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;
    /** Matrix dimension (5x5). */
    private static final int MATRIX_SIZE = 5;

    private final IIssueService issueService;
    private final IChangeRequestService changeRequestService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Risk> create(Risk risk) {
        if (risk == null) {
            throw new BusinessException("风险信息不能为空");
        }
        if (!StringUtils.hasText(risk.getDescription())) {
            throw new BusinessException("风险描述不能为空");
        }
        risk.setId(null);
        risk.setRiskNo(generateRiskNo());
        computeScore(risk);
        risk.setStatus(STATUS_OPEN);
        risk.setIdentifiedAt(LocalDateTime.now());
        this.save(risk);
        return Result.ok(risk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(Risk risk) {
        if (risk == null || risk.getId() == null) {
            throw new BusinessException("风险信息或ID不能为空");
        }
        Risk existing = baseMapper.selectById(risk.getId());
        if (existing == null) {
            throw new BusinessException("风险不存在");
        }
        computeScore(risk);
        this.updateById(risk);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long id) {
        Risk existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("风险不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result<List<Risk>> listAll() {
        List<Risk> list = this.list(new LambdaQueryWrapper<Risk>()
                .orderByDesc(Risk::getCreateTime));
        return Result.ok(list);
    }

    @Override
    public Result<Risk> getById(Long id) {
        Risk risk = baseMapper.selectById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }
        return Result.ok(risk);
    }

    @Override
    public Result<List<Risk>> listByProject(Long projectId) {
        if (projectId == null) {
            return Result.ok(List.of());
        }
        List<Risk> list = this.list(new LambdaQueryWrapper<Risk>()
                .eq(Risk::getProjectId, projectId)
                .orderByDesc(Risk::getCreateTime));
        return Result.ok(list);
    }

    @Override
    public void computeScore(Risk risk) {
        if (risk == null) {
            return;
        }
        Integer likelihood = risk.getLikelihood();
        Integer impact = risk.getImpact();
        if (likelihood != null && impact != null) {
            int l = clamp(likelihood);
            int i = clamp(impact);
            int score = l * i;
            risk.setScore(score);
            risk.setPriority(score <= 6 ? PRIORITY_LOW : (score <= 12 ? PRIORITY_MEDIUM : PRIORITY_HIGH));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> markOccurred(Long id) {
        Risk risk = baseMapper.selectById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }
        // Create a new issue converted from this risk.
        Issue issue = Issue.builder()
                .projectId(risk.getProjectId())
                .issueNo(issueService.generateIssueNo())
                .description("由风险 " + risk.getRiskNo() + " 转化: " + risk.getDescription())
                .raisedBy(risk.getOwnerId())
                .raisedByName(risk.getOwnerName())
                .priority(StringUtils.hasText(risk.getPriority()) ? risk.getPriority() : PRIORITY_MEDIUM)
                .status("OPEN")
                .sourceRiskId(risk.getId())
                .sourceRiskNo(risk.getRiskNo())
                .build();
        issueService.save(issue);
        // Close the risk since it has materialized into an issue.
        risk.setStatus(STATUS_CLOSED);
        risk.setClosedAt(LocalDateTime.now());
        this.updateById(risk);
        return Result.ok(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> escalate(Long id) {
        Risk risk = baseMapper.selectById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }
        // Create a change request escalated from this risk.
        ChangeRequest cr = ChangeRequest.builder()
                .projectId(risk.getProjectId())
                .title("由" + risk.getRiskNo() + "升级的变更请求")
                .description("由风险 " + risk.getRiskNo() + " 升级: " + risk.getDescription())
                .requesterId(risk.getOwnerId())
                .requesterName(risk.getOwnerName())
                .requestDate(LocalDate.now())
                .priority(StringUtils.hasText(risk.getPriority()) ? risk.getPriority() : PRIORITY_MEDIUM)
                .build();
        Result<ChangeRequest> crResult = changeRequestService.create(cr);
        // Mark the risk as escalated.
        risk.setStatus(STATUS_ESCALATED);
        this.updateById(risk);
        return Result.ok(crResult.getData());
    }

    @Override
    public String generateRiskNo() {
        int year = LocalDate.now().getYear();
        String prefix = "RISK-" + year + "-";
        long count = this.count(new LambdaQueryWrapper<Risk>()
                .likeRight(Risk::getRiskNo, prefix));
        long sequence = count + 1;
        return prefix + String.format("%04d", sequence);
    }

    @Override
    public Result<RiskMatrixDto> riskMatrix(Long projectId) {
        List<Risk> risks = projectId == null
                ? this.list()
                : this.list(new LambdaQueryWrapper<Risk>().eq(Risk::getProjectId, projectId));
        // Build a 5x5 matrix: matrix[likelihood-1][impact-1] = count.
        List<List<Integer>> matrix = new ArrayList<>(MATRIX_SIZE);
        for (int l = 0; l < MATRIX_SIZE; l++) {
            List<Integer> row = new ArrayList<>(MATRIX_SIZE);
            for (int i = 0; i < MATRIX_SIZE; i++) {
                row.add(0);
            }
            matrix.add(row);
        }
        int highPriorityCount = 0;
        for (Risk risk : risks) {
            Integer likelihood = risk.getLikelihood();
            Integer impact = risk.getImpact();
            if (likelihood != null && impact != null) {
                int l = clamp(likelihood) - 1;
                int i = clamp(impact) - 1;
                matrix.get(l).set(i, matrix.get(l).get(i) + 1);
            }
            if (PRIORITY_HIGH.equals(risk.getPriority())) {
                highPriorityCount++;
            }
        }
        RiskMatrixDto dto = RiskMatrixDto.builder()
                .matrix(matrix)
                .risks(risks)
                .totalRisks(risks.size())
                .highPriorityCount(highPriorityCount)
                .build();
        return Result.ok(dto);
    }

    /**
     * Clamp a score value to the valid range [1, 5].
     */
    private int clamp(int value) {
        if (value < MIN_SCORE) {
            return MIN_SCORE;
        }
        if (value > MAX_SCORE) {
            return MAX_SCORE;
        }
        return value;
    }
}
