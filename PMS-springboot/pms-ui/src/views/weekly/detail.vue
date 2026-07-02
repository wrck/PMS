<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>周报详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="填报人">{{ data.weeklyPerson }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="data.weeklyState===1?'success':'info'" size="small">{{ data.weeklyState===1?'已提交':'草稿' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ data.weeklyStartTime }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ data.weeklyEndTime }}</el-descriptions-item>
        <el-descriptions-item label="当前任务">{{ data.currentTask }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 本周工作内容 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>本周工作内容</span></template>
      <el-table :data="data.workContents || []" stripe size="small">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="content" label="工作内容" min-width="300" />
        <el-table-column prop="progress" label="进展" width="100" />
      </el-table>
    </el-card>

    <!-- 风险问题 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>风险问题</span></template>
      <el-table :data="data.risks || []" stripe size="small">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="risk" label="风险描述" min-width="250" />
        <el-table-column prop="solution" label="解决方案" min-width="250" />
      </el-table>
    </el-card>

    <!-- 需要协助 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>需要协助</span></template>
      <div>{{ data.helpNeeded || '无' }}</div>
    </el-card>

    <!-- 下周计划 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>下周计划</span></template>
      <el-table :data="data.plans || []" stripe size="small">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="plan" label="计划内容" min-width="300" />
        <el-table-column prop="planDate" label="计划日期" width="120" />
      </el-table>
    </el-card>

    <!-- 附件 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>附件</span></template>
      <el-table :data="data.attachments || []" stripe size="small">
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="fileSize" label="大小" width="100" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button size="small" type="primary" link @click="downloadFile(row)">下载</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 回复 -->
    <el-card>
      <template #header><span>回复记录</span></template>
      <el-table :data="data.feedbacks || []" stripe size="small">
        <el-table-column prop="feedbackPerson" label="回复人" width="100" />
        <el-table-column prop="feedbackContent" label="回复内容" min-width="300" />
        <el-table-column prop="feedbackTime" label="回复时间" width="170" />
      </el-table>
      <div v-if="data.weeklyState === 1" style="margin-top:12px">
        <el-input v-model="feedbackContent" type="textarea" :rows="2" placeholder="输入回复内容" />
        <el-button type="primary" style="margin-top:8px" @click="handleFeedback">回复</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWeekly, feedbackWeekly } from '@/api/weekly'
import { ElMessage } from 'element-plus'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const feedbackContent = ref('')
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }
const handleFeedback = async () => { if (!feedbackContent.value.trim()) { ElMessage.warning('请输入回复内容'); return } await feedbackWeekly({ weeklyId: id, content: feedbackContent.value }); ElMessage.success('已回复'); feedbackContent.value = ''; const r = await getWeekly(id); data.value = r.data || {} }
onMounted(async () => { loading.value = true; try { const r = await getWeekly(id); data.value = r.data || {} } finally { loading.value = false } })
</script>
