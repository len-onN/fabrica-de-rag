import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AskRequest {
  workspaceId: string;
  collectionId?: string;
  query: string;
  topK?: number;
  policy?: string;
  tokenBudget?: number;
  filters?: Record<string, any>;
  includeTables?: boolean;
  includeVisuals?: boolean;
}

export interface Citation {
  contractVersion: string;
  citationId: string;
  documentId: string;
  chunkId: string;
  sourceElementId?: string;
  assetId?: string;
  sourceType: string;
  filePageNumber?: number;
  printedLabel?: string;
  sourceLabel: string;
  quote: string;
  contentKind: string;
  previewAvailable: boolean;
  fallbackTextAvailable: boolean;
  sourceLocator: Record<string, any>;
}

export interface SearchResult {
  chunkId: string;
  score: number;
  documentId: string;
  collectionId: string;
}

export interface ContextItemResponse {
  contextItemId: string;
  chunkId: string;
  rankSource: string;
  contentKind: string;
  content: string;
  tokenCount: number;
  truncated: boolean;
  citationId?: string;
}

export interface AskMetrics {
  retrievedChunks: number;
  contextItems: number;
  estimatedTokens: number;
  latencyMs: number;
}

export interface AskResponse {
  contractVersion: string;
  workspaceId: string;
  collectionId: string;
  queryId: string;
  status: string;
  answer: string;
  citations: Citation[];
  retrievedChunks: SearchResult[];
  expandedContext: ContextItemResponse[];
  reason?: string;
  metrics: AskMetrics;
}

export interface RetrievalFeedbackRequest {
  collectionId: string;
  documentId?: string;
  chunkId?: string;
  queryEventId?: string;
  feedbackType: string;
  note?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RagService {
  private http = inject(HttpClient);

  ask(workspaceId: string, request: AskRequest): Observable<AskResponse> {
    return this.http.post<AskResponse>(`/api/v1/workspaces/${workspaceId}/rag/ask`, request);
  }

  submitFeedback(workspaceId: string, request: RetrievalFeedbackRequest): Observable<void> {
    return this.http.post<void>(`/api/v1/workspaces/${workspaceId}/retrieval-feedback`, request);
  }
}
