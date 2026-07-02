<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><span>转包审批</span></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="转包编码">{{ data.subcontractCode }}</el-descriptions-item>
        <el-descriptions-item label="转包名称">{{ data.subcontractName }}</el-descriptions-item>
        <el-descriptions-item label="转包类型">{{ data.subcontractTypeName }}</el-descriptions-item>
        <el-descriptions-item label="合同号">{{ data.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="服务商">{{ data.facilitatorName }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ data.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ data.applyPerson }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ data.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ data.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 行项目 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>行项目</span></template>
      <el-table :data="lines" stripe size="small">
        <el-table-column prop="lineNo" label="行号" width="80" />
        <el-table-column prop="itemCode" label="物料编码" width="130" />
        <el-table-column prop="itemName" label="物料名称" min-width="200" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="amount" label="金额" width="120" />
      </el-table>
    </el-card>

    <!-- 审批历史 -->
    <el-card style="margin-bottom:16px">
      <template #header><span>审批历史</span></template>
      <el-table :data="comments" stripe size="small">
        <el-table-column prop="commentPerson" label="审批人" width="100" />
        <el-table-column prop="result" label="结果" width="80">
          <template #default="{ row }"><el-tag :type="row.result==='同意'?'success':'danger'" size="small">{{ row.result }}</el-tag></template>
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
          </el-radio-group>
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
import { getSubcontract, getSubcontractLines, getSubcontractComments, auditSubcontract } from '@/api/subcontract'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const id = route.params.id
const loading = ref(false)
const data = ref({})
const lines = ref([])
const comments = ref([])
const canAudit = computed(() => data.value.subcontractState === 0)
const auditForm = reactive({ result: 'agree', comment: '' })
onMounted(async () => {
  loading.value = true
  try {
    const r = await getSubcontract(id); data.value = r.data || {}
    const l = await getSubcontractLines(id); lines.value = l.data || []
    const c = await getSubcontractComments(id); comments.value = c.data || []
  } finally { loading.value = false }
})
const handleAudit = async () => { await auditSubcontract({ id, ...auditForm }); ElMessage.success('审批完成'); router.back() }
</script>
