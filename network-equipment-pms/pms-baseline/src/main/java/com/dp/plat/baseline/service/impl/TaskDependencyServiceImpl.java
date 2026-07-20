package com.dp.plat.baseline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.baseline.dto.CycleNode;
import com.dp.plat.baseline.entity.TaskDependency;
import com.dp.plat.baseline.exception.CycleDetectedException;
import com.dp.plat.baseline.mapper.TaskDependencyMapper;
import com.dp.plat.baseline.service.TaskDependencyService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 任务依赖服务实现 — 保存依赖含 DFS 循环检测。
 *
 * <p>关联设计文档：§3.6 循环依赖检测（Story 4 验收 1）。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDependencyServiceImpl extends ServiceImpl<TaskDependencyMapper, TaskDependency>
        implements TaskDependencyService {

    /** 合法的依赖类型。 */
    private static final Set<String> VALID_TYPES = Set.of("FS", "FF", "SS", "SF");

    private final ImplTaskMapper implTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDependency saveDependency(TaskDependency dependency) {
        // 1. 依赖类型校验
        if (dependency.getDependencyType() == null
                || !VALID_TYPES.contains(dependency.getDependencyType())) {
            throw new BusinessException("依赖类型非法，仅支持 FS/FF/SS/SF");
        }
        if (dependency.getLagDays() == null) {
            dependency.setLagDays(0);
        }

        Long predecessorId = dependency.getPredecessorTaskId();
        Long successorId = dependency.getSuccessorTaskId();
        if (predecessorId == null || successorId == null) {
            throw new BusinessException("前置任务ID与后续任务ID不能为空");
        }

        // 2. 自环检测
        if (predecessorId.equals(successorId)) {
            throw new BusinessException("任务不能依赖自身");
        }

        // 3. 校验前置/后续任务存在
        String predName = implTaskMapper.selectTaskNameById(predecessorId);
        if (predName == null) {
            throw new BusinessException("前置任务不存在：id=" + predecessorId);
        }
        String succName = implTaskMapper.selectTaskNameById(successorId);
        if (succName == null) {
            throw new BusinessException("后续任务不存在：id=" + successorId);
        }

        // 4. 闭环检测：从 successor 出发沿 predecessor→successor 边遍历，
        //    若能回到 predecessor 则形成闭环
        List<Long> cycleIds = detectCycle(successorId, predecessorId, dependency.getProjectId());
        if (!cycleIds.isEmpty()) {
            // 拼装闭环路径节点（含首尾闭合节点）
            List<CycleNode> cyclePath = new ArrayList<>();
            for (Long taskId : cycleIds) {
                String name = implTaskMapper.selectTaskNameById(taskId);
                cyclePath.add(CycleNode.builder().taskId(taskId).taskName(name).build());
            }
            throw new CycleDetectedException(cyclePath);
        }

        // 5. 无闭环 → 保存
        this.save(dependency);
        log.info("保存任务依赖成功：pred={} → succ={}, type={}", predecessorId, successorId,
                dependency.getDependencyType());
        return dependency;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDependency(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException("依赖关系不存在：id=" + id);
        }
        this.removeById(id);
    }

    @Override
    public List<TaskDependency> listByProject(Long projectId) {
        return this.list(new LambdaQueryWrapper<TaskDependency>()
                .eq(TaskDependency::getProjectId, projectId)
                .orderByAsc(TaskDependency::getPredecessorTaskId)
                .orderByAsc(TaskDependency::getSuccessorTaskId));
    }

    /**
     * 闭环检测：从 {@code start}（successor）沿 predecessor→successor 方向 DFS，
     * 若能到达 {@code target}（predecessor）则返回从 start 到 target 的路径（含两端），
     * 并在末尾追加 start 形成闭合路径；未找到返回空列表。
     *
     * <p>实现说明（TD-P8-009 修复）：采用按需加载邻接节点的增量 DFS，不再一次性
     * 全量加载项目所有依赖到内存。仅对 DFS 实际访问到的节点查询其后继列表，
     * 减小大规模项目（>1000 任务）下的内存与首屏延迟压力。</p>
     *
     * <p>性能注记：当前实现为逐节点查询，最坏情况下 DFS 访问 N 个节点则触发 N 次
     * 数据库查询。对于超大规模项目（>1000 任务且依赖密集），可进一步优化为
     * 「批量加载」（每次按 N 个节点 ID 批量查询后继，N 推荐 50-200），
     * 在数据库往返次数与单次结果集大小间取得平衡。</p>
     *
     * @param start     起点（=新增边的 successor）
     * @param target    目标（=新增边的 predecessor）
     * @param projectId 项目ID（限定依赖图范围）
     * @return 闭合路径任务ID列表（首尾相同），或空列表
     */
    private List<Long> detectCycle(Long start, Long target, Long projectId) {
        Set<Long> visited = new HashSet<>();
        List<Long> path = new ArrayList<>();
        if (dfs(start, target, projectId, visited, path)) {
            // path 为 start...target，追加 start 闭合
            path.add(start);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * DFS 查找从 {@code current} 到 {@code target} 的路径（增量按需加载后继）。
     *
     * <p>每次进入节点时按 {@code predecessorTaskId = current} 查询其后继列表，
     * 仅查询当前 DFS 路径所需节点，避免全量加载邻接表。</p>
     *
     * @return true 表示找到路径（结果存入 {@code path}，含 current...target）
     */
    private boolean dfs(Long current, Long target, Long projectId,
                        Set<Long> visited, List<Long> path) {
        path.add(current);
        if (current.equals(target)) {
            return true;
        }
        visited.add(current);
        // 按需加载：仅查询当前节点的直接后继，避免全量加载邻接表
        List<TaskDependency> successors = this.list(new LambdaQueryWrapper<TaskDependency>()
                .eq(TaskDependency::getProjectId, projectId)
                .eq(TaskDependency::getPredecessorTaskId, current));
        for (TaskDependency dep : successors) {
            Long next = dep.getSuccessorTaskId();
            if (!visited.contains(next)) {
                if (dfs(next, target, projectId, visited, path)) {
                    return true;
                }
            }
        }
        // 回溯：当前分支未命中，移除 current
        path.remove(path.size() - 1);
        return false;
    }
}
