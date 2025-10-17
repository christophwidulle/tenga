<template>
  <div class="version-history">
    <header class="header">
      <div class="container">
        <button class="btn btn-secondary" @click="goBack">← Back to Document</button>
      </div>
    </header>

    <div class="container">
      <h2>Version History</h2>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
      </div>

      <div v-else-if="error" class="error">
        {{ error }}
      </div>

      <div v-else-if="versions.length === 0" class="empty-state">
        <p>No version history available</p>
      </div>

      <div v-else class="versions-list">
        <div v-for="version in versions" :key="version.id" class="version-card card">
          <div class="version-header">
            <h3>Version {{ version.versionNumber }}</h3>
            <span class="date">{{ formatDate(version.createdAt) }}</span>
          </div>

          <p v-if="version.changeSummary" class="change-summary">
            {{ version.changeSummary }}
          </p>

          <div class="version-actions">
            <button class="btn btn-secondary" @click="viewVersion(version)">
              View
            </button>
            <button class="btn btn-primary" @click="restoreVersion(version)">
              Restore
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '@/services/api'
import type { DocumentVersion } from '@/types'

const router = useRouter()
const route = useRoute()

const versions = ref<DocumentVersion[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

onMounted(async () => {
  await loadVersions()
})

async function loadVersions() {
  loading.value = true
  error.value = null

  try {
    const id = parseInt(route.params.id as string)
    versions.value = await api.getVersionHistory(id)
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to load version history'
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push({ name: 'document', params: { id: route.params.id } })
}

function viewVersion(version: DocumentVersion) {
  alert(`Viewing version ${version.versionNumber}:\n\n${version.content.substring(0, 200)}...`)
}

async function restoreVersion(version: DocumentVersion) {
  if (!confirm(`Restore to version ${version.versionNumber}? This will create a new version.`)) {
    return
  }

  try {
    const id = parseInt(route.params.id as string)
    await api.restoreVersion(id, version.versionNumber)
    alert('Version restored successfully')
    router.push({ name: 'document', params: { id } })
  } catch (err: any) {
    alert('Failed to restore version')
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.header {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px 0;
  margin-bottom: 30px;
}

.container h2 {
  margin-bottom: 30px;
}

.versions-list {
  max-width: 800px;
  margin: 0 auto;
}

.version-card {
  margin-bottom: 20px;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.version-header h3 {
  margin: 0;
  color: #333;
}

.date {
  color: #666;
  font-size: 14px;
}

.change-summary {
  margin-bottom: 15px;
  color: #666;
  font-style: italic;
}

.version-actions {
  display: flex;
  gap: 10px;
}
</style>
