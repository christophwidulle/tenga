export interface Document {
  id: number
  title: string
  content: string
  tags: Tag[]
  createdAt: string
  updatedAt: string
  currentVersion: number
}

export interface Tag {
  id: number
  name: string
  parentId?: number
  children?: Tag[]
  documentCount?: number
}

export interface DocumentVersion {
  id: number
  documentId: number
  versionNumber: number
  title: string
  content: string
  tagsSnapshot?: string
  createdAt: string
  changeSummary?: string
}

export interface SearchResult {
  id: number
  title: string
  content: string
  snippet: string
  relevanceScore: number
  tags: Tag[]
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  hasNext: boolean
  hasPrevious: boolean
}

export interface CreateDocumentRequest {
  title: string
  content: string
  tags?: string[]
}

export interface UpdateDocumentRequest {
  title?: string
  content?: string
  tags?: string[]
  changeSummary?: string
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
