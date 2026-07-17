<script setup lang="ts">
import { computed } from 'vue'
import type { TaskDiff } from '@/api/baseline'

/**
 * 基线偏差对比表 — 逐任务展示基线计划 vs 当前计划的偏差。
 *
 * <p>关联设计文档 §5.5 Story 4 验收 2。结束偏差非 0 的行高亮：
 * 延迟（正）红色、提前（负）绿色。表头展示是否需要审批与偏差任务总数。</p>
 */
interface Props {
  /** 逐任务偏差列表 */
  diffs: TaskDiff[]
  /** 偏差任务总数 */
  totalVarianced?: number
  /** 是否需要审批 */
  needsApproval?: boolean
  /** 审批原因 */
  approvalReason?: string
}

const props = defineProps<Props>()

/** 行样式：结束偏差非 0 时高亮 */
function rowClassName({ row }: { row: TaskDiff }): string {
  const v = row.endVariance
  if (v == null) return ''
  if (v > 0) return 'row-delayed'
  if (v < 0) return 'row-early'
  return ''
}

/** 偏差文本：正=延迟，负=提前，0=准时，null=- */
function varianceText(v?: number): string {
  if (v == null) return '-'
  if (v === 0) return '准时'
  return v > 0 ? `延迟 ${v} 天` : `提前 ${-v} 天`
}

function varianceType(v?: number): 'danger' | 'success' | 'info' {
  if (v == null) return 'info'
  if (v > 0) return 'danger'
  if (v < 0) return 'success'
  return 'info'
}

function percentText(v?: number): string {
  if (v == null) return '-'
  return `${v.toFixed(2)}%`
}

const summaryText = computed(
  () => `偏差任务 ${props.totalVarianced ?? 0} / ${props.diffs.length} 个`
)
</script>

<template>
  <div class="baseline-diff-table">
    <el-alert
      v-if="needsApproval"
      type="warning"
      show-icon
      :closable="false"
      :title="`偏差超阈值，需要审批：${approvalReason ?? ''}`"
      class="diff-alert"
    />
    <el-alert
      v-else
      type="success"
      show-icon
      :closable="false"
      title="偏差未超阈值，无需审批"
      class="diff-alert"
    />

    <el-table :data="diffs" stripe :row-class-name="rowClassName" border>
      <el-table-column label="任务名称" min-width="160" prop="taskName" />
      <el-table-column label="基线开始" width="120" align="center">
        <template #default="{ row }">{{ row.baselineStart || '-' }}</template>
      </el-table-column>
      <el-table-column label="当前开始" width="120" align="center">
        <template #default="{ row }">{{ row.currentStart || '-' }}</template>
      </el-table-column>
      <el-table-column label="开始偏差" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="varianceType(row.startVariance)" size="small" effect="plain">
            {{ varianceText(row.startVariance) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="基线结束" width="120" align="center">
        <template #default="{ row }">{{ row.baselineEnd || '-' }}</template>
      </el-table-column>
      <el-table-column label="当前结束" width="120" align="center">
        <template #default="{ row }">{{ row.currentEnd || '-' }}</template>
      </el-table-column>
      <el-table-column label="结束偏差" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="varianceType(row.endVariance)" size="small" effect="dark">
            {{ varianceText(row.endVariance) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="偏差百分比" width="120" align="center">
        <template #default="{ row }">
          <span :class="{ 'percent-warn': (row.percentVariance ?? 0) > 10 }">
            {{ percentText(row.percentVariance) }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <div class="diff-summary">{{ summaryText }}</div>
  </div>
</template>

<style scoped>
.baseline-diff-table {
  width: 100%;
}
.diff-alert {
  margin-bottom: 12px;
}
.diff-summary {
  margin-top: 8px;
  color: #606266;
  font-size: 13px;
}
.percent-warn {
  color: #f56c6c;
  font-weight: 600;
}
:deep(.row-delayed) {
  background-color: #fff1f0 !important;
}
:deep(.row-early) {
  background-color: #f6ffed !important;
}
</style>
