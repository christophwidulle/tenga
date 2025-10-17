import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Document, Tag } from '@/types'
import { api } from '@/services/api'

export const useAppStore = defineStore('app', () => {
  const documents = ref<Document[]>([])
  const selectedDocument = ref<Document | null>(null)
  const tags = ref<Tag[]>([])
  const selectedTags = ref<string[]>([])
  const searchQuery = ref('')
  const loading = ref(false)
  const error = ref<string | null>(null)

  const filteredDocuments = computed(() => {
    let filtered = documents.value

    // Filter by selected tags
    if (selectedTags.value.length > 0) {
      filtered = filtered.filter(doc =>
        selectedTags.value.every(selectedTag =>
          doc.tags.some(tag => tag.name === selectedTag)
        )
      )
    }

    // Filter by search query (client-side for now)
    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      filtered = filtered.filter(doc =>
        doc.title.toLowerCase().includes(query) ||
        doc.content.toLowerCase().includes(query)
      )
    }

    return filtered
  })

  async function loadDocuments(page: number = 0, size: number = 20) {
    loading.value = true
    error.value = null
    try {
      const response = await api.getDocuments(page, size)
      documents.value = response.content
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load documents'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function loadDocument(id: number) {
    loading.value = true
    error.value = null
    try {
      const doc = await api.getDocumentById(id)
      selectedDocument.value = doc
      return doc
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load document'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createDocument(title: string, content: string, tags: string[]) {
    loading.value = true
    error.value = null
    try {
      const doc = await api.createDocument({ title, content, tags })
      documents.value.unshift(doc)
      return doc
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create document'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateDocument(id: number, title: string, content: string, tags: string[], changeSummary?: string) {
    loading.value = true
    error.value = null
    try {
      const doc = await api.updateDocument(id, { title, content, tags, changeSummary })
      const index = documents.value.findIndex(d => d.id === id)
      if (index !== -1) {
        documents.value[index] = doc
      }
      if (selectedDocument.value?.id === id) {
        selectedDocument.value = doc
      }
      return doc
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update document'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteDocument(id: number) {
    loading.value = true
    error.value = null
    try {
      await api.deleteDocument(id)
      documents.value = documents.value.filter(d => d.id !== id)
      if (selectedDocument.value?.id === id) {
        selectedDocument.value = null
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete document'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function loadTags() {
    try {
      tags.value = await api.getTagHierarchy()
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load tags'
      throw err
    }
  }

  function toggleTag(tagName: string) {
    const index = selectedTags.value.indexOf(tagName)
    if (index === -1) {
      selectedTags.value.push(tagName)
    } else {
      selectedTags.value.splice(index, 1)
    }
  }

  function clearFilters() {
    selectedTags.value = []
    searchQuery.value = ''
  }

  return {
    documents,
    selectedDocument,
    tags,
    selectedTags,
    searchQuery,
    loading,
    error,
    filteredDocuments,
    loadDocuments,
    loadDocument,
    createDocument,
    updateDocument,
    deleteDocument,
    loadTags,
    toggleTag,
    clearFilters
  }
})
