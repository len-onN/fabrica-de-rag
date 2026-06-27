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

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService {
  private http = inject(HttpClient);
  private apiUrl = '/api/v1/workspaces';

  getDashboardSummary(workspaceId: string): Observable<DashboardSummaryResponse> {
    return this.http.get<DashboardSummaryResponse>(`${this.apiUrl}/${workspaceId}/dashboard`);
  }
}
