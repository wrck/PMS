<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  approveTask,
  assignOemTask,
  getTaskDetail,
  getTaskProgress,
  getTasksByProject,
  getTaskSubtree,
  moveTask,
  submitForReview,
  type ImplTask,
  type ImplTaskNode,
  type TaskPriority,
  type TaskProgressVO,
  type TaskReviewResult
} from '@/api/implementation'
import { listProjects, type Project } from '@/api/project'
import { listComments, createComment, deleteComment, type TaskCommentItem } from '@/api/task-comment'
import { listActivities, type TaskActivityItem } from '@/api/task-activity'
import TaskTree from '@/components/TaskTree.vue'
import TaskChecklist from '@/components/TaskChecklist.vue'
import { useUserStore } from '@/stores/user'
import type { EpTagType } from '@/types'

defineOptions({ name: 'TaskDetail' })

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const taskId = computed(() => Number(route.params.id))

// ============ 任务详情 ============
const loading = ref(false)
const task = ref<ImplTask | null>(null)
const progressVO = ref<TaskProgressVO | null>(null)

const statusMeta: Record<string, { label: string; tagType: EpTagType }> = {
  PENDING: { label: '待接单', tagType: 'info' },
  ACCEPTED: { label: '已接单', tagType: 'warning' },
  IN_PROGRESS: { label: '进行中', tagType: 'primary' },
  REVIEW: { label: '评审中', tagType: 'warning' },
  COMPLETED: { label: '已完成', tagType: 'success' },
  CONFIRMED: { label: '已确认', tagType: 'success' },
  REJECTED: { label: '已驳回', tagType: 'danger' },
  BLOCKED: { label: '已阻塞', tagType: 'danger' }
}

const priorityOptions: { label: string; value: TaskPriority }[] = [
  { label: '低', value: 'LOW' },
  { label: '中', value: 'MEDIUM' },
  { label: '高', value: 'HIGH' },
  { label: '紧急', value: 'CRITICAL' }
]

function statusLabel(status?: string): string {
  return status ? (statusMeta[status]?.label ?? status) : '-'
}
function statusTagType(status?: string): EpTagType {
  return status ? (statusMeta[status]?.tagType ?? 'info') : 'info'
}
function priorityLabel(priority?: TaskPriority): string {
  return priority ? (priorityOptions.find((p) => p.value === priority)?.label ?? priority) : '-'
}
function priorityTagType(priority?: TaskPriority): EpTagType {
  switch (priority) {
    case 'LOW': return 'info'
    case 'MEDIUM': return 'primary'
    case 'HIGH': return 'warning'
    case 'CRITICAL': return 'danger'
    default: return 'info'
  }
}
function assigneeText(t: ImplTask | null): string {
  if (!t) return '-'
  if (t.taskType === 'AGENT') return t.agentName || '-'
  return t.engineerName || '-'
}
function formatDateTime(s?: string): string {
  if (!s) return '-'
  return s.length > 16 ? s.slice(0, 16).replace('T', ' ') : s
}

// ============ Tab 切换 ============
const activeTab = ref('basic')

// 初始 Tab / Action 来自 query
watch(
  () => route.query,
  (q) => {
    if (q.tab && typeof q.tab === 'string') {
      activeTab.value = q.tab
    }
  },
  { immediate: true }
)

watch(activeTab, (tab) => {
  // 切到对应 Tab 时按需加载数据
  if (tab === 'subtasks' && subtree.value.length === 0) loadSubtree()
  if (tab === 'comments' && comments.value.length === 0) loadComments()
  if (tab === 'activity' && activities.value.length === 0) loadActivities()
  if (tab === 'dependencies' && subtree.value.length === 0) loadSubtree()
})

// ============ 加载任务详情 ============
async function loadTask() {
  loading.value = true
  try {
    task.value = await getTaskDetail(taskId.value)
    // 顺便拉取进度视图（含子任务加权汇总）
    try {
      progressVO.value = await getTaskProgress(taskId.value)
    } catch {
      progressVO.value = null
    }
    // 处理 ?action=add-child
    if (route.query.action === 'add-child') {
      activeTab.value = 'subtasks'
      nextTick(() => handleAddChild())
    }
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

// ============ Tab 1: 基本信息 编辑 ============
const editVisible = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref<FormInstance>()
const projectOptions = ref<Project[]>([])
const editForm = reactive<Partial<ImplTask> & { priority?: TaskPriority }>({})

const editRules: FormRules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }]
}

async function loadProjectOptions() {
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records
  } catch {
    /* handled by interceptor */
  }
}

function handleEdit() {
  if (!task.value) return
  Object.assign(editForm, task.value)
  editVisible.value = true
}

async function handleEditSubmit() {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    editSubmitting.value = true
    try {
      // 复用 assignOemTask（后端按 id 走 update 分支）— 仅基础字段更新
      // 注：实际更新接口应使用专用 PUT /api/implementation/task，此处先复用现有 API 保存基础字段
      await assignOemTask(editForm as ImplTask)
      ElMessage.success('保存成功')
      editVisible.value = false
      await loadTask()
    } catch {
      /* handled by interceptor */
    } finally {
      editSubmitting.value = false
    }
  })
}

// ============ Tab 2: 子任务 ============
const subtree = ref<ImplTaskNode[]>([])
const subtreeRoot = ref<ImplTaskNode | null>(null)

async function loadSubtree() {
  try {
    const all = (await getTaskSubtree(taskId.value)) as ImplTaskNode[]
    subtree.value = all
    // 找到当前任务作为根节点，构建嵌套结构
    const root = all.find((t) => t.id === taskId.value)
    if (root) {
      subtreeRoot.value = buildNestedTree(all, taskId.value)
    } else {
      subtreeRoot.value = null
    }
  } catch {
    subtree.value = []
    subtreeRoot.value = null
  }
}

/** 把扁平子树列表组装成以 rootId 为根的嵌套结构 */
function buildNestedTree(all: ImplTaskNode[], rootId: number): ImplTaskNode | null {
  const map = new Map<number, ImplTaskNode>()
  all.forEach((t) => map.set(t.id!, { ...t, children: [] }))
  let root: ImplTaskNode | null = null
  all.forEach((t) => {
    const node = map.get(t.id!)
    if (!node) return
    if (t.id === rootId) {
      root = node
    } else if (t.parentTaskId && map.has(t.parentTaskId)) {
      map.get(t.parentTaskId)!.children!.push(node)
    }
  })
  // 清理空 children
  const cleanup = (n: ImplTaskNode) => {
    if (n.children && n.children.length === 0) {
      delete n.children
    } else if (n.children) {
      n.children.forEach(cleanup)
    }
  }
  if (root) cleanup(root)
  return root
}

function handleNodeClick(id: number) {
  router.push(`/implementation/task/detail/${id}`)
}

function handleEditNode(id: number) {
  router.push(`/implementation/task/detail/${id}`)
}

// ============ Tab 2: 新增子任务 ============
const addChildVisible = ref(false)
const addChildSubmitting = ref(false)
const addChildFormRef = ref<FormInstance>()
const addChildForm = reactive<Partial<ImplTask>>({
  taskName: '',
  projectId: undefined,
  engineerId: undefined,
  engineerName: '',
  planStartDate: '',
  planEndDate: ''
})

const addChildRules: FormRules = {
  taskName: [{ required: true, message: '请输入子任务名称', trigger: 'blur' }],
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }]
}

function handleAddChild() {
  if (!task.value) return
  Object.assign(addChildForm, {
    taskName: '',
    projectId: task.value.projectId,
    engineerId: undefined,
    engineerName: '',
    planStartDate: '',
    planEndDate: '',
    // parentTaskId 通过后端逻辑识别（此处不直接发，需要后端扩展 assignOemTask 支持 parentId）
    parentTaskId: taskId.value
  } as any)
  addChildVisible.value = true
}

async function handleAddChildSubmit() {
  if (!addChildFormRef.value) return
  await addChildFormRef.value.validate(async (valid) => {
    if (!valid) return
    addChildSubmitting.value = true
    try {
      // 注：assignOemTask 当前不直接接受 parentTaskId，但 ImplTask 实体已有该字段，
      // 后端 insert 时会一并持久化。若后端对 parentTaskId 有特殊处理逻辑，需后端补 DTO。
      await assignOemTask(addChildForm as ImplTask)
      ElMessage.success('子任务已创建')
      addChildVisible.value = false
      await loadSubtree()
    } catch {
      /* handled by interceptor */
    } finally {
      addChildSubmitting.value = false
    }
  })
}

// ============ Tab 3: 检查项 ============
const checklistReadonly = computed(() => {
  const s = task.value?.status
  return s === 'COMPLETED' || s === 'CONFIRMED'
})
const hasUncheckedMandatory = ref(false)
function handleMandatoryChange(val: boolean) {
  hasUncheckedMandatory.value = val
}

// ============ Tab 4: 评论 ============
const comments = ref<TaskCommentItem[]>([])
const commentInput = ref('')
const replyTo = ref<TaskCommentItem | null>(null)
const commentSubmitting = ref(false)

async function loadComments() {
  try {
    comments.value = await listComments(taskId.value)
  } catch {
    comments.value = []
  }
}

/** 把扁平评论列表组装为顶级评论 + 二级回复 */
const nestedComments = computed(() => {
  const map = new Map<number, TaskCommentItem>()
  const roots: TaskCommentItem[] = []
  comments.value.forEach((c) => map.set(c.id!, { ...c, replies: [] } as any))
  comments.value.forEach((c) => {
    const node = map.get(c.id!)
    if (!node) return
    if (c.parentCommentId && map.has(c.parentCommentId)) {
      ;(map.get(c.parentCommentId) as any).replies.push(node)
    } else {
      roots.push(node)
    }
  })
  return roots
})

function isOwnComment(c: TaskCommentItem): boolean {
  return !!userStore.userInfo && c.userId === userStore.userInfo.id
}

function handleReply(c: TaskCommentItem) {
  replyTo.value = c
  commentInput.value = ''
}

function cancelReply() {
  replyTo.value = null
  commentInput.value = ''
}

async function handleAddComment() {
  if (!commentInput.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    const payload: TaskCommentItem = {
      taskId: taskId.value,
      userId: userStore.userInfo?.id,
      userName: userStore.userInfo?.nickname,
      content: commentInput.value.trim(),
      parentCommentId: replyTo.value?.id ?? null
    }
    await createComment(payload)
    ElMessage.success(replyTo.value ? '回复成功' : '评论成功')
    commentInput.value = ''
    replyTo.value = null
    await loadComments()
  } catch {
    /* handled by interceptor */
  } finally {
    commentSubmitting.value = false
  }
}

function handleDeleteComment(c: TaskCommentItem) {
  if (!c.id) return
  ElMessageBox.confirm(`确定删除这条评论吗？`, '删除确认', { type: 'warning' })
    .then(async () => {
      await deleteComment(c.id!)
      ElMessage.success('删除成功')
      await loadComments()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ Tab 5: 活动 ============
const activities = ref<TaskActivityItem[]>([])

const activityTypeMeta: Record<string, { label: string; tagType: EpTagType }> = {
  CREATE: { label: '创建', tagType: 'primary' },
  UPDATE: { label: '更新', tagType: 'info' },
  STATUS_CHANGE: { label: '状态变更', tagType: 'warning' },
  SUBMIT_REVIEW: { label: '提交评审', tagType: 'warning' },
  APPROVE: { label: '验收通过', tagType: 'success' },
  REJECT: { label: '驳回', tagType: 'danger' },
  CHECKLIST_CHECK: { label: '检查项勾选', tagType: 'info' },
  COMMENT: { label: '评论', tagType: 'info' },
  PROGRESS_CHANGE: { label: '进度变更', tagType: 'primary' },
  ASSIGN: { label: '分配', tagType: 'primary' },
  MOVE: { label: '移动', tagType: 'warning' }
}

function activityLabel(type?: string): string {
  return type ? (activityTypeMeta[type]?.label ?? type) : '-'
}
function activityTagType(type?: string): EpTagType {
  return type ? (activityTypeMeta[type]?.tagType ?? 'info') : 'info'
}

async function loadActivities() {
  try {
    activities.value = await listActivities(taskId.value)
  } catch {
    activities.value = []
  }
}

// ============ Tab 6: 依赖 ============
const parentTask = computed<ImplTaskNode | null>(() => {
  if (!task.value?.parentTaskId) return null
  return subtree.value.find((t) => t.id === task.value!.parentTaskId) ?? null
})

const directChildren = computed<ImplTaskNode[]>(() =>
  subtree.value.filter((t) => t.parentTaskId === taskId.value)
)

// ============ 移动任务 ============
const moveVisible = ref(false)
const moveForm = reactive({
  newParentId: undefined as number | undefined
})
const moveCandidates = ref<ImplTaskNode[]>([])

function handleMove() {
  if (!task.value) return
  moveForm.newParentId = task.value.parentTaskId ?? undefined
  // 候选父任务：同项目下的所有任务（不含自身及后代）
  if (task.value.projectId) {
    getTasksByProject(task.value.projectId)
      .then((all) => {
        const myPath = task.value?.taskPath || ''
        moveCandidates.value = (all as ImplTaskNode[]).filter((t) => {
          if (t.id === taskId.value) return false
          if (myPath && t.taskPath && t.taskPath.startsWith(myPath)) return false
          return true
        })
      })
      .catch(() => {
        moveCandidates.value = []
      })
  } else {
    moveCandidates.value = []
  }
  moveVisible.value = true
}

async function handleMoveSubmit() {
  try {
    await moveTask(taskId.value, moveForm.newParentId ?? null)
    ElMessage.success('任务已移动')
    moveVisible.value = false
    await loadTask()
    await loadSubtree()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 顶部操作 ============
async function handleSubmitReview() {
  try {
    const result: TaskReviewResult = await submitForReview(taskId.value)
    if (result.success) {
      ElMessage.success('已提交评审')
      await loadTask()
    } else if (result.errorCode === 'TASK_CHECKLIST_REQUIRED') {
      const items = result.uncheckedMandatoryItems ?? []
      const itemText = items.map((i) => `• ${i.title}`).join('\n')
      ElMessageBox.alert(
        `存在 ${items.length} 项未完成的强制检查项：\n\n${itemText}\n\n请先勾选这些检查项后再提交评审。`,
        '无法提交评审',
        { type: 'warning' }
      )
      activeTab.value = 'checklist'
    } else {
      ElMessage.error(result.errorMessage || '提交评审失败')
    }
  } catch {
    /* handled by interceptor */
  }
}

async function handleApprove() {
  ElMessageBox.confirm(`确定验收任务「${task.value?.taskName}」吗？`, '验收确认', {
    type: 'warning',
    confirmButtonText: '验收通过',
    cancelButtonText: '取消'
  })
    .then(async () => {
      try {
        const result: TaskReviewResult = await approveTask(taskId.value)
        if (result.success) {
          ElMessage.success('任务已验收完成')
          await loadTask()
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

const canSubmitReview = computed(() => {
  const s = task.value?.status
  return s === 'IN_PROGRESS' || s === 'BLOCKED'
})
const canApprove = computed(() => task.value?.status === 'REVIEW')

// ============ 初始化 ============
onMounted(async () => {
  await loadProjectOptions()
  await loadTask()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <!-- 顶部任务概要 -->
    <el-card shadow="never" class="header-card">
      <div class="header-row">
        <el-button :icon="'ArrowLeft'" link @click="router.back()">返回</el-button>
        <h2 class="task-title">{{ task?.taskName }}</h2>
        <el-tag :type="statusTagType(task?.status)" effect="plain">
          {{ statusLabel(task?.status) }}
        </el-tag>
        <el-tag
          v-if="task?.priority"
          :type="priorityTagType(task.priority)"
          size="small"
          effect="plain"
        >
          优先级：{{ priorityLabel(task.priority) }}
        </el-tag>
        <el-tag v-if="task?.taskType" size="small" effect="plain">
          {{ task.taskType === 'AGENT' ? '代理商' : '原厂' }}
        </el-tag>
        <div class="header-actions">
          <el-button v-if="canSubmitReview" type="warning" @click="handleSubmitReview">
            提交评审
          </el-button>
          <el-button v-if="canApprove" type="success" @click="handleApprove">
            验收通过
          </el-button>
          <el-button :icon="'Edit'" @click="handleEdit">编辑</el-button>
          <el-button :icon="'Position'" @click="handleMove">移动任务</el-button>
        </div>
      </div>

      <el-descriptions :column="4" border size="small" class="header-desc">
        <el-descriptions-item label="项目">{{ task?.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ assigneeText(task) }}</el-descriptions-item>
        <el-descriptions-item label="进度">
          <el-progress
            :percentage="progressVO?.rolledUpProgress ?? task?.progress ?? 0"
            :stroke-width="14"
            :text-inside="true"
            style="max-width: 200px"
          />
        </el-descriptions-item>
        <el-descriptions-item label="子任务汇总">
          <span v-if="progressVO">
            {{ progressVO.completedSubtasks }} / {{ progressVO.totalSubtasks }} 完成
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="计划开始">{{ task?.planStartDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划结束">{{ task?.planEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际开始">{{ task?.actualStart || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际结束">{{ task?.actualEnd || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 6 Tab -->
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <!-- 1. 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border v-if="task">
            <el-descriptions-item label="任务ID">{{ task.id }}</el-descriptions-item>
            <el-descriptions-item label="任务名称">{{ task.taskName }}</el-descriptions-item>
            <el-descriptions-item label="任务类型">
              {{ task.taskType === 'AGENT' ? '代理商' : '原厂' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              {{ statusLabel(task.status) }}
            </el-descriptions-item>
            <el-descriptions-item label="项目">{{ task.projectName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="里程碑ID">{{ task.milestoneId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="工程师ID">{{ task.engineerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="工程师姓名">{{ task.engineerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="代理商ID">{{ task.agentId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="代理商名称">{{ task.agentName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="优先级">{{ priorityLabel(task.priority) }}</el-descriptions-item>
            <el-descriptions-item label="进度">{{ task.progress ?? 0 }}%</el-descriptions-item>
            <el-descriptions-item label="父任务ID">{{ task.parentTaskId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="任务路径">{{ task.taskPath || '-' }}</el-descriptions-item>
            <el-descriptions-item label="层级深度">{{ task.depth ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="阶段ID">{{ task.phaseId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="实际工时">{{ task.actualHours ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="剩余工时">{{ task.remainingHours ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="任务权重">{{ task.taskWeight ?? 1 }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(task.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ task.description || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ task.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 2. 子任务 -->
        <el-tab-pane label="子任务" name="subtasks">
          <div class="tab-toolbar">
            <el-button type="primary" :icon="'Plus'" @click="handleAddChild">新增子任务</el-button>
            <el-button :icon="'Refresh'" @click="loadSubtree">刷新</el-button>
          </div>
          <div v-if="subtreeRoot" class="subtree-container">
            <TaskTree
              :task="subtreeRoot"
              :default-expanded="true"
              @node-click="handleNodeClick"
              @edit="handleEditNode"
              @add-child="handleAddChild"
            />
          </div>
          <el-empty v-else description="暂无子任务" />
        </el-tab-pane>

        <!-- 3. 检查项 -->
        <el-tab-pane label="检查项" name="checklist">
          <TaskChecklist
            v-if="task"
            :task-id="taskId"
            :readonly="checklistReadonly"
            @mandatory-change="handleMandatoryChange"
          />
          <el-alert
            v-if="hasUncheckedMandatory && canSubmitReview"
            type="warning"
            :closable="false"
            show-icon
            title="存在未完成的强制检查项，提交评审将被拦截"
            description="请先勾选所有标记为「强制」的检查项，再点击「提交评审」按钮。"
            class="checklist-alert"
          />
        </el-tab-pane>

        <!-- 4. 评论 -->
        <el-tab-pane label="评论" name="comments">
          <div class="comment-input-area">
            <div v-if="replyTo" class="reply-banner">
              回复 @{{ replyTo.userName || '匿名' }}：
              <span class="reply-content">{{ replyTo.content }}</span>
              <el-button link type="primary" size="small" @click="cancelReply">取消回复</el-button>
            </div>
            <el-input
              v-model="commentInput"
              type="textarea"
              :rows="3"
              placeholder="请输入评论内容"
              maxlength="1000"
              show-word-limit
            />
            <div class="comment-actions">
              <el-button
                type="primary"
                :loading="commentSubmitting"
                :disabled="!commentInput.trim()"
                @click="handleAddComment"
              >
                {{ replyTo ? '回复' : '评论' }}
              </el-button>
            </div>
          </div>

          <div class="comment-list">
            <div v-for="c in nestedComments" :key="c.id" class="comment-item">
              <div class="comment-main">
                <div class="comment-header">
                  <span class="comment-author">{{ c.userName || '匿名' }}</span>
                  <span class="comment-time">{{ formatDateTime(c.createTime) }}</span>
                </div>
                <div class="comment-content">{{ c.content }}</div>
                <div class="comment-ops">
                  <el-button link type="primary" size="small" @click="handleReply(c)">回复</el-button>
                  <el-button
                    v-if="isOwnComment(c)"
                    link
                    type="danger"
                    size="small"
                    @click="handleDeleteComment(c)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
              <!-- 二级回复 -->
              <div v-if="(c as any).replies?.length" class="reply-list">
                <div v-for="r in (c as any).replies" :key="r.id" class="comment-item reply-item">
                  <div class="comment-main">
                    <div class="comment-header">
                      <span class="comment-author">{{ r.userName || '匿名' }}</span>
                      <span class="comment-time">{{ formatDateTime(r.createTime) }}</span>
                      <span v-if="r.userName" class="reply-to">
                        回复 @{{ c.userName || '匿名' }}
                      </span>
                    </div>
                    <div class="comment-content">{{ r.content }}</div>
                    <div class="comment-ops">
                      <el-button
                        v-if="isOwnComment(r)"
                        link
                        type="danger"
                        size="small"
                        @click="handleDeleteComment(r)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="nestedComments.length === 0" description="暂无评论" />
          </div>
        </el-tab-pane>

        <!-- 5. 活动 -->
        <el-tab-pane label="活动" name="activity">
          <el-timeline v-if="activities.length">
            <el-timeline-item
              v-for="a in activities"
              :key="a.id"
              :timestamp="formatDateTime(a.createTime)"
              placement="top"
            >
              <el-card shadow="hover" class="activity-card">
                <div class="activity-header">
                  <el-tag :type="activityTagType(a.activityType)" size="small" effect="plain">
                    {{ activityLabel(a.activityType) }}
                  </el-tag>
                  <span class="activity-user">{{ a.userName || '系统' }}</span>
                </div>
                <div v-if="a.content" class="activity-content">{{ a.content }}</div>
                <div v-if="a.metadata" class="activity-metadata">
                  <pre>{{ a.metadata }}</pre>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无活动记录" />
        </el-tab-pane>

        <!-- 6. 依赖 -->
        <el-tab-pane label="依赖" name="dependencies">
          <div class="dependencies-section">
            <h4>父任务</h4>
            <div v-if="parentTask" class="dep-item">
              <el-link type="primary" @click="handleNodeClick(parentTask.id!)">
                {{ parentTask.taskName }}
              </el-link>
              <el-tag :type="statusTagType(parentTask.status)" size="small" effect="plain">
                {{ statusLabel(parentTask.status) }}
              </el-tag>
              <el-progress
                :percentage="parentTask.progress ?? 0"
                :stroke-width="10"
                style="max-width: 200px"
              />
            </div>
            <el-empty v-else description="无父任务（顶层任务）" :image-size="60" />

            <h4>直接子任务（{{ directChildren.length }}）</h4>
            <el-table v-if="directChildren.length" :data="directChildren" border size="small">
              <el-table-column prop="taskName" label="任务名称" min-width="200">
                <template #default="{ row }">
                  <el-link type="primary" @click="handleNodeClick(row.id)">{{ row.taskName }}</el-link>
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
                  <el-progress :percentage="row.progress ?? 0" :stroke-width="10" :text-inside="true" />
                </template>
              </el-table-column>
              <el-table-column label="优先级" width="80" align="center">
                <template #default="{ row }">{{ priorityLabel(row.priority) }}</template>
              </el-table-column>
              <el-table-column label="负责人" min-width="100">
                <template #default="{ row }">{{ assigneeText(row) }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无子任务" :image-size="60" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑任务弹窗 -->
    <el-dialog v-model="editVisible" title="编辑任务" width="640px" destroy-on-close>
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="editForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="editForm.projectId" placeholder="请选择项目" filterable style="width: 100%">
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="editForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option
              v-for="p in priorityOptions"
              :key="p.value"
              :label="p.label"
              :value="p.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工程师ID">
          <el-input-number v-model="editForm.engineerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="工程师姓名">
          <el-input v-model="editForm.engineerName" placeholder="请输入工程师姓名" />
        </el-form-item>
        <el-form-item label="计划开始">
          <el-date-picker
            v-model="editForm.planStartDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计划结束">
          <el-date-picker
            v-model="editForm.planEndDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新增子任务弹窗 -->
    <el-dialog v-model="addChildVisible" title="新增子任务" width="560px" destroy-on-close>
      <el-form
        ref="addChildFormRef"
        :model="addChildForm"
        :rules="addChildRules"
        label-width="100px"
      >
        <el-form-item label="子任务名称" prop="taskName">
          <el-input v-model="addChildForm.taskName" placeholder="请输入子任务名称" />
        </el-form-item>
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="addChildForm.projectId" placeholder="请选择项目" filterable style="width: 100%">
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工程师ID">
          <el-input-number v-model="addChildForm.engineerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="工程师姓名">
          <el-input v-model="addChildForm.engineerName" placeholder="请输入工程师姓名" />
        </el-form-item>
        <el-form-item label="计划开始">
          <el-date-picker
            v-model="addChildForm.planStartDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计划结束">
          <el-date-picker
            v-model="addChildForm.planEndDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划结束日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addChildVisible = false">取消</el-button>
        <el-button type="primary" :loading="addChildSubmitting" @click="handleAddChildSubmit">创建</el-button>
      </template>
    </el-dialog>

    <!-- 移动任务弹窗 -->
    <el-dialog v-model="moveVisible" title="移动任务" width="520px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="当前任务">
          <span>{{ task?.taskName }}</span>
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
              v-for="t in moveCandidates"
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
.header-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.header-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
}
.task-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  min-width: 200px;
}
.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.header-desc {
  margin-top: 8px;
}
.tab-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.subtree-container {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 8px;
}
.checklist-alert {
  margin-top: 12px;
}
.comment-input-area {
  margin-bottom: 20px;
}
.reply-banner {
  background: var(--el-fill-color-light);
  padding: 8px 12px;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.reply-content {
  color: #606266;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.comment-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.comment-item {
  padding: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: #fafafa;
}
.comment-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}
.comment-author {
  font-weight: 600;
  color: var(--el-color-primary);
}
.comment-time {
  color: #909399;
  font-size: 12px;
}
.reply-to {
  color: #909399;
  font-size: 12px;
}
.comment-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  white-space: pre-wrap;
}
.comment-ops {
  display: flex;
  gap: 8px;
}
.reply-list {
  margin-top: 12px;
  margin-left: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  border-left: 2px solid var(--el-border-color-lighter);
  padding-left: 12px;
}
.reply-item {
  background: #fff;
  padding: 8px 12px;
}
.activity-card :deep(.el-card__body) {
  padding: 10px 14px;
}
.activity-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.activity-user {
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}
.activity-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}
.activity-metadata pre {
  margin: 6px 0 0;
  padding: 6px 8px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-all;
}
.dependencies-section h4 {
  margin: 16px 0 8px;
  font-size: 14px;
  color: #303133;
}
.dep-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 4px;
}
</style>
