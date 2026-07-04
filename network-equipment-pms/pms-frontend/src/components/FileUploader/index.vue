<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document, Close } from '@element-plus/icons-vue'
import axios from 'axios'

/** 附件信息 */
interface Attachment {
  id: number
  fileName: string
  fileSize: number
  mimeType: string
  uploadTime: string
  geoFenceStatus?: string
}

const props = withDefaults(
  defineProps<{
    /** 业务类型 */
    bizType: string
    /** 业务 ID（可选，新建场景可为空） */
    bizId?: number
    /** 接受的文件类型，例如 'image/*' */
    accept?: string
    /** 是否允许多选 */
    multiple?: boolean
    /** 最大文件大小（MB） */
    maxSize?: number
  }>(),
  {
    accept: '',
    multiple: false,
    maxSize: 20
  }
)

const emit = defineEmits<{
  success: [file: Attachment]
  remove: [id: number]
  change: [files: Attachment[]]
}>()

/** 已上传文件列表 */
const fileList = ref<Attachment[]>([])
/** 是否正在上传 */
const uploading = ref(false)
/** 上传进度（0-100） */
const progress = ref(0)
/** 拖拽悬浮状态 */
const dragging = ref(false)
/** 文件输入框引用 */
const fileInput = ref<HTMLInputElement | null>(null)

/** token */
const token = computed(() => localStorage.getItem('pms_token') || '')

/** 文件大小限制（字节） */
const maxBytes = computed(() => props.maxSize * 1024 * 1024)

/** 判断是否图片 */
function isImage(mimeType: string): boolean {
  return mimeType.startsWith('image/')
}

/** 缩略图 URL */
function thumbnailUrl(file: Attachment): string {
  return `/api/file/${file.id}/thumbnail`
}

/** 格式化文件大小 */
function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}

/** 校验文件 */
function validate(file: File): boolean {
  if (file.size > maxBytes.value) {
    ElMessage.error(`文件 ${file.name} 超过最大限制 ${props.maxSize}MB`)
    return false
  }
  return true
}

/** 执行单个文件上传 */
async function uploadOne(file: File): Promise<Attachment> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('bizType', props.bizType)
  if (props.bizId !== undefined) {
    formData.append('bizId', String(props.bizId))
  }

  const res = await axios.post('/api/file/upload', formData, {
    headers: {
      Authorization: `Bearer ${token.value}`
    },
    onUploadProgress: (e) => {
      if (e.total) {
        progress.value = Math.round((e.loaded * 100) / e.total)
      }
    }
  })
  // 后端返回统一信封：{ code, message, data }
  const payload = res.data as { data?: Attachment } & Partial<Attachment>
  const data = payload?.data ?? (payload as unknown as Attachment)
  return data
}

/** 处理文件选择/拖拽 */
async function handleFiles(files: File | File[]) {
  const list = Array.isArray(files) ? files : [files]
  if (!props.multiple && list.length > 1) {
    ElMessage.warning('当前不支持多文件，仅上传第一个')
  }
  uploading.value = true
  progress.value = 0
  try {
    for (const f of list) {
      if (!validate(f)) continue
      const att = await uploadOne(f)
      fileList.value.push(att)
      emit('success', att)
    }
    emit('change', fileList.value)
  } catch (e) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '上传失败')
  } finally {
    uploading.value = false
    progress.value = 0
  }
}

/** input change 事件 */
function onInputChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files.length) {
    handleFiles(Array.from(input.files))
    // 清空 input 以便重复选择同一文件
    input.value = ''
  }
}

/** 触发文件选择 */
function triggerSelect() {
  fileInput.value?.click()
}

/** 拖拽事件 */
function onDrop(e: DragEvent) {
  dragging.value = false
  if (e.dataTransfer?.files?.length) {
    handleFiles(Array.from(e.dataTransfer.files))
  }
}

function onDragOver() {
  dragging.value = true
}

function onDragLeave() {
  dragging.value = false
}

/** 删除已上传文件 */
async function handleRemove(file: Attachment, event: MouseEvent) {
  event.stopPropagation()
  try {
    await axios.delete(`/api/file/${file.id}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    const idx = fileList.value.findIndex((f) => f.id === file.id)
    if (idx !== -1) fileList.value.splice(idx, 1)
    emit('remove', file.id)
    emit('change', fileList.value)
  } catch (e) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '删除失败')
  }
}
</script>

<template>
  <div class="file-uploader">
    <div
      class="drop-zone"
      :class="{ dragging }"
      @dragover.prevent="onDragOver"
      @dragleave.prevent="onDragLeave"
      @drop.prevent="onDrop"
      @click="triggerSelect"
    >
      <el-icon class="drop-icon"><UploadFilled /></el-icon>
      <div class="drop-text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="drop-tip">
        支持 {{ accept || '所有' }} 类型，单个文件不超过 {{ maxSize }}MB
      </div>
      <input
        ref="fileInput"
        type="file"
        class="file-input"
        :accept="accept"
        :multiple="multiple"
        @change="onInputChange"
      />
    </div>

    <el-progress
      v-if="uploading"
      :percentage="progress"
      :stroke-width="6"
      class="upload-progress"
    />

    <ul v-if="fileList.length" class="file-list">
      <li v-for="file in fileList" :key="file.id" class="file-item">
        <div class="file-thumb">
          <img
            v-if="isImage(file.mimeType)"
            :src="thumbnailUrl(file)"
            :alt="file.fileName"
          />
          <el-icon v-else><Document /></el-icon>
        </div>
        <div class="file-info">
          <div class="file-name" :title="file.fileName">{{ file.fileName }}</div>
          <div class="file-meta">
            <span>{{ formatSize(file.fileSize) }}</span>
            <span v-if="file.uploadTime" class="file-time">{{ file.uploadTime }}</span>
            <el-tag
              v-if="file.geoFenceStatus"
              size="small"
              type="warning"
              effect="plain"
            >
              {{ file.geoFenceStatus }}
            </el-tag>
          </div>
        </div>
        <el-icon class="remove-icon" @click="handleRemove(file, $event)">
          <Close />
        </el-icon>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.file-uploader {
  width: 100%;
}

.drop-zone {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  background-color: #fafafa;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.2s, background-color 0.2s;
  position: relative;
}

.drop-zone:hover,
.drop-zone.dragging {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.drop-icon {
  font-size: 40px;
  color: #c0c4cc;
  margin-bottom: 8px;
}

.drop-text {
  font-size: 14px;
  color: #606266;
}

.drop-text em {
  color: #409eff;
  font-style: normal;
}

.drop-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.file-input {
  display: none;
}

.upload-progress {
  margin-top: 8px;
}

.file-list {
  list-style: none;
  margin: 12px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
}

.file-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.file-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-thumb .el-icon {
  font-size: 22px;
  color: #909399;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.file-time {
  color: #c0c4cc;
}

.remove-icon {
  font-size: 16px;
  color: #f56c6c;
  cursor: pointer;
  flex-shrink: 0;
}

.remove-icon:hover {
  color: #d9363e;
}
</style>
