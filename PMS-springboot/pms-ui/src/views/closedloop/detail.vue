<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>闭环详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="项目编码">{{ data.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="当前步骤">{{ data.currentStep }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="{0:'warning',1:'success',2:'danger',3:'info'}[data.applyState]" size="small">{{ {0:'待审批',1:'已通过',2:'已驳回',3:'无法闭环'}[data.applyState] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.applyTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 审批历史 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>审批历史</span></template>
      <el-table :data="historyList" stripe size="small">
        <el-table-column prop="stepName" label="步骤" width="120" />
        <el-table-column prop="operator" label="处理人" width="100" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }"><el-tag :type="row.result === '同意' ? 'success' : 'danger'" size="small">{{ row.result }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="comment" label="意见" min-width="200" />
        <el-table-column prop="operateTime" label="时间" width="170" />
      </el-table>
    </el-card>

    <!-- 问卷结果 -->
    <el-card v-if="questionnaire">
      <template #header><span>问卷结果</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item v-for="(item, idx) in questionnaire.items" :key="idx" :label="item.question">{{ item.answer }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top:12px"><strong>总分：{{ questionnaire.totalScore }}</strong></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getClosedLoop, getClosedLoopHistory, getClosedLoopQuestionnaire } from '@/api/closedloop'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const historyList = ref([])
const questionnaire = ref(null)
onMounted(async () => {
  loading.value = true
  try {
    const r = await getClosedLoop(id); data.value = r.data || {}
    const h = await getClosedLoopHistory(id); historyList.value = h.data || []
    const q = await getClosedLoopQuestionnaire(id); questionnaire.value = q.data
  } finally { loading.value = false }
})
</script>
