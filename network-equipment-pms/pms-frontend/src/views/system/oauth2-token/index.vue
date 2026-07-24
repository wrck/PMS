<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteOAuth2Token,
  getOAuth2TokenPage,
  type OAuth2AccessTokenPageReqVO,
  type OAuth2AccessTokenRespVO
} from '@/api/yudao-system'

defineOptions({ name: 'SystemOAuth2Token' })

const loading = ref(false)
const tableData = ref<OAuth2AccessTokenRespVO[]>([])
const total = ref(0)

const query = reactive<OAuth2AccessTokenPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  clientId: ''
})

async function loadData() {
  loading.value = true
  try {
    const params: OAuth2AccessTokenPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.userId) params.userId = query.userId
    if (query.clientId) params.clientId = query.clientId
    const res = await getOAuth2TokenPage(params)
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
  query.userId = undefined
  query.clientId = ''
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

// 用户类型：1=管理员，2=会员
function userTypeLabel(userType: number): string {
  if (userType === 1) return '管理员'
  if (userType === 2) return '会员'
  return String(userType)
}

function userTypeTagType(userType: number): 'primary' | 'success' {
  return userType === 1 ? 'primary' : 'success'
}

function handleForceLogout(row: OAuth2AccessTokenRespVO) {
  ElMessageBox.confirm(
    `确定强制注销该令牌吗？\n用户编号：${row.userId}\n客户端：${row.clientId}`,
    '强制注销确认',
    { type: 'warning' }
  )
    .then(async () => {
      try {
        await deleteOAuth2Token(row.accessToken)
        ElMessage.success('强制注销成功')
        loadData()
      } catch {
        /* handled by interceptor */
      }
    })
    .catch(() => {
      /* cancelled */
    })
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
        <span class="page-title">令牌管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户编号">
          <el-input-number
            v-model="query.userId"
            :min="1"
            :controls="false"
            placeholder="请输入用户编号"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="客户端编号">
          <el-input
            v-model="query.clientId"
            placeholder="请输入客户端编号"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column
          prop="accessToken"
          label="访问令牌"
          min-width="240"
          show-overflow-tooltip
        />
        <el-table-column
          prop="refreshToken"
          label="刷新令牌"
          min-width="240"
          show-overflow-tooltip
        />
        <el-table-column prop="userId" label="用户编号" width="100" />
        <el-table-column label="用户类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="userTypeTagType(row.userType)" size="small">
              {{ userTypeLabel(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clientId" label="客户端编号" min-width="140" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="过期时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.expiresTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleForceLogout(row)">强制注销</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无令牌数据" />
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
