<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  assignIssue,
  closeIssue,
  createIssue,
  escalateIssue,
  listIssues,
  resolveIssue,
  type Issue,
  type IssueListQuery,
  type IssueStatus
} from '@/api/issue'
import { listProjects, type Project } from '@/api/project'
import type { ChangeRequestPriority } from '@/api/change-request'
import type { EpTagType } from '@/types'

defineOptions({ name: 'IssueLog' })

// ============== 选项配置 ==============
const statusOptions: { value: IssueStatus; label: string; tagType: EpTagType }[] = [
  { value: 'OPEN', label: '待处理', tagType: 'warning' },
  { value: 'IN_PROGRESS', label: '处理中', tagType: 'primary' },
  { value: 'RESOLVED', label: '已解决', tagType: 'success' },
  { value: 'CLOSED', label: '已关闭', tagType: 'info' }
]

const priorityOptions: { value: ChangeRequestPriority; label: string; tagType: EpTagType }[] = [
  { value: 'LOW', label: '低', tagType: 'info' },
  { value: 'MEDIUM', label: '中', tagType: 'success' },
  { value: 'HIGH', label: '高', tagType: 'warning' },
  { value: 'CRITICAL', label: '紧急', tagType: 'danger' }
]

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

function getPriorityMeta(priority?: string) {
  return priorityOptions.find((p) => p.value === priority) ?? { label: priority ?? '-', tagType: 'info' }
}

function formatDate(date?: string) {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

// 源关联文本
function sourceText(row: Issue): string {
  if (row.sourceRiskNo) return `风险：${row.sourceRiskNo}`
  if (row.sourceChangeNo) return `变更：${row.sourceChangeNo}`
  return '-'
}

// ============== 项目选项 ==============
const projectOptions = ref<Project[]>([])

async function loadProjectOptions() {
  try {
    const res = await listProjects({ page: 1, size: 200 })
    projectOptions.value = res.records ?? []
  } catch {
    /* ignored */
  }
}

function projectNameOf(projectId?: number | null): string {
  if (!projectId) return '-'
  return projectOptions.value.find((p) => p.id === projectId)?.projectName ?? '-'
}

// ============== 列表查询 ==============
const loading = ref(false)
const tableData = ref<Issue[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; projectId?: number; status?: string; assigneeId?: number }>({
  page: 1,
  size: 10,
  projectId: undefined,
  status: '',
  assigneeId: undefined
})

async function loadData() {
  loading.value = true
  try {
    const params: IssueListQuery = { page: query.page, size: query.size }
    if (query.projectId) params.projectId = query.projectId
    if (query.status) params.status = query.status
    if (query.assigneeId) params.assigneeId = query.assigneeId
    const res = await listIssues(params)
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
  query.status = ''
  query.assigneeId = undefined
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
const createVisible = ref(false)
const createSubmitting = ref(false)
const createFormRef = ref<FormInstance>()

interface IssueForm {
  projectId: number | undefined
  description: string
  priority: ChangeRequestPriority
  targetResolveDate: string
}

function createEmptyForm(): IssueForm {
  return {
    projectId: undefined,
    description: '',
    priority: 'MEDIUM',
    targetResolveDate: ''
  }
}

const createForm = reactive<IssueForm>(createEmptyForm())

const createRules: FormRules = {
  projectId: [{ required: true, message: '请输入项目 ID', trigger: 'blur' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

function handleAdd() {
  Object.assign(createForm, createEmptyForm())
  createVisible.value = true
}

async function handleCreateSubmit() {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    createSubmitting.value = true
    try {
      const payload: Issue = {
        projectId: createForm.projectId!,
        description: createForm.description,
        priority: createForm.priority,
        targetResolveDate: createForm.targetResolveDate,
        status: 'OPEN'
      }
      await createIssue(payload)
      ElMessage.success('新建成功')
      createVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      createSubmitting.value = false
    }
  })
}

// ============== 分派 ==============
const assignVisible = ref(false)
const assignSubmitting = ref(false)
const assignForm = reactive({ id: 0, assigneeId: undefined as number | undefined })

function handleAssign(row: Issue) {
  if (!row.id) return
  assignForm.id = row.id
  assignForm.assigneeId = undefined
  assignVisible.value = true
}

async function handleAssignSubmit() {
  if (!assignForm.assigneeId) {
    ElMessage.warning('请输入负责人 ID')
    return
  }
  assignSubmitting.value = true
  try {
    await assignIssue(assignForm.id, assignForm.assigneeId)
    ElMessage.success('分派成功')
    assignVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    assignSubmitting.value = false
  }
}

// ============== 解决 / 关闭 ==============
function handleResolve(row: Issue) {
  if (!row.id) return
  ElMessageBox.confirm(`确认将问题「${row.issueNo ?? row.description}」标记为已解决吗？`, '解决问题', {
    type: 'warning'
  })
    .then(async () => {
      await resolveIssue(row.id!)
      ElMessage.success('已标记为解决')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleClose(row: Issue) {
  if (!row.id) return
  ElMessageBox.confirm(`确认关闭问题「${row.issueNo ?? row.description}」吗？`, '关闭问题', {
    type: 'warning'
  })
    .then(async () => {
      await closeIssue(row.id!)
      ElMessage.success('已关闭')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============== 升级 ==============
function handleEscalate(row: Issue) {
  if (!row.id) return
  ElMessageBox.prompt('请输入升级原因（将触发创建变更请求）', '升级问题', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入升级原因',
    inputValidator: (val) => !!val?.trim() || '升级原因不能为空'
  })
    .then(async ({ value }) => {
      await escalateIssue(row.id!, value)
      ElMessage.success('已升级并创建变更请求')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(() => {
  loadProjectOptions()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">问题日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="所属项目">
          <el-select
            v-model="query.projectId"
            placeholder="选择项目"
            clearable
            filterable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.projectName"
              :value="p.id!"
            />
          </el-select>
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
        <el-form-item label="负责人 ID">
          <el-input
            v-model.number="query.assigneeId"
            placeholder="负责人 ID"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建问题</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="issueNo" label="问题编号" width="130" />
        <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="所属项目" width="140" align="center">
          <template #default="{ row }">{{ projectNameOf(row.projectId) }}</template>
        </el-table-column>
        <el-table-column label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityMeta(row.priority).tagType" size="small">
              {{ getPriorityMeta(row.priority).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).tagType" size="small">
              {{ getStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="raisedByName" label="提出人" width="100" />
        <el-table-column prop="assigneeName" label="负责人" width="100" />
        <el-table-column label="目标解决日期" width="120" align="center">
          <template #default="{ row }">{{ formatDate(row.targetResolveDate) }}</template>
        </el-table-column>
        <el-table-column label="源关联" width="150">
          <template #default="{ row }">{{ sourceText(row) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'OPEN' || row.status === 'IN_PROGRESS'"
              link
              type="primary"
              @click="handleAssign(row)"
            >
              分派
            </el-button>
            <el-button
              v-if="row.status === 'OPEN' || row.status === 'IN_PROGRESS'"
              link
              type="success"
              @click="handleResolve(row)"
            >
              解决
            </el-button>
            <el-button
              v-if="row.status === 'RESOLVED'"
              link
              type="primary"
              @click="handleClose(row)"
            >
              关闭
            </el-button>
            <el-button
              v-if="row.status !== 'CLOSED'"
              link
              type="danger"
              @click="handleEscalate(row)"
            >
              升级
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无问题数据" />
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

    <!-- 新建问题弹窗 -->
    <el-dialog v-model="createVisible" title="新建问题" width="560px" destroy-on-close>
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="110px">
        <el-form-item label="项目 ID" prop="projectId">
          <el-input-number
            v-model="createForm.projectId"
            :min="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入问题描述"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="createForm.priority" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="opt in priorityOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标解决日期">
          <el-date-picker
            v-model="createForm.targetResolveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择目标解决日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleCreateSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分派弹窗 -->
    <el-dialog v-model="assignVisible" title="分派问题" width="420px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="负责人 ID" required>
          <el-input-number
            v-model="assignForm.assigneeId"
            :min="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignSubmitting" @click="handleAssignSubmit">
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
