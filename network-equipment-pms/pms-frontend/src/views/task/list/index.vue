<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveTask,
  getTasksByProject,
  listTasks,
  moveTask,
  submitForReview,
  type ImplTaskNode,
  type TaskPriority,
  type TaskStatus,
  type TaskReviewResult
} from '@/api/implementation'
import { listProjects, type Project } from '@/api/project'
import type { EpTagType } from '@/types'

defineOptions({ name: 'TaskList' })

const router = useRouter()

// ============ 筛选 ============
const loading = ref(false)
const treeData = ref<ImplTaskNode[]>([])
const projectOptions = ref<Project[]>([])
const query = reactive({
  projectId: undefined as number | undefined,
  status: undefined as TaskStatus | undefined,
  taskType: undefined as 'OEM' | 'AGENT' | undefined,
  priority: undefined as TaskPriority | undefined,
  page: 1,
  size: 10
})
const total = ref(0)

const statusOptions: { label: string; value: TaskStatus | 'REVIEW' | 'BLOCKED' }[] = [
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

const priorityTagTypeMap: Record<TaskPriority, EpTagType> = {
  LOW: 'info',
  MEDIUM: 'primary',
  HIGH: 'warning',
  CRITICAL: 'danger'
}

function statusLabel(status?: string): string {
  return statusOptions.find((s) => s.value === status)?.label ?? status ?? '-'
}

function statusTagType(status?: string): EpTagType {
  return status ? (statusTagTypeMap[status] ?? 'info') : 'info'
}

function priorityLabel(priority?: TaskPriority): string {
  return priority ? (priorityOptions.find((p) => p.value === priority)?.label ?? priority) : '-'
}

function priorityTagType(priority?: TaskPriority): EpTagType {
  return priority ? (priorityTagTypeMap[priority] ?? 'info') : 'info'
}

function assigneeText(row: ImplTaskNode): string {
  if (row.taskType === 'AGENT') return row.agentName || '-'
  return row.engineerName || '-'
}

// ============ 数据加载 ============
/**
 * 构建嵌套树：根据 parentTaskId 把扁平列表组装为 children 嵌套结构。
 * 顶层任务：parentTaskId 为 null/undefined/0。
 */
function buildTree(tasks: ImplTaskNode[]): ImplTaskNode[] {
  const map = new Map<number, ImplTaskNode>()
  const roots: ImplTaskNode[] = []
  // 第一遍：建立 id -> node 映射，并初始化 children
  tasks.forEach((t) => {
    map.set(t.id!, { ...t, children: [] })
  })
  // 第二遍：根据 parentTaskId 挂载到父节点的 children，无父则入 roots
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

async function loadProjects() {
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records
  } catch {
    /* handled by interceptor */
  }
}

async function loadData() {
  loading.value = true
  try {
    if (query.projectId) {
      // 项目模式：加载该项目全部任务，客户端构建嵌套树
      const all = await getTasksByProject(query.projectId)
      let filtered: ImplTaskNode[] = all as ImplTaskNode[]
      if (query.status) filtered = filtered.filter((t) => t.status === query.status)
      if (query.taskType) filtered = filtered.filter((t) => t.taskType === query.taskType)
      if (query.priority) filtered = filtered.filter((t) => t.priority === query.priority)
      treeData.value = buildTree(filtered)
      total.value = treeData.value.length
    } else {
      // 全局模式：分页返回扁平任务，按 parentTaskId==null 过滤为顶层
      const res = await listTasks({
        page: query.page,
        size: query.size,
        status: query.status,
        taskType: query.taskType
      } as any)
      const topLevel = (res.records as ImplTaskNode[]).filter(
        (t) => !t.parentTaskId
      )
      treeData.value = buildTree(topLevel)
      total.value = res.total
    }
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.projectId = undefined
  query.status = undefined
  query.taskType = undefined
  query.priority = undefined
  query.page = 1
  loadData()
}

function handlePageChange(p: number) {
  query.page = p
  loadData()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadData()
}

watch(() => query.projectId, () => {
  query.page = 1
})

// ============ 行操作 ============
function handleViewDetail(row: ImplTaskNode) {
  if (!row.id) return
  router.push(`/implementation/task/detail/${row.id}`)
}

function handleAddChild(row: ImplTaskNode) {
  if (!row.id) return
  router.push(`/implementation/task/detail/${row.id}?action=add-child`)
}

async function handleSubmitReview(row: ImplTaskNode) {
  if (!row.id) return
  try {
    const result: TaskReviewResult = await submitForReview(row.id)
    if (result.success) {
      ElMessage.success('已提交评审')
      loadData()
    } else if (result.errorCode === 'TASK_CHECKLIST_REQUIRED') {
      // 强制检查项未通过：弹出未完成清单
      const items = result.uncheckedMandatoryItems ?? []
      const itemText = items.map((i) => `• ${i.title}`).join('\n')
      ElMessageBox.alert(
        `存在 ${items.length} 项未完成的强制检查项：\n\n${itemText}\n\n请先勾选这些检查项后再提交评审。`,
        '无法提交评审',
        { type: 'warning', confirmButtonText: '去检查项 Tab 处理' }
      ).then(() => {
        router.push(`/implementation/task/detail/${row.id}?tab=checklist`)
      }).catch(() => {
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
  // 候选父任务：同项目下的所有任务（不含自身及后代，由后端做环路校验）
  if (query.projectId) {
    getTasksByProject(query.projectId)
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

const canSubmitReview = (status?: string) => status === 'IN_PROGRESS' || status === 'BLOCKED'
const canApprove = (status?: string) => status === 'REVIEW'

onMounted(async () => {
  await loadProjects()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">任务树列表</span>
        <span class="page-subtitle">（按项目查看任务层级，可展开/折叠子任务）</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目">
          <el-select
            v-model="query.projectId"
            placeholder="全部项目（仅顶层任务）"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.taskType" placeholder="全部类型" clearable style="width: 130px">
            <el-option label="原厂" value="OEM" />
            <el-option label="代理商" value="AGENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option
              v-for="s in statusOptions"
              :key="s.value"
              :label="s.label"
              :value="s.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="query.priority" placeholder="全部优先级" clearable style="width: 130px">
            <el-option
              v-for="p in priorityOptions"
              :key="p.value"
              :label="p.label"
              :value="p.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Refresh'" @click="loadData">刷新</el-button>
        <span v-if="query.projectId" class="toolbar-tip">
          当前展示项目全部任务的层级树（已按筛选条件过滤）
        </span>
        <span v-else class="toolbar-tip">
          未选择项目：仅展示顶层任务，子任务请在详情页查看
        </span>
      </div>

      <el-table
        v-loading="loading"
        :data="treeData"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
      >
        <el-table-column prop="taskName" label="任务名称" min-width="240" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目" min-width="160" show-overflow-tooltip />
        <el-table-column label="负责人" min-width="120">
          <template #default="{ row }">{{ assigneeText(row) }}</template>
        </el-table-column>
        <el-table-column label="优先级" width="90" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.priority"
              :type="priorityTagType(row.priority)"
              size="small"
              effect="plain"
            >
              {{ priorityLabel(row.priority) }}
            </el-tag>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="180">
          <template #default="{ row }">
            <el-progress :percentage="row.progress ?? 0" :stroke-width="14" :text-inside="true" />
          </template>
        </el-table-column>
        <el-table-column prop="planStartDate" label="计划开始" min-width="110" />
        <el-table-column prop="planEndDate" label="计划结束" min-width="110" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">详情</el-button>
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
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无任务数据" />
        </template>
      </el-table>

      <el-pagination
        v-if="!query.projectId"
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

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
.page-subtitle {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.toolbar-tip {
  font-size: 12px;
  color: #909399;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
