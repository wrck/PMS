<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createWarranty,
  decommissionAsset,
  listWarranties,
  renewWarranty,
  type SlaLevel,
  type Warranty
} from '@/api/warranty'

const loading = ref(false)
const tableData = ref<Warranty[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; assetId?: number; expiringDays?: number }>({
  page: 1,
  size: 10,
  assetId: undefined,
  expiringDays: undefined
})

// 即将到期天数选项
const expiringOptions = [
  { value: 30, label: '30 天内' },
  { value: 60, label: '60 天内' },
  { value: 90, label: '90 天内' }
]

// SLA 等级选项
const slaOptions: { value: SlaLevel; label: string; tagType: any }[] = [
  { value: 'BASIC', label: '基础版', tagType: 'info' },
  { value: 'PREMIUM', label: '高级版', tagType: 'warning' },
  { value: 'PLATINUM', label: '铂金版', tagType: 'danger' }
]

function getSlaMeta(level?: string) {
  return slaOptions.find((s) => s.value === level) ?? { label: level ?? '-', tagType: 'info' }
}

// 时间格式化：去 T 并截取到秒
function formatDateTime(val?: string): string {
  return val?.replace('T', ' ').slice(0, 19) ?? '-'
}

// 计算剩余天数
function remainingDays(endDate?: string): number {
  if (!endDate) return Number.POSITIVE_INFINITY
  const end = new Date(endDate).getTime()
  if (Number.isNaN(end)) return Number.POSITIVE_INFINITY
  const diff = end - Date.now()
  return Math.ceil(diff / 86400000)
}

// 剩余天数标签颜色：<30红、<60橙、<90黄、>=90绿；过期为灰
function remainingMeta(days: number): { color: string; label: string } {
  if (days < 0) return { color: '#909399', label: '已过期' }
  if (days < 30) return { color: '#f56c6c', label: `${days} 天` }
  if (days < 60) return { color: '#e6a23c', label: `${days} 天` }
  if (days < 90) return { color: '#f0c040', label: `${days} 天` }
  return { color: '#67c23a', label: `${days} 天` }
}

// ============== 新建弹窗 ==============
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface WarrantyForm {
  assetId: number | undefined
  startDate: string
  durationMonths: number
  slaLevel: SlaLevel
  contractNo: string
}

function createEmptyForm(): WarrantyForm {
  return {
    assetId: undefined,
    startDate: '',
    durationMonths: 12,
    slaLevel: 'BASIC',
    contractNo: ''
  }
}

const form = reactive<WarrantyForm>(createEmptyForm())

const rules: FormRules = {
  assetId: [{ required: true, message: '请输入资产 ID', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择起始日期', trigger: 'change' }],
  durationMonths: [{ required: true, message: '请输入月数', trigger: 'blur' }],
  slaLevel: [{ required: true, message: '请选择 SLA 等级', trigger: 'change' }]
}

// ============== 续保弹窗 ==============
const renewVisible = ref(false)
const renewSubmitting = ref(false)
const renewForm = reactive<{ id: number; durationMonths: number; endDate: string }>({
  id: 0,
  durationMonths: 12,
  endDate: ''
})

const renewRules: FormRules = {
  durationMonths: [{ required: true, message: '请输入续保月数', trigger: 'blur' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

// ============== 数据加载 ==============
async function loadData() {
  loading.value = true
  try {
    const params: { page: number; size: number; assetId?: number; expiringDays?: number } = {
      page: query.page,
      size: query.size
    }
    if (query.assetId) params.assetId = query.assetId
    if (query.expiringDays) params.expiringDays = query.expiringDays
    const res = await listWarranties(params)
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
  query.assetId = undefined
  query.expiringDays = undefined
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
      // 根据起始日期与月数推算结束日期
      const start = new Date(form.startDate)
      const end = new Date(start)
      end.setMonth(end.getMonth() + form.durationMonths)
      const payload: Warranty = {
        assetId: form.assetId!,
        startDate: form.startDate,
        endDate: end.toISOString().slice(0, 10),
        durationMonths: form.durationMonths,
        slaLevel: form.slaLevel,
        contractNo: form.contractNo
      }
      await createWarranty(payload)
      ElMessage.success('新建成功')
      dialogVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

// ============== 续保 ==============
function handleRenew(row: Warranty) {
  if (!row.id) return
  renewForm.id = row.id
  renewForm.durationMonths = 12
  renewForm.endDate = ''
  renewVisible.value = true
}

async function handleRenewSubmit() {
  if (!renewForm.durationMonths || !renewForm.endDate) {
    ElMessage.warning('请填写完整的续保信息')
    return
  }
  renewSubmitting.value = true
  try {
    await renewWarranty(renewForm.id, {
      durationMonths: renewForm.durationMonths,
      endDate: renewForm.endDate
    })
    ElMessage.success('续保成功')
    renewVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    renewSubmitting.value = false
  }
}

// ============== 退网 ==============
function handleDecommission(row: Warranty) {
  if (!row.id) return
  ElMessageBox.confirm(
    `确认对资产「${row.assetId}」执行退网操作吗？退网后质保将失效。`,
    '退网确认',
    { type: 'warning' }
  )
    .then(async () => {
      await decommissionAsset(row.id!)
      ElMessage.success('退网成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">质保期管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="资产 ID">
          <el-input-number
            v-model="query.assetId"
            :min="1"
            :controls="false"
            placeholder="请输入资产 ID"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="即将到期">
          <el-select v-model="query.expiringDays" placeholder="全部" clearable style="width: 160px">
            <el-option
              v-for="opt in expiringOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建质保</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="assetId" label="资产 ID" width="100" />
        <el-table-column label="起始日期" width="120" align="center">
          <template #default="{ row }">{{ formatDateTime(row.startDate) }}</template>
        </el-table-column>
        <el-table-column label="结束日期" width="120" align="center">
          <template #default="{ row }">{{ formatDateTime(row.endDate) }}</template>
        </el-table-column>
        <el-table-column label="SLA 等级" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getSlaMeta(row.slaLevel).tagType" size="small">
              {{ getSlaMeta(row.slaLevel).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contractNo" label="合同号" min-width="140" show-overflow-tooltip />
        <el-table-column label="剩余天数" width="120" align="center">
          <template #default="{ row }">
            <el-tag :color="remainingMeta(remainingDays(row.endDate)).color" size="small">
              {{ remainingMeta(remainingDays(row.endDate)).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleRenew(row)">续保</el-button>
            <el-button link type="danger" @click="handleDecommission(row)">退网</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无质保数据" />
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
    <el-dialog v-model="dialogVisible" title="新建质保" width="520px" destroy-on-close>
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
        <el-form-item label="起始日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择起始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="质保月数" prop="durationMonths">
          <el-input-number v-model="form.durationMonths" :min="1" :max="120" style="width: 100%" />
        </el-form-item>
        <el-form-item label="SLA 等级" prop="slaLevel">
          <el-select v-model="form.slaLevel" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="opt in slaOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="合同号">
          <el-input v-model="form.contractNo" placeholder="请输入合同号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 续保弹窗 -->
    <el-dialog v-model="renewVisible" title="质保续保" width="460px" destroy-on-close>
      <el-form :model="renewForm" :rules="renewRules" label-width="100px">
        <el-form-item label="续保月数" prop="durationMonths">
          <el-input-number v-model="renewForm.durationMonths" :min="1" :max="120" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="renewForm.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" :loading="renewSubmitting" @click="handleRenewSubmit">确定</el-button>
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
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
