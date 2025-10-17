import axios, { type AxiosInstance, type AxiosError } from 'axios'
import type {
  Document,
  Tag,
  DocumentVersion,
  SearchResult,
  PageResponse,
  CreateDocumentRequest,
  UpdateDocumentRequest,
  ApiError
} from '@/types'

class ApiClient {
  private client: AxiosInstance
  private apiKey: string | null = null

  constructor() {
    this.client = axios.create({
      baseURL: '/api/v1',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    // Load API key from localStorage
    this.apiKey = localStorage.getItem('tenga_api_key')
    if (this.apiKey) {
      this.setApiKey(this.apiKey)
    }

    // Add response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError<ApiError>) => {
        if (error.response?.status === 401) {
          // Clear invalid API key
          this.clearApiKey()
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  setApiKey(key: string) {
    this.apiKey = key
    this.client.defaults.headers.common['Authorization'] = `Bearer ${key}`
    localStorage.setItem('tenga_api_key', key)
  }

  clearApiKey() {
    this.apiKey = null
    delete this.client.defaults.headers.common['Authorization']
    localStorage.removeItem('tenga_api_key')
  }

  hasApiKey(): boolean {
    return this.apiKey !== null
  }

  async validateApiKey(): Promise<boolean> {
    try {
      await this.getDocuments(0, 1)
      return true
    } catch {
      return false
    }
  }

  // Documents
  async getDocuments(page: number = 0, size: number = 20): Promise<PageResponse<Document>> {
    const response = await this.client.get('/documents', { params: { page, size } })
    return response.data
  }

  async getDocumentById(id: number): Promise<Document> {
    const response = await this.client.get(`/documents/${id}`)
    return response.data
  }

  async createDocument(request: CreateDocumentRequest): Promise<Document> {
    const response = await this.client.post('/documents', request)
    return response.data
  }

  async updateDocument(id: number, request: UpdateDocumentRequest): Promise<Document> {
    const response = await this.client.put(`/documents/${id}`, request)
    return response.data
  }

  async deleteDocument(id: number): Promise<void> {
    await this.client.delete(`/documents/${id}`)
  }

  async restoreDocument(id: number): Promise<Document> {
    const response = await this.client.post(`/documents/${id}/restore`)
    return response.data
  }

  async getDocumentsByTag(tagName: string, page: number = 0, size: number = 20): Promise<PageResponse<Document>> {
    const response = await this.client.get('/documents', { params: { tag: tagName, page, size } })
    return response.data
  }

  // Tags
  async getTags(): Promise<Tag[]> {
    const response = await this.client.get('/tags')
    return response.data
  }

  async getTagHierarchy(): Promise<Tag[]> {
    const response = await this.client.get('/tags/hierarchy')
    return response.data
  }

  async createTag(name: string, parentId?: number): Promise<Tag> {
    const response = await this.client.post('/tags', { name, parentId })
    return response.data
  }

  async deleteTag(id: number): Promise<void> {
    await this.client.delete(`/tags/${id}`)
  }

  async searchTags(query: string): Promise<Tag[]> {
    const response = await this.client.get('/tags/search', { params: { q: query } })
    return response.data
  }

  // Versions
  async getVersionHistory(documentId: number): Promise<DocumentVersion[]> {
    const response = await this.client.get(`/documents/${documentId}/versions`)
    return response.data
  }

  async getVersion(documentId: number, versionNumber: number): Promise<DocumentVersion> {
    const response = await this.client.get(`/documents/${documentId}/versions/${versionNumber}`)
    return response.data
  }

  async restoreVersion(documentId: number, versionNumber: number): Promise<Document> {
    const response = await this.client.post(`/documents/${documentId}/versions/${versionNumber}/restore`)
    return response.data
  }

  async compareVersions(documentId: number, v1: number, v2: number): Promise<any> {
    const response = await this.client.get(`/documents/${documentId}/versions/compare`, {
      params: { v1, v2 }
    })
    return response.data
  }

  // Search
  async search(query: string, tags?: string[], page: number = 0, size: number = 20): Promise<PageResponse<SearchResult>> {
    const params: any = { q: query, page, size }
    if (tags && tags.length > 0) {
      params.tags = tags.join(',')
    }
    const response = await this.client.get('/search', { params })
    return response.data
  }
}

export const api = new ApiClient()
