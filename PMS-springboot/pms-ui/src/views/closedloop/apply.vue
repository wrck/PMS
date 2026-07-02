<template>
  <div>
    <el-card>
      <template #header><span>发起闭环申请</span></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目编码" prop="projectCode">
          <el-input v-model="form.projectCode" disabled />
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" disabled />
        </el-form-item>
        <el-form-item label="申请类型" prop="applyType">
          <el-select v-model="form.applyType" style="width:100%">
            <el-option label="PM申请闭环" value="PM" />
            <el-option label="SM审核" value="SM" />
            <el-option label="CB回访确认" value="CB" />
            <el-option label="CL工程确认" value="CL" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批意见" prop="comment">
          <el-input v-model="form.comment" type="textarea" :rows="4" placeholder="请输入审批意见" />
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
import { applyClosedLoop } from '@/api/closedloop'
import { getProject } from '@/api/project'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const projectId = route.query.projectId
const formRef = ref(null)
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const form = reactive({ projectId, projectCode: '', projectName: '', applyType: 'PM', comment: '', attachments: [] })
const rules = { applyType: [{ required: true, message: '请选择申请类型', trigger: 'change' }], comment: [{ required: true, message: '请输入审批意见', trigger: 'blur' }] }
onMounted(async () => { if (projectId) { const r = await getProject(projectId); form.projectCode = r.data?.projectCode || ''; form.projectName = r.data?.projectName || '' } })
const handleUpload = (res) => { if (res.code === 200) form.attachments.push(res.data) }
const handleSubmit = async () => { await formRef.value.validate(); await applyClosedLoop(form); ElMessage.success('申请已提交'); router.back() }
</script>
