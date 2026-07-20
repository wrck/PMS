<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  getDashboard,
  updateProject,
  type Project,
  type ProjectDashboard,
  type ProjectStatus
} from '@/api/project'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'

defineOptions({ name: 'ProjectKanban' })

const router = useRouter()
const loading = ref(false)
const dashboard = ref<ProjectDashboard>({})

// 5 列看板
interface KanbanColumn {
  key: string
  canonicalStatus: ProjectStatus | string
  title: string
  mappedStatuses: string[]
}

const columns: KanbanColumn[] = [
  {
    key: 'PLANNING',
    canonicalStatus: 'PLANNING',
    title: '规划中',
    mappedStatuses: ['PENDING', 'APPROVED', 'PLANNING']
  },
  {
    key: 'IN_PROGRESS',
    canonicalStatus: 'IN_PROGRESS',
    title: '执行中',
    mappedStatuses: ['IN_PROGRESS', 'INITIAL_ACCEPTANCE', 'FINAL_ACCEPTANCE', 'EXECUTING', 'CLOSING']
  },
  {
    key: 'SUSPENDED',
    canonicalStatus: 'SUSPENDED',
    title: '已暂停',
    mappedStatuses: ['SUSPENDED']
  },
  {
    key: 'COMPLETED',
    canonicalStatus: 'COMPLETED',
    title: '已完成',
    mappedStatuses: ['COMPLETED', 'CLOSED']
  },
  {
    key: 'CANCELLED',
    canonicalStatus: 'CANCELLED',
    title: '已取消',
    mappedStatuses: ['REJECTED', 'CANCELLED']
  }
]

// 筛选
const filter = reactive({
  keyword: '',
  customer: '',
  manager: ''
})

const allProjects = computed<Project[]>(() => {
  return Object.values(dashboard.value).flat().filter(Boolean) as Project[]
})

const customerOptions = computed(() => {
  const set = new Map<string, string>()
  allProjects.value.forEach((p) => {
    if (p.customerName) set.set(p.customerName, p.customerName)
  })
  return Array.from(set.entries()).map(([value, label]) => ({ value, label }))
})

const managerOptions = computed(() => {
  const set = new Map<string, string>()
  allProjects.value.forEach((p) => {
    if (p.projectManagerName) set.set(p.projectManagerName, p.projectManagerName)
  })
  return Array.from(set.entries()).map(([value, label]) => ({ value, label }))
})

function applyFilter(list: Project[]): Project[] {
  return list.filter((p) => {
    if (filter.customer && p.customerName !== filter.customer) return false
    if (filter.manager && p.projectManagerName !== filter.manager) return false
    if (filter.keyword) {
      const kw = filter.keyword.trim().toLowerCase()
      if (
        !p.projectName?.toLowerCase().includes(kw) &&
        !p.projectCode?.toLowerCase().includes(kw)
      ) {
        return false
      }
    }
    return true
  })
}

function getColumnProjects(col: KanbanColumn): Project[] {
  const list: Project[] = []
  col.mappedStatuses.forEach((s) => {
    const items = dashboard.value[s as ProjectStatus] ?? []
    list.push(...items)
  })
  return applyFilter(list)
}

function getColumnCount(col: KanbanColumn): number {
  return getColumnProjects(col).length
}

const totalCount = computed(() =>
  columns.reduce((sum, col) => sum + getColumnCount(col), 0)
)

function viewDetail(project: Project) {
  if (!project.id) return
  router.push(`/project/detail/${project.id}`)
}

// ============== 拖拽（HTML5 drag API） ==============

const draggingProject = ref<Project | null>(null)
const draggingFromColumn = ref<string>('')
const dragOverColumn = ref<string>('')

function onDragStart(event: DragEvent, project: Project, colKey: string) {
  if (!event.dataTransfer) return
  draggingProject.value = project
  draggingFromColumn.value = colKey
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('text/plain', String(project.id ?? ''))
}

function onDragOver(event: DragEvent, colKey: string) {
  // 阻止默认以允许 drop
  event.preventDefault()
  if (event.dataTransfer) event.dataTransfer.dropEffect = 'move'
  dragOverColumn.value = colKey
}

function onDragLeave(_event: DragEvent, colKey: string) {
  if (dragOverColumn.value === colKey) dragOverColumn.value = ''
}

async function onDrop(event: DragEvent, col: KanbanColumn) {
  event.preventDefault()
  dragOverColumn.value = ''
  const project = draggingProject.value
  draggingProject.value = null
  if (!project || !project.id) return
  if (draggingFromColumn.value === col.key) return // 同列无需处理

  // 校验当前原始状态是否已属于该列
  const originalStatuses = col.mappedStatuses
  if (project.status && originalStatuses.includes(project.status)) return

  // 状态变更确认
  try {
    await ElMessageBox.confirm(
      `确认将项目「${project.projectName}」状态变更为「${col.title}」吗？`,
      '状态变更',
      { type: 'warning', confirmButtonText: '变更', cancelButtonText: '取消' }
    )
  } catch {
    return
  }

  try {
    await updateProject({
      ...project,
      status: col.canonicalStatus as ProjectStatus
    })
    ElMessage.success('项目状态已更新')
    await loadDashboard()
  } catch {
    /* handled by interceptor */
  }
}

// ============== 加载 ==============

async function loadDashboard() {
  loading.value = true
  try {
    dashboard.value = (await getDashboard()) ?? {}
  } catch {
    dashboard.value = {}
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  filter.keyword = ''
  filter.customer = ''
  filter.manager = ''
}

onMounted(loadDashboard)
</script>

<template>
  <div class="kanban-page">
    <PageHeader title="项目看板" description="按状态分列展示，支持拖拽变更状态">
      <template #actions>
        <el-button :icon="Refresh" @click="loadDashboard">刷新</el-button>
      </template>
    </PageHeader>

    <div class="filter-bar">
      <el-input
        v-model="filter.keyword"
        placeholder="项目名 / 编码"
        clearable
        :prefix-icon="Search"
        style="width: 220px"
      />
      <el-select
        v-model="filter.customer"
        placeholder="全部客户"
        clearable
        filterable
        style="width: 200px"
      >
        <el-option v-for="o in customerOptions" :key="o.value" :label="o.label" :value="o.value" />
      </el-select>
      <el-select
        v-model="filter.manager"
        placeholder="全部经理"
        clearable
        filterable
        style="width: 180px"
      >
        <el-option v-for="o in managerOptions" :key="o.value" :label="o.label" :value="o.value" />
      </el-select>
      <el-button link :icon="Refresh" @click="resetFilter">重置</el-button>
      <span class="filter-tip">共 {{ totalCount }} 个项目</span>
    </div>

    <SkeletonCard v-if="loading" :loading="true" :rows="6" />

    <EmptyState
      v-else-if="allProjects.length === 0"
      title="暂无项目"
      description="未获取到项目数据"
    />

    <div v-else class="kanban-board">
      <div
        v-for="col in columns"
        :key="col.key"
        class="kanban-column"
        :class="{ 'drag-over': dragOverColumn === col.key }"
        @dragover="onDragOver($event, col.key)"
        @dragleave="onDragLeave($event, col.key)"
        @drop="onDrop($event, col)"
      >
        <div class="column-header">
          <div class="column-title">
            <ProjectStatusTag :status="col.canonicalStatus" size="small" />
            <span class="column-name">{{ col.title }}</span>
          </div>
          <el-tag size="small" round effect="plain">{{ getColumnCount(col) }}</el-tag>
        </div>
        <div class="column-body">
          <div
            v-for="project in getColumnProjects(col)"
            :key="project.id"
            class="kanban-card"
            draggable="true"
            @dragstart="onDragStart($event, project, col.key)"
            @click="viewDetail(project)"
          >
            <div class="card-title" :title="project.projectName">{{ project.projectName }}</div>
            <div class="card-code">{{ project.projectCode || '-' }}</div>
            <el-progress
              :percentage="Number(project.progress ?? 0)"
              :stroke-width="4"
              :show-text="false"
              class="card-progress"
            />
            <div class="card-progress-text">进度 {{ project.progress ?? 0 }}%</div>
            <div class="card-meta">
              <div class="meta-row">
                <span class="meta-label">客户：</span>
                <span class="meta-value">{{ project.customerName || '-' }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">经理：</span>
                <span class="meta-value">{{ project.projectManagerName || '-' }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">计划结束：</span>
                <span class="meta-value">
                  {{ project.planEndDate ? project.planEndDate.substring(0, 10) : '-' }}
                </span>
              </div>
            </div>
          </div>
          <div v-if="getColumnCount(col) === 0" class="column-empty">
            <el-icon :size="32"><Plus /></el-icon>
            <span>拖拽项目到此列</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.kanban-page {
  padding: 16px 24px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.filter-tip {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-left: auto;
}

.kanban-board {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.kanban-column {
  flex: 0 0 300px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-lg);
  padding: 12px;
  display: flex;
  flex-direction: column;
  min-height: 520px;
  max-height: calc(100vh - 240px);
  border: 2px dashed transparent;
  transition: border-color var(--pms-transition-fast);
}
.kanban-column.drag-over {
  border-color: var(--pms-color-primary);
  background: var(--pms-color-primary-light-9);
}

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--pms-color-border-light);
}
.column-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.column-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}

.column-body {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.kanban-card {
  background: var(--pms-color-bg-card);
  border-radius: var(--pms-radius-md);
  padding: 12px;
  box-shadow: var(--pms-shadow-card);
  cursor: grab;
  transition: all var(--pms-transition-fast);
  border-left: 3px solid transparent;
}
.kanban-card:hover {
  box-shadow: var(--pms-shadow-card-hover);
  transform: translateY(-2px);
}
.kanban-card:active {
  cursor: grabbing;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.card-code {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}
.card-progress {
  margin-bottom: 2px;
}
.card-progress-text {
  font-size: 11px;
  color: var(--pms-color-text-secondary);
  margin-bottom: 8px;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-top: 8px;
  border-top: 1px dashed var(--pms-color-border-light);
}
.meta-row {
  display: flex;
  font-size: 12px;
}
.meta-label {
  color: var(--pms-color-text-placeholder);
  flex-shrink: 0;
}
.meta-value {
  color: var(--pms-color-text-regular);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.column-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  color: var(--pms-color-text-placeholder);
  font-size: 12px;
  border: 1px dashed var(--pms-color-border-light);
  border-radius: var(--pms-radius-md);
  gap: 8px;
}
</style>
