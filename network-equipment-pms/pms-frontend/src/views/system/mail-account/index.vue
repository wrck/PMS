<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createMailAccount,
  deleteMailAccount,
  getMailAccountPage,
  updateMailAccount,
  type MailAccountPageReqVO,
  type MailAccountRespVO,
  type MailAccountSaveReqVO
} from '@/api/yudao-system'

const loading = ref(false)
const tableData = ref<MailAccountRespVO[]>([])
const total = ref(0)

const query = reactive<{
  pageNo: number
  pageSize: number
  mail: string
  username: string
  host: string
}>({
  pageNo: 1,
  pageSize: 10,
  mail: '',
  username: '',
  host: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<MailAccountSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  mail: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  host: [{ required: true, message: '请输入 SMTP 服务器', trigger: 'blur' }],
  port: [{ required: true, message: '请输入 SMTP 端口', trigger: 'blur' }]
}

function createEmptyForm(): MailAccountSaveReqVO {
  return {
    mail: '',
    username: '',
    password: '',
    host: '',
    port: 465,
    sslEnable: true,
    starttlsEnable: false
  }
}

async function loadData() {
  loading.value = true
  try {
    const params: MailAccountPageReqVO = {
      pageNo: query.pageNo,
      pageSize: query.pageSize
    }
    if (query.mail) params.mail = query.mail
    if (query.username) params.username = query.username
    if (query.host) params.host = query.host
    const res = await getMailAccountPage(params)
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
  query.mail = ''
  query.username = ''
  query.host = ''
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增邮箱账号'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: MailAccountRespVO) {
  dialogTitle.value = '编辑邮箱账号'
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
        await updateMailAccount(form)
        ElMessage.success('更新成功')
      } else {
        await createMailAccount(form)
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

function handleDelete(row: MailAccountRespVO) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除邮箱账号「${row.mail}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteMailAccount(row.id)
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
        <span>邮箱账号</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="邮箱地址">
          <el-input v-model="query.mail" placeholder="邮箱地址" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="SMTP 服务器">
          <el-input v-model="query.host" placeholder="SMTP 主机" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增邮箱账号</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="mail" label="邮箱地址" min-width="180" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="host" label="SMTP 服务器" min-width="160" />
        <el-table-column prop="port" label="SMTP 端口" width="100" />
        <el-table-column label="SSL" width="80">
          <template #default="{ row }">
            <el-tag :type="row.sslEnable ? 'success' : 'info'">{{ row.sslEnable ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="STARTTLS" width="100">
          <template #default="{ row }">
            <el-tag :type="row.starttlsEnable ? 'success' : 'info'">{{ row.starttlsEnable ? '是' : '否' }}</el-tag>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="邮箱地址" prop="mail">
          <el-input v-model="form.mail" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="SMTP 服务器" prop="host">
          <el-input v-model="form.host" placeholder="请输入 SMTP 服务器地址" />
        </el-form-item>
        <el-form-item label="SMTP 端口" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" controls-position="right" />
        </el-form-item>
        <el-form-item label="启用 SSL" prop="sslEnable">
          <el-switch v-model="form.sslEnable" />
        </el-form-item>
        <el-form-item label="启用 STARTTLS" prop="starttlsEnable">
          <el-switch v-model="form.starttlsEnable" />
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
