<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createOAuth2Client,
  deleteOAuth2Client,
  getOAuth2ClientPage,
  updateOAuth2Client,
  type OAuth2ClientPageReqVO,
  type OAuth2ClientRespVO,
  type OAuth2ClientSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<OAuth2ClientRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  clientId: string
  name: string
  status: number | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  clientId: '',
  name: '',
  status: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const grantTypeOptions = [
  { value: 'password', label: 'password' },
  { value: 'authorization_code', label: 'authorization_code' },
  { value: 'implicit', label: 'implicit' },
  { value: 'refresh_token', label: 'refresh_token' },
  { value: 'client_credentials', label: 'client_credentials' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<OAuth2ClientSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  clientId: [{ required: true, message: '请输入客户端编号', trigger: 'blur' }],
  secret: [{ required: true, message: '请输入客户端密钥', trigger: 'blur' }],
  name: [{ required: true, message: '请输入应用名', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  accessTokenValiditySeconds: [
    { required: true, message: '请输入访问令牌有效期', trigger: 'blur' }
  ],
  refreshTokenValiditySeconds: [
    { required: true, message: '请输入刷新令牌有效期', trigger: 'blur' }
  ]
}

function createEmptyForm(): OAuth2ClientSaveReqVO {
  return {
    clientId: '',
    secret: '',
    name: '',
    logo: '',
    description: '',
    status: 0,
    accessTokenValiditySeconds: 1800,
    refreshTokenValiditySeconds: 43200,
    redirectUris: [],
    autoApprove: false,
    authorizedGrantTypes: [],
    scopes: [],
    authorities: [],
    resourceIds: [],
    additionalInformation: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: OAuth2ClientPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.clientId) params.clientId = query.clientId
    if (query.name) params.name = query.name
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getOAuth2ClientPage(params)
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
  query.clientId = ''
  query.name = ''
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增 OAuth2 客户端'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: OAuth2ClientRespVO) {
  dialogTitle.value = '编辑 OAuth2 客户端'
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
        await updateOAuth2Client(form)
        ElMessage.success('更新成功')
      } else {
        await createOAuth2Client(form)
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

function handleDelete(row: OAuth2ClientRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除客户端「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteOAuth2Client(row.id)
      ElMessage.success('删除成功')
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

function statusLabel(status: number) {
  return status === 0 ? '开启' : '停用'
}

function statusTagType(status: number) {
  return status === 0 ? 'success' : 'danger'
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
        <span>OAuth2 客户端</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="应用名">
          <el-input v-model="query.name" placeholder="应用名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="客户端编号">
          <el-input v-model="query.clientId" placeholder="客户端编号" clearable @keyup.enter="handleSearch" />
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增客户端</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="clientId" label="客户端编号" min-width="140" />
        <el-table-column prop="name" label="应用名" min-width="140" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="访问令牌有效期" width="140">
          <template #default="{ row }">{{ row.accessTokenValiditySeconds }} 秒</template>
        </el-table-column>
        <el-table-column label="刷新令牌有效期" width="140">
          <template #default="{ row }">{{ row.refreshTokenValiditySeconds }} 秒</template>
        </el-table-column>
        <el-table-column label="授权类型" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag v-for="t in row.authorizedGrantTypes" :key="t" size="small" style="margin: 2px">
              {{ t }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="客户端编号" prop="clientId">
          <el-input v-model="form.clientId" :disabled="!!form.id" placeholder="请输入客户端编号" />
        </el-form-item>
        <el-form-item label="客户端密钥" prop="secret">
          <el-input v-model="form.secret" placeholder="请输入客户端密钥" show-password />
        </el-form-item>
        <el-form-item label="应用名" prop="name">
          <el-input v-model="form.name" placeholder="请输入应用名" />
        </el-form-item>
        <el-form-item label="应用图标" prop="logo">
          <el-input v-model="form.logo" placeholder="请输入应用图标 URL" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="访问令牌有效期" prop="accessTokenValiditySeconds">
          <el-input-number v-model="form.accessTokenValiditySeconds" :min="1" controls-position="right" />
          <span style="margin-left: 8px">秒</span>
        </el-form-item>
        <el-form-item label="刷新令牌有效期" prop="refreshTokenValiditySeconds">
          <el-input-number v-model="form.refreshTokenValiditySeconds" :min="1" controls-position="right" />
          <span style="margin-left: 8px">秒</span>
        </el-form-item>
        <el-form-item label="重定向 URI" prop="redirectUris">
          <el-select
            v-model="form.redirectUris"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请输入重定向 URI"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="授权类型" prop="authorizedGrantTypes">
          <el-select
            v-model="form.authorizedGrantTypes"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择授权类型"
            style="width: 100%"
          >
            <el-option v-for="o in grantTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="授权范围" prop="scopes">
          <el-select
            v-model="form.scopes"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请输入授权范围"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限" prop="authorities">
          <el-select
            v-model="form.authorities"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请输入权限"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="自动审批" prop="autoApprove">
          <el-switch v-model="form.autoApprove" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
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
