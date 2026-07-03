<template>
  <div>
    <el-card style="margin-bottom:16px">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <el-page-header @back="$router.back()" content="维保回访详情" />
        <div>
          <el-button v-if="detail.callbackState === 0" type="primary" @click="$router.push(`/warrantyCallback/create?id=${detail.id}`)">去回访</el-button>
        </div>
      </div>
    </el-card>
    <el-card style="margin-bottom:16px">
      <template #header><span>项目信息</span></template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="项目编码">{{ detail.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detail.projectName }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="维保状态"><el-tag size="small">{{ detail.warrantyStatusName }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="回访状态">
          <el-tag :type="{0:'warning',1:'success',2:'info'}[detail.callbackState]" size="small">{{ {0:'待回访',1:'已回访',2:'无法回访'}[detail.callbackState] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="办事处">{{ detail.officeName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card v-if="detail.callbackState === 1">
      <template #header><span>回访记录</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="回访人">{{ detail.callbackPerson }}</el-descriptions-item>
        <el-descriptions-item label="回访时间">{{ detail.callbackTime }}</el-descriptions-item>
        <el-descriptions-item label="回访方式">{{ detail.callbackMethod }}</el-descriptions-item>
        <el-descriptions-item label="满意度">{{ detail.satisfaction }}</el-descriptions-item>
        <el-descriptions-item label="回访内容" :span="2">{{ detail.callbackContent }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWarrantyCallback } from '@/api/warrantyCallback'
const route = useRoute()
const detail = ref({})
onMounted(async () => { const r = await getWarrantyCallback(route.params.id); detail.value = r.data || {} })
</script>
