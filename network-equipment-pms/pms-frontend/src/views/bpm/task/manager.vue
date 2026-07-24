<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { getTaskManagerPage, type TaskRespVO } from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<TaskRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string }>({
  pageNo: 1,
  pageSize: 10,
  name: ''
})

const statusOptions = [
  { value: 0, label: '待审批' },
  { value: 1, label: '审批中' },
  { value: 2, label: '审批通过' },
  { value: 3, label: '审批不通过' },
  { value: 4, label: '已取消' },
  { value: 5, label: '已退回' },
  { value: 7, label: '审批通过中' }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getTaskManagerPage(query)
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

function handleDetail(row: TaskRespVO) {
  ElMessageBox.alert(
    `任务名称：${row.name}<br/>任务编号：${row.id}<br/>流程实例：${row.processInstanceName || row.processInstanceId}<br/>办理人：${row.assigneeUser?.nickname || '-'}<br/>状态：${statusLabel(row.status)}`,
    '任务详情',
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
      <template #header>任务管理</template>

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
