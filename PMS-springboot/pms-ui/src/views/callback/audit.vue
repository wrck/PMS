<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><span>回访审批</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="回访类型">{{ data.callbackType }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="回访说明" :span="2">{{ data.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 问卷区域 -->
    <el-card v-if="questionnaire" style="margin-bottom:16px">
      <template #header><span>回访问卷</span></template>
      <div v-for="(section, sIdx) in questionnaire.sections" :key="sIdx" style="margin-bottom:16px">
        <h4>{{ section.title }}</h4>
        <div v-for="(question, qIdx) in section.questions" :key="qIdx" style="margin-bottom:12px">
          <p>{{ qIdx + 1 }}. {{ question.content }}</p>
          <el-radio-group v-if="question.type === 'radio'" v-model="question.answer">
            <el-radio v-for="opt in question.options" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio>
          </el-radio-group>
          <el-input v-else-if="question.type === 'textarea'" v-model="question.answer" type="textarea" :rows="2" />
          <el-input v-else v-model="question.answer" />
        </div>
      </div>
      <div style="margin-top:16px">
        <strong>总分：{{ totalScore }}</strong>
      </div>
    </el-card>

    <!-- 审批区域 -->
    <el-card>
      <template #header><span>审批意见</span></template>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio value="agree">同意</el-radio>
            <el-radio value="reject">驳回</el-radio>
            <el-radio value="cannot">无法回访</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="auditForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAudit">提交审批</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCallback, auditCallback, getQuestionnaire, saveQuestionnaire } from '@/api/callback'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const questionnaire = ref(null)
const auditForm = reactive({ result: 'agree', comment: '' })
const totalScore = computed(() => {
  if (!questionnaire.value) return 0
  let score = 0
  questionnaire.value.sections?.forEach(s => s.questions?.forEach(q => { if (q.answer && q.score) score += Number(q.score) }))
  return score
})
onMounted(async () => {
  loading.value = true
  try {
    const r = await getCallback(id); data.value = r.data || {}
    const q = await getQuestionnaire(id); questionnaire.value = q.data
  } finally { loading.value = false }
})
const handleAudit = async () => {
  if (questionnaire.value) await saveQuestionnaire({ callbackId: id, answers: questionnaire.value, score: totalScore.value })
  await auditCallback({ id, ...auditForm, score: totalScore.value })
  ElMessage.success('审批完成'); router.back()
}
</script>
