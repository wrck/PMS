<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteFieldPermission,
  listFieldPermissions,
  saveFieldPermission,
  updateFieldPermission,
  type ApprovalFieldPermission
} from '@/api/approval-field-perm'

const loading = ref(false)
const submitting = ref(false)

// 查询条件
const query = reactive<{ approvalNodeId: number | undefined; entityType: string }>({
  approvalNodeId: undefined,
  entityType: ''
})

const tableData = ref<ApprovalFieldPermission[]>([])

// 编辑对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
type ApprovalFieldPermissionForm = Omit<ApprovalFieldPermission, 'approvalNodeId'> & {
  approvalNodeId?: number
}
const form = reactive<ApprovalFieldPermissionForm>({
  id: undefined,
  approvalNodeId: undefined,
  entityType: '',
  fieldName: '',
  permission: 'VISIBLE',
  maskPattern: undefined,
  customPattern: ''
})

const permissionOptions = [
  { value: 'VISIBLE', label: '可见（原值）' },
  { value: 'MASKED', label: '脱敏（按规则）' },
  { value: 'HIDDEN', label: '隐藏（不返回）' }
]

const maskPatternOptions = [
  { value: 'phone-mask', label: '手机号（138****5678）' },
  { value: 'amount-mask', label: '金额（12***.67）' },
  { value: 'email-mask', label: '邮箱（a***@example.com）' },
  { value: 'custom', label: '自定义正则' }
]

const needMaskPattern = ref(false)
const needCustomPattern = ref(false)

function onPermissionChange() {
  needMaskPattern.value = form.permission === 'MASKED'
  if (!needMaskPattern.value) {
    form.maskPattern = undefined
    form.customPattern = ''
    needCustomPattern.value = false
  }
}

function onMaskPatternChange() {
  needCustomPattern.value = form.maskPattern === 'custom'
  if (!needCustomPattern.value) {
    form.customPattern = ''
  }
}

async function loadData() {
  if (!query.approvalNodeId) {
    ElMessage.warning('请输入审批节点ID')
    return
  }
  loading.value = true
  try {
    tableData.value = await listFieldPermissions(
      query.approvalNodeId,
      query.entityType || undefined
    )
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.approvalNodeId = query.approvalNodeId
  form.entityType = ''
  form.fieldName = ''
  form.permission = 'VISIBLE'
  form.maskPattern = undefined
  form.customPattern = ''
  needMaskPattern.value = false
  needCustomPattern.value = false
}

function openCreate() {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

function openEdit(row: ApprovalFieldPermission) {
  form.id = row.id
  form.approvalNodeId = row.approvalNodeId
  form.entityType = row.entityType
  form.fieldName = row.fieldName
  form.permission = row.permission || 'VISIBLE'
  form.maskPattern = row.maskPattern
  form.customPattern = row.customPattern || ''
  needMaskPattern.value = form.permission === 'MASKED'
  needCustomPattern.value = form.maskPattern === 'custom'
  isEdit.value = true
  dialogVisible.value = true
}

function validateForm(): boolean {
  if (!form.approvalNodeId) {
    ElMessage.warning('审批节点ID不能为空')
    return false
  }
  if (!form.entityType) {
    ElMessage.warning('业务实体类型不能为空')
    return false
  }
  if (!form.fieldName) {
    ElMessage.warning('字段名不能为空')
    return false
  }
  if (form.permission === 'MASKED' && !form.maskPattern) {
    ElMessage.warning('脱敏权限必须选择脱敏规则')
    return false
  }
  if (form.maskPattern === 'custom' && !form.customPattern) {
    ElMessage.warning('自定义脱敏规则必须填写正则表达式')
    return false
  }
  return true
}

async function handleSubmit() {
  if (!validateForm()) return
  const payload: ApprovalFieldPermission = {
    ...form,
    approvalNodeId: form.approvalNodeId as number
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateFieldPermission(payload)
      ElMessage.success('更新成功')
    } else {
      await saveFieldPermission(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

function handleDelete(row: ApprovalFieldPermission) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除字段「${row.fieldName}」的权限配置吗？`, '删除确认', {
    type: 'warning'
  })
    .then(async () => {
      await deleteFieldPermission(row.id!)
      ElMessage.success('删除成功')
      await loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function permissionTagType(perm?: string) {
  switch (perm) {
    case 'VISIBLE':
      return 'success'
    case 'MASKED':
      return 'warning'
    case 'HIDDEN':
      return 'info'
    default:
      return 'info'
  }
}

function permissionLabel(perm?: string) {
  switch (perm) {
    case 'VISIBLE':
      return '可见'
    case 'MASKED':
      return '脱敏'
    case 'HIDDEN':
      return '隐藏'
    default:
      return perm || '-'
  }
}

function maskPatternLabel(pattern?: string) {
  const item = maskPatternOptions.find((o) => o.value === pattern)
  return item ? item.label : pattern || '-'
}

onMounted(() => {
  // 默认不加载，等待用户输入节点ID
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">审批字段权限配置</span>
      </template>

      <!-- 查询条件 -->
      <div class="toolbar">
        <el-input-number
          v-model="query.approvalNodeId"
          :min="1"
          placeholder="审批节点ID"
          style="width: 180px"
          controls-position="right"
        />
        <el-input
          v-model="query.entityType"
          placeholder="业务实体类型（可选）"
          style="width: 220px"
          clearable
        />
        <el-button type="primary" :icon="'Search'" @click="loadData">查询</el-button>
        <el-button type="success" :icon="'Plus'" @click="openCreate">新增字段权限</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="entityType" label="业务实体类型" min-width="160" show-overflow-tooltip />
        <el-table-column prop="fieldName" label="字段名" min-width="140" show-overflow-tooltip />
        <el-table-column label="权限" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="permissionTagType(row.permission)" size="small">
              {{ permissionLabel(row.permission) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="脱敏规则" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.maskPattern ? maskPatternLabel(row.maskPattern) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="customPattern" label="自定义正则" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.customPattern || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="请输入审批节点ID查询，或该节点暂无字段权限配置" />
        </template>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字段权限' : '新增字段权限'"
      width="560px"
      destroy-on-close
    >
      <el-form :model="form" label-width="110px">
        <el-form-item label="审批节点ID">
          <el-input-number
            v-model="form.approvalNodeId"
            :min="1"
            controls-position="right"
            style="width: 100%"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="业务实体类型">
          <el-input v-model="form.entityType" placeholder="如 Deliverable" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="字段名">
          <el-input v-model="form.fieldName" placeholder="如 contractAmount" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="权限">
          <el-select v-model="form.permission" @change="onPermissionChange" style="width: 100%">
            <el-option
              v-for="opt in permissionOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="needMaskPattern" label="脱敏规则">
          <el-select
            v-model="form.maskPattern"
            @change="onMaskPatternChange"
            style="width: 100%"
            placeholder="请选择脱敏规则"
          >
            <el-option
              v-for="opt in maskPatternOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="needCustomPattern" label="自定义正则">
          <el-input
            v-model="form.customPattern"
            placeholder="如 \d{4}（匹配部分将被替换为 ***）"
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
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
  flex-wrap: wrap;
}
</style>
