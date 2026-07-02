<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>转包详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="转包编码">{{ data.subcontractCode }}</el-descriptions-item>
        <el-descriptions-item label="转包名称">{{ data.subcontractName }}</el-descriptions-item>
        <el-descriptions-item label="转包类型">{{ data.subcontractTypeName }}</el-descriptions-item>
        <el-descriptions-item label="合同号">{{ data.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="服务商">{{ data.facilitatorName }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="data.subcontractState===1?'success':'info'" size="small">{{ data.subcontractStateName }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.createTime }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ data.totalAmount }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab">
      <!-- 转包行项目 -->
      <el-tab-pane label="转包行项目" name="line">
        <el-table :data="lines" stripe size="small">
          <el-table-column prop="lineNo" label="行号" width="80" />
          <el-table-column prop="itemCode" label="物料编码" width="130" />
          <el-table-column prop="itemName" label="物料名称" min-width="200" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="amount" label="金额" width="120" />
        </el-table>
      </el-tab-pane>

      <!-- 付款信息 -->
      <el-tab-pane label="付款信息" name="payment">
        <el-table :data="payments" stripe size="small">
          <el-table-column prop="paymentNo" label="付款单号" width="150" />
          <el-table-column prop="paymentAmount" label="付款金额" width="120" />
          <el-table-column prop="paymentDate" label="付款日期" width="120" />
          <el-table-column prop="paymentStatus" label="状态" width="100" />
          <el-table-column prop="remark" label="备注" min-width="200" />
        </el-table>
      </el-tab-pane>

      <!-- 交付件 -->
      <el-tab-pane label="交付件" name="deliver">
        <el-table :data="delivers" stripe size="small">
          <el-table-column prop="fileName" label="文件名" min-width="200" />
          <el-table-column prop="fileSize" label="大小" width="100" />
          <el-table-column prop="uploadBy" label="上传人" width="100" />
          <el-table-column prop="uploadTime" label="上传时间" width="170" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }"><el-button size="small" type="primary" link @click="downloadFile(row)">下载</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 回访信息 -->
      <el-tab-pane label="回访信息" name="callback">
        <el-table :data="callbacks" stripe size="small">
          <el-table-column prop="callbackPerson" label="回访人" width="100" />
          <el-table-column prop="callbackTime" label="回访时间" width="170" />
          <el-table-column prop="callbackResult" label="回访结果" min-width="200" />
        </el-table>
      </el-tab-pane>

      <!-- 审批意见 -->
      <el-tab-pane label="审批意见" name="comment">
        <el-table :data="comments" stripe size="small">
          <el-table-column prop="commentPerson" label="审批人" width="100" />
          <el-table-column prop="result" label="结果" width="80">
            <template #default="{ row }"><el-tag :type="row.result==='同意'?'success':'danger'" size="small">{{ row.result }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="commentContent" label="意见" min-width="300" />
          <el-table-column prop="commentTime" label="时间" width="170" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSubcontract, getSubcontractLines, getSubcontractPayments, getSubcontractDelivers, getSubcontractCallbacks, getSubcontractComments } from '@/api/subcontract'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const activeTab = ref('line')
const data = ref({})
const lines = ref([])
const payments = ref([])
const delivers = ref([])
const callbacks = ref([])
const comments = ref([])
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }
const handleTabChange = async (tab) => {
  const loaders = {
    line: async () => { const r = await getSubcontractLines(id); lines.value = r.data || [] },
    payment: async () => { const r = await getSubcontractPayments(id); payments.value = r.data || [] },
    deliver: async () => { const r = await getSubcontractDelivers(id); delivers.value = r.data || [] },
    callback: async () => { const r = await getSubcontractCallbacks(id); callbacks.value = r.data || [] },
    comment: async () => { const r = await getSubcontractComments(id); comments.value = r.data || [] }
  }
  if (loaders[tab]) await loaders[tab]()
}
onMounted(async () => { loading.value = true; try { const r = await getSubcontract(id); data.value = r.data || {}; await handleTabChange('line') } finally { loading.value = false } })
</script>
