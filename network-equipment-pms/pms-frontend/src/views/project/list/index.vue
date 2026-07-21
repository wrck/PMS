<script setup lang="ts">
// =============================================================================
// ProjectList - 项目列表页（卡片化 + 空状态 + 骨架屏）
// -----------------------------------------------------------------------------
// Task 4 重做：
// - 顶部 PageHeader + 副标题（项目总数 / 进行中数）+ 操作（新建 / 从模板创建）
// - 筛选区：搜索（项目名 / 编号）+ 状态 + 类型 + 客户
// - 视图切换：卡片视图（默认）/ 列表视图
// - 卡片视图：CSS Grid（auto-fill, minmax(360px, 1fr)），每张卡片显示
//   编号 + 状态 / 名称 / 客户 + 经理 / 进度环 / 起止日期 / 操作菜单
// - 列表视图：el-table 紧凑模式，max-height: calc(100vh - 320px)
// - 加载中：按 page-size 数量渲染 SkeletonCard 骨架卡片
// - 空状态：EmptyState + CTA
// - 分页 12/24/48（默认 12）
// =============================================================================
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules
} from 'element-plus'
import {
  ArrowDown,
  Delete,
  Edit,
  Files,
  Plus,
  Refresh,
  Search,
  View
} from '@element-plus/icons-vue'
import {
  approveProject,
  closeProject,
  createProject,
  deleteProject,
  listProjects,
  updateProject,
  type Project,
  type ProjectStatus,
  type ProjectType
} from '@/api/project'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ProjectStatusTag from '@/components/common/ProjectStatusTag.vue'
import UserSelect from '@/components/common/UserSelect.vue'
import ProjectTemplateSelector from '@/components/ProjectTemplateSelector.vue'
import type { EpTagType } from '@/types'

defineOptions({ name: 'ProjectList' })

const router = useRouter()
const templateSelectorRef = ref<InstanceType<typeof ProjectTemplateSelector> | null>(null)

// ============== 数据状态 ==============
const loading = ref(false)
const submitting = ref(false)
const closing = ref<number | null>(null)
const tableData = ref<Project[]>([])
const total = ref(0)
// 视图模式默认列表，并持久化到 localStorage（下次进入时恢复用户上次选择）
const VIEW_MODE_STORAGE_KEY = 'pms:project-list:view-mode'
const viewMode = ref<'card' | 'table'>(
  ((): 'card' | 'table' => {
    try {
      const saved = localStorage.getItem(VIEW_MODE_STORAGE_KEY)
      if (saved === 'card' || saved === 'table') return saved
    } catch {
      /* localStorage 不可用时降级到默认值 */
    }
    return 'table'
  })()
)
watch(viewMode, (val) => {
  try {
    localStorage.setItem(VIEW_MODE_STORAGE_KEY, val)
  } catch {
    /* 忽略写入失败 */
  }
})

const query = reactive<{
  page: number
  size: number
  projectName?: string
  status?: ProjectStatus | ''
  type?: ProjectType | ''
  customerName?: string
}>({
  page: 1,
  size: 12,
  projectName: '',
  status: '',
  type: '',
  customerName: ''
})

// ============== 状态 / 类型 / 优先级选项 ==============
const statusOptions: { value: ProjectStatus; label: string; tagType: EpTagType }[] = [
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
  return (
    statusOptions.find((s) => s.value === status) ?? {
      label: status ?? '-',
      tagType: 'info' as EpTagType
    }
  )
}

function getTypeLabel(type?: string) {
  return typeOptions.find((t) => t.value === type)?.label ?? type ?? '-'
}

function formatDate(date?: string) {
  if (!date) return '-'
  return date.length > 10 ? date.substring(0, 10) : date
}

function progressStatus(progress: number | string | undefined): '' | 'success' {
  return Number(progress ?? 0) >= 100 ? 'success' : ''
}

// 是否可关闭（已关闭 / 已驳回 / 已取消的不可关闭）
function canClose(p: Project): boolean {
  return !!p.id && p.status !== 'CLOSED' && p.status !== 'REJECTED'
}

// 副标题：项目总数 / 进行中数
const inProgressCount = computed(
  () =>
    tableData.value.filter(
      (p) => p.status === 'IN_PROGRESS' || p.status === 'APPROVED'
    ).length
)

const subtitle = computed(
  () => `共 ${total.value} 个项目 · 进行中 ${inProgressCount.value} 个`
)

// 骨架卡片数量（对齐当前分页大小）
const skeletonCount = computed(() => query.size)

// ============== 弹窗表单 ==============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

interface ProjectForm {
  id?: number
  projectName: string
  projectType?: ProjectType
  customerName?: string
  customerContact?: string
  customerPhone?: string
  contractNo?: string
  contractAmount?: number
  planStartDate?: string
  planEndDate?: string
  projectManagerId?: number
  projectManagerName?: string
  priority?: number
  description?: string
}

function createEmptyForm(): ProjectForm {
  return {
    id: undefined,
    projectName: '',
    projectType: 'NETWORK_DEVICE',
    customerName: '',
    customerContact: '',
    customerPhone: '',
    contractNo: '',
    contractAmount: undefined,
    planStartDate: '',
    planEndDate: '',
    projectManagerId: undefined,
    projectManagerName: '',
    priority: 2,
    description: ''
  }
}

// 项目经理选择回调：同步 ID + 名称
function onManagerChange(user: { id: number; username: string; realName?: string } | null) {
  if (user) {
    form.projectManagerId = user.id
    form.projectManagerName = user.realName || user.username
  } else {
    form.projectManagerId = undefined
    form.projectManagerName = ''
  }
}

const form = reactive<ProjectForm>(createEmptyForm())
const dateRange = ref<[string, string] | null>(null)

const rules: FormRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }]
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
    // type / customerName 当前后端 list 接口未支持，前端过滤
    const res = await listProjects(params)
    let records = res.records ?? []
    if (query.type) {
      records = records.filter((r) => r.projectType === query.type)
    }
    if (query.customerName) {
      records = records.filter((r) =>
        r.customerName?.toLowerCase().includes(query.customerName!.toLowerCase())
      )
    }
    tableData.value = records
    total.value = res.total ?? records.length
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
  query.type = ''
  query.customerName = ''
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

// ============== 审批 / 删除 / 关闭 ==============
function handleApprove(row: Project) {
  if (!row.id) return
  ElMessageBox.confirm(`确认通过项目「${row.projectName}」的立项审批吗？`, '立项审批', {
    type: 'warning'
  })
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
  ElMessageBox.confirm(`确定删除项目「${row.projectName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteProject(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleClose(row: Project) {
  if (!row.id) return
  ElMessageBox.confirm(
    `确定关闭项目「${row.projectName}」吗？关闭后项目将不可继续执行。`,
    '关闭项目',
    { type: 'warning', confirmButtonText: '确定关闭', cancelButtonText: '取消' }
  )
    .then(async () => {
      closing.value = row.id ?? null
      await closeProject(row.id!)
      ElMessage.success('项目已关闭')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
    .finally(() => {
      closing.value = null
    })
}

// ============== 卡片操作菜单 ==============
function handleCardCommand(cmd: string, project: Project) {
  switch (cmd) {
    case 'workspace':
      goWorkspace(project)
      break
    case 'edit':
      handleEdit(project)
      break
    case 'approve':
      handleApprove(project)
      break
    case 'close':
      handleClose(project)
      break
    case 'delete':
      handleDelete(project)
      break
    default:
      break
  }
}

// ============== 路由跳转 ==============
function goWorkspace(row: Project) {
  if (!row.id) return
  router.push(`/project/workspace/${row.id}`)
}

function handleCreateFromTemplate() {
  templateSelectorRef.value?.open()
}

function onTemplateCreateSuccess(projectId: number) {
  router.push(`/project/workspace/${projectId}`)
}

onMounted(loadData)
</script>

<template>
  <div class="project-list-page">
    <PageHeader title="项目列表" :description="subtitle">
      <template #actions>
        <el-button :icon="Plus" type="primary" @click="handleAdd">新建项目</el-button>
        <el-button :icon="Files" type="success" @click="handleCreateFromTemplate">
          从模板创建
        </el-button>
      </template>
    </PageHeader>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-input
        v-model="query.projectName"
        placeholder="搜索项目名 / 编号"
        :prefix-icon="Search"
        clearable
        class="filter-search"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="query.status"
        placeholder="全部状态"
        clearable
        class="filter-status"
      >
        <el-option
          v-for="opt in statusOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-select
        v-model="query.type"
        placeholder="全部类型"
        clearable
        class="filter-type"
      >
        <el-option
          v-for="opt in typeOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-input
        v-model="query.customerName"
        placeholder="客户名称"
        clearable
        class="filter-customer"
        @keyup.enter="handleSearch"
      />
      <el-button :icon="Search" type="primary" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      <div class="flex-grow" />
      <el-radio-group v-model="viewMode" size="default">
        <el-radio-button value="card">卡片</el-radio-button>
        <el-radio-button value="table">列表</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" class="card-view">
      <!-- 骨架屏 -->
      <div v-if="loading" class="project-grid">
        <SkeletonCard
          v-for="n in skeletonCount"
          :key="`sk-${n}`"
          :loading="true"
          :rows="5"
          class="skeleton-item"
        />
      </div>

      <!-- 空状态 -->
      <div v-else-if="tableData.length === 0" class="empty-wrapper">
        <EmptyState
          title="暂无项目"
          description="点击右上角按钮创建第一个项目，或从模板快速创建"
          icon="Folder"
        >
          <template #action>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新建项目</el-button>
            <el-button :icon="Files" @click="handleCreateFromTemplate">从模板创建</el-button>
          </template>
        </EmptyState>
      </div>

      <!-- 卡片网格 -->
      <div v-else class="project-grid">
        <el-card
          v-for="project in tableData"
          :key="project.id"
          shadow="hover"
          class="project-card"
          @click="goWorkspace(project)"
        >
          <div class="card-header">
            <span class="card-code">{{ project.projectCode || '-' }}</span>
            <ProjectStatusTag
              v-if="project.status"
              :status="project.status"
              size="small"
            />
          </div>

          <div class="card-title" :title="project.projectName">{{ project.projectName }}</div>

          <div class="card-meta">
            <div class="meta-row">
              <span class="meta-label">客户</span>
              <span class="meta-value">{{ project.customerName || '-' }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">经理</span>
              <span class="meta-value">{{ project.projectManagerName || '-' }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">周期</span>
              <span class="meta-value">
                {{ formatDate(project.planStartDate) }} ~ {{ formatDate(project.planEndDate) }}
              </span>
            </div>
          </div>

          <div class="card-progress">
            <el-progress
              :percentage="Number(project.progress ?? 0)"
              :stroke-width="6"
              :status="progressStatus(project.progress)"
            />
          </div>

          <div class="card-footer" @click.stop>
            <span class="footer-text">{{ getTypeLabel(project.projectType) }}</span>
            <el-dropdown
              trigger="click"
              @command="(cmd: string) => handleCardCommand(cmd, project)"
            >
              <el-button text size="small">
                操作
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="workspace">
                    <el-icon><View /></el-icon>进入工作区
                  </el-dropdown-item>
                  <el-dropdown-item command="edit">
                    <el-icon><Edit /></el-icon>编辑
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="project.status === 'PENDING'"
                    command="approve"
                  >
                    立项审批
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="canClose(project)"
                    command="close"
                    :disabled="closing === project.id"
                  >
                    {{ closing === project.id ? '关闭中...' : '关闭项目' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>
                    <span class="danger-text">删除</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-else class="table-view">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        max-height="calc(100vh - 320px)"
      >
        <el-table-column prop="projectCode" label="项目编号" min-width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="项目类型" width="120">
          <template #default="{ row }">
            {{ getTypeLabel(row.projectType) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="customerName"
          label="客户名称"
          min-width="150"
          show-overflow-tooltip
        />
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
              :status="progressStatus(row.progress)"
            />
          </template>
        </el-table-column>
        <el-table-column label="计划开始" width="110" align="center">
          <template #default="{ row }">{{ formatDate(row.planStartDate) }}</template>
        </el-table-column>
        <el-table-column label="计划结束" width="110" align="center">
          <template #default="{ row }">{{ formatDate(row.planEndDate) }}</template>
        </el-table-column>
        <el-table-column prop="projectManagerName" label="项目经理" width="110" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goWorkspace(row)">工作区</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              link
              type="warning"
              @click="handleApprove(row)"
            >
              审批
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <EmptyState
            title="暂无项目"
            description="点击右上角按钮创建第一个项目"
            icon="Folder"
          />
        </template>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 模板选择器 -->
    <ProjectTemplateSelector ref="templateSelectorRef" @success="onTemplateCreateSuccess" />

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
        class="responsive-form"
      >
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="项目类型" prop="projectType">
              <el-select v-model="form.projectType" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="opt in typeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户名称">
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户联系人">
              <el-input v-model="form.customerContact" placeholder="请输入客户联系人" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户电话">
              <el-input v-model="form.customerPhone" placeholder="请输入客户电话" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="合同编号">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
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
          <el-col :xs="24" :sm="12">
            <el-form-item label="项目经理">
              <UserSelect
                v-model="form.projectManagerId"
                placeholder="请搜索选择项目经理"
                @change="onManagerChange"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
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

<style scoped lang="scss">
@use '../../../styles/design-tokens' as *;

.project-list-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
  padding: $spacing-4 $spacing-6;
}

// ====== 筛选区 ======
.filter-bar {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  flex-wrap: wrap;
  padding: $spacing-3;
  background: $color-bg-card;
  border-radius: $radius-lg;
  box-shadow: $shadow-card;

  .filter-search { width: 240px; }
  .filter-status { width: 140px; }
  .filter-type { width: 140px; }
  .filter-customer { width: 180px; }
  .flex-grow { flex: 1 1 auto; }
}

// ====== 卡片视图 ======
.card-view {
  min-height: 240px;
}

.empty-wrapper {
  padding: $spacing-10 0;
  background: $color-bg-card;
  border-radius: $radius-lg;
  box-shadow: $shadow-card;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: $spacing-4;
}

.skeleton-item {
  min-height: 240px;
}

.project-card {
  cursor: pointer;
  transition: transform $transition-fast, box-shadow $transition-fast, border-color $transition-fast;
  border: 1px solid $color-border-light;

  :deep(.el-card__body) {
    padding: $spacing-4;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-card-hover;
    border-color: $color-primary-light-5;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-2;
  }

  .card-code {
    font-size: $font-size-xs;
    color: $color-text-secondary;
    font-family: $font-family-mono;
  }

  .card-title {
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    color: $color-text-primary;
    margin-bottom: $spacing-3;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .card-meta {
    font-size: $font-size-sm;
    color: $color-text-secondary;
    margin-bottom: $spacing-3;
  }

  .meta-row {
    display: flex;
    margin-bottom: $spacing-1;
  }

  .meta-label {
    color: $color-text-placeholder;
    flex-shrink: 0;
    width: 36px;
  }

  .meta-value {
    color: $color-text-regular;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .card-progress {
    margin-bottom: $spacing-3;
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: $spacing-3;
    border-top: 1px solid $color-border-light;
  }

  .footer-text {
    font-size: $font-size-xs;
    color: $color-text-placeholder;
  }

  .danger-text {
    color: $color-danger;
  }
}

// ====== 列表视图 ======
.table-view {
  background: $color-bg-card;
  border-radius: $radius-lg;
  box-shadow: $shadow-card;
  padding: $spacing-3;
}

// ====== 分页 ======
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: $spacing-2 0;
}

// ====== 响应式 ======
@media (max-width: $breakpoint-md - 1px) {
  .project-list-page {
    padding: $spacing-3;
  }

  .filter-bar {
    .filter-search,
    .filter-status,
    .filter-type,
    .filter-customer {
      width: 100%;
    }

    .flex-grow {
      display: none;
    }
  }

  .project-grid {
    grid-template-columns: 1fr;
  }

  .pagination-wrapper {
    :deep(.el-pagination__sizes),
    :deep(.el-pagination__jump) {
      display: none;
    }
  }
}
</style>
