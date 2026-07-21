<script setup lang="ts">
// =============================================================================
// ApprovalHistory - 审批历史页（Task 11 Step 3）
// -----------------------------------------------------------------------------
// 显示单个审批记录的多轮次历史。
// 顶部 PageHeader：标题"审批历史 · {审批标题}"
// 主体：
//   - 轮次时间轴（el-timeline）：
//       每轮作为一个大节点，显示轮次号 + 提交时间 + 提交人 + 当前状态
//       展开后显示该轮的所有节点历史（每节点：节点名 / 审批人 / 审批时间 / 审批意见 / 审批结果）
//   - 业务数据快照（按轮次）：
//       每轮的业务数据快照（el-table 展示字段/值）
//       高亮变化的字段（与上一轮对比）
// 加载中：SkeletonCard  空状态：EmptyState
// =============================================================================
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getApprovalDetail,
  getApprovalHistory,
  type ApprovalDetailVO,
  type ApprovalHistory,
  type ApprovalRecord
} from '@/api/approval-center'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'
import SensitiveFieldDisplay from '@/components/SensitiveFieldDisplay.vue'
import type { EpTagType } from '@/types'

defineOptions({ name: 'ApprovalHistory' })

const route = useRoute()
const router = useRouter()

const detail = ref<ApprovalDetailVO | null>(null)
const history = ref<ApprovalHistory[]>([])
const loading = ref(false)

const recordId = computed(() => Number(route.params.recordId))
const record = computed<ApprovalRecord | null>(() => detail.value?.record ?? null)
const maskedFields = computed(() => detail.value?.maskedFields ?? [])
const businessData = computed<Record<string, unknown>>(() => detail.value?.businessData ?? {})

// ============ 标签 / 文本辅助 ============
const typeLabelMap: Record<string, string> = {
  PROJECT: '项目',
  TASK: '任务',
  DELIVERABLE: '交付件',
  RISK: '风险',
  ISSUE: '问题',
  CHANGE: '变更',
  RESOURCE: '资源',
  COST: '成本',
  PHASE_EXIT: '阶段退出',
  BASELINE_CHANGE: '基线变更'
}

function typeLabel(type?: string): string {
  return typeLabelMap[type ?? ''] ?? type ?? '-'
}

function actionMeta(action?: string): { tagType: EpTagType; label: string } {
  switch (action) {
    case 'SUBMIT':
      return { tagType: 'warning', label: '提交' }
    case 'APPROVE':
      return { tagType: 'success', label: '通过' }
    case 'REJECT':
      return { tagType: 'danger', label: '退回' }
    case 'WITHDRAW':
      return { tagType: 'info', label: '撤回' }
    case 'RESUBMIT':
      return { tagType: 'primary', label: '重新提交' }
    case 'ESCALATE':
      return { tagType: 'danger', label: '升级' }
    case 'TIMEOUT':
      return { tagType: 'danger', label: '超时' }
    default:
      return { tagType: 'info', label: action ?? '-' }
  }
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

// ============ 按轮次分组 ============
interface RoundGroup {
  round: number
  items: ApprovalHistory[]
  submitItem?: ApprovalHistory
  lastItem?: ApprovalHistory
  isCurrent: boolean
}

const roundGroups = computed<RoundGroup[]>(() => {
  const map = new Map<number, ApprovalHistory[]>()
  for (const h of history.value) {
    const r = h.round || 1
    if (!map.has(r)) map.set(r, [])
    map.get(r)!.push(h)
  }
  const groups: RoundGroup[] = []
  const currentRound = record.value?.round ?? 1
  for (const [round, items] of map.entries()) {
    items.sort((a, b) => {
      const ta = a.operatedAt ? new Date(a.operatedAt).getTime() : 0
      const tb = b.operatedAt ? new Date(b.operatedAt).getTime() : 0
      return ta - tb
    })
    const submitItem = items.find((i) => i.action === 'SUBMIT' || i.action === 'RESUBMIT')
    const lastItem = items[items.length - 1]
    groups.push({
      round,
      items,
      submitItem,
      lastItem,
      isCurrent: round === currentRound
    })
  }
  groups.sort((a, b) => b.round - a.round) // 倒序，最新轮次在前
  return groups
})

// 展开的轮次（默认展开当前轮次）
const expandedRounds = ref<Set<number>>(new Set())
function toggleRound(round: number) {
  if (expandedRounds.value.has(round)) {
    expandedRounds.value.delete(round)
  } else {
    expandedRounds.value.add(round)
  }
  // 触发响应式更新
  expandedRounds.value = new Set(expandedRounds.value)
}

function isRoundExpanded(round: number): boolean {
  return expandedRounds.value.has(round)
}

// ============ 业务数据字段列表（当前快照）============
interface SnapshotField {
  name: string
  value: unknown
  isHighlighted: boolean
}

const snapshotFields = computed<SnapshotField[]>(() => {
  // 后端目前只提供当前快照（不保存每轮快照），故仅展示当前轮的字段
  // 若多轮数据存在差异，前端通过对比高亮（这里仅展示当前快照）
  const entries: SnapshotField[] = []
  for (const [k, v] of Object.entries(businessData.value)) {
    const meta = maskedFields.value.find((m) => m.fieldName === k)
    if (meta?.permission === 'HIDDEN') continue
    entries.push({ name: k, value: v, isHighlighted: false })
  }
  return entries
})

// ============ 数据加载 ============
async function loadHistory() {
  if (!recordId.value) return
  loading.value = true
  try {
    // 并行加载详情（获取 businessData / maskedFields / record）和历史
    const [detailRes, historyRes] = await Promise.all([
      getApprovalDetail(recordId.value),
      getApprovalHistory(recordId.value)
    ])
    detail.value = detailRes
    history.value = historyRes ?? []
    // 默认展开当前轮次
    const currentRound = detailRes.record?.round ?? 1
    expandedRounds.value = new Set([currentRound])
  } catch {
    detail.value = null
    history.value = []
  } finally {
    loading.value = false
  }
}

function back() {
  router.back()
}

function goDetail() {
  if (!record.value?.id) return
  router.push(`/workflow/approval-detail/${record.value.id}`)
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div class="approval-history-page">
    <SkeletonCard v-if="loading" :loading="true" :rows="10">
      <div />
    </SkeletonCard>

    <EmptyState
      v-else-if="!record && history.length === 0"
      title="未找到审批历史"
      description="该审批可能已被删除或 ID 无效"
      icon="Warning"
    >
      <template #action>
        <el-button type="primary" @click="back">返回</el-button>
      </template>
    </EmptyState>

    <template v-else>
      <PageHeader
        :title="`审批历史 · ${record?.title ?? '#' + recordId}`"
        :description="`编号 #${record?.id ?? recordId} · ${typeLabel(record?.approvalType)} · 共 ${roundGroups.length} 轮 · ${history.length} 条记录`"
      >
        <template #actions>
          <el-button v-if="record?.id" type="primary" @click="goDetail">查看详情</el-button>
          <el-button @click="back">返回</el-button>
        </template>
      </PageHeader>

      <div class="history-body">
        <!-- ============ 轮次时间轴 ============ -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <span class="section-title">轮次时间轴</span>
          </template>
          <el-timeline v-if="roundGroups.length > 0">
            <el-timeline-item
              v-for="group in roundGroups"
              :key="group.round"
              :timestamp="formatDateTime(group.submitItem?.operatedAt || group.lastItem?.operatedAt)"
              placement="top"
              :type="group.isCurrent ? 'primary' : group.lastItem?.action === 'APPROVE' ? 'success' : group.lastItem?.action === 'REJECT' ? 'danger' : 'info'"
              :hollow="!group.isCurrent"
              size="large"
            >
              <div class="round-header" @click="toggleRound(group.round)">
                <div class="round-info">
                  <el-tag :type="group.isCurrent ? 'primary' : 'info'" effect="dark" size="default">
                    第 {{ group.round }} 轮
                  </el-tag>
                  <span v-if="group.isCurrent" class="current-badge">当前轮次</span>
                  <span class="round-submitter">
                    提交人：{{ group.submitItem?.operatorName || record?.submitterName || '-' }}
                  </span>
                  <el-tag
                    v-if="group.lastItem"
                    :type="actionMeta(group.lastItem.action).tagType"
                    size="small"
                    effect="plain"
                  >
                    {{ actionMeta(group.lastItem.action).label }}
                  </el-tag>
                  <span class="round-count">{{ group.items.length }} 条记录</span>
                </div>
                <el-button link size="small">
                  {{ isRoundExpanded(group.round) ? '收起' : '展开' }}
                </el-button>
              </div>

              <!-- 展开后显示该轮节点历史 -->
              <div v-if="isRoundExpanded(group.round)" class="round-detail">
                <el-table :data="group.items" border stripe size="small">
                  <el-table-column label="节点" min-width="140" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.nodeName }}</template>
                  </el-table-column>
                  <el-table-column label="审批人" min-width="110">
                    <template #default="{ row }">
                      {{ row.operatorName || '匿名用户' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="审批结果" width="110" align="center">
                    <template #default="{ row }">
                      <el-tag :type="actionMeta(row.action).tagType" size="small">
                        {{ actionMeta(row.action).label }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="审批意见" min-width="220" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.opinion || '-' }}</template>
                  </el-table-column>
                  <el-table-column label="审批时间" width="160" align="center">
                    <template #default="{ row }">{{ formatDateTime(row.operatedAt) }}</template>
                  </el-table-column>
                </el-table>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无审批历史" />
        </el-card>

        <!-- ============ 业务数据快照 ============ -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <span class="section-title">业务数据快照</span>
              <span class="meta-hint">
                当前轮次（第 {{ record?.round ?? 1 }} 轮）· 历史轮次快照未存储
              </span>
            </div>
          </template>
          <el-table v-if="snapshotFields.length > 0" :data="snapshotFields" border stripe>
            <el-table-column label="字段" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="field-name">{{ row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column label="值" min-width="280">
              <template #default="{ row }">
                <SensitiveFieldDisplay
                  :field-name="row.name"
                  :value="row.value"
                  :masked-fields="maskedFields"
                />
              </template>
            </el-table-column>
            <el-table-column label="变化" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.isHighlighted" type="warning" size="small" effect="dark">
                  已变化
                </el-tag>
                <el-tag v-else type="info" size="small" effect="plain">
                  未变化
                </el-tag>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无业务数据" />
            </template>
          </el-table>
          <el-empty v-else description="暂无业务数据" />
        </el-card>

        <!-- ============ ApprovalTimeline 组件视图（紧凑模式） ============ -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <span class="section-title">紧凑时间轴（ApprovalTimeline 组件）</span>
          </template>
          <ApprovalTimeline :history="history" />
        </el-card>
      </div>
    </template>
  </div>
</template>

<style scoped>
.approval-history-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.history-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.section-card {
  border-radius: var(--pms-radius-lg, 8px);
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.meta-hint {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}
.round-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 4px 0;
}
.round-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.current-badge {
  font-size: 12px;
  color: var(--el-color-primary, #409eff);
  font-weight: 600;
}
.round-submitter {
  font-size: 13px;
  color: var(--pms-color-text-secondary, #909399);
}
.round-count {
  font-size: 12px;
  color: var(--pms-color-text-placeholder, #c0c4cc);
}
.round-detail {
  margin-top: 12px;
  padding: 8px 12px;
  background: var(--el-fill-color-lighter, #fafafa);
  border-radius: 4px;
}
.field-name {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 13px;
}
</style>
