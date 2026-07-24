<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createSocialClient,
  deleteSocialClient,
  getSocialClientPage,
  updateSocialClient,
  type SocialClientPageReqVO,
  type SocialClientRespVO,
  type SocialClientSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<SocialClientRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  name: string
  socialType: number | undefined
  userType: number | undefined
  status: number | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  socialType: undefined,
  userType: undefined,
  status: undefined
})

const socialTypeOptions = [
  { value: 10, label: '钉钉' },
  { value: 20, label: '企业微信' },
  { value: 30, label: '微信公众号' },
  { value: 31, label: '微信小程序' },
  { value: 32, label: '微信开放平台（网站）' },
  { value: 33, label: '微信开放平台（App）' },
  { value: 34, label: '微信扫码登录' }
]

const userTypeOptions = [
  { value: 1, label: '会员' },
  { value: 2, label: '管理员' }
]

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<SocialClientSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  socialType: [{ required: true, message: '请选择社交类型', trigger: 'change' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  clientId: [{ required: true, message: '请输入客户端编号', trigger: 'blur' }],
  clientSecret: [{ required: true, message: '请输入客户端密钥', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): SocialClientSaveReqVO {
  return {
    name: '',
    socialType: 10,
    userType: 2,
    clientId: '',
    clientSecret: '',
    agentId: '',
    publicKey: '',
    status: 0
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: SocialClientPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.name) params.name = query.name
    if (query.socialType !== undefined && query.socialType !== null) params.socialType = query.socialType
    if (query.userType !== undefined && query.userType !== null) params.userType = query.userType
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const res = await getSocialClientPage(params)
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
  query.socialType = undefined
  query.userType = undefined
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增社交客户端'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: SocialClientRespVO) {
  dialogTitle.value = '编辑社交客户端'
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
        await updateSocialClient(form)
        ElMessage.success('更新成功')
      } else {
        await createSocialClient(form)
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

function handleDelete(row: SocialClientRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除社交客户端「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteSocialClient(row.id)
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

function socialTypeLabel(type: number) {
  return socialTypeOptions.find((o) => o.value === type)?.label || String(type)
}

function userTypeLabel(type: number) {
  return userTypeOptions.find((o) => o.value === type)?.label || String(type)
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
        <span>社交客户端</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="应用名称">
          <el-input v-model="query.name" placeholder="应用名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="社交类型">
          <el-select v-model="query.socialType" placeholder="全部" clearable filterable style="width: 180px">
            <el-option v-for="o in socialTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="query.userType" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in userTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增社交客户端</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="应用名称" min-width="140" />
        <el-table-column label="社交类型" width="160">
          <template #default="{ row }">{{ socialTypeLabel(row.socialType) }}</template>
        </el-table-column>
        <el-table-column label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.userType === 2 ? 'success' : 'primary'">{{ userTypeLabel(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clientId" label="客户端编号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="agentId" label="Agent ID" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入应用名称" />
        </el-form-item>
        <el-form-item label="社交类型" prop="socialType">
          <el-select v-model="form.socialType" placeholder="请选择社交类型" filterable style="width: 100%">
            <el-option v-for="o in socialTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option v-for="o in userTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户端编号" prop="clientId">
          <el-input v-model="form.clientId" placeholder="请输入客户端编号" />
        </el-form-item>
        <el-form-item label="客户端密钥" prop="clientSecret">
          <el-input v-model="form.clientSecret" placeholder="请输入客户端密钥" show-password />
        </el-form-item>
        <el-form-item label="Agent ID" prop="agentId">
          <el-input v-model="form.agentId" placeholder="请输入 Agent ID（可选）" />
        </el-form-item>
        <el-form-item label="公钥" prop="publicKey">
          <el-input v-model="form.publicKey" type="textarea" :rows="3" placeholder="请输入公钥（可选）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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
