<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  approveSettlement,
  createSettlement,
  getAgentPage,
  getSettlementDetail,
  getSettlementPage,
  rejectSettlement,
  type Agent,
  type Settlement,
  type SettlementDetail,
  type SettlementPageQuery,
  type SettlementStatus
} from '@/api/implementation'
import { listProjects as getProjectPage, type Project } from '@/api/project'

const loading = ref(false)
const tableData = ref<Settlement[]>([])
const total = ref(0)
const query = reactive<SettlementPageQuery>({ page: 1, size: 10, agentId: undefined, status: undefined })

const statusOptions: { label: string; value: SettlementStatus }[] = [
  { label: '待审批', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已推送', value: 'PUSHED' }
]

const statusTagType: Record<string, 'primary' | 'success' | 'info' | 'warning' | 'danger'> = {
  PENDING: 'info',
  APPROVED: 'success',
  REJECTED: 'danger',
  PUSHED: 'primary'
}

function statusLabel(status?: string): string {
  return statusOptions.find((s) => s.value === status)?.label ?? status ?? '-'
}

function money(v?: number): string {
  if (v == null) return '-'
  return v.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// Select data sources
const agentOptions = ref<Agent[]>([])
const projectOptions = ref<Project[]>([])

// Create settlement dialog
const formVisible = ref(false)
const formSubmitting = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  agentId: undefined as number | undefined,
  projectId: undefined as number | undefined,
  taskId: undefined as number | undefined,
  taxRate: 13,
  remark: '',
  details: [] as SettlementDetail[]
})

const rules: FormRules = {
  agentId: [{ required: true, message: '请选择代理商', trigger: 'change' }],
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  taxRate: [{ required: true, message: '请输入税率', trigger: 'blur' }]
}

function detailAmount(d: SettlementDetail): number {
  const qty = Number(d.workQuantity) || 0
  const price = Number(d.unitPrice) || 0
  return Math.round(qty * price * 100) / 100
}

const totalAmount = computed(() => {
  return Math.round(form.details.reduce((sum, d) => sum + detailAmount(d), 0) * 100) / 100
})

const taxAmount = computed(() => {
  return Math.round((totalAmount.value * (Number(form.taxRate) || 0)) / 100 * 100) / 100
})

const totalWithTax = computed(() => {
  return Math.round((totalAmount.value + taxAmount.value) * 100) / 100
})

function addDetailRow() {
  form.details.push({ itemName: '', workQuantity: 1, unit: '', unitPrice: 0, amount: 0 })
}

function removeDetailRow(index: number) {
  form.details.splice(index, 1)
}

function resetForm() {
  form.agentId = undefined
  form.projectId = undefined
  form.taskId = undefined
  form.taxRate = 13
  form.remark = ''
  form.details = []
}

// Detail view dialog
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<Settlement | null>(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getSettlementPage(query)
    tableData.value = res.records
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  try {
    const [agentRes, projectRes] = await Promise.all([
      getAgentPage({ page: 1, size: 100 }),
      getProjectPage({ page: 1, size: 100 })
    ])
    agentOptions.value = agentRes.records
    projectOptions.value = projectRes.records
  } catch {
    /* handled by interceptor */
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.agentId = undefined
  query.status = undefined
  query.page = 1
  loadData()
}

function handleCreate() {
  resetForm()
  addDetailRow()
  formVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.details.length === 0) {
      ElMessage.warning('请至少添加一条结算明细')
      return
    }
    const hasInvalid = form.details.some((d) => !d.itemName?.trim())
    if (hasInvalid) {
      ElMessage.warning('请填写所有明细的项目名称')
      return
    }
    formSubmitting.value = true
    try {
      const settlement: Settlement = {
        agentId: form.agentId,
        projectId: form.projectId,
        taskId: form.taskId,
        taxRate: form.taxRate,
        totalAmount: totalAmount.value,
        taxAmount: taxAmount.value,
        totalWithTax: totalWithTax.value,
        remark: form.remark
      }
      const details = form.details.map((d) => ({
        ...d,
        amount: detailAmount(d)
      }))
      await createSettlement({ settlement, details })
      ElMessage.success('结算单创建成功')
      formVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      formSubmitting.value = false
    }
  })
}

async function handleView(row: Settlement) {
  if (!row.id) return
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await getSettlementDetail(row.id)
  } catch {
    detailData.value = row
  } finally {
    detailLoading.value = false
  }
}

function handleApprove(row: Settlement) {
  if (!row.id) return
  ElMessageBox.prompt('请输入审批意见', '审批通过', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入审批意见'
  })
    .then(async ({ value }) => {
      await approveSettlement(row.id!, value)
      ElMessage.success('审批通过')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleRejectSettlement(row: Settlement) {
  if (!row.id) return
  ElMessageBox.prompt('请输入驳回原因', '驳回结算单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入驳回原因',
    inputValidator: (val) => !!val?.trim() || '驳回原因不能为空'
  })
    .then(async ({ value }) => {
      await rejectSettlement(row.id!, value)
      ElMessage.success('已驳回')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
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

onMounted(async () => {
  await loadOptions()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">结算管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="代理商">
          <el-select v-model="query.agentId" placeholder="全部代理商" clearable filterable style="width: 200px">
            <el-option
              v-for="a in agentOptions"
              :key="a.id"
              :label="a.agentName"
              :value="a.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 160px">
            <el-option
              v-for="s in statusOptions"
              :key="s.value"
              :label="s.label"
              :value="s.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleCreate">新建结算</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="settlementNo" label="结算单号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="agentName" label="代理商" min-width="140" show-overflow-tooltip />
        <el-table-column prop="projectName" label="关联项目" min-width="160" show-overflow-tooltip />
        <el-table-column label="总金额" width="130" align="right">
          <template #default="{ row }">{{ money(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="税额" width="120" align="right">
          <template #default="{ row }">{{ money(row.taxAmount) }}</template>
        </el-table-column>
        <el-table-column label="含税总额" width="140" align="right">
          <template #default="{ row }">
            <span class="amount-emph">{{ money(row.totalWithTax) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status] || 'info'" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="success" @click="handleApprove(row)">审批通过</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="danger" @click="handleRejectSettlement(row)">驳回</el-button>
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

    <!-- Create settlement dialog -->
    <el-dialog v-model="formVisible" title="新建结算单" width="820px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="代理商" prop="agentId">
              <el-select v-model="form.agentId" placeholder="请选择代理商" filterable style="width: 100%">
                <el-option
                  v-for="a in agentOptions"
                  :key="a.id"
                  :label="a.agentName"
                  :value="a.id!"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联项目" prop="projectId">
              <el-select v-model="form.projectId" placeholder="请选择项目" filterable style="width: 100%">
                <el-option
                  v-for="p in projectOptions"
                  :key="p.id"
                  :label="p.projectName"
                  :value="p.id!"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联任务ID" prop="taskId">
              <el-input-number v-model="form.taskId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税率(%)" prop="taxRate">
              <el-input-number v-model="form.taxRate" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="结算明细">
          <el-table :data="form.details" border size="small" style="width: 100%">
            <el-table-column label="项目名称" min-width="160">
              <template #default="{ row }">
                <el-input v-model="row.itemName" placeholder="项目名称" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="数量" width="110">
              <template #default="{ row }">
                <el-input-number v-model="row.workQuantity" :min="0" :precision="2" size="small" controls-position="right" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="单位" width="100">
              <template #default="{ row }">
                <el-input v-model="row.unit" placeholder="单位" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" controls-position="right" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="金额" width="120" align="right">
              <template #default="{ row }">{{ money(detailAmount(row)) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" size="small" @click="removeDetailRow($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button :icon="'Plus'" size="small" class="add-row-btn" @click="addDetailRow">添加明细</el-button>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>

        <el-form-item label="合计">
          <div class="summary">
            <span>总金额：<b>{{ money(totalAmount) }}</b></span>
            <span>税额：<b>{{ money(taxAmount) }}</b></span>
            <span>含税总额：<b class="amount-emph">{{ money(totalWithTax) }}</b></span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="formSubmitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Detail view dialog -->
    <el-dialog v-model="detailVisible" title="结算单详情" width="820px" destroy-on-close>
      <div v-loading="detailLoading">
        <el-descriptions v-if="detailData" :column="3" border>
          <el-descriptions-item label="结算单号">{{ detailData.settlementNo }}</el-descriptions-item>
          <el-descriptions-item label="代理商">{{ detailData.agentName }}</el-descriptions-item>
          <el-descriptions-item label="关联项目">{{ detailData.projectName }}</el-descriptions-item>
          <el-descriptions-item label="税率">{{ detailData.taxRate }}%</el-descriptions-item>
          <el-descriptions-item label="总金额">{{ money(detailData.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="税额">{{ money(detailData.taxAmount) }}</el-descriptions-item>
          <el-descriptions-item label="含税总额">
            <span class="amount-emph">{{ money(detailData.totalWithTax) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType[detailData.status!] || 'info'" size="small">
              {{ statusLabel(detailData.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailData.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="3">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="detail-sub-title">结算明细</h4>
        <el-table :data="detailData?.details || []" border stripe size="small">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="itemName" label="项目名称" min-width="160" />
          <el-table-column prop="workQuantity" label="数量" width="100" align="right" />
          <el-table-column prop="unit" label="单位" width="90" />
          <el-table-column prop="unitPrice" label="单价" width="120" align="right" />
          <el-table-column label="金额" width="130" align="right">
            <template #default="{ row }">{{ money(row.amount) }}</template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无明细" />
          </template>
        </el-table>
      </div>
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
.amount-emph {
  color: var(--el-color-danger);
  font-weight: 600;
}
.add-row-btn {
  margin-top: 8px;
  width: 100%;
  border-style: dashed;
}
.summary {
  display: flex;
  gap: 32px;
  font-size: 14px;
}
.summary b {
  color: var(--el-color-primary);
}
.detail-sub-title {
  margin: 16px 0 8px;
  font-size: 14px;
  font-weight: 600;
}
</style>
