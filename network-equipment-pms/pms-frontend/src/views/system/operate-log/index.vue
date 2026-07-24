<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getOperateLogPage,
  type OperateLogPageReqVO,
  type OperateLogRespVO
} from '@/api/yudao-system'

defineOptions({ name: 'SystemOperateLog' })

const loading = ref(false)
const tableData = ref<OperateLogRespVO[]>([])
const total = ref(0)

const query = reactive<OperateLogPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  type: '',
  subType: '',
  createTime: undefined
})

async function loadData() {
  loading.value = true
  try {
    const params: OperateLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.type) params.type = query.type
    if (query.subType) params.subType = query.subType
    if (query.createTime && query.createTime.length === 2) {
      params.createTime = query.createTime
    }
    const res = await getOperateLogPage(params)
    tableData.value = res?.list ?? []
    total.value = res?.total ?? 0
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
  query.type = ''
  query.subType = ''
  query.createTime = undefined
  query.pageNo = 1
  loadData()
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

// 请求方法 tag 类型
function methodTagType(method: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  const m = (method || '').toUpperCase()
  if (m === 'GET') return 'success'
  if (m === 'POST') return 'primary'
  if (m === 'PUT' || m === 'PATCH') return 'warning'
  if (m === 'DELETE') return 'danger'
  return 'info'
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">操作日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="操作模块">
          <el-input
            v-model="query.type"
            placeholder="请输入操作模块"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作名">
          <el-input
            v-model="query.subType"
            placeholder="请输入操作名"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="query.createTime"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="日志编号" width="100" />
        <el-table-column prop="userName" label="操作人" min-width="120" show-overflow-tooltip />
        <el-table-column prop="type" label="操作模块" min-width="140" show-overflow-tooltip />
        <el-table-column prop="subType" label="操作名" min-width="140" show-overflow-tooltip />
        <el-table-column label="请求方法" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="methodTagType(row.requestMethod)" size="small">
              {{ row.requestMethod || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="requestUrl"
          label="请求地址"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          prop="action"
          label="操作明细"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column label="执行时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无操作日志" />
        </template>
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
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
