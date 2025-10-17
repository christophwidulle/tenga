<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/app'

const store = useAppStore()

onMounted(() => {
  // Load initial data if authenticated
  if (store.error === null) {
    store.loadTags().catch(() => {
      // Silent fail for tags
    })
  }
})
</script>
