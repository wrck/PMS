<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  getSocialUserPage,
  type SocialUserPageReqVO,
  type SocialUserRespVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<SocialUserRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  type: number | undefined
  openid: string
  nickname: string
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  type: undefined,
  openid: '',
  nickname: '',
  createTime: undefined
})

const typeOptions = [
  { value: 10, label: '钉钉' },
  { value: 20, label: '企业微信' },
  { value: 30, label: '微信公众号' },
  { value: 31, label: '微信小程序' },
  { value: 32, label: '微信开放平台（网站）' },
  { value: 33, label: '微信开放平台（App）' },
  { value: 34, label: '微信扫码登录' }
]

const detailVisible = ref(false)
const detailData = ref<SocialUserRespVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    const params: SocialUserPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.type !== undefined && query.type !== null) params.type = query.type
    if (query.openid) params.openid = query.openid
    if (query.nickname) params.nickname = query.nickname
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getSocialUserPage(params)
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
  query.openid = ''
  query.nickname = ''
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

function handleViewDetail(row: SocialUserRespVO) {
  detailData.value = row
  detailVisible.value = true
}

function typeLabel(type: number) {
  return typeOptions.find((o) => o.value === type)?.label || String(type)
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>社交用户</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="社交类型">
          <el-select v-model="query.type" placeholder="全部" clearable filterable style="width: 180px">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="OpenID">
          <el-input v-model="query.openid" placeholder="OpenID" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="query.nickname" placeholder="用户昵称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="创建时间">
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
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column label="社交类型" width="160">
          <template #default="{ row }">{{ typeLabel(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="openid" label="OpenID" min-width="200" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.avatar"
              :src="row.avatar"
              style="width: 40px; height: 40px; border-radius: 50%"
              fit="cover"
              :preview-src-list="[row.avatar]"
              preview-teleported
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="编号 Code" min-width="140" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="社交用户详情" width="680px" destroy-on-close>
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="编号">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="社交类型">{{ typeLabel(detailData.type) }}</el-descriptions-item>
        <el-descriptions-item label="OpenID" :span="2">{{ detailData.openid }}</el-descriptions-item>
        <el-descriptions-item label="Token" :span="2">{{ detailData.token }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="编号 Code">{{ detailData.code }}</el-descriptions-item>
        <el-descriptions-item label="State">{{ detailData.state }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-image
            v-if="detailData.avatar"
            :src="detailData.avatar"
            style="width: 80px; height: 80px; border-radius: 50%"
            fit="cover"
            :preview-src-list="[detailData.avatar]"
            preview-teleported
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="原始 Token 信息" :span="2">{{ detailData.rawTokenInfo }}</el-descriptions-item>
        <el-descriptions-item label="原始用户信息" :span="2">{{ detailData.rawUserInfo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
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
