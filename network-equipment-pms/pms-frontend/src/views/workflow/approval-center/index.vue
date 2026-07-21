<script setup lang="ts">
// =============================================================================
// ApprovalCenter - 统一审批中心（Task 11 Step 1）
// -----------------------------------------------------------------------------
// 顶部 PageHeader：标题"统一审批中心" + 操作（字段权限配置按钮）
// 3 Tab：待审批 / 我发起的 / 全部
//   - 待审批 Tab：卡片列表（按提交时间倒序）
//       卡片：审批标题 + 业务类型标签 + 来源项目 + 提交人 + 提交时间 + 紧急程度（超期标红）
//       操作：通过 / 退回 / 详情
//   - 我发起的 Tab：卡片列表（按提交时间倒序）
//       卡片：审批标题 + 当前节点 + 当前审批人 + 状态 + 提交时间
//       操作：详情 / 撤回（仅 PENDING）/ 重新提交（仅 REJECTED/WITHDRAWN）
//   - 全部 Tab：el-table 列：编号/标题/业务类型/来源项目/提交人/当前节点/状态/提交时间/完成时间/操作
// 筛选：业务类型 / 状态 / 来源项目 / 关键字 / 时间范围
// 分页：10/20/50
// 加载中：SkeletonCard  空状态：EmptyState
// =============================================================================
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveApproval,
  getApprovalsByProject,
  getPendingApprovals,
  getSubmittedApprovals,
  listApprovals,
  rejectApproval,
  resubmitApproval,
  withdrawApproval,
  type ApprovalRecord,
  type ApprovalStatus,
  type ApprovalType
} from '@/api/approval-center'
import { useProjectContext } from '@/composables/useProjectContext'
import { listProjects, type Project } from '@/api/project'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import type { EpTagType } from '@/types'

defineOptions({ name: 'ApprovalCenter' })

interface Props {
  /** 嵌入项目工作区时传入的项目 ID；独立页面不传 */
  projectId?: number | string
}
const props = defineProps<Props>()
const router = useRouter()
const { currentProject } = useProjectContext()

// ============ Tab 状态 ============
type TabName = 'pending' | 'submitted' | 'all'
const activeTab = ref<TabName>('pending')
const loading = ref(false)
const acting = ref(false)

const pendingData = ref<ApprovalRecord[]>([])
const submittedData = ref<ApprovalRecord[]>([])
const allData = ref<ApprovalRecord[]>([])
const projectOptions = ref<Project[]>([])

// ============ 业务类型 / 状态选项 ============
const typeOptions: { value: ApprovalType | string; label: string }[] = [
  { value: 'PROJECT', label: '项目' },
  { value: 'TASK', label: '任务' },
  { value: 'DELIVERABLE', label: '交付件' },
  { value: 'RISK', label: '风险' },
  { value: 'ISSUE', label: '问题' },
  { value: 'CHANGE', label: '变更' },
  { value: 'RESOURCE', label: '资源' },
  { value: 'COST', label: '成本' },
  { value: 'PHASE_EXIT', label: '阶段退出' },
  { value: 'BASELINE_CHANGE', label: '基线变更' }
]

const statusOptions: { value: ApprovalStatus | string; label: string; tagType: EpTagType }[] = [
  { value: 'PENDING', label: '待审批', tagType: 'warning' },
  { value: 'APPROVED', label: '已通过', tagType: 'success' },
  { value: 'REJECTED', label: '已退回', tagType: 'danger' },
  { value: 'WITHDRAWN', label: '已撤回', tagType: 'info' },
  { value: 'TIMEOUT', label: '已超时', tagType: 'danger' }
]

const targetProjectId = computed<number | undefined>(() => {
  if (props.projectId !== undefined && props.projectId !== null && props.projectId !== '') {
    const n = Number(props.projectId)
    return Number.isNaN(n) ? undefined : n
  }
  return currentProject.value?.id ?? undefined
})

// ============ 筛选 ============
const filters = reactive<{
  approvalType: string
  status: string
  projectId: number | undefined
  keyword: string
  dateRange: [string, string] | null
}>({
  approvalType: '',
  status: '',
  projectId: undefined,
  keyword: '',
  dateRange: null
})

// ============ 分页 ============
const pagination = reactive<{ page: number; size: number }>({ page: 1, size: 10 })
const pageSizeOptions = [10, 20, 50]

// ============ 标签 / 文本辅助 ============
function statusMeta(status?: string): { tagType: EpTagType; label: string } {
  return (
    statusOptions.find((s) => s.value === status) ?? {
      tagType: 'info' as EpTagType,
      label: status ?? '-'
    }
  )
}

function typeLabel(type?: string): string {
  return typeOptions.find((t) => t.value === type)?.label ?? type ?? '-'
}

function projectNameOf(projectId?: number | null): string {
  if (!projectId) return '-'
  return projectOptions.value.find((p) => p.id === projectId)?.projectName ?? '-'
}

function projectLabel(rec: ApprovalRecord): string {
  return projectNameOf(rec.projectId)
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

/**
 * 紧急程度：基于 submittedAt + escalated + timeoutAt 计算。
 * - 已超期（timeoutAt 已过 / escalated=true）→ 'overdue' 标红
 * - 即将超期（48h 内）→ 'urgent'
 * - 普通 → 'normal'
 */
function urgencyMeta(rec: ApprovalRecord): {
  level: 'overdue' | 'urgent' | 'normal'
  label: string
  tagType: EpTagType
} {
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

// ============ 排序 + 过滤 ============
function sortBySubmittedDesc(a: ApprovalRecord, b: ApprovalRecord): number {
  const ta = a.submittedAt ? new Date(a.submittedAt).getTime() : 0
  const tb = b.submittedAt ? new Date(b.submittedAt).getTime() : 0
  return tb - ta
}

function matchKeyword(rec: ApprovalRecord, kw: string): boolean {
  if (!kw) return true
  const k = kw.toLowerCase()
  return (
    (rec.title ?? '').toLowerCase().includes(k) ||
    (rec.businessCode ?? '').toLowerCase().includes(k) ||
    (rec.submitterName ?? '').toLowerCase().includes(k) ||
    (rec.currentNodeName ?? '').toLowerCase().includes(k) ||
    String(rec.id ?? '').includes(k)
  )
}

function matchDateRange(rec: ApprovalRecord, range: [string, string] | null): boolean {
  if (!range || !range[0] || !range[1]) return true
  if (!rec.submittedAt) return false
  const t = rec.submittedAt.slice(0, 10)
  return t >= range[0].slice(0, 10) && t <= range[1].slice(0, 10)
}

const filteredPending = computed(() => {
  let list = [...pendingData.value]
  if (filters.approvalType) list = list.filter((r) => r.approvalType === filters.approvalType)
  if (filters.keyword) list = list.filter((r) => matchKeyword(r, filters.keyword))
  if (filters.dateRange) list = list.filter((r) => matchDateRange(r, filters.dateRange))
  return list.sort(sortBySubmittedDesc)
})

const filteredSubmitted = computed(() => {
  let list = [...submittedData.value]
  if (filters.approvalType) list = list.filter((r) => r.approvalType === filters.approvalType)
  if (filters.status) list = list.filter((r) => r.status === filters.status)
  if (filters.keyword) list = list.filter((r) => matchKeyword(r, filters.keyword))
  if (filters.dateRange) list = list.filter((r) => matchDateRange(r, filters.dateRange))
  return list.sort(sortBySubmittedDesc)
})

const filteredAll = computed(() => {
  let list = [...allData.value]
  if (filters.approvalType) list = list.filter((r) => r.approvalType === filters.approvalType)
  if (filters.status) list = list.filter((r) => r.status === filters.status)
  if (filters.projectId) list = list.filter((r) => r.projectId === filters.projectId)
  if (filters.keyword) list = list.filter((r) => matchKeyword(r, filters.keyword))
  if (filters.dateRange) list = list.filter((r) => matchDateRange(r, filters.dateRange))
  return list.sort(sortBySubmittedDesc)
})

const pagedAll = computed(() => {
  const start = (pagination.page - 1) * pagination.size
  return filteredAll.value.slice(start, start + pagination.size)
})

const allTotal = computed(() => filteredAll.value.length)

const subtitle = computed(() => {
  if (targetProjectId.value) {
    return `当前项目：${projectNameOf(targetProjectId.value)} · 待审批 ${filteredPending.value.length} 条 · 我发起 ${filteredSubmitted.value.length} 条 · 全部 ${allTotal.value} 条`
  }
  return `待审批 ${filteredPending.value.length} 条 · 我发起 ${filteredSubmitted.value.length} 条 · 全部 ${allTotal.value} 条`
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

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'pending') {
      const list = targetProjectId.value
        ? await getApprovalsByProject(targetProjectId.value)
        : await getPendingApprovals()
      pendingData.value = (list ?? []).filter((r) => r.status === 'PENDING')
    } else if (activeTab.value === 'submitted') {
      const list = await getSubmittedApprovals()
      submittedData.value = list ?? []
    } else {
      const params: { projectId?: number } = {}
      if (targetProjectId.value) params.projectId = targetProjectId.value
      const list = await listApprovals(params)
      allData.value = list ?? []
    }
  } catch {
    if (activeTab.value === 'pending') pendingData.value = []
    else if (activeTab.value === 'submitted') submittedData.value = []
    else allData.value = []
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  pagination.page = 1
  loadData()
}

function handleFilter() {
  pagination.page = 1
}

function handleResetFilters() {
  filters.approvalType = ''
  filters.status = ''
  filters.projectId = undefined
  filters.keyword = ''
  filters.dateRange = null
  pagination.page = 1
}

function handlePageChange(p: number) {
  pagination.page = p
}

function handleSizeChange(s: number) {
  pagination.size = s
  pagination.page = 1
}

// ============ 操作（通过 / 退回 / 撤回 / 重新提交）============
async function quickApprove(rec: ApprovalRecord, e: Event) {
  e.stopPropagation()
  if (!rec.id) return
  let comment = ''
  try {
    const res = await ElMessageBox.prompt('请输入审批意见（可通过）', '通过审批', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '可填写审批意见，留空则默认"同意"',
      inputValue: '同意'
    })
    comment = res?.value ?? '同意'
  } catch {
    return
  }
  acting.value = true
  try {
    await approveApproval(rec.id, comment)
    ElMessage.success('已通过')
    await loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

async function quickReject(rec: ApprovalRecord, e: Event) {
  e.stopPropagation()
  if (!rec.id) return
  let comment = ''
  try {
    const res = await ElMessageBox.prompt('请输入退回原因（必填）', '退回审批', {
      confirmButtonText: '退回',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请填写退回原因',
      inputValidator: (v: string) => (v && v.trim().length > 0) || '退回原因必填'
    })
    comment = res?.value ?? ''
  } catch {
    return
  }
  acting.value = true
  try {
    await rejectApproval(rec.id, comment)
    ElMessage.success('已退回')
    await loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

async function quickWithdraw(rec: ApprovalRecord, e: Event) {
  e.stopPropagation()
  if (!rec.id) return
  try {
    await ElMessageBox.confirm(`确定撤回「${rec.title}」吗？`, '撤回确认', { type: 'warning' })
  } catch {
    return
  }
  acting.value = true
  try {
    await withdrawApproval(rec.id)
    ElMessage.success('已撤回')
    await loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

async function quickResubmit(rec: ApprovalRecord, e: Event) {
  e.stopPropagation()
  if (!rec.id) return
  let comment = ''
  try {
    const res = await ElMessageBox.prompt('请输入重新提交说明', '重新提交', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '说明本次修改要点'
    })
    comment = res?.value ?? ''
  } catch {
    return
  }
  acting.value = true
  try {
    await resubmitApproval(rec.id, comment)
    ElMessage.success('已重新提交')
    await loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    acting.value = false
  }
}

// ============ 路由跳转 ============
function goDetail(rec: ApprovalRecord) {
  if (!rec.id) return
  router.push(`/workflow/approval-detail/${rec.id}`)
}

function goHistory(rec: ApprovalRecord) {
  if (!rec.id) return
  router.push(`/workflow/approval-history/${rec.id}`)
}

function goFieldPerm() {
  router.push('/workflow/field-perm')
}

// 切换项目上下文时重新加载
watch(targetProjectId, () => {
  if (activeTab.value !== 'all') loadData()
})

onMounted(() => {
  loadProjectOptions()
  loadData()
})
</script>

<template>
  <div class="approval-center-page">
    <PageHeader title="统一审批中心" :description="subtitle">
      <template #actions>
        <el-button @click="goFieldPerm">字段权限配置</el-button>
      </template>
    </PageHeader>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索标题 / 编号 / 提交人"
        clearable
        class="filter-search"
        @input="handleFilter"
      />
      <el-select
        v-model="filters.approvalType"
        placeholder="业务类型"
        clearable
        class="filter-select"
        @change="handleFilter"
      >
        <el-option
          v-for="opt in typeOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-select
        v-if="activeTab !== 'pending'"
        v-model="filters.status"
        placeholder="状态"
        clearable
        class="filter-select"
        @change="handleFilter"
      >
        <el-option
          v-for="opt in statusOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-input-number
        v-if="activeTab === 'all'"
        v-model="filters.projectId"
        :min="1"
        :controls="false"
        placeholder="来源项目 ID"
        class="filter-project"
        @change="handleFilter"
      />
      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        class="filter-date"
        @change="handleFilter"
      />
      <el-button @click="handleResetFilters">重置</el-button>
      <el-button type="primary" @click="loadData">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- ============ 待审批 Tab ============ -->
      <el-tab-pane :label="`待审批 (${filteredPending.length})`" name="pending">
        <div v-if="loading" class="card-list">
          <SkeletonCard v-for="n in 4" :key="`sk-pending-${n}`" :loading="true" :rows="4" />
        </div>
        <EmptyState
          v-else-if="filteredPending.length === 0"
          title="暂无待审批事项"
          description="目前没有等待您审批的事项"
          icon="BellFilled"
        />
        <div v-else class="card-list">
          <el-card
            v-for="rec in filteredPending"
            :key="rec.id"
            shadow="hover"
            class="approval-card"
            @click="goDetail(rec)"
          >
            <div class="card-head">
              <span class="card-title" :title="rec.title">{{ rec.title }}</span>
              <div class="card-tags">
                <el-tag size="small" type="primary" effect="plain">
                  {{ typeLabel(rec.approvalType) }}
                </el-tag>
                <el-tag
                  size="small"
                  :type="urgencyMeta(rec).tagType"
                  effect="dark"
                >
                  {{ urgencyMeta(rec).label }}
                </el-tag>
              </div>
            </div>
            <div class="card-meta">
              <span class="meta-item">
                <span class="meta-label">来源项目：</span>
                <span class="meta-value">{{ projectLabel(rec) }}</span>
              </span>
              <span class="meta-item">
                <span class="meta-label">提交人：</span>
                <span class="meta-value">{{ rec.submitterName || '未命名' }}</span>
              </span>
              <span class="meta-item">
                <span class="meta-label">当前节点：</span>
                <span class="meta-value">{{ rec.currentNodeName || '-' }}</span>
              </span>
              <span class="meta-item">
                <span class="meta-label">提交时间：</span>
                <span class="meta-value">{{ formatDateTime(rec.submittedAt) }}</span>
              </span>
              <span v-if="rec.round && rec.round > 1" class="meta-item">
                <span class="meta-label">轮次：</span>
                <span class="meta-value">第 {{ rec.round }} 轮</span>
              </span>
            </div>
            <div class="card-actions" @click.stop>
              <el-button
                type="success"
                size="small"
                :loading="acting"
                @click="quickApprove(rec, $event)"
              >通过</el-button>
              <el-button
                type="danger"
                size="small"
                :loading="acting"
                @click="quickReject(rec, $event)"
              >退回</el-button>
              <el-button size="small" @click="goDetail(rec)">详情</el-button>
              <el-button link size="small" @click="goHistory(rec)">历史</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- ============ 我发起的 Tab ============ -->
      <el-tab-pane :label="`我发起的 (${filteredSubmitted.length})`" name="submitted">
        <div v-if="loading" class="card-list">
          <SkeletonCard v-for="n in 4" :key="`sk-submitted-${n}`" :loading="true" :rows="4" />
        </div>
        <EmptyState
          v-else-if="filteredSubmitted.length === 0"
          title="您还没有发起过审批"
          description="在业务模块提交审批申请后，将在此处追踪进度"
          icon="Document"
        />
        <div v-else class="card-list">
          <el-card
            v-for="rec in filteredSubmitted"
            :key="rec.id"
            shadow="hover"
            class="approval-card"
            @click="goDetail(rec)"
          >
            <div class="card-head">
              <span class="card-title" :title="rec.title">{{ rec.title }}</span>
              <div class="card-tags">
                <el-tag size="small" type="primary" effect="plain">
                  {{ typeLabel(rec.approvalType) }}
                </el-tag>
                <el-tag :type="statusMeta(rec.status).tagType" size="small" effect="dark">
                  {{ statusMeta(rec.status).label }}
                </el-tag>
              </div>
            </div>
            <div class="card-meta">
              <span class="meta-item">
                <span class="meta-label">当前节点：</span>
                <span class="meta-value">{{ rec.currentNodeName || '-' }}</span>
              </span>
              <span class="meta-item">
                <span class="meta-label">当前审批人：</span>
                <span class="meta-value">{{ rec.currentNodeName || '-' }}</span>
              </span>
              <span class="meta-item">
                <span class="meta-label">提交时间：</span>
                <span class="meta-value">{{ formatDateTime(rec.submittedAt) }}</span>
              </span>
              <span v-if="rec.round && rec.round > 1" class="meta-item">
                <span class="meta-label">轮次：</span>
                <span class="meta-value">第 {{ rec.round }} 轮</span>
              </span>
            </div>
            <div class="card-actions" @click.stop>
              <el-button size="small" @click="goDetail(rec)">详情</el-button>
              <el-button
                v-if="rec.status === 'PENDING'"
                type="warning"
                size="small"
                :loading="acting"
                @click="quickWithdraw(rec, $event)"
              >撤回</el-button>
              <el-button
                v-if="rec.status === 'REJECTED' || rec.status === 'WITHDRAWN'"
                type="primary"
                size="small"
                :loading="acting"
                @click="quickResubmit(rec, $event)"
              >重新提交</el-button>
              <el-button link size="small" @click="goHistory(rec)">历史</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- ============ 全部 Tab ============ -->
      <el-tab-pane :label="`全部 (${allTotal})`" name="all">
        <div v-if="loading" class="card-list">
          <SkeletonCard v-for="n in 4" :key="`sk-all-${n}`" :loading="true" :rows="6" />
        </div>
        <EmptyState
          v-else-if="filteredAll.length === 0"
          title="暂无审批记录"
          description="请调整筛选条件或新建审批"
          icon="Files"
        />
        <template v-else>
          <el-table :data="pagedAll" border stripe>
            <el-table-column label="编号" width="80" align="center">
              <template #default="{ row }">#{{ row.id }}</template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
            <el-table-column label="业务类型" width="110" align="center">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ typeLabel(row.approvalType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="来源项目" width="110" align="center">
              <template #default="{ row }">{{ projectLabel(row) }}</template>
            </el-table-column>
            <el-table-column label="提交人" min-width="110">
              <template #default="{ row }">
                {{ row.submitterName || '未命名' }}
              </template>
            </el-table-column>
            <el-table-column label="当前节点" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">{{ row.currentNodeName || '-' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusMeta(row.status).tagType" size="small">
                  {{ statusMeta(row.status).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提交时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.submittedAt) }}</template>
            </el-table-column>
            <el-table-column label="完成时间" width="160" align="center">
              <template #default="{ row }">{{ formatDateTime(row.completedAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="goDetail(row)">详情</el-button>
                <el-button link type="info" @click="goHistory(row)">历史</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-bar">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :total="allTotal"
              :page-sizes="pageSizeOptions"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.approval-center-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.filter-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
  padding: 12px;
  background: var(--pms-color-bg-card, #fff);
  border-radius: var(--pms-radius-lg, 8px);
  border: 1px solid var(--pms-color-border-light, #ebeef5);
}
.filter-search {
  width: 240px;
}
.filter-select {
  width: 150px;
}
.filter-project {
  width: 160px;
}
.filter-date {
  width: 280px;
}
.card-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.approval-card {
  cursor: pointer;
  transition: transform var(--pms-transition-fast, 0.15s ease), box-shadow 0.15s ease;
  border-radius: var(--pms-radius-lg, 8px);
}
.approval-card:hover {
  transform: translateY(-2px);
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.card-tags {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}
.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  margin-bottom: 8px;
}
.meta-item {
  display: inline-flex;
  align-items: center;
}
.meta-label {
  color: var(--pms-color-text-placeholder, #c0c4cc);
}
.meta-value {
  color: var(--pms-color-text-regular, #606266);
  font-weight: 500;
}
.card-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
