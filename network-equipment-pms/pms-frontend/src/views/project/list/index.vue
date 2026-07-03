<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  approveProject,
  createProject,
  deleteProject,
  listProjects,
  updateProject,
  type Project,
  type ProjectStatus,
  type ProjectType
} from '@/api/project'

const router = useRouter()

const loading = ref(false)
const tableData = ref<Project[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; projectName?: string; status?: string }>({
  page: 1,
  size: 10,
  projectName: '',
  status: ''
})

// 状态选项（颜色与文案）
const statusOptions: { value: ProjectStatus; label: string; tagType: any }[] = [
  { value: 'PENDING', label: '待审批', tagType: 'info' },
  { value: 'APPROVED', label: '已立项', tagType: 'warning' },
  { value: 'IN_PROGRESS', label: '执行中', tagType: 'primary' },
  { value: 'INITIAL_ACCEPTANCE', label: '初验', tagType: 'warning' },
  { value: 'FINAL_ACCEPTANCE', label: '终验中', tagType: 'danger' },
  { value: 'COMPLETED', label: '已完成', tagType: 'success' },
  { value: 'CLOSED', label: '已关闭', tagType: 'info' },
  { value: 'REJECTED', label: '已驳回', tagType: 'danger' }
]

const typeOptions: { value: ProjectType; label: string }[] = [
  { value: 'NETWORK_DEVICE', label: '网络设备' },
  { value: 'SECURITY', label: '安全设备' },
  { value: 'DATACENTER', label: '数据中心' }
]

const priorityOptions = [
  { value: 1, label: '高' },
  { value: 2, label: '中' },
  { value: 3, label: '低' }
]

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

function getTypeLabel(type?: string) {
  return typeOptions.find((t) => t.value === type)?.label ?? type ?? '-'
}

function formatDate(date?: string) {
  if (!date) return '-'
  // 统一只取 YYYY-MM-DD
  return date.length > 10 ? date.substring(0, 10) : date
}

// ============== 弹窗表单 ==============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface ProjectForm {
  id?: number
  name: string
  type?: ProjectType
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  managerName?: string
  priority?: number
  description?: string
}

function createEmptyForm(): ProjectForm {
  return {
    id: undefined,
    name: '',
    type: 'NETWORK_DEVICE',
    customerName: '',
    customerContact: '',
    customerPhone: '',
    contractNo: '',
    contractAmount: undefined,
    planStartDate: '',
    planEndDate: '',
    managerName: '',
    priority: 2,
    description: ''
  }
}

const form = reactive<ProjectForm>(createEmptyForm())

const dateRange = ref<[string, string] | null>(null)

const rules: FormRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择项目类型', trigger: 'change' }]
}

// ============== 数据加载 ==============
async function loadData() {
  loading.value = true
  try {
    const params: { page: number; size: number; projectName?: string; status?: string } = {
      page: query.page,
      size: query.size
    }
    if (query.projectName) params.projectName = query.projectName
    if (query.status) params.status = query.status
    const res = await listProjects(params)
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
  query.projectName = ''
  query.status = ''
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

// ============== 新增 / 编辑 ==============
function handleAdd() {
  dialogTitle.value = '新建项目'
  Object.assign(form, createEmptyForm())
  dateRange.value = null
  dialogVisible.value = true
}

function handleEdit(row: Project) {
  dialogTitle.value = '编辑项目'
  Object.assign(form, createEmptyForm(), row)
  dateRange.value =
    row.planStartDate && row.planEndDate ? [row.planStartDate, row.planEndDate] : null
  dialogVisible.value = true
}

function handleDateRangeChange(val: [string, string] | null) {
  if (val && val.length === 2) {
    form.planStartDate = val[0]
    form.planEndDate = val[1]
  } else {
    form.planStartDate = ''
    form.planEndDate = ''
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: ProjectForm = { ...form }
      if (form.id) {
        await updateProject(payload)
        ElMessage.success('更新成功')
      } else {
        await createProject(payload)
        ElMessage.success('新建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

// ============== 审批 / 删除 ==============
function handleApprove(row: Project) {
  if (!row.id) return
  ElMessageBox.confirm(`确认通过项目「${row.name}」的立项审批吗？`, '立项审批', { type: 'warning' })
    .then(async () => {
      await approveProject(row.id!)
      ElMessage.success('审批通过')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: Project) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除项目「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteProject(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function viewDetail(row: Project) {
  if (!row.id) return
  router.push(`/project/detail/${row.id}`)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">项目管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目名称">
          <el-input
            v-model="query.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select
            v-model="query.status"
            placeholder="全部状态"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="opt in statusOptions"
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建项目</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="code" label="项目编号" min-width="140" />
        <el-table-column prop="name" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="项目类型" width="120">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).tagType">
              {{ getStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="160" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="Number(row.progress ?? 0)"
              :stroke-width="10"
              :status="(Number(row.progress ?? 0) >= 100 ? 'success' : '') as any"
            />
          </template>
        </el-table-column>
        <el-table-column label="计划开始日期" width="120" align="center">
          <template #default="{ row }">{{ formatDate(row.planStartDate) }}</template>
        </el-table-column>
        <el-table-column label="计划结束日期" width="120" align="center">
          <template #default="{ row }">{{ formatDate(row.planEndDate) }}</template>
        </el-table-column>
        <el-table-column prop="managerName" label="项目经理" width="110" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              link
              type="warning"
              @click="handleApprove(row)"
            >
              立项审批
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无项目数据" />
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in typeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称">
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户联系人">
              <el-input v-model="form.customerContact" placeholder="请输入客户联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户电话">
              <el-input v-model="form.customerPhone" placeholder="请输入客户电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同编号">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同金额">
              <el-input-number
                v-model="form.contractAmount"
                :min="0"
                :precision="2"
                :controls="false"
                style="width: 100%"
                placeholder="请输入合同金额"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目经理">
              <el-input v-model="form.managerName" placeholder="请输入项目经理" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="form.priority" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in priorityOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="计划起止日期">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%"
                @change="handleDateRangeChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="项目描述">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="请输入项目描述"
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
