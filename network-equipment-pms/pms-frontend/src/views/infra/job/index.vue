<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createJob,
  deleteJob,
  getJobPage,
  triggerJob,
  updateJob,
  updateJobStatus,
  type JobRespVO,
  type JobSaveReqVO
} from '@/api/yudao-infra'

const loading = ref(false)
const tableData = ref<JobRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string; handlerName: string; status: number | undefined }>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  handlerName: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '初始化' },
  { value: 1, label: '开启' },
  { value: 2, label: '暂停' }
]

function statusTagType(status: number): 'info' | 'success' | 'warning' {
  if (status === 0) return 'info'
  if (status === 1) return 'success'
  return 'warning'
}

function statusLabel(status: number): string {
  if (status === 0) return '初始化'
  if (status === 1) return '开启'
  return '暂停'
}

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<JobSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  handlerName: [{ required: true, message: '请输入处理器名称', trigger: 'blur' }],
  cronExpression: [{ required: true, message: '请输入 CRON 表达式', trigger: 'blur' }],
  retryCount: [{ required: true, message: '请输入重试次数', trigger: 'blur' }],
  retryInterval: [{ required: true, message: '请输入重试间隔', trigger: 'blur' }]
}

function createEmptyForm(): JobSaveReqVO {
  return {
    name: '',
    handlerName: '',
    handlerParam: '',
    cronExpression: '',
    retryCount: 0,
    retryInterval: 0,
    monitorTimeout: 0
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getJobPage(query)
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
  query.handlerName = ''
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增任务'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: JobRespVO) {
  dialogTitle.value = '编辑任务'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    handlerName: row.handlerName,
    handlerParam: row.handlerParam ?? '',
    cronExpression: row.cronExpression,
    retryCount: row.retryCount,
    retryInterval: row.retryInterval,
    monitorTimeout: row.monitorTimeout ?? 0
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateJob(form)
        ElMessage.success('更新成功')
      } else {
        await createJob(form)
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

function handleToggleStatus(row: JobRespVO) {
  const targetStatus = row.status === 1 ? 2 : 1
  const action = targetStatus === 2 ? '暂停' : '开启'
  ElMessageBox.confirm(`确定${action}任务「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await updateJobStatus(row.id, targetStatus)
      ElMessage.success(`${action}成功`)
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleTrigger(row: JobRespVO) {
  ElMessageBox.confirm(`确定立即触发任务「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await triggerJob(row.id)
      ElMessage.success('触发成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: JobRespVO) {
  ElMessageBox.confirm(`确定删除任务「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteJob(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
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
      <template #header>定时任务</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="任务名称">
          <el-input v-model="query.name" placeholder="任务名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="处理器名称">
          <el-input v-model="query.handlerName" placeholder="处理器名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增任务</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="任务编号" width="90" />
        <el-table-column prop="name" label="任务名称" min-width="140" />
        <el-table-column label="任务状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理器名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="cronExpression" label="CRON 表达式" min-width="160" />
        <el-table-column prop="retryCount" label="重试次数" width="90" />
        <el-table-column label="重试间隔" width="100">
          <template #default="{ row }">{{ row.retryInterval }}ms</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="warning" @click="handleToggleStatus(row)">暂停</el-button>
            <el-button v-if="row.status === 2" link type="success" @click="handleToggleStatus(row)">开启</el-button>
            <el-button link type="primary" @click="handleTrigger(row)">触发</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="处理器名称" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入处理器名称" />
        </el-form-item>
        <el-form-item label="处理器参数" prop="handlerParam">
          <el-input v-model="form.handlerParam" placeholder="请输入处理器参数" />
        </el-form-item>
        <el-form-item label="CRON 表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="请输入 CRON 表达式" />
        </el-form-item>
        <el-form-item label="重试次数" prop="retryCount">
          <el-input-number v-model="form.retryCount" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="重试间隔" prop="retryInterval">
          <el-input-number v-model="form.retryInterval" :min="0" controls-position="right" />
          <span class="unit-tip">单位：毫秒</span>
        </el-form-item>
        <el-form-item label="监控超时时间" prop="monitorTimeout">
          <el-input-number v-model="form.monitorTimeout" :min="0" controls-position="right" />
          <span class="unit-tip">单位：毫秒</span>
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
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.unit-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
