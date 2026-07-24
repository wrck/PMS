<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createFileConfig,
  deleteFileConfig,
  getFileConfig,
  getFileConfigPage,
  testFileConfig,
  updateFileConfig,
  updateFileConfigMaster,
  type FileConfigPageReqVO,
  type FileConfigRespVO,
  type FileConfigSaveReqVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraFileConfig' })

const loading = ref(false)
const tableData = ref<FileConfigRespVO[]>([])
const total = ref(0)

const query = reactive<FileConfigPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  name: '',
  storage: undefined,
  createTime: undefined
})

/** 文件存储器类型映射（对应 DICT_TYPE.INFRA_FILE_STORAGE） */
const storageOptions = [
  { value: 0, label: '数据库', tag: 'info' as const },
  { value: 1, label: '本地存储', tag: 'success' as const },
  { value: 2, label: 'FTP', tag: 'info' as const },
  { value: 3, label: 'SFTP', tag: 'info' as const },
  { value: 4, label: 'S3', tag: 'primary' as const },
  { value: 11, label: '阿里云 OSS', tag: 'warning' as const },
  { value: 12, label: '腾讯云 COS', tag: 'warning' as const },
  { value: 13, label: '七牛云存储', tag: 'warning' as const },
  { value: 14, label: '华为云 OBS', tag: 'warning' as const },
  { value: 15, label: 'MinIO', tag: 'primary' as const },
  { value: 20, label: 'AWS S3', tag: 'primary' as const }
]

function storageLabel(storage?: number): string {
  return storageOptions.find((s) => s.value === storage)?.label ?? String(storage ?? '-')
}

function storageTag(storage?: number): '' | 'primary' | 'success' | 'info' | 'warning' | 'danger' {
  return storageOptions.find((s) => s.value === storage)?.tag ?? ''
}

async function loadData() {
  loading.value = true
  try {
    const params: FileConfigPageReqVO = { pageNo: query.pageNo, pageSize: query.pageSize }
    if (query.name) params.name = query.name
    if (query.storage !== undefined && query.storage !== null) params.storage = query.storage
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getFileConfigPage(params)
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
  query.storage = undefined
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

// ============ CRUD 弹窗 ============
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<FileConfigSaveReqVO & { configJson: string }>(createEmptyForm())

const rules: FormRules = {
  name: [{ required: true, message: '请输入配置名', trigger: 'blur' }],
  storage: [{ required: true, message: '请选择存储器', trigger: 'change' }]
}

function createEmptyForm(): FileConfigSaveReqVO & { configJson: string } {
  return {
    name: '',
    storage: 1,
    master: false,
    visible: true,
    remark: '',
    config: { basePath: '', domain: '' },
    configJson: '{\n  "basePath": "",\n  "domain": ""\n}'
  }
}

function handleAdd() {
  dialogTitle.value = '新增文件配置'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

async function handleEdit(row: FileConfigRespVO) {
  dialogTitle.value = '编辑文件配置'
  try {
    const detail = await getFileConfig(row.id)
    const configJson = JSON.stringify(detail.config ?? {}, null, 2)
    Object.assign(form, {
      id: detail.id,
      name: detail.name,
      storage: detail.storage,
      master: detail.master,
      visible: detail.visible,
      remark: detail.remark ?? '',
      config: detail.config,
      configJson
    })
    dialogVisible.value = true
  } catch {
    /* handled by interceptor */
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    // 校验 config JSON 格式
    try {
      form.config = JSON.parse(form.configJson)
    } catch {
      ElMessage.error('配置 JSON 格式不正确')
      return
    }
    submitting.value = true
    try {
      const payload: FileConfigSaveReqVO = {
        id: form.id,
        name: form.name,
        storage: form.storage,
        master: form.master,
        visible: form.visible,
        config: form.config,
        remark: form.remark
      }
      if (form.id) {
        await updateFileConfig(payload)
        ElMessage.success('更新成功')
      } else {
        await createFileConfig(payload)
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

// ============ 主配置 / 测试 / 删除 ============
function handleMaster(row: FileConfigRespVO) {
  ElMessageBox.confirm(`是否确认将配置「${row.name}」设为主配置？`, '提示', { type: 'warning' })
    .then(async () => {
      await updateFileConfigMaster(row.id)
      ElMessage.success('设置成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

const testingId = ref<number | null>(null)

async function handleTest(row: FileConfigRespVO) {
  testingId.value = row.id
  try {
    const url = await testFileConfig(row.id)
    ElMessageBox.confirm('是否要访问该文件？', '测试上传成功', { type: 'success' })
      .then(() => {
        window.open(url, '_blank')
      })
      .catch(() => {
        /* cancelled */
      })
  } catch {
    /* handled by interceptor */
  } finally {
    testingId.value = null
  }
}

function handleDelete(row: FileConfigRespVO) {
  ElMessageBox.confirm(`确定删除配置「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteFileConfig(row.id)
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
      <template #header>文件配置</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="配置名">
          <el-input
            v-model="query.name"
            placeholder="请输入配置名"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="存储器">
          <el-select
            v-model="query.storage"
            placeholder="全部"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="opt in storageOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
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
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增配置</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="配置名" min-width="140" />
        <el-table-column label="存储器" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="storageTag(row.storage)" size="small">
              {{ storageLabel(row.storage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主配置" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.master ? 'success' : 'info'" size="small">
              {{ row.master ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              link
              type="success"
              :disabled="row.master"
              @click="handleMaster(row)"
            >
              主配置
            </el-button>
            <el-button
              link
              type="primary"
              :loading="testingId === row.id"
              @click="handleTest(row)"
            >
              测试
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无文件配置" />
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置名" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名" />
        </el-form-item>
        <el-form-item label="存储器" prop="storage">
          <el-select v-model="form.storage" placeholder="请选择存储器" style="width: 100%">
            <el-option
              v-for="opt in storageOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否主配置" prop="master">
          <el-switch v-model="form.master" />
        </el-form-item>
        <el-form-item label="是否可见" prop="visible">
          <el-switch v-model="form.visible" />
        </el-form-item>
        <el-form-item label="配置(JSON)" prop="configJson">
          <el-input
            v-model="form.configJson"
            type="textarea"
            :rows="6"
            placeholder='请输入配置 JSON，如 {"basePath":"","domain":""}'
          />
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
