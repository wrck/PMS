<script setup lang="ts">
// =============================================================================
// ApprovalDetail - 审批详情页（Task 11 Step 2）
// -----------------------------------------------------------------------------
// 顶部 PageHeader：审批标题 + 状态标签 + 关键操作（通过 / 退回 / 撤回 / 重新提交）
// 主区（左侧 16 列）：
//   - 审批基本信息卡片：业务类型 / 来源项目 / 提交人 / 提交时间 / 当前节点 / 紧急程度
//   - 业务数据卡片（SensitiveFieldDisplay 集成）：
//       VISIBLE: 正常显示  MASKED: 脱敏值 + tooltip  HIDDEN: 不显示
//   - 审批意见输入区：el-input textarea + 通过 / 退回 按钮（带意见必填校验）
//   - 审批时间轴（ApprovalTimeline）：所有轮次历史，当前轮次高亮
// 右侧侧栏（8 列）：
//   - 流程图卡片：横向 el-steps，已通过/当前/未开始
//   - 字段权限说明卡片：当前用户对各类字段的权限
//   - 操作历史卡片：最近 10 条操作记录
// 加载中：SkeletonCard  空状态：EmptyState
// =============================================================================
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveApproval,
  getApprovalDetail,
  rejectApproval,
  resubmitApproval,
  withdrawApproval,
  type ApprovalDetailVO,
  type ApprovalHistory,
  type ApprovalRecord
} from '@/api/approval-center'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'
import SensitiveFieldDisplay from '@/components/SensitiveFieldDisplay.vue'
import { listProjects, type Project } from '@/api/project'
import { useUserStore } from '@/stores/user'
import type { EpTagType } from '@/types'

defineOptions({ name: 'ApprovalDetail' })

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const detail = ref<ApprovalDetailVO | null>(null)
const loading = ref(false)
const submitting = ref(false)
const projectOptions = ref<Project[]>([])

// 审批意见
const opinion = ref('')
const actionMode = ref<'approve' | 'reject' | 'resubmit' | ''>('')

const recordId = computed(() => Number(route.params.id))
const record = computed<ApprovalRecord | null>(() => detail.value?.record ?? null)
const history = computed<ApprovalHistory[]>(() => detail.value?.history ?? [])
const businessData = computed<Record<string, unknown>>(() => detail.value?.businessData ?? {})
const maskedFields = computed(() => detail.value?.maskedFields ?? [])

// ============ 状态判定 ============
const isSubmitter = computed(
  () => record.value?.submitterId != null && record.value?.submitterId === userStore.userInfo?.id
)
const canApprove = computed(
  () => record.value?.status === 'PENDING' && !isSubmitter.value
)
const canReject = computed(
  () => record.value?.status === 'PENDING' && !isSubmitter.value
)
const canWithdraw = computed(
  () => record.value?.status === 'PENDING' && isSubmitter.value
)
const canResubmit = computed(
  () =>
    (record.value?.status === 'REJECTED' || record.value?.status === 'WITHDRAWN') &&
    isSubmitter.value
)

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

function statusMeta(status?: string): { tagType: EpTagType; label: string } {
  switch (status) {
    case 'PENDING':
      return { tagType: 'warning', label: '待审批' }
    case 'APPROVED':
      return { tagType: 'success', label: '已通过' }
    case 'REJECTED':
      return { tagType: 'danger', label: '已退回' }
    case 'WITHDRAWN':
      return { tagType: 'info', label: '已撤回' }
    case 'TIMEOUT':
      return { tagType: 'danger', label: '已超时' }
    default:
      return { tagType: 'info', label: status ?? '-' }
  }
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

function urgencyMeta(rec: ApprovalRecord | null): {
  level: 'overdue' | 'urgent' | 'normal' | 'none'
  label: string
  tagType: EpTagType
} {
  if (!rec) return { level: 'none', label: '-', tagType: 'info' }
  if (rec.status === 'APPROVED' || rec.status === 'REJECTED' || rec.status === 'WITHDRAWN') {
    return { level: 'normal', label: '已完结', tagType: 'info' }
  }
  if (rec.escalated) {
    return { level: 'overdue', label: '已升级', tagType: 'danger' }
  }
  if (rec.timeoutAt) {
    const timeout = new Date(rec.timeoutAt).getTime()
    const now = Date.now()
    if (timeout < now) {
      return { level: 'overdue', label: '已超期', tagType: 'danger' }
    }
    const diff = timeout - now
    if (diff <= 48 * 60 * 60 * 1000) {
      return { level: 'urgent', label: '即将超期', tagType: 'warning' }
    }
  }
  return { level: 'normal', label: '正常', tagType: 'success' }
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

function projectNameOf(projectId?: number | null): string {
  if (!projectId) return '-'
  return projectOptions.value.find((p) => p.id === projectId)?.projectName ?? '-'
}

// ============ 业务数据字段（HIDDEN 已被后端过滤，前端兜底）============
interface FieldEntry {
  name: string
  value: unknown
}

const visibleFields = computed<FieldEntry[]>(() => {
  const entries: FieldEntry[] = []
  for (const [k, v] of Object.entries(businessData.value)) {
    const meta = maskedFields.value.find((m) => m.fieldName === k)
    if (meta?.permission === 'HIDDEN') continue
    entries.push({ name: k, value: v })
  }
  return entries
})

// ============ 字段权限统计 ============
const fieldPermStats = computed(() => {
  const stats = { visible: 0, masked: 0, hidden: 0 }
  for (const m of maskedFields.value) {
    if (m.permission === 'VISIBLE') stats.visible++
    else if (m.permission === 'MASKED') stats.masked++
    else if (m.permission === 'HIDDEN') stats.hidden++
  }
  return stats
})

const fieldPermList = computed(() => {
  return maskedFields.value.map((m) => ({
    fieldName: m.fieldName,
    permission: m.permission,
    maskPattern: m.maskPattern ?? '-'
  }))
})

function permMeta(perm?: string): { tagType: EpTagType; label: string } {
  switch (perm) {
    case 'VISIBLE':
      return { tagType: 'success', label: '可见' }
    case 'MASKED':
      return { tagType: 'warning', label: '脱敏' }
    case 'HIDDEN':
      return { tagType: 'info', label: '隐藏' }
    default:
      return { tagType: 'info', label: perm ?? '-' }
  }
}

// ============ 流程节点（从 history 推导）============
interface ProcessStep {
  name: string
  status: 'done' | 'active' | 'pending'
  operator?: string
  operatedAt?: string
  opinion?: string
  action?: string
}

const processSteps = computed<ProcessStep[]>(() => {
  const steps: ProcessStep[] = []
  const seen = new Set<string>()
  // 按 operatedAt 升序遍历 history
  const sorted = [...history.value].sort((a, b) => {
    const ta = a.operatedAt ? new Date(a.operatedAt).getTime() : 0
    const tb = b.operatedAt ? new Date(b.operatedAt).getTime() : 0
    return ta - tb
  })
  for (const h of sorted) {
    if (!seen.has(h.nodeName)) {
      seen.add(h.nodeName)
      steps.push({
        name: h.nodeName,
        status: 'done',
        operator: h.operatorName,
        operatedAt: h.operatedAt,
        opinion: h.opinion,
        action: h.action
      })
    }
  }
  // 当前节点（record.currentNodeName）若未在历史中，作为 active
  const current = record.value?.currentNodeName
  if (current && !seen.has(current)) {
    steps.push({ name: current, status: 'active' })
  }
  // 标记最后一个 done 为 active（如果整个流程还没完结且没有显式 active）
  if (record.value?.status === 'PENDING' && steps.length > 0 && !steps.some((s) => s.status === 'active')) {
    steps[steps.length - 1].status = 'active'
  }
  return steps
})

const currentRound = computed(() => record.value?.round ?? 1)

// ============ 操作历史时间线（最近 10 条）============
const recentHistory = computed<ApprovalHistory[]>(() => {
  return [...history.value]
    .sort((a, b) => {
      const ta = a.operatedAt ? new Date(a.operatedAt).getTime() : 0
      const tb = b.operatedAt ? new Date(b.operatedAt).getTime() : 0
      return tb - ta
    })
    .slice(0, 10)
})

// ============ 数据加载 ============
async function loadProjectOptions() {
  try {
    const res = await listProjects({ page: 1, size: 200 })
    projectOptions.value = res.records ?? []
  } catch {
    /* ignored */
  }
}

async function loadDetail() {
  if (!recordId.value) return
  loading.value = true
  try {
    detail.value = await getApprovalDetail(recordId.value)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

// ============ 操作（通过 / 退回 / 重新提交 / 撤回）============
function resetOpinion() {
  opinion.value = ''
  actionMode.value = ''
}

function setActionMode(mode: 'approve' | 'reject' | 'resubmit') {
  actionMode.value = mode
  opinion.value = ''
}

function validateOpinion(): boolean {
  if (actionMode.value === 'reject' && !opinion.value.trim()) {
    ElMessage.warning('退回原因必填')
    return false
  }
  if (actionMode.value === 'resubmit' && !opinion.value.trim()) {
    ElMessage.warning('重新提交说明必填')
    return false
  }
  return true
}

async function handleAction() {
  if (!record.value?.id || !actionMode.value) return
  if (!validateOpinion()) return
  submitting.value = true
  try {
    const id = record.value.id
    if (actionMode.value === 'approve') {
      await approveApproval(id, opinion.value || '同意')
      ElMessage.success('已通过')
    } else if (actionMode.value === 'reject') {
      await rejectApproval(id, opinion.value)
      ElMessage.success('已退回')
    } else if (actionMode.value === 'resubmit') {
      await resubmitApproval(id, opinion.value)
      ElMessage.success('已重新提交')
    }
    resetOpinion()
    await loadDetail()
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

function handleWithdraw() {
  if (!record.value?.id) return
  ElMessageBox.confirm(`确定撤回「${record.value.title}」吗？`, '撤回确认', { type: 'warning' })
    .then(async () => {
      submitting.value = true
      try {
        await withdrawApproval(record.value!.id!)
        ElMessage.success('已撤回')
        await loadDetail()
      } catch {
        /* handled by interceptor */
      } finally {
        submitting.value = false
      }
    })
    .catch(() => {
      /* cancelled */
    })
}

function backToList() {
  router.push('/workflow/approval-center')
}

function goHistory() {
  if (!record.value?.id) return
  router.push(`/workflow/approval-history/${record.value.id}`)
}

onMounted(() => {
  loadProjectOptions()
  loadDetail()
})
</script>

<template>
  <div class="approval-detail-page">
    <SkeletonCard v-if="loading" :loading="true" :rows="10">
      <div />
    </SkeletonCard>

    <EmptyState
      v-else-if="!record"
      title="未找到审批记录"
      description="该审批可能已被删除或 ID 无效"
      icon="Warning"
    >
      <template #action>
        <el-button type="primary" @click="backToList">返回审批中心</el-button>
      </template>
    </EmptyState>

    <template v-else>
      <!-- PageHeader -->
      <PageHeader
        :title="record.title"
        :description="`编号 #${record.id} · ${typeLabel(record.approvalType)} · 来源项目 ${projectNameOf(record.projectId)} · 第 ${currentRound} 轮`"
      >
        <template #actions>
          <el-tag :type="statusMeta(record.status).tagType" size="large" effect="dark">
            {{ statusMeta(record.status).label }}
          </el-tag>
          <el-button
            v-if="canApprove"
            type="success"
            :loading="submitting"
            @click="setActionMode('approve')"
          >通过</el-button>
          <el-button
            v-if="canReject"
            type="danger"
            :loading="submitting"
            @click="setActionMode('reject')"
          >退回</el-button>
          <el-button
            v-if="canWithdraw"
            type="warning"
            :loading="submitting"
            @click="handleWithdraw"
          >撤回</el-button>
          <el-button
            v-if="canResubmit"
            type="primary"
            :loading="submitting"
            @click="setActionMode('resubmit')"
          >重新提交</el-button>
          <el-button @click="goHistory">查看历史</el-button>
          <el-button @click="backToList">返回</el-button>
        </template>
      </PageHeader>

      <div class="detail-body">
        <!-- ============ 主区 ============ -->
        <div class="detail-main">
          <!-- 审批基本信息 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <span class="section-title">审批基本信息</span>
            </template>
            <el-descriptions :column="3" border>
              <el-descriptions-item label="业务类型">
                <el-tag size="small" effect="plain">{{ typeLabel(record.approvalType) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="来源项目">
                {{ projectNameOf(record.projectId) }}
              </el-descriptions-item>
              <el-descriptions-item label="业务编号">
                {{ record.businessCode || '未关联业务' }}
              </el-descriptions-item>
              <el-descriptions-item label="提交人">
                {{ record.submitterName || '匿名用户' }}
              </el-descriptions-item>
              <el-descriptions-item label="提交时间">
                {{ formatDateTime(record.submittedAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="当前节点">
                {{ record.currentNodeName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="紧急程度">
                <el-tag :type="urgencyMeta(record).tagType" size="small" effect="dark">
                  {{ urgencyMeta(record).label }}
                </el-tag>
                <span v-if="record.timeoutAt" class="meta-hint">
                  超时时间：{{ formatDateTime(record.timeoutAt) }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="完成时间">
                {{ formatDateTime(record.completedAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="是否升级">
                <el-tag :type="record.escalated ? 'danger' : 'info'" size="small">
                  {{ record.escalated ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 业务数据（含脱敏） -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <div class="section-header">
                <span class="section-title">业务数据</span>
                <span class="meta-hint">
                  可见 {{ fieldPermStats.visible }} / 脱敏 {{ fieldPermStats.masked }} / 隐藏 {{ fieldPermStats.hidden }}
                </span>
              </div>
            </template>
            <el-descriptions
              v-if="visibleFields.length > 0"
              :column="2"
              border
              size="default"
            >
              <el-descriptions-item
                v-for="field in visibleFields"
                :key="field.name"
                :label="field.name"
              >
                <SensitiveFieldDisplay
                  :field-name="field.name"
                  :value="field.value"
                  :masked-fields="maskedFields"
                />
              </el-descriptions-item>
            </el-descriptions>
            <el-empty v-else description="无业务数据" />
          </el-card>

          <!-- 审批意见输入区 -->
          <el-card v-if="canApprove || canReject || canResubmit" shadow="never" class="section-card">
            <template #header>
              <span class="section-title">审批意见</span>
            </template>
            <el-form label-position="top">
              <el-form-item label="意见 / 说明">
                <el-input
                  v-model="opinion"
                  type="textarea"
                  :rows="4"
                  :placeholder="
                    actionMode === 'reject'
                      ? '请输入退回原因（必填）'
                      : actionMode === 'resubmit'
                        ? '请输入重新提交说明（必填）'
                        : '请输入审批意见（可通过时留空默认同意）'
                  "
                />
              </el-form-item>
            </el-form>
            <div class="opinion-actions">
              <el-button
                v-if="canApprove"
                type="success"
                :loading="submitting"
                :disabled="actionMode === 'reject' || actionMode === 'resubmit'"
                @click="actionMode === 'approve' ? handleAction() : setActionMode('approve')"
              >
                {{ actionMode === 'approve' ? '确认通过' : '切换到通过' }}
              </el-button>
              <el-button
                v-if="canReject"
                type="danger"
                :loading="submitting"
                :disabled="actionMode === 'approve' || actionMode === 'resubmit'"
                @click="actionMode === 'reject' ? handleAction() : setActionMode('reject')"
              >
                {{ actionMode === 'reject' ? '确认退回' : '切换到退回' }}
              </el-button>
              <el-button
                v-if="canResubmit"
                type="primary"
                :loading="submitting"
                :disabled="actionMode === 'approve' || actionMode === 'reject'"
                @click="actionMode === 'resubmit' ? handleAction() : setActionMode('resubmit')"
              >
                {{ actionMode === 'resubmit' ? '确认重新提交' : '切换到重新提交' }}
              </el-button>
              <el-button v-if="actionMode" @click="resetOpinion">取消</el-button>
              <span v-if="actionMode === 'reject' || actionMode === 'resubmit'" class="meta-hint">
                * 当前操作的意见为必填
              </span>
            </div>
          </el-card>

          <!-- 审批时间轴 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <span class="section-title">审批历史时间轴（共 {{ history.length }} 条 · 当前第 {{ currentRound }} 轮）</span>
            </template>
            <ApprovalTimeline :history="history" />
          </el-card>
        </div>

        <!-- ============ 右侧侧栏 ============ -->
        <div class="detail-aside">
          <!-- 流程图 -->
          <el-card shadow="never" class="aside-card">
            <template #header><span class="section-title">审批流程</span></template>
            <div v-if="processSteps.length > 0" class="process-flow">
              <el-steps :active="processSteps.findIndex((s) => s.status === 'active')" align-center>
                <el-step
                  v-for="(step, idx) in processSteps"
                  :key="`${step.name}-${idx}`"
                  :title="step.name"
                  :status="
                    step.status === 'done'
                      ? 'success'
                      : step.status === 'active'
                        ? 'process'
                        : 'wait'
                  "
                >
                  <template #description>
                    <div v-if="step.operator" class="step-desc">
                      <span>{{ step.operator }}</span>
                    </div>
                    <div v-if="step.operatedAt" class="step-desc">
                      <span class="meta-hint">{{ formatDateTime(step.operatedAt) }}</span>
                    </div>
                  </template>
                </el-step>
              </el-steps>
            </div>
            <el-empty v-else description="暂无流程节点信息" :image-size="60" />
          </el-card>

          <!-- 字段权限说明 -->
          <el-card shadow="never" class="aside-card">
            <template #header>
              <div class="section-header">
                <span class="section-title">字段权限说明</span>
                <span class="meta-hint">共 {{ maskedFields.length }} 个字段</span>
              </div>
            </template>
            <div v-if="fieldPermList.length > 0" class="perm-list">
              <div v-for="perm in fieldPermList" :key="perm.fieldName" class="perm-row">
                <span class="perm-name" :title="perm.fieldName">{{ perm.fieldName }}</span>
                <el-tag :type="permMeta(perm.permission).tagType" size="small">
                  {{ permMeta(perm.permission).label }}
                </el-tag>
                <span v-if="perm.permission === 'MASKED'" class="meta-hint perm-pattern">
                  {{ perm.maskPattern }}
                </span>
              </div>
            </div>
            <el-empty v-else description="无字段权限配置" :image-size="60" />
          </el-card>

          <!-- 操作历史 -->
          <el-card shadow="never" class="aside-card">
            <template #header>
              <span class="section-title">操作历史（最近 10 条）</span>
            </template>
            <el-timeline v-if="recentHistory.length > 0">
              <el-timeline-item
                v-for="(h, idx) in recentHistory"
                :key="h.id ?? idx"
                :timestamp="formatDateTime(h.operatedAt)"
                placement="top"
                :type="
                  h.action === 'APPROVE'
                    ? 'success'
                    : h.action === 'REJECT' || h.action === 'TIMEOUT' || h.action === 'ESCALATE'
                      ? 'danger'
                      : h.action === 'RESUBMIT'
                        ? 'primary'
                        : h.action === 'WITHDRAW'
                          ? 'info'
                          : 'primary'
                "
              >
                <div class="timeline-head">
                  <el-tag :type="actionMeta(h.action).tagType" size="small" effect="plain">
                    {{ actionMeta(h.action).label }}
                  </el-tag>
                  <span class="timeline-node">{{ h.nodeName }}</span>
                  <span v-if="h.round" class="meta-hint">第 {{ h.round }} 轮</span>
                </div>
                <div v-if="h.operatorName" class="timeline-operator">
                  操作人：{{ h.operatorName }}
                </div>
                <div v-if="h.opinion" class="timeline-opinion">{{ h.opinion }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无操作历史" :image-size="60" />
          </el-card>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.approval-detail-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.detail-body {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 12px;
  align-items: start;
}
.detail-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}
.detail-aside {
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: sticky;
  top: 12px;
}
.section-card,
.aside-card {
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
  margin-left: 8px;
}
.opinion-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}
.process-flow {
  padding: 8px 0;
}
.step-desc {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  line-height: 1.6;
}
.perm-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 280px;
  overflow-y: auto;
}
.perm-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  padding: 4px 6px;
  border-radius: 4px;
}
.perm-row:hover {
  background: var(--el-fill-color-lighter, #fafafa);
}
.perm-name {
  flex: 1;
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.perm-pattern {
  font-size: 11px;
  font-style: italic;
}
.timeline-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.timeline-node {
  font-weight: 600;
  font-size: 13px;
}
.timeline-operator {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  margin-top: 4px;
}
.timeline-opinion {
  margin-top: 4px;
  font-size: 13px;
  background: var(--el-fill-color-lighter, #fafafa);
  padding: 6px 10px;
  border-radius: 4px;
  color: var(--pms-color-text-regular, #606266);
}
</style>
