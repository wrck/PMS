<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  approveChangeRequest,
  createChangeRequest,
  listBaselineHistory,
  listChangeRequests,
  rejectChangeRequest,
  type BaselineHistory,
  type ChangeRequest,
  type ChangeRequestListQuery,
  type ChangeRequestPriority,
  type ChangeRequestStatus
} from '@/api/change-request'
import { listProjects, type Project } from '@/api/project'
import type { EpTagType } from '@/types'

defineOptions({ name: 'ChangeRequestManage' })

// ============== 选项配置 ==============
const statusOptions: { value: ChangeRequestStatus; label: string; tagType: EpTagType }[] = [
  { value: 'SUBMITTED', label: '已提交', tagType: 'info' },
  { value: 'UNDER_REVIEW', label: '审核中', tagType: 'warning' },
  { value: 'CCB_APPROVED', label: 'CCB 通过', tagType: 'success' },
  { value: 'CCB_REJECTED', label: 'CCB 驳回', tagType: 'danger' },
  { value: 'IMPLEMENTING', label: '实施中', tagType: 'primary' },
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

// 基线类型映射
const baselineTypeMap: Record<string, string> = {
  SCHEDULE: '进度基线',
  COST: '成本基线',
  SCOPE: '范围基线'
}

function baselineTypeLabel(type?: string) {
  return baselineTypeMap[type ?? ''] ?? type ?? '-'
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
const tableData = ref<ChangeRequest[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; projectId?: number; status?: string }>({
  page: 1,
  size: 10,
  projectId: undefined,
  status: ''
})

async function loadData() {
  loading.value = true
  try {
    const params: ChangeRequestListQuery = { page: query.page, size: query.size }
    if (query.projectId) params.projectId = query.projectId
    if (query.status) params.status = query.status
    const res = await listChangeRequests(params)
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

interface CrForm {
  projectId: number | undefined
  title: string
  description: string
  impactScope: string
  impactSchedule: string
  impactCost: string
  impactQuality: string
  priority: ChangeRequestPriority
}

function createEmptyForm(): CrForm {
  return {
    projectId: undefined,
    title: '',
    description: '',
    impactScope: '',
    impactSchedule: '',
    impactCost: '',
    impactQuality: '',
    priority: 'MEDIUM'
  }
}

const createForm = reactive<CrForm>(createEmptyForm())

const createRules: FormRules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  title: [{ required: true, message: '请输入变更标题', trigger: 'blur' }],
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
      const payload: ChangeRequest = {
        projectId: createForm.projectId!,
        title: createForm.title,
        description: createForm.description,
        impactScope: createForm.impactScope,
        impactSchedule: createForm.impactSchedule,
        impactCost: createForm.impactCost,
        impactQuality: createForm.impactQuality,
        priority: createForm.priority,
        status: 'SUBMITTED'
      }
      await createChangeRequest(payload)
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

// ============== 审批 / 驳回 ==============
const opinionVisible = ref(false)
const opinionTitle = ref('')
const opinionSubmitting = ref(false)
const opinionForm = reactive({ id: 0, opinion: '' })

function handleApprove(row: ChangeRequest) {
  if (!row.id) return
  opinionTitle.value = '审批通过'
  opinionForm.id = row.id
  opinionForm.opinion = ''
  opinionVisible.value = true
}

function handleReject(row: ChangeRequest) {
  if (!row.id) return
  opinionTitle.value = '驳回变更'
  opinionForm.id = row.id
  opinionForm.opinion = ''
  opinionVisible.value = true
}

async function handleOpinionSubmit() {
  if (!opinionForm.opinion.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  opinionSubmitting.value = true
  try {
    if (opinionTitle.value === '审批通过') {
      await approveChangeRequest(opinionForm.id, opinionForm.opinion)
      ElMessage.success('审批通过')
    } else {
      await rejectChangeRequest(opinionForm.id, opinionForm.opinion)
      ElMessage.success('已驳回')
    }
    opinionVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    opinionSubmitting.value = false
  }
}

// ============== 基线历史 ==============
const baselineVisible = ref(false)
const baselineLoading = ref(false)
const baselineList = ref<BaselineHistory[]>([])
const baselineTitle = ref('')

async function handleViewBaseline(row: ChangeRequest) {
  if (!row.id) return
  baselineTitle.value = `基线变更历史 - ${row.crNo ?? ''}`
  baselineVisible.value = true
  baselineLoading.value = true
  try {
    baselineList.value = await listBaselineHistory(row.id)
  } catch {
    baselineList.value = []
  } finally {
    baselineLoading.value = false
  }
}

function baselineTagType(type?: string): EpTagType {
  switch (type) {
    case 'SCHEDULE':
      return 'primary'
    case 'COST':
      return 'warning'
    case 'SCOPE':
      return 'success'
    default:
      return 'info'
  }
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
        <span class="page-title">变更管理</span>
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建变更</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="crNo" label="CR 编号" width="140" />
        <el-table-column prop="title" label="变更标题" min-width="200" show-overflow-tooltip />
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
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).tagType" size="small">
              {{ getStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requesterName" label="申请人" width="110" />
        <el-table-column label="申请日期" width="120" align="center">
          <template #default="{ row }">{{ formatDate(row.requestDate) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'SUBMITTED' || row.status === 'UNDER_REVIEW'"
              link
              type="success"
              @click="handleApprove(row)"
            >
              审批通过
            </el-button>
            <el-button
              v-if="row.status === 'SUBMITTED' || row.status === 'UNDER_REVIEW'"
              link
              type="danger"
              @click="handleReject(row)"
            >
              驳回
            </el-button>
            <el-button link type="primary" @click="handleViewBaseline(row)">基线历史</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无变更请求" />
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

    <!-- 新建变更弹窗 -->
    <el-dialog v-model="createVisible" title="新建变更请求" width="680px" destroy-on-close>
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select
                v-model="createForm.projectId"
                placeholder="请选择项目"
                filterable
                style="width: 100%"
              >
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
          </el-col>
          <el-col :span="24">
            <el-form-item label="变更标题" prop="title">
              <el-input v-model="createForm.title" placeholder="请输入变更标题" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="变更描述">
              <el-input
                v-model="createForm.description"
                type="textarea"
                :rows="2"
                placeholder="请输入变更描述"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响范围">
              <el-input
                v-model="createForm.impactScope"
                type="textarea"
                :rows="2"
                placeholder="请输入影响范围"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响进度">
              <el-input
                v-model="createForm.impactSchedule"
                type="textarea"
                :rows="2"
                placeholder="请输入影响进度"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响成本">
              <el-input
                v-model="createForm.impactCost"
                type="textarea"
                :rows="2"
                placeholder="请输入影响成本"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响质量">
              <el-input
                v-model="createForm.impactQuality"
                type="textarea"
                :rows="2"
                placeholder="请输入影响质量"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleCreateSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审批意见弹窗 -->
    <el-dialog v-model="opinionVisible" :title="opinionTitle" width="480px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="审批意见" required>
          <el-input
            v-model="opinionForm.opinion"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="opinionVisible = false">取消</el-button>
        <el-button type="primary" :loading="opinionSubmitting" @click="handleOpinionSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 基线历史弹窗 -->
    <el-dialog v-model="baselineVisible" :title="baselineTitle" width="640px">
      <div v-loading="baselineLoading">
        <el-timeline v-if="baselineList.length > 0">
          <el-timeline-item
            v-for="item in baselineList"
            :key="item.id"
            :timestamp="item.changedAt"
            placement="top"
          >
            <el-card shadow="never">
              <div class="baseline-header">
                <el-tag :type="baselineTagType(item.baselineType)" size="small">
                  {{ baselineTypeLabel(item.baselineType) }}
                </el-tag>
                <span class="baseline-by">变更人：{{ item.changedBy }}</span>
              </div>
              <div class="baseline-desc">{{ item.changeDescription }}</div>
              <div class="baseline-change">
                <span class="old-value">{{ item.oldValue || '（空）' }}</span>
                <span class="arrow">→</span>
                <span class="new-value">{{ item.newValue || '（空）' }}</span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无基线变更历史" />
      </div>
      <template #footer>
        <el-button @click="baselineVisible = false">关闭</el-button>
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
.baseline-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.baseline-by {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.baseline-desc {
  font-size: 13px;
  margin-bottom: 6px;
  color: var(--el-text-color-primary);
}
.baseline-change {
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.old-value {
  color: var(--el-color-danger);
  text-decoration: line-through;
}
.arrow {
  color: var(--el-text-color-secondary);
}
.new-value {
  color: var(--el-color-success);
  font-weight: 600;
}
</style>
