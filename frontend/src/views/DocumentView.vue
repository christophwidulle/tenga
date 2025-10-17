<template>
  <div class="document-view">
    <header class="header">
      <div class="container">
        <button class="btn btn-secondary" @click="goBack">← Back</button>
        <div class="actions">
          <button class="btn btn-secondary" @click="viewVersions">Versions</button>
          <button class="btn btn-primary" @click="editDocument">Edit</button>
          <button class="btn btn-danger" @click="deleteDoc">Delete</button>
        </div>
      </div>
    </header>

    <div v-if="store.loading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else-if="store.error" class="error container">
      {{ store.error }}
    </div>

    <div v-else-if="store.selectedDocument" class="container">
      <article class="document card">
        <h1>{{ store.selectedDocument.title }}</h1>

        <div class="meta">
          <span>Created: {{ formatDate(store.selectedDocument.createdAt) }}</span>
          <span>Updated: {{ formatDate(store.selectedDocument.updatedAt) }}</span>
          <span>Version: {{ store.selectedDocument.currentVersion }}</span>
        </div>

        <div class="tags">
          <span v-for="tag in store.selectedDocument.tags" :key="tag.id" class="tag">
            {{ tag.name }}
          </span>
        </div>

        <div class="markdown-content" v-html="renderedContent"></div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const route = useRoute()
const store = useAppStore()

const renderedContent = computed(() => {
  if (!store.selectedDocument) return ''
  // Simple markdown rendering - in production, use markdown-it
  return store.selectedDocument.content
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/\*\*(.*)\*\*/gim, '<strong>$1</strong>')
    .replace(/\*(.*)\*/gim, '<em>$1</em>')
    .replace(/\n/g, '<br/>')
})

onMounted(async () => {
  const id = parseInt(route.params.id as string)
  await store.loadDocument(id)
})

function goBack() {
  router.push({ name: 'home' })
}

function editDocument() {
  router.push({ name: 'document-edit', params: { id: route.params.id } })
}

function viewVersions() {
  router.push({ name: 'document-versions', params: { id: route.params.id } })
}

async function deleteDoc() {
  if (!confirm('Are you sure you want to delete this document?')) return

  const id = parseInt(route.params.id as string)
  await store.deleteDocument(id)
  router.push({ name: 'home' })
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

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

.document {
  max-width: 800px;
  margin: 0 auto 40px;
}

.document h1 {
  margin-bottom: 20px;
  color: #333;
}

.meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 30px;
}

.tag {
  padding: 4px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 3px;
  font-size: 12px;
}
</style>
