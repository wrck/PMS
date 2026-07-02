<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><span>售前审批</span></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="售前编码">{{ data.presalesCode }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ data.presalesName }}</el-descriptions-item>
        <el-descriptions-item label="售前类型">{{ data.presalesTypeName }}</el-descriptions-item>
        <el-descriptions-item label="办事处">{{ data.officeName }}</el-descriptions-item>
        <el-descriptions-item label="项目经理">{{ data.programManagerName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="申请说明" :span="2">{{ data.applyReason }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 产品信息 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>产品信息</span></template>
      <el-table :data="products" stripe size="small">
        <el-table-column prop="productCode" label="产品编码" width="130" />
        <el-table-column prop="productName" label="产品名称" min-width="200" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="amount" label="金额" width="120" />
      </el-table>
    </el-card>

    <!-- 发货信息 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>发货信息</span></template>
      <el-table :data="shipments" stripe size="small">
        <el-table-column prop="serialNumber" label="序列号" width="150" />
        <el-table-column prop="itemCode" label="物料" width="130" />
        <el-table-column prop="itemName" label="名称" min-width="200" />
        <el-table-column prop="shipmentDate" label="发货日期" width="120" />
      </el-table>
    </el-card>

    <!-- 审批历史 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>审批历史</span></template>
      <el-table :data="comments" stripe size="small">
        <el-table-column prop="commentPerson" label="审批人" width="100" />
        <el-table-column prop="result" label="结果" width="80">
          <template #default="{ row }"><el-tag :type="row.result === '同意' ? 'success' : 'danger'" size="small">{{ row.result }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="commentContent" label="意见" min-width="300" />
        <el-table-column prop="commentTime" label="时间" width="170" />
      </el-table>
    </el-card>

    <!-- 审批操作 -->
    <el-card v-if="canAudit">
      <template #header><span>审批操作</span></template>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio value="agree">同意</el-radio>
            <el-radio value="reject">驳回</el-radio>
            <el-radio value="close">直接关闭</el-radio>
            <el-radio value="changePm">变更项目经理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.result === 'changePm'" label="新项目经理">
          <el-autocomplete v-model="auditForm.newPmName" :fetch-suggestions="queryPMUser" style="width:300px" />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="auditForm.comment" type="textarea" :rows="3" />
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
import { getPresales, getPresalesProducts, getPresalesShipment, getPresalesComments, auditPresales } from '@/api/presales'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const products = ref([])
const shipments = ref([])
const comments = ref([])
const canAudit = computed(() => data.value.presalesState === 1)
const auditForm = reactive({ result: 'agree', comment: '', newPmName: '' })
const queryPMUser = (q, cb) => { cb([]) }
onMounted(async () => {
  loading.value = true
  try {
    const r = await getPresales(id); data.value = r.data || {}
    const p = await getPresalesProducts(id); products.value = p.data || []
    const s = await getPresalesShipment(id); shipments.value = s.data || []
    const c = await getPresalesComments(id); comments.value = c.data || []
  } finally { loading.value = false }
})
const handleAudit = async () => {
  await auditPresales({ id, ...auditForm })
  ElMessage.success('审批完成'); router.back()
}
</script>
