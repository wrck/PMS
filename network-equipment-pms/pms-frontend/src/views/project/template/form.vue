<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Sortable from 'sortablejs'
import {
  ArrowDown,
  ArrowUp,
  Check,
  CirclePlus,
  Close,
  Delete,
  Plus,
  Promotion,
  Rank,
  RefreshLeft
} from '@element-plus/icons-vue'
import {
  getTemplate,
  getDraftVersion,
  getPublishedVersion,
  createTemplate,
  updateTemplate,
  publishVersion,
  saveDraftSnapshot,
  listTemplateVersions,
  type PhaseDef,
  type PhaseCriteria,
  type PhaseExitGate,
  type ProjectTemplate,
  type ProjectTemplateVersion,
  type TaskDef,
  type TemplateSnapshot
} from '@/api/project-template'
import { getAllRoles, type RoleOption, type SysDictItem } from '@/api/system'
import { loadDeliverableTypes } from '@/api/deliverable'
import PageHeader from '@/components/common/PageHeader.vue'
import PhaseExitGateEditor from '@/components/PhaseExitGateEditor.vue'
import DeliverableRefEntitySelector from '@/components/DeliverableRefEntitySelector.vue'

defineOptions({ name: 'ProjectTemplateForm' })

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const publishing = ref(false)

/** 交付件性质类型字典项（从数据字典 pms_deliverable_type 加载） */
const deliverableTypeOptions = ref<SysDictItem[]>([])

const isEdit = computed(() => !!route.params.id)

// 8 个步骤
// 顺序说明：阶段定义提前到第 2 步，任务/交付件/里程碑等可关联阶段；
// 阶段的进入/退出条件需引用已配置的交付件/任务/里程碑/审批，放到最后一步。
const steps = [
  { title: '基本信息', description: '模板名称与分类' },
  { title: '阶段定义', description: '阶段编码与名称' },
  { title: '任务配置', description: '任务树与属性' },
  { title: '交付件配置', description: '交付件类型与签核' },
  { title: '依赖配置', description: '任务依赖关系' },
  { title: '审批配置', description: '审批计划列表' },
  { title: '里程碑配置', description: '关键里程碑' },
  { title: '进入/退出条件', description: '阶段进入与退出条件' }
]

const activeStep = ref(0)

// 模板基本信息
const form = reactive<ProjectTemplate>({
  templateCode: '',
  templateName: '',
  category: 'IMPLEMENT',
  description: '',
  status: 'DRAFT'
})

// 阶段列表
const phases = ref<PhaseDef[]>([])

// 任务节点：el-table 树形数据，通过 parentId 关联父子关系，children 由 buildTaskTree 计算
interface TaskNode {
  id: string
  parentId: string | null
  taskName: string
  taskCode: string
  phaseCode: string
  assigneeRole?: string
  plannedHours?: number
  weight?: number
  priority?: string
  description?: string
  children?: TaskNode[]
}
const tasks = ref<TaskNode[]>([])

/** el-table 渲染用的树形数据：由扁平 tasks 按 parentId 实时计算 */
const taskTree = computed(() => buildTaskTree(tasks.value))

/** 任务树最大深度：父任务 + 一级子任务（共 2 层），避免子任务内再嵌套子任务 */
const MAX_TASK_DEPTH = 2

/** el-table 树形配置：children 字段渲染子行（非懒加载，不需要 hasChildren） */
const taskTableTreeProps = { children: 'children' }

/** 按 parentId 将扁平列表重建为 el-table 树形数据 */
function buildTaskTree(flat: TaskNode[]): TaskNode[] {
  const map = new Map<string, TaskNode>()
  const roots: TaskNode[] = []
  // 第一遍：克隆并清空 children，建立 id → node 索引
  for (const t of flat) {
    map.set(t.id, { ...t, children: [] })
  }
  // 第二遍：按 parentId 挂载，保留 tasks 中的原始顺序
  for (const t of flat) {
    const node = map.get(t.id)!
    if (t.parentId && map.has(t.parentId)) {
      map.get(t.parentId)!.children!.push(node)
    } else {
      roots.push(node)
    }
  }
  return roots
}

/** 计算 node 在树中的深度（根=1） */
function getTaskDepth(flat: TaskNode[], id: string): number {
  let depth = 1
  let cur = flat.find((t) => t.id === id)
  while (cur?.parentId) {
    depth++
    cur = flat.find((t) => t.id === cur!.parentId)
    if (depth > MAX_TASK_DEPTH + 1) break // 防御循环引用
  }
  return depth
}

// 交付件
interface DeliverableNode {
  id: string
  name: string
  type: string
  required: boolean
  signOffRole?: string
  phaseCode?: string
  /** 引用实体类型（仅 type=ENTITY_REF 时使用） */
  refEntityType?: string
  /** 引用实体 ID（仅 type=ENTITY_REF 时使用） */
  refEntityId?: number
}
const deliverables = ref<DeliverableNode[]>([])

// 依赖关系（前后置任务通过 taskName 关联，对齐后端 DependencyDef.predecessorTaskName/successorTaskName）
interface DependencyNode {
  id: string
  fromTaskName: string
  toTaskName: string
  type: string // FINISH_TO_START / START_TO_START / FINISH_TO_FINISH / START_TO_FINISH
}
const dependencies = ref<DependencyNode[]>([])

// 审批计划
interface ApprovalPlanNode {
  id: string
  name: string
  phaseCode: string
  approverRole: string
  trigger: string // PHASE_EXIT / DELIVERABLE_SUBMIT / MILESTONE
}
const approvalPlans = ref<ApprovalPlanNode[]>([])

// 里程碑
interface MilestoneNode {
  id: string
  name: string
  phaseCode: string
  plannedDate?: string
  description?: string
}
const milestones = ref<MilestoneNode[]>([])

// 角色下拉数据
const roles = ref<RoleOption[]>([])

// 发布对话框
const publishDialogVisible = ref(false)
const publishForm = reactive({ version: '', changeLog: '' })

// ============== 工具函数 ==============

function genId(prefix: string) {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`
}

/** 扁平化所有非空任务名（供依赖关系选择），重复名自动加序号后缀避免歧义 */
const allTaskNames = computed(() => {
  const names: string[] = []
  function collect(nodes: TaskNode[]) {
    for (const n of nodes) {
      if (n.taskName && n.taskName.trim()) names.push(n.taskName.trim())
      if (n.children) collect(n.children)
    }
  }
  collect(tasks.value)
  return names
})

/** 依赖下拉选项：任务名 + 所属阶段标签（便于区分同名任务） */
const taskNameOptions = computed(() => {
  return allTaskNames.value.map((name) => {
    const task = findTaskByName(name)
    const phase = task?.phaseCode ? phases.value.find((p) => p.phaseCode === task.phaseCode) : null
    return {
      label: phase ? `${name} [${phase.phaseName || task?.phaseCode}]` : name,
      value: name
    }
  })
})

function findTaskByName(name: string): TaskNode | null {
  function find(nodes: TaskNode[]): TaskNode | null {
    for (const n of nodes) {
      if (n.taskName === name) return n
      if (n.children) {
        const found = find(n.children)
        if (found) return found
      }
    }
    return null
  }
  return find(tasks.value)
}

/** 阶段编码重复校验，返回重复的编码列表 */
function findDuplicatePhaseCodes(): string[] {
  const seen = new Set<string>()
  const dups = new Set<string>()
  for (const p of phases.value) {
    const code = p.phaseCode?.trim()
    if (!code) continue
    if (seen.has(code)) dups.add(code)
    else seen.add(code)
  }
  return [...dups]
}

/** 基于已有版本号生成下一个建议版本号 */
async function suggestNextVersion(): Promise<string> {
  if (!form.id) return 'v1.0.0'
  try {
    const res = await listTemplateVersions(form.id, 1, 200)
    const versions = res?.records ?? []
    if (versions.length === 0) return 'v1.0.0'
    // 取最大版本号递增
    const sorted = [...versions].sort((a, b) => compareVersionDesc(b.version, a.version))
    const latest = sorted[0]?.version ?? 'v1.0.0'
    const parts = latest.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
    parts[parts.length - 1] = (parts[parts.length - 1] ?? 0) + 1
    return 'v' + parts.join('.')
  } catch {
    return 'v1.0.0'
  }
}

function compareVersionDesc(v1: string, v2: string): number {
  const a = v1.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
  const b = v2.replace(/^v/i, '').split('.').map((n) => parseInt(n, 10) || 0)
  const len = Math.max(a.length, b.length)
  for (let i = 0; i < len; i++) {
    const ai = a[i] ?? 0
    const bi = b[i] ?? 0
    if (ai !== bi) return ai - bi
  }
  return 0
}

// ============== 阶段操作 ==============

function addPhase() {
  phases.value.push({
    phaseCode: '',
    phaseName: '',
    sortOrder: phases.value.length + 1,
    entryCriteria: { requirePreviousPhaseComplete: false, requireApproval: false },
    exitCriteria: { requiredDeliverables: [], requiredTasks: [], requiredMilestones: [], requiredApprovals: [] }
  } as PhaseDef)
}

function removePhase(idx: number) {
  phases.value.splice(idx, 1)
}

function movePhase(idx: number, direction: 'up' | 'down') {
  const target = direction === 'up' ? idx - 1 : idx + 1
  if (target < 0 || target >= phases.value.length) return
  const list = phases.value
  ;[list[idx], list[target]] = [list[target], list[idx]]
  // 重新计算 sortOrder
  list.forEach((p, i) => (p.sortOrder = i + 1))
}

/** 更新阶段进入条件（结构化对象） */
function updateEntryCriteria(phase: PhaseDef, key: 'requirePreviousPhaseComplete' | 'requireApproval', value: boolean) {
  const current = phase.entryCriteria ?? { requirePreviousPhaseComplete: false, requireApproval: false }
  phase.entryCriteria = { ...current, [key]: value }
}

/** 更新阶段退出条件（结构化对象，由 PhaseExitGateEditor 触发） */
function updateExitCriteria(phase: PhaseDef, value: PhaseExitGate) {
  phase.exitCriteria = value
}

// ============== 任务操作 ==============

/** el-table 实例引用：用于初始化 sortablejs 拖拽 */
const taskTableRef = ref()

/** 拖拽手柄列宽（仅显示拖拽手柄图标） */
const DRAG_HANDLE_WIDTH = 48

function newTask(parent?: TaskNode): TaskNode {
  return {
    id: genId('task'),
    parentId: parent?.id ?? null,
    taskName: '新任务',
    taskCode: '',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    assigneeRole: '',
    plannedHours: 0,
    weight: 1,
    priority: 'NORMAL',
    description: '',
    children: []
  }
}

function addTask(parent?: TaskNode) {
  tasks.value.push(newTask(parent))
}

function removeTask(row: TaskNode) {
  // 递归收集 row 及其所有后代 id，从扁平列表中一并移除
  const idsToRemove = new Set<string>()
  const collect = (id: string) => {
    idsToRemove.add(id)
    for (const t of tasks.value) {
      if (t.parentId === id) collect(t.id)
    }
  }
  collect(row.id)
  tasks.value = tasks.value.filter((t) => !idsToRemove.has(t.id))
}

/** 升级：将 row 变为其当前祖父的子节点（减少一层嵌套） */
function indentTask(row: TaskNode) {
  if (!row.parentId) return
  const parent = tasks.value.find((t) => t.id === row.parentId)
  if (!parent) return
  row.parentId = parent.parentId ?? null
}

/** 降级：将 row 变为前一个兄弟的子节点（增加一层嵌套），受 MAX_TASK_DEPTH 限制 */
function outdentTask(row: TaskNode) {
  const siblings = tasks.value.filter((t) => t.parentId === row.parentId)
  const idx = siblings.findIndex((t) => t.id === row.id)
  if (idx <= 0) return // 无前一个兄弟
  const prevSibling = siblings[idx - 1]
  const newParentDepth = getTaskDepth(tasks.value, prevSibling.id)
  const subtreeDepth = (id: string): number => {
    const kids = tasks.value.filter((t) => t.parentId === id)
    return kids.length === 0 ? 1 : 1 + Math.max(...kids.map((k) => subtreeDepth(k.id)))
  }
  const resultingDepth = newParentDepth + subtreeDepth(row.id)
  if (resultingDepth > MAX_TASK_DEPTH) {
    ElMessage.warning(`降级后层级深度 ${resultingDepth} 超过最大 ${MAX_TASK_DEPTH}，已阻止`)
    return
  }
  row.parentId = prevSibling.id
}

/** 上移/下移：在同兄弟间调整顺序（操作扁平列表中的相对位置） */
function moveTask(row: TaskNode, dir: 'up' | 'down') {
  const sibIndices = tasks.value
    .map((t, i) => (t.parentId === row.parentId ? i : -1))
    .filter((i) => i >= 0)
  const curIdx = tasks.value.findIndex((t) => t.id === row.id)
  const pos = sibIndices.indexOf(curIdx)
  const targetPos = dir === 'up' ? pos - 1 : pos + 1
  if (targetPos < 0 || targetPos >= sibIndices.length) return
  const i = sibIndices[pos]
  const j = sibIndices[targetPos]
  const segI = tasks.value.slice(i, i + 1)
  const segJ = tasks.value.slice(j, j + 1)
  const newTasks = tasks.value.slice()
  newTasks[i] = segJ[0]
  newTasks[j] = segI[0]
  tasks.value = newTasks
}

/** 判断 row 是否可上下移动（是否存在同父兄弟可交换） */
function canMoveSibling(row: TaskNode, dir: 'up' | 'down'): boolean {
  const sibIndices = tasks.value
    .map((t, i) => (t.parentId === row.parentId ? i : -1))
    .filter((i) => i >= 0)
  const curIdx = tasks.value.findIndex((t) => t.id === row.id)
  const pos = sibIndices.indexOf(curIdx)
  if (pos < 0) return false
  return dir === 'up' ? pos > 0 : pos < sibIndices.length - 1
}

/** 判断 row 是否可降级（存在前一个兄弟且降级后不超过最大深度） */
function canOutdent(row: TaskNode): boolean {
  const siblings = tasks.value.filter((t) => t.parentId === row.parentId)
  const idx = siblings.findIndex((t) => t.id === row.id)
  if (idx <= 0) return false
  const prevSibling = siblings[idx - 1]
  const newParentDepth = getTaskDepth(tasks.value, prevSibling.id)
  const subtreeDepth = (id: string): number => {
    const kids = tasks.value.filter((t) => t.parentId === id)
    return kids.length === 0 ? 1 : 1 + Math.max(...kids.map((k) => subtreeDepth(k.id)))
  }
  return newParentDepth + subtreeDepth(row.id) <= MAX_TASK_DEPTH
}

/** tasks 变化或切换到任务配置步骤后，重新初始化 sortablejs（el-table 行 DOM 会重建） */
watch(
  () => tasks.value.length,
  () => {
    initTaskSortable()
  }
)
watch(activeStep, (step) => {
  if (step === 2) initTaskSortable()
})

/** 初始化 sortablejs：在 el-table tbody 上启用行拖拽，变更父子关系与排序 */
let taskSortable: Sortable | null = null
function initTaskSortable() {
  if (taskSortable) {
    taskSortable.destroy()
    taskSortable = null
  }
  nextTick(() => {
    const tableEl = taskTableRef.value?.$el as HTMLElement | undefined
    const tbody = tableEl?.querySelector('.el-table__body-wrapper tbody') as HTMLElement | undefined
    if (!tbody) return
    taskSortable = Sortable.create(tbody, {
      animation: 150,
      handle: '.task-drag-handle',
      ghostClass: 'task-drag-ghost',
      onEnd(evt) {
        handleTaskRowDrop(evt)
      }
    })
  })
}

/** 处理 el-table 行拖拽结束：根据目标行位置与放置方向更新 parentId 与顺序 */
function handleTaskRowDrop(evt: Sortable.MoveEvent) {
  const oldIndex = evt.oldIndex
  const newIndex = evt.newIndex
  if (oldIndex === undefined || newIndex === undefined || oldIndex === newIndex) return
  const dragged = tasks.value[oldIndex]
  const target = tasks.value[newIndex]
  if (!dragged || !target || dragged.id === target.id) {
    tasks.value = [...tasks.value]
    return
  }
  // 防止拖入自己的后代（会形成环）
  let cur: TaskNode | undefined = target
  while (cur) {
    if (cur.id === dragged.id) {
      ElMessage.warning('不能拖入自身或后代任务')
      tasks.value = [...tasks.value]
      return
    }
    cur = cur.parentId ? tasks.value.find((t) => t.id === cur!.parentId) : undefined
  }
  const without = tasks.value.filter((t) => t.id !== dragged.id)
  const targetIdx = without.findIndex((t) => t.id === target.id)
  // 放在目标行下方→成为其子节点（受深度限制）；上方→成为前一个兄弟
  const placeAsChild = newIndex > oldIndex
  if (placeAsChild) {
    const targetDepth = getTaskDepth(without, target.id)
    const subDepth = (id: string): number => {
      const kids = without.filter((t) => t.parentId === id)
      return kids.length === 0 ? 1 : 1 + Math.max(...kids.map((k) => subDepth(k.id)))
    }
    if (targetDepth + subDepth(dragged.id) > MAX_TASK_DEPTH) {
      ElMessage.warning(`作为子任务后层级深度超过最大 ${MAX_TASK_DEPTH}，已阻止`)
      tasks.value = [...tasks.value]
      return
    }
    dragged.parentId = target.id
    without.splice(targetIdx + 1, 0, dragged)
  } else {
    dragged.parentId = target.parentId
    without.splice(targetIdx, 0, dragged)
  }
  tasks.value = without
  ElMessage.success('任务顺序已更新')
}

// ============== 交付件操作 ==============

function addDeliverable() {
  deliverables.value.push({
    id: genId('delv'),
    name: '新交付件',
    type: 'DOCUMENT',
    required: true,
    signOffRole: '',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    refEntityType: '',
    refEntityId: undefined
  })
}

/** 交付件类型变更时，重置引用实体字段（非 ENTITY_REF 类型清空引用） */
function onDeliverableTypeChange(row: DeliverableNode) {
  if (row.type !== 'ENTITY_REF') {
    row.refEntityType = ''
    row.refEntityId = undefined
  }
}

function removeDeliverable(idx: number) {
  deliverables.value.splice(idx, 1)
}

// ============== 依赖操作 ==============

function addDependency() {
  dependencies.value.push({
    id: genId('dep'),
    fromTaskName: '',
    toTaskName: '',
    type: 'FINISH_TO_START'
  })
}

function removeDependency(idx: number) {
  dependencies.value.splice(idx, 1)
}

// ============== 审批计划操作 ==============

function addApprovalPlan() {
  approvalPlans.value.push({
    id: genId('appr'),
    name: '新审批计划',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    approverRole: '',
    trigger: 'PHASE_EXIT'
  })
}

function removeApprovalPlan(idx: number) {
  approvalPlans.value.splice(idx, 1)
}

// ============== 里程碑操作 ==============

function addMilestone() {
  milestones.value.push({
    id: genId('ms'),
    name: '新里程碑',
    phaseCode: phases.value[0]?.phaseCode ?? '',
    plannedDate: '',
    description: ''
  })
}

function removeMilestone(idx: number) {
  milestones.value.splice(idx, 1)
}

// ============== 数据加载 ==============

/**
 * 规范化阶段进入条件字段类型（对齐后端 com.dp.plat.common.dto.PhaseCriteria）。
 * 确保 requirePreviousPhaseComplete / requireApproval 为布尔值，防止
 * undefined/字符串等脏数据导致后端 Jackson 反序列化或前端 v-model 类型异常。
 */
function normalizeEntryCriteria(raw: any): PhaseCriteria {
  const obj = raw && typeof raw === 'object' ? raw : {}
  return {
    requirePreviousPhaseComplete: Boolean(obj.requirePreviousPhaseComplete),
    requireApproval: Boolean(obj.requireApproval)
  }
}

/**
 * 规范化阶段退出条件字段类型（对齐后端 com.dp.plat.common.dto.PhaseExitGate）。
 * - 4 类条件统一为非空数组
 * - 内部对象 boolean 字段强制布尔化
 * - ID 字段（deliverableId/phaseId/milestoneId）统一为 string，兼容模板态字符串引用与项目态数字 ID
 *   后端 PhaseExitGate 已将 ID 字段改为 String，避免 Jackson 反序列化字符串到 Long 的类型错误。
 */
function normalizeExitCriteria(raw: any): PhaseExitGate {
  const obj = raw && typeof raw === 'object' ? raw : {}
  return {
    requiredDeliverables: (Array.isArray(obj.requiredDeliverables) ? obj.requiredDeliverables : []).map((d: any) => ({
      deliverableId: d.deliverableId != null ? String(d.deliverableId) : undefined,
      deliverableName: d.deliverableName ?? '',
      requiredStatus: d.requiredStatus ?? ''
    })),
    requiredTasks: (Array.isArray(obj.requiredTasks) ? obj.requiredTasks : []).map((t: any) => ({
      phaseId: t.phaseId != null ? String(t.phaseId) : undefined,
      allCompleted: Boolean(t.allCompleted)
    })),
    requiredMilestones: (Array.isArray(obj.requiredMilestones) ? obj.requiredMilestones : []).map((m: any) => ({
      milestoneId: m.milestoneId != null ? String(m.milestoneId) : undefined,
      mustReached: Boolean(m.mustReached)
    })),
    requiredApprovals: (Array.isArray(obj.requiredApprovals) ? obj.requiredApprovals : []).map((a: any) => ({
      approvalType: a.approvalType ?? '',
      mustApproved: Boolean(a.mustApproved)
    }))
  }
}

/**
 * 将后端 TemplateSnapshot 中的 DTO 字段映射到前端各 ref 节点。
 * 字段差异参考 com.dp.plat.common.dto.TemplateSnapshot。
 */
function applySnapshot(snap?: TemplateSnapshot) {
  if (!snap) return
  // 阶段：字段名一致，但需规范化 entryCriteria / exitCriteria 结构化对象
  // 历史数据可能存在 entryCriteria 为字符串/undefined 的情况，统一转为对象
  phases.value = ((snap.phases ?? []) as any[]).map((p: any): PhaseDef => ({
    phaseCode: p.phaseCode ?? '',
    phaseName: p.phaseName ?? '',
    sortOrder: p.sortOrder ?? 1,
    entryCriteria: normalizeEntryCriteria(p.entryCriteria),
    exitCriteria: normalizeExitCriteria(p.exitCriteria)
  }))

  // 任务：后端 TaskDef 为扁平列表（通过 parentTaskName 引用父任务）。
  // 加载时按 parentTaskName 转换为前端 parentId（id 引用），存入扁平 tasks，
  // 渲染用的树由 computed taskTree 实时构建，避免层级丢失或回显异常。
  const flatTasks = (snap.tasks ?? []) as any[]
  // 第一遍：为每个 TaskDef 生成 node 并记录 taskName → id
  const nameToId = new Map<string, string>()
  const nodes: TaskNode[] = []
  for (const t of flatTasks) {
    const id = genId('task')
    const node: TaskNode = {
      id,
      parentId: null,
      taskName: t.taskName ?? '',
      taskCode: t.taskCode ?? '',
      phaseCode: t.phaseCode ?? '',
      assigneeRole: t.assigneeRole ?? '',
      plannedHours: t.plannedHours ?? 0,
      weight: t.weight ?? 1,
      priority: t.priority ?? 'NORMAL',
      description: t.description ?? '',
      children: []
    }
    nodes.push(node)
    if (node.taskName) nameToId.set(node.taskName, id)
  }
  // 第二遍：按 parentTaskName 查找父节点 id 设置 parentId
  for (let i = 0; i < flatTasks.length; i++) {
    const parentName = (flatTasks[i] as any).parentTaskName
    if (parentName && nameToId.has(parentName)) {
      nodes[i].parentId = nameToId.get(parentName)!
    }
  }
  tasks.value = nodes

  // 交付件：name↔deliverableName, type↔deliverableType, required↔mandatory, signOffRole↔approverRole,
  // refEntityType/refEntityId 直接对齐
  deliverables.value = ((snap.deliverables ?? []) as any[]).map((d: any) => ({
    id: genId('delv'),
    name: d.name ?? d.deliverableName ?? '',
    type: d.type ?? d.deliverableType ?? 'OTHER',
    required: d.required ?? d.mandatory ?? true,
    signOffRole: d.signOffRole ?? d.approverRole ?? '',
    phaseCode: d.phaseCode ?? '',
    refEntityType: d.refEntityType ?? '',
    refEntityId: d.refEntityId ?? undefined
  })) as DeliverableNode[]

  // 依赖：fromTaskName↔predecessorTaskName, toTaskName↔successorTaskName, type↔dependencyType
  dependencies.value = ((snap.dependencies ?? []) as any[]).map((dep: any) => ({
    id: genId('dep'),
    fromTaskName: dep.fromTaskName ?? dep.predecessorTaskName ?? '',
    toTaskName: dep.toTaskName ?? dep.successorTaskName ?? '',
    type: dep.type ?? dep.dependencyType ?? 'FINISH_TO_START'
  })) as DependencyNode[]

  // 审批计划：name↔approvalType, approverRole↔approverRoles[0], trigger↔triggerPhaseCode/phaseCode
  approvalPlans.value = ((snap.approvalPlans ?? []) as any[]).map((a: any) => ({
    id: genId('appr'),
    name: a.name ?? a.approvalType ?? '',
    phaseCode: a.phaseCode ?? a.triggerPhaseCode ?? '',
    approverRole: a.approverRole ?? (Array.isArray(a.approverRoles) ? (a.approverRoles[0] ?? '') : ''),
    trigger: a.trigger ?? 'PHASE_EXIT'
  })) as ApprovalPlanNode[]

  // 里程碑：name↔milestoneName, plannedDate/description 后端无对应字段
  milestones.value = ((snap.milestones ?? []) as any[]).map((m: any) => ({
    id: genId('ms'),
    name: m.name ?? m.milestoneName ?? '',
    phaseCode: m.phaseCode ?? '',
    plannedDate: m.plannedDate ?? '',
    description: m.description ?? ''
  })) as MilestoneNode[]
}

async function loadTemplate(id: number) {
  loading.value = true
  try {
    const res = await getTemplate(id)
    Object.assign(form, res)
    // 优先加载草稿版本（用户未发布的编辑），无草稿则回退到已发布版本
    let snapshot: TemplateSnapshot | undefined
    try {
      const draft = await getDraftVersion(id)
      if (draft?.snapshotJson) {
        snapshot = draft.snapshotJson
      }
    } catch {
      /* 草稿版本接口异常，忽略 */
    }
    if (!snapshot) {
      try {
        const published = await getPublishedVersion(id)
        if (published?.snapshotJson) {
          snapshot = published.snapshotJson
        }
      } catch {
        /* 已发布版本不存在（如纯草稿模板），保持 ref 为空即可 */
      }
    }
    applySnapshot(snapshot)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  try {
    roles.value = await getAllRoles()
  } catch {
    roles.value = []
  }
}

// ============== 提交 ==============

function validateBasic(): string | null {
  if (!form.templateCode?.trim()) return '请填写模板编码'
  if (!form.templateName?.trim()) return '请填写模板名称'
  return null
}

async function handleSaveDraft() {
  const err = validateBasic()
  if (err) {
    ElMessage.warning(err)
    activeStep.value = 0
    return
  }
  submitting.value = true
  try {
    form.status = 'DRAFT'
    if (!form.id) {
      const created = await createTemplate(form)
      form.id = created.id
    } else {
      await updateTemplate(form)
    }
    // 持久化阶段/任务/交付件等详细配置到草稿快照
    await saveDraftSnapshot(form.id, buildSnapshot())
    ElMessage.success('草稿已保存')
  } catch (e: any) {
    // 原实现仅 try/finally 无 catch，保存失败时错误被静默吞掉，用户只看到按钮恢复但无任何提示。
    ElMessage.error('保存失败：' + (e?.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

function buildSnapshot(): TemplateSnapshot {
  // 前端短名 → 后端 DTO 长名映射（对齐 com.dp.plat.common.dto.TemplateSnapshot）
  return {
    phases: phases.value.map((p) => ({
      phaseCode: p.phaseCode,
      phaseName: p.phaseName,
      sortOrder: p.sortOrder,
      entryCriteria: normalizeEntryCriteria(p.entryCriteria),
      exitCriteria: normalizeExitCriteria(p.exitCriteria)
    })),
    // 任务：flattenTasks 将树扁平化为后端 TaskDef 列表，parentTaskName 引用父任务名称；
    // 前端 id/children 为内部字段，不随 TaskDef 提交（后端 DTO 不含这两个字段）
    tasks: flattenTasks(tasks.value),
    // 交付件：name→deliverableName, type→deliverableType, required→mandatory, signOffRole→approverRole,
    // refEntityType/refEntityId 直接对齐后端 DeliverableDef
    deliverables: deliverables.value.map((d) => ({
      deliverableName: d.name,
      deliverableType: d.type,
      phaseCode: d.phaseCode,
      mandatory: d.required,
      approverRole: d.signOffRole,
      refEntityType: d.type === 'ENTITY_REF' ? d.refEntityType : undefined,
      refEntityId: d.type === 'ENTITY_REF' ? d.refEntityId : undefined
    })),
    // 依赖：fromTaskName→predecessorTaskName, toTaskName→successorTaskName, type→dependencyType
    dependencies: dependencies.value.map((dep) => ({
      predecessorTaskName: dep.fromTaskName,
      successorTaskName: dep.toTaskName,
      dependencyType: dep.type
    })),
    // 审批计划：name→approvalType, approverRole→approverRoles[0], trigger 不属于后端 DTO，phaseCode→triggerPhaseCode
    approvalPlans: approvalPlans.value.map((a) => ({
      approvalType: a.name,
      triggerPhaseCode: a.phaseCode,
      approverRoles: a.approverRole ? [a.approverRole] : []
    })),
    // 里程碑：name→milestoneName；plannedDate/description 后端 DTO 无对应字段，保留会被忽略
    milestones: milestones.value.map((m, idx) => ({
      milestoneName: m.name,
      phaseCode: m.phaseCode,
      sortOrder: idx + 1
    }))
  }
}

/** 将扁平任务列表转换为后端 TaskDef 列表（parentTaskName 关联父节点，补全所有字段） */
function flattenTasks(nodes: TaskNode[]): TaskDef[] {
  const result: TaskDef[] = []
  let order = 1
  for (const n of nodes) {
    const parent = n.parentId ? nodes.find((t) => t.id === n.parentId) : undefined
    result.push({
      taskName: n.taskName,
      parentTaskName: parent?.taskName,
      phaseCode: n.phaseCode,
      plannedHours: n.plannedHours,
      assigneeRole: n.assigneeRole,
      weight: n.weight,
      priority: n.priority,
      description: n.description,
      sortOrder: order++
    })
  }
  return result
}

async function handleSave() {
  const err = validateBasic()
  if (err) {
    ElMessage.warning(err)
    activeStep.value = 0
    return
  }
  submitting.value = true
  try {
    form.status = 'DRAFT'
    if (!form.id) {
      const created = await createTemplate(form)
      form.id = created.id
    } else {
      await updateTemplate(form)
    }
    // 持久化阶段/任务/交付件等详细配置到草稿快照
    await saveDraftSnapshot(form.id, buildSnapshot())
    ElMessage.success('保存成功')
    router.back()
  } catch (e: any) {
    ElMessage.error('保存失败：' + (e?.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

async function handlePublish() {
  if (!form.id) {
    ElMessage.warning('请先保存模板')
    return
  }
  if (phases.value.length === 0) {
    ElMessage.warning('请至少配置一个阶段')
    activeStep.value = 1
    return
  }
  // 阶段编码重复校验
  const dups = findDuplicatePhaseCodes()
  if (dups.length > 0) {
    ElMessage.warning(`阶段编码重复：${dups.join('、')}，请修改后重试`)
    activeStep.value = 1
    return
  }
  if (!publishForm.version.trim()) {
    ElMessage.warning('请填写版本号')
    return
  }
  publishing.value = true
  try {
    // 发布前先保存草稿快照，确保最新配置被持久化
    await saveDraftSnapshot(form.id, buildSnapshot())
    const snapshot = buildSnapshot()
    await publishVersion(form.id, {
      version: publishForm.version,
      snapshot,
      changeLog: publishForm.changeLog
    })
    ElMessage.success('版本已发布')
    publishDialogVisible.value = false
    publishForm.version = ''
    publishForm.changeLog = ''
    // 重新加载模板，让 form.status 同步为 'PUBLISHED'
    await loadTemplate(form.id)
  } finally {
    publishing.value = false
  }
}

/** 打开发布对话框时自动建议版本号 */
async function openPublishDialog() {
  publishForm.version = await suggestNextVersion()
  publishDialogVisible.value = true
}

function handleNext() {
  if (activeStep.value === 0) {
    const err = validateBasic()
    if (err) {
      ElMessage.warning(err)
      return
    }
  }
  if (activeStep.value < steps.length - 1) activeStep.value++
}

function handlePrev() {
  if (activeStep.value > 0) activeStep.value--
}

onMounted(() => {
  loadRoles()
  loadDeliverableTypes().then((items) => {
    deliverableTypeOptions.value = items
  })
  const id = route.params.id as string | undefined
  if (id) loadTemplate(Number(id))
})
</script>

<template>
  <div class="template-form-page" v-loading="loading">
    <PageHeader :title="isEdit ? '编辑模板' : '新建模板'" description="按 7 步分步配置模板内容">
      <template #actions>
        <el-button :icon="RefreshLeft" @click="router.back()">取消</el-button>
        <el-button v-permission="'project:template:add'" :icon="Check" :loading="submitting" @click="handleSaveDraft">保存草稿</el-button>
        <el-button v-if="form.id" v-permission="'project:template:publish'" type="success" :icon="Promotion" @click="openPublishDialog">
          发布版本
        </el-button>
        <el-button v-permission="'project:template:add'" type="primary" :icon="Check" :loading="submitting" @click="handleSave">保存</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never" class="steps-card">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step v-for="(s, idx) in steps" :key="idx" :title="s.title" :description="s.description" />
      </el-steps>
    </el-card>

    <el-card shadow="never" class="step-content-card">
      <!-- Step 1: 基本信息 -->
      <div v-if="activeStep === 0" class="step-panel">
        <el-form :model="form" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="模板编码" required>
                <el-input
                  v-model="form.templateCode"
                  :disabled="!!form.id"
                  placeholder="如 TPL-IMPL-STD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="模板名称" required>
                <el-input v-model="form.templateName" placeholder="请输入模板名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目类型">
                <el-select v-model="form.category" style="width: 100%">
                  <el-option label="实施" value="IMPLEMENT" />
                  <el-option label="维护" value="MAINTENANCE" />
                  <el-option label="咨询" value="CONSULTING" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-tag :type="form.status === 'PUBLISHED' ? 'success' : 'info'" effect="light">
                  {{ form.status }}
                </el-tag>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="3"
                  placeholder="说明模板用途、适用项目类型等"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- Step 2: 阶段定义（仅编码/名称，进入/退出条件在最后一步配置） -->
      <div v-else-if="activeStep === 1" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ phases.length }} 个阶段 · 使用上下箭头调整顺序</span>
          <el-button type="primary" :icon="Plus" @click="addPhase">添加阶段</el-button>
        </div>
        <el-empty v-if="phases.length === 0" description="暂无阶段，点击「添加阶段」开始配置" />
        <div v-for="(phase, idx) in phases" :key="idx" class="phase-row">
          <div class="phase-order">{{ idx + 1 }}</div>
          <div class="phase-fields">
            <el-input v-model="phase.phaseCode" placeholder="阶段编码 PREPARE" style="width: 180px" />
            <el-input v-model="phase.phaseName" placeholder="阶段名称" style="width: 200px" />
          </div>
          <div class="phase-actions">
            <el-button
              link
              :icon="ArrowUp"
              :disabled="idx === 0"
              @click="movePhase(idx, 'up')"
            />
            <el-button
              link
              :icon="ArrowDown"
              :disabled="idx === phases.length - 1"
              @click="movePhase(idx, 'down')"
            />
            <el-button link type="danger" :icon="Delete" @click="removePhase(idx)" />
          </div>
        </div>
      </div>

      <!-- Step 3: 任务配置（el-table 树形数据，支持拖拽排序与父子关系变更） -->
      <div v-else-if="activeStep === 2" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">拖拽行变更排序与父子关系 · 升降级调整层级 · 最大 {{ MAX_TASK_DEPTH }} 层</span>
          <el-button type="primary" :icon="Plus" @click="addTask()">添加任务</el-button>
        </div>
        <el-empty v-if="tasks.length === 0" description="暂无任务，点击「添加任务」开始配置" />
        <el-table
          v-else
          ref="taskTableRef"
          v-show="tasks.length > 0"
          :data="taskTree"
          row-key="id"
          border
          default-expand-all
          :tree-props="taskTableTreeProps"
          @expand-change="initTaskSortable"
        >
          <el-table-column :width="DRAG_HANDLE_WIDTH" align="center" class-name="drag-col">
            <template #default>
              <el-icon class="task-drag-handle" title="拖拽变更排序与父子关系"><Rank /></el-icon>
            </template>
          </el-table-column>
          <el-table-column label="任务名称" prop="taskName" min-width="220" class-name="task-name-col">
            <template #default="{ row }">
              <el-input v-model="row.taskName" size="small" placeholder="任务名称" />
            </template>
          </el-table-column>
          <el-table-column label="任务编码" min-width="140">
            <template #default="{ row }">
              <el-input v-model="row.taskCode" size="small" placeholder="任务编码" />
            </template>
          </el-table-column>
          <el-table-column label="所属阶段" min-width="150">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" placeholder="请选择阶段" style="width: 100%">
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="负责角色" min-width="150">
            <template #default="{ row }">
              <el-select v-model="row.assigneeRole" size="small" filterable clearable placeholder="请选择角色" style="width: 100%">
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="工时" min-width="110">
            <template #default="{ row }">
              <el-input-number v-model="row.plannedHours" :min="0" size="small" controls-position="right" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="权重" min-width="110">
            <template #default="{ row }">
              <el-input-number v-model="row.weight" :min="0" :max="100" :precision="2" size="small" controls-position="right" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="优先级" min-width="110">
            <template #default="{ row }">
              <el-select v-model="row.priority" size="small" placeholder="请选择" style="width: 100%">
                <el-option label="高" value="HIGH" />
                <el-option label="中" value="NORMAL" />
                <el-option label="低" value="LOW" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="描述" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.description" size="small" placeholder="任务详细描述" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" :icon="CirclePlus" @click.stop="addTask(row)">子任务</el-button>
              <el-button link size="small" :icon="ArrowUp" :disabled="!canMoveSibling(row, 'up')" @click.stop="moveTask(row, 'up')" />
              <el-button link size="small" :icon="ArrowDown" :disabled="!canMoveSibling(row, 'down')" @click.stop="moveTask(row, 'down')" />
              <el-button link size="small" :icon="Promotion" :disabled="!row.parentId" @click.stop="indentTask(row)" title="升级" />
              <el-button link size="small" :icon="ArrowDown" :disabled="!canOutdent(row)" @click.stop="outdentTask(row)" title="降级" />
              <el-button link type="danger" size="small" :icon="Close" @click.stop="removeTask(row)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 4: 交付件配置 -->
      <div v-else-if="activeStep === 3" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ deliverables.length }} 个交付件</span>
          <el-button type="primary" :icon="Plus" @click="addDeliverable">添加交付件</el-button>
        </div>
        <el-empty v-if="deliverables.length === 0" description="暂无交付件" />
        <el-table v-else :data="deliverables" border stripe>
          <el-table-column label="名称" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="类型" width="160">
            <template #default="{ row }">
              <el-select
                v-model="row.type"
                size="small"
                placeholder="请选择交付件性质类型"
                @change="onDeliverableTypeChange(row)"
              >
                <el-option
                  v-for="item in deliverableTypeOptions"
                  :key="item.itemValue"
                  :label="item.itemText"
                  :value="item.itemValue"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="引用实体" min-width="320">
            <template #default="{ row }">
              <DeliverableRefEntitySelector
                v-if="row.type === 'ENTITY_REF'"
                v-model:ref-entity-type="row.refEntityType"
                v-model:ref-entity-id="row.refEntityId"
                size="small"
              />
              <span v-else class="text-muted">—</span>
            </template>
          </el-table-column>
          <el-table-column label="所属阶段" width="160">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必需" width="80" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.required" />
            </template>
          </el-table-column>
          <el-table-column label="签核角色" width="160">
            <template #default="{ row }">
              <el-select v-model="row.signOffRole" size="small" filterable clearable placeholder="请选择签核角色">
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeDeliverable($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 5: 依赖配置 -->
      <div v-else-if="activeStep === 4" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ dependencies.length }} 条依赖关系</span>
          <el-button type="primary" :icon="Plus" @click="addDependency">添加依赖</el-button>
        </div>
        <el-empty v-if="dependencies.length === 0" description="暂无依赖关系" />
        <el-table v-else :data="dependencies" border stripe>
          <el-table-column label="前置任务" min-width="200">
            <template #default="{ row }">
              <el-select v-model="row.fromTaskName" size="small" filterable clearable placeholder="请选择前置任务">
                <el-option
                  v-for="opt in taskNameOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="后置任务" min-width="200">
            <template #default="{ row }">
              <el-select v-model="row.toTaskName" size="small" filterable clearable placeholder="请选择后置任务">
                <el-option
                  v-for="opt in taskNameOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="依赖类型" width="200">
            <template #default="{ row }">
              <el-select v-model="row.type" size="small">
                <el-option label="完成-开始 (FS)" value="FINISH_TO_START" />
                <el-option label="开始-开始 (SS)" value="START_TO_START" />
                <el-option label="完成-完成 (FF)" value="FINISH_TO_FINISH" />
                <el-option label="开始-完成 (SF)" value="START_TO_FINISH" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeDependency($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 6: 审批配置 -->
      <div v-else-if="activeStep === 5" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ approvalPlans.length }} 个审批计划</span>
          <el-button type="primary" :icon="Plus" @click="addApprovalPlan">添加审批计划</el-button>
        </div>
        <el-empty v-if="approvalPlans.length === 0" description="暂无审批计划" />
        <el-table v-else :data="approvalPlans" border stripe>
          <el-table-column label="名称" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="触发阶段" width="180">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="触发类型" width="200">
            <template #default="{ row }">
              <el-select v-model="row.trigger" size="small">
                <el-option label="阶段退出" value="PHASE_EXIT" />
                <el-option label="交付件提交" value="DELIVERABLE_SUBMIT" />
                <el-option label="里程碑达成" value="MILESTONE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="审批角色" width="180">
            <template #default="{ row }">
              <el-select v-model="row.approverRole" size="small" filterable clearable placeholder="请选择审批角色">
                <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeApprovalPlan($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 7: 里程碑配置 -->
      <div v-else-if="activeStep === 6" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">共 {{ milestones.length }} 个里程碑</span>
          <el-button type="primary" :icon="Plus" @click="addMilestone">添加里程碑</el-button>
        </div>
        <el-empty v-if="milestones.length === 0" description="暂无里程碑" />
        <el-table v-else :data="milestones" border stripe>
          <el-table-column label="名称" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="关联阶段" width="180">
            <template #default="{ row }">
              <el-select v-model="row.phaseCode" size="small" clearable>
                <el-option v-for="p in phases" :key="p.phaseCode" :label="p.phaseName" :value="p.phaseCode" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="计划日期" width="180">
            <template #default="{ row }">
              <el-date-picker
                v-model="row.plannedDate"
                type="date"
                size="small"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="描述" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.description" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeMilestone($index)" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Step 8: 进入/退出条件（最后一步，退出条件可引用前面已配置的交付件/任务/里程碑/审批） -->
      <div v-else-if="activeStep === 7" class="step-panel">
        <div class="panel-toolbar">
          <span class="panel-tip">为每个阶段配置进入与退出条件</span>
        </div>
        <el-empty v-if="phases.length === 0" description="请先在「阶段定义」中添加阶段" />
        <div v-for="(phase, idx) in phases" :key="idx" class="phase-row">
          <div class="phase-order">{{ idx + 1 }}</div>
          <div class="phase-fields">
            <span class="phase-name-label">{{ phase.phaseName || phase.phaseCode || `阶段 ${idx + 1}` }}</span>
            <span class="phase-code-label">{{ phase.phaseCode }}</span>
          </div>
          <div class="phase-criteria-block">
            <div class="criteria-group">
              <div class="criteria-title">进入条件</div>
              <div class="criteria-body">
                <el-checkbox
                  :model-value="phase.entryCriteria?.requirePreviousPhaseComplete ?? false"
                  @update:model-value="(v: boolean | string | number | undefined) => updateEntryCriteria(phase, 'requirePreviousPhaseComplete', Boolean(v))"
                >
                  需要前置阶段完成
                </el-checkbox>
                <el-checkbox
                  :model-value="phase.entryCriteria?.requireApproval ?? false"
                  @update:model-value="(v: boolean | string | number | undefined) => updateEntryCriteria(phase, 'requireApproval', Boolean(v))"
                >
                  需要审批通过
                </el-checkbox>
              </div>
            </div>
            <div class="criteria-group">
              <div class="criteria-title">退出条件</div>
              <div class="criteria-body">
                <PhaseExitGateEditor
                  :model-value="phase.exitCriteria"
                  :deliverable-options="deliverables.map((d) => ({ id: d.id, label: d.name }))"
                  :phase-options="phases.filter((p) => p.phaseCode).map((p) => ({ id: p.phaseCode, label: p.phaseName || p.phaseCode }))"
                  :milestone-options="milestones.filter((m) => m.id).map((m) => ({ id: m.id, label: m.name || '未命名里程碑' }))"
                  @update:model-value="(v: PhaseExitGate) => updateExitCriteria(phase, v)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 步骤导航 -->
      <div class="step-actions">
        <el-button v-if="activeStep > 0" @click="handlePrev">上一步</el-button>
        <el-button v-if="activeStep < steps.length - 1" type="primary" @click="handleNext">
          下一步
        </el-button>
        <el-button v-if="activeStep === steps.length - 1" type="primary" @click="handleSave">
          完成保存
        </el-button>
      </div>
    </el-card>

    <!-- 发布版本对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布新版本" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="如 v1.0.0" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input v-model="publishForm.changeLog" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.template-form-page {
  padding: 16px 24px;
}

.steps-card {
  margin-bottom: 16px;
  border-radius: var(--pms-radius-lg);
}

.step-content-card {
  border-radius: var(--pms-radius-lg);
}

.step-panel {
  min-height: 360px;
  padding: 8px 4px;
}

.panel-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.panel-tip {
  font-size: 13px;
  color: var(--pms-color-text-secondary);
}

.phase-row {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px;
  background: var(--pms-color-bg-page);
  border-radius: var(--pms-radius-md);
  margin-bottom: 12px;
}
.phase-order {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--pms-color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
  margin-top: 4px;
}
.phase-fields {
  display: flex;
  gap: 8px;
  flex: 1;
  flex-wrap: wrap;
  align-items: center;
  min-width: 0;
}
.phase-name-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
}
.phase-code-label {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  font-family: 'Courier New', monospace;
}
.phase-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
}
.phase-criteria-block {
  flex-basis: 100%;
  display: flex;
  gap: 16px;
  margin-top: 8px;
  flex-wrap: wrap;
}
.criteria-group {
  flex: 1;
  min-width: 320px;
  background: #fff;
  border: 1px solid var(--el-border-color-light);
  border-radius: var(--pms-radius-sm);
  padding: 10px 12px;
}
.criteria-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
  margin-bottom: 8px;
}
.criteria-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.task-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding-right: 8px;
  flex-wrap: wrap;
}

/* 任务表格拖拽手柄 */
.task-drag-handle {
  cursor: grab;
  color: var(--el-text-color-secondary);
  font-size: 16px;
}
.task-drag-handle:active {
  cursor: grabbing;
}
.task-drag-ghost {
  opacity: 0.5;
  background: var(--el-color-primary-light-9);
}
/* el-table 树形层级展示：展开图标与缩进 */
.task-name-col .el-table__expand-icon {
  margin-right: 6px;
}
.task-name-col .cell {
  display: flex;
  align-items: center;
}
/* 子行通过层级背景色增强视觉区分 */
.el-table .el-table__row--level-1 .task-name-col {
  background-color: var(--el-fill-color-light);
}
.text-muted {
  color: var(--el-text-color-secondary);
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 24px 0 8px;
  border-top: 1px solid var(--pms-color-border-light);
  margin-top: 16px;
}
</style>
