<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createCodegenList,
  deleteCodegenTable,
  downloadCodegen,
  getCodegenTable,
  getCodegenTablePage,
  getDataSourceConfigList,
  getSchemaTableList,
  previewCodegen,
  syncCodegenFromDB,
  updateCodegenTable,
  type CodegenColumnRespVO,
  type CodegenPreviewVO,
  type CodegenTablePageReqVO,
  type CodegenTableRespVO,
  type CodegenTableSaveReqVO,
  type CodegenUpdateReqVO,
  type DatabaseTableVO,
  type DataSourceConfigRespVO
} from '@/api/yudao-infra'

defineOptions({ name: 'InfraCodegen' })

const loading = ref(false)
const tableData = ref<CodegenTableRespVO[]>([])
const total = ref(0)

const query = reactive<CodegenTablePageReqVO>({
  pageNo: 1,
  pageSize: 10,
  tableName: '',
  tableComment: '',
  createTime: undefined
})

/** 生成场景映射（infra_codegen_scene） */
const sceneOptions = [
  { value: 0, label: '管理后台' },
  { value: 1, label: '用户 APP' }
]

function sceneLabel(scene?: number): string {
  return sceneOptions.find((s) => s.value === scene)?.label ?? String(scene ?? '-')
}

function sceneTag(scene?: number): '' | 'primary' | 'success' {
  return scene === 1 ? 'success' : 'primary'
}

async function loadData() {
  loading.value = true
  try {
    const params: CodegenTablePageReqVO = { pageNo: query.pageNo, pageSize: query.pageSize }
    if (query.tableName) params.tableName = query.tableName
    if (query.tableComment) params.tableComment = query.tableComment
    if (query.createTime && query.createTime.length === 2) params.createTime = query.createTime
    const res = await getCodegenTablePage(params)
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
  query.tableName = ''
  query.tableComment = ''
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

// ============ 导入表弹窗 ============
const importVisible = ref(false)
const importLoading = ref(false)
const dbTableLoading = ref(false)
const dbTableList = ref<DatabaseTableVO[]>([])
const dataSourceList = ref<DataSourceConfigRespVO[]>([])
const importQuery = reactive<{ dataSourceConfigId: number; name: string }>({
  dataSourceConfigId: 0,
  name: ''
})
const checkedTables = ref<string[]>([])

async function openImport() {
  importVisible.value = true
  checkedTables.value = []
  importQuery.name = ''
  try {
    if (dataSourceList.value.length === 0) {
      dataSourceList.value = await getDataSourceConfigList()
    }
    if (dataSourceList.value.length > 0 && !importQuery.dataSourceConfigId) {
      importQuery.dataSourceConfigId = dataSourceList.value[0].id!
    }
    await loadDbTables()
  } catch {
    /* handled by interceptor */
  }
}

async function loadDbTables() {
  if (!importQuery.dataSourceConfigId) return
  dbTableLoading.value = true
  try {
    const params: { dataSourceConfigId: number; name?: string } = {
      dataSourceConfigId: importQuery.dataSourceConfigId
    }
    if (importQuery.name) params.name = importQuery.name
    dbTableList.value = await getSchemaTableList(params)
  } catch {
    /* handled by interceptor */
  } finally {
    dbTableLoading.value = false
  }
}

function handleSelectionChange(rows: DatabaseTableVO[]) {
  checkedTables.value = rows.map((r) => r.name)
}

async function handleImport() {
  if (checkedTables.value.length === 0) {
    ElMessage.warning('请选择要导入的表')
    return
  }
  importLoading.value = true
  try {
    await createCodegenList({
      dataSourceConfigId: importQuery.dataSourceConfigId,
      tableNames: checkedTables.value
    })
    ElMessage.success('导入成功')
    importVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    importLoading.value = false
  }
}

// ============ 预览弹窗 ============
const previewVisible = ref(false)
const previewLoading = ref(false)
const previewData = ref<CodegenPreviewVO[]>([])
const activeFilePath = ref('')

async function handlePreview(row: CodegenTableRespVO) {
  previewVisible.value = true
  previewLoading.value = true
  try {
    previewData.value = await previewCodegen(row.id)
    activeFilePath.value = previewData.value[0]?.filePath ?? ''
  } catch {
    /* handled by interceptor */
  } finally {
    previewLoading.value = false
  }
}

function fileNameOf(filePath: string): string {
  return filePath.substring(filePath.lastIndexOf('/') + 1)
}

// ============ 编辑弹窗（简化：仅基本信息） ============
const editVisible = ref(false)
const editLoading = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive<CodegenTableSaveReqVO>(createEmptyEditForm())
const editColumns = ref<CodegenColumnRespVO[]>([])

const editRules: FormRules = {
  tableName: [{ required: true, message: '请输入表名称', trigger: 'blur' }],
  tableComment: [{ required: true, message: '请输入表描述', trigger: 'blur' }],
  className: [{ required: true, message: '请输入实体类名称', trigger: 'blur' }]
}

function createEmptyEditForm(): CodegenTableSaveReqVO {
  return {
    id: 0,
    tableId: 0,
    isParentMenuIdValid: false,
    dataSourceConfigId: 0,
    scene: 0,
    tableName: '',
    tableComment: '',
    remark: '',
    moduleName: '',
    businessName: '',
    className: '',
    classComment: '',
    author: '',
    createTime: '',
    updateTime: '',
    templateType: 0,
    parentMenuId: 0
  }
}

async function handleEdit(row: CodegenTableRespVO) {
  editVisible.value = true
  editLoading.value = true
  try {
    const detail: CodegenUpdateReqVO = await getCodegenTable(row.id)
    editColumns.value = detail.columns ?? []
    Object.assign(editForm, detail.table)
  } catch {
    /* handled by interceptor */
  } finally {
    editLoading.value = false
  }
}

async function handleEditSubmit() {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    editSubmitting.value = true
    try {
      await updateCodegenTable({ table: editForm, columns: editColumns.value })
      ElMessage.success('更新成功')
      editVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      editSubmitting.value = false
    }
  })
}

// ============ 同步 / 删除 / 下载 ============
function handleSync(row: CodegenTableRespVO) {
  ElMessageBox.confirm(`确认要强制同步「${row.tableName}」表结构吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await syncCodegenFromDB(row.id)
      ElMessage.success('同步成功')
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleDelete(row: CodegenTableRespVO) {
  ElMessageBox.confirm(`确定删除表「${row.tableName}」的生成配置吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteCodegenTable(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

async function handleDownload(row: CodegenTableRespVO) {
  try {
    const blob = await downloadCodegen(row.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `codegen-${row.className || row.tableName}.zip`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch {
    /* handled by interceptor */
  }
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
      <template #header>代码生成</template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="表名称">
          <el-input
            v-model="query.tableName"
            placeholder="请输入表名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="表描述">
          <el-input
            v-model="query.tableComment"
            placeholder="请输入表描述"
            clearable
            style="width: 200px"
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
        <el-button type="primary" :icon="'Download'" @click="openImport">生成代码</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="tableName" label="表名称" min-width="180" />
        <el-table-column prop="tableComment" label="表描述" min-width="160" show-overflow-tooltip />
        <el-table-column prop="className" label="实体类" min-width="160" show-overflow-tooltip />
        <el-table-column label="生成场景" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="sceneTag(row.scene)" size="small">{{ sceneLabel(row.scene) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="340" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleSync(row)">同步</el-button>
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无代码生成配置" />
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

    <!-- 导入表弹窗 -->
    <el-dialog v-model="importVisible" title="导入表" width="800px" destroy-on-close>
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="数据源">
          <el-select
            v-model="importQuery.dataSourceConfigId"
            placeholder="请选择数据源"
            style="width: 200px"
            @change="loadDbTables"
          >
            <el-option
              v-for="ds in dataSourceList"
              :key="ds.id"
              :label="ds.name"
              :value="ds.id!"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表名称">
          <el-input
            v-model="importQuery.name"
            placeholder="请输入表名称"
            clearable
            style="width: 200px"
            @keyup.enter="loadDbTables"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="loadDbTables">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="dbTableLoading"
        :data="dbTableList"
        border
        stripe
        height="320"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="表名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="comment" label="表描述" min-width="200" show-overflow-tooltip />
        <template #empty>
          <el-empty description="暂无数据表" />
        </template>
      </el-table>

      <template #footer>
        <el-button @click="importVisible = false">关闭</el-button>
        <el-button
          type="primary"
          :loading="importLoading"
          :disabled="checkedTables.length === 0"
          @click="handleImport"
        >
          导入（{{ checkedTables.length }}）
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog
      v-model="previewVisible"
      title="代码预览"
      width="80%"
      top="5vh"
      destroy-on-close
    >
      <div v-loading="previewLoading">
        <el-tabs v-model="activeFilePath" v-if="previewData.length > 0">
          <el-tab-pane
            v-for="item in previewData"
            :key="item.filePath"
            :label="fileNameOf(item.filePath)"
            :name="item.filePath"
          >
            <div class="preview-toolbar">
              <span class="preview-path">{{ item.filePath }}</span>
            </div>
            <pre class="preview-code">{{ item.code }}</pre>
          </el-tab-pane>
        </el-tabs>
        <el-empty v-else description="暂无预览代码" />
      </div>
    </el-dialog>

    <!-- 编辑弹窗（基本信息） -->
    <el-dialog
      v-model="editVisible"
      title="编辑代码生成配置"
      width="640px"
      destroy-on-close
    >
      <div v-loading="editLoading">
        <el-form
          v-if="!editLoading"
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="110px"
        >
          <el-form-item label="表名称" prop="tableName">
            <el-input v-model="editForm.tableName" placeholder="请输入表名称" />
          </el-form-item>
          <el-form-item label="表描述" prop="tableComment">
            <el-input v-model="editForm.tableComment" placeholder="请输入表描述" />
          </el-form-item>
          <el-form-item label="实体类名称" prop="className">
            <el-input v-model="editForm.className" placeholder="请输入实体类名称" />
          </el-form-item>
          <el-form-item label="类描述" prop="classComment">
            <el-input v-model="editForm.classComment" placeholder="请输入类描述" />
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="editForm.author" placeholder="请输入作者" />
          </el-form-item>
          <el-form-item label="模块名" prop="moduleName">
            <el-input v-model="editForm.moduleName" placeholder="请输入模块名" />
          </el-form-item>
          <el-form-item label="业务名" prop="businessName">
            <el-input v-model="editForm.businessName" placeholder="请输入业务名" />
          </el-form-item>
          <el-form-item label="生成场景" prop="scene">
            <el-select v-model="editForm.scene" style="width: 100%">
              <el-option
                v-for="opt in sceneOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="editForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">确定</el-button>
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
.preview-toolbar {
  margin-bottom: 8px;
}
.preview-path {
  font-size: 12px;
  color: #909399;
  word-break: break-all;
}
.preview-code {
  max-height: 60vh;
  overflow: auto;
  padding: 12px;
  margin: 0;
  background-color: #1e1e1e;
  color: #e0e0e0;
  border-radius: 4px;
  font-family: 'Menlo', 'Consolas', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
