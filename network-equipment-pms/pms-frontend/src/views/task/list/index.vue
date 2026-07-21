<script setup lang="ts">
// =============================================================================
// TaskList - 任务列表页（树形 / 看板双视图）
// -----------------------------------------------------------------------------
// - 树形视图（默认）：el-table 树形数据 + 层级缩进 + 行展开
//     列：任务名/编号/优先级/状态/负责人/计划起止/实际起止/进度/操作
//     工具：折叠所有 / 展开所有 / 仅看我的任务
// - 看板视图：5 列 (TODO/IN_PROGRESS/BLOCKED/REVIEW/DONE)
//     卡片可拖拽变更状态 (HTML5 drag API)
//     列头数量徽章；卡片点击进入详情
// - 筛选：阶段 / 优先级 / 负责人 / 关键字
// - PageHeader + SkeletonCard + EmptyState + TaskPriorityTag
// =============================================================================
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type TableInstance } from 'element-plus'
import {
  approveTask,
  completeTask,
  getTasksByProject,
  listTasks,
  moveTask,
  startTask,
  submitForReview,
  type ImplTaskNode,
  type TaskPriority,
  type TaskStatus,
  type TaskReviewResult
} from '@/api/implementation'
import { getProject, listProjects, type Project } from '@/api/project'
import { listPhasesByProjectId, type ProjectPhase } from '@/api/project-phase'
import { listMembersByProjectId, type ProjectMember } from '@/api/project-member'
import { useUserStore } from '@/stores/user'
import type { EpTagType } from '@/types'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import TaskPriorityTag from '@/components/common/TaskPriorityTag.vue'

defineOptions({ name: 'TaskList' })

interface Props {
  projectId?: number | string
}
const props = defineProps<Props>()
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// ============ 视图模式 ============
const viewMode = ref<'tree' | 'kanban'>('tree')

// ============ 数据 ============
const loading = ref(false)
const allTasks = ref<ImplTaskNode[]>([]) // 全量任务（项目模式或当前页扁平任务）
const projectInfo = ref<Project | null>(null)
const phaseOptions = ref<ProjectPhase[]>([])
const memberOptions = ref<ProjectMember[]>([])
const projectOptions = ref<Project[]>([]) // 全局模式下项目下拉
const total = ref(0)
const page = reactive({ current: 1, size: 20 })

// ============ 表格 ref ============
const tableRef = ref<TableInstance | null>(null)

// ============ 筛选 ============
const query = reactive({
  projectId: undefined as number | undefined,
  phaseId: undefined as number | undefined,
  priority: undefined as TaskPriority | undefined,
  assigneeId: undefined as number | undefined,
  status: undefined as TaskStatus | undefined,
  keyword: ''
})
const onlyMine = ref(false)

// ============ 看板列定义 ============
interface KanbanColumn {
  label: string
  value: string
  statuses: TaskStatus[]
}
const kanbanColumns: KanbanColumn[] = [
  { label: '待开始', value: 'TODO', statuses: ['PENDING', 'ACCEPTED'] },
  { label: '进行中', value: 'IN_PROGRESS', statuses: ['IN_PROGRESS'] },
  { label: '已阻塞', value: 'BLOCKED', statuses: ['BLOCKED'] },
  { label: '评审中', value: 'REVIEW', statuses: ['REVIEW'] },
  { label: '已完成', value: 'DONE', statuses: ['COMPLETED', 'CONFIRMED'] }
]

// ============ 状态/优先级选项 ============
const statusOptions: { label: string; value: TaskStatus }[] = [
  { label: '待接单', value: 'PENDING' },
  { label: '已接单', value: 'ACCEPTED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '评审中', value: 'REVIEW' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已确认', value: 'CONFIRMED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已阻塞', value: 'BLOCKED' }
]

const priorityOptions: { label: string; value: TaskPriority }[] = [
  { label: '低', value: 'LOW' },
  { label: '中', value: 'MEDIUM' },
  { label: '高', value: 'HIGH' },
  { label: '紧急', value: 'CRITICAL' }
]

const statusTagTypeMap: Record<string, EpTagType> = {
  PENDING: 'info',
  ACCEPTED: 'warning',
  IN_PROGRESS: 'primary',
  REVIEW: 'warning',
  COMPLETED: 'success',
  CONFIRMED: 'success',
  REJECTED: 'danger',
  BLOCKED: 'danger'
}

function statusLabel(status?: string): string {
  return statusOptions.find((s) => s.value === status)?.label ?? status ?? '-'
}

function statusTagType(status?: string): EpTagType {
  return status ? (statusTagTypeMap[status] ?? 'info') : 'info'
}

function assigneeText(row: ImplTaskNode): string {
  if (row.taskType === 'AGENT') return row.agentName || '-'
  return row.engineerName || '-'
}

function assigneeId(row: ImplTaskNode): number | undefined {
  if (row.taskType === 'AGENT') return row.agentId
  return row.engineerId
}

/** 根据 phaseId 查找阶段名称（避免直接显示 ID） */
function phaseNameOf(phaseId?: number | null): string {
  if (!phaseId) return '-'
  const phase = phaseOptions.value.find((p) => p.id === phaseId)
  return phase?.phaseName ?? `阶段#${phaseId}`
}

/** 根据 projectId 查找项目名称（避免直接显示 ID） */
function projectNameOf(projectId?: number | null): string {
  if (!projectId) return '-'
  // 项目模式下 projectInfo 已加载
  if (projectInfo.value?.id === projectId) return projectInfo.value.projectName
  // 全局模式下从 projectOptions 查找
  const proj = projectOptions.value.find((p) => p.id === projectId)
  return proj?.projectName ?? '-'
}

// ============ 当前 projectId：prop 优先，否则从 route.params ============
const currentProjectId = computed<number | undefined>(() => {
  if (props.projectId !== undefined && props.projectId !== null && props.projectId !== '') {
    const n = Number(props.projectId)
    return Number.isNaN(n) ? undefined : n
  }
  if (route.params.projectId) {
    const n = Number(route.params.projectId)
    return Number.isNaN(n) ? undefined : n
  }
  return undefined
})

const currentProjectName = computed(() => projectInfo.value?.projectName || '全部项目')

const headerTitle = computed(() => `任务管理 · ${currentProjectName.value}`)

// ============ 树形构建 ============
/**
 * 构建嵌套树：根据 parentTaskId 把扁平列表组装为 children 嵌套结构。
 * 顶层任务：parentTaskId 为 null/undefined/0。
 */
function buildTree(tasks: ImplTaskNode[]): ImplTaskNode[] {
  const map = new Map<number, ImplTaskNode>()
  const roots: ImplTaskNode[] = []
  tasks.forEach((t) => {
    map.set(t.id!, { ...t, children: [] })
  })
  tasks.forEach((t) => {
    const node = map.get(t.id!)
    if (!node) return
    const parentId = t.parentTaskId
    if (parentId && map.has(parentId)) {
      map.get(parentId)!.children!.push(node)
    } else {
      roots.push(node)
    }
  })
  // 清理空 children 数组（el-table 据此判断是否显示展开箭头）
  const cleanup = (node: ImplTaskNode) => {
    if (node.children && node.children.length === 0) {
      delete node.children
    } else if (node.children) {
      node.children.forEach(cleanup)
    }
  }
  roots.forEach(cleanup)
  return roots
}

// ============ 客户端筛选 ============
const filteredTasks = computed<ImplTaskNode[]>(() => {
  let list = allTasks.value
  if (query.phaseId) list = list.filter((t) => t.phaseId === query.phaseId)
  if (query.priority) list = list.filter((t) => t.priority === query.priority)
  if (query.assigneeId) {
    list = list.filter((t) => assigneeId(t) === query.assigneeId)
  }
  if (query.status) list = list.filter((t) => t.status === query.status)
  if (query.keyword) {
    const kw = query.keyword.trim().toLowerCase()
    list = list.filter(
      (t) =>
        (t.taskName || '').toLowerCase().includes(kw) ||
        (t.projectName || '').toLowerCase().includes(kw)
    )
  }
  if (onlyMine.value) {
    const uid = userStore.userInfo?.id
    if (uid) {
      list = list.filter((t) => assigneeId(t) === uid)
    }
  }
  return list
})

const taskTree = computed<ImplTaskNode[]>(() => buildTree(filteredTasks.value))

// ============ 看板分组 ============
function getTasksByColumn(col: KanbanColumn): ImplTaskNode[] {
  // 看板只展示顶层任务（无 parentTaskId），避免父子重复
  return filteredTasks.value.filter(
    (t) => !t.parentTaskId && t.status && col.statuses.includes(t.status as TaskStatus)
  )
}

// ============ 数据加载 ============
async function loadProjectMeta() {
  const pid = currentProjectId.value
  if (!pid) {
    projectInfo.value = null
    phaseOptions.value = []
    memberOptions.value = []
    return
  }
  try {
    projectInfo.value = await getProject(pid)
  } catch {
    projectInfo.value = null
  }
  try {
    phaseOptions.value = (await listPhasesByProjectId(pid)) ?? []
  } catch {
    phaseOptions.value = []
  }
  try {
    memberOptions.value = (await listMembersByProjectId(pid)) ?? []
  } catch {
    memberOptions.value = []
  }
}

async function loadProjectOptions() {
  // 全局模式下加载项目下拉
  if (currentProjectId.value) return
  try {
    const res = await listProjects({ page: 1, size: 200 })
    projectOptions.value = res.records ?? []
  } catch {
    projectOptions.value = []
  }
}

async function loadData() {
  loading.value = true
  try {
    const pid = currentProjectId.value || query.projectId
    if (pid) {
      // 项目模式：加载该项目全部任务，客户端构建嵌套树
      const all = await getTasksByProject(pid)
      allTasks.value = (all as ImplTaskNode[]) ?? []
      total.value = allTasks.value.length
    } else {
      // 全局模式：分页返回扁平任务
      const res = await listTasks({
        page: page.current,
        size: page.size,
        status: query.status
      } as any)
      allTasks.value = (res.records as ImplTaskNode[]) ?? []
      total.value = res.total ?? 0
    }
  } catch {
    allTasks.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.current = 1
  loadData()
}

function handleReset() {
  query.phaseId = undefined
  query.priority = undefined
  query.assigneeId = undefined
  query.status = undefined
  query.keyword = ''
  onlyMine.value = false
  page.current = 1
  loadData()
}

function handlePageChange(p: number) {
  page.current = p
  loadData()
}

function handleSizeChange(s: number) {
  page.size = s
  page.current = 1
  loadData()
}

// ============ 树形展开/折叠 ============
function expandAll() {
  nextTick(() => {
    toggleAll(true)
  })
}

function collapseAll() {
  nextTick(() => {
    toggleAll(false)
  })
}

function toggleAll(expand: boolean) {
  const table = tableRef.value
  if (!table) return
  // 递归遍历树，调用 toggleTreeExpansion
  const walk = (nodes: ImplTaskNode[]) => {
    nodes.forEach((n) => {
      if (n.children && n.children.length > 0) {
        table!.toggleTreeExpansion(n, expand)
        walk(n.children)
      }
    })
  }
  walk(taskTree.value)
}

// ============ 行操作 ============
function handleViewDetail(row: ImplTaskNode) {
  if (!row.id) return
  router.push(`/implementation/task/detail/${row.id}`)
}

function handleEdit(row: ImplTaskNode) {
  if (!row.id) return
  router.push(`/implementation/task/detail/${row.id}?action=edit`)
}

function handleAddChild(row: ImplTaskNode) {
  if (!row.id) return
  router.push(`/implementation/task/detail/${row.id}?action=add-child`)
}

async function handleDelete(row: ImplTaskNode) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确定删除任务「${row.taskName}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  // 注：当前 API 模块未暴露 deleteTask，仅提示
  ElMessage.warning('当前未提供任务删除 API，请联系后端确认')
}

async function handleSubmitReview(row: ImplTaskNode) {
  if (!row.id) return
  try {
    const result: TaskReviewResult = await submitForReview(row.id)
    if (result.success) {
      ElMessage.success('已提交评审')
      loadData()
    } else if (result.errorCode === 'TASK_CHECKLIST_REQUIRED') {
      const items = result.uncheckedMandatoryItems ?? []
      const itemText = items.map((i) => `• ${i.title}`).join('\n')
      ElMessageBox.alert(
        `存在 ${items.length} 项未完成的强制检查项：\n\n${itemText}\n\n请先勾选这些检查项后再提交评审。`,
        '无法提交评审',
        { type: 'warning', confirmButtonText: '去检查项 Tab 处理' }
      )
        .then(() => {
          router.push(`/implementation/task/detail/${row.id}?tab=checklist`)
        })
        .catch(() => {
          /* dismissed */
        })
    } else {
      ElMessage.error(result.errorMessage || '提交评审失败')
    }
  } catch {
    /* handled by interceptor */
  }
}

async function handleApprove(row: ImplTaskNode) {
  if (!row.id) return
  ElMessageBox.confirm(`确定验收任务「${row.taskName}」吗？`, '验收确认', {
    type: 'warning',
    confirmButtonText: '验收通过',
    cancelButtonText: '取消'
  })
    .then(async () => {
      try {
        const result: TaskReviewResult = await approveTask(row.id!)
        if (result.success) {
          ElMessage.success('任务已验收完成')
          loadData()
        } else {
          ElMessage.error(result.errorMessage || '验收失败')
        }
      } catch {
        /* handled by interceptor */
      }
    })
    .catch(() => {
      /* cancelled */
    })
}

const canSubmitReview = (status?: string) => status === 'IN_PROGRESS' || status === 'BLOCKED'
const canApprove = (status?: string) => status === 'REVIEW'

// ============ 移动任务 ============
const moveVisible = ref(false)
const moveForm = reactive({
  taskId: 0,
  taskName: '',
  newParentId: undefined as number | undefined
})
const moveCandidateTasks = ref<ImplTaskNode[]>([])

function handleMove(row: ImplTaskNode) {
  if (!row.id) return
  moveForm.taskId = row.id
  moveForm.taskName = row.taskName
  moveForm.newParentId = undefined
  const pid = currentProjectId.value || query.projectId
  if (pid) {
    getTasksByProject(pid)
      .then((all) => {
        // 客户端过滤掉自身及其后代（基于 taskPath），避免明显环路
        const myPath = row.taskPath || ''
        moveCandidateTasks.value = (all as ImplTaskNode[]).filter((t) => {
          if (t.id === row.id) return false
          if (myPath && t.taskPath && t.taskPath.startsWith(myPath)) return false
          return true
        })
      })
      .catch(() => {
        moveCandidateTasks.value = []
      })
  } else {
    moveCandidateTasks.value = []
  }
  moveVisible.value = true
}

async function handleMoveSubmit() {
  try {
    await moveTask(moveForm.taskId, moveForm.newParentId ?? null)
    ElMessage.success('任务已移动')
    moveVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 新建任务 ============
const createVisible = ref(false)
const createForm = reactive({
  taskName: '',
  priority: 'MEDIUM' as TaskPriority
})

function openCreate() {
  createForm.taskName = ''
  createForm.priority = 'MEDIUM'
  createVisible.value = true
}

async function handleCreate() {
  if (!createForm.taskName.trim()) {
    ElMessage.warning('请输入任务名称')
    return
  }
  // 复用 OEM 任务分配接口（当前 API 未提供裸 createTask）
  const pid = currentProjectId.value || query.projectId
  if (!pid) {
    ElMessage.warning('请先选择项目')
    return
  }
  try {
    // 简化：直接跳转到详情页新建（避免误用 assignOemTask/assignAgentTask）
    ElMessage.info('请到任务详情页填写完整信息后创建')
    createVisible.value = false
    router.push(`/implementation/task/detail/0?action=create&projectId=${pid}&taskName=${encodeURIComponent(createForm.taskName)}&priority=${createForm.priority}`)
  } catch {
    /* handled by interceptor */
  }
}

// ============ 看板拖拽（HTML5 drag API） ============
const draggingTask = ref<ImplTaskNode | null>(null)
const dragOverColumn = ref<string>('')

function onDragStart(event: DragEvent, task: ImplTaskNode) {
  draggingTask.value = task
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    // 兼容 Firefox：必须设置 data 才能触发 drag
    event.dataTransfer.setData('text/plain', String(task.id ?? ''))
  }
}

function onDragOver(event: DragEvent, colValue: string) {
  // 允许 drop
  event.preventDefault()
  if (event.dataTransfer) event.dataTransfer.dropEffect = 'move'
  dragOverColumn.value = colValue
}

function onDragLeave(_event: DragEvent, colValue: string) {
  if (dragOverColumn.value === colValue) {
    dragOverColumn.value = ''
  }
}

function onDrop(event: DragEvent, col: KanbanColumn) {
  event.preventDefault()
  dragOverColumn.value = ''
  const task = draggingTask.value
  draggingTask.value = null
  if (!task || !task.id) return
  // 若拖到当前所属列，无操作
  if (col.statuses.includes(task.status as TaskStatus)) return
  handleKanbanDrop(task, col)
}

async function handleKanbanDrop(task: ImplTaskNode, col: KanbanColumn) {
  const id = task.id!
  const previousStatus = task.status
  // 乐观更新：先把卡片放到目标列
  task.status = col.statuses[0]
  try {
    switch (col.value) {
      case 'TODO':
        // 当前 API 不支持回退到待开始
        ElMessage.warning('不支持将任务回退到待开始，请到详情页处理')
        task.status = previousStatus as TaskStatus
        return
      case 'IN_PROGRESS':
        await startTask(id)
        ElMessage.success('任务已开始')
        break
      case 'BLOCKED':
        // 当前 API 未提供阻塞接口
        ElMessage.warning('暂不支持直接拖拽到阻塞，请到详情页处理')
        task.status = previousStatus as TaskStatus
        return
      case 'REVIEW':
        await submitForReview(id)
        ElMessage.success('已提交评审')
        break
      case 'DONE':
        await completeTask(id)
        ElMessage.success('任务已完成')
        break
    }
    loadData()
  } catch {
    // 失败回滚
    task.status = previousStatus as TaskStatus
    ElMessage.error('状态变更失败，已回滚')
  }
}

function onDragEnd() {
  draggingTask.value = null
  dragOverColumn.value = ''
}

// ============ 日期格式化 ============
function formatDate(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 10)
}

// ============ 监听 ============
watch(
  () => currentProjectId.value,
  () => {
    loadProjectMeta()
    loadData()
  }
)

watch(
  () => query.projectId,
  () => {
    page.current = 1
    loadData()
  }
)

onMounted(async () => {
  // 同步 query.projectId 初值
  query.projectId = currentProjectId.value
  await loadProjectOptions()
  await loadProjectMeta()
  loadData()
})
</script>

<template>
  <div class="task-list-page">
    <PageHeader :title="headerTitle" description="按树形结构或看板查看任务，支持拖拽变更状态">
      <template #actions>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="tree">树形列表</el-radio-button>
          <el-radio-button label="kanban">看板</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="openCreate">新建任务</el-button>
      </template>
    </PageHeader>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select
        v-if="!currentProjectId"
        v-model="query.projectId"
        placeholder="选择项目"
        clearable
        filterable
        style="width: 200px"
      >
        <el-option v-for="p in projectOptions" :key="p.id" :label="p.projectName" :value="p.id!" />
      </el-select>
      <el-select
        v-model="query.phaseId"
        placeholder="阶段"
        clearable
        style="width: 160px"
        :disabled="!currentProjectId"
      >
        <el-option
          v-for="ph in phaseOptions"
          :key="ph.id"
          :label="ph.phaseName"
          :value="ph.id!"
        />
      </el-select>
      <el-select v-model="query.priority" placeholder="优先级" clearable style="width: 120px">
        <el-option
          v-for="p in priorityOptions"
          :key="p.value"
          :label="p.label"
          :value="p.value"
        />
      </el-select>
      <el-select
        v-model="query.assigneeId"
        placeholder="负责人"
        clearable
        filterable
        style="width: 160px"
        :disabled="!currentProjectId"
      >
        <el-option
          v-for="m in memberOptions"
          :key="m.id"
          :label="m.userName || `成员（角色：${m.role}）`"
          :value="m.userId"
        />
      </el-select>
      <el-select v-if="viewMode === 'tree'" v-model="query.status" placeholder="状态" clearable style="width: 130px">
        <el-option
          v-for="s in statusOptions"
          :key="s.value"
          :label="s.label"
          :value="s.value"
        />
      </el-select>
      <el-input
        v-model="query.keyword"
        placeholder="搜索任务名/项目"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-checkbox v-model="onlyMine" border>仅看我的任务</el-checkbox>
    </div>

    <!-- 加载骨架屏 -->
    <SkeletonCard v-if="loading" :loading="true" :rows="6">
      <div />
    </SkeletonCard>

    <template v-else>
      <!-- 空状态 -->
      <EmptyState
        v-if="filteredTasks.length === 0"
        title="暂无任务"
        description="当前筛选条件下没有任务数据，可调整筛选或新建任务"
        icon="Tickets"
      >
        <template #action>
          <el-button type="primary" @click="openCreate">新建任务</el-button>
        </template>
      </EmptyState>

      <!-- 树形列表视图 -->
      <div v-else-if="viewMode === 'tree'" class="tree-view">
        <div class="tree-toolbar">
          <el-button size="small" @click="expandAll">展开所有</el-button>
          <el-button size="small" @click="collapseAll">折叠所有</el-button>
          <span class="tree-tip">共 {{ filteredTasks.length }} 条任务（含子任务）</span>
        </div>
        <el-table
          ref="tableRef"
          :data="taskTree"
          border
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          default-expand-all
          highlight-current-row
        >
          <el-table-column
            prop="taskName"
            label="任务名称"
            min-width="220"
            show-overflow-tooltip
          />
          <el-table-column label="所属项目" width="160" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="task-project">{{ projectNameOf(row.projectId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="所属阶段" width="140" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="task-phase">{{ phaseNameOf(row.phaseId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="优先级" width="100" align="center">
            <template #default="{ row }">
              <TaskPriorityTag
                v-if="row.priority"
                :priority="row.priority"
                size="small"
              />
              <span v-else>—</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small" effect="light">
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="负责人" min-width="100">
            <template #default="{ row }">{{ assigneeText(row) }}</template>
          </el-table-column>
          <el-table-column label="计划起止" min-width="180">
            <template #default="{ row }">
              <span class="date-range">
                {{ formatDate(row.planStartDate) }} ~ {{ formatDate(row.planEndDate) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="实际起止" min-width="180">
            <template #default="{ row }">
              <span class="date-range">
                {{ formatDate(row.actualStart) }} ~ {{ formatDate(row.actualEnd) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="进度" width="160">
            <template #default="{ row }">
              <el-progress
                :percentage="row.progress ?? 0"
                :stroke-width="14"
                :text-inside="true"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleViewDetail(row)">详情</el-button>
              <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="primary" size="small" @click="handleAddChild(row)">加子任务</el-button>
              <el-button link type="primary" size="small" @click="handleMove(row)">移动</el-button>
              <el-button
                v-if="canSubmitReview(row.status)"
                link
                type="warning"
                size="small"
                @click="handleSubmitReview(row)"
              >
                提交评审
              </el-button>
              <el-button
                v-if="canApprove(row.status)"
                link
                type="success"
                size="small"
                @click="handleApprove(row)"
              >
                验收
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-if="!currentProjectId && !query.projectId"
          class="pagination"
          background
          :current-page="page.current"
          :page-size="page.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>

      <!-- 看板视图 -->
      <div v-else class="kanban-view">
        <div
          v-for="col in kanbanColumns"
          :key="col.value"
          class="kanban-column"
          :class="{ 'drag-over': dragOverColumn === col.value }"
          @dragover="onDragOver($event, col.value)"
          @dragleave="onDragLeave($event, col.value)"
          @drop="onDrop($event, col)"
        >
          <div class="column-header">
            <span class="column-title">{{ col.label }}</span>
            <el-badge
              :value="getTasksByColumn(col).length"
              :type="(statusTagType(col.statuses[0]) as any)"
              class="column-badge"
            />
          </div>
          <div class="column-body">
            <div
              v-for="task in getTasksByColumn(col)"
              :key="task.id"
              class="kanban-card"
              draggable="true"
              @dragstart="onDragStart($event, task)"
              @dragend="onDragEnd"
              @click="handleViewDetail(task)"
            >
              <div class="card-title">{{ task.taskName }}</div>
              <div class="card-code">
                <span class="card-project">{{ projectNameOf(task.projectId) }}</span>
                <span v-if="task.phaseId" class="card-phase">· {{ phaseNameOf(task.phaseId) }}</span>
              </div>
              <div class="card-meta">
                <TaskPriorityTag
                  v-if="task.priority"
                  :priority="task.priority"
                  size="small"
                />
                <el-tag :type="statusTagType(task.status)" size="small" effect="light">
                  {{ statusLabel(task.status) }}
                </el-tag>
              </div>
              <div class="card-progress">
                <el-progress
                  :percentage="task.progress ?? 0"
                  :stroke-width="6"
                  :show-text="false"
                />
              </div>
              <div class="card-footer">
                <span class="assignee">{{ assigneeText(task) }}</span>
                <span class="due-date">{{ formatDate(task.planEndDate) }}</span>
              </div>
            </div>
            <div v-if="getTasksByColumn(col).length === 0" class="column-empty">
              拖拽任务到此处
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 移动任务弹窗 -->
    <el-dialog v-model="moveVisible" title="移动任务" width="520px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="当前任务">
          <span>{{ moveForm.taskName }}</span>
        </el-form-item>
        <el-form-item label="新父任务">
          <el-select
            v-model="moveForm.newParentId"
            placeholder="留空表示提升为顶层任务"
            clearable
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="t in moveCandidateTasks"
              :key="t.id"
              :label="t.taskName"
              :value="t.id!"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moveVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMoveSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新建任务弹窗 -->
    <el-dialog v-model="createVisible" title="新建任务" width="500px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="任务名称">
          <el-input v-model="createForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="createForm.priority" style="width: 100%">
            <el-option
              v-for="p in priorityOptions"
              :key="p.value"
              :label="p.label"
              :value="p.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">下一步（到详情页填写）</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.task-list-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 12px;
  background: var(--pms-color-bg-card, #fff);
  border-radius: var(--pms-radius-lg, 8px);
  box-shadow: var(--pms-shadow-card, 0 1px 2px rgba(0, 0, 0, 0.04));
}

.tree-view {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tree-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tree-tip {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

.task-code {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

.task-project,
.task-phase {
  font-size: 12px;
  color: var(--pms-color-text-regular, #606266);
}

.card-project,
.card-phase {
  font-size: 11px;
  color: var(--pms-color-text-secondary, #909399);
}

.date-range {
  font-size: 12px;
  color: var(--pms-color-text-regular, #606266);
}

.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}

/* ============ 看板样式 ============ */
.kanban-view {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  min-height: 480px;
}

.kanban-column {
  flex: 1;
  min-width: 280px;
  max-width: 360px;
  background: var(--pms-color-bg-page, #f5f7fa);
  border-radius: var(--pms-radius-lg, 8px);
  padding: 12px;
  display: flex;
  flex-direction: column;
  transition: background 0.15s ease, box-shadow 0.15s ease;
}

.kanban-column.drag-over {
  background: var(--el-color-primary-light-9, #ecf5ff);
  box-shadow: inset 0 0 0 2px var(--el-color-primary, #409eff);
}

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.column-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-regular, #303133);
}

.column-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-height: 80px;
}

.column-empty {
  font-size: 12px;
  color: var(--pms-color-text-placeholder, #c0c4cc);
  text-align: center;
  padding: 24px 8px;
  border: 1px dashed var(--pms-color-border-light, #ebeef5);
  border-radius: 4px;
}

.kanban-card {
  background: var(--pms-color-bg-card, #fff);
  border-radius: 6px;
  padding: 10px 12px;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  user-select: none;
}

.kanban-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.kanban-card:active {
  cursor: grabbing;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--pms-color-text-primary, #303133);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-code {
  font-family: 'Courier New', monospace;
  font-size: 11px;
  color: var(--pms-color-text-placeholder, #909399);
  margin-bottom: 6px;
}

.card-meta {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.card-progress {
  margin-bottom: 8px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

.card-footer .assignee {
  font-weight: 500;
}

.card-footer .due-date {
  font-family: 'Courier New', monospace;
}
</style>
