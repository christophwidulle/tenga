<template>
  <div class="document-edit">
    <header class="header">
      <div class="container">
        <button class="btn btn-secondary" @click="cancel">Cancel</button>
        <button class="btn btn-primary" @click="save" :disabled="saving">
          {{ saving ? 'Saving...' : 'Save' }}
        </button>
      </div>
    </header>

    <div class="container">
      <div class="editor-layout">
        <div class="editor-panel card">
          <input
            v-model="title"
            type="text"
            class="input title-input"
            placeholder="Document title..."
          />

          <textarea
            v-model="content"
            class="content-input"
            placeholder="Write your content here (Markdown supported)..."
          ></textarea>

          <div class="tags-input">
            <label>Tags (comma-separated):</label>
            <input
              v-model="tagsInput"
              type="text"
              class="input"
              placeholder="tag1, tag2, tag3"
            />
          </div>

          <div v-if="isEdit" class="change-summary">
            <label>Change Summary (optional):</label>
            <input
              v-model="changeSummary"
              type="text"
              class="input"
              placeholder="Describe your changes..."
            />
          </div>
        </div>

        <div class="preview-panel card">
          <h3>Preview</h3>
          <div class="markdown-content" v-html="renderedPreview"></div>
        </div>
      </div>
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

const title = ref('')
const content = ref('')
const tagsInput = ref('')
const changeSummary = ref('')
const saving = ref(false)

const isEdit = computed(() => route.params.id !== undefined && route.name === 'document-edit')

const renderedPreview = computed(() => {
  // Simple markdown rendering
  return content.value
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/\*\*(.*)\*\*/gim, '<strong>$1</strong>')
    .replace(/\*(.*)\*/gim, '<em>$1</em>')
    .replace(/\n/g, '<br/>')
})

onMounted(async () => {
  if (isEdit.value) {
    const id = parseInt(route.params.id as string)
    const doc = await store.loadDocument(id)
    if (doc) {
      title.value = doc.title
      content.value = doc.content
      tagsInput.value = doc.tags.map(t => t.name).join(', ')
    }
  }
})

async function save() {
  if (!title.value || !content.value) {
    alert('Title and content are required')
    return
  }

  saving.value = true

  try {
    const tags = tagsInput.value
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0)

    if (isEdit.value) {
      const id = parseInt(route.params.id as string)
      await store.updateDocument(id, title.value, content.value, tags, changeSummary.value)
      router.push({ name: 'document', params: { id } })
    } else {
      const doc = await store.createDocument(title.value, content.value, tags)
      router.push({ name: 'document', params: { id: doc.id } })
    }
  } catch (error) {
    alert('Failed to save document')
  } finally {
    saving.value = false
  }
}

function cancel() {
  if (isEdit.value) {
    router.push({ name: 'document', params: { id: route.params.id } })
  } else {
    router.push({ name: 'home' })
  }
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

.editor-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
}

.editor-panel,
.preview-panel {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.title-input {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  border: none;
  border-bottom: 2px solid #e0e0e0;
  border-radius: 0;
}

.title-input:focus {
  border-bottom-color: #007bff;
}

.content-input {
  width: 100%;
  min-height: 400px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
}

.content-input:focus {
  outline: none;
  border-color: #007bff;
}

.tags-input,
.change-summary {
  margin-top: 20px;
}

.tags-input label,
.change-summary label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.preview-panel h3 {
  margin-bottom: 20px;
  color: #333;
}

@media (max-width: 1024px) {
  .editor-layout {
    grid-template-columns: 1fr;
  }

  .preview-panel {
    display: none;
  }
}
</style>
