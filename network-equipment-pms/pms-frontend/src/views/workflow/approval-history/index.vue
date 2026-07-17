<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getApprovalHistory, type ApprovalHistory } from '@/api/approval-center'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'

const route = useRoute()
const router = useRouter()

const history = ref<ApprovalHistory[]>([])
const loading = ref(false)

const recordId = computed(() => Number(route.params.recordId))

// 按轮次分组统计
const roundSummary = computed(() => {
  const rounds = new Map<number, number>()
  for (const h of history.value) {
    const r = h.round || 1
    rounds.set(r, (rounds.get(r) || 0) + 1)
  }
  return Array.from(rounds.entries())
    .map(([round, count]) => ({ round, count }))
    .sort((a, b) => a.round - b.round)
})

async function loadHistory() {
  loading.value = true
  try {
    history.value = await getApprovalHistory(recordId.value)
  } catch {
    history.value = []
  } finally {
    loading.value = false
  }
}

function back() {
  router.back()
}

function actionTagType(action?: string) {
  switch (action) {
    case 'APPROVE':
      return 'success'
    case 'REJECT':
      return 'danger'
    case 'WITHDRAW':
      return 'info'
    case 'RESUBMIT':
      return 'primary'
    case 'SUBMIT':
      return 'warning'
    case 'TIMEOUT':
      return 'danger'
    case 'ESCALATE':
      return 'danger'
    default:
      return 'info'
  }
}

function actionLabel(action?: string) {
  const map: Record<string, string> = {
    SUBMIT: '提交',
    APPROVE: '通过',
    REJECT: '退回',
    WITHDRAW: '撤回',
    RESUBMIT: '重新提交',
    ESCALATE: '升级',
    TIMEOUT: '超时'
  }
  return map[action || ''] || action || '-'
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header :icon="'ArrowLeft'" :title="'返回'" @back="back">
      <template #content>
        <span class="header-title">审批历史（记录 #{{ recordId }}）</span>
      </template>
    </el-page-header>

    <!-- 轮次概览 -->
    <el-card v-if="roundSummary.length > 0" shadow="never">
      <template #header>
        <span class="page-title">轮次概览</span>
      </template>
      <div class="round-summary">
        <el-tag
          v-for="item in roundSummary"
          :key="item.round"
          size="large"
          type="info"
          effect="plain"
        >
          第 {{ item.round }} 轮 · {{ item.count }} 条记录
        </el-tag>
      </div>
    </el-card>

    <!-- 多轮次时间轴 -->
    <el-card shadow="never">
      <template #header>
        <span class="page-title">审批轨迹</span>
      </template>
      <ApprovalTimeline :history="history" />

      <!-- 明细表格 -->
      <h4 class="sub-title">历史明细</h4>
      <el-table :data="history" border stripe>
        <el-table-column label="轮次" width="80" align="center">
          <template #default="{ row }">第 {{ row.round }} 轮</template>
        </el-table-column>
        <el-table-column prop="nodeName" label="节点" min-width="120" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" min-width="110" show-overflow-tooltip />
        <el-table-column label="动作" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.action)" size="small">
              {{ actionLabel(row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="opinion" label="意见" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatedAt" label="操作时间" min-width="160" />
        <template #empty>
          <el-empty description="暂无审批历史" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  margin-left: 8px;
}
.sub-title {
  margin: 16px 0 8px;
  font-size: 14px;
  font-weight: 600;
}
.round-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
