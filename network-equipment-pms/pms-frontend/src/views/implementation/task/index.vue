<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  acceptTask,
  assignAgentTask,
  assignOemTask,
  completeTask,
  confirmTask,
  getAgentPage,
  getTaskPage,
  rejectTask,
  reportTaskProgress,
  startTask,
  type AgentAssignPayload,
  type ImplTask,
  type OemAssignPayload,
  type TaskPageQuery,
  type TaskStatus
} from '@/api/implementation'
import { listMilestones, listProjects, type Milestone, type Project } from '@/api/project'

interface AssignForm {
  taskName: string
  projectId: number | undefined
  milestoneId: number | undefined
  engineerId: number | undefined
  engineerName: string
  agentId: number | undefined
  planStartDate: string
  planEndDate: string
}

const activeTab = ref<'OEM' | 'AGENT'>('OEM')
const loading = ref(false)
const tableData = ref<ImplTask[]>([])
const total = ref(0)

const query = reactive<TaskPageQuery>({ page: 1, size: 10, projectId: undefined, taskType: 'OEM', status: undefined })

const statusOptions: { label: string; value: TaskStatus }[] = [
  { label: '待接单', value: 'PENDING' },
  { label: '已接单', value: 'ACCEPTED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已确认', value: 'CONFIRMED' },
  { label: '已驳回', value: 'REJECTED' }
]

const statusTagType: Record<string, 'primary' | 'success' | 'info' | 'warning' | 'danger'> = {
  PENDING: 'info',
  ACCEPTED: 'warning',
  IN_PROGRESS: 'primary',
  COMPLETED: 'success',
  CONFIRMED: 'success',
  REJECTED: 'danger'
}

function statusLabel(status?: string): string {
  return statusOptions.find((s) => s.value === status)?.label ?? status ?? '-'
}

// Select data sources
const projectOptions = ref<Project[]>([])
const agentOptions = ref<{ id?: number; agentName: string }[]>([])
const milestoneOptions = ref<Milestone[]>([])

// Assign task dialog
const assignVisible = ref(false)
const assignSubmitting = ref(false)
const assignFormRef = ref<FormInstance>()
const assignForm = reactive<AssignForm>(createEmptyAssignForm())

function createEmptyAssignForm(): AssignForm {
  return {
    taskName: '',
    projectId: undefined,
    milestoneId: undefined,
    engineerId: undefined,
    engineerName: '',
    agentId: undefined,
    planStartDate: '',
    planEndDate: ''
  }
}

const assignRules = computed<FormRules>(() => {
  const rules: FormRules = {
    taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
    projectId: [{ required: true, message: '请选择关联项目', trigger: 'change' }]
  }
  if (activeTab.value === 'OEM') {
    rules.engineerId = [{ required: true, message: '请输入工程师ID', trigger: 'blur' }]
  } else {
    rules.agentId = [{ required: true, message: '请选择代理商', trigger: 'change' }]
  }
  return rules
})

// Progress report dialog
const progressVisible = ref(false)
const progressSubmitting = ref(false)
const progressForm = reactive({
  taskId: 0,
  progressPercent: 0,
  workLog: '',
  photoUrlsText: ''
})

async function loadProjects() {
  try {
    const res = await listProjects({ page: 1, size: 100 })
    projectOptions.value = res.records
  } catch {
    /* handled by interceptor */
  }
}

async function loadAgents() {
  try {
    const res = await getAgentPage({ page: 1, size: 100 })
    agentOptions.value = res.records.map((a) => ({ id: a.id, agentName: a.agentName }))
  } catch {
    /* handled by interceptor */
  }
}

async function loadMilestones(projectId: number) {
  try {
    milestoneOptions.value = await listMilestones(projectId)
  } catch {
    milestoneOptions.value = []
  }
}

async function loadData() {
  loading.value = true
  try {
    query.taskType = activeTab.value
    const res = await getTaskPage(query)
    tableData.value = res.records
    total.value = res.total
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
  query.status = undefined
  query.page = 1
  loadData()
}

function handleTabChange() {
  query.page = 1
  query.status = undefined
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

function handleAssign() {
  Object.assign(assignForm, createEmptyAssignForm())
  milestoneOptions.value = []
  assignVisible.value = true
}

async function handleProjectChange(projectId: number | undefined) {
  assignForm.milestoneId = undefined
  milestoneOptions.value = []
  if (projectId) {
    await loadMilestones(projectId)
  }
}

async function handleAssignSubmit() {
  if (!assignFormRef.value) return
  await assignFormRef.value.validate(async (valid) => {
    if (!valid) return
    assignSubmitting.value = true
    try {
      if (activeTab.value === 'OEM') {
        const payload: OemAssignPayload = {
          taskName: assignForm.taskName,
          projectId: assignForm.projectId!,
          milestoneId: assignForm.milestoneId,
          engineerId: assignForm.engineerId!,
          engineerName: assignForm.engineerName,
          planStartDate: assignForm.planStartDate,
          planEndDate: assignForm.planEndDate
        }
        await assignOemTask(payload)
        ElMessage.success('原厂任务分配成功')
      } else {
        const payload: AgentAssignPayload = {
          taskName: assignForm.taskName,
          projectId: assignForm.projectId!,
          milestoneId: assignForm.milestoneId,
          agentId: assignForm.agentId!,
          planStartDate: assignForm.planStartDate,
          planEndDate: assignForm.planEndDate
        }
        await assignAgentTask(payload)
        ElMessage.success('代理商任务分配成功')
      }
      assignVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      assignSubmitting.value = false
    }
  })
}

function handleAccept(row: ImplTask) {
  if (!row.id) return
  ElMessageBox.confirm(`确定接单任务「${row.taskName}」吗？`, '接单确认', { type: 'warning' })
    .then(async () => {
      await acceptTask(row.id!)
      ElMessage.success('接单成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleStart(row: ImplTask) {
  if (!row.id) return
  ElMessageBox.confirm(`确定开始执行任务「${row.taskName}」吗？`, '开始确认', { type: 'warning' })
    .then(async () => {
      await startTask(row.id!)
      ElMessage.success('任务已开始')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleProgress(row: ImplTask) {
  if (!row.id) return
  progressForm.taskId = row.id
  progressForm.progressPercent = row.progress ?? 0
  progressForm.workLog = ''
  progressForm.photoUrlsText = ''
  progressVisible.value = true
}

async function handleProgressSubmit() {
  if (!progressForm.workLog) {
    ElMessage.warning('请填写工作日志')
    return
  }
  progressSubmitting.value = true
  try {
    const photoUrls = progressForm.photoUrlsText
      .split(/[\n,，]/)
      .map((s) => s.trim())
      .filter((s) => s.length > 0)
    await reportTaskProgress(progressForm.taskId, {
      progressPercent: progressForm.progressPercent,
      workLog: progressForm.workLog,
      photoUrls
    })
    ElMessage.success('进度上报成功')
    progressVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    progressSubmitting.value = false
  }
}

function handleComplete(row: ImplTask) {
  if (!row.id) return
  ElMessageBox.prompt('请输入完成说明', '完成任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入完成情况说明'
  })
    .then(async ({ value }) => {
      await completeTask(row.id!, value)
      ElMessage.success('任务已完成')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleConfirm(row: ImplTask) {
  if (!row.id) return
  ElMessageBox.prompt('请输入确认意见', '确认任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入确认意见'
  })
    .then(async ({ value }) => {
      await confirmTask(row.id!, value)
      ElMessage.success('任务已确认')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleReject(row: ImplTask) {
  if (!row.id) return
  ElMessageBox.prompt('请输入驳回原因', '驳回任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入驳回原因',
    inputValidator: (val) => !!val?.trim() || '驳回原因不能为空'
  })
    .then(async ({ value }) => {
      await rejectTask(row.id!, value)
      ElMessage.success('任务已驳回')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function projectNameOf(projectId?: number | null, projectName?: string): string {
  if (projectName && projectName.trim()) return projectName
  if (!projectId) return '-'
  return projectOptions.value.find((p) => p.id === projectId)?.projectName ?? '-'
}

function assigneeText(row: ImplTask): string {
  if (row.taskType === 'AGENT') {
    return row.agentName || '-'
  }
  return row.engineerName || '-'
}

// Keep the query task type synced when tab switches externally
watch(activeTab, () => {
  query.taskType = activeTab.value
})

onMounted(async () => {
  await Promise.all([loadProjects(), loadAgents()])
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">实施任务管理</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="原厂实施" name="OEM" />
        <el-tab-pane label="代理商实施" name="AGENT" />
      </el-tabs>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="项目">
          <el-select
            v-model="query.projectId"
            placeholder="全部项目"
            clearable
            filterable
            style="width: 200px"
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
        <el-button type="primary" :icon="'Plus'" @click="handleAssign">分配任务</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="taskName" label="任务名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.taskType === 'AGENT' ? 'warning' : 'primary'" size="small">
              {{ row.taskType === 'AGENT' ? '代理商' : '原厂' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联项目" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ projectNameOf(row.projectId, row.projectName) }}</template>
        </el-table-column>
        <el-table-column label="工程师/代理商" min-width="130">
          <template #default="{ row }">{{ assigneeText(row) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status] || 'info'" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="170">
          <template #default="{ row }">
            <el-progress :percentage="row.progress ?? 0" :stroke-width="14" :text-inside="true" />
          </template>
        </el-table-column>
        <el-table-column prop="planStartDate" label="计划开始" min-width="120" />
        <el-table-column prop="planEndDate" label="计划结束" min-width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" link type="primary" @click="handleAccept(row)">接单</el-button>
            <el-button v-if="row.status === 'ACCEPTED'" link type="primary" @click="handleStart(row)">开始</el-button>
            <template v-if="row.status === 'IN_PROGRESS'">
              <el-button link type="primary" @click="handleProgress(row)">上报进度</el-button>
              <el-button link type="success" @click="handleComplete(row)">完成</el-button>
            </template>
            <template v-if="row.status === 'COMPLETED'">
              <el-button link type="success" @click="handleConfirm(row)">确认</el-button>
              <el-button link type="danger" @click="handleReject(row)">驳回</el-button>
            </template>
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

    <!-- Assign task dialog -->
    <el-dialog
      v-model="assignVisible"
      :title="activeTab === 'OEM' ? '分配原厂实施任务' : '分配代理商实施任务'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="assignForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="关联项目" prop="projectId">
          <el-select
            v-model="assignForm.projectId"
            placeholder="请选择项目"
            filterable
            style="width: 100%"
            @change="handleProjectChange"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.projectName"
              :value="p.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="里程碑" prop="milestoneId">
          <el-select v-model="assignForm.milestoneId" placeholder="请选择里程碑" clearable style="width: 100%">
            <el-option
              v-for="m in milestoneOptions"
              :key="m.id"
              :label="m.name"
              :value="m.id!"
            />
          </el-select>
        </el-form-item>
        <template v-if="activeTab === 'OEM'">
          <el-form-item label="工程师ID" prop="engineerId">
            <el-input-number v-model="assignForm.engineerId" :min="1" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="工程师姓名" prop="engineerName">
            <el-input v-model="assignForm.engineerName" placeholder="请输入工程师姓名" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="代理商" prop="agentId">
            <el-select v-model="assignForm.agentId" placeholder="请选择代理商" filterable style="width: 100%">
              <el-option
                v-for="a in agentOptions"
                :key="a.id"
                :label="a.agentName"
                :value="a.id!"
              />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="计划开始" prop="planStartDate">
          <el-date-picker
            v-model="assignForm.planStartDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计划结束" prop="planEndDate">
          <el-date-picker
            v-model="assignForm.planEndDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择计划结束日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignSubmitting" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Progress report dialog -->
    <el-dialog v-model="progressVisible" title="上报进度" width="520px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="完成进度">
          <el-slider v-model="progressForm.progressPercent" show-input :max="100" />
        </el-form-item>
        <el-form-item label="工作日志" required>
          <el-input
            v-model="progressForm.workLog"
            type="textarea"
            :rows="4"
            placeholder="请输入工作日志"
          />
        </el-form-item>
        <el-form-item label="照片URL">
          <el-input
            v-model="progressForm.photoUrlsText"
            type="textarea"
            :rows="2"
            placeholder="多个URL用逗号或换行分隔"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressVisible = false">取消</el-button>
        <el-button type="primary" :loading="progressSubmitting" @click="handleProgressSubmit">提交</el-button>
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
