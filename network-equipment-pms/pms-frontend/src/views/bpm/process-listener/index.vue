<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createProcessListener,
  deleteProcessListener,
  getProcessListenerPage,
  updateProcessListener,
  type ProcessListenerRespVO,
  type ProcessListenerSaveReqVO
} from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<ProcessListenerRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string; type: string | undefined; status: number | undefined }>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  type: undefined,
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '关闭' }
]

// 监听器类型
const typeOptions = [
  { value: 'execution', label: '执行监听器' },
  { value: 'task', label: '任务监听器' },
  { value: 'event', label: '事件监听器' }
]

// 监听事件
const eventOptions = [
  { value: 'start', label: 'start' },
  { value: 'end', label: 'end' },
  { value: 'take', label: 'take' },
  { value: 'create', label: 'create' },
  { value: 'assignment', label: 'assignment' },
  { value: 'complete', label: 'complete' },
  { value: 'delete', label: 'delete' }
]

// 值类型
const valueTypeOptions = [
  { value: 'class', label: 'class' },
  { value: 'expression', label: 'expression' },
  { value: 'delegateExpression', label: 'delegateExpression' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<ProcessListenerSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入监听器名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择监听器类型', trigger: 'change' }],
  event: [{ required: true, message: '请选择监听事件', trigger: 'change' }],
  valueType: [{ required: true, message: '请选择值类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入值', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): ProcessListenerSaveReqVO {
  return { name: '', type: 'execution', event: 'start', valueType: 'class', value: '', status: 0 }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getProcessListenerPage(query)
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
  query.type = undefined
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增流程监听器'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: ProcessListenerRespVO) {
  dialogTitle.value = '编辑流程监听器'
  Object.assign(form, createEmptyForm(), row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateProcessListener(form)
        ElMessage.success('更新成功')
      } else {
        await createProcessListener(form)
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

function handleDelete(row: ProcessListenerRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除监听器「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteProcessListener(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function statusLabel(status: number) {
  return status === 0 ? '开启' : '关闭'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'info'
}

function typeLabel(type: string) {
  return typeOptions.find((o) => o.value === type)?.label || type
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
      <template #header>流程监听器</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="监听器名称">
          <el-input v-model="query.name" placeholder="监听器名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增监听器</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="监听器名称" min-width="140" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">{{ typeLabel(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="event" label="事件" width="110" />
        <el-table-column prop="valueType" label="值类型" width="140" />
        <el-table-column prop="value" label="值" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="监听器名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入监听器名称" />
        </el-form-item>
        <el-form-item label="监听器类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择监听器类型" style="width: 100%">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="监听事件" prop="event">
          <el-select v-model="form.event" placeholder="请选择监听事件" style="width: 100%" filterable allow-create>
            <el-option v-for="o in eventOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="值类型" prop="valueType">
          <el-select v-model="form.valueType" placeholder="请选择值类型" style="width: 100%">
            <el-option v-for="o in valueTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="form.value" placeholder="请输入值" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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
</style>
