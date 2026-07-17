<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listTemplates,
  listTemplateVersions,
  createProjectFromTemplate,
  type ProjectTemplate,
  type ProjectTemplateVersion,
  type ProjectCreateFromTemplateDTO
} from '@/api/project-template'

const emit = defineEmits<{
  success: [projectId: number]
}>()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)

const templates = ref<ProjectTemplate[]>([])
const versions = ref<ProjectTemplateVersion[]>([])
const selectedTemplateId = ref<number>()
const selectedVersionId = ref<number>()

const projectForm = reactive<ProjectCreateFromTemplateDTO>({
  templateId: 0,
  versionId: 0,
  projectCode: '',
  projectName: '',
  customerName: '',
  planStartDate: '',
  planEndDate: '',
  projectManagerId: undefined,
  projectObjective: '',
  projectScope: '',
  members: [],
  configOverrides: {}
})

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

function open() {
  visible.value = true
  Object.assign(projectForm, {
    templateId: 0,
    versionId: 0,
    projectCode: '',
    projectName: '',
    customerName: '',
    planStartDate: '',
    planEndDate: '',
    projectManagerId: undefined,
    projectObjective: '',
    projectScope: '',
    members: [],
    configOverrides: {}
  })
  loadTemplates()
}

async function handleSubmit() {
  if (!selectedTemplateId.value || !selectedVersionId.value) {
    ElMessage.warning('请选择模板和版本')
    return
  }
  if (!projectForm.projectCode || !projectForm.projectName) {
    ElMessage.warning('请填写项目编号和名称')
    return
  }
  submitting.value = true
  try {
    projectForm.templateId = selectedTemplateId.value
    projectForm.versionId = selectedVersionId.value
    const res = await createProjectFromTemplate(projectForm)
    ElMessage.success('项目创建成功')
    visible.value = false
    emit('success', (res as any).id)
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<template>
  <el-dialog v-model="visible" title="从模板创建项目" width="720px" destroy-on-close>
    <el-form :model="projectForm" label-width="110px">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="选择模板" required>
            <el-select
              v-model="selectedTemplateId"
              placeholder="请选择已发布模板"
              style="width: 100%"
              :loading="loading"
              @change="onTemplateChange"
            >
              <el-option
                v-for="t in templates"
                :key="t.id"
                :label="`${t.templateName} (${t.templateCode})`"
                :value="t.id"
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
                :value="v.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目编号" required>
            <el-input v-model="projectForm.projectCode" placeholder="如 IMPL-2026-001" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目名称" required>
            <el-input v-model="projectForm.projectName" placeholder="请输入项目名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称">
            <el-input v-model="projectForm.customerName" placeholder="请输入客户名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目经理ID">
            <el-input-number
              v-model="projectForm.projectManagerId"
              :min="1"
              style="width: 100%"
              placeholder="请输入用户ID"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划开始日期">
            <el-date-picker
              v-model="projectForm.planStartDate"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划结束日期">
            <el-date-picker
              v-model="projectForm.planEndDate"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
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
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">创建</el-button>
    </template>
  </el-dialog>
</template>
