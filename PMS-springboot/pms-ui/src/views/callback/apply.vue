<template>
  <div>
    <el-card>
      <template #header><span>发起回访申请</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" disabled />
        </el-form-item>
        <el-form-item label="回访类型" prop="callbackType">
          <el-select v-model="form.callbackType" style="width:100%">
            <el-option v-for="t in callbackTypeList" :key="t.basicDataId" :label="t.basicDataName" :value="t.basicDataId" />
          </el-select>
        </el-form-item>
        <el-form-item label="回访说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入回访说明" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleUpload" :file-list="form.attachments" multiple>
            <el-button size="small">上传附件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交申请</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { applyCallback } from '@/api/callback'
import { getProject } from '@/api/project'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const projectId = route.query.projectId
const resubmitId = route.query.resubmit
const formRef = ref(null)
const callbackTypeList = ref([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const form = reactive({ projectId, projectName: '', callbackType: '', remark: '', attachments: [] })
const rules = { callbackType: [{ required: true, message: '请选择回访类型', trigger: 'change' }], remark: [{ required: true, message: '请输入回访说明', trigger: 'blur' }] }
onMounted(async () => { if (projectId) { const r = await getProject(projectId); form.projectName = r.data?.projectName || '' } })
const handleUpload = (res) => { if (res.code === 200) form.attachments.push(res.data) }
const handleSubmit = async () => { await formRef.value.validate(); await applyCallback(form); ElMessage.success('申请已提交'); router.back() }
</script>
