import { Injectable, inject } from '@angular/core';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

export interface IngestStepResponse {
  publicId: string;
  stepName: string;
  status: string;
  attempt: number;
  startedAt?: string;
  finishedAt?: string;
  durationMs?: number;
  errorCode?: string;
  errorMessage?: string;
  safeDetails?: Record<string, any>;
}

export interface IngestRunDetailResponse {
  publicId: string;
  documentPublicId: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  retryOfRunPublicId?: string;
  reprocessOfRunPublicId?: string;
  steps: IngestStepResponse[];
}

export interface IngestRunLogResponse {
  level: string;
  message: string;
  details?: Record<string, any>;
  createdAt: string;
  stepPublicId?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class IngestRunService {
  private http = inject(HttpService);

  getRunDetails(workspaceId: string, runId: string): Observable<IngestRunDetailResponse> {
    return this.http.get<IngestRunDetailResponse>(`/api/v1/workspaces/${workspaceId}/ingest-runs/${runId}`);
  }

  getRunLogs(workspaceId: string, runId: string): Observable<Page<IngestRunLogResponse>> {
    return this.http.get<Page<IngestRunLogResponse>>(`/api/v1/workspaces/${workspaceId}/ingest-runs/${runId}/logs`);
  }

  cancelRun(workspaceId: string, runId: string): Observable<void> {
    return this.http.post<void>(`/api/v1/workspaces/${workspaceId}/ingest-runs/${runId}/cancel`, {});
  }

  retryRun(workspaceId: string, runId: string): Observable<IngestRunDetailResponse> {
    return this.http.post<IngestRunDetailResponse>(`/api/v1/workspaces/${workspaceId}/ingest-runs/${runId}/retry`, {});
  }
}
