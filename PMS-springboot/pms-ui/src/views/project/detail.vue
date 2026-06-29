<template>
  <div>
    <el-page-header @back="router.back()" content="项目详情" style="margin-bottom: 20px" />
    <el-card v-loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目编码">{{ project.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ project.projectName }}</el-descriptions-item>
        <el-descriptions-item label="合同号">{{ project.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="办事处">{{ project.officeCode }}</el-descriptions-item>
        <el-descriptions-item label="项目类型">{{ project.projectType }}</el-descriptions-item>
        <el-descriptions-item label="项目状态">{{ project.projectState }}</el-descriptions-item>
        <el-descriptions-item label="项目经理">{{ project.pmCode }}</el-descriptions-item>
        <el-descriptions-item label="服务经理">{{ project.smCode }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ project.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ project.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>项目成员</span>
          <el-button type="primary" size="small">添加成员</el-button>
        </div>
      </template>
      <el-table :data="members" border>
        <el-table-column prop="memberName" label="姓名" />
        <el-table-column prop="memberRole" label="角色" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="effectiveFrom" label="生效时间" />
        <el-table-column prop="effectiveTo" label="失效时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProjectDetail, getProjectMembers } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const project = ref({})
const members = ref([])

onMounted(async () => {
  const id = route.params.id
  loading.value = true
  try {
    const [detailRes, memberRes] = await Promise.all([
      getProjectDetail(id),
      getProjectMembers(id)
    ])
    project.value = detailRes.data
    members.value = memberRes.data
  } finally {
    loading.value = false
  }
})
</script>
