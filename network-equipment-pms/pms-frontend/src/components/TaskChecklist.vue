<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createChecklist,
  deleteChecklist,
  listChecklist,
  toggleCheck,
  updateChecklist,
  type TaskChecklistItem
} from '@/api/task-checklist'

defineOptions({ name: 'TaskChecklist' })

const props = defineProps<{
  /** 任务ID */
  taskId: number
  /** 是否只读（如任务已完成时） */
  readonly?: boolean
}>()

const emit = defineEmits<{
  /** 强制检查项勾选状态变化时通知父组件，便于禁用「提交评审」按钮 */
  (e: 'mandatory-change', hasUncheckedMandatory: boolean): void
}>()

const loading = ref(false)
const list = ref<TaskChecklistItem[]>([])

// ============ 表单弹窗 ============
const dialogVisible = ref(false)
const dialogTitle = ref('新增检查项')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive<TaskChecklistItem>({
  id: undefined,
  taskId: 0,
  title: '',
  description: '',
  mandatory: false,
  checked: false,
  checkedBy: undefined,
  checkedAt: undefined,
  sortOrder: 0
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入检查项标题', trigger: 'blur' },
    { max: 128, message: '标题长度不能超过 128 字符', trigger: 'blur' }
  ],
  description: [{ max: 500, message: '描述长度不能超过 500 字符', trigger: 'blur' }]
}

// ============ 派生状态 ============
const hasUncheckedMandatory = computed(() =>
  list.value.some((item) => item.mandatory && !item.checked)
)

const totalMandatory = computed(() => list.value.filter((i) => i.mandatory).length)
const checkedMandatory = computed(
  () => list.value.filter((i) => i.mandatory && i.checked).length
)

// ============ 数据加载 ============
async function loadData() {
  loading.value = true
  try {
    list.value = await listChecklist(props.taskId)
    emitMandatoryChange()
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function emitMandatoryChange() {
  emit('mandatory-change', hasUncheckedMandatory.value)
}

// ============ 行内勾选 ============
async function handleToggle(row: TaskChecklistItem, val: boolean) {
  if (!row.id) return
  try {
    const updated = await toggleCheck(row.id, val)
    Object.assign(row, updated)
    emitMandatoryChange()
    ElMessage.success(val ? '已勾选' : '已取消勾选')
  } catch {
    // 失败时回滚 checkbox 状态
    row.checked = !val
  }
}

// ============ 新增 / 编辑 ============
function resetForm() {
  Object.assign(form, {
    id: undefined,
    taskId: props.taskId,
    title: '',
    description: '',
    mandatory: false,
    checked: false,
    checkedBy: undefined,
    checkedAt: undefined,
    sortOrder: list.value.length
  })
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增检查项'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: TaskChecklistItem) {
  isEdit.value = true
  dialogTitle.value = '编辑检查项'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value && form.id) {
        await updateChecklist({ ...form, taskId: props.taskId })
        ElMessage.success('更新成功')
      } else {
        await createChecklist({ ...form, taskId: props.taskId })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      await loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

// ============ 删除 ============
function handleDelete(row: TaskChecklistItem) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除检查项「${row.title}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteChecklist(row.id!)
      ElMessage.success('删除成功')
      await loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="task-checklist">
    <div class="checklist-header">
      <div class="checklist-summary">
        <span>
          强制检查项：<strong>{{ checkedMandatory }} / {{ totalMandatory }}</strong>
        </span>
        <el-tag v-if="hasUncheckedMandatory" type="danger" size="small" effect="plain">
          存在未完成的强制检查项
        </el-tag>
        <el-tag v-else-if="totalMandatory > 0" type="success" size="small" effect="plain">
          强制检查项已全部完成
        </el-tag>
      </div>
      <el-button v-if="!readonly" type="primary" :icon="'Plus'" size="small" @click="handleAdd">
        新增检查项
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe row-key="id">
      <el-table-column label="" width="60" align="center">
        <template #default="{ row }">
          <el-checkbox
            v-model="row.checked"
            :disabled="readonly"
            @change="(val: boolean) => handleToggle(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="检查项" min-width="220">
        <template #default="{ row }">
          <span :class="{ 'mandatory-unchecked': row.mandatory && !row.checked }">
            {{ row.title }}
          </span>
          <el-tag
            v-if="row.mandatory"
            type="danger"
            size="small"
            effect="plain"
            class="ml-2"
          >
            强制
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column label="勾选信息" width="200">
        <template #default="{ row }">
          <span v-if="row.checkedAt">
            {{ row.checkedAt?.slice(0, 16)?.replace('T', ' ') }}
          </span>
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!readonly" label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无检查项" />
      </template>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入检查项标题" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="可选，最长 500 字符"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="是否强制">
          <el-switch v-model="form.mandatory" />
          <span class="form-hint">强制项在提交评审前必须勾选</span>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" />
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
.task-checklist {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.checklist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.checklist-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}
/* 强制检查项未勾选时高亮红色（Story 3 验收 1 视觉提示） */
.mandatory-unchecked {
  color: var(--el-color-danger);
  font-weight: 600;
}
.ml-2 {
  margin-left: 8px;
}
.form-hint {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}
.text-muted {
  color: #c0c4cc;
}
</style>
