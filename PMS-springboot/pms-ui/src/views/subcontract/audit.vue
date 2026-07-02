<template>
  <div class="subcontract-audit">
    <el-card>
      <template #header><span>转包audit</span></template>
      <el-form v-if="'audit' !== 'detail'" :model="form" label-width="120px">
        <el-form-item label="转包项目名称"><el-input v-model="form.subcontractName" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">提交</el-button></el-form-item>
      </el-form>
      <div v-else>转包项目详情页面</div>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const form = ref({})
const handleSubmit = () => {}
</script>
