<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelProcessInstanceByAdmin,
  getProcessInstanceManagerPage,
  type ProcessInstanceRespVO
} from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<ProcessInstanceRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  name: string
  status: number | undefined
  result: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  status: undefined,
  result: undefined,
  createTime: undefined
})

const statusOptions = [
  { value: 1, label: '审批中' },
  { value: 2, label: '审批通过' },
  { value: 3, label: '审批不通过' },
  { value: 4, label: '已取消' }
]

const resultOptions = [
  { value: 2, label: '审批通过' },
  { value: 3, label: '审批不通过' },
  { value: 4, label: '已取消' }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getProcessInstanceManagerPage(query)
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
  query.status = undefined
  query.result = undefined
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function statusLabel(status: number) {
  return statusOptions.find((o) => o.value === status)?.label || '未知'
}

function statusTagType(status: number) {
  const map: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return map[status] || 'info'
}

function resultLabel(result: number) {
  return resultOptions.find((o) => o.value === result)?.label || '-'
}

function resultTagType(result: number) {
  const map: Record<number, string> = { 2: 'success', 3: 'danger', 4: 'info' }
  return map[result] || 'info'
}

function handleDetail(row: ProcessInstanceRespVO) {
  ElMessageBox.alert(
    `流程名称：${row.name}<br/>流程编号：${row.id}<br/>流程定义：${row.processDefinitionId}<br/>流程分类：${row.category || '-'}<br/>状态：${statusLabel(row.status)}<br/>结果：${resultLabel(row.result)}`,
    '流程详情',
    { dangerouslyUseHTMLString: true }
  ).catch(() => {
    /* closed */
  })
}

function handleCancel(row: ProcessInstanceRespVO) {
  ElMessageBox.prompt('请输入取消原因', '管理员取消流程', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/,
    inputErrorMessage: '取消原因不能为空'
  })
    .then(async ({ value }) => {
      await cancelProcessInstanceByAdmin(row.id, value)
      ElMessage.success('取消成功')
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
      <template #header>流程实例管理</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="流程名称">
          <el-input v-model="query.name" placeholder="流程名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.result" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in resultOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发起时间">
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

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="流程编号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="name" label="流程名称" min-width="160" />
        <el-table-column prop="category" label="流程分类" min-width="120" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.result" :type="resultTagType(row.result)">{{ resultLabel(row.result) }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发起时间" min-width="160" />
        <el-table-column prop="endTime" label="结束时间" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="danger" @click="handleCancel(row)">取消</el-button>
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
