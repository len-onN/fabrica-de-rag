import { Injectable, inject } from '@angular/core';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

export interface DocumentResponse {
  publicId: string;
  originalFilename: string;
  mimeType: string;
  fileSizeBytes: number;
  status: string;
  pageCount: number;
  createdAt: string;
}

export interface DocumentPage {
  content: DocumentResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private http = inject(HttpService);

  uploadDocument(workspaceId: string, collectionId: string, file: File): Observable<DocumentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentResponse>(`/api/v1/workspaces/${workspaceId}/collections/${collectionId}/documents`, formData);
  }

  getDocuments(workspaceId: string, collectionId: string, page = 0, size = 10): Observable<DocumentPage> {
    return this.http.get<DocumentPage>(`/api/v1/workspaces/${workspaceId}/collections/${collectionId}/documents?page=${page}&size=${size}`);
  }
}
