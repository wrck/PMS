<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getLoginLogPage,
  type LoginLogPageReqVO,
  type LoginLogRespVO
} from '@/api/yudao-system'

defineOptions({ name: 'SystemLoginLog' })

const loading = ref(false)
const tableData = ref<LoginLogRespVO[]>([])
const total = ref(0)

const query = reactive<LoginLogPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  username: '',
  userIp: '',
  createTime: undefined
})

async function loadData() {
  loading.value = true
  try {
    const params: LoginLogPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.username) params.username = query.username
    if (query.userIp) params.userIp = query.userIp
    if (query.createTime && query.createTime.length === 2) {
      params.createTime = query.createTime
    }
    const res = await getLoginLogPage(params)
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
  query.username = ''
  query.userIp = ''
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

// 日志类型：1=登录，2=登出
function logTypeLabel(logType: number): string {
  if (logType === 1) return '登录'
  if (logType === 2) return '登出'
  return String(logType)
}

function logTypeTagType(logType: number): 'primary' | 'info' {
  return logType === 1 ? 'primary' : 'info'
}

// 登录结果：0=成功，非0=失败
function resultTagType(result: number): 'success' | 'danger' {
  return result === 0 ? 'success' : 'danger'
}

function resultLabel(result: number): string {
  return result === 0 ? '成功' : `失败(${result})`
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
        <span class="page-title">登录日志</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户账号">
          <el-input
            v-model="query.username"
            placeholder="请输入用户账号"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="用户 IP">
          <el-input
            v-model="query.userIp"
            placeholder="请输入用户 IP"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="登录时间">
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
        <el-table-column label="日志类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="logTypeTagType(row.logType)" size="small">
              {{ logTypeLabel(row.logType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户账号" min-width="140" show-overflow-tooltip />
        <el-table-column prop="userIp" label="登录 IP" min-width="140" />
        <el-table-column label="登录结果" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="resultTagType(row.result)" size="small">
              {{ resultLabel(row.result) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="userAgent"
          label="User Agent"
          min-width="240"
          show-overflow-tooltip
        />
        <el-table-column label="登录时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无登录日志" />
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
