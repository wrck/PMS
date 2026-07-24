<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { getProcessInstanceCopyPage, type ProcessInstanceRespVO } from '@/api/yudao-bpm'

const loading = ref(false)
const tableData = ref<ProcessInstanceRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  processInstanceId: string
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  processInstanceId: '',
  createTime: undefined
})

async function loadData() {
  loading.value = true
  try {
    const res = await getProcessInstanceCopyPage(query)
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
  query.processInstanceId = ''
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function handleDetail(row: ProcessInstanceRespVO) {
  ElMessageBox.alert(
    `流程名称：${row.name}<br/>流程编号：${row.id}<br/>状态：${row.status}<br/>备注：${row.remark || '-'}`,
    '流程详情',
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
      <template #header>抄送任务</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="流程编号">
          <el-input
            v-model="query.processInstanceId"
            placeholder="流程编号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="抄送时间">
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
        <el-table-column prop="name" label="流程名称" min-width="180" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="remark" label="抄送意见" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="抄送时间" min-width="160" />
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
