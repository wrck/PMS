<script setup lang="ts">
// =============================================================================
// PhaseManage - 阶段管理页（Task 7 重做）
// -----------------------------------------------------------------------------
// 双视图：流水线（横向卡片） + 时间轴（纵向 el-timeline），el-radio-group 切换。
// 阶段详情抽屉：基本信息 + 退出条件（PhaseExitGateEditor 只读）+ 必需交付件
//              + 阶段任务 + 阶段里程碑。
// 推进阶段：退出条件 violations 校验 + 二次确认。
// 入口：1) 工作区 Tab 嵌入（props.projectId）；2) 独立路由 /project/phase/:projectId。
// =============================================================================
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight, InfoFilled, WarningFilled } from '@element-plus/icons-vue'
import {
  advancePhase,
  listPhasesByProjectId,
  type PhaseExitGateResult,
  type PhaseExitGateViolation,
  type ProjectPhase
} from '@/api/project-phase'
import { getProject, listMilestones, type Milestone, type Project } from '@/api/project'
import { listFullDeliverables, type Deliverable } from '@/api/deliverable'
import { getTasksByProject, type ImplTask } from '@/api/implementation'
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
const selectedPhase = ref<ProjectPhase | null>(null)
const detailLoading = ref(false)
const phaseDeliverables = ref<Deliverable[]>([])
const phaseTasks = ref<ImplTask[]>([])
const phaseMilestones = ref<Milestone[]>([])

// ===== 推进 violations 弹窗 =====
const violationDialogVisible = ref(false)
const violationResult = ref<PhaseExitGateResult | null>(null)

/** 当前可推进的阶段（唯一一个 IN_PROGRESS） */
const currentPhase = computed<ProjectPhase | null>(
  () => phases.value.find((p) => p.status === 'IN_PROGRESS') ?? null
)

const inProgressCount = computed(() => phases.value.filter((p) => p.status === 'IN_PROGRESS').length)
const completedCount = computed(() => phases.value.filter((p) => p.status === 'COMPLETED').length)

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

async function reload() {
  await Promise.all([loadProject(), loadPhases()])
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
    phaseMilestones.value = (milestones ?? []).filter((m) => m.id != null)
  } finally {
    detailLoading.value = false
  }
}

// ===== 交互 =====
function selectPhase(phase: ProjectPhase) {
  selectedPhase.value = phase
  drawerVisible.value = true
  loadPhaseDetails(phase)
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
        <el-button
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
      :title="selectedPhase ? `阶段详情 · ${selectedPhase.phaseName}` : '阶段详情'"
      size="55%"
      destroy-on-close
    >
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="selectedPhase">
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

          <!-- 退出条件（只读） -->
          <div class="detail-block">
            <div class="block-title">退出条件</div>
            <PhaseExitGateEditor
              :model-value="selectedPhase.exitCriteria"
              :disabled="true"
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
              <el-table-column label="名称" min-width="180" prop="name" show-overflow-tooltip />
              <el-table-column label="类型" width="120" prop="type" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag size="small" type="info">{{ row.status || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="计划日期" width="120" align="center">
                <template #default="{ row }">{{ formatDate(row.plannedDate) }}</template>
              </el-table-column>
              <el-table-column label="实际日期" width="120" align="center">
                <template #default="{ row }">{{ formatDate(row.actualDate) }}</template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 推进按钮 -->
          <div v-if="selectedPhase.status === 'IN_PROGRESS'" class="drawer-footer">
            <el-button type="primary" :loading="advancing" @click="handleAdvance(selectedPhase)">
              推进该阶段
            </el-button>
          </div>
        </template>
      </div>
    </el-drawer>

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
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--pms-color-border-light, #ebeef5);
  display: flex;
  justify-content: flex-end;
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
