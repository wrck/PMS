<script setup lang="ts">
import { ref } from 'vue'

defineOptions({ name: 'InfraDruid' })

const src = ref('/druid/index.html')
</script>

<template>
  <div class="page-container" style="height: calc(100vh - 120px)">
    <iframe :src="src" style="width: 100%; height: 100%; border: none" />
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
