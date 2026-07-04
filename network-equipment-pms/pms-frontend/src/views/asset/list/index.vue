<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  allocateAsset,
  applyTransfer,
  approveTransfer,
  ASSET_STATUS,
  deleteAsset,
  getAsset,
  getAssetLifecycle,
  inboundAsset,
  listAssets,
  listTransfers,
  rejectTransfer,
  returnAsset,
  TRANSFER_STATUS,
  updateAsset,
  type Asset,
  type AssetLifecycleRecord,
  type AssetTransfer,
  type TransferListQuery
} from '@/api/asset'
import ExcelImportExport from '@/components/ExcelImportExport/index.vue'

// ============ Asset list ============
const loading = ref(false)
const tableData = ref<Asset[]>([])
const total = ref(0)
const query = reactive({
  page: 1,
  size: 10,
  serialNo: '',
  status: '',
  projectId: undefined as number | undefined
})

const statusOptions = [
  { value: ASSET_STATUS.IN_STOCK, label: '在库', tag: 'success' },
  { value: ASSET_STATUS.ALLOCATED, label: '已分配', tag: 'warning' },
  { value: ASSET_STATUS.IN_TRANSIT, label: '调拨中', tag: 'primary' },
  { value: ASSET_STATUS.SCRAPPED, label: '已报废', tag: 'danger' }
]

function statusTagType(status?: string): 'success' | 'warning' | 'primary' | 'danger' | 'info' {
  return (statusOptions.find((o) => o.value === status)?.tag as any) ?? 'info'
}

function statusLabel(status?: string) {
  return statusOptions.find((o) => o.value === status)?.label ?? status ?? '-'
}

async function loadAssets() {
  loading.value = true
  try {
    const res = await listAssets({
      page: query.page,
      size: query.size,
      serialNo: query.serialNo || undefined,
      status: query.status || undefined,
      projectId: query.projectId
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadAssets()
}

function handleReset() {
  query.serialNo = ''
  query.status = ''
  query.projectId = undefined
  query.page = 1
  loadAssets()
}

function handlePageChange(p: number) {
  query.page = p
  loadAssets()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadAssets()
}

// ============ 序列号异步校验 ============
// TODO: 后端暂无专门的序列号查重接口，此处用列表过滤模拟。
//       后续应改为调用 GET /api/asset/check-serial-no?serialNo=xxx 由后端返回是否占用。
function makeSerialNoValidator(getCurrentId: () => number | undefined) {
  return async (
    _rule: unknown,
    value: string,
    callback: (err?: Error) => void
  ) => {
    if (!value) return callback()
    try {
      const res = await listAssets({ page: 1, size: 50, serialNo: value })
      const exists = (res.records || []).some(
        (a) => a.serialNo === value && a.id !== getCurrentId()
      )
      if (exists) return callback(new Error('该序列号已存在'))
      callback()
    } catch {
      // 查重接口异常时不阻塞表单提交
      callback()
    }
  }
}

// ============ Inbound dialog ============
const inboundVisible = ref(false)
const inboundRef = ref<FormInstance>()
const inboundSubmitting = ref(false)
const inboundForm = reactive<Asset>(createInboundForm())

const inboundRules: FormRules = {
  serialNo: [
    { required: true, message: '请输入序列号', trigger: 'blur' },
    { validator: makeSerialNoValidator(() => undefined), trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  warehouse: [{ required: true, message: '请输入仓库', trigger: 'blur' }]
}

function createInboundForm(): Asset {
  return {
    serialNo: '',
    name: '',
    modelId: undefined,
    warehouse: '',
    location: '',
    remark: ''
  }
}

function openInbound() {
  Object.assign(inboundForm, createInboundForm())
  inboundVisible.value = true
}

async function submitInbound() {
  if (!inboundRef.value) return
  await inboundRef.value.validate(async (valid) => {
    if (!valid) return
    inboundSubmitting.value = true
    try {
      await inboundAsset({ ...inboundForm })
      ElMessage.success('入库成功')
      inboundVisible.value = false
      loadAssets()
    } catch {
      /* handled by interceptor */
    } finally {
      inboundSubmitting.value = false
    }
  })
}

// ============ Allocate dialog ============
const allocateVisible = ref(false)
const allocateRef = ref<FormInstance>()
const allocateSubmitting = ref(false)
const allocateForm = reactive<{ id?: number; serialNo?: string; projectId?: number }>({
  id: undefined,
  serialNo: '',
  projectId: undefined
})

const allocateRules: FormRules = {
  projectId: [{ required: true, message: '请输入项目ID', trigger: 'blur' }]
}

function openAllocate(row: Asset) {
  allocateForm.id = row.id
  allocateForm.serialNo = row.serialNo
  allocateForm.projectId = row.projectId
  allocateVisible.value = true
}

async function submitAllocate() {
  if (!allocateRef.value || !allocateForm.id || !allocateForm.projectId) return
  const assetId = allocateForm.id
  const projectId = allocateForm.projectId
  await allocateRef.value.validate(async (valid) => {
    if (!valid) return
    allocateSubmitting.value = true
    try {
      await allocateAsset(assetId, { projectId })
      ElMessage.success('分配成功')
      allocateVisible.value = false
      loadAssets()
    } catch {
      /* handled by interceptor */
    } finally {
      allocateSubmitting.value = false
    }
  })
}

// ============ Return asset ============
function handleReturn(row: Asset) {
  if (!row.id) return
  ElMessageBox.confirm(
    `确定回收设备「${row.serialNo}」吗？回收后设备将回到在库状态。`,
    '提示',
    { type: 'warning' }
  )
    .then(async () => {
      await returnAsset(row.id!)
      ElMessage.success('回收成功')
      loadAssets()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ Edit dialog ============
const editVisible = ref(false)
const editRef = ref<FormInstance>()
const editSubmitting = ref(false)
const editForm = reactive<Asset>(createEditForm())

const editRules: FormRules = {
  serialNo: [
    { required: true, message: '请输入序列号', trigger: 'blur' },
    { validator: makeSerialNoValidator(() => editForm.id), trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

function createEditForm(): Asset {
  return {
    id: undefined,
    serialNo: '',
    name: '',
    warehouse: '',
    location: '',
    remark: ''
  }
}

async function openEdit(row: Asset) {
  if (!row.id) return
  try {
    const detail = await getAsset(row.id)
    Object.assign(editForm, createEditForm(), detail)
    editVisible.value = true
  } catch {
    /* handled by interceptor */
  }
}

async function submitEdit() {
  if (!editRef.value || !editForm.id) return
  await editRef.value.validate(async (valid) => {
    if (!valid) return
    editSubmitting.value = true
    try {
      await updateAsset({ ...editForm })
      ElMessage.success('更新成功')
      editVisible.value = false
      loadAssets()
    } catch {
      /* handled by interceptor */
    } finally {
      editSubmitting.value = false
    }
  })
}

// ============ Delete asset ============
function handleDelete(row: Asset) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除资产「${row.serialNo}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteAsset(row.id!)
      ElMessage.success('删除成功')
      loadAssets()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ Lifecycle dialog ============
const lifecycleVisible = ref(false)
const lifecycleLoading = ref(false)
const lifecycleRecords = ref<AssetLifecycleRecord[]>([])
const lifecycleAssetSn = ref('')

const actionMeta: Record<string, { label: string; tag: 'success' | 'warning' | 'primary' | 'danger' | 'info' }> = {
  INBOUND: { label: '入库', tag: 'success' },
  ALLOCATE: { label: '分配', tag: 'warning' },
  TRANSFER: { label: '调拨', tag: 'primary' },
  RETURN: { label: '回收', tag: 'info' },
  SCRAP: { label: '报废', tag: 'danger' }
}

function actionLabel(action?: string) {
  return actionMeta[action ?? '']?.label ?? action ?? '-'
}

function actionTag(action?: string): 'success' | 'warning' | 'primary' | 'danger' | 'info' {
  return actionMeta[action ?? '']?.tag ?? 'info'
}

async function openLifecycle(row: Asset) {
  if (!row.id) return
  lifecycleAssetSn.value = row.serialNo
  lifecycleVisible.value = true
  lifecycleLoading.value = true
  lifecycleRecords.value = []
  try {
    lifecycleRecords.value = (await getAssetLifecycle(row.id)) || []
  } catch {
    /* handled by interceptor */
  } finally {
    lifecycleLoading.value = false
  }
}

function formatDateTime(val?: string) {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

// ============ Transfer apply dialog ============
const transferApplyVisible = ref(false)
const transferApplyRef = ref<FormInstance>()
const transferApplySubmitting = ref(false)
const transferApplyForm = reactive({
  assetId: undefined as number | undefined,
  assetSerialNo: '',
  fromProjectId: undefined as number | undefined,
  toProjectId: undefined as number | undefined,
  transferReason: ''
})

const transferApplyRules: FormRules = {
  assetId: [{ required: true, message: '请输入设备ID', trigger: 'blur' }],
  fromProjectId: [{ required: true, message: '请输入源项目ID', trigger: 'blur' }],
  toProjectId: [{ required: true, message: '请输入目标项目ID', trigger: 'blur' }],
  transferReason: [{ required: true, message: '请输入调拨原因', trigger: 'blur' }]
}

function openTransferApply(row?: Asset) {
  transferApplyForm.assetId = row?.id
  transferApplyForm.assetSerialNo = row?.serialNo ?? ''
  transferApplyForm.fromProjectId = row?.projectId
  transferApplyForm.toProjectId = undefined
  transferApplyForm.transferReason = ''
  transferApplyVisible.value = true
}

async function submitTransferApply() {
  if (!transferApplyRef.value) return
  await transferApplyRef.value.validate(async (valid) => {
    if (!valid) return
    if (
      !transferApplyForm.assetId ||
      !transferApplyForm.fromProjectId ||
      !transferApplyForm.toProjectId
    )
      return
    transferApplySubmitting.value = true
    try {
      await applyTransfer({
        assetId: transferApplyForm.assetId,
        fromProjectId: transferApplyForm.fromProjectId,
        toProjectId: transferApplyForm.toProjectId,
        transferReason: transferApplyForm.transferReason
      })
      ElMessage.success('调拨申请已提交')
      transferApplyVisible.value = false
      activeTab.value = 'transfer'
      loadTransfers()
    } catch {
      /* handled by interceptor */
    } finally {
      transferApplySubmitting.value = false
    }
  })
}

// ============ Transfer management ============
const activeTab = ref<'asset' | 'transfer'>('asset')
const transferLoading = ref(false)
const transferTableData = ref<AssetTransfer[]>([])
const transferTotal = ref(0)
const transferQuery = reactive<TransferListQuery>({
  page: 1,
  size: 10,
  status: ''
})

const transferStatusOptions = [
  { value: TRANSFER_STATUS.PENDING, label: '待审批', tag: 'warning' },
  { value: TRANSFER_STATUS.APPROVED, label: '已通过', tag: 'success' },
  { value: TRANSFER_STATUS.REJECTED, label: '已驳回', tag: 'danger' }
]

function transferStatusTag(status?: string): 'success' | 'warning' | 'danger' | 'info' {
  return (transferStatusOptions.find((o) => o.value === status)?.tag as any) ?? 'info'
}

function transferStatusLabel(status?: string) {
  return transferStatusOptions.find((o) => o.value === status)?.label ?? status ?? '-'
}

async function loadTransfers() {
  transferLoading.value = true
  try {
    const res = await listTransfers({
      page: transferQuery.page,
      size: transferQuery.size,
      status: transferQuery.status || undefined
    })
    transferTableData.value = res.records || []
    transferTotal.value = res.total || 0
  } catch {
    /* handled by interceptor */
  } finally {
    transferLoading.value = false
  }
}

function handleTransferSearch() {
  transferQuery.page = 1
  loadTransfers()
}

function handleTransferReset() {
  transferQuery.status = ''
  transferQuery.page = 1
  loadTransfers()
}

function handleTransferPageChange(p: number) {
  transferQuery.page = p
  loadTransfers()
}

function handleTransferSizeChange(s: number) {
  transferQuery.size = s
  transferQuery.page = 1
  loadTransfers()
}

// ============ Transfer approve/reject ============
const opinionVisible = ref(false)
const opinionTitle = ref('')
const opinionSubmitting = ref(false)
const opinionForm = reactive<{ id?: number; opinion: string; action: 'approve' | 'reject' }>({
  id: undefined,
  opinion: '',
  action: 'approve'
})

const opinionRules: FormRules = {
  opinion: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

function openApprove(row: AssetTransfer) {
  if (!row.id) return
  opinionTitle.value = '审批通过'
  opinionForm.id = row.id
  opinionForm.opinion = ''
  opinionForm.action = 'approve'
  opinionVisible.value = true
}

function openReject(row: AssetTransfer) {
  if (!row.id) return
  opinionTitle.value = '驳回申请'
  opinionForm.id = row.id
  opinionForm.opinion = ''
  opinionForm.action = 'reject'
  opinionVisible.value = true
}

const opinionFormRef = ref<FormInstance>()

async function handleSubmitOpinion() {
  if (!opinionFormRef.value || !opinionForm.id) return
  await opinionFormRef.value.validate(async (valid) => {
    if (!valid) return
    opinionSubmitting.value = true
    try {
      if (opinionForm.action === 'approve') {
        await approveTransfer(opinionForm.id!, { opinion: opinionForm.opinion })
        ElMessage.success('审批通过')
      } else {
        await rejectTransfer(opinionForm.id!, { opinion: opinionForm.opinion })
        ElMessage.success('已驳回')
      }
      opinionVisible.value = false
      loadTransfers()
    } catch {
      /* handled by interceptor */
    } finally {
      opinionSubmitting.value = false
    }
  })
}

const canApprove = (status?: string) => status === TRANSFER_STATUS.PENDING

function handleTabChange(name: string | number) {
  if (name === 'transfer' && transferTableData.value.length === 0) {
    loadTransfers()
  }
}

// ============ Init ============
onMounted(() => {
  loadAssets()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title">设备资产管理</div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- ============ Tab 1: Asset list ============ -->
      <el-tab-pane label="设备资产" name="asset">
        <el-card shadow="never">
          <el-form :inline="true" @submit.prevent>
            <el-form-item label="序列号">
              <el-input
                v-model="query.serialNo"
                placeholder="设备序列号"
                clearable
                style="width: 200px"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select
                v-model="query.status"
                placeholder="全部"
                clearable
                style="width: 140px"
              >
                <el-option
                  v-for="o in statusOptions"
                  :key="o.value"
                  :label="o.label"
                  :value="o.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="项目ID">
              <el-input-number
                v-model="query.projectId"
                :min="1"
                controls-position="right"
                placeholder="项目ID"
                style="width: 160px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
              <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <div class="toolbar">
            <el-button type="primary" :icon="'Download'" @click="openInbound">设备入库</el-button>
            <el-button type="warning" :icon="'Switch'" @click="openTransferApply()">
              设备调拨
            </el-button>
            <ExcelImportExport
              template-url="/api/asset/template"
              import-url="/api/asset/import"
              export-url="/api/asset/export"
              template-file-name="asset-template.xlsx"
              export-file-name="asset-list.xlsx"
              :export-params="{
                serialNo: query.serialNo || undefined,
                status: query.status || undefined,
                projectId: query.projectId
              }"
              @refresh="loadAssets"
            />
          </div>

          <el-table v-loading="loading" :data="tableData" border stripe>
            <el-table-column prop="serialNo" label="序列号" min-width="160" show-overflow-tooltip />
            <el-table-column prop="name" label="设备名称" min-width="140" show-overflow-tooltip />
            <el-table-column prop="modelName" label="型号" min-width="140" show-overflow-tooltip />
            <el-table-column prop="categoryName" label="分类" min-width="120" show-overflow-tooltip />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warehouse" label="仓库" min-width="120" show-overflow-tooltip />
            <el-table-column prop="location" label="库位" min-width="120" show-overflow-tooltip />
            <el-table-column label="关联项目" min-width="120">
              <template #default="{ row }">
                {{ row.projectId ? `${row.projectName ?? row.projectId}` : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="入库时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.inboundDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openLifecycle(row)">
                  生命周期
                </el-button>
                <el-button
                  v-if="row.status === ASSET_STATUS.IN_STOCK"
                  link
                  type="primary"
                  @click="openAllocate(row)"
                >
                  分配
                </el-button>
                <el-button
                  v-if="row.status === ASSET_STATUS.ALLOCATED"
                  link
                  type="warning"
                  @click="handleReturn(row)"
                >
                  回收
                </el-button>
                <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无数据" />
            </template>
          </el-table>

          <el-pagination
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
      </el-tab-pane>

      <!-- ============ Tab 2: Transfer management ============ -->
      <el-tab-pane label="调拨管理" name="transfer">
        <el-card shadow="never">
          <el-form :inline="true" @submit.prevent>
            <el-form-item label="状态">
              <el-select
                v-model="transferQuery.status"
                placeholder="全部"
                clearable
                style="width: 160px"
              >
                <el-option
                  v-for="o in transferStatusOptions"
                  :key="o.value"
                  :label="o.label"
                  :value="o.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="'Search'" @click="handleTransferSearch">
                查询
              </el-button>
              <el-button :icon="'Refresh'" @click="handleTransferReset">重置</el-button>
              <el-button type="warning" :icon="'Switch'" @click="openTransferApply()">
                新增调拨
              </el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="transferLoading" :data="transferTableData" border stripe>
            <el-table-column
              prop="assetSerialNo"
              label="设备序列号"
              min-width="160"
              show-overflow-tooltip
            />
            <el-table-column label="源项目" min-width="140">
              <template #default="{ row }">
                {{ row.fromProjectName ?? row.fromProjectId ?? '-' }}
              </template>
            </el-table-column>
            <el-table-column label="目标项目" min-width="140">
              <template #default="{ row }">
                {{ row.toProjectName ?? row.toProjectId ?? '-' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="transferReason"
              label="调拨原因"
              min-width="180"
              show-overflow-tooltip
            />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="transferStatusTag(row.status)">
                  {{ transferStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicantName" label="申请人" min-width="100" />
            <el-table-column label="申请时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.applyTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <template v-if="canApprove(row.status)">
                  <el-button link type="success" @click="openApprove(row)">审批通过</el-button>
                  <el-button link type="danger" @click="openReject(row)">驳回</el-button>
                </template>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无数据" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="transferQuery.page"
            :page-size="transferQuery.size"
            :total="transferTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleTransferPageChange"
            @size-change="handleTransferSizeChange"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- ============ Inbound dialog ============ -->
    <el-dialog v-model="inboundVisible" title="设备入库" width="560px" destroy-on-close>
      <el-form ref="inboundRef" :model="inboundForm" :rules="inboundRules" label-width="100px">
        <el-form-item label="序列号" prop="serialNo">
          <el-input v-model="inboundForm.serialNo" placeholder="请输入序列号" maxlength="50" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="inboundForm.name" placeholder="请输入设备名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="型号ID" prop="modelId">
          <el-input-number
            v-model="inboundForm.modelId"
            :min="1"
            controls-position="right"
            placeholder="设备型号ID"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="仓库" prop="warehouse">
          <el-input v-model="inboundForm.warehouse" placeholder="请输入仓库名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="库位" prop="location">
          <el-input v-model="inboundForm.location" placeholder="请输入库位" maxlength="50" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="inboundForm.remark"
            type="textarea"
            :rows="2"
            placeholder="备注信息"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inboundVisible = false">取消</el-button>
        <el-button type="primary" :loading="inboundSubmitting" @click="submitInbound">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- ============ Allocate dialog ============ -->
    <el-dialog v-model="allocateVisible" title="分配设备" width="480px" destroy-on-close>
      <el-form ref="allocateRef" :model="allocateForm" :rules="allocateRules" label-width="100px">
        <el-form-item label="序列号">
          <el-input :model-value="allocateForm.serialNo" disabled />
        </el-form-item>
        <el-form-item label="项目ID" prop="projectId">
          <el-input-number
            v-model="allocateForm.projectId"
            :min="1"
            controls-position="right"
            placeholder="请输入项目ID"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="allocateVisible = false">取消</el-button>
        <el-button type="primary" :loading="allocateSubmitting" @click="submitAllocate">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- ============ Edit dialog ============ -->
    <el-dialog v-model="editVisible" title="编辑设备" width="560px" destroy-on-close>
      <el-form ref="editRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="序列号" prop="serialNo">
          <el-input v-model="editForm.serialNo" placeholder="请输入序列号" maxlength="50" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入设备名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="仓库" prop="warehouse">
          <el-input v-model="editForm.warehouse" placeholder="请输入仓库名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="库位" prop="location">
          <el-input v-model="editForm.location" placeholder="请输入库位" maxlength="50" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="2"
            placeholder="备注信息"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- ============ Lifecycle dialog ============ -->
    <el-dialog
      v-model="lifecycleVisible"
      :title="`设备生命周期 - ${lifecycleAssetSn}`"
      width="640px"
      destroy-on-close
    >
      <div v-loading="lifecycleLoading">
        <el-timeline v-if="lifecycleRecords.length > 0">
          <el-timeline-item
            v-for="(item, idx) in lifecycleRecords"
            :key="item.id ?? idx"
            :timestamp="formatDateTime(item.operateTime)"
            placement="top"
          >
            <el-card shadow="hover" class="lifecycle-card">
              <div class="lifecycle-row">
                <el-tag :type="actionTag(item.action)">{{ actionLabel(item.action) }}</el-tag>
                <span class="lifecycle-operator">操作人：{{ item.operator || '-' }}</span>
              </div>
              <div v-if="item.fromProjectId || item.toProjectId" class="lifecycle-project">
                <span>项目变更：</span>
                <span>{{ item.fromProjectName ?? item.fromProjectId ?? '-' }}</span>
                <span class="arrow">→</span>
                <span>{{ item.toProjectName ?? item.toProjectId ?? '-' }}</span>
              </div>
              <div v-if="item.remark" class="lifecycle-remark">备注：{{ item.remark }}</div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无生命周期记录" />
      </div>
    </el-dialog>

    <!-- ============ Transfer apply dialog ============ -->
    <el-dialog v-model="transferApplyVisible" title="设备调拨申请" width="560px" destroy-on-close>
      <el-form
        ref="transferApplyRef"
        :model="transferApplyForm"
        :rules="transferApplyRules"
        label-width="100px"
      >
        <el-form-item label="设备ID" prop="assetId">
          <el-input-number
            v-model="transferApplyForm.assetId"
            :min="1"
            controls-position="right"
            placeholder="请输入设备ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="transferApplyForm.assetSerialNo" label="序列号">
          <el-input :model-value="transferApplyForm.assetSerialNo" disabled />
        </el-form-item>
        <el-form-item label="源项目ID" prop="fromProjectId">
          <el-input-number
            v-model="transferApplyForm.fromProjectId"
            :min="1"
            controls-position="right"
            placeholder="请输入源项目ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="目标项目ID" prop="toProjectId">
          <el-input-number
            v-model="transferApplyForm.toProjectId"
            :min="1"
            controls-position="right"
            placeholder="请输入目标项目ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="调拨原因" prop="transferReason">
          <el-input
            v-model="transferApplyForm.transferReason"
            type="textarea"
            :rows="3"
            placeholder="请输入调拨原因"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferApplyVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="transferApplySubmitting"
          @click="submitTransferApply"
        >
          提交申请
        </el-button>
      </template>
    </el-dialog>

    <!-- ============ Approve / Reject opinion dialog ============ -->
    <el-dialog v-model="opinionVisible" :title="opinionTitle" width="480px" destroy-on-close>
      <el-form
        ref="opinionFormRef"
        :model="opinionForm"
        :rules="opinionRules"
        label-width="100px"
      >
        <el-form-item label="审批意见" prop="opinion">
          <el-input
            v-model="opinionForm.opinion"
            type="textarea"
            :rows="3"
            placeholder="请输入审批意见"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="opinionVisible = false">取消</el-button>
        <el-button type="primary" :loading="opinionSubmitting" @click="handleSubmitOpinion">
          确定
        </el-button>
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
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.text-muted {
  color: #c0c4cc;
}
.lifecycle-card {
  margin-bottom: 0;
}
.lifecycle-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.lifecycle-operator {
  color: #606266;
  font-size: 13px;
}
.lifecycle-project {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}
.lifecycle-project .arrow {
  margin: 0 6px;
  color: #909399;
}
.lifecycle-remark {
  font-size: 13px;
  color: #909399;
}
</style>
