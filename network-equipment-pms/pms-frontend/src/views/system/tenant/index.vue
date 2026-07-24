<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createTenant,
  deleteTenant,
  getTenantPage,
  getTenantPackageList,
  updateTenant,
  type TenantPackageSimpleRespVO,
  type TenantPageReqVO,
  type TenantRespVO,
  type TenantSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<TenantRespVO[]>([])
const total = ref(0)
const packageList = ref<TenantPackageSimpleRespVO[]>([])

const query = reactive<{
  pageNo: number
  pageSize: number
  name: string
  contactName: string
  status: number | undefined
  createTime: string[] | undefined
}>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  contactName: '',
  status: undefined,
  createTime: undefined
})

const statusOptions = [
  { value: 0, label: '开启' },
  { value: 1, label: '停用' }
]

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<TenantSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入租户名', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactMobile: [{ required: true, message: '请输入联系手机号', trigger: 'blur' }],
  domain: [{ required: true, message: '请输入绑定域名', trigger: 'blur' }],
  packageId: [{ required: true, message: '请选择租户套餐', trigger: 'change' }],
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入管理员密码', trigger: 'blur' }],
  expireTime: [{ required: true, message: '请选择过期时间', trigger: 'change' }],
  accountCount: [{ required: true, message: '请输入账号额度', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function createEmptyForm(): TenantSaveReqVO {
  return {
    name: '',
    contactName: '',
    contactMobile: '',
    status: 0,
    domain: '',
    packageId: 0,
    username: '',
    password: '',
    expireTime: '',
    accountCount: 0,
    websites: []
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: TenantPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.name) params.name = query.name
    if (query.contactName) params.contactName = query.contactName
    if (query.status !== undefined && query.status !== null) params.status = query.status
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getTenantPage(params)
    tableData.value = res.list
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function loadPackageList() {
  try {
    packageList.value = await getTenantPackageList()
  } catch {
    /* handled by interceptor */
  }
}

function handleSearch() {
  query.pageNo = 1
  loadData()
}

function handleReset() {
  query.name = ''
  query.contactName = ''
  query.status = undefined
  query.createTime = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增租户'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: TenantRespVO) {
  dialogTitle.value = '编辑租户'
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
        await updateTenant(form)
        ElMessage.success('更新成功')
      } else {
        await createTenant(form)
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

function handleDelete(row: TenantRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除租户「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTenant(row.id)
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

function packageName(id: number) {
  return packageList.value.find((p) => p.id === id)?.name || '-'
}

function formatDateTime(val?: string): string {
  if (!val) return '-'
  return val.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  loadData()
  loadPackageList()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>租户管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="租户名">
          <el-input v-model="query.name" placeholder="租户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="query.contactName" placeholder="联系人" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增租户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="租户名" min-width="140" />
        <el-table-column prop="contactName" label="联系人" min-width="100" />
        <el-table-column prop="contactMobile" label="联系手机号" min-width="120" />
        <el-table-column prop="domain" label="绑定域名" min-width="160" show-overflow-tooltip />
        <el-table-column label="租户套餐" min-width="120">
          <template #default="{ row }">{{ packageName(row.packageId) }}</template>
        </el-table-column>
        <el-table-column prop="accountCount" label="账号额度" width="100" />
        <el-table-column label="过期时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.expireTime) }}</template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="租户名" prop="name">
          <el-input v-model="form.name" placeholder="请输入租户名" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系手机号" prop="contactMobile">
          <el-input v-model="form.contactMobile" placeholder="请输入联系手机号" />
        </el-form-item>
        <el-form-item label="绑定域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入绑定域名" />
        </el-form-item>
        <el-form-item label="租户套餐" prop="packageId">
          <el-select v-model="form.packageId" placeholder="请选择租户套餐" style="width: 100%">
            <el-option
              v-for="p in packageList"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账号额度" prop="accountCount">
          <el-input-number v-model="form.accountCount" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择过期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="管理员账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="管理员密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入管理员密码" show-password />
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
