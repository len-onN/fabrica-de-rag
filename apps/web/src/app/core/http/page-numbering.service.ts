import { Injectable, inject } from '@angular/core';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

export interface DocumentPageDto {
  id: string;
  filePageNumber: number;
  detectedPrintedLabel: string;
  effectivePrintedLabel: string;
  numberingStyle: string;
  pageRole: string;
  includeInSearch: boolean;
  includeInNumbering: boolean;
  ocrStatus: string;
}

export interface PageNumberingAnchorDto {
  id: string;
  filePageNumber: number;
  printedLabel: string;
  numberingStyle: string;
  applyDirection: string;
}

export interface UpdateNumberingAnchorsRequest {
  anchors: PageNumberingAnchorDto[];
}

@Injectable({
  providedIn: 'root'
})
export class PageNumberingService {
  private http = inject(HttpService);
  
  getPages(workspaceId: string, documentId: string): Observable<DocumentPageDto[]> {
    return this.http.get<DocumentPageDto[]>(`/api/v1/workspaces/${workspaceId}/documents/${documentId}/pages`);
  }

  getAnchors(workspaceId: string, documentId: string): Observable<PageNumberingAnchorDto[]> {
    return this.http.get<PageNumberingAnchorDto[]>(`/api/v1/workspaces/${workspaceId}/documents/${documentId}/pages/anchors`);
  }

  updateAnchors(workspaceId: string, documentId: string, request: UpdateNumberingAnchorsRequest): Observable<void> {
    return this.http.post<void>(`/api/v1/workspaces/${workspaceId}/documents/${documentId}/pages/anchors`, request);
  }
}
