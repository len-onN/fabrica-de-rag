import { Injectable, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpService } from './http.service';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ChunkResponse {
  id: string;
  publicId: string;
  documentId: string;
  sequenceNumber: number;
  contentKind: string;
  content: string;
  headingPath: string;
  tokenCount: number;
  sourceLocator: string;
  embeddingStatus: string;
  createdAt: string;
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
export class ChunkService {
  private http = inject(HttpService);

  getChunks(
    workspaceId: string, 
    collectionId: string, 
    documentId?: string, 
    query?: string, 
    page: number = 0, 
    size: number = 25
  ): Observable<Page<ChunkResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (documentId) {
      params = params.set('documentId', documentId);
    }
    if (query) {
      params = params.set('q', query);
    }

    return this.http.get<Page<ChunkResponse>>(
      `/api/v1/workspaces/${workspaceId}/collections/${collectionId}/chunks`,
      { params }
    );
  }

  getNeighbor(workspaceId: string, collectionId: string, chunkId: string, offset: number): Observable<ChunkResponse> {
    const params = new HttpParams().set('offset', offset.toString());
    return this.http.get<ChunkResponse>(
      `/api/v1/workspaces/${workspaceId}/collections/${collectionId}/chunks/${chunkId}/neighbors`,
      { params }
    );
  }

  submitFeedback(workspaceId: string, request: RetrievalFeedbackRequest): Observable<void> {
    return this.http.post<void>(
      `/api/v1/workspaces/${workspaceId}/retrieval-feedback`,
      request
    );
  }
}
