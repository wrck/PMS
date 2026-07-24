<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createGoViewProject,
  deleteGoViewProject,
  getGoViewProjectPage,
  publishGoViewProject,
  GOVIEW_STATUS_PUBLISHED,
  GOVIEW_STATUS_UNPUBLISHED,
  type GoViewProjectRespVO,
  type GoViewProjectSaveReqVO
} from '@/api/yudao-report'

defineOptions({ name: 'GoViewProject' })

// GoView 编辑器/预览器为外部应用，假设部署路径如下：
const GOVIEW_EDITOR_URL = '/go-view/project/edit?id='
const GOVIEW_PREVIEW_URL = '/go-view/project/preview?id='

const loading = ref(false)
const tableData = ref<GoViewProjectRespVO[]>([])
const total = ref(0)

const query = reactive<{ pageNo: number; pageSize: number; name: string; status?: number }>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<GoViewProjectSaveReqVO>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入大屏名称', trigger: 'blur' }]
}

function createEmptyForm(): GoViewProjectSaveReqVO {
  return {
    name: '',
    remark: ''
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getGoViewProjectPage(query)
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
  query.status = undefined
  query.pageNo = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新建大屏'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const id = await createGoViewProject(form)
      ElMessage.success('创建成功')
      dialogVisible.value = false
      loadData()
      // 创建成功后直接在新窗口打开编辑器进行设计
      window.open(GOVIEW_EDITOR_URL + id, '_blank')
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

function handleEdit(row: GoViewProjectRespVO) {
  window.open(GOVIEW_EDITOR_URL + row.id, '_blank')
}

function handlePreview(row: GoViewProjectRespVO) {
  if (row.status !== GOVIEW_STATUS_PUBLISHED) {
    ElMessage.warning('请先发布大屏再预览')
    return
  }
  window.open(GOVIEW_PREVIEW_URL + row.id, '_blank')
}

function handlePublish(row: GoViewProjectRespVO) {
  ElMessageBox.confirm(`确定发布大屏「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await publishGoViewProject(row.id)
      ElMessage.success('发布成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: GoViewProjectRespVO) {
  ElMessageBox.confirm(`确定删除大屏「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteGoViewProject(row.id)
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

function statusTagType(status: number): 'success' | 'info' {
  return status === GOVIEW_STATUS_PUBLISHED ? 'success' : 'info'
}

function statusLabel(status: number): string {
  return status === GOVIEW_STATUS_PUBLISHED ? '已发布' : '未发布'
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>GoView 数据大屏</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="大屏名称">
          <el-input v-model="query.name" placeholder="大屏名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="未发布" :value="GOVIEW_STATUS_UNPUBLISHED" />
            <el-option label="已发布" :value="GOVIEW_STATUS_PUBLISHED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建大屏</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="缩略图" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.thumbnail"
              :src="row.thumbnail"
              style="width: 80px; height: 45px"
              fit="cover"
              :preview-src-list="[row.thumbnail]"
              preview-teleported
            />
            <span v-else class="thumbnail-empty">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="大屏名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="creator" label="创建人" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="success" @click="handlePublish(row)">发布</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="大屏名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入大屏名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
.thumbnail-empty {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}
</style>
