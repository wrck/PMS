<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import {
  approveTask,
  delegateTask,
  getTaskTodoPage,
  rejectTask,
  transferTask,
  type TaskRespVO
} from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<TaskRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string }>({
  pageNo: 1,
  pageSize: 10,
  name: ''
})

// 任务状态选项
const statusOptions = [
  { value: 0, label: '待审批' },
  { value: 1, label: '审批中' },
  { value: 2, label: '审批通过' },
  { value: 3, label: '审批不通过' },
  { value: 4, label: '已取消' },
  { value: 5, label: '已退回' },
  { value: 7, label: '审批通过中' }
]

// 审批弹窗
const auditVisible = ref(false)
const auditFormRef = ref<FormInstance>()
const auditSubmitting = ref(false)
const currentTask = ref<TaskRespVO | null>(null)
const auditForm = reactive<{ reason: string; userId: number | undefined }>({
  reason: '',
  userId: undefined
})

async function loadData() {
  loading.value = true
  try {
    const res = await getTaskTodoPage(query)
    tableData.value = res.list
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNo = 1
  loadData()
}

function handleReset() {
  query.name = ''
  query.pageNo = 1
  loadData()
}

function statusLabel(status: number) {
  return statusOptions.find((o) => o.value === status)?.label || '未知'
}

function statusTagType(status: number) {
  const map: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
    4: 'info',
    5: 'warning',
    7: 'success'
  }
  return map[status] || 'info'
}

function handleAudit(row: TaskRespVO) {
  currentTask.value = row
  auditForm.reason = ''
  auditForm.userId = undefined
  auditVisible.value = true
}

function validateReason(): boolean {
  if (!auditForm.reason || !auditForm.reason.trim()) {
    ElMessage.warning('请输入审批意见')
    return false
  }
  return true
}

async function handleApprove() {
  if (!currentTask.value || !validateReason()) return
  auditSubmitting.value = true
  try {
    await approveTask({ id: currentTask.value.id, reason: auditForm.reason })
    ElMessage.success('审批通过成功')
    auditVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    auditSubmitting.value = false
  }
}

async function handleReject() {
  if (!currentTask.value || !validateReason()) return
  auditSubmitting.value = true
  try {
    await rejectTask({ id: currentTask.value.id, reason: auditForm.reason })
    ElMessage.success('拒绝成功')
    auditVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    auditSubmitting.value = false
  }
}

async function handleDelegate() {
  if (!currentTask.value) return
  if (!auditForm.userId) {
    ElMessage.warning('请输入被委派人 ID')
    return
  }
  if (!validateReason()) return
  auditSubmitting.value = true
  try {
    await delegateTask({
      id: currentTask.value.id,
      delegateUserId: auditForm.userId,
      reason: auditForm.reason
    })
    ElMessage.success('委派成功')
    auditVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    auditSubmitting.value = false
  }
}

async function handleTransfer() {
  if (!currentTask.value) return
  if (!auditForm.userId) {
    ElMessage.warning('请输入转办人 ID')
    return
  }
  if (!validateReason()) return
  auditSubmitting.value = true
  try {
    await transferTask({
      id: currentTask.value.id,
      transferUserId: auditForm.userId,
      reason: auditForm.reason
    })
    ElMessage.success('转办成功')
    auditVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    auditSubmitting.value = false
  }
}

function handlePageChange(p: number) {
  query.pageNo = p
  loadData()
}

function handleSizeChange(s: number) {
  query.pageSize = s
  query.pageNo = 1
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>待办任务</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="任务名称">
          <el-input v-model="query.name" placeholder="任务名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="任务编号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="name" label="任务名称" min-width="160" />
        <el-table-column label="流程实例" min-width="200">
          <template #default="{ row }">
            {{ row.processInstanceName || row.processInstanceId }}
          </template>
        </el-table-column>
        <el-table-column label="办理人" width="120">
          <template #default="{ row }">
            {{ row.assigneeUser?.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAudit(row)">办理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.pageNo"
        :page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <el-dialog v-model="auditVisible" title="任务审批" width="560px" destroy-on-close>
      <el-form ref="auditFormRef" :model="auditForm" label-width="100px">
        <el-form-item label="任务名称">
          <span>{{ currentTask?.name }}</span>
        </el-form-item>
        <el-form-item label="流程实例">
          <span>{{ currentTask?.processInstanceName || currentTask?.processInstanceId }}</span>
        </el-form-item>
        <el-form-item label="委派/转办用户">
          <el-input-number
            v-model="auditForm.userId"
            :min="1"
            controls-position="right"
            placeholder="用户 ID"
            style="width: 100%"
          />
          <el-text type="info" size="small">仅「委派」「转办」需要填写</el-text>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input
            v-model="auditForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="success" :loading="auditSubmitting" @click="handleApprove">通过</el-button>
        <el-button type="danger" :loading="auditSubmitting" @click="handleReject">拒绝</el-button>
        <el-button type="warning" :loading="auditSubmitting" @click="handleDelegate">委派</el-button>
        <el-button type="primary" :loading="auditSubmitting" @click="handleTransfer">转办</el-button>
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
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
