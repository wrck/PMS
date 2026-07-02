<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><span>任务办理</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="任务名称">{{ task.taskName }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ task.processName }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ task.assignee }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ task.createTime }}</el-descriptions-item>
        <el-descriptions-item label="到期时间">{{ task.dueDate }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ task.priority }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 业务表单 -->
    <el-card v-if="businessData" style="margin-bottom:16px">
      <template #header><span>业务信息</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item v-for="(value, key) in businessData" :key="key" :label="key">{{ value }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 流程图 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>流程图</span></template>
      <div style="text-align:center">
        <img v-if="processImage" :src="processImage" style="max-width:100%" />
        <el-empty v-else description="暂无流程图" />
      </div>
    </el-card>

    <!-- 审批历史 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>审批历史</span></template>
      <el-table :data="comments" stripe size="small">
        <el-table-column prop="commentPerson" label="审批人" width="100" />
        <el-table-column prop="result" label="结果" width="80">
          <template #default="{ row }"><el-tag :type="row.result==='同意'?'success':'danger'" size="small">{{ row.result }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="commentContent" label="意见" min-width="300" />
        <el-table-column prop="commentTime" label="时间" width="170" />
      </el-table>
    </el-card>

    <!-- 办理操作 -->
    <el-card>
      <template #header><span>办理操作</span></template>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio value="agree">同意</el-radio>
            <el-radio value="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="auditForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item label="转办">
          <el-input v-model="auditForm.assignee" placeholder="输入用户名转办（可选）" style="width:300px" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTaskDetail, getTaskForm, getTaskComments, getTaskProcessImage, completeTask, claimTask, delegateTask } from '@/api/workflow'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const taskId = route.params.id
const loading = ref(false)
const task = ref({})
const businessData = ref(null)
const processImage = ref(null)
const comments = ref([])
const auditForm = reactive({ result: 'agree', comment: '', assignee: '' })
onMounted(async () => {
  loading.value = true
  try {
    const t = await getTaskDetail(taskId); task.value = t.data || {}
    const f = await getTaskForm(taskId); businessData.value = f.data
    const c = await getTaskComments(taskId); comments.value = c.data || []
    const img = await getTaskProcessImage(taskId); if (img.data) processImage.value = URL.createObjectURL(img.data)
  } finally { loading.value = false }
})
const handleSubmit = async () => {
  if (auditForm.assignee) { await delegateTask({ taskId, assignee: auditForm.assignee }); ElMessage.success('已转办') }
  else { await completeTask({ taskId, ...auditForm }); ElMessage.success('办理完成') }
  router.back()
}
</script>
