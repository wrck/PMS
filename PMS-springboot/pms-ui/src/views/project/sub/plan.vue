<template>
  <div class="plan-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工程计划</span>
          <el-button type="primary" size="small" @click="handleEdit">编辑计划</el-button>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item v-for="event in planEvents" :key="event.id"
          :timestamp="event.eventPlanHappenDate" placement="top">
          <el-card>
            <h4>{{ event.eventName }}</h4>
            <p>计划时间: {{ event.eventPlanHappenDate }}</p>
            <p>实际时间: {{ event.eventActualHappenDate || '未完成' }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const planEvents = ref([])
const handleEdit = () => {}
</script>
