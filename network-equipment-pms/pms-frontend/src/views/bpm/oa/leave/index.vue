<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createLeave,
  getLeavePage,
  type LeaveRespVO,
  type LeaveSaveReqVO
} from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<LeaveRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  type: number | undefined
  status: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  type: undefined,
  status: undefined,
  createTime: undefined
})

// 请假类型
const typeOptions = [
  { value: 1, label: '病假' },
  { value: 2, label: '事假' },
  { value: 3, label: '婚假' },
  { value: 4, label: '丧假' },
  { value: 5, label: '年假' }
]

// 审批状态
const statusOptions = [
  { value: 1, label: '审批中' },
  { value: 2, label: '审批通过' },
  { value: 3, label: '审批不通过' },
  { value: 4, label: '已取消' }
]

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<LeaveSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  type: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入请假原因', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

function createEmptyForm(): LeaveSaveReqVO {
  return { type: 1, reason: '', startTime: '', endTime: '' }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getLeavePage(query)
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
  query.type = undefined
  query.status = undefined
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.startTime && form.endTime && form.startTime >= form.endTime) {
      ElMessage.warning('结束时间需大于开始时间')
      return
    }
    submitting.value = true
    try {
      await createLeave(form)
      ElMessage.success('发起请假成功')
      dialogVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

function typeLabel(type: number) {
  return typeOptions.find((o) => o.value === type)?.label || '未知'
}

function statusLabel(status: number) {
  return statusOptions.find((o) => o.value === status)?.label || '未知'
}

function statusTagType(status: number) {
  const map: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return map[status] || 'info'
}

/** 计算请假时长（天） */
function durationLabel(row: LeaveRespVO) {
  if (!row.startTime || !row.endTime) return '-'
  const start = new Date(row.startTime).getTime()
  const end = new Date(row.endTime).getTime()
  if (isNaN(start) || isNaN(end) || end <= start) return '-'
  return (end - start) / (1000 * 60 * 60 * 24)
}

function handleDetail(row: LeaveRespVO) {
  ElMessageBox.alert(
    `编号：${row.id}<br/>类型：${typeLabel(row.type)}<br/>原因：${row.reason}<br/>开始时间：${row.startTime}<br/>结束时间：${row.endTime}<br/>状态：${statusLabel(row.status)}`,
    '请假详情',
    { dangerouslyUseHTMLString: true }
  ).catch(() => {
    /* closed */
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
      <template #header>OA 请假</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="请假类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="query.createTime"
            type="daterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">发起请假</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="申请编号" width="100" />
        <el-table-column label="请假类型" width="100">
          <template #default="{ row }">{{ typeLabel(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" min-width="160" />
        <el-table-column prop="endTime" label="结束时间" min-width="160" />
        <el-table-column label="时长(天)" width="100">
          <template #default="{ row }">{{ durationLabel(row) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" min-width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" title="发起请假" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择请假类型" style="width: 100%">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入请假原因" />
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
