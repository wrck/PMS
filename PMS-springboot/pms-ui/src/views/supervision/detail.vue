<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>督查详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="项目名称">{{ data.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目编码">{{ data.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="督查人">{{ data.createUser }}</el-descriptions-item>
        <el-descriptions-item label="督查时间">{{ data.processTime }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="data.status===1?'success':'warning'" size="small">{{ data.status===1?'已整改':'待整改' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="办事处">{{ data.officeName }}</el-descriptions-item>
        <el-descriptions-item label="督查内容" :span="3">{{ data.supervisionContent }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 督查问题 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>督查问题</span></template>
      <el-table :data="questions" stripe size="small">
        <el-table-column prop="questionNo" label="序号" width="80" />
        <el-table-column prop="questionContent" label="问题内容" min-width="300" />
        <el-table-column prop="questionType" label="问题类型" width="120" />
        <el-table-column prop="rectifyStatus" label="整改状态" width="100">
          <template #default="{ row }"><el-tag :type="row.rectifyStatus===1?'success':'warning'" size="small">{{ row.rectifyStatus===1?'已整改':'待整改' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="rectifyContent" label="整改内容" min-width="200" />
        <el-table-column prop="rectifyTime" label="整改时间" width="170" />
      </el-table>
    </el-card>

    <!-- 整改操作 -->
    <el-card v-if="canRectify">
      <template #header><span>整改反馈</span></template>
      <el-form :model="rectifyForm" label-width="80px">
        <el-form-item label="整改内容">
          <el-input v-model="rectifyForm.rectifyContent" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleUpload" :file-list="rectifyForm.attachments" multiple>
            <el-button size="small">上传附件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRectify">提交整改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSupervision, rectifySupervision } from '@/api/supervision'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const questions = ref([])
const canRectify = computed(() => data.value.status === 0)
const rectifyForm = reactive({ rectifyContent: '', attachments: [] })
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const handleUpload = (res) => { if (res.code === 200) rectifyForm.attachments.push(res.data) }
const handleRectify = async () => { await rectifySupervision({ id, ...rectifyForm }); ElMessage.success('整改已提交'); router.back() }
onMounted(async () => { loading.value = true; try { const r = await getSupervision(id); data.value = r.data || {} } finally { loading.value = false } })
</script>
