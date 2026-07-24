<script setup lang="ts">
import { JMREPORT_LIST_URL } from '@/api/yudao-report'

defineOptions({ name: 'JmReport' })
</script>

<template>
  <div class="page-container" style="height: calc(100vh - 120px)">
    <iframe :src="JMREPORT_LIST_URL" style="width: 100%; height: 100%; border: none" />
  </div>
</template>
