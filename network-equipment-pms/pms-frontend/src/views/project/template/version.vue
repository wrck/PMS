<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  listTemplateVersions,
  getTemplate,
  type ProjectTemplate,
  type ProjectTemplateVersion
} from '@/api/project-template'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const template = ref<ProjectTemplate>()
const tableData = ref<ProjectTemplateVersion[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

async function loadData() {
  const templateId = Number(route.params.id)
  if (!templateId) return
  loading.value = true
  try {
    template.value = await getTemplate(templateId)
    const res = await listTemplateVersions(templateId, page.value, size.value)
    tableData.value = res.records ?? []
    total.value = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function getStatusTagType(status: string) {
  return { DRAFT: 'info', PUBLISHED: 'success', ARCHIVED: 'warning' }[status] ?? 'info'
}

function handlePageChange(p: number) { page.value = p; loadData() }

function viewSnapshot(row: ProjectTemplateVersion) {
  // 简化：用 alert 展示 JSON（实际可用 ElMessageBox 或 Drawer）
  ElMessage.info(`版本 ${row.version} 快照查看：可扩展为 Drawer 展示`)
  console.log('Snapshot:', row.snapshotJson)
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="page-title">版本管理 — {{ template?.templateName }}</span>
      </template>

      <div class="toolbar">
        <el-button @click="router.push('/project/template')">返回列表</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="changeLog" label="变更说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status) as any">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="160" />
        <el-table-column prop="publishedBy" label="发布人ID" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewSnapshot(row)">查看快照</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无版本记录" /></template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="page"
        :page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>
