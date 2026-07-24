<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createDataSourceConfig,
  deleteDataSourceConfig,
  getDataSourceConfigList,
  updateDataSourceConfig,
  type DataSourceConfigRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraDataSourceConfig' })

const loading = ref(false)
const tableData = ref<DataSourceConfigRespVO[]>([])
const searchName = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<DataSourceConfigRespVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入数据源连接', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function createEmptyForm(): DataSourceConfigRespVO {
  return {
    name: '',
    url: '',
    username: '',
    password: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    tableData.value = await getDataSourceConfigList()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

/** 客户端按名称过滤（接口仅提供 list） */
const filteredData = computed(() => {
  const kw = searchName.value.trim().toLowerCase()
  if (!kw) return tableData.value
  return tableData.value.filter((item) => item.name?.toLowerCase().includes(kw))
})

function handleSearch() {
  /* 客户端过滤，filteredData 自动响应 */
}

function handleReset() {
  searchName.value = ''
}

function handleAdd() {
  dialogTitle.value = '新增数据源'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: DataSourceConfigRespVO) {
  dialogTitle.value = '编辑数据源'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    url: row.url,
    username: row.username,
    password: row.password
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateDataSourceConfig(form)
        ElMessage.success('更新成功')
      } else {
        await createDataSourceConfig(form)
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

function handleDelete(row: DataSourceConfigRespVO) {
  ElMessageBox.confirm(`确定删除数据源「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDataSourceConfig(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
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
      <template #header>数据源配置</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="数据源名称">
          <el-input
            v-model="searchName"
            placeholder="请输入数据源名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增数据源</el-button>
      </div>

      <el-table v-loading="loading" :data="filteredData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="数据源名称" min-width="140" />
        <el-table-column prop="url" label="数据源连接" min-width="280" show-overflow-tooltip />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据源" />
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="数据源名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="数据源连接" prop="url">
          <el-input v-model="form.url" placeholder="请输入数据源连接 URL" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
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
</style>
