<script setup lang="ts">
// =============================================================================
// DeliverableDetail - 交付件详情页
// -----------------------------------------------------------------------------
// 顶部 PageHeader：交付件名 + 状态徽章 + 关键操作（修订 / 状态变更 / 删除）
// 主区：
//   - 状态流可视化（DeliverableStatusFlow 组件）
//   - 基本信息卡片
//   - 当前版本卡片
//   - 修订版本列表（DeliverableVersionList 组件）
//   - 签名记录列表
//   - 引用记录列表
// 右侧侧栏：
//   - 状态变更卡片
//   - 操作历史卡片（合并版本/签名/引用/状态变更时间线）
// =============================================================================
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import {
  addDeliverableReference,
  addDeliverableSignature,
  archiveDeliverable,
  DELIVERABLE_STATUS_LABELS,
  DELIVERABLE_STATUS_ORDER,
  deleteDeliverable,
  getDeliverable,
  getReferencedEntitySummary,
  listDeliverableReferences,
  listDeliverableSignatures,
  listDeliverableVersions,
  publishDeliverable,
  reviewDeliverable,
  reviseDeliverable,
  signDeliverable,
  submitDeliverable,
  translateDeliverableType,
  translateRefEntityType,
  type Deliverable,
  type DeliverableReference,
  type DeliverableSignature,
  type DeliverableStatus,
  type DeliverableVersion,
  type ReferencedEntitySummary,
  type ReviseRequest
} from '@/api/deliverable'
import { getProject, type Project } from '@/api/project'
import { listPhasesByProjectId, type ProjectPhase } from '@/api/project-phase'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import DeliverableStatusBadge from '@/components/common/DeliverableStatusBadge.vue'
import DeliverableStatusFlow from '@/components/DeliverableStatusFlow.vue'
import DeliverableVersionList from '@/components/DeliverableVersionList.vue'

defineOptions({ name: 'DeliverableDetail' })

const route = useRoute()
const router = useRouter()
const deliverableId = computed(() => Number(route.params.id))

const loading = ref(false)
const deliverable = ref<Deliverable | null>(null)
const versions = ref<DeliverableVersion[]>([])
const signatures = ref<DeliverableSignature[]>([])
const references = ref<DeliverableReference[]>([])
const projectInfo = ref<Project | null>(null)
const phaseOptions = ref<ProjectPhase[]>([])
const refEntitySummary = ref<ReferencedEntitySummary | null>(null)
const refEntityLoading = ref(false)

const versionListRef = ref<InstanceType<typeof DeliverableVersionList> | null>(null)

function typeLabel(t?: string): string {
  return translateDeliverableType(t)
}

/** 当前交付件是否为实体引用类 */
const isEntityRef = computed(
  () => deliverable.value?.deliverableType === 'ENTITY_REF'
)

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').slice(0, 19)
}

// ============ 项目 / 阶段名称解析（避免直接显示 ID）============
const projectNameText = computed(() => {
  const name = deliverable.value?.projectName
  if (name && name.trim()) return name
  return projectInfo.value?.projectName || '-'
})

function phaseNameOf(phaseId?: number | null): string {
  if (!phaseId) return '-'
  const phase = phaseOptions.value.find((p) => p.id === phaseId)
  return phase?.phaseName ?? '-'
}

// ============ 当前状态 / 下一状态 ============
function nextStatusOf(status?: DeliverableStatus): DeliverableStatus | null {
  if (!status) return null
  const idx = DELIVERABLE_STATUS_ORDER.indexOf(status)
  if (idx < 0 || idx >= DELIVERABLE_STATUS_ORDER.length - 1) return null
  return DELIVERABLE_STATUS_ORDER[idx + 1]
}

const nextStatus = computed<DeliverableStatus | null>(() => nextStatusOf(deliverable.value?.status))

// 顶部按钮：是否可执行状态变更（下一状态存在且不是 REFERENCED）
const canTransition = computed(() => {
  const next = nextStatus.value
  return !!next && next !== 'REFERENCED'
})

// ============ 当前版本（从版本列表取最新或回退到 deliverable）============
const currentVersion = computed<DeliverableVersion | null>(() => {
  if (versions.value.length > 0) return versions.value[0]
  if (!deliverable.value) return null
  return {
    id: undefined,
    deliverableId: deliverable.value.id ?? 0,
    versionNo: deliverable.value.currentVersion ?? 1,
    filePath: deliverable.value.filePath ?? '',
    uploadedAt: deliverable.value.updateTime ?? deliverable.value.createTime,
    uploadedBy: undefined,
    changeLog: '初始版本'
  }
})

// ============ 操作历史时间线 ============
interface HistoryEntry {
  id: string
  action: string
  operator: string
  timestamp: string
  detail?: string
}

const historyTimeline = computed<HistoryEntry[]>(() => {
  const entries: HistoryEntry[] = []
  // 创建
  if (deliverable.value) {
    entries.push({
      id: 'create',
      action: '创建',
      operator: deliverable.value.createBy ?? '-',
      timestamp: deliverable.value.createTime ?? '',
      detail: `创建交付件「${deliverable.value.deliverableName}」`
    })
  }
  // 版本上传（跳过第一个，对应创建）
  versions.value.forEach((v, idx) => {
    if (idx === 0) return
    entries.push({
      id: `version-${v.id ?? v.versionNo}`,
      action: '修订',
      operator: v.uploadedBy != null ? String(v.uploadedBy) : '-',
      timestamp: v.uploadedAt ?? '',
      detail: v.changeLog || `新增 v${v.versionNo} 版本`
    })
  })
  // 签名
  signatures.value.forEach((s) => {
    entries.push({
      id: `sig-${s.id ?? s.signedAt}`,
      action: '签名',
      operator: s.signerName ?? String(s.signerId),
      timestamp: s.signedAt ?? '',
      detail: `${s.signerRole ?? ''} ${s.signatureType ?? 'ELECTRONIC'}`.trim()
    })
  })
  // 引用
  references.value.forEach((r) => {
    entries.push({
      id: `ref-${r.id ?? r.createTime}`,
      action: '引用',
      operator: r.referencedByName ?? String(r.referencedById),
      timestamp: r.createTime ?? '',
      detail: `${r.referenceType} #${r.referencedById}`
    })
  })
  // 状态变更里程碑
  if (deliverable.value?.publishedAt) {
    entries.push({
      id: 'publish',
      action: '发布',
      operator: deliverable.value.updateBy ?? '-',
      timestamp: deliverable.value.publishedAt,
      detail: '交付件发布'
    })
  }
  if (deliverable.value?.archivedAt) {
    entries.push({
      id: 'archive',
      action: '归档',
      operator: deliverable.value.updateBy ?? '-',
      timestamp: deliverable.value.archivedAt,
      detail: '交付件归档'
    })
  }
  // 按时间倒序，最多 10 条
  return entries
    .filter((e) => e.timestamp)
    .sort((a, b) => (a.timestamp < b.timestamp ? 1 : -1))
    .slice(0, 10)
})

// ============ 数据加载 ============
async function loadDetail() {
  if (!deliverableId.value) return
  loading.value = true
  try {
    deliverable.value = await getDeliverable(deliverableId.value)
  } catch {
    deliverable.value = null
  } finally {
    loading.value = false
  }
  if (deliverable.value?.projectId) {
    getProject(deliverable.value.projectId)
      .then((proj) => {
        projectInfo.value = proj
        if (deliverable.value && !deliverable.value.projectName) {
          deliverable.value.projectName = proj.projectName
        }
      })
      .catch(() => {
        projectInfo.value = null
      })
    listPhasesByProjectId(deliverable.value.projectId)
      .then((list) => {
        phaseOptions.value = list ?? []
      })
      .catch(() => {
        phaseOptions.value = []
      })
  }
}

async function loadVersions() {
  if (!deliverableId.value) return
  try {
    versions.value = (await listDeliverableVersions(deliverableId.value)) ?? []
  } catch {
    versions.value = []
  }
}

async function loadSignatures() {
  if (!deliverableId.value) return
  try {
    signatures.value = (await listDeliverableSignatures(deliverableId.value)) ?? []
  } catch {
    signatures.value = []
  }
}

async function loadReferences() {
  if (!deliverableId.value) return
  try {
    references.value = (await listDeliverableReferences(deliverableId.value)) ?? []
  } catch {
    references.value = []
  }
}

/** 加载被引用实体的概要信息（仅 ENTITY_REF 类型触发） */
async function loadRefEntitySummary() {
  const d = deliverable.value
  if (!d || d.deliverableType !== 'ENTITY_REF' || !d.refEntityType || !d.refEntityId) {
    refEntitySummary.value = null
    return
  }
  refEntityLoading.value = true
  try {
    refEntitySummary.value = await getReferencedEntitySummary(d.refEntityType, d.refEntityId)
  } catch {
    refEntitySummary.value = null
  } finally {
    refEntityLoading.value = false
  }
}

/** 跳转到被引用实体详情页 */
function goRefEntityDetail() {
  const summary = refEntitySummary.value
  if (summary?.detailUrl) {
    window.open(summary.detailUrl, '_blank')
  } else {
    ElMessage.info('该实体暂无详情页地址')
  }
}

async function loadAll() {
  await loadDetail()
  await Promise.all([
    loadVersions(),
    loadSignatures(),
    loadReferences(),
    loadRefEntitySummary()
  ])
  // 同步刷新子组件版本列表
  versionListRef.value?.refresh?.()
}

// ============ 状态变更 ============
const transitionDialog = ref(false)
const transitionForm = ref<{ target: DeliverableStatus; reason: string }>({
  target: 'SUBMITTED',
  reason: ''
})

function openTransition(target: DeliverableStatus) {
  transitionForm.value = { target, reason: '' }
  transitionDialog.value = true
}

function handleTransitionClick() {
  const next = nextStatus.value
  if (!next) return
  // PUBLISHED → REFERENCED 需新增引用
  if (next === 'REFERENCED') {
    ElMessage.info('PUBLISHED → REFERENCED 需新增引用关系，请在「引用记录」中操作')
    return
  }
  openTransition(next)
}

async function handleTransition() {
  const id = deliverable.value?.id
  if (!id) return
  const target = transitionForm.value.target
  try {
    switch (target) {
      case 'SUBMITTED':
        await submitDeliverable(id)
        break
      case 'REVIEWED':
        await reviewDeliverable(id, true)
        break
      case 'SIGNED':
        await signDeliverable(id)
        break
      case 'PUBLISHED':
        await publishDeliverable(id)
        break
      case 'ARCHIVED':
        await archiveDeliverable(id)
        break
      case 'REFERENCED':
        ElMessage.info('PUBLISHED → REFERENCED 需通过「新增引用」操作')
        return
    }
    ElMessage.success(`已流转到「${DELIVERABLE_STATUS_LABELS[target]}」`)
    transitionDialog.value = false
    await loadAll()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 修订 ============
const reviseVisible = ref(false)
const reviseForm = ref<ReviseRequest>({ filePath: '', changeLog: '' })

function openRevise() {
  reviseForm.value = { filePath: '', changeLog: '' }
  reviseVisible.value = true
}

async function handleRevise() {
  if (!deliverable.value?.id) return
  if (!reviseForm.value.filePath) {
    ElMessage.warning('请填写新版本文件路径')
    return
  }
  try {
    const newVersion = await reviseDeliverable(deliverable.value.id, reviseForm.value)
    ElMessage.success(`修订成功，新版本 v${newVersion.versionNo}`)
    reviseVisible.value = false
    await loadAll()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 删除 ============
async function handleDelete() {
  if (!deliverable.value?.id) return
  try {
    await ElMessageBox.confirm(
      `确认删除草稿「${deliverable.value.deliverableName}」？此操作不可恢复。`,
      '删除',
      { type: 'warning' }
    )
    await deleteDeliverable(deliverable.value.id)
    ElMessage.success('已删除')
    router.back()
  } catch {
    /* cancelled or error */
  }
}

// ============ 新增签名 ============
const sigDialogVisible = ref(false)
const sigForm = ref<Partial<DeliverableSignature>>({
  signerId: 0,
  signerName: '',
  signerRole: '',
  signatureType: 'ELECTRONIC',
  signatureData: ''
})

function openAddSignature() {
  sigForm.value = {
    signerId: 0,
    signerName: '',
    signerRole: '',
    signatureType: 'ELECTRONIC',
    signatureData: ''
  }
  sigDialogVisible.value = true
}

async function handleAddSignature() {
  if (!sigForm.value.signerId) {
    ElMessage.warning('请填写签核人 ID')
    return
  }
  try {
    await addDeliverableSignature(deliverableId.value, sigForm.value)
    ElMessage.success('签名记录已新增')
    sigDialogVisible.value = false
    await loadSignatures()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 新增引用 ============
const refDialogVisible = ref(false)
const refForm = ref<Partial<DeliverableReference>>({
  referenceType: 'TASK',
  referencedById: 0,
  referencedByName: '',
  targetDeliverableId: undefined
})

function openAddReference() {
  refForm.value = {
    referenceType: 'TASK',
    referencedById: 0,
    referencedByName: '',
    targetDeliverableId: undefined
  }
  refDialogVisible.value = true
}

async function handleAddReference() {
  if (!refForm.value.referencedById) {
    ElMessage.warning('请填写引用方业务 ID')
    return
  }
  try {
    await addDeliverableReference(deliverableId.value, refForm.value)
    ElMessage.success('引用关系已新增（若源为 PUBLISHED 将自动流转为 REFERENCED）')
    refDialogVisible.value = false
    await loadAll()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 下载 ============
function handleDownloadVersion(v: DeliverableVersion) {
  if (!v.filePath) {
    ElMessage.warning('该版本无文件路径')
    return
  }
  window.open(v.filePath, '_blank')
}

onMounted(loadAll)
</script>

<template>
  <div class="deliverable-detail-page">
    <SkeletonCard v-if="loading" :loading="true" :rows="8">
      <div />
    </SkeletonCard>

    <EmptyState
      v-else-if="!deliverable"
      title="未找到交付件"
      description="该交付件可能已被删除或 ID 无效"
      icon="Warning"
    />

    <template v-else>
      <!-- PageHeader -->
      <PageHeader
        :title="deliverable.deliverableName"
        :description="`编号 #${deliverable.id} · ${typeLabel(deliverable.deliverableType)}`"
      >
        <template #actions>
          <DeliverableStatusBadge :status="deliverable.status || 'DRAFT'" size="default" />
          <el-button
            v-if="deliverable.status === 'PUBLISHED' || deliverable.status === 'REFERENCED'"
            type="primary"
            @click="openRevise"
          >修订</el-button>
          <el-button
            v-if="canTransition"
            type="success"
            @click="handleTransitionClick"
          >状态变更</el-button>
          <el-button
            v-if="deliverable.status === 'DRAFT'"
            type="danger"
            plain
            @click="handleDelete"
          >删除</el-button>
        </template>
      </PageHeader>

      <div class="detail-body">
        <!-- 主区 -->
        <div class="detail-main">
          <!-- 状态流可视化 -->
          <el-card shadow="never" class="section-card">
            <template #header><span class="section-title">状态流可视化</span></template>
            <DeliverableStatusFlow :current="deliverable.status" />
            <div v-if="nextStatus" class="flow-hint">
              <span>下一状态：</span>
              <DeliverableStatusBadge :status="nextStatus" size="small" />
            </div>
          </el-card>

          <!-- 基本信息 -->
          <el-card shadow="never" class="section-card">
            <template #header><span class="section-title">基本信息</span></template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="编号">#{{ deliverable.id }}</el-descriptions-item>
              <el-descriptions-item label="名称">{{ deliverable.deliverableName }}</el-descriptions-item>
              <el-descriptions-item label="类型">{{ typeLabel(deliverable.deliverableType) }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <DeliverableStatusBadge :status="deliverable.status || 'DRAFT'" size="small" />
              </el-descriptions-item>
              <el-descriptions-item label="所属项目">{{ projectNameText }}</el-descriptions-item>
              <el-descriptions-item label="所属阶段">{{ phaseNameOf(deliverable.phaseId) }}</el-descriptions-item>
              <el-descriptions-item label="必需">
                <el-tag v-if="deliverable.mandatory" type="danger" size="small">必需</el-tag>
                <el-tag v-else type="info" size="small">可选</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="签核角色">{{ deliverable.approverRole ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ deliverable.updateBy || deliverable.createBy || '-' }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(deliverable.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="最后修改时间">{{ formatDateTime(deliverable.updateTime) }}</el-descriptions-item>
              <el-descriptions-item v-if="isEntityRef" label="引用实体类型">
                <el-tag size="small" type="warning">{{ translateRefEntityType(deliverable.refEntityType) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item v-if="isEntityRef" label="引用实体ID">
                #{{ deliverable.refEntityId ?? '-' }}
              </el-descriptions-item>
              <el-descriptions-item v-else label="文件路径">{{ deliverable.filePath ?? '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 引用实体概要（仅 ENTITY_REF 类型显示） -->
          <el-card v-if="isEntityRef" shadow="never" class="section-card">
            <template #header>
              <div class="section-header">
                <span class="section-title">引用实体概要</span>
                <el-button
                  v-if="refEntitySummary?.detailUrl"
                  type="primary"
                  size="small"
                  @click="goRefEntityDetail"
                >查看实体详情</el-button>
              </div>
            </template>
            <div v-if="refEntityLoading" class="ref-entity-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载引用实体信息...</span>
            </div>
            <el-descriptions v-else-if="refEntitySummary" :column="2" border>
              <el-descriptions-item label="实体类型">
                <el-tag size="small" type="warning">{{ translateRefEntityType(refEntitySummary.refEntityType) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="实体ID">#{{ refEntitySummary.refEntityId }}</el-descriptions-item>
              <el-descriptions-item label="实体名称" :span="2">
                {{ refEntitySummary.name ?? '-' }}
              </el-descriptions-item>
              <el-descriptions-item v-if="refEntitySummary.projectId" label="所属项目">
                #{{ refEntitySummary.projectId }}
              </el-descriptions-item>
              <el-descriptions-item v-if="refEntitySummary.hostname" label="主机名">
                {{ refEntitySummary.hostname }}
              </el-descriptions-item>
              <el-descriptions-item v-if="refEntitySummary.detailUrl" label="详情地址" :span="2">
                <el-link type="primary" :href="refEntitySummary.detailUrl" target="_blank">
                  {{ refEntitySummary.detailUrl }}
                </el-link>
              </el-descriptions-item>
            </el-descriptions>
            <el-empty v-else description="无法加载引用实体信息，可能该实体已被删除" />
          </el-card>

          <!-- 当前版本 -->
          <el-card v-if="currentVersion" shadow="never" class="section-card">
            <template #header><span class="section-title">当前版本</span></template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="版本号">v{{ currentVersion.versionNo }}</el-descriptions-item>
              <el-descriptions-item label="文件名">{{ currentVersion.filePath || '-' }}</el-descriptions-item>
              <el-descriptions-item label="上传时间">{{ formatDateTime(currentVersion.uploadedAt) }}</el-descriptions-item>
              <el-descriptions-item label="上传人">{{ currentVersion.uploadedBy ?? '-' }}</el-descriptions-item>
            </el-descriptions>
            <div class="version-actions">
              <el-button
                v-if="currentVersion.filePath"
                type="primary"
                size="small"
                @click="handleDownloadVersion(currentVersion)"
              >下载</el-button>
              <el-button
                v-if="currentVersion.filePath"
                size="small"
                @click="handleDownloadVersion(currentVersion)"
              >预览</el-button>
            </div>
          </el-card>

          <!-- 修订版本列表 -->
          <el-card shadow="never" class="section-card">
            <template #header><span class="section-title">修订版本列表</span></template>
            <DeliverableVersionList
              ref="versionListRef"
              :deliverable-id="deliverableId"
              @download="handleDownloadVersion"
            />
          </el-card>

          <!-- 签名记录 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <div class="section-header">
                <span class="section-title">签名记录（{{ signatures.length }}）</span>
                <el-button type="primary" size="small" @click="openAddSignature">新增签名</el-button>
              </div>
            </template>
            <el-table :data="signatures" border stripe>
              <el-table-column prop="versionNo" label="版本" width="80" align="center">
                <template #default="{ row }">v{{ row.versionNo ?? '-' }}</template>
              </el-table-column>
              <el-table-column prop="signatureType" label="签名类型" width="120" align="center">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.signatureType ?? 'ELECTRONIC' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="signerName" label="签名人" min-width="120">
                <template #default="{ row }">
                  {{ row.signerName ?? `#${row.signerId}` }}
                </template>
              </el-table-column>
              <el-table-column prop="signerRole" label="角色" width="120">
                <template #default="{ row }">{{ row.signerRole ?? '-' }}</template>
              </el-table-column>
              <el-table-column label="签名时间" width="160" align="center">
                <template #default="{ row }">
                  <span v-if="row.signedAt">{{ formatDateTime(row.signedAt) }}</span>
                  <el-tag v-else type="warning" size="small">等待签名</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="signatureData" label="签名数据" min-width="160" show-overflow-tooltip>
                <template #default="{ row }">{{ row.signatureData ?? '-' }}</template>
              </el-table-column>
              <template #empty>
                <el-empty description="暂无签名记录" />
              </template>
            </el-table>
          </el-card>

          <!-- 引用记录 -->
          <el-card shadow="never" class="section-card">
            <template #header>
              <div class="section-header">
                <span class="section-title">引用记录（{{ references.length }}）</span>
                <el-button type="primary" size="small" @click="openAddReference">新增引用</el-button>
              </div>
            </template>
            <el-table :data="references" border stripe>
              <el-table-column prop="referenceType" label="引用类型" width="120" align="center">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.referenceType }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="引用项目/方" min-width="140">
                <template #default="{ row }">{{ row.referencedByName ?? `#${row.referencedById}` }}</template>
              </el-table-column>
              <el-table-column label="引用阶段" width="120" align="center">
                <template #default="{ row }">{{ row.referenceType === 'PHASE' ? `#${row.referencedById}` : '-' }}</template>
              </el-table-column>
              <el-table-column label="引用人" width="120">
                <template #default="{ row }">{{ row.createBy ?? '-' }}</template>
              </el-table-column>
              <el-table-column label="引用时间" width="160" align="center">
                <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
              </el-table-column>
              <template #empty>
                <el-empty description="暂无引用记录" />
              </template>
            </el-table>
          </el-card>
        </div>

        <!-- 右侧侧栏 -->
        <div class="detail-aside">
          <!-- 状态变更卡片 -->
          <el-card shadow="never" class="aside-card">
            <template #header><span class="section-title">状态变更</span></template>
            <div class="transition-block">
              <div class="status-row">
                <span class="label">当前状态</span>
                <DeliverableStatusBadge :status="deliverable.status || 'DRAFT'" size="small" />
              </div>
              <div v-if="nextStatus" class="status-row">
                <span class="label">可流转到</span>
                <el-button
                  type="primary"
                  size="small"
                  @click="handleTransitionClick"
                >
                  <DeliverableStatusBadge :status="nextStatus" size="small" />
                </el-button>
              </div>
              <div v-else class="terminal-tip">
                <el-tag type="info" size="small">已到终态</el-tag>
              </div>
            </div>
          </el-card>

          <!-- 操作历史 -->
          <el-card shadow="never" class="aside-card">
            <template #header><span class="section-title">操作历史（最近 10 条）</span></template>
            <el-timeline v-if="historyTimeline.length > 0">
              <el-timeline-item
                v-for="entry in historyTimeline"
                :key="entry.id"
                :timestamp="formatDateTime(entry.timestamp)"
                placement="top"
              >
                <div class="timeline-action">
                  <el-tag size="small" effect="plain">{{ entry.action }}</el-tag>
                  <span class="timeline-operator">{{ entry.operator }}</span>
                </div>
                <div v-if="entry.detail" class="timeline-detail">{{ entry.detail }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无操作历史" />
          </el-card>
        </div>
      </div>

      <!-- 修订对话框 -->
      <el-dialog v-model="reviseVisible" title="修订交付件（新建版本）" width="520px" destroy-on-close>
        <div class="revise-tip">
          当前版本 v{{ deliverable.currentVersion ?? 1 }} / 状态 {{ DELIVERABLE_STATUS_LABELS[deliverable.status ?? 'DRAFT'] }}
          <br />修订后将创建 v{{ (deliverable.currentVersion ?? 1) + 1 }} 新版本，旧版本记录保留不变。
        </div>
        <el-form :model="reviseForm" label-width="100px">
          <el-form-item label="新文件路径" required>
            <el-input v-model="reviseForm.filePath" placeholder="如：/files/impl-plan-v2.docx" />
          </el-form-item>
          <el-form-item label="变更说明">
            <el-input v-model="reviseForm.changeLog" type="textarea" :rows="3" placeholder="本次修订的变更说明" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="reviseVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRevise">确定修订</el-button>
        </template>
      </el-dialog>

      <!-- 状态变更对话框 -->
      <el-dialog v-model="transitionDialog" title="状态变更" width="500px" destroy-on-close>
        <el-form label-width="100px">
          <el-form-item label="目标状态">
            <DeliverableStatusBadge :status="transitionForm.target" size="small" />
          </el-form-item>
          <el-form-item label="变更原因">
            <el-input
              v-model="transitionForm.reason"
              type="textarea"
              :rows="3"
              placeholder="请说明本次状态变更的原因（可选）"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="transitionDialog = false">取消</el-button>
          <el-button type="primary" @click="handleTransition">确定流转</el-button>
        </template>
      </el-dialog>

      <!-- 新增签名弹窗 -->
      <el-dialog v-model="sigDialogVisible" title="新增签名记录" width="520px" destroy-on-close>
        <el-form :model="sigForm" label-width="100px">
          <el-form-item label="签核人 ID" required>
            <el-input-number v-model="sigForm.signerId" :min="1" :controls="false" style="width: 100%" />
          </el-form-item>
          <el-form-item label="签核人姓名">
            <el-input v-model="sigForm.signerName" placeholder="冗余字段，便于展示" />
          </el-form-item>
          <el-form-item label="签核角色">
            <el-input v-model="sigForm.signerRole" placeholder="如：技术负责人" />
          </el-form-item>
          <el-form-item label="签名类型">
            <el-select v-model="sigForm.signatureType" style="width: 100%">
              <el-option label="电子签名" value="ELECTRONIC" />
              <el-option label="印章" value="STAMP" />
              <el-option label="数字签名" value="DIGITAL" />
            </el-select>
          </el-form-item>
          <el-form-item label="签名数据">
            <el-input v-model="sigForm.signatureData" placeholder="证书指纹/印章图URL" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="sigDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddSignature">确定</el-button>
        </template>
      </el-dialog>

      <!-- 新增引用弹窗 -->
      <el-dialog v-model="refDialogVisible" title="新增引用关系" width="520px" destroy-on-close>
        <div class="ref-tip">
          注：仅 PUBLISHED 或 REFERENCED 状态的交付件可被引用。新增引用后，若源为
          PUBLISHED 将自动流转为 REFERENCED。
        </div>
        <el-form :model="refForm" label-width="110px">
          <el-form-item label="引用方类型" required>
            <el-select v-model="refForm.referenceType" style="width: 100%">
              <el-option label="任务" value="TASK" />
              <el-option label="阶段" value="PHASE" />
              <el-option label="项目" value="PROJECT" />
              <el-option label="交付件" value="DELIVERABLE" />
              <el-option label="报告" value="REPORT" />
            </el-select>
          </el-form-item>
          <el-form-item label="引用方 ID" required>
            <el-input-number v-model="refForm.referencedById" :min="1" :controls="false" style="width: 100%" />
          </el-form-item>
          <el-form-item label="引用方名称">
            <el-input v-model="refForm.referencedByName" placeholder="冗余字段，便于展示" />
          </el-form-item>
          <el-form-item label="目标交付件 ID">
            <el-input-number v-model="refForm.targetDeliverableId" :min="1" :controls="false" style="width: 100%" placeholder="引用方为交付件时填写" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="refDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddReference">确定</el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<style scoped>
.deliverable-detail-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-body {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 12px;
  align-items: start;
}

.detail-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.detail-aside {
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: sticky;
  top: 12px;
}

.section-card,
.aside-card {
  border-radius: var(--pms-radius-lg, 8px);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #303133);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flow-hint {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--pms-color-text-secondary, #909399);
}

.version-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.transition-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.status-row .label {
  color: var(--pms-color-text-secondary, #909399);
  min-width: 60px;
}

.terminal-tip {
  text-align: center;
  padding: 8px 0;
}

.timeline-action {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.timeline-operator {
  font-size: 13px;
  color: var(--pms-color-text-regular, #606266);
  font-weight: 500;
}

.timeline-detail {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

.revise-tip,
.ref-tip {
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f4f4f5;
  border-radius: 4px;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}

.ref-tip {
  background: #fdf6ec;
  color: #e6a23c;
}

.ref-entity-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 0;
  color: var(--pms-color-text-secondary, #909399);
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .detail-body {
    grid-template-columns: 1fr;
  }
  .detail-aside {
    position: static;
  }
}
</style>
