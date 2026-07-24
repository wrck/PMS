<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import {
  deleteFile,
  getFilePage,
  uploadFile,
  type FilePageReqVO,
  type FileRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraFile' })

const loading = ref(false)
const tableData = ref<FileRespVO[]>([])
const total = ref(0)

const query = reactive<FilePageReqVO>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  type: '',
  createTime: undefined
})

const uploadVisible = ref(false)
const uploading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params: FilePageReqVO = { pageNo: query.pageNo, pageSize: query.pageSize }
    if (query.name) params.name = query.name
    if (query.type) params.type = query.type
    if (query.createTime && query.createTime.length === 2) {
      params.createTime = query.createTime
    }
    const res = await getFilePage(params)
    tableData.value = res?.list ?? []
    total.value = res?.total ?? 0
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
  query.type = ''
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

function handleUpload() {
  uploadVisible.value = true
}

/** 自定义上传：调用 uploadFile */
async function customUpload(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    await uploadFile(formData)
    ElMessage.success('上传成功')
    uploadVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    uploading.value = false
  }
}

function handleDelete(row: FileRespVO) {
  ElMessageBox.confirm(`确定删除文件「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteFile(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

/** 文件大小格式化 */
function formatFileSize(size: number): string {
  if (!size) return '-'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024).toFixed(2) + ' MB'
  return (size / 1024 / 1024 / 1024).toFixed(2) + ' GB'
}

function isImage(type?: string): boolean {
  return !!type && type.toLowerCase().includes('image')
}

function isPdf(type?: string): boolean {
  return !!type && type.toLowerCase().includes('pdf')
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
      <template #header>文件管理</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="文件名">
          <el-input
            v-model="query.name"
            placeholder="请输入文件名"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-input
            v-model="query.type"
            placeholder="请输入文件类型"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="query.createTime"
            type="daterange"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Upload'" @click="handleUpload">文件上传</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="name" label="文件名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="path" label="文件路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="url" label="URL" min-width="220" show-overflow-tooltip />
        <el-table-column label="文件大小" width="110" align="center">
          <template #default="{ row }">{{ formatFileSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="type" label="文件类型" min-width="140" show-overflow-tooltip />
        <el-table-column label="文件内容" width="110" align="center">
          <template #default="{ row }">
            <el-image
              v-if="isImage(row.type)"
              style="width: 60px; height: 60px"
              lazy
              fit="cover"
              :src="row.url"
              :preview-src-list="[row.url]"
              preview-teleported
            />
            <el-link v-else-if="isPdf(row.type)" type="primary" :href="row.url" target="_blank" :underline="false">
              预览
            </el-link>
            <el-link v-else type="primary" :href="row.url" target="_blank" download :underline="false">
              下载
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无文件" />
        </template>
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

    <!-- 上传弹窗 -->
    <el-dialog v-model="uploadVisible" title="文件上传" width="480px" destroy-on-close>
      <el-upload
        drag
        :auto-upload="true"
        :show-file-list="false"
        :http-request="customUpload"
        :disabled="uploading"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">支持上传任意类型文件</div>
        </template>
      </el-upload>
      <div v-if="uploading" class="upload-tip">上传中...</div>
      <template #footer>
        <el-button @click="uploadVisible = false">关闭</el-button>
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
.upload-tip {
  margin-top: 8px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}
</style>
