import { Injectable, inject } from '@angular/core';
import { HttpService } from './http.service';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

export interface CollectionResponse {
  id: string;
  workspaceId: string;
  name: string;
  description: string;
  purpose: string;
  status: string;
  defaultIngestionProfile: string;
  defaultContextPolicy: string;
  documentCount: number;
  chunkCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CollectionCreateRequest {
  name: string;
  description: string;
  purpose: string;
  defaultIngestionProfile: string;
  defaultContextPolicy: string;
}

@Injectable({
  providedIn: 'root'
})
export class CollectionService {
  private http = inject(HttpService);
  private authService = inject(AuthService);

  getCollections(): Observable<{ data: CollectionResponse[] }> {
    const workspaceId = this.authService.activeWorkspaceId;
    return this.http.get<{ data: CollectionResponse[] }>(`/api/v1/workspaces/${workspaceId}/collections`);
  }

  getCollection(id: string): Observable<CollectionResponse> {
    const workspaceId = this.authService.activeWorkspaceId;
    return this.http.get<CollectionResponse>(`/api/v1/workspaces/${workspaceId}/collections/${id}`);
  }

  createCollection(data: CollectionCreateRequest): Observable<CollectionResponse> {
    const workspaceId = this.authService.activeWorkspaceId;
    return this.http.post<CollectionResponse>(`/api/v1/workspaces/${workspaceId}/collections`, data);
  }
}
