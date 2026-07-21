<script setup lang="ts">
// =============================================================================
// DeliverableLifecycle - 交付件全生命周期列表页（7 态状态机）
// -----------------------------------------------------------------------------
// - 视图切换：7 态分组视图（默认）/ 平铺列表视图
// - 7 态分组：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED
//     列头：DeliverableStatusBadge + 数量徽章
//     卡片：交付件名 + 编号 + 版本号 + 类型 + 负责人 + 修订次数
//     拖拽变更状态（仅允许 DRAFT→SUBMITTED→...→ARCHIVED 顺序流转）
// - 平铺列表：el-table，操作含详情/修订/状态变更/删除
// - 筛选：项目 / 阶段 / 类型 / 关键字
// - 分页：12 / 24 / 48
// - PageHeader + SkeletonCard + EmptyState + DeliverableStatusBadge
// =============================================================================
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  archiveDeliverable,
  createDeliverable,
  deleteDeliverable,
  DELIVERABLE_STATUS_LABELS,
  DELIVERABLE_STATUS_ORDER,
  listFullDeliverables,
  publishDeliverable,
  reviewDeliverable,
  reviseDeliverable,
  signDeliverable,
  submitDeliverable,
  type Deliverable,
  type DeliverableStatus,
  type ReviseRequest
} from '@/api/deliverable'
import { getProject, listProjects, type Project } from '@/api/project'
import { listPhasesByProjectId, type ProjectPhase } from '@/api/project-phase'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import DeliverableStatusBadge from '@/components/common/DeliverableStatusBadge.vue'

defineOptions({ name: 'DeliverableLifecycle' })

interface Props {
  projectId?: number | string
}
const props = defineProps<Props>()
const route = useRoute()
const router = useRouter()

// ============ 视图模式 ============
const viewMode = ref<'status' | 'list'>('status')

// ============ 数据 ============
const loading = ref(false)
const allDeliverables = ref<Deliverable[]>([])
const projectInfo = ref<Project | null>(null)
const phaseOptions = ref<ProjectPhase[]>([])
const projectOptions = ref<Project[]>([])
const total = ref(0)
const page = reactive({ current: 1, size: 12 })

// ============ 筛选 ============
const query = reactive({
  projectId: undefined as number | undefined,
  phaseId: undefined as number | undefined,
  type: undefined as string | undefined,
  keyword: ''
})

// ============ 当前 projectId：prop 优先，否则从 route.query ============
const currentProjectId = computed<number | undefined>(() => {
  if (props.projectId !== undefined && props.projectId !== null && props.projectId !== '') {
    const n = Number(props.projectId)
    return Number.isNaN(n) ? undefined : n
  }
  if (route.query.projectId) {
    const n = Number(route.query.projectId)
    return Number.isNaN(n) ? undefined : n
  }
  return undefined
})

const currentProjectName = computed(() => projectInfo.value?.projectName || '全部项目')
const headerTitle = computed(() => `交付件管理 · ${currentProjectName.value}`)

// ============ 7 态列 ============
const statusColumns = DELIVERABLE_STATUS_ORDER.map((s) => ({
  value: s,
  label: DELIVERABLE_STATUS_LABELS[s]
}))

// ============ 类型选项 ============
const typeOptions = [
  { label: '文档', value: 'DOCUMENT' },
  { label: '配置', value: 'CONFIG' },
  { label: '报告', value: 'REPORT' },
  { label: '其他', value: 'OTHER' }
]

function typeLabel(t?: string): string {
  return typeOptions.find((x) => x.value === t)?.label ?? t ?? '-'
}

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').slice(0, 19)
}

function ownerName(row: Deliverable): string {
  return row.updateBy || row.createBy || '-'
}

function revisionCount(row: Deliverable): number {
  return row.currentVersion ?? 1
}

/** 根据 phaseId 查找阶段名称（避免直接显示 ID） */
function phaseNameOf(phaseId?: number | null): string {
  if (!phaseId) return '-'
  const phase = phaseOptions.value.find((p) => p.id === phaseId)
  return phase?.phaseName ?? '-'
}

function getDeliverablesByStatus(status: DeliverableStatus): Deliverable[] {
  return filteredDeliverables.value.filter((d) => d.status === status)
}

// 当前状态可流转到的下一状态
function nextStatusOf(status?: DeliverableStatus): DeliverableStatus | null {
  if (!status) return null
  const idx = DELIVERABLE_STATUS_ORDER.indexOf(status)
  if (idx < 0 || idx >= DELIVERABLE_STATUS_ORDER.length - 1) return null
  return DELIVERABLE_STATUS_ORDER[idx + 1]
}

// ============ 客户端筛选 ============
const filteredDeliverables = computed<Deliverable[]>(() => {
  let list = allDeliverables.value
  if (query.phaseId) list = list.filter((d) => d.phaseId === query.phaseId)
  if (query.type) list = list.filter((d) => (d.deliverableType ?? 'DOCUMENT') === query.type)
  if (query.keyword) {
    const kw = query.keyword.trim().toLowerCase()
    list = list.filter((d) => (d.deliverableName || '').toLowerCase().includes(kw))
  }
  return list
})

// 平铺视图分页
const pagedDeliverables = computed<Deliverable[]>(() => {
  if (viewMode.value !== 'list') return filteredDeliverables.value
  const start = (page.current - 1) * page.size
  return filteredDeliverables.value.slice(start, start + page.size)
})

// ============ 数据加载 ============
async function loadProjectMeta() {
  const pid = currentProjectId.value
  if (!pid) {
    projectInfo.value = null
    phaseOptions.value = []
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
}

async function loadProjectOptions() {
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
    if (!pid) {
      allDeliverables.value = []
      total.value = 0
      return
    }
    const params: { projectId?: number; phaseId?: number } = { projectId: pid }
    if (query.phaseId) params.phaseId = query.phaseId
    allDeliverables.value = (await listFullDeliverables(params)) ?? []
    total.value = allDeliverables.value.length
  } catch {
    allDeliverables.value = []
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
  query.type = undefined
  query.keyword = ''
  page.current = 1
  loadData()
}

function handlePageChange(p: number) {
  page.current = p
}

function handleSizeChange(s: number) {
  page.size = s
  page.current = 1
}

// ============ 跳转详情 ============
function goDetail(row: Deliverable) {
  if (!row.id) return
  router.push(`/deliverable/detail/${row.id}`)
}

// ============ 新建交付件 ============
const createVisible = ref(false)
const createForm = ref<Deliverable>({
  projectId: 0,
  deliverableName: '',
  deliverableType: 'DOCUMENT',
  filePath: '',
  mandatory: false,
  phaseId: undefined,
  approverRole: ''
})

function openCreate() {
  createForm.value = {
    projectId: currentProjectId.value ?? 0,
    deliverableName: '',
    deliverableType: 'DOCUMENT',
    filePath: '',
    mandatory: false,
    phaseId: undefined,
    approverRole: ''
  }
  createVisible.value = true
}

async function handleCreate() {
  if (!createForm.value.deliverableName) {
    ElMessage.warning('请填写交付件名称')
    return
  }
  if (!createForm.value.projectId) {
    ElMessage.warning('请先选择项目')
    return
  }
  try {
    await createDeliverable(createForm.value)
    ElMessage.success('新建交付件成功')
    createVisible.value = false
    await loadData()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 行级状态变更（平铺列表 dropdown） ============
type StatusAction = 'submit' | 'review' | 'sign' | 'publish' | 'archive'

async function handleStatusAction(row: Deliverable, action: StatusAction) {
  if (!row.id) return
  const verbs: Record<StatusAction, string> = {
    submit: '提交评审',
    review: '审核通过',
    sign: '签核',
    publish: '发布',
    archive: '归档'
  }
  try {
    await ElMessageBox.confirm(
      `确认对「${row.deliverableName}」执行${verbs[action]}？`,
      '状态变更',
      { type: 'warning' }
    )
    switch (action) {
      case 'submit':
        await submitDeliverable(row.id)
        break
      case 'review':
        await reviewDeliverable(row.id, true)
        break
      case 'sign':
        await signDeliverable(row.id)
        break
      case 'publish':
        await publishDeliverable(row.id)
        break
      case 'archive':
        await archiveDeliverable(row.id)
        break
    }
    ElMessage.success('操作成功')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

function onStatusCommand(row: Deliverable, cmd: unknown) {
  handleStatusAction(row, cmd as StatusAction)
}

// ============ 修订 ============
const reviseVisible = ref(false)
const reviseRow = ref<Deliverable | null>(null)
const reviseForm = ref<ReviseRequest>({ filePath: '', changeLog: '' })

function openRevise(row: Deliverable) {
  reviseRow.value = row
  reviseForm.value = { filePath: '', changeLog: '' }
  reviseVisible.value = true
}

async function handleRevise() {
  if (!reviseRow.value?.id) return
  if (!reviseForm.value.filePath) {
    ElMessage.warning('请填写新版本文件路径')
    return
  }
  try {
    const newVersion = await reviseDeliverable(reviseRow.value.id, reviseForm.value)
    ElMessage.success(`修订成功，新版本 v${newVersion.versionNo}`)
    reviseVisible.value = false
    await loadData()
  } catch {
    /* handled by interceptor */
  }
}

// ============ 删除 ============
async function handleDelete(row: Deliverable) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(
      `确认删除草稿「${row.deliverableName}」？此操作不可恢复。`,
      '删除',
      { type: 'warning' }
    )
    await deleteDeliverable(row.id)
    ElMessage.success('已删除')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

// ============ 拖拽状态变更（HTML5 drag API） ============
const draggingItem = ref<Deliverable | null>(null)
const dragOverStatus = ref<DeliverableStatus | ''>('')

function onDragStart(event: DragEvent, item: Deliverable) {
  draggingItem.value = item
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', String(item.id ?? ''))
  }
}

function onDragOver(event: DragEvent, status: DeliverableStatus) {
  const drag = draggingItem.value
  // 仅允许拖到下一合法状态
  if (!drag || nextStatusOf(drag.status) !== status) return
  event.preventDefault()
  if (event.dataTransfer) event.dataTransfer.dropEffect = 'move'
  dragOverStatus.value = status
}

function onDragLeave(status: DeliverableStatus) {
  if (dragOverStatus.value === status) dragOverStatus.value = ''
}

async function onDrop(event: DragEvent, status: DeliverableStatus) {
  event.preventDefault()
  dragOverStatus.value = ''
  const item = draggingItem.value
  draggingItem.value = null
  if (!item?.id) return
  if (item.status === status) return
  const expected = nextStatusOf(item.status)
  if (expected !== status) {
    ElMessage.warning(
      `仅允许从「${DELIVERABLE_STATUS_LABELS[item.status ?? 'DRAFT']}」拖到下一状态` +
        (expected ? `「${DELIVERABLE_STATUS_LABELS[expected]}」` : '（已到终态）')
    )
    return
  }
  // PUBLISHED → REFERENCED 需要引用方信息，提示到详情页操作
  if (item.status === 'PUBLISHED' && status === 'REFERENCED') {
    ElMessage.info('PUBLISHED → REFERENCED 需新增引用关系，请到详情页操作')
    return
  }
  try {
    switch (status) {
      case 'SUBMITTED':
        await submitDeliverable(item.id)
        break
      case 'REVIEWED':
        await reviewDeliverable(item.id, true)
        break
      case 'SIGNED':
        await signDeliverable(item.id)
        break
      case 'PUBLISHED':
        await publishDeliverable(item.id)
        break
      case 'ARCHIVED':
        await archiveDeliverable(item.id)
        break
    }
    ElMessage.success(`已流转到「${DELIVERABLE_STATUS_LABELS[status]}」`)
    await loadData()
  } catch {
    /* handled by interceptor */
  }
}

function onDragEnd() {
  draggingItem.value = null
  dragOverStatus.value = ''
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
  query.projectId = currentProjectId.value
  await loadProjectOptions()
  await loadProjectMeta()
  loadData()
})
</script>

<template>
  <div class="deliverable-lifecycle-page">
    <PageHeader :title="headerTitle" description="7 态状态机分组视图，支持拖拽变更状态">
      <template #actions>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="status">按状态分组</el-radio-button>
          <el-radio-button label="list">平铺列表</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="openCreate">新建交付件</el-button>
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
        :disabled="!currentProjectId && !query.projectId"
      >
        <el-option
          v-for="ph in phaseOptions"
          :key="ph.id"
          :label="ph.phaseName"
          :value="ph.id!"
        />
      </el-select>
      <el-select v-model="query.type" placeholder="类型" clearable style="width: 120px">
        <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-input
        v-model="query.keyword"
        placeholder="搜索交付件名"
        clearable
        style="width: 220px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 加载骨架屏 -->
    <SkeletonCard v-if="loading" :loading="true" :rows="6">
      <div />
    </SkeletonCard>

    <template v-else>
      <!-- 空状态 -->
      <EmptyState
        v-if="filteredDeliverables.length === 0"
        title="暂无交付件"
        description="当前筛选条件下没有交付件数据，可调整筛选或新建交付件"
        icon="Files"
      >
        <template #action>
          <el-button type="primary" @click="openCreate">新建交付件</el-button>
        </template>
      </EmptyState>

      <!-- 7 态分组视图 -->
      <div v-else-if="viewMode === 'status'" class="status-columns">
        <div
          v-for="col in statusColumns"
          :key="col.value"
          class="status-column"
          :class="{ 'drag-over': dragOverStatus === col.value }"
          @dragover="onDragOver($event, col.value)"
          @dragleave="onDragLeave(col.value)"
          @drop="onDrop($event, col.value)"
        >
          <div class="column-header">
            <DeliverableStatusBadge :status="col.value" size="small" />
            <el-tag size="small" round effect="plain">
              {{ getDeliverablesByStatus(col.value).length }}
            </el-tag>
          </div>
          <div class="column-body">
            <div
              v-for="item in getDeliverablesByStatus(col.value)"
              :key="item.id"
              class="deliverable-card"
              draggable="true"
              @dragstart="onDragStart($event, item)"
              @dragend="onDragEnd"
              @click="goDetail(item)"
            >
              <div class="card-title" :title="item.deliverableName">{{ item.deliverableName }}</div>
              <div class="card-code">#{{ item.id }} · {{ typeLabel(item.deliverableType) }}</div>
              <div class="card-meta">
                <span class="version">v{{ item.currentVersion ?? 1 }}</span>
                <el-tag v-if="item.mandatory" type="danger" size="small" effect="plain">必需</el-tag>
              </div>
              <div class="card-footer">
                <span class="owner">{{ ownerName(item) }}</span>
                <span class="rev">修订 {{ revisionCount(item) }} 次</span>
              </div>
            </div>
            <div v-if="getDeliverablesByStatus(col.value).length === 0" class="column-empty">
              {{ nextStatusOf(col.value) ? '拖拽到此流转' : '终态状态' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 平铺列表视图 -->
      <div v-else class="list-view">
        <el-table :data="pagedDeliverables" border stripe>
          <el-table-column label="编号" width="80" align="center">
            <template #default="{ row }">#{{ row.id }}</template>
          </el-table-column>
          <el-table-column prop="deliverableName" label="交付件名称" min-width="200" show-overflow-tooltip />
          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">{{ typeLabel(row.deliverableType) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <DeliverableStatusBadge :status="row.status || 'DRAFT'" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="版本" width="80" align="center">
            <template #default="{ row }">v{{ row.currentVersion ?? 1 }}</template>
          </el-table-column>
          <el-table-column label="负责人" width="110">
            <template #default="{ row }">{{ ownerName(row) }}</template>
          </el-table-column>
          <el-table-column label="阶段" width="100" align="center">
            <template #default="{ row }">{{ phaseNameOf(row.phaseId) }}</template>
          </el-table-column>
          <el-table-column label="修订次数" width="90" align="center">
            <template #default="{ row }">{{ revisionCount(row) }}</template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" align="center">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="goDetail(row)">详情</el-button>
              <el-button
                v-if="row.status === 'PUBLISHED' || row.status === 'REFERENCED'"
                link
                type="primary"
                size="small"
                @click="openRevise(row)"
              >修订</el-button>
              <el-dropdown
                v-if="row.status && row.status !== 'ARCHIVED' && nextStatusOf(row.status)"
                trigger="click"
                @command="(cmd: unknown) => onStatusCommand(row, cmd)"
              >
                <el-button link type="primary" size="small">状态变更 ▾</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="row.status === 'DRAFT'" command="submit">提交评审</el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'SUBMITTED'" command="review">审核通过</el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'REVIEWED'" command="sign">签核</el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'SIGNED'" command="publish">发布</el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'REFERENCED'" command="archive">归档</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button
                v-if="row.status === 'DRAFT'"
                link
                type="danger"
                size="small"
                @click="handleDelete(row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          class="pagination"
          background
          :current-page="page.current"
          :page-size="page.size"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </template>

    <!-- 新建弹窗 -->
    <el-dialog v-model="createVisible" title="新建交付件" width="520px" destroy-on-close>
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="交付件名称" required>
          <el-input v-model="createForm.deliverableName" placeholder="请输入交付件名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="createForm.deliverableType" style="width: 100%">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="createForm.phaseId" placeholder="可选" clearable style="width: 100%">
            <el-option v-for="ph in phaseOptions" :key="ph.id" :label="ph.phaseName" :value="ph.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件路径">
          <el-input v-model="createForm.filePath" placeholder="可留空，后续修订时上传" />
        </el-form-item>
        <el-form-item label="签核角色">
          <el-input v-model="createForm.approverRole" placeholder="如：技术负责人" />
        </el-form-item>
        <el-form-item label="必需交付件">
          <el-switch v-model="createForm.mandatory" />
          <span class="hint">必需交付件影响阶段退出校验</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修订弹窗 -->
    <el-dialog v-model="reviseVisible" title="修订交付件（新建版本）" width="520px" destroy-on-close>
      <div v-if="reviseRow" class="revise-tip">
        当前：<strong>{{ reviseRow.deliverableName }}</strong>
        （版本 v{{ reviseRow.currentVersion ?? 1 }} / 状态 {{ DELIVERABLE_STATUS_LABELS[reviseRow.status ?? 'DRAFT'] }}）
        <br />修订后将创建 v{{ (reviseRow.currentVersion ?? 1) + 1 }} 新版本，旧版本记录保留不变。
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
  </div>
</template>

<style scoped>
.deliverable-lifecycle-page {
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

.list-view {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pagination {
  justify-content: flex-end;
}

.hint {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

.revise-tip {
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f4f4f5;
  border-radius: 4px;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}

/* ============ 7 态分组视图 ============ */
.status-columns {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  min-height: 480px;
}

.status-column {
  flex: 1;
  min-width: 220px;
  max-width: 280px;
  background: var(--pms-color-bg-page, #f5f7fa);
  border-radius: var(--pms-radius-lg, 8px);
  padding: 12px;
  display: flex;
  flex-direction: column;
  transition: background 0.15s ease, box-shadow 0.15s ease;
}

.status-column.drag-over {
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

.deliverable-card {
  background: var(--pms-color-bg-card, #fff);
  border-radius: 6px;
  padding: 10px 12px;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  user-select: none;
}

.deliverable-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.deliverable-card:active {
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
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.card-meta .version {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  font-family: 'Courier New', monospace;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
}

.card-footer .owner {
  font-weight: 500;
}

.card-footer .rev {
  font-family: 'Courier New', monospace;
}
</style>
