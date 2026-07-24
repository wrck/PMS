<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createConfig,
  deleteConfig,
  getConfigPage,
  updateConfig,
  type ConfigRespVO,
  type ConfigSaveReqVO
} from '@/api/yudao-infra'

const loading = ref(false)
const tableData = ref<ConfigRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string; key: string }>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  key: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<ConfigSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  category: [{ required: true, message: '请输入参数分类', trigger: 'blur' }],
  name: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  key: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  value: [{ required: true, message: '请输入参数键值', trigger: 'blur' }]
}

function createEmptyForm(): ConfigSaveReqVO {
  return {
    category: '',
    name: '',
    key: '',
    value: '',
    visible: true,
    remark: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getConfigPage(query)
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
  query.key = ''
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增参数'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: ConfigRespVO) {
  dialogTitle.value = '编辑参数'
  Object.assign(form, {
    id: row.id,
    category: row.category,
    name: row.name,
    key: row.key,
    value: row.value,
    visible: row.visible,
    remark: row.remark ?? ''
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
        await updateConfig(form)
        ElMessage.success('更新成功')
      } else {
        await createConfig(form)
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

function handleDelete(row: ConfigRespVO) {
  ElMessageBox.confirm(`确定删除参数「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteConfig(row.id)
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

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>参数配置</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="参数名称">
          <el-input v-model="query.name" placeholder="参数名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="参数键名">
          <el-input v-model="query.key" placeholder="参数键名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增参数</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="参数名称" min-width="140" />
        <el-table-column prop="key" label="参数键名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="value" label="参数键值" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="参数分类" min-width="120" />
        <el-table-column label="是否可见" width="100">
          <template #default="{ row }">
            <el-tag :type="row.visible ? 'success' : 'info'">
              {{ row.visible ? '可见' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="参数分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入参数分类" />
        </el-form-item>
        <el-form-item label="参数名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="key">
          <el-input v-model="form.key" :disabled="!!form.id" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="value">
          <el-input v-model="form.value" type="textarea" :rows="3" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="是否可见" prop="visible">
          <el-switch v-model="form.visible" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
