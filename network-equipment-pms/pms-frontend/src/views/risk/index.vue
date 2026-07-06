<script setup lang="ts">
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createRisk,
  deleteRisk,
  getRiskMatrix,
  listRisks,
  markOccurred,
  updateRisk,
  type Risk,
  type RiskCategory,
  type RiskListQuery,
  type RiskMatrixCell,
  type RiskMitigation,
  type RiskPriority,
  type RiskStatus
} from '@/api/risk'
import type { EpTagType } from '@/types'

defineOptions({ name: 'RiskRegister' })

// ============== 选项配置 ==============
const statusOptions: { value: RiskStatus; label: string; tagType: EpTagType }[] = [
  { value: 'OPEN', label: '待处理', tagType: 'info' },
  { value: 'IN_PROGRESS', label: '处理中', tagType: 'primary' },
  { value: 'CLOSED', label: '已关闭', tagType: 'success' },
  { value: 'ESCALATED', label: '已升级', tagType: 'danger' }
]

const priorityOptions: { value: RiskPriority; label: string; tagType: EpTagType }[] = [
  { value: 'LOW', label: '低', tagType: 'success' },
  { value: 'MEDIUM', label: '中', tagType: 'warning' },
  { value: 'HIGH', label: '高', tagType: 'danger' }
]

const categoryOptions: { value: RiskCategory; label: string }[] = [
  { value: 'TECHNICAL', label: '技术' },
  { value: 'EXTERNAL', label: '外部' },
  { value: 'ORGANIZATIONAL', label: '组织' },
  { value: 'PM', label: '项目管理' }
]

const mitigationOptions: { value: RiskMitigation; label: string }[] = [
  { value: 'AVOID', label: '规避' },
  { value: 'MITIGATE', label: '缓解' },
  { value: 'TRANSFER', label: '转移' },
  { value: 'ACCEPT', label: '接受' }
]

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

function getPriorityMeta(priority?: string) {
  return priorityOptions.find((p) => p.value === priority) ?? { label: priority ?? '-', tagType: 'info' }
}

function getCategoryLabel(category?: string) {
  return categoryOptions.find((c) => c.value === category)?.label ?? category ?? '-'
}

// ============== 矩阵热力图 ==============
const matrixChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null
const matrixData = ref<RiskMatrixCell[]>([])

// 点击格子展示的风险列表
const cellDialogVisible = ref(false)
const cellDialogTitle = ref('')
const cellRisks = ref<Risk[]>([])

// 根据分数计算优先级颜色
function scoreToColor(score: number): string {
  if (score <= 6) return '#67C23A' // LOW 绿
  if (score <= 12) return '#E6A23C' // MEDIUM 黄
  return '#F56C6C' // HIGH 红
}

function scoreToPriority(score: number): string {
  if (score <= 6) return 'LOW'
  if (score <= 12) return 'MEDIUM'
  return 'HIGH'
}

async function loadMatrix() {
  try {
    const matrix = await getRiskMatrix(query.projectId)
    // 二维数组扁平化
    const flat: RiskMatrixCell[] = []
    if (Array.isArray(matrix)) {
      matrix.forEach((row) => {
        if (Array.isArray(row)) flat.push(...row)
      })
    }
    matrixData.value = flat
    nextTick(() => renderMatrix())
  } catch {
    /* handled by interceptor */
  }
}

function renderMatrix() {
  if (!matrixChartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(matrixChartRef.value)
    chartInstance.on('click', handleMatrixClick)
  }
  const data = matrixData.value.map((cell) => {
    const score = cell.likelihood * cell.impact
    return {
      value: [cell.impact - 1, cell.likelihood - 1, score],
      cellData: cell
    }
  })
  chartInstance.setOption(
    {
      tooltip: {
        formatter: (params: unknown) => {
          const cell = (params as { data: { cellData: RiskMatrixCell } }).data.cellData
          const score = cell.likelihood * cell.impact
          return [
            `概率：${cell.likelihood}`,
            `影响：${cell.impact}`,
            `分数：${score}（${scoreToPriority(score)}）`,
            `风险数量：${cell.count}`
          ].join('<br/>')
        }
      },
      grid: { left: 60, right: 30, top: 30, bottom: 50 },
      xAxis: {
        type: 'category',
        data: ['1', '2', '3', '4', '5'],
        name: '影响',
        nameLocation: 'middle',
        nameGap: 30,
        splitArea: { show: true }
      },
      yAxis: {
        type: 'category',
        data: ['1', '2', '3', '4', '5'],
        name: '概率',
        nameLocation: 'middle',
        nameGap: 40,
        splitArea: { show: true }
      },
      visualMap: {
        show: false,
        min: 1,
        max: 25,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '0',
        inRange: { color: ['#67C23A', '#E6A23C', '#F56C6C'] }
      },
      series: [
        {
          name: '风险矩阵',
          type: 'heatmap',
          data,
          label: {
            show: true,
            formatter: (params: unknown) => {
              return (params as { data: { cellData: RiskMatrixCell } }).data.cellData.count
            }
          },
          emphasis: {
            itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' }
          }
        }
      ]
    },
    { notMerge: true }
  )
}

function handleMatrixClick(params: unknown) {
  const cell = (params as { data?: { cellData?: RiskMatrixCell } })?.data?.cellData
  if (!cell || !cell.risks || cell.risks.length === 0) {
    ElMessage.info('该格子暂无风险')
    return
  }
  cellDialogTitle.value = `概率${cell.likelihood} × 影响${cell.impact}（共${cell.count}项）`
  cellRisks.value = cell.risks
  cellDialogVisible.value = true
}

function handleResize() {
  chartInstance?.resize()
}

// ============== 列表查询 ==============
const loading = ref(false)
const tableData = ref<Risk[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; projectId?: number; status?: string; priority?: string }>({
  page: 1,
  size: 10,
  projectId: undefined,
  status: '',
  priority: ''
})

async function loadData() {
  loading.value = true
  try {
    const params: RiskListQuery = { page: query.page, size: query.size }
    if (query.projectId) params.projectId = query.projectId
    if (query.status) params.status = query.status
    if (query.priority) params.priority = query.priority
    const res = await listRisks(params)
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
  loadMatrix()
}

function handleReset() {
  query.projectId = undefined
  query.status = ''
  query.priority = ''
  query.page = 1
  loadData()
  loadMatrix()
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

// ============== 新增 / 编辑 ==============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface RiskForm {
  id?: number
  projectId: number | undefined
  description: string
  category: RiskCategory
  likelihood: number
  impact: number
  mitigation: RiskMitigation
  contingencyPlan: string
  ownerId: number | undefined
  status: RiskStatus
  reviewDate: string
}

function createEmptyForm(): RiskForm {
  return {
    id: undefined,
    projectId: undefined,
    description: '',
    category: 'TECHNICAL',
    likelihood: 3,
    impact: 3,
    mitigation: 'MITIGATE',
    contingencyPlan: '',
    ownerId: undefined,
    status: 'OPEN',
    reviewDate: ''
  }
}

const form = reactive<RiskForm>(createEmptyForm())

const rules: FormRules = {
  projectId: [{ required: true, message: '请输入项目 ID', trigger: 'blur' }],
  description: [{ required: true, message: '请输入风险描述', trigger: 'blur' }],
  category: [{ required: true, message: '请选择风险分类', trigger: 'change' }],
  likelihood: [{ required: true, message: '请选择概率', trigger: 'change' }],
  impact: [{ required: true, message: '请选择影响', trigger: 'change' }],
  mitigation: [{ required: true, message: '请选择缓解策略', trigger: 'change' }]
}

function handleAdd() {
  dialogTitle.value = '新建风险'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: Risk) {
  dialogTitle.value = '编辑风险'
  Object.assign(form, createEmptyForm(), {
    id: row.id,
    projectId: row.projectId,
    description: row.description,
    category: row.category,
    likelihood: row.likelihood,
    impact: row.impact,
    mitigation: row.mitigation,
    contingencyPlan: row.contingencyPlan ?? '',
    ownerId: row.ownerId,
    status: row.status,
    reviewDate: row.reviewDate ?? ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: Risk = {
        id: form.id,
        projectId: form.projectId!,
        description: form.description,
        category: form.category,
        likelihood: form.likelihood,
        impact: form.impact,
        mitigation: form.mitigation,
        contingencyPlan: form.contingencyPlan,
        ownerId: form.ownerId,
        status: form.status,
        reviewDate: form.reviewDate
      }
      if (form.id) {
        await updateRisk(form.id, payload)
        ElMessage.success('更新成功')
      } else {
        await createRisk(payload)
        ElMessage.success('新建成功')
      }
      dialogVisible.value = false
      loadData()
      loadMatrix()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

// ============== 标记已发生 / 删除 ==============
function handleMarkOccurred(row: Risk) {
  if (!row.id) return
  ElMessageBox.confirm(
    `确认将风险「${row.riskNo ?? row.description}」标记为已发生并转为问题吗？`,
    '标记已发生',
    { type: 'warning' }
  )
    .then(async () => {
      await markOccurred(row.id!)
      ElMessage.success('已标记为发生并转为问题')
      loadData()
      loadMatrix()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: Risk) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除风险「${row.riskNo ?? row.description}」吗？`, '提示', {
    type: 'warning'
  })
    .then(async () => {
      await deleteRisk(row.id!)
      ElMessage.success('删除成功')
      loadData()
      loadMatrix()
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(() => {
  loadData()
  loadMatrix()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<template>
  <div class="page-container">
    <!-- 5x5 矩阵热力图 -->
    <el-card shadow="never">
      <template #header>
        <span class="page-title">风险登记册</span>
      </template>
      <div class="matrix-section">
        <div ref="matrixChartRef" class="matrix-chart" />
        <div class="matrix-legend">
          <div class="legend-item">
            <span class="legend-color" style="background: #67C23A" />
            <span>LOW（分数 ≤ 6）</span>
          </div>
          <div class="legend-item">
            <span class="legend-color" style="background: #E6A23C" />
            <span>MEDIUM（分数 7-12）</span>
          </div>
          <div class="legend-item">
            <span class="legend-color" style="background: #F56C6C" />
            <span>HIGH（分数 13-25）</span>
          </div>
          <div class="legend-tip">点击格子查看该区间的风险列表</div>
        </div>
      </div>
    </el-card>

    <!-- 风险列表 -->
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目 ID">
          <el-input
            v-model.number="query.projectId"
            placeholder="请输入项目 ID"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option
              v-for="opt in statusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="query.priority" placeholder="全部优先级" clearable style="width: 140px">
            <el-option
              v-for="opt in priorityOptions"
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建风险</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="riskNo" label="风险编号" width="130" />
        <el-table-column prop="description" label="风险描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="100" align="center">
          <template #default="{ row }">{{ getCategoryLabel(row.category) }}</template>
        </el-table-column>
        <el-table-column prop="likelihood" label="概率" width="80" align="center" />
        <el-table-column prop="impact" label="影响" width="80" align="center" />
        <el-table-column prop="score" label="分数" width="80" align="center" />
        <el-table-column label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityMeta(row.priority).tagType" size="small">
              {{ getPriorityMeta(row.priority).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).tagType" size="small">
              {{ getStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="110" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status !== 'CLOSED'"
              link
              type="warning"
              @click="handleMarkOccurred(row)"
            >
              标记已发生
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无风险数据" />
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

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目 ID" prop="projectId">
              <el-input-number
                v-model="form.projectId"
                :min="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in categoryOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入风险描述" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="概率" prop="likelihood">
              <el-input-number v-model="form.likelihood" :min="1" :max="5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响" prop="impact">
              <el-input-number v-model="form.impact" :min="1" :max="5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缓解策略" prop="mitigation">
              <el-select v-model="form.mitigation" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in mitigationOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人 ID">
              <el-input-number
                v-model="form.ownerId"
                :min="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="复审日期">
              <el-date-picker
                v-model="form.reviewDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择复审日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="应急预案">
              <el-input
                v-model="form.contingencyPlan"
                type="textarea"
                :rows="3"
                placeholder="请输入应急预案"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 格子风险列表弹窗 -->
    <el-dialog v-model="cellDialogVisible" :title="cellDialogTitle" width="640px">
      <el-table :data="cellRisks" border stripe size="small">
        <el-table-column prop="riskNo" label="风险编号" width="130" />
        <el-table-column prop="description" label="风险描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="分数" width="80" align="center">
          <template #default="{ row }">{{ row.likelihood * row.impact }}</template>
        </el-table-column>
        <el-table-column label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityMeta(row.priority).tagType" size="small">
              {{ getPriorityMeta(row.priority).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).tagType" size="small">
              {{ getStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="cellDialogVisible = false">关闭</el-button>
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
.matrix-section {
  display: flex;
  align-items: center;
  gap: 24px;
}
.matrix-chart {
  width: 420px;
  height: 360px;
  flex-shrink: 0;
}
.matrix-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.legend-color {
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 3px;
}
.legend-tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
