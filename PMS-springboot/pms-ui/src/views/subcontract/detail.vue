<template>
  <div class="subcontract-detail">
    <el-card>
      <template #header><span>转包detail</span></template>
      <el-form v-if="'detail' !== 'detail'" :model="form" label-width="120px">
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
