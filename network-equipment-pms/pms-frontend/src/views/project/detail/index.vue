<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  applyAcceptance,
  approveAcceptance,
  createMilestone,
  deleteMilestone,
  getAcceptanceByProject,
  getProject,
  listMilestones,
  rejectAcceptance,
  updateMilestone,
  updateMilestoneProgress,
  type FinalAcceptance,
  type Milestone,
  type Project,
  type ProjectStatus,
  type ProjectType
} from '@/api/project'

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.id))

const loading = ref(false)
const project = ref<Project | null>(null)
const activeTab = ref('basic')

// ===== 状态映射 =====
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

const priorityOptions: { value: number; label: string }[] = [
  { value: 1, label: '高' },
  { value: 2, label: '中' },
  { value: 3, label: '低' }
]

const milestoneTypeOptions = [
  { value: 'START', label: '启动' },
  { value: 'DELIVERY', label: '到货' },
  { value: 'INSTALL', label: '安装' },
  { value: 'INITIAL_ACCEPTANCE', label: '初验' },
  { value: 'FINAL_ACCEPTANCE', label: '终验' },
  { value: 'OTHER', label: '其他' }
]

function getStatusMeta(status?: string) {
  return statusOptions.find((s) => s.value === status) ?? { label: status ?? '-', tagType: 'info' }
}

function getTypeLabel(type?: string) {
  return typeOptions.find((t) => t.value === type)?.label ?? type ?? '-'
}

function getPriorityLabel(priority?: number) {
  return priorityOptions.find((p) => p.value === priority)?.label ?? '-'
}

function getMilestoneTypeLabel(type?: string) {
  return milestoneTypeOptions.find((t) => t.value === type)?.label ?? type ?? '-'
}

function formatDate(date?: string) {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

// ===== 加载项目详情 =====
async function loadProject() {
  loading.value = true
  try {
    project.value = await getProject(projectId.value)
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

// ============== 里程碑 ==============
const milestoneLoading = ref(false)
const milestones = ref<Milestone[]>([])

async function loadMilestones() {
  milestoneLoading.value = true
  try {
    milestones.value = (await listMilestones(projectId.value)) ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    milestoneLoading.value = false
  }
}

// 判断里程碑是否已完成
function isMilestoneDone(m: Milestone): boolean {
  if (m.status) {
    const s = m.status.toUpperCase()
    if (s === 'COMPLETED' || s === 'DONE' || s === 'FINISHED') return true
    if (s === 'PENDING' || s === 'IN_PROGRESS' || s === 'TODO') return false
  }
  return !!m.actualDate
}

function getMilestoneStatusMeta(m: Milestone): { label: string; tagType: any } {
  if (isMilestoneDone(m)) return { label: '已完成', tagType: 'success' }
  if (m.status) {
    const s = m.status.toUpperCase()
    if (s === 'IN_PROGRESS') return { label: '进行中', tagType: 'warning' }
    if (s === 'PENDING' || s === 'TODO') return { label: '待开始', tagType: 'info' }
    return { label: m.status, tagType: 'info' }
  }
  return { label: '待开始', tagType: 'info' }
}

// 是否全部里程碑完成（用于终验申请前置条件）
const allMilestonesDone = computed(() => {
  if (milestones.value.length === 0) return false
  return milestones.value.every(isMilestoneDone)
})

// 里程碑新增/编辑弹窗
const milestoneDialogVisible = ref(false)
const milestoneDialogTitle = ref('')
const milestoneSubmitting = ref(false)
const milestoneFormRef = ref<FormInstance>()

interface MilestoneForm {
  id?: number
  projectId?: number
  name: string
  type?: string
  plannedDate?: string
  description?: string
}

function createEmptyMilestoneForm(): MilestoneForm {
  return {
    id: undefined,
    projectId: projectId.value,
    name: '',
    type: 'OTHER',
    plannedDate: '',
    description: ''
  }
}

const milestoneForm = reactive<MilestoneForm>(createEmptyMilestoneForm())

const milestoneRules: FormRules = {
  name: [{ required: true, message: '请输入里程碑名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择里程碑类型', trigger: 'change' }],
  plannedDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }]
}

function handleAddMilestone() {
  milestoneDialogTitle.value = '新增里程碑'
  Object.assign(milestoneForm, createEmptyMilestoneForm())
  milestoneDialogVisible.value = true
}

function handleEditMilestone(row: Milestone) {
  milestoneDialogTitle.value = '编辑里程碑'
  Object.assign(milestoneForm, createEmptyMilestoneForm(), row)
  milestoneDialogVisible.value = true
}

async function handleSubmitMilestone() {
  if (!milestoneFormRef.value) return
  await milestoneFormRef.value.validate(async (valid) => {
    if (!valid) return
    milestoneSubmitting.value = true
    try {
      const payload = { ...milestoneForm, projectId: projectId.value }
      if (milestoneForm.id) {
        await updateMilestone(payload)
        ElMessage.success('更新成功')
      } else {
        await createMilestone(payload)
        ElMessage.success('新增成功')
      }
      milestoneDialogVisible.value = false
      loadMilestones()
    } catch {
      /* handled by interceptor */
    } finally {
      milestoneSubmitting.value = false
    }
  })
}

function handleDeleteMilestone(row: Milestone) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除里程碑「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteMilestone(row.id!)
      ElMessage.success('删除成功')
      loadMilestones()
    })
    .catch(() => {
      /* cancelled */
    })
}

// 里程碑进度更新弹窗
const progressDialogVisible = ref(false)
const progressSubmitting = ref(false)
const progressFormRef = ref<FormInstance>()
const progressForm = reactive<{ milestoneId?: number; milestoneName: string; actualDate: string; description: string }>({
  milestoneId: undefined,
  milestoneName: '',
  actualDate: '',
  description: ''
})

const progressRules: FormRules = {
  actualDate: [{ required: true, message: '请选择实际完成日期', trigger: 'change' }]
}

function handleUpdateProgress(row: Milestone) {
  progressForm.milestoneId = row.id
  progressForm.milestoneName = row.name
  progressForm.actualDate = row.actualDate ?? ''
  progressForm.description = row.description ?? ''
  progressDialogVisible.value = true
}

async function handleSubmitProgress() {
  if (!progressFormRef.value || !progressForm.milestoneId) return
  const milestoneId = progressForm.milestoneId
  await progressFormRef.value.validate(async (valid) => {
    if (!valid) return
    progressSubmitting.value = true
    try {
      await updateMilestoneProgress(milestoneId, {
        actualDate: progressForm.actualDate,
        description: progressForm.description
      })
      ElMessage.success('进度更新成功')
      progressDialogVisible.value = false
      loadMilestones()
    } catch {
      /* handled by interceptor */
    } finally {
      progressSubmitting.value = false
    }
  })
}

// ============== 项目成员（占位） ==============
const members = ref<any[]>([])

// ============== 终验交付 ==============
const acceptanceLoading = ref(false)
const acceptance = ref<FinalAcceptance | null>(null)

async function loadAcceptance() {
  acceptanceLoading.value = true
  try {
    acceptance.value = await getAcceptanceByProject(projectId.value)
  } catch {
    acceptance.value = null
  } finally {
    acceptanceLoading.value = false
  }
}

const acceptanceStatusMeta = computed(() => {
  const s = acceptance.value?.status?.toUpperCase()
  if (s === 'APPROVED') return { label: '已通过', tagType: 'success' }
  if (s === 'REJECTED') return { label: '已驳回', tagType: 'danger' }
  if (s === 'PENDING') return { label: '待审批', tagType: 'warning' }
  return { label: s ?? '未知', tagType: 'info' }
})

// 申请终验弹窗
const applyDialogVisible = ref(false)
const applySubmitting = ref(false)
const applyFormRef = ref<FormInstance>()
const applyForm = reactive<{ report: string }>({ report: '' })

const applyRules: FormRules = {
  report: [{ required: true, message: '请填写终验报告', trigger: 'blur' }]
}

function openApplyDialog() {
  applyForm.report = ''
  applyDialogVisible.value = true
}

async function handleSubmitApply() {
  if (!applyFormRef.value) return
  await applyFormRef.value.validate(async (valid) => {
    if (!valid) return
    applySubmitting.value = true
    try {
      await applyAcceptance({ projectId: projectId.value, report: applyForm.report })
      ElMessage.success('终验申请已提交')
      applyDialogVisible.value = false
      loadAcceptance()
    } catch {
      /* handled by interceptor */
    } finally {
      applySubmitting.value = false
    }
  })
}

// 审批意见弹窗
const opinionDialogVisible = ref(false)
const opinionDialogTitle = ref('')
const opinionSubmitting = ref(false)
const opinionFormRef = ref<FormInstance>()
const opinionForm = reactive<{ acceptanceId?: number; opinion: string }>({
  acceptanceId: undefined,
  opinion: ''
})

const opinionRules: FormRules = {
  opinion: [{ required: true, message: '请填写审批意见', trigger: 'blur' }]
}

function openApproveDialog() {
  if (!acceptance.value?.id) return
  opinionDialogTitle.value = '通过终验'
  opinionForm.acceptanceId = acceptance.value.id
  opinionForm.opinion = ''
  opinionDialogVisible.value = true
}

function openRejectDialog() {
  if (!acceptance.value?.id) return
  opinionDialogTitle.value = '驳回终验'
  opinionForm.acceptanceId = acceptance.value.id
  opinionForm.opinion = ''
  opinionDialogVisible.value = true
}

async function handleSubmitOpinion(approve: boolean) {
  if (!opinionFormRef.value || !opinionForm.acceptanceId) return
  const acceptanceId = opinionForm.acceptanceId
  await opinionFormRef.value.validate(async (valid) => {
    if (!valid) return
    opinionSubmitting.value = true
    try {
      if (approve) {
        await approveAcceptance(acceptanceId, { opinion: opinionForm.opinion })
        ElMessage.success('已通过终验')
      } else {
        await rejectAcceptance(acceptanceId, { opinion: opinionForm.opinion })
        ElMessage.success('已驳回终验')
      }
      opinionDialogVisible.value = false
      loadAcceptance()
    } catch {
      /* handled by interceptor */
    } finally {
      opinionSubmitting.value = false
    }
  })
}

onMounted(async () => {
  await loadProject()
  loadMilestones()
  loadAcceptance()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header :icon="null" @back="goBack">
      <template #content>
        <div class="header-content">
          <span class="header-title">{{ project?.name ?? '项目详情' }}</span>
          <el-tag
            v-if="project?.code"
            class="header-code"
            type="info"
            effect="plain"
            size="small"
          >
            {{ project.code }}
          </el-tag>
          <el-tag
            v-if="project?.status"
            :type="getStatusMeta(project.status).tagType"
            class="header-status"
          >
            {{ getStatusMeta(project.status).label }}
          </el-tag>
        </div>
      </template>
    </el-page-header>

    <el-card shadow="never" class="progress-card">
      <div class="progress-row">
        <span class="progress-label">项目进度</span>
        <el-progress
          :percentage="Number(project?.progress ?? 0)"
          :stroke-width="14"
          :status="(Number(project?.progress ?? 0) >= 100 ? 'success' : '') as any"
          style="flex: 1"
        />
      </div>
    </el-card>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions v-if="project" :column="3" border>
            <el-descriptions-item label="项目编号">{{ project.code || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目名称">{{ project.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目类型">{{ getTypeLabel(project.type) }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ project.customerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户联系人">{{ project.customerContact || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户电话">{{ project.customerPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="合同编号">{{ project.contractNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="合同金额">
              {{ project.contractAmount != null ? `￥${Number(project.contractAmount).toFixed(2)}` : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="优先级">{{ getPriorityLabel(project.priority) }}</el-descriptions-item>
            <el-descriptions-item label="项目经理">{{ project.managerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="计划开始日期">{{ formatDate(project.planStartDate) }}</el-descriptions-item>
            <el-descriptions-item label="计划结束日期">{{ formatDate(project.planEndDate) }}</el-descriptions-item>
            <el-descriptions-item label="项目状态">
              <el-tag :type="getStatusMeta(project.status).tagType">
                {{ getStatusMeta(project.status).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(project.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="项目描述" :span="3">
              {{ project.description || '-' }}
            </el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="暂无项目信息" />
        </el-tab-pane>

        <!-- 里程碑管理 -->
        <el-tab-pane label="里程碑管理" name="milestone">
          <div class="toolbar">
            <el-button type="primary" :icon="'Plus'" @click="handleAddMilestone">新增里程碑</el-button>
            <span v-if="milestones.length > 0" class="milestone-tip">
              已完成 {{ milestones.filter(isMilestoneDone).length }} / {{ milestones.length }}
            </span>
          </div>
          <el-table v-loading="milestoneLoading" :data="milestones" border stripe>
            <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
            <el-table-column label="类型" width="120" align="center">
              <template #default="{ row }">{{ getMilestoneTypeLabel(row.type) }}</template>
            </el-table-column>
            <el-table-column label="计划日期" width="120" align="center">
              <template #default="{ row }">{{ formatDate(row.plannedDate) }}</template>
            </el-table-column>
            <el-table-column label="实际日期" width="120" align="center">
              <template #default="{ row }">{{ formatDate(row.actualDate) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="110" align="center">
              <template #default="{ row }">
                <el-tag :type="getMilestoneStatusMeta(row).tagType">
                  {{ getMilestoneStatusMeta(row).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleUpdateProgress(row)">更新进度</el-button>
                <el-button link type="primary" @click="handleEditMilestone(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteMilestone(row)">删除</el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无里程碑数据" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- 项目成员 -->
        <el-tab-pane label="项目成员" name="member">
          <el-table :data="members" border stripe>
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="姓名" min-width="120" />
            <el-table-column prop="role" label="角色" min-width="120" />
            <el-table-column prop="phone" label="电话" min-width="140" />
            <el-table-column prop="email" label="邮箱" min-width="180" />
            <template #empty>
              <el-empty description="暂无成员数据" />
            </template>
          </el-table>
        </el-tab-pane>

        <!-- 终验交付 -->
        <el-tab-pane label="终验交付" name="acceptance">
          <div v-loading="acceptanceLoading" class="acceptance-wrap">
            <!-- 无终验记录 -->
            <template v-if="!acceptance">
              <div class="acceptance-empty">
                <el-empty description="暂无终验申请记录">
                  <el-button
                    v-if="allMilestonesDone"
                    type="primary"
                    :icon="'Check'"
                    @click="openApplyDialog"
                  >
                    申请终验
                  </el-button>
                  <div v-else class="acceptance-tip">
                    <el-icon><WarningFilled /></el-icon>
                    需完成全部里程碑后才可申请终验
                  </div>
                </el-empty>
              </div>
            </template>

            <!-- 已有终验记录 -->
            <template v-else>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="终验状态">
                  <el-tag :type="acceptanceStatusMeta.tagType">
                    {{ acceptanceStatusMeta.label }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="申请人">{{ acceptance.applicantName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="申请日期">{{ formatDate(acceptance.applyDate) }}</el-descriptions-item>
                <el-descriptions-item label="审批日期">{{ formatDate(acceptance.acceptDate) }}</el-descriptions-item>
                <el-descriptions-item label="终验报告" :span="2">
                  <pre class="report-text">{{ acceptance.report || '-' }}</pre>
                </el-descriptions-item>
                <el-descriptions-item label="审批意见" :span="2">
                  {{ acceptance.opinion || '-' }}
                </el-descriptions-item>
              </el-descriptions>

              <div v-if="acceptance.status?.toUpperCase() === 'PENDING'" class="acceptance-actions">
                <el-button type="success" :icon="'Check'" @click="openApproveDialog">通过终验</el-button>
                <el-button type="danger" :icon="'Close'" @click="openRejectDialog">驳回终验</el-button>
              </div>
            </template>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增 / 编辑里程碑弹窗 -->
    <el-dialog
      v-model="milestoneDialogVisible"
      :title="milestoneDialogTitle"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="milestoneFormRef"
        :model="milestoneForm"
        :rules="milestoneRules"
        label-width="90px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="milestoneForm.name" placeholder="请输入里程碑名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="milestoneForm.type" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="opt in milestoneTypeOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期" prop="plannedDate">
          <el-date-picker
            v-model="milestoneForm.plannedDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择计划日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="milestoneForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入里程碑描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="milestoneDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="milestoneSubmitting" @click="handleSubmitMilestone">确定</el-button>
      </template>
    </el-dialog>

    <!-- 里程碑进度更新弹窗 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="更新里程碑进度"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="progressFormRef"
        :model="progressForm"
        :rules="progressRules"
        label-width="100px"
      >
        <el-form-item label="里程碑">
          <span>{{ progressForm.milestoneName }}</span>
        </el-form-item>
        <el-form-item label="实际日期" prop="actualDate">
          <el-date-picker
            v-model="progressForm.actualDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择实际完成日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="完成情况" prop="description">
          <el-input
            v-model="progressForm.description"
            type="textarea"
            :rows="3"
            placeholder="请描述完成情况"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="progressSubmitting" @click="handleSubmitProgress">确定</el-button>
      </template>
    </el-dialog>

    <!-- 申请终验弹窗 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="申请终验"
      width="560px"
      destroy-on-close
    >
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="applyRules"
        label-width="90px"
      >
        <el-form-item label="终验报告" prop="report">
          <el-input
            v-model="applyForm.report"
            type="textarea"
            :rows="6"
            placeholder="请填写终验报告内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applySubmitting" @click="handleSubmitApply">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 审批意见弹窗 -->
    <el-dialog
      v-model="opinionDialogVisible"
      :title="opinionDialogTitle"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="opinionFormRef"
        :model="opinionForm"
        :rules="opinionRules"
        label-width="90px"
      >
        <el-form-item label="审批意见" prop="opinion">
          <el-input
            v-model="opinionForm.opinion"
            type="textarea"
            :rows="4"
            placeholder="请填写审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="opinionDialogVisible = false">取消</el-button>
        <el-button
          :type="opinionDialogTitle === '通过终验' ? 'success' : 'danger'"
          :loading="opinionSubmitting"
          @click="handleSubmitOpinion(opinionDialogTitle === '通过终验')"
        >
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
.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.header-code {
  margin: 0;
}
.header-status {
  margin: 0;
}
.progress-card .progress-row {
  display: flex;
  align-items: center;
  gap: 16px;
}
.progress-label {
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}
.milestone-tip {
  color: #909399;
  font-size: 13px;
}
.acceptance-wrap {
  min-height: 200px;
}
.acceptance-empty {
  padding: 24px 0;
}
.acceptance-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #e6a23c;
  font-size: 13px;
  margin-top: 12px;
  justify-content: center;
}
.acceptance-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}
.report-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
}
</style>
