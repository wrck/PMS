<script setup lang="ts">
// =============================================================================
// TaskDetail - 任务详情页（Task 9 重做）
// -----------------------------------------------------------------------------
// 布局：左侧主区 (70%) + 右侧侧栏 (30%)
//   主区：PageHeader + 基本信息 + 任务进度 + 检查项 + 评论 + 活动时间轴
//   侧栏：任务状态 + 父子任务 + 依赖关系 + 附件 + 操作历史
// 引用：PageHeader / SkeletonCard / EmptyState / TaskPriorityTag / TaskChecklist
// API ：implementation / task-checklist / task-comment / task-activity / task-dependency
// =============================================================================
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules
} from 'element-plus'
import {
  approveTask,
  assignAgentTask,
  assignOemTask,
  completeTask,
  getTaskDetail,
  getTaskProgress,
  getTasksByProject,
  getTaskSubtree,
  moveTask,
  startTask,
  submitForReview,
  type ImplTask,
  type ImplTaskNode,
  type TaskPriority,
  type TaskProgressVO,
  type TaskReviewResult,
  type TaskStatus
} from '@/api/implementation'
import { listProjects, type Project } from '@/api/project'
import {
  createComment,
  deleteComment,
  listComments,
  type TaskCommentItem
} from '@/api/task-comment'
import { listActivities, type TaskActivityItem } from '@/api/task-activity'
import {
  deleteDependency,
  listDependencies,
  saveDependency,
  type DependencyType,
  type TaskDependency
} from '@/api/task-dependency'
import { useUserStore } from '@/stores/user'
import type { EpTagType } from '@/types'
import type { MentionUser } from '@/api/system'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import TaskPriorityTag from '@/components/common/TaskPriorityTag.vue'
import UserSelect from '@/components/common/UserSelect.vue'
import FileUploader from '@/components/FileUploader/index.vue'
import TaskChecklist from '@/components/TaskChecklist.vue'
import {
  attachmentDownloadUrl,
  deleteAttachment,
  formatFileSize,
  formatUploadTime,
  listAttachmentsByBiz,
  type Attachment
} from '@/api/attachment'

defineOptions({ name: 'TaskDetail' })

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const taskId = computed(() => Number(route.params.id))

// ============ 状态/优先级元信息 ============
const statusMeta: Record<TaskStatus, { label: string; tagType: EpTagType }> = {
  PENDING: { label: '待接单', tagType: 'info' },
  ACCEPTED: { label: '已接单', tagType: 'warning' },
  IN_PROGRESS: { label: '进行中', tagType: 'primary' },
  REVIEW: { label: '评审中', tagType: 'warning' },
  BLOCKED: { label: '已阻塞', tagType: 'danger' },
  COMPLETED: { label: '已完成', tagType: 'success' },
  CONFIRMED: { label: '已确认', tagType: 'success' },
  REJECTED: { label: '已驳回', tagType: 'danger' }
}

const priorityOptions: { label: string; value: TaskPriority }[] = [
  { label: '低', value: 'LOW' },
  { label: '中', value: 'MEDIUM' },
  { label: '高', value: 'HIGH' },
  { label: '紧急', value: 'CRITICAL' }
]

const dependencyTypeOptions: { label: string; value: DependencyType; desc: string }[] = [
  { label: 'FS', value: 'FS', desc: '完成-开始' },
  { label: 'FF', value: 'FF', desc: '完成-完成' },
  { label: 'SS', value: 'SS', desc: '开始-开始' },
  { label: 'SF', value: 'SF', desc: '开始-完成' }
]

function statusLabel(status?: string): string {
  return status ? (statusMeta[status as TaskStatus]?.label ?? status) : '-'
}
function statusTagType(status?: string): EpTagType {
  return status ? (statusMeta[status as TaskStatus]?.tagType ?? 'info') : 'info'
}
function priorityLabel(priority?: TaskPriority): string {
  return priority ? (priorityOptions.find((p) => p.value === priority)?.label ?? priority) : '-'
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
function formatDate(s?: string): string {
  if (!s) return '-'
  return s.length > 10 ? s.slice(0, 10) : s
}

// ============ 任务详情 ============
const loading = ref(false)
const task = ref<ImplTask | null>(null)
const progressVO = ref<TaskProgressVO | null>(null)
const parentTask = ref<ImplTask | null>(null)
const subtree = ref<ImplTaskNode[]>([])

// ============ 子任务（直接子任务） ============
const directChildren = computed<ImplTaskNode[]>(() =>
  subtree.value.filter((t) => t.parentTaskId === taskId.value)
)

// ============ 进度派生指标 ============
/** 计划工时：实际工时 + 剩余工时（若任一缺失则返回 undefined） */
const plannedHours = computed<number | undefined>(() => {
  const a = task.value?.actualHours
  const r = task.value?.remainingHours
  if (a == null && r == null) return undefined
  return (a ?? 0) + (r ?? 0)
})

/** 工时消耗率：actualHours / plannedHours * 100 */
const hoursBurnRate = computed<number>(() => {
  const p = plannedHours.value
  const a = task.value?.actualHours ?? 0
  if (!p || p <= 0) return 0
  return Math.min(100, Math.round((a / p) * 100))
})

/** 子任务完成率 */
const subtaskCompletionRate = computed<number>(() => {
  if (!progressVO.value || progressVO.value.totalSubtasks === 0) return 0
  return Math.round(
    (progressVO.value.completedSubtasks / progressVO.value.totalSubtasks) * 100
  )
})

/** 检查项完成率（依赖 TaskChecklist 内部状态，此处用 task.progress 估算） */
const overallProgress = computed<number>(
  () => progressVO.value?.rolledUpProgress ?? task.value?.progress ?? 0
)

// ============ 加载任务详情 ============
/** 是否为新建模式：taskId=0 且 query.action=create */
const isCreateMode = computed(
  () => taskId.value === 0 && route.query.action === 'create'
)

/** 从 URL 查询参数初始化一个空任务（用于新建模式） */
function buildEmptyTaskFromQuery(): ImplTask {
  const projectIdRaw = route.query.projectId
  const projectId =
    projectIdRaw != null && projectIdRaw !== ''
      ? Number(projectIdRaw)
      : NaN
  const taskName =
    typeof route.query.taskName === 'string'
      ? decodeURIComponent(route.query.taskName)
      : ''
  const priority =
    typeof route.query.priority === 'string'
      ? (route.query.priority as TaskPriority)
      : undefined
  return {
    id: undefined,
    taskName,
    taskType: 'OEM',
    projectId: Number.isNaN(projectId) ? (0 as unknown as number) : projectId,
    status: 'PENDING',
    progress: 0,
    priority,
    parentTaskId: null,
    description: '',
    remark: ''
  }
}

async function loadTask() {
  // 新建模式：跳过后端加载，直接用查询参数初始化空任务并打开编辑弹窗
  if (isCreateMode.value) {
    task.value = buildEmptyTaskFromQuery()
    progressVO.value = null
    parentTask.value = null
    subtree.value = []
    comments.value = []
    activities.value = []
    dependencies.value = []
    attachments.value = []
    loading.value = false
    nextTick(() => handleEdit())
    return
  }

  loading.value = true
  try {
    task.value = await getTaskDetail(taskId.value)
    try {
      progressVO.value = await getTaskProgress(taskId.value)
    } catch {
      progressVO.value = null
    }
    // 并行加载：父任务 / 子树 / 评论 / 活动 / 依赖 / 附件
    const parallel: Promise<unknown>[] = [
      loadSubtree(),
      loadComments(),
      loadActivities(),
      loadAttachments()
    ]
    if (task.value.parentTaskId) {
      parallel.push(
        getTaskDetail(task.value.parentTaskId)
          .then((p) => {
            parentTask.value = p
          })
          .catch(() => {
            parentTask.value = null
          })
      )
    } else {
      parentTask.value = null
    }
    if (task.value.projectId) {
      parallel.push(loadDependencies(task.value.projectId))
    }
    await Promise.allSettled(parallel)
    // 处理 ?action=add-child
    if (route.query.action === 'add-child') {
      nextTick(() => handleAddChild())
    }
  } catch {
    task.value = null
  } finally {
    loading.value = false
  }
}

// ============ 子树 ============
async function loadSubtree() {
  try {
    const all = (await getTaskSubtree(taskId.value)) as ImplTaskNode[]
    subtree.value = all
  } catch {
    subtree.value = []
  }
}

function handleNodeClick(id: number) {
  router.push(`/implementation/task/detail/${id}`)
}

// ============ 检查项：强制检查项拦截 ============
const checklistReadonly = computed(() => {
  const s = task.value?.status
  return s === 'COMPLETED' || s === 'CONFIRMED'
})
const hasUncheckedMandatory = ref(false)
function handleMandatoryChange(val: boolean) {
  hasUncheckedMandatory.value = val
}

// ============ 评论 ============
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
  const map = new Map<number, TaskCommentItem & { replies: TaskCommentItem[] }>()
  const roots: (TaskCommentItem & { replies: TaskCommentItem[] })[] = []
  comments.value.forEach((c) => map.set(c.id!, { ...c, replies: [] }))
  comments.value.forEach((c) => {
    const node = map.get(c.id!)
    if (!node) return
    if (c.parentCommentId && map.has(c.parentCommentId)) {
      map.get(c.parentCommentId)!.replies.push(node)
    } else {
      roots.push(node)
    }
  })
  // 按时间倒序
  roots.sort((a, b) => (b.createTime || '').localeCompare(a.createTime || ''))
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
  ElMessageBox.confirm('确定删除这条评论吗？', '删除确认', { type: 'warning' })
    .then(async () => {
      await deleteComment(c.id!)
      ElMessage.success('删除成功')
      await loadComments()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ 活动记录 ============
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

/** 状态流转历史（仅状态相关活动） */
const statusHistory = computed<TaskActivityItem[]>(() =>
  activities.value.filter((a) =>
    ['CREATE', 'STATUS_CHANGE', 'SUBMIT_REVIEW', 'APPROVE', 'REJECT'].includes(a.activityType)
  )
)

/** 操作历史卡片：最近 10 条 */
const recentActivities = computed<TaskActivityItem[]>(() => activities.value.slice(0, 10))

// ============ 依赖关系 ============
const dependencies = ref<TaskDependency[]>([])
const projectTasks = ref<ImplTaskNode[]>([])

async function loadDependencies(projectId: number) {
  try {
    const [deps, tasks] = await Promise.all([
      listDependencies(projectId),
      getTasksByProject(projectId)
    ])
    dependencies.value = deps
    projectTasks.value = tasks as ImplTaskNode[]
  } catch {
    dependencies.value = []
    projectTasks.value = []
  }
}

function taskNameById(id: number): string {
  return projectTasks.value.find((t) => t.id === id)?.taskName ?? `#${id}`
}

/** 前置任务：当前任务为后置（successorTaskId === taskId） */
const predecessors = computed<TaskDependency[]>(() =>
  dependencies.value.filter((d) => d.successorTaskId === taskId.value)
)

/** 后置任务：当前任务为前置（predecessorTaskId === taskId） */
const successors = computed<TaskDependency[]>(() =>
  dependencies.value.filter((d) => d.predecessorTaskId === taskId.value)
)

function depTypeLabel(t?: DependencyType): string {
  return t ? (dependencyTypeOptions.find((o) => o.value === t)?.desc ?? t) : '-'
}

// ============ 附件（持久化到后端 /api/file） ============
// 任务附件 bizType 约定为 'TASK'，bizId 为任务 id；新建模式（id=0）下隐藏上传入口
const TASK_ATTACHMENT_BIZ_TYPE = 'TASK'
const attachments = ref<Attachment[]>([])
const attachmentLoading = ref(false)

async function loadAttachments() {
  const id = taskId.value
  if (!id || id <= 0) {
    attachments.value = []
    return
  }
  attachmentLoading.value = true
  try {
    attachments.value = (await listAttachmentsByBiz(TASK_ATTACHMENT_BIZ_TYPE, id)) ?? []
  } catch {
    attachments.value = []
  } finally {
    attachmentLoading.value = false
  }
}

// FileUploader 上传成功回调：刷新附件列表
async function handleAttachmentUploaded() {
  await loadAttachments()
}

function handleDownloadAttachment(att: Attachment) {
  if (!att.id) return
  window.open(attachmentDownloadUrl(att.id), '_blank')
}

function handleDeleteAttachment(att: Attachment) {
  if (!att.id) return
  ElMessageBox.confirm(
    `确认删除附件「${att.fileName ?? ''}」吗？`,
    '删除确认',
    { type: 'warning' }
  )
    .then(async () => {
      await deleteAttachment(att.id!)
      ElMessage.success('附件已删除')
      await loadAttachments()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ 顶部操作（开始 / 提交评审 / 验收完成 / 编辑 / 移动） ============
const canStart = computed(() => {
  const s = task.value?.status
  return s === 'PENDING' || s === 'ACCEPTED'
})
const canSubmitReview = computed(() => {
  const s = task.value?.status
  return s === 'IN_PROGRESS' || s === 'BLOCKED'
})
const canApprove = computed(() => task.value?.status === 'REVIEW')
const canComplete = computed(() => task.value?.status === 'IN_PROGRESS')
const canEdit = computed(() => {
  const s = task.value?.status
  return s !== 'COMPLETED' && s !== 'CONFIRMED'
})

async function handleStart() {
  try {
    await startTask(taskId.value)
    ElMessage.success('任务已开始')
    await loadTask()
  } catch {
    /* handled by interceptor */
  }
}

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
    } else {
      ElMessage.error(result.errorMessage || '提交评审失败')
    }
  } catch {
    /* handled by interceptor */
  }
}

async function handleApprove() {
  try {
    await ElMessageBox.confirm(
      `确定验收任务「${task.value?.taskName}」吗？`,
      '验收确认',
      { type: 'warning', confirmButtonText: '验收通过', cancelButtonText: '取消' }
    )
  } catch {
    return /* cancelled */
  }
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
}

async function handleComplete() {
  try {
    await ElMessageBox.confirm(
      `确定完成任务「${task.value?.taskName}」吗？`,
      '完成确认',
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await completeTask(taskId.value)
    ElMessage.success('任务已完成')
    await loadTask()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 编辑任务 ============
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

// 工程师选择回调：同步 ID + 姓名
function onEditEngineerChange(user: MentionUser | null) {
  if (user) {
    editForm.engineerId = user.id
    editForm.engineerName = user.realName || user.username
  } else {
    editForm.engineerId = undefined
    editForm.engineerName = ''
  }
}

function onAddChildEngineerChange(user: MentionUser | null) {
  if (user) {
    addChildForm.engineerId = user.id
    addChildForm.engineerName = user.realName || user.username
  } else {
    addChildForm.engineerId = undefined
    addChildForm.engineerName = ''
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
        const isAgentTask = (editForm as ImplTask).taskType === 'AGENT'
        const created = isAgentTask
          ? await assignAgentTask(editForm as ImplTask)
          : await assignOemTask(editForm as ImplTask)
        ElMessage.success('保存成功')
      editVisible.value = false
      // 新建模式：跳转到新任务的详情页（脱离 ?action=create 状态）
      if (isCreateMode.value && created?.id) {
        router.replace(`/implementation/task/detail/${created.id}`)
        return
      }
      await loadTask()
    } catch {
      /* handled by interceptor */
    } finally {
      editSubmitting.value = false
    }
  })
}

// ============ 新增子任务 ============
const addChildVisible = ref(false)
const addChildSubmitting = ref(false)
const addChildFormRef = ref<FormInstance>()
const addChildForm = reactive<Partial<ImplTask>>({
  taskName: '',
  taskType: 'OEM',
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
  // 显式清空 id，确保走「新建」分支（assignOemTask 后端按 id 是否存在区分创建/更新）
  Object.assign(addChildForm, {
    id: undefined,
    taskName: '',
    taskType: 'OEM',
    projectId: task.value.projectId,
    engineerId: undefined,
    engineerName: '',
    planStartDate: '',
    planEndDate: '',
    parentTaskId: taskId.value
  } as Partial<ImplTask>)
  addChildVisible.value = true
}

async function handleAddChildSubmit() {
  if (!addChildFormRef.value) return
  await addChildFormRef.value.validate(async (valid) => {
    if (!valid) return
    addChildSubmitting.value = true
    try {
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

// ============ 移动任务 ============
const moveVisible = ref(false)
const moveForm = reactive({ newParentId: undefined as number | undefined })
const moveCandidates = ref<ImplTaskNode[]>([])

function handleMove() {
  if (!task.value) return
  moveForm.newParentId = task.value.parentTaskId ?? undefined
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
  } catch {
    /* handled by interceptor */
  }
}

// ============ 添加依赖 ============
const addDepVisible = ref(false)
const addDepSubmitting = ref(false)
const addDepFormRef = ref<FormInstance>()
const addDepForm = reactive<{
  direction: 'predecessor' | 'successor'
  relatedTaskId: number | undefined
  dependencyType: DependencyType
  lagDays: number
}>({
  direction: 'predecessor',
  relatedTaskId: undefined,
  dependencyType: 'FS',
  lagDays: 0
})

const addDepRules: FormRules = {
  relatedTaskId: [{ required: true, message: '请选择关联任务', trigger: 'change' }]
}

/** 候选关联任务：同项目下除自身以外的所有任务 */
const depTaskCandidates = computed<ImplTaskNode[]>(() =>
  projectTasks.value.filter((t) => t.id !== taskId.value)
)

function handleAddDependency() {
  if (!task.value?.projectId) {
    ElMessage.warning('当前任务缺少项目信息，无法添加依赖')
    return
  }
  Object.assign(addDepForm, {
    direction: 'predecessor',
    relatedTaskId: undefined,
    dependencyType: 'FS',
    lagDays: 0
  })
  addDepVisible.value = true
}

async function handleAddDepSubmit() {
  if (!addDepFormRef.value || !addDepForm.relatedTaskId || !task.value?.projectId) return
  await addDepFormRef.value.validate(async (valid) => {
    if (!valid) return
    addDepSubmitting.value = true
    try {
      const payload: TaskDependency = {
        projectId: task.value!.projectId,
        predecessorTaskId:
          addDepForm.direction === 'predecessor'
            ? addDepForm.relatedTaskId
            : taskId.value,
        successorTaskId:
          addDepForm.direction === 'predecessor'
            ? taskId.value
            : addDepForm.relatedTaskId,
        dependencyType: addDepForm.dependencyType,
        lagDays: addDepForm.lagDays
      }
      const result = await saveDependency(payload)
      if (result && typeof result === 'object' && 'success' in result && result.success === false) {
        // 循环依赖检测命中
        const cycle = result.cyclePath?.map((n) => n.taskName ?? `#${n.taskId}`).join(' → ')
        ElMessageBox.alert(
          `检测到循环依赖：\n\n${cycle || result.errorMessage}`,
          '无法保存依赖',
          { type: 'error' }
        )
      } else {
        ElMessage.success('依赖已添加')
        addDepVisible.value = false
        await loadDependencies(task.value!.projectId)
      }
    } catch {
      /* handled by interceptor */
    } finally {
      addDepSubmitting.value = false
    }
  })
}

async function handleDeleteDependency(id: number) {
  try {
    await ElMessageBox.confirm('确定删除这条依赖关系吗？', '删除确认', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteDependency(id)
    ElMessage.success('依赖已删除')
    if (task.value?.projectId) await loadDependencies(task.value.projectId)
  } catch {
    /* handled by interceptor */
  }
}

// ============ 强制检查项未完成弹窗 ============
const checklistDialogVisible = ref(false)
const uncheckedItems = computed(
  () => task.value?.checklistItems?.filter((i: any) => i.mandatory && !i.checked) ?? []
)
watch(hasUncheckedMandatory, (v) => {
  if (v && task.value?.status === 'IN_PROGRESS') {
    // 仅在用户尝试提交时通过 handleSubmitReview 弹出，此处不自动弹
  }
})

// ============ 主区滚动定位（侧栏"查看全部"切换活动区） ============
const activeSection = ref<'basic' | 'checklist' | 'comment' | 'activity'>('basic')
function scrollToActivity() {
  activeSection.value = 'activity'
  nextTick(() => {
    const el = document.querySelector('.activity-section')
    el?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
}

// ============ 初始化 ============
watch(
  () => route.query,
  (q) => {
    if (q.tab && typeof q.tab === 'string') {
      activeSection.value = q.tab as typeof activeSection.value
    }
  },
  { immediate: true }
)

onMounted(async () => {
  await loadProjectOptions()
  await loadTask()
})
</script>

<template>
  <div class="task-detail-page">
    <PageHeader>
      <template #title>
        <div class="title-row">
          <el-button link :icon="'ArrowLeft'" @click="router.back()">返回</el-button>
          <span class="task-name">{{
            isCreateMode ? '新建任务' : task?.taskName || '任务详情'
          }}</span>
          <el-tag v-if="isCreateMode" type="primary" size="small" effect="plain">新建</el-tag>
          <template v-else>
            <el-tag v-if="task" :type="statusTagType(task.status)" size="small" effect="plain">
              {{ statusLabel(task.status) }}
            </el-tag>
            <TaskPriorityTag v-if="task?.priority" :priority="task.priority" size="small" />
            <el-tag v-if="task?.taskType" size="small" effect="plain" type="info">
              {{ task.taskType === 'AGENT' ? '代理商' : '原厂' }}
            </el-tag>
          </template>
        </div>
      </template>
      <template #actions>
        <template v-if="isCreateMode">
          <el-button type="primary" :icon="'Edit'" @click="handleEdit">填写信息</el-button>
        </template>
        <template v-else>
          <el-button v-if="canStart" type="primary" @click="handleStart">开始</el-button>
          <el-button v-if="canSubmitReview" type="warning" @click="handleSubmitReview">
            提交评审
          </el-button>
          <el-button v-if="canApprove" type="success" @click="handleApprove">验收完成</el-button>
          <el-button v-if="canComplete" @click="handleComplete">完成</el-button>
          <el-button v-if="canEdit" :icon="'Edit'" @click="handleEdit">编辑</el-button>
          <el-button :icon="'Position'" @click="handleMove">移动</el-button>
        </template>
      </template>
    </PageHeader>

    <SkeletonCard :loading="loading" :rows="8">
      <EmptyState
        v-if="!task"
        title="任务不存在或加载失败"
        description="该任务可能已被删除，或您没有权限查看。"
        icon="WarningFilled"
      >
        <template #action>
          <el-button type="primary" @click="router.back()">返回上一页</el-button>
        </template>
      </EmptyState>

      <div v-else class="task-body">
        <!-- ===================== 主区 (70%) ===================== -->
        <main class="task-main">
          <!-- 1. 基本信息 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">基本信息</span>
                <el-tag size="small" effect="plain">ID: {{ task.id }}</el-tag>
              </div>
            </template>
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="任务编号">
                {{ task.id }}
              </el-descriptions-item>
              <el-descriptions-item label="所属项目">
                {{ task.projectName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="所属阶段">
                {{ task.phaseId ? `#${task.phaseId}` : '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="任务路径">
                {{ task.taskPath || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="计划开始">
                {{ formatDate(task.planStartDate) }}
              </el-descriptions-item>
              <el-descriptions-item label="计划结束">
                {{ formatDate(task.planEndDate) }}
              </el-descriptions-item>
              <el-descriptions-item label="实际开始">
                {{ formatDate(task.actualStart) }}
              </el-descriptions-item>
              <el-descriptions-item label="实际结束">
                {{ formatDate(task.actualEnd) }}
              </el-descriptions-item>
              <el-descriptions-item label="计划工时">
                {{ plannedHours != null ? `${plannedHours}h` : '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="实际工时">
                {{ task.actualHours != null ? `${task.actualHours}h` : '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="负责人">
                {{ assigneeText(task) }}
              </el-descriptions-item>
              <el-descriptions-item label="优先级">
                {{ priorityLabel(task.priority) }}
              </el-descriptions-item>
              <el-descriptions-item label="描述" :span="2">
                {{ task.description || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="备注" :span="2">
                {{ task.remark || '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 2. 任务进度 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <span class="card-title">任务进度</span>
            </template>
            <div class="progress-grid">
              <div class="progress-item">
                <div class="progress-label">汇总进度</div>
                <el-progress
                  type="circle"
                  :percentage="overallProgress"
                  :width="90"
                  :stroke-width="8"
                />
                <div class="progress-meta">
                  {{ progressVO?.completedSubtasks ?? 0 }} / {{ progressVO?.totalSubtasks ?? 0 }} 子任务
                </div>
              </div>
              <div class="progress-item">
                <div class="progress-label">子任务完成率</div>
                <el-progress
                  :percentage="subtaskCompletionRate"
                  :stroke-width="14"
                  :text-inside="true"
                  status="success"
                  class="bar-progress"
                />
                <div class="progress-meta">
                  {{ progressVO?.completedSubtasks ?? 0 }} / {{ progressVO?.totalSubtasks ?? 0 }}
                </div>
              </div>
              <div class="progress-item">
                <div class="progress-label">工时消耗率</div>
                <el-progress
                  :percentage="hoursBurnRate"
                  :stroke-width="14"
                  :text-inside="true"
                  :status="hoursBurnRate >= 90 ? 'warning' : undefined"
                  class="bar-progress"
                />
                <div class="progress-meta">
                  {{ task.actualHours ?? 0 }}h / {{ plannedHours ?? 0 }}h
                </div>
              </div>
            </div>
          </el-card>

          <!-- 3. 检查项 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">检查项</span>
                <el-tag
                  v-if="hasUncheckedMandatory"
                  type="danger"
                  size="small"
                  effect="plain"
                >
                  存在未完成的强制检查项
                </el-tag>
              </div>
            </template>
            <TaskChecklist
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
          </el-card>

          <!-- 4. 评论 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <span class="card-title">评论 ({{ comments.length }})</span>
            </template>
            <div class="comment-input-area">
              <div v-if="replyTo" class="reply-banner">
                <span>回复 @{{ replyTo.userName || '匿名' }}：</span>
                <span class="reply-content">{{ replyTo.content }}</span>
                <el-button link type="primary" size="small" @click="cancelReply">取消</el-button>
              </div>
              <el-input
                v-model="commentInput"
                type="textarea"
                :rows="3"
                placeholder="请输入评论内容，可使用 @ 提及他人"
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
                  {{ replyTo ? '回复' : '发布评论' }}
                </el-button>
              </div>
            </div>

            <div class="comment-list">
              <div
                v-for="c in nestedComments"
                :key="c.id"
                class="comment-item"
              >
                <div class="comment-main">
                  <div class="comment-header">
                    <span class="comment-author">{{ c.userName || '匿名' }}</span>
                    <span class="comment-time">{{ formatDateTime(c.createTime) }}</span>
                  </div>
                  <div class="comment-content">{{ c.content }}</div>
                  <div class="comment-ops">
                    <el-button link type="primary" size="small" @click="handleReply(c)">
                      回复
                    </el-button>
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
                <div v-if="c.replies?.length" class="reply-list">
                  <div
                    v-for="r in c.replies"
                    :key="r.id"
                    class="comment-item reply-item"
                  >
                    <div class="comment-main">
                      <div class="comment-header">
                        <span class="comment-author">{{ r.userName || '匿名' }}</span>
                        <span class="comment-time">{{ formatDateTime(r.createTime) }}</span>
                        <span class="reply-to">回复 @{{ c.userName || '匿名' }}</span>
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
              <EmptyState
                v-if="nestedComments.length === 0"
                title="暂无评论"
                description="发表第一条评论吧。"
                icon="ChatDotRound"
              />
            </div>
          </el-card>

          <!-- 5. 活动时间轴 -->
          <el-card shadow="never" class="section-card activity-section">
            <template #header>
              <span class="card-title">活动时间轴 ({{ activities.length }})</span>
            </template>
            <el-timeline v-if="activities.length">
              <el-timeline-item
                v-for="a in activities"
                :key="a.id"
                :timestamp="formatDateTime(a.createTime)"
                placement="top"
              >
                <div class="activity-row">
                  <el-tag :type="activityTagType(a.activityType)" size="small" effect="plain">
                    {{ activityLabel(a.activityType) }}
                  </el-tag>
                  <span class="activity-user">{{ a.userName || '系统' }}</span>
                  <span v-if="a.content" class="activity-content">{{ a.content }}</span>
                </div>
                <div v-if="a.metadata" class="activity-metadata">
                  <pre>{{ a.metadata }}</pre>
                </div>
              </el-timeline-item>
            </el-timeline>
            <EmptyState
              v-else
              title="暂无活动记录"
              description="任务的相关操作将记录在这里。"
              icon="Clock"
            />
          </el-card>
        </main>

        <!-- ===================== 侧栏 (30%) ===================== -->
        <aside class="task-sidebar">
          <!-- 1. 任务状态 -->
          <el-card shadow="never" class="sidebar-card">
            <template #header>
              <span class="card-title">任务状态</span>
            </template>
            <div class="status-current">
              <span class="status-label">当前状态</span>
              <el-tag :type="statusTagType(task.status)" effect="plain">
                {{ statusLabel(task.status) }}
              </el-tag>
            </div>
            <div class="status-history">
              <div class="sub-title">状态流转</div>
              <el-timeline v-if="statusHistory.length">
                <el-timeline-item
                  v-for="(s, idx) in statusHistory"
                  :key="s.id ?? idx"
                  :timestamp="formatDateTime(s.createTime)"
                  placement="top"
                  size="normal"
                >
                  <el-tag :type="activityTagType(s.activityType)" size="small" effect="plain">
                    {{ activityLabel(s.activityType) }}
                  </el-tag>
                  <span class="status-actor">{{ s.userName || '系统' }}</span>
                </el-timeline-item>
              </el-timeline>
              <div v-else class="empty-mini">暂无状态流转记录</div>
            </div>
          </el-card>

          <!-- 2. 父子任务 -->
          <el-card shadow="never" class="sidebar-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">父子任务</span>
                <el-button
                  v-if="canEdit"
                  link
                  type="primary"
                  size="small"
                  :icon="'Plus'"
                  @click="handleAddChild"
                >
                  添加子任务
                </el-button>
              </div>
            </template>
            <div class="sub-title">父任务</div>
            <div v-if="parentTask" class="related-task-item">
              <el-link type="primary" @click="handleNodeClick(parentTask.id!)">
                {{ parentTask.taskName }}
              </el-link>
              <el-tag :type="statusTagType(parentTask.status)" size="small" effect="plain">
                {{ statusLabel(parentTask.status) }}
              </el-tag>
            </div>
            <div v-else class="empty-mini">无父任务（顶层任务）</div>

            <div class="sub-title">直接子任务 ({{ directChildren.length }})</div>
            <div v-if="directChildren.length" class="related-task-list">
              <div
                v-for="c in directChildren"
                :key="c.id"
                class="related-task-item"
              >
                <el-link type="primary" @click="handleNodeClick(c.id!)">
                  {{ c.taskName }}
                </el-link>
                <el-tag :type="statusTagType(c.status)" size="small" effect="plain">
                  {{ statusLabel(c.status) }}
                </el-tag>
                <el-progress
                  :percentage="c.progress ?? 0"
                  :stroke-width="6"
                  :show-text="false"
                  class="mini-progress"
                />
              </div>
            </div>
            <div v-else class="empty-mini">暂无子任务</div>
          </el-card>

          <!-- 3. 依赖关系 -->
          <el-card shadow="never" class="sidebar-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">依赖关系</span>
                <el-button
                  v-if="canEdit"
                  link
                  type="primary"
                  size="small"
                  :icon="'Plus'"
                  @click="handleAddDependency"
                >
                  添加依赖
                </el-button>
              </div>
            </template>
            <div class="sub-title">前置任务 ({{ predecessors.length }})</div>
            <div v-if="predecessors.length" class="related-task-list">
              <div
                v-for="d in predecessors"
                :key="d.id"
                class="dep-item"
              >
                <el-link type="primary" @click="handleNodeClick(d.predecessorTaskId)">
                  {{ taskNameById(d.predecessorTaskId) }}
                </el-link>
                <el-tag size="small" effect="plain">{{ depTypeLabel(d.dependencyType) }}</el-tag>
                <span v-if="d.lagDays" class="lag-days">{{ d.lagDays > 0 ? '+' : '' }}{{ d.lagDays }}d</span>
                <el-button
                  v-if="canEdit"
                  link
                  type="danger"
                  size="small"
                  @click="handleDeleteDependency(d.id!)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <div v-else class="empty-mini">无前置任务</div>

            <div class="sub-title">后置任务 ({{ successors.length }})</div>
            <div v-if="successors.length" class="related-task-list">
              <div
                v-for="d in successors"
                :key="d.id"
                class="dep-item"
              >
                <el-link type="primary" @click="handleNodeClick(d.successorTaskId)">
                  {{ taskNameById(d.successorTaskId) }}
                </el-link>
                <el-tag size="small" effect="plain">{{ depTypeLabel(d.dependencyType) }}</el-tag>
                <span v-if="d.lagDays" class="lag-days">{{ d.lagDays > 0 ? '+' : '' }}{{ d.lagDays }}d</span>
                <el-button
                  v-if="canEdit"
                  link
                  type="danger"
                  size="small"
                  @click="handleDeleteDependency(d.id!)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <div v-else class="empty-mini">无后置任务</div>
          </el-card>

          <!-- 4. 附件 -->
          <el-card shadow="never" class="sidebar-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">附件</span>
              </div>
            </template>
            <!-- 新建模式（任务尚未保存）下不允许上传 -->
            <FileUploader
              v-if="taskId > 0"
              :biz-type="TASK_ATTACHMENT_BIZ_TYPE"
              :biz-id="taskId"
              :max-size="50"
              @success="handleAttachmentUploaded"
            />
            <div v-else class="empty-mini">请先保存任务后再上传附件</div>

            <div
              v-if="attachments.length"
              v-loading="attachmentLoading"
              class="attachment-list"
            >
              <div
                v-for="att in attachments"
                :key="att.id"
                class="attachment-item"
              >
                <el-icon class="att-icon"><Document /></el-icon>
                <div class="att-info">
                  <div class="att-name" :title="att.fileName">{{ att.fileName }}</div>
                  <div class="att-meta">
                    {{ formatFileSize(att.fileSize) }} · {{ formatUploadTime(att.uploadTime) }}
                    <span v-if="att.uploadUserName">· {{ att.uploadUserName }}</span>
                  </div>
                </div>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="handleDownloadAttachment(att)"
                >
                  下载
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  @click="handleDeleteAttachment(att)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <div v-else-if="!attachmentLoading" class="empty-mini">暂无附件</div>
          </el-card>

          <!-- 5. 操作历史 -->
          <el-card shadow="never" class="sidebar-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">操作历史</span>
                <el-button
                  v-if="activities.length > 10"
                  link
                  type="primary"
                  size="small"
                  @click="scrollToActivity"
                >
                  查看全部
                </el-button>
              </div>
            </template>
            <el-timeline v-if="recentActivities.length">
              <el-timeline-item
                v-for="a in recentActivities"
                :key="a.id"
                :timestamp="formatDateTime(a.createTime)"
                placement="top"
                size="normal"
              >
                <div class="recent-row">
                  <el-tag :type="activityTagType(a.activityType)" size="small" effect="plain">
                    {{ activityLabel(a.activityType) }}
                  </el-tag>
                  <span class="recent-content">{{ a.content || a.userName || '系统' }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <div v-else class="empty-mini">暂无操作记录</div>
          </el-card>
        </aside>
      </div>
    </SkeletonCard>

    <!-- ===================== Dialogs ===================== -->
    <!-- 编辑任务 -->
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
              :label="p.projectName"
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
        <el-form-item label="工程师">
          <UserSelect
            v-model="editForm.engineerId"
            placeholder="请搜索选择工程师"
            @change="onEditEngineerChange"
          />
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

    <!-- 新增子任务 -->
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
              :label="p.projectName"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工程师">
          <UserSelect
            v-model="addChildForm.engineerId"
            placeholder="请搜索选择工程师"
            @change="onAddChildEngineerChange"
          />
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
        <el-button type="primary" :loading="addChildSubmitting" @click="handleAddChildSubmit">
          创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 移动任务 -->
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

    <!-- 添加依赖 -->
    <el-dialog v-model="addDepVisible" title="添加依赖关系" width="520px" destroy-on-close>
      <el-form
        ref="addDepFormRef"
        :model="addDepForm"
        :rules="addDepRules"
        label-width="100px"
      >
        <el-form-item label="依赖方向">
          <el-radio-group v-model="addDepForm.direction">
            <el-radio value="predecessor">前置任务</el-radio>
            <el-radio value="successor">后置任务</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联任务" prop="relatedTaskId">
          <el-select
            v-model="addDepForm.relatedTaskId"
            placeholder="请选择关联任务"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="t in depTaskCandidates"
              :key="t.id"
              :label="t.taskName"
              :value="t.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="依赖类型">
          <el-select v-model="addDepForm.dependencyType" style="width: 100%">
            <el-option
              v-for="o in dependencyTypeOptions"
              :key="o.value"
              :label="`${o.value} (${o.desc})`"
              :value="o.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="滞后天数">
          <el-input-number
            v-model="addDepForm.lagDays"
            :min="-365"
            :max="365"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-hint">正数表示滞后，负数表示提前。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDepVisible = false">取消</el-button>
        <el-button type="primary" :loading="addDepSubmitting" @click="handleAddDepSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 强制检查项未完成弹窗（保留备用，主要走 handleSubmitReview 内的提示） -->
    <el-dialog v-model="checklistDialogVisible" title="强制检查项未完成" width="500px">
      <p>以下强制检查项尚未勾选，无法提交评审：</p>
      <ul>
        <li v-for="(item, idx) in uncheckedItems" :key="idx">{{ (item as any).title }}</li>
      </ul>
      <template #footer>
        <el-button type="primary" @click="checklistDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.task-detail-page {
  display: flex;
  flex-direction: column;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.task-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}
.task-body {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.task-main {
  flex: 0 0 70%;
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.task-sidebar {
  flex: 0 0 calc(30% - 16px);
  max-width: calc(30% - 16px);
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.section-card,
.sidebar-card {
  border-radius: var(--pms-radius-lg);
}
.section-card :deep(.el-card__body),
.sidebar-card :deep(.el-card__body) {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-regular);
}
.sub-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--pms-color-text-secondary);
  margin: 12px 0 6px;
}
.sub-title:first-child {
  margin-top: 0;
}

/* 进度区 */
.progress-grid {
  display: flex;
  gap: 24px;
  align-items: center;
  flex-wrap: wrap;
}
.progress-item {
  flex: 1;
  min-width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.progress-label {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
  font-weight: 500;
}
.progress-meta {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
.bar-progress {
  width: 100%;
}
.checklist-alert {
  margin-top: 12px;
}

/* 评论 */
.comment-input-area {
  margin-bottom: 16px;
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
  gap: 12px;
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

/* 活动时间轴 */
.activity-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.activity-user {
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}
.activity-content {
  font-size: 13px;
  color: #303133;
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

/* 侧栏：状态卡 */
.status-current {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.status-label {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}
.status-actor {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

/* 侧栏：父子任务 / 依赖 */
.related-task-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  flex-wrap: wrap;
}
.related-task-item .mini-progress {
  width: 80px;
}
.related-task-list {
  display: flex;
  flex-direction: column;
}
.dep-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  flex-wrap: wrap;
  border-bottom: 1px dashed var(--el-border-color-lighter);
}
.dep-item:last-child {
  border-bottom: none;
}
.lag-days {
  font-size: 12px;
  color: var(--pms-color-text-secondary);
}
.empty-mini {
  font-size: 12px;
  color: var(--pms-color-text-placeholder);
  padding: 8px 0;
}

/* 侧栏：附件 */
.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}
.att-icon {
  color: var(--pms-color-text-secondary);
}
.att-info {
  flex: 1;
  min-width: 0;
}
.att-name {
  font-size: 13px;
  color: var(--pms-color-text-regular);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.att-meta {
  font-size: 11px;
  color: var(--pms-color-text-secondary);
}

/* 侧栏：操作历史 */
.recent-row {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.recent-content {
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 响应式：窄屏堆叠 */
@media (max-width: 1200px) {
  .task-body {
    flex-direction: column;
  }
  .task-main,
  .task-sidebar {
    flex: 1 1 auto;
    max-width: 100%;
  }
}
</style>
