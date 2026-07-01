import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardSummaryResponse {
  contractVersion: string;
  workspaceId: string;
  range: {
    preset: string;
    from: string;
    to: string;
  };
  filters: {
    collectionId?: string;
    origins: string[];
  };
  ingestion: {
    runsStarted: number;
    runsCompleted: number;
    runsFailed: number;
    runsWaitingForReview: number;
    medianDurationMs: number;
  };
  retrieval: {
    queriesCount: number;
    lowConfidenceCount: number;
    noResultsCount: number;
    averageRetrievedChunks: number;
    contextBudgetHitCount: number;
  };
  answers: {
    generatedCount: number;
    insufficientEvidenceCount: number;
    averageCitations: number;
    usefulFeedbackCount: number;
    notUsefulFeedbackCount: number;
  };
  apiAndMcp: {
    apiCallsCount: number;
    mcpToolCallsCount: number;
    limitHitCount: number;
    agentLoopDetectedCount: number;
  };
  topFailures: Array<{
    stageName: string;
    errorCode: string;
    count: number;
  }>;
  empty: boolean;
}

export interface DashboardFilters {
  preset?: string;
  from?: string;
  to?: string;
  collectionId?: string;
  origins?: string[];
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

  getDashboardSummary(workspaceId: string, filters?: DashboardFilters): Observable<DashboardSummaryResponse> {
    let params = new HttpParams();
    if (filters) {
      if (filters.preset) params = params.set('preset', filters.preset);
      if (filters.from) params = params.set('from', filters.from);
      if (filters.to) params = params.set('to', filters.to);
      if (filters.collectionId) params = params.set('collectionId', filters.collectionId);
      if (filters.origins && filters.origins.length > 0) {
        params = params.set('origins', filters.origins.join(','));
      }
    }
    return this.http.get<DashboardSummaryResponse>(`${this.apiUrl}/${workspaceId}/dashboard`, { params });
  }

  getWorkspaceSettings(workspaceId: string): Observable<WorkspaceSettingsResponse> {
    return this.http.get<WorkspaceSettingsResponse>(`${this.apiUrl}/${workspaceId}/settings`);
  }

  updateWorkspaceSettings(workspaceId: string, request: WorkspaceSettingsUpdateRequest): Observable<WorkspaceSettingsResponse> {
    return this.http.patch<WorkspaceSettingsResponse>(`${this.apiUrl}/${workspaceId}/settings`, request);
  }
}
