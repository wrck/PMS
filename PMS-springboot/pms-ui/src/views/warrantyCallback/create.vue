<template>
  <div>
    <el-card>
      <template #header><span>{{ isEdit ? '编辑回访' : '新建回访' }}</span></template>
      <el-form :model="formData" label-width="100px" style="max-width:700px">
        <el-form-item label="项目名称" required>
          <el-input v-model="formData.projectName" disabled />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="formData.customerName" disabled />
        </el-form-item>
        <el-form-item label="回访方式" required>
          <el-select v-model="formData.callbackMethod" style="width:100%">
            <el-option label="电话回访" value="phone" /><el-option label="现场回访" value="onsite" /><el-option label="邮件回访" value="email" />
          </el-select>
        </el-form-item>
        <el-form-item label="满意度" required>
          <el-rate v-model="formData.satisfaction" :max="5" />
        </el-form-item>
        <el-form-item label="回访内容" required>
          <el-input v-model="formData.callbackContent" type="textarea" :rows="4" placeholder="请输入回访内容" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getWarrantyCallback, createWarrantyCallback, updateWarrantyCallback } from '@/api/warrantyCallback'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.query.id)
const formData = reactive({ id: null, projectName: '', customerName: '', callbackMethod: '', satisfaction: 3, callbackContent: '', remark: '' })
onMounted(async () => {
  if (route.query.id) { const r = await getWarrantyCallback(route.query.id); Object.assign(formData, r.data) }
})
const handleSubmit = async () => {
  if (!formData.callbackMethod || !formData.callbackContent) { ElMessage.warning('请填写必填项'); return }
  if (isEdit.value) await updateWarrantyCallback(formData)
  else await createWarrantyCallback(formData)
  ElMessage.success('提交成功'); router.push('/warrantyCallback')
}
</script>
