<script setup lang="ts">
// =============================================================================
// GlobalTodoCenter - 全局待办聚合组件
// -----------------------------------------------------------------------------
// 4 类待办 Tab：
//   1. 审批待办      — getPendingApprovals / getApprovalsByProject (status=PENDING)
//   2. 任务待办      — listTasks / getTasksByProject (status ∈ PENDING/ACCEPTED/IN_PROGRESS/BLOCKED/REVIEW)
//   3. 阶段推进待办  — listProjects (status=IN_PROGRESS, manager=当前用户)
//   4. 基线偏差告警  — listBaselines + diffBaseline (needsApproval=true)
//
// Props:
//   - projectId?: number  传值时进入「项目维度」模式（仅显示该项目下的待办）
//                        未传值时进入「全局」模式（显示当前用户在所有项目下的待办）
//
// 特性：
//   - 顶部统计栏：4 类待办总数 + 紧急（已超期）数量
//   - 筛选：按项目 + 按紧急程度
//   - 每类 Tab 独立分页（page-size 10）
//   - 60s 自动刷新（el-switch 控制开关）
//   - 加载中：SkeletonCard
//   - 空状态：每个 Tab 独立的 EmptyState
// =============================================================================
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar,
  Checked,
  Clock,
  Folder,
  Histogram,
  Promotion,
  Refresh,
  Tickets,
  WarningFilled
} from '@element-plus/icons-vue'
import { ElBadge, ElButton, ElCard, ElIcon, ElMessage, ElPagination, ElSelect, ElOption, ElSwitch, ElTabPane, ElTabs, ElTag } from 'element-plus'
import { getPendingApprovals, getApprovalsByProject, type ApprovalRecord } from '@/api/approval-center'
import { getTasksByProject, listTasks, type ImplTask, type ImplTaskNode } from '@/api/implementation'
import { listProjects, type Project } from '@/api/project'
import { diffBaseline, listBaselines, type BaselineDiffResult, type BaselineSnapshot } from '@/api/baseline'
import { useUserStore } from '@/stores/user'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

type TabKey = 'approval' | 'task' | 'phase' | 'baseline'
type UrgencyFilter = 'all' | 'urgent' | 'normal'

const props = defineProps<{ projectId?: number }>()
const router = useRouter()
const userStore = useUserStore()

// --- 当前用户信息（用于过滤"分配给我"的任务 / "我负责"的项目） ---
const currentUserId = computed(() => userStore.userInfo?.id)
const currentUserNickname = computed(() => userStore.userInfo?.nickname ?? '')

// --- 顶部统计 + 自动刷新控制 ---
const activeTab = ref<TabKey>('approval')
const autoRefresh = ref(true)
let refreshTimer: number | null = null

// --- 筛选 ---
// 在项目维度（props.projectId 有值）时隐藏项目筛选；全局模式下可切换。
const filterProjectId = ref<number | undefined>(props.projectId)
const filterUrgency = ref<UrgencyFilter>('all')

watch(() => props.projectId, (val) => {
  filterProjectId.value = val
  resetPagination()
  loadAll()
})

// --- 项目下拉选项（仅全局模式加载一次，用于卡片显示项目名） ---
const projectOptions = ref<Project[]>([])

// --- 4 类待办数据 + loading ---
const approvalTodos = ref<ApprovalRecord[]>([])
const taskTodos = ref<ImplTask[]>([])
const phaseTodos = ref<Project[]>([])
interface BaselineTodoItem {
  baseline: BaselineSnapshot
  diff: BaselineDiffResult
}
const baselineTodos = ref<BaselineTodoItem[]>([])

const loading = reactive({
  approval: false,
  task: false,
  phase: false,
  baseline: false
})

// --- 每类 Tab 独立分页 ---
const pagination = reactive({
  approval: { page: 1, size: 10 },
  task: { page: 1, size: 10 },
  phase: { page: 1, size: 10 },
  baseline: { page: 1, size: 10 }
})

function resetPagination() {
  pagination.approval.page = 1
  pagination.task.page = 1
  pagination.phase.page = 1
  pagination.baseline.page = 1
}

// ============================================================================
// 紧急程度（已超期）判定
// ============================================================================
function isOverdue(dateStr?: string): boolean {
  if (!dateStr) return false
  const t = new Date(dateStr).getTime()
  if (Number.isNaN(t)) return false
  return t < Date.now()
}

function approvalOverdue(todo: ApprovalRecord): boolean {
  return isOverdue(todo.timeoutAt)
}
function taskOverdue(todo: ImplTask): boolean {
  return isOverdue(todo.planEndDate)
}
function phaseOverdue(todo: Project): boolean {
  return isOverdue(todo.planEndDate)
}
// 基线告警本身即视为"紧急"
function baselineOverdue(_todo: BaselineTodoItem): boolean {
  return true
}

// ============================================================================
// 筛选后的待办列表（按紧急程度过滤）
// ============================================================================
const filteredApprovalTodos = computed(() => {
  if (filterUrgency.value === 'all') return approvalTodos.value
  return approvalTodos.value.filter((t) =>
    filterUrgency.value === 'urgent' ? approvalOverdue(t) : !approvalOverdue(t)
  )
})
const filteredTaskTodos = computed(() => {
  if (filterUrgency.value === 'all') return taskTodos.value
  return taskTodos.value.filter((t) =>
    filterUrgency.value === 'urgent' ? taskOverdue(t) : !taskOverdue(t)
  )
})
const filteredPhaseTodos = computed(() => {
  if (filterUrgency.value === 'all') return phaseTodos.value
  return phaseTodos.value.filter((t) =>
    filterUrgency.value === 'urgent' ? phaseOverdue(t) : !phaseOverdue(t)
  )
})
const filteredBaselineTodos = computed(() => baselineTodos.value) // 基线告警始终视为紧急

// --- 分页后的当前页数据 ---
const pagedApprovalTodos = computed(() => paginate(filteredApprovalTodos.value, pagination.approval))
const pagedTaskTodos = computed(() => paginate(filteredTaskTodos.value, pagination.task))
const pagedPhaseTodos = computed(() => paginate(filteredPhaseTodos.value, pagination.phase))
const pagedBaselineTodos = computed(() => paginate(filteredBaselineTodos.value, pagination.baseline))

function paginate<T>(list: T[], page: { page: number; size: number }): T[] {
  const start = (page.page - 1) * page.size
  return list.slice(start, start + page.size)
}

// ============================================================================
// 统计栏：4 类总数 + 紧急数量
// ============================================================================
const totalCount = computed(() =>
  approvalTodos.value.length +
  taskTodos.value.length +
  phaseTodos.value.length +
  baselineTodos.value.length
)
const urgentCount = computed(() => {
  const a = approvalTodos.value.filter(approvalOverdue).length
  const t = taskTodos.value.filter(taskOverdue).length
  const p = phaseTodos.value.filter(phaseOverdue).length
  const b = baselineTodos.value.length
  return a + t + p + b
})

// ============================================================================
// 数据加载
// ============================================================================
async function loadProjectOptions() {
  // 仅在全局模式且未传 projectId 时加载；项目模式下无需下拉
  if (props.projectId) return
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records ?? []
  } catch {
    projectOptions.value = []
  }
}

async function loadApprovalTodos() {
  loading.approval = true
  try {
    let list: ApprovalRecord[] = []
    const pid = filterProjectId.value
    if (pid) {
      // 项目维度：拉取项目下所有审批，前端按 PENDING 过滤
      list = await getApprovalsByProject(pid)
      list = list.filter((r) => !r.status || r.status === 'PENDING')
    } else {
      // 全局：拉取当前用户的待办审批
      list = await getPendingApprovals()
    }
    approvalTodos.value = list
  } catch {
    approvalTodos.value = []
  } finally {
    loading.approval = false
  }
}

async function loadTaskTodos() {
  loading.task = true
  try {
    const pid = filterProjectId.value
    const activeStatuses = ['PENDING', 'ACCEPTED', 'IN_PROGRESS', 'BLOCKED', 'REVIEW']
    let list: ImplTask[] = []
    if (pid) {
      // 项目维度：拉取项目下全部任务，前端按 engineer + 状态过滤
      const tasks = await getTasksByProject(pid)
      list = tasks.filter(
        (t) =>
          (!t.engineerId || t.engineerId === currentUserId.value) &&
          (!t.status || activeStatuses.includes(t.status))
      )
    } else {
      // 全局：调用分页 listTasks，按状态过滤；engineer 过滤在前端做
      // listTasks 默认按 planEndDate 倒序，拉取前 200 条作为待办池
      const res = await listTasks({ page: 1, size: 200 } as Parameters<typeof listTasks>[0])
      const records: ImplTaskNode[] = res.records ?? []
      list = records.filter(
        (t) =>
          (!t.engineerId || t.engineerId === currentUserId.value) &&
          (!t.status || activeStatuses.includes(t.status))
      )
    }
    taskTodos.value = list
  } catch {
    taskTodos.value = []
  } finally {
    loading.task = false
  }
}

async function loadPhaseTodos() {
  loading.phase = true
  try {
    const pid = filterProjectId.value
    if (pid) {
      // 项目维度：仅当项目状态为 IN_PROGRESS 时显示一条"推进阶段"待办
      const res = await listProjects({ page: 1, size: 200, status: 'IN_PROGRESS' })
      const proj = (res.records ?? []).find((p) => p.id === pid)
      phaseTodos.value = proj ? [proj] : []
    } else {
      // 全局：拉取所有进行中的项目，前端按当前用户为 manager 过滤
      const res = await listProjects({ page: 1, size: 200, status: 'IN_PROGRESS' })
      phaseTodos.value = (res.records ?? []).filter(
        (p) => !p.projectManagerName || p.projectManagerName === currentUserNickname.value
      )
    }
  } catch {
    phaseTodos.value = []
  } finally {
    loading.phase = false
  }
}

async function loadBaselineTodos() {
  loading.baseline = true
  try {
    const pid = filterProjectId.value
    const items: BaselineTodoItem[] = []
    // 候选项目列表：项目模式下为当前项目；全局模式下复用 projectOptions（已加载）
    const candidateProjects: Array<{ id?: number; projectName?: string }> = pid
      ? [{ id: pid }]
      : projectOptions.value.map((p) => ({ id: p.id, projectName: p.projectName }))
    // 全局模式下限制最多 20 个项目，避免 N+1 调用过载
    const projectsToCheck = candidateProjects.slice(0, 20)
    for (const p of projectsToCheck) {
      if (!p.id) continue
      try {
        const baselines = await listBaselines(p.id)
        // 仅检查最新的 APPROVED 基线（按 createTime 倒序，取首个 APPROVED）
        const active = baselines.find((b) => b.status === 'APPROVED') ?? baselines[0]
        if (!active || !active.id) continue
        const diff = await diffBaseline(active.id)
        if (diff.needsApproval) {
          items.push({ baseline: active, diff })
        }
      } catch {
        // 单个项目失败不影响整体
      }
    }
    baselineTodos.value = items
  } catch {
    baselineTodos.value = []
  } finally {
    loading.baseline = false
  }
}

async function loadAll() {
  await Promise.all([loadApprovalTodos(), loadTaskTodos(), loadPhaseTodos(), loadBaselineTodos()])
}

// ============================================================================
// 自动刷新（60s）
// ============================================================================
function startAutoRefresh() {
  stopAutoRefresh()
  if (autoRefresh.value) {
    refreshTimer = window.setInterval(loadAll, 60_000)
  }
}
function stopAutoRefresh() {
  if (refreshTimer !== null) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}
function onAutoRefreshChange(val: boolean | string | number) {
  if (val) startAutoRefresh()
  else stopAutoRefresh()
}

// ============================================================================
// 跳转
// ============================================================================
// 严格 id 校验：避免 todo.id 为 0/null/undefined/NaN 时 router.push
// 拼出无效路径（如 /implementation/task/detail/0），被 404 兜底重定向到 /dashboard，
// 造成「点待办跳首页」的表象。校验失败时给出明确提示。
function isValidId(id: unknown): id is number {
  return typeof id === 'number' && Number.isFinite(id) && id > 0
}

function goApproval(todo: ApprovalRecord) {
  if (!isValidId(todo.id)) {
    ElMessage.warning('该审批待办缺少有效 ID，无法跳转')
    return
  }
  router.push(`/workflow/approval-detail/${todo.id}`)
}
function goTask(todo: ImplTask) {
  if (!isValidId(todo.id)) {
    ElMessage.warning('该任务待办缺少有效 ID，无法跳转')
    return
  }
  router.push(`/implementation/task/detail/${todo.id}`)
}
function goPhase(todo: Project) {
  if (!isValidId(todo.id)) {
    ElMessage.warning('该项目待办缺少有效 ID，无法跳转')
    return
  }
  router.push(`/project/workspace/${todo.id}`)
}
function goBaseline(todo: BaselineTodoItem) {
  if (!isValidId(todo.baseline.id)) {
    ElMessage.warning('该基线待办缺少有效 ID，无法跳转')
    return
  }
  router.push(`/baseline/diff/${todo.baseline.id}`)
}

// ============================================================================
// 工具：格式化 / 项目名解析
// ============================================================================
function formatDate(date?: string): string {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

function formatApprovalType(t?: string): string {
  const map: Record<string, string> = {
    PROJECT: '项目审批',
    TASK: '任务审批',
    DELIVERABLE: '交付件审批',
    RISK: '风险审批',
    ISSUE: '问题审批',
    CHANGE: '变更审批',
    RESOURCE: '资源审批',
    COST: '成本审批',
    PHASE_EXIT: '阶段退出',
    BASELINE_CHANGE: '基线变更'
  }
  return t ? (map[t] ?? t) : '-'
}

function formatTaskStatus(s?: string): string {
  const map: Record<string, string> = {
    PENDING: '待接受',
    ACCEPTED: '已接受',
    IN_PROGRESS: '进行中',
    REVIEW: '评审中',
    BLOCKED: '阻塞',
    COMPLETED: '已完成',
    CONFIRMED: '已确认',
    REJECTED: '已拒绝'
  }
  return s ? (map[s] ?? s) : '-'
}

function projectName(pid?: number): string {
  if (!pid) return '-'
  // 优先从下拉选项查找
  const found = projectOptions.value.find((p) => p.id === pid)
  return found?.projectName ?? `#${pid}`
}

// 卡片左侧状态色条 class
function cardSeverityClass(overdue: boolean): string {
  return overdue ? 'severity-danger' : 'severity-primary'
}

// ============================================================================
// 生命周期
// ============================================================================
onMounted(async () => {
  await loadProjectOptions()
  await loadAll()
  startAutoRefresh()
})

onBeforeUnmount(() => {
  stopAutoRefresh()
})

// 暴露给模板的类型守卫
defineExpose({ loadAll })
</script>

<template>
  <div class="global-todo-center">
    <!-- 顶部统计栏 -->
    <div class="stats-bar">
      <div
        class="stat-card"
        :class="{ active: activeTab === 'approval' }"
        @click="activeTab = 'approval'"
      >
        <div class="stat-value">{{ approvalTodos.length }}</div>
        <div class="stat-label">审批待办</div>
      </div>
      <div
        class="stat-card"
        :class="{ active: activeTab === 'task' }"
        @click="activeTab = 'task'"
      >
        <div class="stat-value">{{ taskTodos.length }}</div>
        <div class="stat-label">任务待办</div>
      </div>
      <div
        class="stat-card"
        :class="{ active: activeTab === 'phase' }"
        @click="activeTab = 'phase'"
      >
        <div class="stat-value">{{ phaseTodos.length }}</div>
        <div class="stat-label">阶段推进</div>
      </div>
      <div
        class="stat-card"
        :class="{ active: activeTab === 'baseline' }"
        @click="activeTab = 'baseline'"
      >
        <div class="stat-value">{{ baselineTodos.length }}</div>
        <div class="stat-label">基线告警</div>
      </div>
      <div class="stat-card urgent">
        <div class="stat-value">{{ urgentCount }}</div>
        <div class="stat-label">紧急（已超期）</div>
      </div>
      <div class="stat-card total">
        <div class="stat-value">{{ totalCount }}</div>
        <div class="stat-label">合计</div>
      </div>
    </div>

    <!-- 筛选 + 自动刷新 -->
    <div class="filter-bar">
      <el-select
        v-if="!props.projectId"
        v-model="filterProjectId"
        placeholder="按项目筛选"
        clearable
        filterable
        style="width: 240px"
        @change="() => { resetPagination(); loadAll() }"
      >
        <el-option
          v-for="p in projectOptions"
          :key="p.id"
          :label="p.projectName"
          :value="p.id as number"
        />
      </el-select>
      <el-select
        v-model="filterUrgency"
        placeholder="紧急程度"
        style="width: 180px"
        @change="resetPagination"
      >
        <el-option label="全部" value="all" />
        <el-option label="紧急（已超期）" value="urgent" />
        <el-option label="未超期" value="normal" />
      </el-select>
      <div class="spacer" />
      <el-switch
        v-model="autoRefresh"
        active-text="60s 自动刷新"
        @change="onAutoRefreshChange"
      />
      <el-button :icon="Refresh" @click="loadAll">刷新</el-button>
    </div>

    <!-- Tabs -->
    <el-tabs v-model="activeTab">
      <!-- ============== 1. 审批待办 ============== -->
      <el-tab-pane name="approval">
        <template #label>
          <el-badge
            :value="approvalTodos.length"
            :hidden="approvalTodos.length === 0"
            type="danger"
          >
            <span class="tab-label">
              <el-icon><Checked /></el-icon>
              审批待办
            </span>
          </el-badge>
        </template>

        <SkeletonCard :loading="loading.approval">
          <EmptyState
            v-if="!filteredApprovalTodos.length && !loading.approval"
            title="暂无审批待办"
            description="当前没有等待您审批的事项"
            icon="Checked"
          />
          <div v-else class="todo-list">
            <el-card
              v-for="todo in pagedApprovalTodos"
              :key="todo.id"
              shadow="hover"
              class="todo-card"
              @click="goApproval(todo)"
            >
              <div class="todo-card-content">
                <div
                  class="status-bar"
                  :class="cardSeverityClass(approvalOverdue(todo))"
                />
                <div class="todo-card-body">
                  <div class="todo-title">
                    {{ todo.title || '-' }}
                    <el-tag
                      v-if="approvalOverdue(todo)"
                      type="danger"
                      size="small"
                      effect="dark"
                    >已超期</el-tag>
                  </div>
                  <div class="todo-desc">
                    {{ formatApprovalType(todo.approvalType) }} · 提交人
                    {{ todo.submitterName || '-' }}
                  </div>
                  <div class="todo-meta">
                    <span><el-icon><Folder /></el-icon>{{ projectName(todo.projectId) }}</span>
                    <span v-if="todo.timeoutAt">
                      <el-icon><Clock /></el-icon>截止 {{ formatDate(todo.timeoutAt) }}
                    </span>
                    <span v-if="todo.submittedAt">
                      <el-icon><Calendar /></el-icon>提交 {{ formatDate(todo.submittedAt) }}
                    </span>
                  </div>
                </div>
                <el-button type="primary" size="small" @click.stop="goApproval(todo)">
                  处理
                </el-button>
              </div>
            </el-card>

            <el-pagination
              v-if="filteredApprovalTodos.length > pagination.approval.size"
              class="pagination"
              background
              :current-page="pagination.approval.page"
              :page-size="pagination.approval.size"
              :total="filteredApprovalTodos.length"
              layout="prev, pager, next, total"
              @current-change="(p: number) => (pagination.approval.page = p)"
            />
          </div>
        </SkeletonCard>
      </el-tab-pane>

      <!-- ============== 2. 任务待办 ============== -->
      <el-tab-pane name="task">
        <template #label>
          <el-badge
            :value="taskTodos.length"
            :hidden="taskTodos.length === 0"
            type="warning"
          >
            <span class="tab-label">
              <el-icon><Tickets /></el-icon>
              任务待办
            </span>
          </el-badge>
        </template>

        <SkeletonCard :loading="loading.task">
          <EmptyState
            v-if="!filteredTaskTodos.length && !loading.task"
            title="暂无任务待办"
            description="当前没有分配给您的活跃任务"
            icon="Tickets"
          />
          <div v-else class="todo-list">
            <el-card
              v-for="todo in pagedTaskTodos"
              :key="todo.id"
              shadow="hover"
              class="todo-card"
              @click="goTask(todo)"
            >
              <div class="todo-card-content">
                <div
                  class="status-bar"
                  :class="cardSeverityClass(taskOverdue(todo))"
                />
                <div class="todo-card-body">
                  <div class="todo-title">
                    {{ todo.taskName }}
                    <el-tag
                      v-if="taskOverdue(todo)"
                      type="danger"
                      size="small"
                      effect="dark"
                    >已超期</el-tag>
                    <el-tag
                      v-if="todo.status"
                      size="small"
                      type="info"
                    >{{ formatTaskStatus(todo.status) }}</el-tag>
                  </div>
                  <div class="todo-desc">
                    {{ todo.taskType || '任务' }} ·
                    {{ todo.engineerName || todo.agentName || '-' }}
                  </div>
                  <div class="todo-meta">
                    <span><el-icon><Folder /></el-icon>{{ projectName(todo.projectId) }}</span>
                    <span v-if="todo.planEndDate">
                      <el-icon><Clock /></el-icon>截止 {{ formatDate(todo.planEndDate) }}
                    </span>
                    <span v-if="todo.progress !== undefined">
                      <el-icon><Promotion /></el-icon>进度 {{ todo.progress }}%
                    </span>
                  </div>
                </div>
                <el-button type="primary" size="small" @click.stop="goTask(todo)">
                  处理
                </el-button>
              </div>
            </el-card>

            <el-pagination
              v-if="filteredTaskTodos.length > pagination.task.size"
              class="pagination"
              background
              :current-page="pagination.task.page"
              :page-size="pagination.task.size"
              :total="filteredTaskTodos.length"
              layout="prev, pager, next, total"
              @current-change="(p: number) => (pagination.task.page = p)"
            />
          </div>
        </SkeletonCard>
      </el-tab-pane>

      <!-- ============== 3. 阶段推进待办 ============== -->
      <el-tab-pane name="phase">
        <template #label>
          <el-badge
            :value="phaseTodos.length"
            :hidden="phaseTodos.length === 0"
            type="primary"
          >
            <span class="tab-label">
              <el-icon><Promotion /></el-icon>
              阶段推进
            </span>
          </el-badge>
        </template>

        <SkeletonCard :loading="loading.phase">
          <EmptyState
            v-if="!filteredPhaseTodos.length && !loading.phase"
            title="暂无阶段推进待办"
            description="您当前没有可推进阶段的项目"
            icon="Promotion"
          />
          <div v-else class="todo-list">
            <el-card
              v-for="todo in pagedPhaseTodos"
              :key="todo.id"
              shadow="hover"
              class="todo-card"
              @click="goPhase(todo)"
            >
              <div class="todo-card-content">
                <div
                  class="status-bar"
                  :class="cardSeverityClass(phaseOverdue(todo))"
                />
                <div class="todo-card-body">
                  <div class="todo-title">
                    {{ todo.projectName }}
                    <el-tag
                      v-if="phaseOverdue(todo)"
                      type="danger"
                      size="small"
                      effect="dark"
                    >已超期</el-tag>
                    <el-tag
                      v-if="todo.status"
                      size="small"
                      type="success"
                    >进行中</el-tag>
                  </div>
                  <div class="todo-desc">
                    {{ todo.projectCode || '-' }} · 经理 {{ todo.projectManagerName || '-' }}
                  </div>
                  <div class="todo-meta">
                    <span><el-icon><Folder /></el-icon>{{ todo.customerName || '-' }}</span>
                    <span v-if="todo.planEndDate">
                      <el-icon><Clock /></el-icon>计划完工 {{ formatDate(todo.planEndDate) }}
                    </span>
                    <span v-if="todo.progress !== undefined">
                      <el-icon><Promotion /></el-icon>进度 {{ todo.progress }}%
                    </span>
                  </div>
                </div>
                <el-button type="primary" size="small" @click.stop="goPhase(todo)">
                  推进
                </el-button>
              </div>
            </el-card>

            <el-pagination
              v-if="filteredPhaseTodos.length > pagination.phase.size"
              class="pagination"
              background
              :current-page="pagination.phase.page"
              :page-size="pagination.phase.size"
              :total="filteredPhaseTodos.length"
              layout="prev, pager, next, total"
              @current-change="(p: number) => (pagination.phase.page = p)"
            />
          </div>
        </SkeletonCard>
      </el-tab-pane>

      <!-- ============== 4. 基线偏差告警 ============== -->
      <el-tab-pane name="baseline">
        <template #label>
          <el-badge
            :value="baselineTodos.length"
            :hidden="baselineTodos.length === 0"
            type="danger"
          >
            <span class="tab-label">
              <el-icon><Histogram /></el-icon>
              基线告警
            </span>
          </el-badge>
        </template>

        <SkeletonCard :loading="loading.baseline">
          <EmptyState
            v-if="!filteredBaselineTodos.length && !loading.baseline"
            title="暂无基线偏差告警"
            description="所有项目基线均在阈值范围内"
            icon="Histogram"
          />
          <div v-else class="todo-list">
            <el-card
              v-for="todo in pagedBaselineTodos"
              :key="todo.baseline.id"
              shadow="hover"
              class="todo-card"
              @click="goBaseline(todo)"
            >
              <div class="todo-card-content">
                <div class="status-bar severity-danger" />
                <div class="todo-card-body">
                  <div class="todo-title">
                    {{ todo.baseline.baselineName }}
                    <el-tag type="danger" size="small" effect="dark">
                      <el-icon><WarningFilled /></el-icon>需审批
                    </el-tag>
                    <el-tag size="small" type="warning">
                      偏差 {{ todo.diff.totalVarianced }} 项
                    </el-tag>
                  </div>
                  <div class="todo-desc">
                    {{ todo.diff.approvalReason || '基线偏差超过双阈值，需触发 BASELINE_CHANGE 审批' }}
                  </div>
                  <div class="todo-meta">
                    <span><el-icon><Folder /></el-icon>{{ projectName(todo.baseline.projectId) }}</span>
                    <span v-if="todo.baseline.approvedAt">
                      <el-icon><Calendar /></el-icon>基线审批 {{ formatDate(todo.baseline.approvedAt) }}
                    </span>
                  </div>
                </div>
                <el-button type="primary" size="small" @click.stop="goBaseline(todo)">
                  查看
                </el-button>
              </div>
            </el-card>

            <el-pagination
              v-if="filteredBaselineTodos.length > pagination.baseline.size"
              class="pagination"
              background
              :current-page="pagination.baseline.page"
              :page-size="pagination.baseline.size"
              :total="filteredBaselineTodos.length"
              layout="prev, pager, next, total"
              @current-change="(p: number) => (pagination.baseline.page = p)"
            />
          </div>
        </SkeletonCard>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.global-todo-center {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ===== 顶部统计栏 ===== */
.stats-bar {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  background: var(--pms-color-bg-card, #fff);
  border: 1px solid var(--pms-color-border-light, #ebeef5);
  border-radius: var(--pms-radius-lg, 8px);
  cursor: pointer;
  transition: all 0.2s;
}
.stat-card:hover {
  border-color: var(--pms-color-primary, #409eff);
  box-shadow: var(--pms-shadow-card, 0 2px 8px rgba(0, 0, 0, 0.06));
}
.stat-card.active {
  border-color: var(--pms-color-primary, #409eff);
  background: var(--pms-color-primary-bg, #ecf5ff);
}
.stat-card.urgent {
  background: var(--pms-color-danger-bg, #fef0f0);
  border-color: var(--pms-color-danger, #f56c6c);
}
.stat-card.urgent .stat-value {
  color: var(--pms-color-danger, #f56c6c);
}
.stat-card.total {
  background: var(--pms-color-info-bg, #f4f4f5);
  border-color: var(--pms-color-info, #909399);
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
  line-height: 1.2;
}
.stat-label {
  margin-top: 4px;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

/* ===== 筛选栏 ===== */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.filter-bar .spacer {
  flex: 1;
}

/* ===== Tabs / 卡片 ===== */
.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.todo-card {
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.todo-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.todo-card-content {
  display: flex;
  align-items: stretch;
  gap: 12px;
}
.status-bar {
  width: 4px;
  border-radius: 2px;
  flex-shrink: 0;
}
.status-bar.severity-primary {
  background: var(--pms-color-primary, #409eff);
}
.status-bar.severity-danger {
  background: var(--pms-color-danger, #f56c6c);
}
.todo-card-body {
  flex: 1;
  min-width: 0;
}
.todo-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--pms-color-text-primary, #303133);
  margin-bottom: 4px;
}
.todo-desc {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  margin-bottom: 4px;
}
.todo-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: var(--pms-color-text-regular, #606266);
}
.todo-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}

/* 响应式：小屏 6 列统计栏改为 3 列 */
@media (max-width: 1024px) {
  .stats-bar {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 600px) {
  .stats-bar {
    grid-template-columns: repeat(2, 1fr);
  }
  .filter-bar {
    flex-wrap: wrap;
  }
}
</style>
