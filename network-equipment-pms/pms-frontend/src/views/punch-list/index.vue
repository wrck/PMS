<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import FileUploader from '@/components/FileUploader/index.vue'
import {
  createPunchList,
  deletePunchList,
  listPunchLists,
  resolvePunchList,
  verifyPunchList,
  type PunchList,
  type PunchListSeverity,
  type PunchListStatus,
  type WalkdownStage
} from '@/api/punch-list'

const loading = ref(false)
const tableData = ref<PunchList[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; projectId?: number; severity?: string; status?: string }>({
  page: 1,
  size: 10,
  projectId: undefined,
  severity: '',
  status: ''
})

// 严重等级选项
const severityOptions: { value: PunchListSeverity; label: string; tagType: any }[] = [
  { value: 'SAFETY', label: '安全', tagType: 'danger' },
  { value: 'FUNCTIONAL', label: '功能', tagType: 'warning' },
  { value: 'COSMETIC', label: '外观', tagType: 'info' }
]

// 状态选项
const statusOptions: { value: PunchListStatus; label: string; tagType: any }[] = [
  { value: 'OPEN', label: '待整改', tagType: 'warning' },
  { value: 'RESOLVED', label: '已整改', tagType: 'primary' },
  { value: 'VERIFIED', label: '已验证', tagType: 'success' }
]

// 走场阶段选项
const walkdownOptions: { value: WalkdownStage; label: string }[] = [
  { value: 'PRE_PUNCH', label: '预走场' },
  { value: 'FORMAL', label: '正式走场' }
]

function getSeverityMeta(severity?: string) {
  return severityOptions.find((s) => s.value === severity) ?? { label: severity ?? '-', tagType: 'info' }
}

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

// 时间格式化：去 T 并截取到秒
function formatDateTime(val?: string): string {
  return val?.replace('T', ' ').slice(0, 19) ?? '-'
}

// ============== 弹窗表单 ==============
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

interface PunchListForm {
  id?: number
  projectId: number | undefined
  milestoneId: number | undefined
  severity: PunchListSeverity
  title: string
  description: string
  walkdownStage: WalkdownStage
  assigneeId: number | undefined
  assigneeName: string
  deadline: string
  attachmentIds: number[]
}

function createEmptyForm(): PunchListForm {
  return {
    id: undefined,
    projectId: undefined,
    milestoneId: undefined,
    severity: 'FUNCTIONAL',
    title: '',
    description: '',
    walkdownStage: 'PRE_PUNCH',
    assigneeId: undefined,
    assigneeName: '',
    deadline: '',
    attachmentIds: []
  }
}

const form = reactive<PunchListForm>(createEmptyForm())

const rules: FormRules = {
  projectId: [{ required: true, message: '请输入项目 ID', trigger: 'blur' }],
  severity: [{ required: true, message: '请选择严重等级', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  walkdownStage: [{ required: true, message: '请选择走场阶段', trigger: 'change' }]
}

// FileUploader 上传成功回调，收集附件 ID
function handleUploaded(payload: any) {
  const id = typeof payload === 'number' ? payload : payload?.id
  if (typeof id === 'number') {
    form.attachmentIds.push(id)
  }
}

// ============== 数据加载 ==============
async function loadData() {
  loading.value = true
  try {
    const params: { page: number; size: number; projectId?: number; severity?: string; status?: string } = {
      page: query.page,
      size: query.size
    }
    if (query.projectId) params.projectId = query.projectId
    if (query.severity) params.severity = query.severity
    if (query.status) params.status = query.status
    const res = await listPunchLists(params)
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
  query.projectId = undefined
  query.severity = ''
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
      const payload: PunchList = {
        projectId: form.projectId!,
        milestoneId: form.milestoneId,
        severity: form.severity,
        title: form.title,
        description: form.description,
        walkdownStage: form.walkdownStage,
        assigneeId: form.assigneeId,
        assigneeName: form.assigneeName,
        deadline: form.deadline,
        status: 'OPEN',
        attachmentIds: form.attachmentIds
      }
      await createPunchList(payload)
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

// ============== 整改 / 验证 / 删除 ==============
function handleResolve(row: PunchList) {
  if (!row.id) return
  ElMessageBox.confirm(`确认缺陷「${row.title}」已完成整改吗？`, '整改确认', { type: 'warning' })
    .then(async () => {
      await resolvePunchList(row.id!)
      ElMessage.success('已标记为整改完成')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleVerify(row: PunchList) {
  if (!row.id) return
  ElMessageBox.confirm(`确认验证通过缺陷「${row.title}」吗？`, '验证确认', { type: 'warning' })
    .then(async () => {
      await verifyPunchList(row.id!)
      ElMessage.success('验证通过')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: PunchList) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除缺陷「${row.title}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deletePunchList(row.id!)
      ElMessage.success('删除成功')
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
        <span class="page-title">Punch List 管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目 ID">
          <el-input-number
            v-model="query.projectId"
            :min="1"
            :controls="false"
            placeholder="请输入项目 ID"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="严重等级">
          <el-select v-model="query.severity" placeholder="全部等级" clearable style="width: 160px">
            <el-option
              v-for="opt in severityOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 160px">
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建缺陷</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="严重等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getSeverityMeta(row.severity).tagType" size="small">
              {{ getSeverityMeta(row.severity).label }}
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
        <el-table-column prop="assigneeName" label="负责人" width="110" />
        <el-table-column label="截止日期" width="120" align="center">
          <template #default="{ row }">{{ formatDateTime(row.deadline) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'OPEN'"
              link
              type="primary"
              @click="handleResolve(row)"
            >
              整改
            </el-button>
            <el-button
              v-if="row.status === 'RESOLVED'"
              link
              type="success"
              @click="handleVerify(row)"
            >
              验证
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无缺陷数据" />
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
    <el-dialog
      v-model="dialogVisible"
      title="新建缺陷"
      width="640px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目 ID" prop="projectId">
              <el-input-number
                v-model="form.projectId"
                :min="1"
                :controls="false"
                placeholder="请输入项目 ID"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="里程碑 ID">
              <el-input-number
                v-model="form.milestoneId"
                :min="1"
                :controls="false"
                placeholder="可选"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="严重等级" prop="severity">
              <el-select v-model="form.severity" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in severityOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="走场阶段" prop="walkdownStage">
              <el-select v-model="form.walkdownStage" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in walkdownOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入缺陷标题" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="请输入缺陷描述"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人 ID">
              <el-input-number
                v-model="form.assigneeId"
                :min="1"
                :controls="false"
                placeholder="可选"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker
                v-model="form.deadline"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择截止日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="缺陷照片">
              <FileUploader biz-type="PUNCH_LIST" @uploaded="handleUploaded" />
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
