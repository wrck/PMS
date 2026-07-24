<script setup lang="ts">
// =============================================================================
// ProjectTemplateSelector - 从模板创建项目对话框
// -----------------------------------------------------------------------------
// 风格与「新建项目」弹窗（ProjectList handleAdd）统一：
//   - 字段集合对齐 Project 实体（projectType/priority/customerContact 等）
//   - 项目经理使用 UserSelect 主档选择器（禁止手输数字 ID）
//   - 优先级使用字符串枚举 HIGH/NORMAL/LOW（对齐后端 VARCHAR(16)）
//   - 计划起止日期使用 daterange
//   - 日期/类型/优先级选项复用同一份常量
//   - 表单校验 required 标记统一
// =============================================================================
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  listTemplates,
  listTemplateVersions,
  createProjectFromTemplate,
  type ProjectCreateFromTemplateDTO,
  type ProjectTemplate,
  type ProjectTemplateVersion
} from '@/api/project-template'
import { type ProjectPriority, type ProjectType } from '@/api/project'
import UserSelect from '@/components/common/UserSelect.vue'

const emit = defineEmits<{
  success: [projectId: number]
}>()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const templates = ref<ProjectTemplate[]>([])
const versions = ref<ProjectTemplateVersion[]>([])
const selectedTemplateId = ref<number>()
const selectedVersionId = ref<number>()

// ============== 选项常量（与 ProjectList 弹窗保持一致） ==============

const typeOptions: { value: ProjectType; label: string }[] = [
  { value: 'NETWORK_DEVICE', label: '网络设备' },
  { value: 'SECURITY', label: '安全设备' },
  { value: 'DATACENTER', label: '数据中心' }
]

const priorityOptions: { value: ProjectPriority; label: string }[] = [
  { value: 'HIGH', label: '高' },
  { value: 'NORMAL', label: '中' },
  { value: 'LOW', label: '低' }
]

// ============== 表单 ==============

function createEmptyForm(): ProjectCreateFromTemplateDTO {
  return {
    templateId: 0,
    versionId: 0,
    projectCode: '',
    projectName: '',
    projectType: 'NETWORK_DEVICE',
    customerName: '',
    customerContact: '',
    customerPhone: '',
    contractNo: '',
    contractAmount: undefined,
    planStartDate: '',
    planEndDate: '',
    projectManagerId: undefined,
    projectManagerName: '',
    priority: 'NORMAL',
    description: '',
    projectObjective: '',
    projectScope: '',
    members: [],
    configOverrides: {}
  }
}

const projectForm = reactive<ProjectCreateFromTemplateDTO>(createEmptyForm())
const dateRange = ref<[string, string] | null>(null)

const rules: FormRules = {
  projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

// ============== 数据加载 ==============

async function loadTemplates() {
  loading.value = true
  try {
    const res = await listTemplates({ page: 1, size: 100, status: 'PUBLISHED' })
    templates.value = res.records ?? []
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function onTemplateChange(templateId: number) {
  selectedVersionId.value = undefined
  versions.value = []
  if (!templateId) return
  const res = await listTemplateVersions(templateId, 1, 50)
  versions.value = (res.records ?? []).filter((v) => v.status === 'PUBLISHED')
  if (versions.value.length > 0) {
    selectedVersionId.value = versions.value[0].id
  }
}

function onManagerChange(user: { id: number; username: string; realName?: string } | null) {
  if (user) {
    projectForm.projectManagerId = user.id
    projectForm.projectManagerName = user.realName || user.username
  } else {
    projectForm.projectManagerId = undefined
    projectForm.projectManagerName = ''
  }
}

function handleDateRangeChange(val: [string, string] | null) {
  if (val && val.length === 2) {
    projectForm.planStartDate = val[0]
    projectForm.planEndDate = val[1]
  } else {
    projectForm.planStartDate = ''
    projectForm.planEndDate = ''
  }
}

// ============== 对话框开关 ==============

function open() {
  visible.value = true
  Object.assign(projectForm, createEmptyForm())
  selectedTemplateId.value = undefined
  selectedVersionId.value = undefined
  versions.value = []
  dateRange.value = null
  loadTemplates()
}

// ============== 提交 ==============

async function handleSubmit() {
  if (!selectedTemplateId.value || !selectedVersionId.value) {
    ElMessage.warning('请选择模板和版本')
    return
  }
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      projectForm.templateId = selectedTemplateId.value!
      projectForm.versionId = selectedVersionId.value!
      const res = await createProjectFromTemplate(projectForm)
      ElMessage.success('项目创建成功')
      visible.value = false
      emit('success', res.id)
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

defineExpose({ open })
</script>

<template>
  <el-dialog
    v-model="visible"
    title="从模板创建项目"
    width="780px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="projectForm"
      :rules="rules"
      label-width="110px"
      class="responsive-form"
    >
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="选择模板" required>
            <el-select
              v-model="selectedTemplateId"
              placeholder="请选择已发布模板"
              style="width: 100%"
              :loading="loading"
              filterable
              @change="onTemplateChange"
            >
              <el-option
                v-for="t in templates"
                :key="t.id"
                :label="`${t.templateName} (${t.templateCode})`"
                :value="t.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="模板版本" required>
            <el-select
              v-model="selectedVersionId"
              placeholder="请选择版本"
              style="width: 100%"
              :disabled="!selectedTemplateId"
            >
              <el-option
                v-for="v in versions"
                :key="v.id"
                :label="`${v.version} — ${v.changeLog || '无变更说明'}`"
                :value="v.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="项目编号" prop="projectCode">
            <el-input v-model="projectForm.projectCode" placeholder="如 IMPL-2026-001" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="项目名称" prop="projectName">
            <el-input v-model="projectForm.projectName" placeholder="请输入项目名称" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="项目类型" prop="projectType">
            <el-select v-model="projectForm.projectType" placeholder="请选择项目类型" style="width: 100%">
              <el-option
                v-for="opt in typeOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="projectForm.customerName" placeholder="请输入客户名称" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="客户联系人">
            <el-input v-model="projectForm.customerContact" placeholder="请输入客户联系人" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="客户电话">
            <el-input v-model="projectForm.customerPhone" placeholder="请输入客户电话" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="合同编号">
            <el-input v-model="projectForm.contractNo" placeholder="请输入合同编号" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="合同金额">
            <el-input-number
              v-model="projectForm.contractAmount"
              :min="0"
              :precision="2"
              :controls="false"
              style="width: 100%"
              placeholder="请输入合同金额"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="项目经理">
            <UserSelect
              v-model="projectForm.projectManagerId"
              placeholder="请搜索选择项目经理"
              @change="onManagerChange"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="优先级">
            <el-select v-model="projectForm.priority" placeholder="请选择优先级" style="width: 100%">
              <el-option
                v-for="opt in priorityOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="计划起止日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
              @change="handleDateRangeChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目目标">
            <el-input
              v-model="projectForm.projectObjective"
              type="textarea"
              :rows="2"
              placeholder="项目目标"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目范围">
            <el-input
              v-model="projectForm.projectScope"
              type="textarea"
              :rows="2"
              placeholder="项目范围"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目描述">
            <el-input
              v-model="projectForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入项目描述"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">创建</el-button>
    </template>
  </el-dialog>
</template>
