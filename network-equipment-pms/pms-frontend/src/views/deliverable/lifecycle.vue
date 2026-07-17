<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
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
import { listProjects, type Project } from '@/api/project'
import type { EpTagType } from '@/types'

defineOptions({ name: 'DeliverableLifecycle' })

const router = useRouter()

const loading = ref(false)
const projectOptions = ref<Project[]>([])
const selectedProjectId = ref<number | undefined>(undefined)
const phaseIdFilter = ref<number | undefined>(undefined)
const activeStatus = ref<DeliverableStatus | ''>('')
const tableData = ref<Deliverable[]>([])

// ============ 状态标签元数据 ============
function statusMeta(status?: DeliverableStatus | string): { label: string; tagType: EpTagType } {
  const s = (status ?? 'DRAFT') as DeliverableStatus
  const label = DELIVERABLE_STATUS_LABELS[s] ?? status ?? '-'
  const tagType: EpTagType = (() => {
    switch (s) {
      case 'DRAFT':
        return 'info'
      case 'SUBMITTED':
        return 'warning'
      case 'REVIEWED':
        return 'primary'
      case 'SIGNED':
        return 'success'
      case 'PUBLISHED':
        return 'success'
      case 'REFERENCED':
        return 'success'
      case 'ARCHIVED':
        return 'danger'
      default:
        return 'info'
    }
  })()
  return { label, tagType }
}

function formatDateTime(dt?: string): string {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 19)
}

// 当前状态下可执行的动作
function canSubmit(row: Deliverable): boolean { return row.status === 'DRAFT' }
function canReview(row: Deliverable): boolean { return row.status === 'SUBMITTED' }
function canSign(row: Deliverable): boolean { return row.status === 'REVIEWED' }
function canPublish(row: Deliverable): boolean { return row.status === 'SIGNED' }
function canArchive(row: Deliverable): boolean { return row.status === 'REFERENCED' }
function canRevise(row: Deliverable): boolean {
  return row.status === 'PUBLISHED' || row.status === 'REFERENCED'
}
function canDelete(row: Deliverable): boolean { return row.status === 'DRAFT' }

// ============ 数据加载 ============
async function loadProjects() {
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records
    if (res.records.length > 0 && selectedProjectId.value === undefined && res.records[0].id != null) {
      selectedProjectId.value = res.records[0].id
      await loadData()
    }
  } catch {
    /* handled by interceptor */
  }
}

async function loadData() {
  if (!selectedProjectId.value) {
    tableData.value = []
    return
  }
  loading.value = true
  try {
    const params: { projectId?: number; phaseId?: number; status?: string } = {
      projectId: selectedProjectId.value
    }
    if (phaseIdFilter.value) params.phaseId = phaseIdFilter.value
    if (activeStatus.value) params.status = activeStatus.value
    tableData.value = (await listFullDeliverables(params)) ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function onProjectChange() {
  await loadData()
}

async function onStatusTabChange() {
  await loadData()
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
    projectId: selectedProjectId.value ?? 0,
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

// ============ 状态流转 ============
async function handleSubmit(row: Deliverable) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认提交「${row.deliverableName}」进入审核流程？`, '提交', { type: 'warning' })
    await submitDeliverable(row.id)
    ElMessage.success('已提交')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

async function handleReview(row: Deliverable, passed: boolean) {
  if (!row.id) return
  const action = passed ? '通过审核' : '退回草稿'
  try {
    await ElMessageBox.confirm(`确认对「${row.deliverableName}」${action}？`, '审核', { type: 'warning' })
    await reviewDeliverable(row.id, passed)
    ElMessage.success(`已${action}`)
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

async function handleSign(row: Deliverable) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认签核「${row.deliverableName}」？`, '签核', { type: 'warning' })
    await signDeliverable(row.id)
    ElMessage.success('已签核')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

async function handlePublish(row: Deliverable) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认发布「${row.deliverableName}」？发布后版本固化。`, '发布', { type: 'warning' })
    await publishDeliverable(row.id)
    ElMessage.success('已发布')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

async function handleArchive(row: Deliverable) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认归档「${row.deliverableName}」？归档后只读。`, '归档', { type: 'warning' })
    await archiveDeliverable(row.id)
    ElMessage.success('已归档')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

// ============ 修订（Story 5 验收 1）============
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
    ElMessage.success(`修订成功，新版本 v${newVersion.versionNo}，旧版本保留不变`)
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
    await ElMessageBox.confirm(`确认删除草稿「${row.deliverableName}」？此操作不可恢复。`, '删除', { type: 'warning' })
    await deleteDeliverable(row.id)
    ElMessage.success('已删除')
    await loadData()
  } catch {
    /* cancelled or error */
  }
}

// ============ 跳转详情 ============
function goDetail(row: Deliverable) {
  if (!row.id) return
  router.push(`/deliverable/detail/${row.id}`)
}

// ============ 状态过滤标签 ============
const statusTabs = computed(() => [
  { label: '全部', value: '' as const },
  ...DELIVERABLE_STATUS_ORDER.map((s) => ({ label: DELIVERABLE_STATUS_LABELS[s], value: s }))
])

onMounted(loadProjects)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-bar">
          <span class="page-title">交付件全生命周期（7 态状态机）</span>
          <el-button type="primary" :icon="'Plus'" @click="openCreate">新建交付件</el-button>
        </div>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目">
          <el-select
            v-model="selectedProjectId"
            placeholder="请选择项目"
            filterable
            style="width: 220px"
            @change="onProjectChange"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段 ID">
          <el-input-number
            v-model="phaseIdFilter"
            :min="1"
            :controls="false"
            placeholder="可选"
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 状态过滤标签 -->
      <el-tabs v-model="activeStatus" @tab-change="onStatusTabChange" class="status-tabs">
        <el-tab-pane
          v-for="tab in statusTabs"
          :key="tab.value"
          :label="tab.label"
          :name="tab.value"
        />
      </el-tabs>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="deliverableName" label="交付件名称" min-width="160" />
        <el-table-column prop="deliverableType" label="类型" width="100" align="center">
          <template #default="{ row }">{{ row.deliverableType ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMeta(row.status).tagType" size="small">
              {{ statusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="版本" width="80" align="center">
          <template #default="{ row }">v{{ row.currentVersion ?? 1 }}</template>
        </el-table-column>
        <el-table-column label="必需" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.mandatory" type="warning" size="small">必需</el-tag>
            <el-tag v-else type="info" size="small">可选</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="160" align="center">
          <template #default="{ row }">{{ formatDateTime(row.publishedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="380" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row)">详情</el-button>
            <el-button v-if="canSubmit(row)" link type="primary" @click="handleSubmit(row)">提交</el-button>
            <el-button v-if="canReview(row)" link type="success" @click="handleReview(row, true)">通过</el-button>
            <el-button v-if="canReview(row)" link type="warning" @click="handleReview(row, false)">退回</el-button>
            <el-button v-if="canSign(row)" link type="primary" @click="handleSign(row)">签核</el-button>
            <el-button v-if="canPublish(row)" link type="success" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="canArchive(row)" link type="danger" @click="handleArchive(row)">归档</el-button>
            <el-button v-if="canRevise(row)" link type="primary" @click="openRevise(row)">修订</el-button>
            <el-button v-if="canDelete(row)" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无交付件，请新建或调整筛选条件" />
        </template>
      </el-table>
    </el-card>

    <!-- 新建弹窗 -->
    <el-dialog v-model="createVisible" title="新建交付件" width="520px" destroy-on-close>
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="交付件名称" required>
          <el-input v-model="createForm.deliverableName" placeholder="请输入交付件名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="createForm.deliverableType" style="width: 100%">
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="配置" value="CONFIG" />
            <el-option label="报告" value="REPORT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段 ID">
          <el-input-number v-model="createForm.phaseId" :min="1" :controls="false" style="width: 100%" />
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

    <!-- 修订弹窗（Story 5 验收 1：新建版本不覆盖旧版本）-->
    <el-dialog v-model="reviseVisible" title="修订交付件（新建版本）" width="520px" destroy-on-close>
      <div v-if="reviseRow" class="revise-tip">
        当前：<strong>{{ reviseRow.deliverableName }}</strong>
        （版本 v{{ reviseRow.currentVersion ?? 1 }} / 状态 {{ statusMeta(reviseRow.status).label }}）
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
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.status-tabs {
  margin-bottom: 12px;
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
</style>
