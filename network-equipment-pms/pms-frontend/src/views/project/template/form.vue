<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getTemplate,
  createTemplate,
  updateTemplate,
  publishVersion,
  type ProjectTemplate,
  type TemplateSnapshot,
  type PhaseDef
} from '@/api/project-template'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const publishing = ref(false)

const form = reactive<ProjectTemplate>({
  templateCode: '',
  templateName: '',
  category: 'IMPLEMENT',
  description: '',
  status: 'DRAFT'
})

// 快照构建：阶段列表
const phases = ref<PhaseDef[]>([])

// 发布版本表单
const publishDialogVisible = ref(false)
const publishForm = reactive({ version: '', changeLog: '' })

function addPhase() {
  phases.value.push({
    phaseCode: '',
    phaseName: '',
    sortOrder: phases.value.length + 1
  })
}

function removePhase(idx: number) {
  phases.value.splice(idx, 1)
}

async function loadTemplate(id: number) {
  loading.value = true
  try {
    const res = await getTemplate(id)
    Object.assign(form, res)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!form.templateCode || !form.templateName) {
    ElMessage.warning('请填写模板编码和名称')
    return
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateTemplate(form)
      ElMessage.success('更新成功')
    } else {
      await createTemplate(form)
      ElMessage.success('创建成功')
      router.back()
    }
  } finally {
    submitting.value = false
  }
}

async function handlePublish() {
  if (phases.value.length === 0) {
    ElMessage.warning('请至少添加一个阶段')
    return
  }
  if (!publishForm.version) {
    ElMessage.warning('请填写版本号')
    return
  }
  publishing.value = true
  try {
    const snapshot: TemplateSnapshot = { phases: phases.value }
    await publishVersion(form.id!, {
      version: publishForm.version,
      snapshot,
      changeLog: publishForm.changeLog
    })
    ElMessage.success('版本发布成功')
    publishDialogVisible.value = false
    if (form.id) loadTemplate(form.id)
  } finally {
    publishing.value = false
  }
}

onMounted(() => {
  const id = route.params.id as string | undefined
  if (id) loadTemplate(Number(id))
})
</script>

<template>
  <div class="page-container">
    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="page-title">{{ form.id ? '编辑模板' : '新建模板' }}</span>
      </template>

      <el-form :model="form" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板编码" required>
              <el-input v-model="form.templateCode" :disabled="!!form.id" placeholder="如 TPL-IMPL-STD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板名称" required>
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类别">
              <el-select v-model="form.category" style="width: 100%">
                <el-option label="实施" value="IMPLEMENT" />
                <el-option label="维护" value="MAINTENANCE" />
                <el-option label="咨询" value="CONSULTING" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag>{{ form.status }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">阶段定义（快照构建器）</el-divider>
      <div v-for="(phase, idx) in phases" :key="idx" class="phase-row">
        <el-input v-model="phase.phaseCode" placeholder="阶段编码 PREPARE" style="width: 180px" />
        <el-input v-model="phase.phaseName" placeholder="阶段名称" style="width: 220px" />
        <el-input-number v-model="phase.sortOrder" :min="1" placeholder="排序" style="width: 120px" />
        <el-button link type="danger" @click="removePhase(idx)">删除</el-button>
      </div>
      <el-button :icon="'Plus'" @click="addPhase">添加阶段</el-button>

      <div class="toolbar" style="margin-top: 20px">
        <el-button @click="router.back()">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button v-if="form.id" type="success" @click="publishDialogVisible = true">发布新版本</el-button>
      </div>
    </el-card>

    <el-dialog v-model="publishDialogVisible" title="发布新版本" width="500px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="版本号" required>
          <el-input v-model="publishForm.version" placeholder="如 v1.0.0" />
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input v-model="publishForm.changeLog" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.phase-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}
</style>
