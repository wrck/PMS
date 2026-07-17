<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  diffBaseline,
  type BaselineDiffResult,
  type BaselineStatus
} from '@/api/baseline'
import BaselineDiffTable from '@/components/BaselineDiffTable.vue'
import type { EpTagType } from '@/types'

/**
 * 基线偏差分析视图。
 *
 * <p>关联设计文档 §5.5 Story 4 验收 2：调用 {@code GET /api/baseline/diff}
 * 获取基线快照与当前计划的逐任务偏差，由 {@code BaselineDiffTable} 渲染。
 * 路由 {@code /baseline/diff/:baselineId} 由 baseline/index.vue 的「偏差分析」按钮跳入。</p>
 */
defineOptions({ name: 'BaselineDiff' })

const route = useRoute()
const router = useRouter()

const baselineId = computed(() => Number(route.params.baselineId))

const loading = ref(false)
const result = ref<BaselineDiffResult | null>(null)

function statusMeta(status?: BaselineStatus): { label: string; tagType: EpTagType } {
  switch (status) {
    case 'APPROVED':
      return { label: '已批准', tagType: 'success' }
    case 'SUPERSEDED':
      return { label: '已取代', tagType: 'info' }
    case 'DRAFT':
    default:
      return { label: '草稿', tagType: 'warning' }
  }
}

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 19)
}

const baseline = computed(() => result.value?.baseline ?? null)
const diffs = computed(() => result.value?.diffs ?? [])
const hasDiff = computed(() => diffs.value.length > 0)

// ============ 数据加载 ============
async function loadDiff() {
  if (!baselineId.value || Number.isNaN(baselineId.value)) {
    ElMessage.error('基线 ID 无效')
    return
  }
  loading.value = true
  try {
    result.value = await diffBaseline(baselineId.value)
  } catch {
    result.value = null
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

/** 返回基线列表 */
function backToBaselineList() {
  router.push({ name: 'BaselineList' })
}

onMounted(loadDiff)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header :icon="null" @back="goBack">
      <template #content>
        <div class="header-content">
          <span class="header-title">基线偏差分析</span>
          <el-tag
            v-if="baseline"
            :type="statusMeta(baseline.status).tagType"
            size="small"
            effect="dark"
          >
            {{ statusMeta(baseline.status).label }}
          </el-tag>
          <span v-if="baseline" class="header-name">{{ baseline.baselineName }}</span>
          <span v-if="baseline?.approvedAt" class="header-stat">
            批准时间：{{ formatDateTime(baseline.approvedAt) }}
          </span>
        </div>
      </template>
      <template #extra>
        <el-button type="primary" link @click="backToBaselineList">返回基线列表</el-button>
        <el-button :icon="'Refresh'" link @click="loadDiff">刷新</el-button>
      </template>
    </el-page-header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>偏差对比明细</span>
          <span v-if="result" class="card-summary">
            偏差任务 {{ result.totalVarianced ?? 0 }} / {{ diffs.length }} 个
            <el-tag
              v-if="result.needsApproval"
              type="warning"
              size="small"
              effect="plain"
              style="margin-left: 8px"
            >
              需审批
            </el-tag>
            <el-tag v-else type="success" size="small" effect="plain" style="margin-left: 8px">
              无需审批
            </el-tag>
          </span>
        </div>
      </template>

      <el-empty
        v-if="!loading && !result"
        description="基线不存在或加载失败，请返回列表重试"
      />
      <el-empty
        v-else-if="!loading && result && !hasDiff"
        description="暂无任务偏差数据（基线快照为空或当前无任务）"
      />
      <BaselineDiffTable
        v-else-if="result"
        :diffs="diffs"
        :total-varianced="result.totalVarianced"
        :needs-approval="result.needsApproval"
        :approval-reason="result.approvalReason"
      />
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-name {
  font-size: 14px;
  color: #606266;
}
.header-stat {
  font-size: 13px;
  color: #909399;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #303133;
}
.card-summary {
  font-size: 13px;
  color: #606266;
  font-weight: normal;
  display: inline-flex;
  align-items: center;
}
</style>
