<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getExceptionLogPage,
  getLoginLogPage,
  type ExceptionLog,
  type ExceptionLogQuery,
  type LoginLog,
  type LoginLogQuery
} from '@/api/system-audit'

defineOptions({ name: 'SystemAudit' })

const router = useRouter()
const activeTab = ref<'login' | 'exception' | 'schedule'>('login')

// ============ 登录日志 ============
const loginLoading = ref(false)
const loginData = ref<LoginLog[]>([])
const loginTotal = ref(0)
const loginQuery = reactive<LoginLogQuery & { page: number; size: number }>({
  page: 1,
  size: 10,
  username: '',
  status: ''
})

async function loadLogin() {
  loginLoading.value = true
  try {
    const params: LoginLogQuery = { page: loginQuery.page, size: loginQuery.size }
    if (loginQuery.username) params.username = loginQuery.username
    if (loginQuery.status) params.status = loginQuery.status
    const res = await getLoginLogPage(params)
    loginData.value = res?.records ?? []
    loginTotal.value = res?.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    loginLoading.value = false
  }
}

function handleLoginSearch() {
  loginQuery.page = 1
  loadLogin()
}

function handleLoginReset() {
  loginQuery.username = ''
  loginQuery.status = ''
  loginQuery.page = 1
  loadLogin()
}

function handleLoginPageChange(p: number) {
  loginQuery.page = p
  loadLogin()
}

function handleLoginSizeChange(s: number) {
  loginQuery.size = s
  loginQuery.page = 1
  loadLogin()
}

function loginStatusTag(status?: string): 'success' | 'danger' | 'info' {
  if (status === 'SUCCESS') return 'success'
  if (status === 'FAIL') return 'danger'
  return 'info'
}

function loginStatusLabel(status?: string) {
  if (status === 'SUCCESS') return '成功'
  if (status === 'FAIL') return '失败'
  return status ?? '-'
}

// ============ 异常日志 ============
const exceptionLoading = ref(false)
const exceptionData = ref<ExceptionLog[]>([])
const exceptionTotal = ref(0)
const exceptionQuery = reactive<ExceptionLogQuery & { page: number; size: number }>({
  page: 1,
  size: 10,
  username: '',
  requestUri: ''
})

// 堆栈详情弹窗
const stackVisible = ref(false)
const stackLoading = ref(false)
const currentException = ref<ExceptionLog | null>(null)

async function loadException() {
  exceptionLoading.value = true
  try {
    const params: ExceptionLogQuery = {
      page: exceptionQuery.page,
      size: exceptionQuery.size
    }
    if (exceptionQuery.username) params.username = exceptionQuery.username
    if (exceptionQuery.requestUri) params.requestUri = exceptionQuery.requestUri
    const res = await getExceptionLogPage(params)
    exceptionData.value = res?.records ?? []
    exceptionTotal.value = res?.total ?? 0
  } catch {
    /* handled by interceptor */
  } finally {
    exceptionLoading.value = false
  }
}

function handleExceptionSearch() {
  exceptionQuery.page = 1
  loadException()
}

function handleExceptionReset() {
  exceptionQuery.username = ''
  exceptionQuery.requestUri = ''
  exceptionQuery.page = 1
  loadException()
}

function handleExceptionPageChange(p: number) {
  exceptionQuery.page = p
  loadException()
}

function handleExceptionSizeChange(s: number) {
  exceptionQuery.size = s
  exceptionQuery.page = 1
  loadException()
}

function handleRowClick(row: ExceptionLog) {
  if (!row.stackTrace) {
    return
  }
  currentException.value = row
  stackVisible.value = true
  stackLoading.value = false
}

function goScheduleMonitor() {
  router.push('/system/schedule')
}

function handleTabChange(name: string | number) {
  if (name === 'exception' && exceptionData.value.length === 0) {
    loadException()
  }
}

function formatDateTime(val?: string) {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

// ============ 初始化 ============
onMounted(() => {
  loadLogin()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title">审计日志</div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 登录日志 -->
        <el-tab-pane label="登录日志" name="login">
          <el-form :inline="true" @submit.prevent>
            <el-form-item label="用户名">
              <el-input
                v-model="loginQuery.username"
                placeholder="用户名"
                clearable
                style="width: 180px"
                @keyup.enter="handleLoginSearch"
              />
            </el-form-item>
            <el-form-item label="结果">
              <el-select
                v-model="loginQuery.status"
                placeholder="全部"
                clearable
                style="width: 140px"
              >
                <el-option label="成功" value="SUCCESS" />
                <el-option label="失败" value="FAIL" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="'Search'" @click="handleLoginSearch">查询</el-button>
              <el-button :icon="'Refresh'" @click="handleLoginReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="loginLoading" :data="loginData" border stripe>
            <el-table-column prop="username" label="用户名" min-width="140" show-overflow-tooltip />
            <el-table-column label="登录时间" width="170">
              <template #default="{ row }">{{ formatDateTime(row.loginTime) }}</template>
            </el-table-column>
            <el-table-column prop="loginIp" label="IP" min-width="140" show-overflow-tooltip />
            <el-table-column prop="loginLocation" label="登录地点" min-width="140" show-overflow-tooltip />
            <el-table-column label="登录结果" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="loginStatusTag(row.status)" size="small">
                  {{ loginStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="browser" label="浏览器" min-width="160" show-overflow-tooltip />
            <el-table-column prop="os" label="操作系统" min-width="140" show-overflow-tooltip />
            <el-table-column prop="message" label="提示消息" min-width="180" show-overflow-tooltip />
            <template #empty>
              <el-empty description="暂无登录日志" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="loginQuery.page"
            :page-size="loginQuery.size"
            :total="loginTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleLoginPageChange"
            @size-change="handleLoginSizeChange"
          />
        </el-tab-pane>

        <!-- 异常日志 -->
        <el-tab-pane label="异常日志" name="exception">
          <el-form :inline="true" @submit.prevent>
            <el-form-item label="用户名">
              <el-input
                v-model="exceptionQuery.username"
                placeholder="用户名"
                clearable
                style="width: 180px"
                @keyup.enter="handleExceptionSearch"
              />
            </el-form-item>
            <el-form-item label="请求路径">
              <el-input
                v-model="exceptionQuery.requestUri"
                placeholder="请求 URI"
                clearable
                style="width: 220px"
                @keyup.enter="handleExceptionSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="'Search'" @click="handleExceptionSearch">
                查询
              </el-button>
              <el-button :icon="'Refresh'" @click="handleExceptionReset">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table
            v-loading="exceptionLoading"
            :data="exceptionData"
            border
            stripe
            highlight-current-row
            @row-click="handleRowClick"
          >
            <el-table-column label="时间" width="170">
              <template #default="{ row }">{{ formatDateTime(row.occurTime) }}</template>
            </el-table-column>
            <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
            <el-table-column
              prop="exceptionType"
              label="异常类名"
              min-width="220"
              show-overflow-tooltip
            />
            <el-table-column
              prop="exceptionMessage"
              label="异常信息"
              min-width="260"
              show-overflow-tooltip
            />
            <el-table-column prop="requestMethod" label="方法" width="80" align="center" />
            <el-table-column
              prop="requestUri"
              label="请求路径"
              min-width="220"
              show-overflow-tooltip
            />
            <el-table-column prop="requestIp" label="IP" min-width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.stackTrace"
                  link
                  type="primary"
                  @click.stop="handleRowClick(row)"
                >
                  堆栈
                </el-button>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无异常日志" />
            </template>
          </el-table>

          <el-pagination
            class="pagination"
            background
            :current-page="exceptionQuery.page"
            :page-size="exceptionQuery.size"
            :total="exceptionTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handleExceptionPageChange"
            @size-change="handleExceptionSizeChange"
          />
        </el-tab-pane>

        <!-- 调度日志：跳转到定时任务监控页 -->
        <el-tab-pane label="调度日志" name="schedule">
          <div class="jump-card">
            <el-icon :size="48" class="jump-icon"><Promotion /></el-icon>
            <div class="jump-title">调度日志</div>
            <div class="jump-desc">
              调度日志详情请在「定时任务监控」页面查看，支持统计卡片、状态/日期筛选与失败列表。
            </div>
            <el-button type="primary" :icon="'Link'" @click="goScheduleMonitor">
              前往定时任务监控
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 堆栈详情弹窗 -->
    <el-dialog v-model="stackVisible" title="异常堆栈详情" width="720px" destroy-on-close>
      <div v-loading="stackLoading">
        <el-descriptions :column="1" border size="small" v-if="currentException">
          <el-descriptions-item label="时间">
            {{ formatDateTime(currentException.occurTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ currentException.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="异常类型">
            {{ currentException.exceptionType || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="异常消息">
            {{ currentException.exceptionMessage || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="请求路径">
            {{ currentException.requestMethod }} {{ currentException.requestUri }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="stack-title">完整堆栈</div>
        <pre class="stack-pre">{{ currentException?.stackTrace || '无堆栈信息' }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.text-muted {
  color: #c0c4cc;
}
.jump-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
  text-align: center;
}
.jump-icon {
  color: #409eff;
  margin-bottom: 16px;
}
.jump-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.jump-desc {
  font-size: 13px;
  color: #909399;
  max-width: 480px;
  margin-bottom: 20px;
  line-height: 1.6;
}
.stack-title {
  margin: 16px 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}
.stack-pre {
  max-height: 360px;
  overflow: auto;
  padding: 12px;
  margin: 0;
  background-color: #1e1e1e;
  color: #e0e0e0;
  border-radius: 4px;
  font-family: 'Menlo', 'Consolas', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
