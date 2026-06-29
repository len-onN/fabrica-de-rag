import { Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

export interface AnalyticsEventRequest {
  eventVersion: string;
  eventName: string;
  origin: 'ui' | 'api' | 'worker' | 'mcp' | 'system';
  retentionClass: 'analytics' | 'history' | 'run_log' | 'audit_minimum';
  actor?: { type: string; id?: string };
  correlationId: string;
  occurredAt: string;
  resource?: { type: string; id: string };
  properties: any;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  constructor(private http: HttpService) {}

  recordEvent(workspaceId: string, request: AnalyticsEventRequest): Observable<void> {
    return this.http.post<void>(`/api/v1/workspaces/${workspaceId}/analytics/events`, request);
  }

  deleteAnalytics(workspaceId: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/workspaces/${workspaceId}/analytics/events`);
  }

  exportAnalytics(workspaceId: string): Observable<Blob> {
    // For MVP, we are hitting the endpoint. Note that if downloading, we might need to handle blob response.
    // httpService.get normally expects json. We can bypass HttpService wrapper if we need raw blobs,
    // but for simplicity, returning raw if we implement blob support in HttpService, 
    // or we can use native fetch/HttpClient for blob.
    throw new Error('exportAnalytics requires Http client blob configuration which is out of MVP scope for now.');
  }
}
