<script setup lang="ts">
// =============================================================================
// PhaseManage - 阶段管理页（Task 7 重做）
// -----------------------------------------------------------------------------
// 双视图：流水线（横向卡片） + 时间轴（纵向 el-timeline），el-radio-group 切换。
// 阶段详情抽屉：基本信息 + 进入条件（只读）+ 退出条件（PhaseExitGateEditor 只读）+ 必需交付件
//              + 阶段任务 + 阶段里程碑。
// 推进阶段：退出条件 violations 校验 + 二次确认。
// 入口：1) 工作区 Tab 嵌入（props.projectId）；2) 独立路由 /project/phase/:projectId。
// =============================================================================
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type UploadFile } from 'element-plus'
import { ArrowRight, Delete, Edit, InfoFilled, Plus, Upload, WarningFilled } from '@element-plus/icons-vue'
import {
  advancePhase,
  createPhase,
  deletePhase,
  listPhasesByProjectId,
  updatePhase,
  type PhaseExitGateResult,
  type PhaseExitGateViolation,
  type ProjectPhase
} from '@/api/project-phase'
import { getProject, listMilestones, type Milestone, type Project } from '@/api/project'
import {
  listDeliverables,
  listFullDeliverables,
  uploadDeliverableInitialVersion,
  type Deliverable,
  type DeliverableChecklist,
  type DeliverableType
} from '@/api/deliverable'
import { getTasksByProject, type ImplTask } from '@/api/implementation'
import type { PhaseCriteria } from '@/api/project-template'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import PhaseStatusTag from '@/components/common/PhaseStatusTag.vue'
import PhaseExitGateEditor from '@/components/PhaseExitGateEditor.vue'
import type { EpTagType } from '@/types'

defineOptions({ name: 'PhaseManage' })

interface Props {
  /** 工作区嵌入时传入；独立路由访问时为空，回退到 route.params.projectId */
  projectId?: number | string
}
const props = defineProps<Props>()

const route = useRoute()
const router = useRouter()

/** 实际 projectId：优先 props，回退 route.params.projectId */
const projectId = computed<number>(() => {
  const fromProps = props.projectId
  if (fromProps != null && fromProps !== '') return Number(fromProps)
  return Number(route.params.projectId)
})

// ===== 视图模式 =====
const viewMode = ref<'pipeline' | 'timeline'>('pipeline')

// ===== 数据 =====
const loading = ref(false)
const advancing = ref(false)
const project = ref<Project | null>(null)
const phases = ref<ProjectPhase[]>([])

// ===== 阶段详情抽屉 =====
const drawerVisible = ref(false)
const drawerMode = ref<'view' | 'create' | 'edit'>('view')
const selectedPhase = ref<ProjectPhase | null>(null)
const detailLoading = ref(false)
const phaseDeliverables = ref<Deliverable[]>([])
const phaseTasks = ref<ImplTask[]>([])
const phaseMilestones = ref<Milestone[]>([])
const projectDeliverables = ref<Deliverable[]>([])
const projectMilestones = ref<Milestone[]>([])
/** 项目交付件清单（DeliverableChecklist，8 类标准交付件，退出条件必需交付件数据源） */
const projectChecklist = ref<DeliverableChecklist[]>([])
const uploadVisible = ref(false)
const uploadDeliverable = ref<Deliverable | null>(null)
const uploadFile = ref<File | null>(null)
const uploadChangeLog = ref('')
const uploadSaving = ref(false)

// ===== 新增/编辑阶段 =====
const formMode = ref<'create' | 'edit'>('create')
const formSaving = ref(false)
const phaseForm = reactive<ProjectPhase>({
  projectId: 0,
  phaseName: '',
  phaseCode: '',
  sortOrder: 1,
  status: 'NOT_STARTED',
  plannedStartDate: undefined,
  plannedEndDate: undefined
})

// ===== 推进 violations 弹窗 =====
const violationDialogVisible = ref(false)
const violationResult = ref<PhaseExitGateResult | null>(null)

/** 当前可推进的阶段（唯一一个 IN_PROGRESS） */
const currentPhase = computed<ProjectPhase | null>(
  () => phases.value.find((p) => p.status === 'IN_PROGRESS') ?? null
)

const inProgressCount = computed(() => phases.value.filter((p) => p.status === 'IN_PROGRESS').length)
const completedCount = computed(() => phases.value.filter((p) => p.status === 'COMPLETED').length)

/** 交付件清单类型中文映射（与交付件管理页 deliverable/index.vue 一致） */
const checklistTypeLabels: Record<DeliverableType, string> = {
  AS_BUILT: '竣工图',
  TEST_REPORT: '测试报告',
  ACCEPTANCE_CERT: '验收证书',
  TRAINING_RECORD: '培训记录',
  OPERATION_MANUAL: '操作手册',
  ASSET_REGISTER: '资产清单',
  WARRANTY_CERT: '质保证书',
  SPARE_PARTS_LIST: '备件清单'
}
function checklistTypeLabel(type?: string): string {
  return (checklistTypeLabels as Record<string, string>)[type ?? ''] ?? type ?? '-'
}

/** 必需交付件下拉数据源：项目交付件清单（DeliverableChecklist） */
const deliverableOptions = computed(() => projectChecklist.value
  .filter((item): item is DeliverableChecklist & { id: number } => item.id != null)
  .map((item) => ({ id: item.id, label: checklistTypeLabel(item.deliverableType) })))
const phaseOptions = computed(() => phases.value
  .filter((item): item is ProjectPhase & { id: number } => item.id != null)
  .map((item) => ({ id: item.id, label: item.phaseName })))
const milestoneOptions = computed(() => projectMilestones.value
  .filter((item): item is Milestone & { id: number } => item.id != null)
  .map((item) => ({ id: item.id, label: item.milestoneName || item.name || '未命名里程碑' })))

// ===== 工具函数 =====
function formatDate(date?: string): string {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

/** 退出条件 4 类标签 */
function gateTypeMeta(gateType?: string): { label: string; tagType: EpTagType } {
  switch (gateType) {
    case 'DELIVERABLE':
      return { label: '交付件', tagType: 'warning' }
    case 'TASK':
      return { label: '任务', tagType: 'primary' }
    case 'MILESTONE':
      return { label: '里程碑', tagType: 'success' }
    case 'APPROVAL':
      return { label: '审批', tagType: 'danger' }
    default:
      return { label: gateType ?? '-', tagType: 'info' }
  }
}

/** 退出条件完成度（基于状态简化估算） */
function getExitGateProgress(phase: ProjectPhase): number {
  if (phase.status === 'COMPLETED') return 100
  if (phase.status === 'IN_PROGRESS') return 60
  if (phase.status === 'SKIPPED') return 100
  return 0
}

function getExitGateText(phase: ProjectPhase): string {
  if (phase.status === 'COMPLETED') return '已完成'
  if (phase.status === 'IN_PROGRESS') return '退出条件部分满足'
  if (phase.status === 'SKIPPED') return '已跳过'
  return '未开始'
}

/** el-timeline 节点 type 映射 */
function getTimelineType(status?: string): 'primary' | 'success' | 'warning' | 'info' {
  switch (status) {
    case 'COMPLETED':
      return 'success'
    case 'IN_PROGRESS':
      return 'primary'
    case 'SKIPPED':
      return 'warning'
    default:
      return 'info'
  }
}

function violationRowKey(v: PhaseExitGateViolation, idx: number): string {
  return `${v.gateType ?? ''}-${v.businessId ?? ''}-${idx}`
}

// ===== 数据加载 =====
async function loadProject() {
  if (!projectId.value || Number.isNaN(projectId.value)) return
  try {
    project.value = await getProject(projectId.value)
  } catch {
    /* handled by interceptor */
  }
}

async function loadPhases() {
  if (!projectId.value || Number.isNaN(projectId.value)) {
    phases.value = []
    return
  }
  loading.value = true
  try {
    phases.value = (await listPhasesByProjectId(projectId.value)) ?? []
  } catch {
    phases.value = []
  } finally {
    loading.value = false
  }
}

async function loadProjectResources() {
  if (!projectId.value || Number.isNaN(projectId.value)) {
    projectDeliverables.value = []
    projectMilestones.value = []
    projectChecklist.value = []
    return
  }
  const [deliverables, milestones, checklist] = await Promise.all([
    listFullDeliverables({ projectId: projectId.value }).catch(() => [] as Deliverable[]),
    listMilestones(projectId.value).catch(() => [] as Milestone[]),
    listDeliverables(projectId.value).catch(() => [] as DeliverableChecklist[])
  ])
  projectDeliverables.value = deliverables ?? []
  projectMilestones.value = milestones ?? []
  projectChecklist.value = checklist ?? []
}

async function reload() {
  await Promise.all([loadProject(), loadPhases(), loadProjectResources()])
}

/** 加载阶段详情关联数据（交付件/任务/里程碑） */
async function loadPhaseDetails(phase: ProjectPhase) {
  if (!phase.id) return
  detailLoading.value = true
  try {
    const [deliverables, tasks, milestones] = await Promise.all([
      listFullDeliverables({ projectId: projectId.value, phaseId: phase.id }).catch(() => [] as Deliverable[]),
      getTasksByProject(projectId.value).catch(() => [] as ImplTask[]),
      listMilestones(projectId.value).catch(() => [] as Milestone[])
    ])
    phaseDeliverables.value = (deliverables ?? []).filter((d) => d.phaseId === phase.id)
    phaseTasks.value = (tasks ?? []).filter((t) => t.phaseId === phase.id)
    phaseMilestones.value = (milestones ?? []).filter(
      (m) => m.id != null && m.ppdiooPhase === phase.phaseCode
    )
  } finally {
    detailLoading.value = false
  }
}

// ===== 交互 =====
function selectPhase(phase: ProjectPhase) {
  selectedPhase.value = phase
  drawerMode.value = 'view'
  drawerVisible.value = true
  loadPhaseDetails(phase)
}

function resetPhaseForm(phase?: ProjectPhase) {
  const rawEntry = phase?.entryCriteria
  const entryCriteria: PhaseCriteria = rawEntry && typeof rawEntry === 'object'
    ? {
        requirePreviousPhaseComplete: Boolean((rawEntry as PhaseCriteria).requirePreviousPhaseComplete),
        requireApproval: Boolean((rawEntry as PhaseCriteria).requireApproval)
      }
    : { requirePreviousPhaseComplete: false, requireApproval: false }
  Object.assign(phaseForm, {
    id: phase?.id,
    projectId: projectId.value,
    templatePhaseId: phase?.templatePhaseId,
    phaseName: phase?.phaseName ?? '',
    phaseCode: phase?.phaseCode ?? '',
    sortOrder: phase?.sortOrder ?? phases.value.length + 1,
    entryCriteria,
    exitCriteria: phase?.exitCriteria,
    status: phase?.status ?? 'NOT_STARTED',
    plannedStartDate: phase?.plannedStartDate,
    plannedEndDate: phase?.plannedEndDate,
    actualStartDate: phase?.actualStartDate,
    actualEndDate: phase?.actualEndDate
  })
}

/** 更新阶段进入条件（结构化对象） */
function updateEntryCriteria(key: 'requirePreviousPhaseComplete' | 'requireApproval', value: boolean) {
  const current = (phaseForm.entryCriteria as PhaseCriteria) ?? { requirePreviousPhaseComplete: false, requireApproval: false }
  phaseForm.entryCriteria = { ...current, [key]: value }
}

function openCreatePhase() {
  formMode.value = 'create'
  resetPhaseForm()
  selectedPhase.value = null
  drawerMode.value = 'create'
  drawerVisible.value = true
}

function openEditPhase(phase: ProjectPhase) {
  formMode.value = 'edit'
  resetPhaseForm(phase)
  drawerMode.value = 'edit'
}

function closePhaseForm() {
  if (selectedPhase.value) {
    drawerMode.value = 'view'
  } else {
    drawerVisible.value = false
  }
}

async function savePhase() {
  const name = phaseForm.phaseName.trim()
  const code = phaseForm.phaseCode.trim().toUpperCase()
  if (!name || !code) {
    ElMessage.warning('请填写阶段名称和阶段编码')
    return
  }
  if (phaseForm.plannedStartDate && phaseForm.plannedEndDate && phaseForm.plannedStartDate > phaseForm.plannedEndDate) {
    ElMessage.warning('计划结束日期不能早于计划开始日期')
    return
  }
  formSaving.value = true
  try {
    const payload = { ...phaseForm, phaseName: name, phaseCode: code }
    const saved = formMode.value === 'create'
      ? await createPhase(payload)
      : await updatePhase(payload)
    ElMessage.success(formMode.value === 'create' ? '阶段已新增' : '阶段已保存')
    await loadPhases()
    selectedPhase.value = phases.value.find((item) => item.id === saved.id) ?? saved
    drawerMode.value = 'view'
    if (selectedPhase.value) {
      await loadPhaseDetails(selectedPhase.value)
    }
  } finally {
    formSaving.value = false
  }
}

async function handleDeletePhase(phase: ProjectPhase) {
  if (!phase.id) return
  if (phase.status !== 'NOT_STARTED') {
    ElMessage.warning('仅允许删除未开始的阶段')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认删除阶段「${phase.phaseName}」？此操作不可撤销。`,
      '删除阶段',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  await deletePhase(phase.id)
  ElMessage.success('阶段已删除')
  drawerVisible.value = false
  selectedPhase.value = null
  await loadPhases()
}

/** 推进阶段：二次确认 + violations 校验 */
async function handleAdvance(phase: ProjectPhase) {
  if (!phase.id) return
  try {
    await ElMessageBox.confirm(
      `确认推进阶段「${phase.phaseName}」？将完成当前阶段并激活下一阶段。`,
      '推进确认',
      { type: 'warning', confirmButtonText: '推进', cancelButtonText: '取消' }
    )
  } catch {
    return // 用户取消
  }

  advancing.value = true
  try {
    const result = await advancePhase(phase.id)
    // 响应拦截器在 code===200 时直接解包为 data；
    // 推进失败时后端返回 code=200 + data.success=false，
    // 故以 resolved 形式收到 PhaseExitGateResult，需运行时判定。
    if (result && typeof result === 'object' && 'success' in result && result.success === false) {
      violationResult.value = result as PhaseExitGateResult
      violationDialogVisible.value = true
    } else {
      ElMessage.success('阶段推进成功')
      await loadPhases()
    }
  } catch {
    /* 网络异常由拦截器统一提示 */
  } finally {
    advancing.value = false
  }
}

// ===== 跳转 =====
function goWorkspace() {
  router.push({ name: 'ProjectWorkspace', params: { id: String(projectId.value) } })
}

function goBack() {
  router.back()
}

function goDeliverableDetail(id?: number) {
  if (id != null) router.push({ name: 'DeliverableDetail', params: { id: String(id) } })
}

function goTaskDetail(id?: number) {
  if (id != null) router.push({ name: 'TaskDetail', params: { id: String(id) } })
}

function openDeliverableUpload(deliverable: Deliverable) {
  uploadDeliverable.value = deliverable
  uploadFile.value = null
  uploadChangeLog.value = ''
  uploadVisible.value = true
}

function handleUploadFileChange(file: UploadFile) {
  uploadFile.value = file.raw ?? null
}

async function submitDeliverableUpload() {
  if (!uploadDeliverable.value?.id || !uploadFile.value) {
    ElMessage.warning('请选择需要上传的文件')
    return
  }
  uploadSaving.value = true
  try {
    await uploadDeliverableInitialVersion(
      uploadDeliverable.value.id,
      uploadFile.value,
      uploadChangeLog.value
    )
    ElMessage.success('交付件初始版本上传成功')
    uploadVisible.value = false
    if (selectedPhase.value) await loadPhaseDetails(selectedPhase.value)
    await loadProjectResources()
  } finally {
    uploadSaving.value = false
  }
}

// ===== 生命周期 =====
watch(projectId, reload)
onMounted(reload)
</script>

<template>
  <div class="phase-manage">
    <!-- 顶部 PageHeader -->
    <PageHeader
      :title="`阶段管理${project?.projectName ? ' · ' + project.projectName : ''}`"
      :description="`共 ${phases.length} 个阶段 · 已完成 ${completedCount} · 进行中 ${inProgressCount}`"
    >
      <template #actions>
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" @click="goWorkspace">返回工作区</el-button>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="pipeline">流水线</el-radio-button>
          <el-radio-button label="timeline">时间轴</el-radio-button>
        </el-radio-group>
        <el-button v-permission="'project:phase:advance'" :icon="Plus" @click="openCreatePhase">
          新增阶段
        </el-button>
        <el-button
          v-permission="'project:phase:advance'"
          type="primary"
          :disabled="!currentPhase"
          :loading="advancing"
          @click="currentPhase && handleAdvance(currentPhase)"
        >
          推进当前阶段
        </el-button>
      </template>
    </PageHeader>

    <!-- 加载骨架 -->
    <SkeletonCard v-if="loading" :loading="loading" :rows="6" />

    <!-- 空状态 -->
    <EmptyState
      v-else-if="phases.length === 0"
      icon="Files"
      title="暂无阶段数据"
      description="项目尚无阶段，可从模板创建项目时生成。"
    />

    <!-- 流水线视图 -->
    <div v-else-if="viewMode === 'pipeline'" class="pipeline-view">
      <template v-for="(phase, idx) in phases" :key="phase.id ?? idx">
        <el-card
          shadow="hover"
          class="phase-card"
          :class="{
            'phase-completed': phase.status === 'COMPLETED',
            'phase-in-progress': phase.status === 'IN_PROGRESS',
            'phase-skipped': phase.status === 'SKIPPED',
            'phase-not-started': !phase.status || phase.status === 'NOT_STARTED'
          }"
          @click="selectPhase(phase)"
        >
          <div class="phase-header">
            <PhaseStatusTag :status="phase.status || 'NOT_STARTED'" size="small" />
            <span class="phase-sort">#{{ phase.sortOrder ?? idx + 1 }}</span>
          </div>
          <h3 class="phase-name" :title="phase.phaseName">{{ phase.phaseName }}</h3>
          <div class="phase-meta">
            <div class="meta-row">
              <span class="meta-label">编码</span>
              <span class="meta-value">{{ phase.phaseCode || '-' }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">计划</span>
              <span class="meta-value">
                {{ formatDate(phase.plannedStartDate) }} ~ {{ formatDate(phase.plannedEndDate) }}
              </span>
            </div>
            <div class="meta-row">
              <span class="meta-label">实际</span>
              <span class="meta-value">
                {{ formatDate(phase.actualStartDate) }} ~ {{ formatDate(phase.actualEndDate) }}
              </span>
            </div>
          </div>
          <!-- 退出条件可视化 -->
          <div class="exit-gate-summary">
            <el-progress
              :percentage="getExitGateProgress(phase)"
              :stroke-width="4"
              :status="phase.status === 'COMPLETED' ? 'success' : undefined"
            />
            <span class="gate-text">{{ getExitGateText(phase) }}</span>
          </div>
          <!-- 当前阶段标识 -->
          <div v-if="phase.status === 'IN_PROGRESS'" class="current-badge">当前阶段</div>
          <!-- 阶段操作按钮 -->
          <div class="phase-actions" @click.stop>
            <el-button
              v-if="phase.status === 'IN_PROGRESS'"
              type="primary"
              size="small"
              :loading="advancing"
              @click="handleAdvance(phase)"
            >
              推进阶段
            </el-button>
            <el-button size="small" @click="selectPhase(phase)">查看详情</el-button>
          </div>
        </el-card>
        <el-icon v-if="idx < phases.length - 1" class="phase-arrow"><ArrowRight /></el-icon>
      </template>
    </div>

    <!-- 时间轴视图 -->
    <el-timeline v-else class="timeline-view">
      <el-timeline-item
        v-for="(phase, idx) in phases"
        :key="phase.id ?? idx"
        :type="getTimelineType(phase.status)"
        :timestamp="formatDate(phase.actualStartDate || phase.plannedStartDate)"
        placement="top"
        :class="{ 'timeline-active': phase.status === 'IN_PROGRESS' }"
      >
        <el-card shadow="hover" class="timeline-card" @click="selectPhase(phase)">
          <div class="timeline-card-header">
            <h4 class="timeline-title">{{ phase.phaseName }}</h4>
            <PhaseStatusTag :status="phase.status || 'NOT_STARTED'" size="small" />
          </div>
          <div class="timeline-meta">
            <span class="meta-label">编码：</span>
            <span class="meta-value">{{ phase.phaseCode || '-' }}</span>
            <span class="meta-divider">|</span>
            <span class="meta-label">计划：</span>
            <span class="meta-value">
              {{ formatDate(phase.plannedStartDate) }} ~ {{ formatDate(phase.plannedEndDate) }}
            </span>
          </div>
          <div class="timeline-gate">
            <el-progress
              :percentage="getExitGateProgress(phase)"
              :stroke-width="4"
              :status="phase.status === 'COMPLETED' ? 'success' : undefined"
            />
            <span class="gate-text">{{ getExitGateText(phase) }}</span>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <!-- 当前推进阶段提示 -->
    <div v-if="!loading && phases.length > 0" class="current-tip-wrap">
      <div v-if="currentPhase" class="current-tip">
        <el-icon><InfoFilled /></el-icon>
        当前推进阶段：<strong>{{ currentPhase.phaseName }}</strong>
        ，点击「推进当前阶段」按钮将完成该阶段并激活下一阶段。
      </div>
      <div v-else class="current-tip current-tip-warning">
        <el-icon><WarningFilled /></el-icon>
        当前没有进行中的阶段。若所有阶段已完成，项目将进入收尾状态。
      </div>
    </div>

    <!-- 阶段详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="drawerMode === 'create'
        ? '阶段详情 · 新增阶段'
        : drawerMode === 'edit'
          ? `阶段详情 · ${phaseForm.phaseName || '编辑阶段'}`
          : selectedPhase
            ? `阶段详情 · ${selectedPhase.phaseName}`
            : '阶段详情'"
      size="55%"
      destroy-on-close
    >
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="drawerMode === 'view' && selectedPhase">
          <!-- 基本信息 -->
          <el-descriptions :column="2" border size="small" class="block">
            <el-descriptions-item label="阶段名称">{{ selectedPhase.phaseName }}</el-descriptions-item>
            <el-descriptions-item label="阶段编码">{{ selectedPhase.phaseCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="阶段状态">
              <PhaseStatusTag :status="selectedPhase.status || 'NOT_STARTED'" size="small" />
            </el-descriptions-item>
            <el-descriptions-item label="排序">#{{ selectedPhase.sortOrder }}</el-descriptions-item>
            <el-descriptions-item label="计划开始">{{ formatDate(selectedPhase.plannedStartDate) }}</el-descriptions-item>
            <el-descriptions-item label="计划结束">{{ formatDate(selectedPhase.plannedEndDate) }}</el-descriptions-item>
            <el-descriptions-item label="实际开始">{{ formatDate(selectedPhase.actualStartDate) }}</el-descriptions-item>
            <el-descriptions-item label="实际结束">{{ formatDate(selectedPhase.actualEndDate) }}</el-descriptions-item>
          </el-descriptions>

          <!-- 进入条件（只读） -->
          <div class="detail-block">
            <div class="block-title">进入条件</div>
            <div class="entry-criteria-view">
              <el-tag
                :type="(selectedPhase.entryCriteria as PhaseCriteria)?.requirePreviousPhaseComplete ? 'success' : 'info'"
                :effect="(selectedPhase.entryCriteria as PhaseCriteria)?.requirePreviousPhaseComplete ? 'dark' : 'plain'"
                size="large"
              >
                {{ (selectedPhase.entryCriteria as PhaseCriteria)?.requirePreviousPhaseComplete ? '✓' : '○' }}
                需要前置阶段完成
              </el-tag>
              <el-tag
                :type="(selectedPhase.entryCriteria as PhaseCriteria)?.requireApproval ? 'success' : 'info'"
                :effect="(selectedPhase.entryCriteria as PhaseCriteria)?.requireApproval ? 'dark' : 'plain'"
                size="large"
              >
                {{ (selectedPhase.entryCriteria as PhaseCriteria)?.requireApproval ? '✓' : '○' }}
                需要审批通过
              </el-tag>
              <span
                v-if="!(selectedPhase.entryCriteria as PhaseCriteria)?.requirePreviousPhaseComplete
                  && !(selectedPhase.entryCriteria as PhaseCriteria)?.requireApproval"
                class="entry-criteria-empty"
              >
                未配置进入条件
              </span>
            </div>
          </div>

          <!-- 退出条件（只读） -->
          <div class="detail-block">
            <div class="block-title">退出条件</div>
            <PhaseExitGateEditor
              :model-value="selectedPhase.exitCriteria"
              :disabled="true"
              :deliverable-options="deliverableOptions"
              :phase-options="phaseOptions"
              :milestone-options="milestoneOptions"
            />
          </div>

          <!-- 必需交付件 -->
          <div class="detail-block">
            <div class="block-title">该阶段交付件（{{ phaseDeliverables.length }}）</div>
            <el-table :data="phaseDeliverables" border stripe size="small" empty-text="暂无交付件">
              <el-table-column type="index" label="#" width="50" align="center" />
              <el-table-column label="名称" min-width="180" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-link type="primary" @click="goDeliverableDetail(row.id)">
                    {{ row.deliverableName }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column label="类型" width="120" prop="deliverableType" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag size="small" type="info">{{ row.status || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="版本" width="80" align="center" prop="currentVersion" />
              <el-table-column label="操作" width="110" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 'DRAFT' && !row.filePath"
                    v-permission="'project:deliverable:upload'"
                    link
                    type="primary"
                    :icon="Upload"
                    @click="openDeliverableUpload(row)"
                  >
                    上传文件
                  </el-button>
                  <span v-else>-</span>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 阶段任务 -->
          <div class="detail-block">
            <div class="block-title">该阶段任务（{{ phaseTasks.length }}）</div>
            <el-table :data="phaseTasks" border stripe size="small" empty-text="暂无任务">
              <el-table-column type="index" label="#" width="50" align="center" />
              <el-table-column label="任务名称" min-width="180" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-link type="primary" @click="goTaskDetail(row.id)">
                    {{ row.taskName }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag size="small" type="info">{{ row.status || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="进度" width="120" align="center">
                <template #default="{ row }">
                  <el-progress :percentage="row.progress || 0" :stroke-width="4" />
                </template>
              </el-table-column>
              <el-table-column label="计划结束" width="120" align="center">
                <template #default="{ row }">{{ formatDate(row.planEndDate) }}</template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 阶段里程碑 -->
          <div class="detail-block">
            <div class="block-title">项目里程碑（{{ phaseMilestones.length }}）</div>
            <el-table :data="phaseMilestones" border stripe size="small" empty-text="暂无里程碑">
              <el-table-column type="index" label="#" width="50" align="center" />
              <el-table-column label="名称" min-width="180" show-overflow-tooltip>
                <template #default="{ row }">{{ row.milestoneName || row.name || '-' }}</template>
              </el-table-column>
              <el-table-column label="类型" width="120">
                <template #default="{ row }">{{ row.milestoneType || row.type || '-' }}</template>
              </el-table-column>
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag size="small" type="info">{{ row.status || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="计划日期" width="120" align="center">
                <template #default="{ row }">{{ formatDate(row.planDate || row.plannedDate) }}</template>
              </el-table-column>
              <el-table-column label="实际日期" width="120" align="center">
                <template #default="{ row }">{{ formatDate(row.actualDate) }}</template>
              </el-table-column>
            </el-table>
          </div>

        </template>

        <template v-else>
          <el-form :model="phaseForm" label-position="top">
          <div class="detail-block form-first-block">
            <div class="block-title">基本信息</div>
            <div class="phase-form-grid">
              <el-form-item label="阶段名称" required>
                <el-input v-model="phaseForm.phaseName" maxlength="100" show-word-limit />
              </el-form-item>
              <el-form-item label="阶段编码" required>
                <el-input v-model="phaseForm.phaseCode" placeholder="例如 DESIGN" maxlength="50" />
              </el-form-item>
              <el-form-item label="阶段状态">
                <el-select v-model="phaseForm.status" disabled style="width: 100%">
                  <el-option label="未开始" value="NOT_STARTED" />
                  <el-option label="进行中" value="IN_PROGRESS" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="已跳过" value="SKIPPED" />
                </el-select>
              </el-form-item>
              <el-form-item label="排序" required>
                <el-input-number v-model="phaseForm.sortOrder" :min="1" :max="999" style="width: 100%" />
              </el-form-item>
              <el-form-item label="计划开始">
                <el-date-picker
                  v-model="phaseForm.plannedStartDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择开始日期"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="计划结束">
                <el-date-picker
                  v-model="phaseForm.plannedEndDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择结束日期"
                  style="width: 100%"
                />
              </el-form-item>
            </div>
          </div>

          <div class="detail-block">
            <div class="block-title">进入条件</div>
            <div class="entry-criteria-edit">
              <el-checkbox
                :model-value="(phaseForm.entryCriteria as PhaseCriteria)?.requirePreviousPhaseComplete ?? false"
                @update:model-value="(v: boolean | string | number | undefined) => updateEntryCriteria('requirePreviousPhaseComplete', Boolean(v))"
              >
                需要前置阶段完成
              </el-checkbox>
              <el-checkbox
                :model-value="(phaseForm.entryCriteria as PhaseCriteria)?.requireApproval ?? false"
                @update:model-value="(v: boolean | string | number | undefined) => updateEntryCriteria('requireApproval', Boolean(v))"
              >
                需要审批通过
              </el-checkbox>
            </div>
          </div>

          <div class="detail-block">
            <div class="block-title">退出条件</div>
            <div class="exit-gate-editor-wrap">
              <PhaseExitGateEditor
                v-model="phaseForm.exitCriteria"
                :deliverable-options="deliverableOptions"
                :phase-options="phaseOptions"
                :milestone-options="milestoneOptions"
              />
            </div>
          </div>
          </el-form>

        </template>
      </div>

      <template #footer>
        <div class="drawer-footer">
          <template v-if="drawerMode === 'view' && selectedPhase">
            <el-button v-permission="'project:phase:advance'" :icon="Edit" @click="openEditPhase(selectedPhase)">
              编辑阶段
            </el-button>
            <el-button
              v-if="selectedPhase.status === 'NOT_STARTED'"
              v-permission="'project:phase:advance'"
              type="danger"
              plain
              :icon="Delete"
              @click="handleDeletePhase(selectedPhase)"
            >
              删除阶段
            </el-button>
            <el-button
              v-if="selectedPhase.status === 'IN_PROGRESS'"
              v-permission="'project:phase:advance'"
              type="primary"
              :loading="advancing"
              @click="handleAdvance(selectedPhase)"
            >
              推进该阶段
            </el-button>
          </template>
          <template v-else>
            <el-button @click="closePhaseForm">取消</el-button>
            <el-button v-permission="'project:phase:advance'" type="primary" :loading="formSaving" @click="savePhase">
              保存
            </el-button>
          </template>
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="uploadVisible" title="上传阶段交付件" width="560px" destroy-on-close>
      <el-alert
        v-if="uploadDeliverable"
        :title="`阶段：${selectedPhase?.phaseName ?? '-'} · 交付件：${uploadDeliverable.deliverableName}`"
        type="info"
        :closable="false"
        show-icon
        class="upload-context"
      />
      <el-form label-position="top">
        <el-form-item label="文件" required>
          <el-upload
            drag
            :auto-upload="false"
            :limit="1"
            :on-change="handleUploadFileChange"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">拖拽文件到此处，或<em>点击选择</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="版本说明">
          <el-input v-model="uploadChangeLog" type="textarea" :rows="3" placeholder="可选，默认记录为初始版本" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button
          v-permission="'project:deliverable:upload'"
          type="primary"
          :loading="uploadSaving"
          @click="submitDeliverableUpload"
        >
          上传并创建 v1
        </el-button>
      </template>
    </el-dialog>

    <!-- violations 弹窗 -->
    <el-dialog
      v-model="violationDialogVisible"
      title="阶段推进被阻止 — 退出条件未满足"
      width="720px"
      destroy-on-close
    >
      <div v-if="violationResult" class="violation-summary">
        <el-alert
          :title="violationResult.errorMessage || '当前阶段退出条件未满足'"
          type="error"
          :closable="false"
          show-icon
        >
          <template v-if="violationResult.errorCode" #default>
            <div class="violation-code">错误码：{{ violationResult.errorCode }}</div>
          </template>
        </el-alert>
        <div class="violation-count">
          共 {{ violationResult.violations?.length ?? 0 }} 项未满足条件：
        </div>
      </div>
      <el-table
        :data="violationResult?.violations ?? []"
        border
        stripe
        :row-key="violationRowKey"
        empty-text="无违规项"
      >
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="gateTypeMeta(row.gateType).tagType" size="small">
              {{ gateTypeMeta(row.gateType).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="业务对象" min-width="160">
          <template #default="{ row }">
            <span v-if="row.businessName">{{ row.businessName }}</span>
            <span v-else-if="row.businessId">业务对象 #{{ row.businessId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="期望状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.expectedStatus" type="success" size="small" effect="plain">
              {{ row.expectedStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="实际状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.actualStatus" type="danger" size="small" effect="plain">
              {{ row.actualStatus }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="violationDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.phase-manage {
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ===== 流水线视图 ===== */
.pipeline-view {
  display: flex;
  align-items: stretch;
  gap: 0;
  padding: 8px 0;
  overflow-x: auto;
  flex-wrap: wrap;
}
.phase-card {
  flex: 0 0 auto;
  width: 240px;
  cursor: pointer;
  transition: all var(--pms-transition-fast, 0.2s);
  border-radius: 8px;
}
.phase-card:hover {
  transform: translateY(-2px);
}
.phase-card :deep(.el-card__body) {
  padding: 14px;
}
.phase-completed {
  border-color: #67c23a;
  background: #f0f9eb;
}
.phase-in-progress {
  border-color: var(--pms-color-primary, #409eff);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.25);
}
.phase-skipped {
  border-color: #c0c4cc;
  background: #f4f4f5;
  opacity: 0.75;
}
.phase-not-started {
  border-color: #ebeef5;
  background: #fafafa;
}
.phase-arrow {
  align-self: center;
  color: var(--pms-color-text-placeholder, #c0c4cc);
  font-size: 20px;
  margin: 0 4px;
}
.phase-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.phase-sort {
  font-size: 12px;
  color: var(--pms-color-text-placeholder, #909399);
  font-weight: 600;
}
.phase-name {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--pms-color-text-primary, #303133);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.phase-meta {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #606266);
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.meta-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.meta-label {
  color: var(--pms-color-text-placeholder, #909399);
}
.meta-value {
  color: var(--pms-color-text-primary, #303133);
}
.exit-gate-summary {
  display: flex;
  align-items: center;
  gap: 8px;
}
.exit-gate-summary :deep(.el-progress) {
  flex: 1;
}
.gate-text {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  white-space: nowrap;
}
.current-badge {
  margin-top: 8px;
  display: inline-block;
  padding: 2px 8px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: var(--pms-color-primary, #409eff);
  border-radius: 10px;
}
.phase-actions {
  margin-top: 10px;
  display: flex;
  gap: 6px;
  justify-content: flex-start;
}

/* ===== 时间轴视图 ===== */
.timeline-view {
  padding: 8px 0;
}
.timeline-card {
  cursor: pointer;
  transition: all var(--pms-transition-fast, 0.2s);
}
.timeline-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.timeline-card :deep(.el-card__body) {
  padding: 12px 16px;
}
.timeline-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}
.timeline-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  color: var(--pms-color-text-primary, #303133);
}
.timeline-meta {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #606266);
  margin-bottom: 8px;
}
.meta-divider {
  margin: 0 8px;
  color: var(--pms-color-border-light, #ebeef5);
}
.timeline-gate {
  display: flex;
  align-items: center;
  gap: 8px;
}
.timeline-gate :deep(.el-progress) {
  flex: 1;
}
/* 进行中节点脉冲动画 */
.timeline-active :deep(.el-timeline-item__node) {
  animation: pulse 1.6s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(64, 158, 255, 0.5);
  }
  50% {
    box-shadow: 0 0 0 6px rgba(64, 158, 255, 0);
  }
}

/* ===== 当前阶段提示 ===== */
.current-tip-wrap {
  margin-top: 8px;
}
.current-tip {
  padding: 10px 14px;
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 6px;
  color: var(--pms-color-text-primary, #303133);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.current-tip-warning {
  background: #fdf6ec;
  border-color: #faecd8;
}

/* ===== 抽屉内容 ===== */
.drawer-body {
  padding: 0 16px 24px;
}
.block {
  margin-bottom: 16px;
}
.detail-block {
  margin-top: 20px;
}
.block-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid var(--pms-color-primary, #409eff);
}
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 32px;
}
.upload-context {
  margin-bottom: 16px;
}
.form-first-block {
  margin-top: 0;
}
.phase-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 24px;
}
.phase-form-grid :deep(.el-form-item) {
  margin-bottom: 18px;
}
.exit-gate-editor-wrap {
  padding: 12px;
  border: 1px solid var(--pms-color-border-light, #ebeef5);
  border-radius: 6px;
  background: var(--pms-color-fill-lighter, #fafafa);
}
.entry-criteria-view {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding: 4px 0;
}
.entry-criteria-edit {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: center;
  padding: 8px 4px;
}
.entry-criteria-empty {
  color: var(--pms-color-text-secondary, #909399);
  font-size: 13px;
}
@media (max-width: 900px) {
  .phase-form-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== violations 弹窗 ===== */
.violation-summary {
  margin-bottom: 12px;
}
.violation-code {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  margin-top: 4px;
}
.violation-count {
  margin-top: 10px;
  font-size: 13px;
  color: var(--pms-color-text-regular, #606266);
  font-weight: 600;
}
</style>
