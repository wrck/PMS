<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>回访详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="回访类型">{{ data.callbackType }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="{0:'warning',1:'success',2:'danger',3:'info'}[data.applyState]" size="small">{{ {0:'待审批',1:'已通过',2:'已驳回',3:'无法回访'}[data.applyState] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="审批人">{{ data.approvePerson }}</el-descriptions-item>
        <el-descriptions-item label="回访说明" :span="2">{{ data.remark }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ data.approveComment }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 问卷结果 -->
    <el-card v-if="questionnaire" style="margin-bottom:16px">
      <template #header><span>问卷结果</span></template>
      <div v-for="(section, sIdx) in questionnaire.sections" :key="sIdx" style="margin-bottom:16px">
        <h4>{{ section.title }}</h4>
        <div v-for="(question, qIdx) in section.questions" :key="qIdx" style="margin-bottom:8px">
          <p>{{ qIdx + 1 }}. {{ question.content }}：<strong>{{ question.answer }}</strong></p>
        </div>
      </div>
      <div><strong>总分：{{ questionnaire.totalScore }}</strong></div>
    </el-card>

    <!-- 审批历史 -->
    <el-card>
      <template #header><span>审批历史</span></template>
      <el-table :data="historyList" stripe size="small">
        <el-table-column prop="stepName" label="步骤" width="120" />
        <el-table-column prop="operator" label="处理人" width="100" />
        <el-table-column prop="result" label="结果" width="100" />
        <el-table-column prop="comment" label="意见" min-width="200" />
        <el-table-column prop="operateTime" label="时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCallback, getQuestionnaire, getCallbackHistory } from '@/api/callback'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const questionnaire = ref(null)
const historyList = ref([])
onMounted(async () => {
  loading.value = true
  try {
    const r = await getCallback(id); data.value = r.data || {}
    const q = await getQuestionnaire(id); questionnaire.value = q.data
    const h = await getCallbackHistory(id); historyList.value = h.data || []
  } finally { loading.value = false }
})
</script>
