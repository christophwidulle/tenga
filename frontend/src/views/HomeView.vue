<template>
  <div class="home">
    <header class="header">
      <div class="container">
        <h1>Tenga Knowledge Base</h1>
        <button class="btn btn-primary" @click="createNew">+ New Document</button>
      </div>
    </header>

    <div class="main-content">
      <aside class="sidebar">
        <div class="search-box">
          <input
            v-model="store.searchQuery"
            type="text"
            class="input"
            placeholder="Search documents..."
          />
        </div>

        <div class="tag-section">
          <h3>Tags</h3>
          <div v-if="store.tags.length === 0" class="empty-state">
            No tags yet
          </div>
          <div v-else class="tag-list">
            <div
              v-for="tag in store.tags"
              :key="tag.id"
              class="tag-item"
              :class="{ active: store.selectedTags.includes(tag.name) }"
              @click="store.toggleTag(tag.name)"
            >
              {{ tag.name }}
              <span v-if="tag.documentCount" class="count">({{ tag.documentCount }})</span>
            </div>
          </div>

          <button
            v-if="store.selectedTags.length > 0"
            class="btn btn-secondary"
            @click="store.clearFilters"
          >
            Clear Filters
          </button>
        </div>
      </aside>

      <main class="content">
        <div v-if="store.loading" class="loading">
          <div class="spinner"></div>
        </div>

        <div v-else-if="store.error" class="error">
          {{ store.error }}
        </div>

        <div v-else-if="store.filteredDocuments.length === 0" class="empty-state">
          <p>No documents found</p>
          <button class="btn btn-primary" @click="createNew">Create your first document</button>
        </div>

        <div v-else class="document-list">
          <div
            v-for="doc in store.filteredDocuments"
            :key="doc.id"
            class="document-card card"
            @click="openDocument(doc.id)"
          >
            <h3>{{ doc.title }}</h3>
            <div class="tags">
              <span v-for="tag in doc.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
            </div>
            <div class="meta">
              <span>Updated: {{ formatDate(doc.updatedAt) }}</span>
              <span>Version: {{ doc.currentVersion }}</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const store = useAppStore()

onMounted(async () => {
  await store.loadDocuments()
  await store.loadTags()
})

function openDocument(id: number) {
  router.push({ name: 'document', params: { id } })
}

function createNew() {
  router.push({ name: 'document-new' })
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>

<style scoped>
.header {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px 0;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-content {
  display: flex;
  min-height: calc(100vh - 80px);
}

.sidebar {
  width: 300px;
  background: white;
  padding: 20px;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
}

.search-box {
  margin-bottom: 30px;
}

.tag-section h3 {
  margin-bottom: 15px;
}

.tag-list {
  margin-bottom: 15px;
}

.tag-item {
  padding: 8px 12px;
  margin-bottom: 5px;
  background: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tag-item:hover {
  background: #e0e0e0;
}

.tag-item.active {
  background: #007bff;
  color: white;
}

.tag-item .count {
  color: #666;
  font-size: 12px;
}

.tag-item.active .count {
  color: rgba(255, 255, 255, 0.8);
}

.content {
  flex: 1;
  padding: 20px;
}

.document-list {
  display: grid;
  gap: 20px;
}

.document-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.document-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.document-card h3 {
  margin-bottom: 10px;
  color: #333;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.tag {
  padding: 4px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 3px;
  font-size: 12px;
}

.meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }
}
</style>
