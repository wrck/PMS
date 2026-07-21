<script setup lang="ts">
// =============================================================================
// ProjectMemberList - 项目成员管理（工作区「成员」Tab 子组件）
// -----------------------------------------------------------------------------
// 功能：
//   - 列表展示：成员姓名 / 角色 / 加入日期 / 离开日期 / 操作
//   - 新增/编辑成员：用户选择查找（UserSelect）+ 角色 + 加入日期
//   - 删除成员（带二次确认）
//   - 角色筛选
//   - 嵌入工作区时通过 props.projectId 自动加载；独立路由进入时支持手动输入
// =============================================================================
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Refresh, Search, Delete, Edit } from '@element-plus/icons-vue'
import {
  createMember,
  deleteMember,
  listMembersByProjectId,
  updateMember,
  type ProjectMember
} from '@/api/project-member'
import type { MentionUser } from '@/api/system'
import UserSelect from '@/components/common/UserSelect.vue'
import type { EpTagType } from '@/types'

const props = defineProps<{ projectId?: number }>()

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<ProjectMember[]>([])

// 独立路由进入时的手动输入
const localProjectId = ref<number | undefined>(undefined)
const effectiveProjectId = computed(() => props.projectId ?? localProjectId.value)
const embedded = computed(() => typeof props.projectId === 'number')

// 角色筛选
const filterRole = ref<string>('')
const filteredData = computed(() =>
  filterRole.value ? tableData.value.filter((m) => m.role === filterRole.value) : tableData.value
)

// 角色元信息
const ROLE_META: Record<
  ProjectMember['role'],
  { label: string; tagType: EpTagType; desc: string }
> = {
  PROJECT_MANAGER: { label: '项目经理', tagType: 'primary', desc: '负责项目整体推进与决策' },
  PROJECT_MEMBER: { label: '项目成员', tagType: 'success', desc: '参与项目任务执行' },
  APPROVER: { label: '审批人', tagType: 'warning', desc: '负责审批流程签核' },
  VIEWER: { label: '观察者', tagType: 'info', desc: '只读查看项目信息' },
  CUSTOMER: { label: '客户代表', tagType: 'danger', desc: '客户侧接口人' }
}

const roleOptions = Object.entries(ROLE_META).map(([value, meta]) => ({
  value,
  label: meta.label
}))

function roleLabel(role?: string): string {
  return role ? (ROLE_META[role as ProjectMember['role']]?.label ?? role) : '-'
}
function roleTagType(role?: string): EpTagType {
  return role ? (ROLE_META[role as ProjectMember['role']]?.tagType ?? 'info') : 'info'
}

function formatDate(s?: string): string {
  if (!s) return '-'
  return s.length > 10 ? s.slice(0, 10) : s
}

// ============ 弹窗 ============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = ref<ProjectMember>({
  projectId: 0,
  userId: 0,
  role: 'PROJECT_MEMBER',
  joinDate: ''
})

const rules: FormRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function resetForm() {
  form.value = {
    projectId: effectiveProjectId.value ?? 0,
    userId: 0,
    role: 'PROJECT_MEMBER',
    joinDate: new Date().toISOString().slice(0, 10)
  }
}

function onUserChange(user: MentionUser | null) {
  if (user) {
    form.value.userId = user.id
    form.value.userName = user.realName || user.username
  } else {
    form.value.userId = 0
    form.value.userName = ''
  }
}

function handleAdd() {
  if (!effectiveProjectId.value) {
    ElMessage.warning('请先指定项目')
    return
  }
  resetForm()
  dialogTitle.value = '新增成员'
  dialogVisible.value = true
}

function handleEdit(row: ProjectMember) {
  form.value = { ...row }
  dialogTitle.value = '编辑成员'
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: ProjectMember = {
        ...form.value,
        projectId: effectiveProjectId.value!
      }
      if (form.value.id) {
        await updateMember(payload)
        ElMessage.success('更新成功')
      } else {
        await createMember(payload)
        ElMessage.success('新增成功')
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

function handleDelete(row: ProjectMember) {
  if (!row.id) return
  ElMessageBox.confirm(
    `确定从项目中移除成员「${row.userName ?? row.userId}」吗？`,
    '移除成员',
    { type: 'warning', confirmButtonText: '移除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteMember(row.id!)
      ElMessage.success('已移除')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

// ============ 加载 ============
async function loadData() {
  if (!effectiveProjectId.value) {
    ElMessage.warning('请输入项目 ID')
    return
  }
  loading.value = true
  try {
    tableData.value = (await listMembersByProjectId(effectiveProjectId.value)) ?? []
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

watch(
  () => props.projectId,
  (val) => {
    if (typeof val === 'number') loadData()
  }
)

onMounted(() => {
  if (embedded.value) loadData()
})
</script>

<template>
  <div class="project-member-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">项目成员</span>
          <span v-if="embedded" class="header-project-id">项目 ID：{{ effectiveProjectId }}</span>
        </div>
      </template>

      <!-- 工具栏 -->
      <div class="toolbar">
        <template v-if="!embedded">
          <el-input-number
            v-model="localProjectId"
            :min="1"
            :controls="false"
            placeholder="项目 ID"
            style="width: 140px"
          />
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
        </template>
        <el-select
          v-model="filterRole"
          placeholder="全部角色"
          clearable
          style="width: 160px"
        >
          <el-option
            v-for="opt in roleOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <div class="spacer" />
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增成员</el-button>
      </div>

      <el-table v-loading="loading" :data="filteredData" border stripe>
        <el-table-column label="成员" min-width="160">
          <template #default="{ row }">
            <div class="member-cell">
              <el-avatar :size="28" class="member-avatar">
                {{ (row.userName || row.userId || '?').toString().charAt(0) }}
              </el-avatar>
              <span class="member-name">{{ row.userName ?? `#${row.userId}` }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户 ID" width="100" align="center">
          <template #default="{ row }">{{ row.userId }}</template>
        </el-table-column>
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="加入日期" width="130" align="center">
          <template #default="{ row }">{{ formatDate(row.joinDate) }}</template>
        </el-table-column>
        <el-table-column label="离开日期" width="130" align="center">
          <template #default="{ row }">{{ formatDate(row.leaveDate) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">移除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty :description="embedded ? '该项目暂无成员，点击「新增成员」添加' : '请输入项目 ID 并查询'" />
        </template>
      </el-table>
    </el-card>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户" prop="userId">
          <UserSelect
            :model-value="form.userId"
            placeholder="请搜索选择用户"
            @change="onUserChange"
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="opt in roleOptions"
              :key="opt.value"
              :label="`${opt.label} · ${ROLE_META[opt.value as ProjectMember['role']].desc}`"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="加入日期">
          <el-date-picker
            v-model="form.joinDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择加入日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="离开日期">
          <el-date-picker
            v-model="form.leaveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="留空表示当前在项目中"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.project-member-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.header-project-id {
  font-size: 13px;
  color: var(--pms-color-text-secondary, #909399);
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.toolbar .spacer {
  flex: 1;
}
.member-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.member-avatar {
  background-color: var(--pms-color-primary, #409eff);
  color: #fff;
  font-size: 13px;
  flex-shrink: 0;
}
.member-name {
  font-weight: 500;
  color: var(--pms-color-text-primary, #303133);
}
</style>
