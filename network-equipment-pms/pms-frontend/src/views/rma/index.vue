<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  checkWarranty,
  closeRma,
  createRma,
  getRmaKpi,
  inspectRma,
  issueRma,
  listRmas,
  markReturning,
  type Rma,
  type RmaKpi,
  type RmaTicketStatus,
  type WarrantyStatus
} from '@/api/rma'
import type { EpTagType } from '@/types'

const loading = ref(false)
const tableData = ref<Rma[]>([])
const total = ref(0)

// KPI 数据
const kpi = ref<RmaKpi>({ mttrHours: 0, firstPassRate: 0, total: 0, closed: 0 })

const query = reactive<{ page: number; size: number; ticketStatus?: string; assetId?: number }>({
  page: 1,
  size: 10,
  ticketStatus: '',
  assetId: undefined
})

// 工单状态选项
const statusOptions: { value: RmaTicketStatus; label: string; tagType: EpTagType }[] = [
  { value: 'REGISTERED', label: '已登记', tagType: 'info' },
  { value: 'WARRANTY_CHECKED', label: '已校验保修', tagType: 'warning' },
  { value: 'RMA_ISSUED', label: '已签发 RMA', tagType: 'primary' },
  { value: 'RETURNING', label: '返回中', tagType: 'warning' },
  { value: 'INSPECTED', label: '已检验', tagType: 'success' },
  { value: 'CLOSED', label: '已关闭', tagType: 'info' }
]

// 保修状态标签
const warrantyTagType: Record<string, EpTagType> = {
  IN_WARRANTY: 'success',
  OUT_OF_WARRANTY: 'danger'
}

function warrantyLabel(status?: string): string {
  if (status === 'IN_WARRANTY') return '保内'
  if (status === 'OUT_OF_WARRANTY') return '保外'
  return '-'
}

function statusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

// 时间格式化：去 T 并截取到秒
function formatDateTime(val?: string): string {
  return val?.replace('T', ' ').slice(0, 19) ?? '-'
}

// 根据当前状态返回下一步操作文案
function nextActionLabel(status?: string): string {
  switch (status) {
    case 'REGISTERED':
      return '校验保修'
    case 'WARRANTY_CHECKED':
      return '签发 RMA'
    case 'RMA_ISSUED':
      return '标记返回'
    case 'RETURNING':
      return '到货检验'
    case 'INSPECTED':
      return '关闭'
    default:
      return ''
  }
}

// ============== 新建弹窗 ==============
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface RmaForm {
  assetId: number | undefined
  sn: string
  faultDescription: string
}

function createEmptyForm(): RmaForm {
  return { assetId: undefined, sn: '', faultDescription: '' }
}

const form = reactive<RmaForm>(createEmptyForm())

const rules: FormRules = {
  assetId: [{ required: true, message: '请输入资产 ID', trigger: 'blur' }],
  faultDescription: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
}

// ============== 检验弹窗 ==============
const inspectVisible = ref(false)
const inspectSubmitting = ref(false)
const inspectForm = reactive<{ id: number; inspectionResult: string; updateAsset: boolean }>({
  id: 0,
  inspectionResult: '',
  updateAsset: false
})

// ============== 数据加载 ==============
async function loadKpi() {
  try {
    kpi.value = await getRmaKpi()
  } catch {
    /* handled by interceptor */
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: { page: number; size: number; ticketStatus?: string; assetId?: number } = {
      page: query.page,
      size: query.size
    }
    if (query.ticketStatus) params.ticketStatus = query.ticketStatus
    if (query.assetId) params.assetId = query.assetId
    const res = await listRmas(params)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
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
  query.ticketStatus = ''
  query.assetId = undefined
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

// ============== 新建 ==============
function handleAdd() {
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: Rma = {
        assetId: form.assetId!,
        sn: form.sn,
        faultDescription: form.faultDescription,
        ticketStatus: 'REGISTERED'
      }
      await createRma(payload)
      ElMessage.success('新建成功')
      dialogVisible.value = false
      loadData()
      loadKpi()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

// ============== 状态流转 ==============
function handleNext(row: Rma) {
  if (!row.id) return
  switch (row.ticketStatus) {
    case 'REGISTERED':
      confirmAction(row, '校验保修', checkWarranty)
      break
    case 'WARRANTY_CHECKED':
      confirmAction(row, '签发 RMA', issueRma)
      break
    case 'RMA_ISSUED':
      confirmAction(row, '标记返回', markReturning)
      break
    case 'RETURNING':
      openInspectDialog(row)
      break
    case 'INSPECTED':
      confirmAction(row, '关闭', closeRma)
      break
  }
}

function confirmAction(row: Rma, actionName: string, fn: (id: number) => Promise<Rma>) {
  if (!row.id) return
  ElMessageBox.confirm(`确认对 RMA「${row.rmaNo ?? row.id}」执行「${actionName}」操作吗？`, actionName, {
    type: 'warning'
  })
    .then(async () => {
      await fn(row.id!)
      ElMessage.success(`${actionName}成功`)
      loadData()
      loadKpi()
    })
    .catch(() => {
      /* cancelled */
    })
}

function openInspectDialog(row: Rma) {
  inspectForm.id = row.id ?? 0
  inspectForm.inspectionResult = ''
  inspectForm.updateAsset = false
  inspectVisible.value = true
}

async function handleInspectSubmit() {
  if (!inspectForm.inspectionResult) {
    ElMessage.warning('请填写检验结果')
    return
  }
  inspectSubmitting.value = true
  try {
    await inspectRma(inspectForm.id, {
      inspectionResult: inspectForm.inspectionResult,
      updateAsset: inspectForm.updateAsset
    })
    ElMessage.success('检验完成')
    inspectVisible.value = false
    loadData()
    loadKpi()
  } catch {
    /* handled by interceptor */
  } finally {
    inspectSubmitting.value = false
  }
}

onMounted(() => {
  loadKpi()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <!-- KPI 卡片 -->
    <el-row :gutter="12">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="kpi-card">
            <div class="kpi-label">MTTR（小时）</div>
            <div class="kpi-value">{{ kpi.mttrHours.toFixed(1) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="kpi-card">
            <div class="kpi-label">一次通过率（%）</div>
            <div class="kpi-value">{{ kpi.firstPassRate.toFixed(1) }}%</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="kpi-card">
            <div class="kpi-label">RMA 总数</div>
            <div class="kpi-value">{{ kpi.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="kpi-card">
            <div class="kpi-label">已关闭数</div>
            <div class="kpi-value">{{ kpi.closed }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <template #header>
        <span class="page-title">RMA 返修管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="工单状态">
          <el-select v-model="query.ticketStatus" placeholder="全部状态" clearable style="width: 180px">
            <el-option
              v-for="opt in statusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资产 ID">
          <el-input-number
            v-model="query.assetId"
            :min="1"
            :controls="false"
            placeholder="请输入资产 ID"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建 RMA</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="rmaNo" label="RMA 编号" width="150" />
        <el-table-column prop="assetId" label="资产 ID" width="100" />
        <el-table-column prop="sn" label="SN" width="140" show-overflow-tooltip />
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="保修状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="warrantyTagType[row.warrantyStatus] || 'info'" size="small">
              {{ warrantyLabel(row.warrantyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="工单状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMeta(row.ticketStatus).tagType" size="small">
              {{ statusMeta(row.ticketStatus).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="nextActionLabel(row.ticketStatus)"
              link
              type="primary"
              @click="handleNext(row)"
            >
              {{ nextActionLabel(row.ticketStatus) }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无 RMA 数据" />
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

    <!-- 新建弹窗 -->
    <el-dialog v-model="dialogVisible" title="新建 RMA" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="资产 ID" prop="assetId">
          <el-input-number
            v-model="form.assetId"
            :min="1"
            :controls="false"
            placeholder="请输入资产 ID"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="SN">
          <el-input v-model="form.sn" placeholder="请输入序列号" />
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDescription">
          <el-input
            v-model="form.faultDescription"
            type="textarea"
            :rows="4"
            placeholder="请输入故障描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 检验弹窗 -->
    <el-dialog v-model="inspectVisible" title="到货检验" width="520px" destroy-on-close>
      <el-form label-width="120px">
        <el-form-item label="检验结果" required>
          <el-input
            v-model="inspectForm.inspectionResult"
            type="textarea"
            :rows="4"
            placeholder="请输入检验结果"
          />
        </el-form-item>
        <el-form-item label="是否更新资产状态">
          <el-switch v-model="inspectForm.updateAsset" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inspectVisible = false">取消</el-button>
        <el-button type="primary" :loading="inspectSubmitting" @click="handleInspectSubmit">
          提交
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
  font-size: 16px;
  font-weight: 600;
}
.kpi-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.kpi-label {
  font-size: 13px;
  color: #909399;
}
.kpi-value {
  font-size: 24px;
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
</style>
