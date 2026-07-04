<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, type UploadFile, type UploadFiles, type UploadUserFile } from 'element-plus'
import { Download, Upload, UploadFilled } from '@element-plus/icons-vue'
import {
  downloadExcel,
  uploadExcel,
  type ExcelImportError,
  type ExcelImportResult
} from '@/api/excel'

/**
 * 通用 Excel 导入导出组件。
 *
 * - 模板下载与导出走 axios `responseType: 'blob'`，再用 `URL.createObjectURL` + a 标签下载
 * - 导入用 el-upload `:auto-upload="false"`，选中后通过 :on-change 手动 axios FormData 上传
 * - 导入完成后展示 ExcelImportResult：成功数 + 错误表格（行号/数据/错误信息）
 * - 导入成功（即使有部分错误）后通过 `@refresh` 通知父组件刷新列表
 */

const props = withDefaults(
  defineProps<{
    /** 模板下载地址，例如 /api/asset/template */
    templateUrl?: string
    /** 上传地址，例如 /api/asset/import */
    importUrl?: string
    /** 导出地址，例如 /api/asset/export（可选） */
    exportUrl?: string
    /** 导出文件名（含扩展名，例如 asset-list.xlsx） */
    exportFileName?: string
    /** 模板下载文件名（含扩展名，例如 asset-template.xlsx） */
    templateFileName?: string
    /** 导出时附带的查询参数（可选） */
    exportParams?: Record<string, unknown>
  }>(),
  {
    templateUrl: '',
    importUrl: '',
    exportUrl: '',
    exportFileName: 'export.xlsx',
    templateFileName: 'template.xlsx'
  }
)

const emit = defineEmits<{
  /** 导入完成后通知父组件刷新列表（无论是否有错误行，只要有成功行就触发） */
  refresh: []
  /** 导入完成后的原始结果回传 */
  imported: [result: ExcelImportResult]
}>()

const uploading = ref(false)
const resultVisible = ref(false)
const importResult = ref<ExcelImportResult | null>(null)
/** el-upload 绑定的文件列表，用于在 :show-file-list 模式下控制显示与清理 */
const fileList = ref<UploadUserFile[]>([])

/** 下载模板 */
async function handleDownloadTemplate() {
  if (!props.templateUrl) {
    ElMessage.warning('未配置模板下载地址')
    return
  }
  try {
    await downloadExcel(props.templateUrl, undefined, props.templateFileName)
    ElMessage.success('模板下载成功')
  } catch (e) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '模板下载失败')
  }
}

/** 导出当前列表 */
async function handleExport() {
  if (!props.exportUrl) {
    ElMessage.warning('未配置导出地址')
    return
  }
  try {
    await downloadExcel(props.exportUrl, props.exportParams, props.exportFileName)
    ElMessage.success('导出成功')
  } catch (e) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '导出失败')
  }
}

/**
 * el-upload 的 :on-change 钩子。因为 :auto-upload=false 不会自动上传，
 * 在文件被选入时手动用 axios FormData 上传。仅在文件状态为 ready 时触发，
 * 避免对上传完成/移除等状态重复触发。
 */
async function handleFileChange(
  uploadFile: UploadFile,
  uploadFiles: UploadFiles
): Promise<void> {
  if (uploadFile.status !== 'ready') {
    return
  }
  if (!props.importUrl) {
    ElMessage.warning('未配置导入地址')
    fileList.value = []
    return
  }
  const raw = uploadFile.raw
  if (!raw) {
    return
  }
  if (!validateFile(raw)) {
    fileList.value = []
    return
  }
  uploading.value = true
  try {
    const result = await uploadExcel(props.importUrl, raw)
    importResult.value = result
    resultVisible.value = true
    const successCount = result.successList?.length ?? 0
    const errorCount = result.errors?.length ?? 0
    if (successCount > 0) {
      ElMessage.success(`导入完成：成功 ${successCount} 条${errorCount > 0 ? `，失败 ${errorCount} 条` : ''}`)
      emit('refresh')
    } else if (errorCount > 0) {
      ElMessage.warning(`导入失败：${errorCount} 条数据校验未通过`)
    } else {
      ElMessage.info('导入文件为空')
    }
    emit('imported', result)
  } catch (e) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message
    ElMessage.error(msg || '导入失败')
  } finally {
    uploading.value = false
    // 清空文件列表，避免 :show-file-list 模式下文件残留影响下次选择
    fileList.value = []
    void uploadFiles
  }
}

/**
 * 校验待上传文件：仅允许 .xlsx，且大小不超过 20MB。
 *
 * @param file 待校验文件
 */
function validateFile(file: File): boolean {
  const isExcel =
    file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
    file.name.toLowerCase().endsWith('.xlsx')
  if (!isExcel) {
    ElMessage.error('仅支持 .xlsx 格式文件')
    return false
  }
  const maxSize = 20 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 20MB')
    return false
  }
  return true
}

/** 关闭结果对话框 */
function handleCloseResult() {
  resultVisible.value = false
}

/** 获取错误表格数据 */
function errorRows(): ExcelImportError[] {
  return importResult.value?.errors ?? []
}
</script>

<template>
  <div class="excel-import-export">
    <el-button
      v-if="props.templateUrl"
      :icon="Download"
      @click="handleDownloadTemplate"
    >
      下载模板
    </el-button>
    <el-upload
      v-if="props.importUrl"
      v-model:file-list="fileList"
      class="upload-inline"
      :show-file-list="false"
      :auto-upload="false"
      :on-change="handleFileChange"
      accept=".xlsx"
    >
      <el-button :icon="Upload" :loading="uploading">导入</el-button>
    </el-upload>
    <el-button v-if="props.exportUrl" :icon="UploadFilled" @click="handleExport">
      导出
    </el-button>

    <!-- 导入结果对话框 -->
    <el-dialog
      v-model="resultVisible"
      title="导入结果"
      width="720px"
      destroy-on-close
      @close="handleCloseResult"
    >
      <div v-if="importResult" class="result-summary">
        <el-tag type="success" size="large">
          成功 {{ importResult.successList?.length ?? 0 }} 条
        </el-tag>
        <el-tag
          :type="(importResult.errors?.length ?? 0) > 0 ? 'danger' : 'info'"
          size="large"
        >
          失败 {{ importResult.errors?.length ?? 0 }} 条
        </el-tag>
      </div>
      <el-table
        v-if="errorRows().length > 0"
        :data="errorRows()"
        border
        stripe
        max-height="360"
        class="error-table"
      >
        <el-table-column prop="rowIndex" label="行号" width="80" align="center" />
        <el-table-column
          prop="rowData"
          label="行数据"
          min-width="240"
          show-overflow-tooltip
        />
        <el-table-column
          prop="errorMessage"
          label="错误信息"
          min-width="200"
          show-overflow-tooltip
        />
      </el-table>
      <el-empty
        v-else-if="importResult && (importResult.successList?.length ?? 0) > 0"
        description="全部数据导入成功"
      />
      <template #footer>
        <el-button type="primary" @click="handleCloseResult">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.excel-import-export {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.upload-inline {
  display: inline-block;
}

.upload-inline :deep(.el-upload) {
  display: inline-block;
}

.result-summary {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.error-table {
  width: 100%;
}
</style>
