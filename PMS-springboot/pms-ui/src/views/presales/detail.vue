<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>售前详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="售前编码">{{ data.presalesCode }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ data.presalesName }}</el-descriptions-item>
        <el-descriptions-item label="售前类型">{{ data.presalesTypeName }}</el-descriptions-item>
        <el-descriptions-item label="办事处">{{ data.officeName }}</el-descriptions-item>
        <el-descriptions-item label="项目经理">{{ data.programManagerName }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="getStateType(data.presalesState)" size="small">{{ getStateText(data.presalesState) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ data.endTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab">
      <!-- 产品信息 -->
      <el-tab-pane label="产品信息" name="product">
        <el-table :data="products" stripe size="small">
          <el-table-column prop="productCode" label="产品编码" width="130" />
          <el-table-column prop="productName" label="产品名称" min-width="200" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- 计划任务 -->
      <el-tab-pane label="计划任务" name="task">
        <el-table :data="tasks" stripe size="small">
          <el-table-column prop="taskName" label="任务名称" min-width="200" />
          <el-table-column prop="planDate" label="计划日期" width="120" />
          <el-table-column prop="actualDate" label="实际日期" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- 审批意见 -->
      <el-tab-pane label="审批意见" name="comment">
        <el-table :data="comments" stripe size="small">
          <el-table-column prop="commentPerson" label="审批人" width="100" />
          <el-table-column prop="commentContent" label="意见" min-width="300" />
          <el-table-column prop="commentTime" label="时间" width="170" />
        </el-table>
      </el-tab-pane>

      <!-- 发货信息 -->
      <el-tab-pane label="发货信息" name="shipment">
        <el-table :data="shipments" stripe size="small">
          <el-table-column prop="serialNumber" label="序列号" width="150" />
          <el-table-column prop="itemCode" label="物料" width="130" />
          <el-table-column prop="itemName" label="名称" min-width="200" />
          <el-table-column prop="shipmentDate" label="发货日期" width="120" />
        </el-table>
      </el-tab-pane>

      <!-- 借转销信息 -->
      <el-tab-pane label="借转销" name="lend2sale">
        <el-table :data="lend2saleList" stripe size="small">
          <el-table-column prop="orderNo" label="订单号" width="150" />
          <el-table-column prop="itemCode" label="物料" width="130" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- 核销信息 -->
      <el-tab-pane label="核销" name="lend2rma">
        <el-table :data="lend2rmaList" stripe size="small">
          <el-table-column prop="rmaNo" label="核销单号" width="150" />
          <el-table-column prop="itemCode" label="物料" width="130" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- 临时授权 -->
      <el-tab-pane label="临时授权" name="tempAuth">
        <el-table :data="tempAuthList" stripe size="small">
          <el-table-column prop="authCode" label="授权编码" width="150" />
          <el-table-column prop="authType" label="类型" width="100" />
          <el-table-column prop="expireDate" label="过期日期" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
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
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPresales, getPresalesProducts, getPresalesTasks, getPresalesComments, getPresalesShipment, getPresalesLend2Sale, getPresalesLend2Rma, getPresalesTempAuth, getPresalesDelivers } from '@/api/presales'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const activeTab = ref('product')
const data = ref({})
const products = ref([])
const tasks = ref([])
const comments = ref([])
const shipments = ref([])
const lend2saleList = ref([])
const lend2rmaList = ref([])
const tempAuthList = ref([])
const delivers = ref([])
const getStateType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }[s] || 'info')
const getStateText = (s) => ({ 0: '草稿', 1: '审批中', 2: '已通过', 3: '已驳回', 4: '已关闭' }[s] || '未知')
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }
const handleTabChange = async (tab) => {
  const loaders = {
    product: async () => { const r = await getPresalesProducts(id); products.value = r.data || [] },
    task: async () => { const r = await getPresalesTasks(id); tasks.value = r.data || [] },
    comment: async () => { const r = await getPresalesComments(id); comments.value = r.data || [] },
    shipment: async () => { const r = await getPresalesShipment(id); shipments.value = r.data || [] },
    lend2sale: async () => { const r = await getPresalesLend2Sale(id); lend2saleList.value = r.data || [] },
    lend2rma: async () => { const r = await getPresalesLend2Rma(id); lend2rmaList.value = r.data || [] },
    tempAuth: async () => { const r = await getPresalesTempAuth(id); tempAuthList.value = r.data || [] },
    deliver: async () => { const r = await getPresalesDelivers(id); delivers.value = r.data || [] }
  }
  if (loaders[tab]) await loaders[tab]()
}
onMounted(async () => { loading.value = true; try { const r = await getPresales(id); data.value = r.data || {}; await handleTabChange('product') } finally { loading.value = false } })
</script>
