<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>维保详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目编码">{{ data.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="维护类型">{{ data.maintenanceType }}</el-descriptions-item>
        <el-descriptions-item label="维护分类">{{ data.maintenanceCategoryName }}</el-descriptions-item>
        <el-descriptions-item label="维护人">{{ data.maintenancePerson }}</el-descriptions-item>
        <el-descriptions-item label="维护时间">{{ data.maintenanceTime }}</el-descriptions-item>
        <el-descriptions-item label="维护内容" :span="3">{{ data.maintenanceContent }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 问卷结果 -->
    <el-card v-if="questionnaire" style="margin-bottom:16px">
      <template #header><span>问卷结果</span></template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item v-for="(item, idx) in questionnaire.items" :key="idx" :label="item.question">{{ item.answer }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 交付件 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>交付件</span></template>
      <el-table :data="delivers" stripe size="small">
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="fileSize" label="大小" width="100" />
        <el-table-column prop="uploadBy" label="上传人" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button size="small" type="primary" link @click="downloadFile(row)">下载</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMaintenance, getMaintenanceQuestionnaire, getMaintenanceDelivers } from '@/api/maintenance'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const questionnaire = ref(null)
const delivers = ref([])
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }
onMounted(async () => {
  loading.value = true
  try {
    const r = await getMaintenance(id); data.value = r.data || {}
    const q = await getMaintenanceQuestionnaire(id); questionnaire.value = q.data
    const d = await getMaintenanceDelivers(id); delivers.value = d.data || []
  } finally { loading.value = false }
})
</script>
