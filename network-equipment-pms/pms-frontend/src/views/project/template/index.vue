<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listTemplates,
  deleteTemplate,
  type ProjectTemplate
} from '@/api/project-template'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ProjectTemplate[]>([])
const total = ref(0)

const query = reactive({ page: 1, size: 10, templateName: '', category: '', status: '' })

const categoryOptions = [
  { value: 'IMPLEMENT', label: '实施' },
  { value: 'MAINTENANCE', label: '维护' },
  { value: 'CONSULTING', label: '咨询' }
]
const statusOptions = [
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'DEPRECATED', label: '已废弃' }
]

function getStatusTagType(status: string) {
  return { DRAFT: 'info', PUBLISHED: 'success', DEPRECATED: 'danger' }[status] ?? 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await listTemplates(query)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }
function handleReset() {
  query.templateName = ''; query.category = ''; query.status = ''
  query.page = 1; loadData()
}
function handlePageChange(p: number) { query.page = p; loadData() }
function handleSizeChange(s: number) { query.size = s; query.page = 1; loadData() }

function handleAdd() { router.push('/project/template/form') }
function handleEdit(row: ProjectTemplate) { router.push(`/project/template/form/${row.id}`) }
function handleVersion(row: ProjectTemplate) { router.push(`/project/template/version/${row.id}`) }

function handleDelete(row: ProjectTemplate) {
  if (!row.id) return
  ElMessageBox.confirm(`确认删除模板「${row.templateName}」吗？仅 DRAFT 状态可删除。`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTemplate(row.id!)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header><span class="page-title">项目模板</span></template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="模板名称">
          <el-input v-model="query.templateName" placeholder="请输入" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.category" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="opt in categoryOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新建模板</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="templateCode" label="模板编码" min-width="140" />
        <el-table-column prop="templateName" label="模板名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="类别" width="100">
          <template #default="{ row }">
            {{ categoryOptions.find(c => c.value === row.category)?.label ?? row.category }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status) as any">
              {{ statusOptions.find(s => s.value === row.status)?.label ?? row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleVersion(row)">版本管理</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无模板数据" /></template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>
