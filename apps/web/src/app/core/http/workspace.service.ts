import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardSummaryResponse {
  schemaVersion: string;
  workspaceId: string;
  collectionCount: number;
  documentCount: number;
  activeRunsCount: number;
}

export interface WorkspaceSettingsResponse {
  publicId: string;
  name: string;
  purpose: string;
  slug: string | null;
  settings: any;
  createdAt: string;
  updatedAt: string;
}

export interface WorkspaceSettingsUpdateRequest {
  name?: string;
  purpose?: string;
  slug?: string;
  settings?: any;
}

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService {
  private http = inject(HttpClient);
  private apiUrl = '/api/v1/workspaces';

  getDashboardSummary(workspaceId: string): Observable<DashboardSummaryResponse> {
    return this.http.get<DashboardSummaryResponse>(`${this.apiUrl}/${workspaceId}/dashboard`);
  }

  getWorkspaceSettings(workspaceId: string): Observable<WorkspaceSettingsResponse> {
    return this.http.get<WorkspaceSettingsResponse>(`${this.apiUrl}/${workspaceId}/settings`);
  }

  updateWorkspaceSettings(workspaceId: string, request: WorkspaceSettingsUpdateRequest): Observable<WorkspaceSettingsResponse> {
    return this.http.patch<WorkspaceSettingsResponse>(`${this.apiUrl}/${workspaceId}/settings`, request);
  }
}
